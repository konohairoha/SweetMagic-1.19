package sweetmagic.init.tile.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.util.ISMTip;
import sweetmagic.init.ItemInit;
import sweetmagic.init.tile.menu.WarpMenu;
import sweetmagic.init.tile.sm.TileWarp;

public class GuiWarp extends GuiSMBase<WarpMenu> implements ISMTip {

	private final static ItemStack CLERO = new ItemStack(ItemInit.clero_petal);
	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_warp.png");
	private static final ResourceLocation MISC = SweetMagicCore.getSRC("textures/gui/gui_misc.png");
	private final TileWarp tile;
	private final WarpMenu menu;

	private boolean buttonView[] = new boolean[4];
	private Player player;

	public GuiWarp(WarpMenu menu, Inventory pInv, Component title) {
		super(menu, pInv, title);
		this.setGuiWidth(173);
		this.setGuiHeight(134);
		this.tile = menu.tile;
		this.menu = menu;
		this.player = pInv.player;
	}

	@Override
	protected void renderBg(PoseStack pose, float parTick, int mouseX, int mouseY) {
		super.renderBg(pose, parTick, mouseX, mouseY);

		// 座標の取得
		int x = this.getWidth();
		int y = this.getHeight();
		RenderSystem.setShaderTexture(0, MISC);

		for (int i = 0; i < 4; i++) {
			if (this.tile.getInputItem(i).isEmpty()) { continue; }
			this.blit(pose, x + 17 + i * 36, y + 31, 20, 16 + (this.buttonView[i] ? 16 : 0), 32, 14);
		}

		for (int i = 0; i < 4; i++) {
			this.renderSlotItem(this.menu.invSlot[i], CLERO, pose);
		}
	}

	@Override
	protected void renderLabels(PoseStack pose, int mouseX, int mouseY) {
		super.renderLabels(pose, mouseX, mouseY);

		//GUIの左上からの位置
		int xAxis = mouseX - this.getWidth();
		int yAxis = mouseY - this.getHeight();
		int tipX = this.getWidth() + 17;
		int tipY = this.getHeight() + 31;

		for (int id = 0; id < 4; id++) {

			this.buttonView[id] = false;

			if (this.isRendeer(tipX, tipY, mouseX, mouseY, 31, 11)) {

				ItemStack stack = this.tile.getInputItem(id);
				if (stack.isEmpty()) {
					tipX += 36;
					continue;
				}

				CompoundTag tags = stack.getTag();
				if (tags == null || !tags.contains("pX")) {
					tipX += 36;
					return;
				}

				List<Component> tipList = new ArrayList<>();
				int x = tags.getInt("pX");
				int y = tags.getInt("pY");
				int z = tags.getInt("pZ");
	            String dim = tags.getString("dim_view");

				String pos = x + ", " + y + ", " + z;
				tipList.add(this.getTipArray(stack.getHoverName()).withStyle(GREEN));
				tipList.add(this.getLabel(" "));
				tipList.add(this.getTipArray(this.getText("teleport_pos"), pos).withStyle(GOLD));
				tipList.add(this.getTipArray(this.getText("teleport_dim"), dim).withStyle(GOLD));
				tipList.add(this.getLabel(" "));
				tipList.add(this.getText("teleport_action").withStyle(GREEN));

	            this.renderComponentTooltip(pose, tipList, xAxis, yAxis);
	            this.buttonView[id] = true;
			}

			tipX += 36;
		}

		for (int i = 0; i < 4; i++) {
			this.renderItemLabel(this.menu.invSlot[i], CLERO, pose, mouseX, mouseY);
		}
	}

	public boolean mouseClicked(double guiX, double guiY, int mouseButton) {

		int x = this.getWidth();
		int y = this.getHeight();

		for (int i = 0; i < 4; i++) {
			double dX = guiX - (double) (x + 17 + i * 36);
			double dY = guiY - (double) (y + 31);

			// 各エンチャントの当たり判定チェック
			if (dX >= 0D && dX <= 31 && dY >= 0D && dY < 11) {
				this.menu.clickMenuButton(this.player, i);
				this.minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, i);
				this.removed();
				this.onClose();
			}
		}

		return super.mouseClicked(guiX, guiY, mouseButton);
	}

	// アイテム描画
	public void renderSlotItem (Slot slot, ItemStack stack, PoseStack pose) {
		this.renderSlotItem(slot, stack, pose, false);
	}

	// アイテム描画
	public void renderSlotItem (Slot slot, ItemStack stack, PoseStack pose, boolean flag) {
		if ( !slot.getItem().isEmpty()) { return; }

		int x = this.leftPos + slot.x;
		int y = this.topPos + slot.y;
		PoseStack view = RenderSystem.getModelViewStack();

		view.pushPose();
		view.mulPoseMatrix(pose.last().pose());
		Font font = IClientItemExtensions.of(stack).getFont(stack, IClientItemExtensions.FontContext.TOOLTIP);
		font = font == null ? this.font : font;

		this.itemRenderer.renderAndDecorateFakeItem(stack, x, y);
		RenderSystem.enableDepthTest();
		RenderSystem.depthFunc(516);
		GuiComponent.fill(pose, x, y, x + 16, y + 16, 822083583);
		RenderSystem.depthFunc(515);
		RenderSystem.disableDepthTest();
		this.itemRenderer.renderGuiItemDecorations(font, stack, x, y, null);
		view.popPose();
	}

	// スロットの説明
	public void renderItemLabel (Slot slot, ItemStack stack, PoseStack pose, int mouseX, int mouseY) {
		if ( !slot.getItem().isEmpty()) { return; }

		//描画位置を計算
		int tipX = this.getWidth() + slot.x;
		int tipY = this.getHeight() + slot.y;

		if (this.isRendeer(tipX, tipY, mouseX, mouseY, 16, 16)) {
			int xAxis = mouseX - this.getWidth();
			int yAxis = mouseY - this.getHeight();
			String name = "slot_need";
			List<Component> tipList = Arrays.<Component> asList(this.getText(name).withStyle(GREEN), stack.getDisplayName());
            this.renderComponentTooltip(pose, tipList, xAxis, yAxis);
		}
	}

	protected ResourceLocation getTEX () {
		return TEX;
	}
}
