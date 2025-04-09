package sweetmagic.init.tile.gui;

import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundRenameItemPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.util.ISMTip;
import sweetmagic.handler.PacketHandler;
import sweetmagic.init.LootInit;
import sweetmagic.init.tile.menu.WoodChestLootMenu;
import sweetmagic.init.tile.sm.TileWoodChest;
import sweetmagic.packet.WoodChestLootPKT;

public class GuiWoodChestLoot extends GuiSMBase<WoodChestLootMenu> implements ISMTip {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_woodchest_loot.png");
	public final TileWoodChest tile;
	private EditBox count;
	private EditBox chance;
	private boolean lootView[] = new boolean[4];
	private float scrollOffset = 0F;
	private int startIndex = 0;
	private boolean scrolling = false;
	private boolean isView = false;
	private int selectID = -1;

	public GuiWoodChestLoot(WoodChestLootMenu menu, Inventory pInv, Component title) {
		super(menu, pInv, title);
		this.setGuiSize(176, 174);
		this.tile = menu.tile;
		ResourceLocation loot = this.tile.lootTable;
		if (loot == null) { return; }

		for (int i = 0; i < LootInit.lootList.size(); i++) {
			if (!loot.equals(LootInit.lootList.get(i))) { continue; }
			this.selectID = i;
			break;
		}
	}

	@Override
	protected void renderBg(PoseStack pose, float parTick, int mouseX, int mouseY) {
		super.renderBg(pose, parTick, mouseX, mouseY);

		int x = this.getWidth();
		int y = this.getHeight();

		// スクロールバーの表示
		int h = (int) (60F * this.scrollOffset);
		RenderSystem.setShaderTexture(0, MISC);
		this.blit(pose, x + 160, y + 9 + h, 83, 93, 8, 15);

		if (this.isView) {
			this.blit(pose, x + 20, y + 70, 180, 18, 27, 12);
		}

		int size = LootInit.lootList.size();
		for (int id = 0; id < 4; id++) {

			this.blit(pose, x + 57, y + 7 + id * 20, 99, 93, 101, 20);
			if (id + this.startIndex >= size) { break; }

			this.blit(pose, x + 57, y + 7 + id * 20, 99, 113 + (this.lootView[id] ? 20 : 0), 101, 20);

			if (id + this.startIndex == this.selectID) {
				this.blit(pose, x + 57, y + 7 + id * 20, 99, 153, 101, 20);
			}
		}

		this.font.drawShadow(pose, this.getText("save"), x + 25, y + 72, 0xFFFFFF);

		for (int id = 0; id < 4; id++) {

			if (id + this.startIndex >= size) { break; }

			String lootText = LootInit.lootList.get(id + this.startIndex).toString();
			String result = lootText.substring(lootText.indexOf("/") + 1);
			String tip = result.substring(result.indexOf("/") + 1);
			this.font.drawShadow(pose, this.getTip(tip), x + 60, y + 12 + id * 20, 0x2BC444);
		}
	}

	@Override
	protected void renderLabels(PoseStack pose, int mouseX, int mouseY) {
		super.renderLabels(pose, mouseX, mouseY);

		int tipX = this.getWidth() + 20;
		int tipY = this.getHeight() + 70;
		int xAxis = mouseX - this.getWidth();
		int yAxis = mouseY - this.getHeight();
		this.isView = this.isRender(tipX, tipY, mouseX, mouseY, 27, 12);
		tipX = this.getWidth() + 57;
		tipY = this.getWidth() - 104;

		for (int id = 0; id < 4; id++) {

			this.lootView[id] = false;

			if (this.isRender(tipX, tipY, mouseX, mouseY, 98, 19)) {
				if (id + this.startIndex >= LootInit.lootList.size()) { break; }

				ResourceLocation loot = LootInit.lootList.get(id + this.startIndex);
				this.renderTooltip(pose, this.getTip(loot.toString()).withStyle(GOLD), xAxis - 80, yAxis - 6);
				this.lootView[id] = true;
			}

			tipY += 20;
		}
	}

