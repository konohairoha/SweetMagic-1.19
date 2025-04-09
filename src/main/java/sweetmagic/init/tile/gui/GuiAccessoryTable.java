package sweetmagic.init.tile.gui;

import java.util.Arrays;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.ItemInit;
import sweetmagic.init.tile.gui.util.SMButton;
import sweetmagic.init.tile.gui.util.SMButton.SMButtonTip;
import sweetmagic.init.tile.gui.util.SMRenderTex;
import sweetmagic.init.tile.gui.util.SMRenderTex.MFRenderGage;
import sweetmagic.init.tile.menu.AccessoryTableMenu;
import sweetmagic.init.tile.sm.TileAccessoryTable;

public class GuiAccessoryTable extends GuiSMBase<AccessoryTableMenu> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_accessory_processing.png");
	private final static ItemStack STAR = new ItemStack(ItemInit.starlight);
	private final TileAccessoryTable tile;
	private final AccessoryTableMenu atMenu;

	public GuiAccessoryTable(AccessoryTableMenu menu, Inventory pInv, Component title) {
		super(menu, pInv, title);
		this.setGuiSize(176, 172);
		this.tile = menu.tile;
		this.atMenu = (AccessoryTableMenu) this.menu;

		SMButtonTip buttonTip = new SMButtonTip("", -18, 14, this.tile) {

			public boolean isFlagText(TileAccessoryTable tile) {
				return tile.canCraft();
			}

			public String getTip() {
				return this.isFlagText(tile) ? "addstack_start" : tile.getTip();
			}
		};

		SMButton button = new SMButton(MISC, 128, 38, 114, 15, 32, 12, buttonTip) {

			public boolean isButtonRender() {
				TileAccessoryTable tile = (TileAccessoryTable) this.getButtonTip().getTile();
				return tile.canCraft();
			}
		};

		this.addButtonMap(0, button);
		this.addRenderTexList(new SMRenderTex(TEX, 7, 7, 0, 0, 11, 77, new MFRenderGage(this.tile, true)));
	}

	@Override
	protected void renderBg(PoseStack pose, float parTick, int mouseX, int mouseY) {
		super.renderBg(pose, parTick, mouseX, mouseY);

		// 座標の取得
		int x = this.getWidth();
		int y = this.getHeight();

		if (!this.tile.canCraft()) {
			RenderSystem.setShaderTexture(0, MISC);
			this.blit(pose, x + 128, y + 38, 20, 0, 32, 14);
		}

		RenderSystem.setShaderTexture(0, this.getTEX());

		// ゲージを表示
		if (this.tile.craftTime > 0) {
			int progress = this.tile.getProgress(52);
			this.blit(pose, x + 73, y + 17, 197, 17, progress, 57);
		}

		this.renderSlotItem(this.atMenu.starSlot, STAR, pose);
	}

	@Override
	protected void renderLabels(PoseStack pose, int mouseX, int mouseY) {
		super.renderLabels(pose, mouseX, mouseY);
		this.renderItemLabel(this.atMenu.starSlot, STAR, pose, mouseX, mouseY);
	}

	// アイテム描画
	public void renderSlotItem(Slot slot, ItemStack stack, PoseStack pose) {
		this.renderSlotItem(slot, stack, pose, false);
	}

	// アイテム描画
	public void renderSlotItem(Slot slot, ItemStack stack, PoseStack pose, boolean flag) {
		if (!slot.getItem().isEmpty()) { return; }

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
	public void renderItemLabel(Slot slot, ItemStack stack, PoseStack pose, int mouseX, int mouseY) {
		this.renderItemLabel(slot, stack, pose, mouseX, mouseY, false);
	}

	// スロットの説明
	public void renderItemLabel(Slot slot, ItemStack stack, PoseStack pose, int mouseX, int mouseY, boolean flag) {
		if (!slot.getItem().isEmpty()) { return; }

		int tipX = this.getWidth() + slot.x;
		int tipY = this.getHeight() + slot.y;
		if (!this.isRender(tipX, tipY, mouseX, mouseY, 16, 16)) { return; }

		int xAxis = (mouseX - this.getWidth());
		int yAxis = (mouseY - this.getHeight());
		String name = flag ? "craft_out" : "slot_need";
		List<Component> tipList = Arrays.<Component> asList(this.getText(name).withStyle(GREEN), stack.getDisplayName());
		this.renderComponentTooltip(pose, tipList, xAxis, yAxis);
	}

	protected ResourceLocation getTEX() {
		return TEX;
	}
}
