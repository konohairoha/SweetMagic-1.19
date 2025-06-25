package sweetmagic.init.tile.gui;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import sweetmagic.SweetMagicCore;
import sweetmagic.handler.PacketHandler;
import sweetmagic.init.StructureInit;
import sweetmagic.init.StructureInit.StructureInfo;
import sweetmagic.init.tile.gui.util.SMButton;
import sweetmagic.init.tile.menu.CompasMenu;
import sweetmagic.packet.CompasPKT;

public class GuiCompas extends GuiSMBase<CompasMenu> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_dungeon_compas.png");
	private boolean lootView[] = new boolean[4];
	private float scrollOffset = 0F;
	private int startIndex = 0;
	private boolean scrolling = false;
	private int selectID = -1;

	public GuiCompas(CompasMenu menu, Inventory pInv, Component title) {
		super(menu, pInv, title);
		this.setGuiSize(126, 111);

		CompoundTag tags = this.player.getMainHandItem().getOrCreateTag();

		if (tags.contains("selectId")) {
			this.selectID = tags.getInt("selectId");
		}

		this.addButtonMap(0, new SMButton(TEX, 25, 91, 127, 20, 29, 12));
		this.addButtonMap(1, new SMButton(TEX, 58, 91, 127, 20, 29, 12));
	}

	@Override
	protected void renderBg(PoseStack pose, float parTick, int mouseX, int mouseY) {
		super.renderBg(pose, parTick, mouseX, mouseY);

		// 座標の取得
		int x = this.getWidth();
		int y = this.getHeight();
		RenderSystem.setShaderTexture(0, MISC);

		// スクロールバーの表示
		int h = (int) (60F * this.scrollOffset);
		this.blit(pose, x + 110, y + 9 + h, 83, 93, 8, 15);

		int size = StructureInit.strucMap.size();
		for (int id = 0; id < 4; id++) {

			this.blit(pose, x + 7, y + 7 + id * 20, 99, 93, 101, 20);
			if (id + this.startIndex >= size) { break; }

			this.blit(pose, x + 7, y + 7 + id * 20, 99, 113 + (this.lootView[id] ? 20 : 0), 101, 20);

			if (id + this.startIndex == this.selectID) {
				this.blit(pose, x + 7, y + 7 + id * 20, 99, 153, 101, 20);
			}
		}

		this.font.drawShadow(pose, this.getText("decision"), x + 30, y + 93, 0xFFFFFF);
		this.font.drawShadow(pose, this.getText("cancel"), x + 63, y + 93, 0xFFFFFF);

		for (int id = 0; id < 4; id++) {

			if (id + this.startIndex >= size) { break; }

			String name = StructureInit.strucMap.get(id + this.startIndex).name();
			this.font.drawShadow(pose, this.getTip("structure.sweetmagic." + name), x + 17, y + 12 + id * 20, 0x2BC444);
		}
	}

	@Override
	protected void renderLabels(PoseStack pose, int mouseX, int mouseY) {
		super.renderLabels(pose, mouseX, mouseY);

		int tipX = this.getWidth() + 7;
		int tipY = this.getHeight() + 7;
		int xAxis = mouseX - this.getWidth();
		int yAxis = mouseY - this.getHeight();
		Map<Integer, StructureInfo> strucMap = StructureInit.strucMap;

		for (int id = 0; id < 4; id++) {

			this.lootView[id] = false;

			if (this.isRender(tipX, tipY, mouseX, mouseY, 98, 19)) {

				if (id + this.startIndex >= strucMap.size()) { break; }

				StructureInfo info = strucMap.get(id + this.startIndex);
				MutableComponent struc = this.getTip("structure.sweetmagic." + info.name()).withStyle(GOLD);
				MutableComponent dim = this.getTipArray(this.getText("dimension"), ": ", this.getText(info.getDim())).withStyle(GREEN);
				MutableComponent dif = this.getTipArray(this.getText("difficulty"), ": ★", info.level()).withStyle(GREEN);
				List<Component> comList = Arrays.<Component> asList(struc, dim, dif);
				this.renderComponentTooltip(pose, comList, xAxis + 0, yAxis - 20);
				this.lootView[id] = true;
			}

			tipY += 20;
		}
	}

	public boolean mouseClicked(double guiX, double guiY, int mouseButton) {

		int aX = this.leftPos + 110;
		int aY = (int) (this.topPos + 9 + (this.scrollOffset * 60F));
		int w = 8;
		int h = 14;

		// スクロールバーの当たり判定チェック
		if (guiX >= aX && guiX < aX + w && guiY >= aY && guiY < aY + h) {
			this.scrolling = true;
		}

		else {
			this.scrolling = false;
		}

		aX = this.leftPos + 25;
		aY = this.topPos + 91;
		w = 29;
		h = 12;

		if (guiX >= aX && guiX < aX + w && guiY >= aY && guiY < aY + h) {
			PacketHandler.sendToServer(new CompasPKT(this.selectID));
			this.minecraft.player.closeContainer();
			return super.mouseClicked(guiX, guiY, mouseButton);
		}

		aX = this.leftPos + 58;

		if (guiX >= aX && guiX < aX + w && guiY >= aY && guiY < aY + h) {
			this.player.getLevel().playSound(null, this.player.blockPosition(), SoundEvents.UI_BUTTON_CLICK, SoundSource.PLAYERS, 0.25F, 1F);
			this.minecraft.player.closeContainer();
			return super.mouseClicked(guiX, guiY, mouseButton);
		}

		int x = this.getWidth();
		int y = this.getHeight();
		double dX = guiX - (double) (x + 7);

		for (int id = 0; id < 4; ++id) {

			double dY = guiY - (double) (y + 7 + 20 * id);

			// 各エンチャントの当たり判定チェック
			if (dX >= 0D && dX <= 98D && dY >= 0D && dY < 20D) {
				int newSelectID = id + this.startIndex;
				this.selectID = (newSelectID != this.selectID) ? newSelectID : -1;
				this.player.getLevel().playSound(null, this.player.blockPosition(), SoundEvents.UI_BUTTON_CLICK, SoundSource.PLAYERS, 0.25F, 1F);
			}
		}

		return super.mouseClicked(guiX, guiY, mouseButton);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {
		int size = StructureInit.strucMap.size();
		if (!this.scrolling) { return super.mouseDragged(mouseX, mouseY, mouseButton, dragX, dragY); }

		int i = this.topPos + 20;
		int j = i + 73;
		int offscreenRows = size - 4;
		this.scrollOffset = ((float) mouseY - (float) i + 5F) / ((float) (j - i) - 15F);
		this.scrollOffset = Mth.clamp(this.scrollOffset, 0F, 1F);
		this.startIndex = (int) ((double) (this.scrollOffset * (float) offscreenRows) + 0.5D);
		return true;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollDelta) {
		int size = StructureInit.strucMap.size();
		int offscreenRows = size - 4;
		this.scrollOffset = (float) ((double) this.scrollOffset - scrollDelta / (double) offscreenRows);
		this.scrollOffset = Mth.clamp(this.scrollOffset, 0F, 1F);
		this.startIndex = (int) ((double) (this.scrollOffset * (float) offscreenRows) + 0.5D);
		return true;
	}

	protected ResourceLocation getTEX() {
		return TEX;
	}
}