	public boolean mouseClicked(double guiX, double guiY, int mouseButton) {

		int aX = this.leftPos + 159;
		int aY = (int) (this.topPos + 8 + (this.scrollOffset * 60F));
		int w = 8;
		int h = 14;

		// スクロールバーの当たり判定チェック
		if (guiX >= aX && guiX < aX + w && guiY >= aY && guiY < aY + h) {
			this.scrolling = true;
		}

		aX = this.leftPos + 20;
		aY = this.topPos + 70;
		w = 27;
		h = 12;

		if (guiX >= aX && guiX < aX + w && guiY >= aY && guiY < aY + h) {
			PacketHandler.sendToServer(new WoodChestLootPKT(Integer.valueOf(this.count.getValue()), Float.valueOf(this.chance.getValue()), this.selectID, this.tile.getBlockPos()));
			this.minecraft.player.closeContainer();
		}

		int x = this.getWidth();
		int y = this.getHeight();
		double dX = guiX - (double) (x + 57);

		for (int id = 0; id < 4; ++id) {

			double dY = guiY - (double) (y + 7 + 20 * id);

			// 各エンチャントの当たり判定チェック
			if (dX >= 0D && dX <= 95 && dY >= 0D && dY < 18D) {
				this.selectID = id + this.startIndex;
			}
		}

		return super.mouseClicked(guiX, guiY, mouseButton);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {
		List<ResourceLocation> enchaList = LootInit.lootList;
		int size = enchaList.size();
		if (!this.scrolling) { return super.mouseDragged(mouseX, mouseY, mouseButton, dragX, dragY); }

		int i = topPos + 44;
		int j = i + 73;
		int offscreenRows = size - 4;
		this.scrollOffset = ((float) mouseY - (float) i + 5F) / ((float) (j - i) - 15F);
		this.scrollOffset = Mth.clamp(this.scrollOffset, 0F, 1F);
		this.startIndex = (int) ((double) (this.scrollOffset * (float) offscreenRows) + 0.5D);

		return true;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollDelta) {
		List<ResourceLocation> enchaList = LootInit.lootList;
		int size = enchaList.size();
		int offscreenRows = size - 4;
		this.scrollOffset = (float) ((double) this.scrollOffset - scrollDelta / (double) offscreenRows);
		this.scrollOffset = Mth.clamp(this.scrollOffset, 0F, 1F);
		this.startIndex = (int) ((double) (this.scrollOffset * (float) offscreenRows) + 0.5D);
		return true;
	}

	public void containerTick() {
		super.containerTick();
		this.count.tick();
		this.chance.tick();
	}

	protected void init() {
		super.init();
		this.subInit();
		this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
	}

	protected void subInit() {

		this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
		int x = this.getWidth();
		int y = this.getHeight();

		this.count = new EditBox(this.font, x + 9, y + 19, 116, 12, this.getTip("container.repair1"));
		this.count.setCanLoseFocus(false);
		this.count.setTextColor(-1);
		this.count.setTextColorUneditable(-1);
		this.count.setBordered(false);
		this.count.setMaxLength(40);
		this.count.setResponder(this::onNameChanged);
		this.count.setValue("" + this.tile.count);

		this.chance = new EditBox(this.font, x + 9, y + 46, 116, 12, this.getTip("container.repair"));
		this.chance.setCanLoseFocus(false);
		this.chance.setTextColor(-1);
		this.chance.setTextColorUneditable(-1);
		this.chance.setBordered(false);
		this.chance.setMaxLength(40);
		this.chance.setResponder(this::onNameChanged);
		this.chance.setValue("" + this.tile.chance);

		this.addWidget(this.count);
		this.setInitialFocus(this.count);
		this.setFocused(this.count);
		this.addWidget(this.chance);
		this.setInitialFocus(this.chance);
		this.setFocused(this.chance);
	}

	public void resize(Minecraft mc, int x, int y) {
		String countString = this.count.getValue();
		String chanceString = this.chance.getValue();
		this.init(mc, x, y);
		this.count.setValue(countString);
		this.chance.setValue(chanceString);
	}

	public boolean keyPressed(int id, int par1, int par2) {

		if (id == 256) {
			this.minecraft.player.closeContainer();
		}

		return !this.count.keyPressed(id, par1, par2) && !this.chance.keyPressed(id, par1, par2) ? super.keyPressed(id, par1, par2) : true;
	}

	private void onNameChanged(String name) {
		if (name.isEmpty()) { return; }
		String s = name;
		this.minecraft.player.connection.send(new ServerboundRenameItemPacket(s));
	}

	public void render(PoseStack pose, int mouseX, int mouseY, float parTick) {
		super.render(pose, mouseX, mouseY, parTick);
		this.count.render(pose, mouseX, mouseY, parTick);
		this.chance.render(pose, mouseX, mouseY, parTick);
	}

	protected ResourceLocation getTEX() {
		return TEX;
	}
}
