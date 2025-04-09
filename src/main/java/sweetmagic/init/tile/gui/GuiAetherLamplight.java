package sweetmagic.init.tile.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import sweetmagic.SweetMagicCore;
import sweetmagic.handler.PacketHandler;
import sweetmagic.init.tile.gui.util.SMButton;
import sweetmagic.init.tile.gui.util.SMRenderTex;
import sweetmagic.init.tile.gui.util.SMRenderTex.MFRenderGage;
import sweetmagic.init.tile.menu.AetherLamplightMenu;
import sweetmagic.init.tile.sm.TileAetherLamplight;
import sweetmagic.init.tile.sm.TileAetherLamplight.BlockOrder;
import sweetmagic.packet.AetherLampLightPKT;

public class GuiAetherLamplight extends GuiSMBase<AetherLamplightMenu> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_aether_lamp_light.png");
	private final TileAetherLamplight tile;
	private float scrollOffset = 0F;
	private int startIndex = 0;
	private boolean scrolling = false;
	private boolean enchaView[] = new boolean[4];
	private int selectId = -1;
	private List<Block> blockList = new ArrayList<>();
	private Set<Block> blockSet = new HashSet<>();

	public GuiAetherLamplight(AetherLamplightMenu menu, Inventory pInv, Component title) {
		super(menu, pInv, title);
		this.tile = menu.tile;
		this.setGuiSize(183, 174);
		this.addButtonMap(0, new SMButton(TEX, 26, 7, 196, 224, 11, 9));
		this.addButtonMap(1, new SMButton(TEX, 39, 7, 196, 233, 15, 9));
		this.addButtonMap(2, new SMButton(TEX, 26, 19, 226, 224, 11, 9));
		this.addButtonMap(3, new SMButton(TEX, 39, 19, 226, 233, 15, 9));
		this.addButtonMap(4, new SMButton(TEX, 18, 55, 196, 224, 11, 9));
		this.addButtonMap(5, new SMButton(TEX, 51, 55, 226, 224, 11, 9));
		this.addButtonMap(6, new SMButton(TEX, 18, 70, 196, 210, 14, 14));
		this.addButtonMap(7, new SMButton(TEX, 34, 70, 196, 242, 28, 14));
		this.addRenderTexList(new SMRenderTex(TEX, 5, 7, 0, 0, 11, 77, new MFRenderGage(menu.tile, true)));
		this.blockSet = this.tile.getRangeBlockSet();
		this.blockList = this.tile.getRangeBlockList();
	}

	@Override
	protected void renderBg(PoseStack pose, float parTick, int mouseX, int mouseY) {
		super.renderBg(pose, parTick, mouseX, mouseY);

		// 座標の取得
		int x = this.getWidth();
		int y = this.getHeight();

		if (!this.blockSet.isEmpty()) {
			int size = this.blockSet.size();
			int maxItemList = Math.min(4, size);
			List<Block> blockSetList = this.blockSet.stream().toList();
			BlockOrder order = this.tile.blockOrder;

			// スクロールバーの表示
			int h = (int) (60F * this.scrollOffset);
			boolean isActive = this.scrollbarActive();
			RenderSystem.setShaderTexture(0, MISC);
			this.blit(pose, x + 167, y + 9 + h, 83 + (isActive ? 0 : 8), 93, 8, 15);

			for (int id = 0; id < 4; id++)
				this.blit(pose, x + 64, y + 7 + id * 20, 99, 93, 101, 20);

			for (int i = 0; i < maxItemList; i++) {
				this.blit(pose, x + 64, y + 7 + i * 20, 99, 113 + (this.enchaView[i] ? 20 : 0), 101, 20);
			}

			for (int i = 0; i < maxItemList; i++) {
				if (i + this.startIndex >= size) { break; }

				if (i + this.startIndex == this.selectId) {
					this.blit(pose, x + 64, y + 7 + i * 20, 99, 153, 101, 20);
				}
			}

			for (int id = 0; id < maxItemList; id++) {
				if (id + this.startIndex >= size) { break; }

				pose.pushPose();
				Block block = blockSetList.get(id + this.startIndex);
				ItemStack stack = new ItemStack(block);
				this.itemRenderer.renderAndDecorateFakeItem(stack, x + 67, y + 8 + id * 20);
				MutableComponent tip = this.getLabel("×" + Collections.frequency(this.blockList, block));
				int orderNumber = order.getBlockOrder(block);
				ChatFormatting color = orderNumber == -1 ? RED : WHITE;
				this.font.drawShadow(pose, this.getTipArray(this.getText("order"), this.getLabel(" " + orderNumber).withStyle(color)).withStyle(GOLD), x + 100, y + 13 + id * 20, 0xEEEEEE);
				pose.translate(0D, 0D, 200D);
				this.font.drawShadow(pose, tip, x + 80, y + 16 + id * 20, 0xEEEEEE);
				pose.popPose();
			}
		}

		this.font.drawShadow(pose, this.getLabel("" + this.tile.range), x + 35, y + 34, 0x2BC444);

		if (this.selectId != -1 && !this.blockSet.isEmpty()) {
			this.font.drawShadow(pose, this.getLabel("" + this.tile.order), x + 35, y + 56, 0x2BC444);
			this.font.drawShadow(pose, this.getText("decision"), x + 39, y + 73, 0x2BC444);
		}
	}

	@Override
	protected void renderLabels(PoseStack pose, int mouseX, int mouseY) {
		super.renderLabels(pose, mouseX, mouseY);

		int tipX = this.getWidth() + 18;
		int tipY = this.getHeight() + 62;

		if (this.isRender(tipX, tipY, mouseX, mouseY, 14, 14)) {
			int xAxis = (mouseX - this.getWidth());
			int yAxis = (mouseY - this.getHeight());
			this.renderTooltip(pose, this.getTipArray(this.getText("isrange"), this.getTip("" + this.tile.isRangeView)).withStyle(GOLD), xAxis, yAxis);
		}

		if (!this.blockSet.isEmpty()) {

			tipX = this.getWidth() + 64;
			tipY = this.getHeight() + 6;
			int size = this.blockSet.size();
			int maxItemList = Math.min(4, size);
			List<Block> blockSetList = this.blockSet.stream().toList();

			for (int id = 0; id < maxItemList; id++) {
				if (id + this.startIndex >= size) { break; }
				this.enchaView[id] = false;

				if (this.isRender(tipX, tipY, mouseX, mouseY, 98, 19)) {
					int xAxis = mouseX - this.getWidth();
					int yAxis = mouseY - this.getHeight();
					this.renderTooltip(pose, new ItemStack(blockSetList.get(id + this.startIndex)), xAxis, yAxis);
					this.enchaView[id] = true;
				}

				tipY += 20;
			}
		}
	}

	public void clickSMButton(int id) {
		super.clickSMButton(id);

		if (id == 7) {
			this.blockSet = this.tile.getRangeBlockSet();
			this.blockList = this.tile.getRangeBlockList();
			this.selectId = -1;
			PacketHandler.sendToServer(new AetherLampLightPKT(this.selectId, this.tile.getBlockPos(), false));
		}
	}

	public boolean mouseClicked(double guiX, double guiY, int mouseButton) {

		this.scrolling = false;
		int x = this.getWidth();
		int y = this.getHeight();
		double dX = guiX - x - 64;
		int maxItemList = Math.min(4, this.blockSet.size());

		for (int id = 0; id < maxItemList; id++) {

			double dY = guiY - y - 6 - id * 20;

			if (dX >= 0D && dX <= 98D && dY >= 0D && dY < 20D) {
				this.selectId = this.selectId == id + this.startIndex ? -1 : id + this.startIndex;
				PacketHandler.sendToServer(new AetherLampLightPKT(this.selectId, this.tile.getBlockPos(), true));
			}
		}

		dX = guiX - x - 23;
		int w = 8;
		int h = 14;
		int aX = this.leftPos + 167;
		int aY = (int) (this.topPos + 9 + (this.scrollOffset * 60F));

		// スクロールバーの当たり判定チェック
		if (guiX >= aX && guiX < aX + w && guiY >= aY && guiY < aY + h) {
			this.scrolling = true;
		}

		return super.mouseClicked(guiX, guiY, mouseButton);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {
		int size = this.blockSet.size();
		if (!this.scrolling || !this.scrollbarActive()) { return super.mouseDragged(mouseX, mouseY, mouseButton, dragX, dragY); }

		int i = this.topPos + 16;
		int j = i + 73;
		int offscreenRows = size - 4;
		this.scrollOffset = ((float) mouseY - (float) i + 5F) / ((float) (j - i) - 15F);
		this.scrollOffset = Mth.clamp(this.scrollOffset, 0F, 1F);
		this.startIndex = (int) ((double) (this.scrollOffset * (float) offscreenRows) + 0.5D);
		return true;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollDelta) {
		if (!this.scrollbarActive()) { return super.mouseScrolled(mouseX, mouseY, scrollDelta); }

		int size = this.blockSet.size();
		int offscreenRows = size - 4;
		this.scrollOffset = (float) ((double) this.scrollOffset - scrollDelta / (double) offscreenRows);
		this.scrollOffset = Mth.clamp(this.scrollOffset, 0F, 1F);
		this.startIndex = (int) ((double) (this.scrollOffset * (float) offscreenRows) + 0.5D);
		return true;
	}

	private boolean scrollbarActive() {
		return this.blockSet.size() > 4;
	}

	@Override
	protected ResourceLocation getTEX() {
		return TEX;
	}
}
