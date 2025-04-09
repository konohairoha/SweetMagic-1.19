package sweetmagic.init.tile.gui;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.tile.menu.CardboardStorageMenu;
import sweetmagic.init.tile.slot.MagiaSlot;

public class GuiCardboardStorage extends GuiSMBase<CardboardStorageMenu> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_moden_rack.png");
	private final CardboardStorageMenu menu;
	private List<Integer> stackList = new ArrayList<>();

	public GuiCardboardStorage(CardboardStorageMenu menu, Inventory pInv, Component title) {
		super(menu, pInv, title);
		this.menu = menu;
		this.setGuiSize(173, 132);
	}

	@Override
	protected void renderBg(PoseStack pose, float parTick, int mouseX, int mouseY) {
		super.renderBg(pose, parTick, mouseX, mouseY);
		this.blit(pose, this.getWidth() + 78, this.getHeight() + 13, 173, 0, 18, 18);
	}

	public void render(PoseStack pose, int mouseX, int mouseY, float parTick) {

		this.stackList.clear();

		for (Slot slot : this.menu.slotList) {
			ItemStack stack = this.menu.slots.get(slot.getSlotIndex()).getItem().copy();
			this.stackList.add(stack.getCount());

			if (stack.isEmpty() || stack.getCount() == 1) { continue; }
			stack.setCount(1);
			slot.set(stack);
		}

		super.render(pose, mouseX, mouseY, parTick);

		for (int i = 0; i < this.menu.slotList.size(); i++) {
			Slot slot = this.menu.slots.get(i);
			if (!slot.isActive()) { return; }

			int count = this.stackList.get(i);
			if (count <= 0) { continue; }

			ItemStack stack = slot.getItem();

			if (stack.isEmpty()) { continue; }

			if (count > 1) {
				this.renderSlotItem(slot, stack, count, pose);
			}
			stack.setCount(count);
			slot.set(stack);
		}
	}

	// アイテム描画
	public void renderSlotItem(Slot slot, ItemStack stack, int count, PoseStack pose) {

		int x = this.leftPos + slot.x;
		int y = this.topPos + slot.y + 0;
		PoseStack view = RenderSystem.getModelViewStack();

		view.pushPose();
		view.mulPoseMatrix(pose.last().pose());
		Font font = IClientItemExtensions.of(stack).getFont(stack, IClientItemExtensions.FontContext.TOOLTIP);
		font = font == null ? this.font : font;

		RenderSystem.enableDepthTest();
		RenderSystem.depthFunc(516);
		GuiComponent.fill(view, x, y, x + 16, y + 16, 0);
		RenderSystem.depthFunc(515);
		RenderSystem.disableDepthTest();
		String stackSize = String.valueOf(count);
		int stackLength = (stackSize.length() - 1) / 3;

		if (stackSize.length() > 3) {
			String encord = ENCODED_SUFFIXES[stackLength - 1];
			stackSize = stackSize.substring(0, stackSize.length() - stackLength * 3 + 1) + encord;
			stackSize = stackSize.substring(0, stackSize.length() - 2) + "." + stackSize.substring(stackSize.length() - 2, stackSize.length());
		}

		MultiBufferSource.BufferSource buf = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
		PoseStack poses = new PoseStack();
		poses.scale(0.5F, 0.5F, 0.5F);
		poses.translate(0D, 0D, 600D);
		font.drawInBatch(stackSize, (x + 6 + 9) * 2 - font.width(stackSize), (y + 12) * 2, 16777215, true, poses.last().pose(), buf, false, 0, 15728880);
		buf.endBatch();
		view.popPose();
	}

	protected void renderTooltip(PoseStack pose, ItemStack stack, int x, int y) {

		List<Component> tipList = this.getTooltipFromItem(stack);

		if (this.hoveredSlot instanceof MagiaSlot) {
			int index = this.hoveredSlot.getSlotIndex();
			if (index >= this.stackList.size()) { return; }

			int vCount = this.stackList.get(index);
			int vMax = this.hoveredSlot.getMaxStackSize();
			String count = String.format("%,d", vCount) + "/";
			String max = String.format("%,d", vMax);
			String par = " (" + String.format("%.1f", ((float) vCount / (float) vMax) * 100F) + "%" + ")";

			tipList.add(this.getLabel(count + max + par).withStyle(GOLD));
		}
		this.renderTooltip(pose, tipList, stack.getTooltipImage(), x, y);
	}

	protected ResourceLocation getTEX() {
		return TEX;
	}
}
