package sweetmagic.init.tile.gui;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.tile.gui.util.SMButton;
import sweetmagic.init.tile.gui.util.SMButton.SMButtonTip;
import sweetmagic.init.tile.menu.AetherCraftTableMenu;
import sweetmagic.init.tile.sm.TileAetherCraftTable;

public class GuiAetherCraftTable extends GuiSMBase<AetherCraftTableMenu> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_aether_crafttable.png");
	public final TileAetherCraftTable tile;
	public final AetherCraftTableMenu menu;
	private List<Integer> stackList = new ArrayList<>();
	private int startIndex = 0;
	private boolean scrollingView = false;

	public GuiAetherCraftTable(AetherCraftTableMenu menu, Inventory pInv, Component title) {
		super(menu, pInv, title);
		this.setGuiSize(190, 241);
		this.menu = menu;
		this.tile = menu.tile;
		this.addButtonMap(0, new SMButton(TEX, 170, 99, 194, 99, 16, 16, new SMButtonTip("id_sort", 10, -4)));
		this.addButtonMap(1, new SMButton(TEX, 170, 119, 194, 119, 16, 16, new SMButtonTip("name_sort", 10, -4)));
		this.addButtonMap(2, new SMButton(TEX, 170, 139, 194, 139, 16, 16, new SMButtonTip("chest_sort", 10, -4)));
		this.addButtonMap(3, new SMButton(TEX, 170, 159, 194, 159, 16, 16, new SMButtonTip("ascending_order", 10, -4)));
		this.addButtonMap(4, new SMButton(TEX, 170, 179, 194, 179, 16, 16, new SMButtonTip("inventory_sort", 10, -4)));
	}

	public void render(PoseStack pose, int mouseX, int mouseY, float parTick) {

		this.stackList.clear();

		for (Slot slot : this.menu.slotList) {
			if (!slot.isActive()) { continue; }

			ItemStack stack = slot.getItem().copy();
			this.stackList.add(stack.getCount());
			if (stack.isEmpty() || stack.getCount() <= 64) { continue; }
			stack.setCount(1);
			slot.set(stack);
		}

		super.render(pose, mouseX, mouseY, parTick);

		for (int i = 0; i < this.menu.slotList.size(); i++) {
			Slot slot = this.menu.slotList.get(i);
			if (!slot.isActive()) { continue; }

			int count = this.stackList.get(i);
			if (count <= 64) { continue; }

			ItemStack stack = slot.getItem();
			this.renderSlotItem(slot, stack, count, pose);

			stack.setCount(count);
			slot.set(stack);
		}
	}

	@Override
	protected void renderBg(PoseStack pose, float parTick, int mouseX, int mouseY) {
		super.renderBg(pose, parTick, mouseX, mouseY);

		this.scrollingView = !this.canScroll();
		int x = this.getWidth() + 174;
		int y = (int) (this.getHeight() + 8 + (73F * this.menu.scrollOffset));
		RenderSystem.setShaderTexture(0, this.getTEX());

		if (this.isRender(x, y, mouseX, mouseY, 12, 15)) {
			this.scrollingView = true;
		}

		// 最後のペーにブランクがあるなら
		if (this.menu.scrollOffset >= 1F && this.menu.maxSlots % 9 != 0) {
			for (int i = 0; i < 9 - this.menu.maxSlots % 9; i++) {
				this.blit(pose, this.getWidth() + 151 - i * 18, this.getHeight() + 79, 216, 0, 18, 18);
			}
		}

		// スロットが最大数以下なら
		else if (this.menu.maxSlots < 45) {

			int grayslot = 45 - this.menu.maxSlots;

			for (int i = 0; i < grayslot; i++) {
				this.blit(pose, this.getWidth() + 151 - i % 9 * 18, this.getHeight() + 79 - (i / 9) * 18, 216, 0, 18, 18);
			}
		}

		this.blit(pose, x, y, 195 + (this.scrollingView ? 8 : 0), 0, 8, 15);
	}

	@Override
	protected void renderTooltip(PoseStack pose, ItemStack stack, int x, int y) {

		List<Component> tipList = this.getTooltipFromItem(stack);
		int vMax = this.hoveredSlot.getMaxStackSize();

		if (vMax > 64) {
			int index = this.hoveredSlot.getSlotIndex();
			if (index >= this.stackList.size()) { return; }

			int vCount = this.stackList.get(index);
			String count = String.format("%,d", vCount) + "/";
			String max = String.format("%,d", vMax);
			String par = " (" + String.format("%.1f", ((float) vCount / (float) vMax) * 100F) + "%" + ")";

			tipList.add(this.getLabel(count + max + par).withStyle(GOLD));
		}
		this.renderTooltip(pose, tipList, stack.getTooltipImage(), x, y);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {

		if (this.canScroll() && this.scrollingView) {

			int i = this.topPos + 23;
			int j = i + 87;
			int maxSlots = this.menu.maxSlots;
			int offscreenRows = maxSlots / 9 - 5;

			if (maxSlots > 9 && maxSlots % 9 != 0) {
				offscreenRows += 1;
			}

			this.menu.scrollOffset = ((float) mouseY - (float) i + 5F) / ((float) (j - i) - 15F);
			this.menu.scrollOffset = Mth.clamp(this.menu.scrollOffset, 0F, 1F);
			this.startIndex = (int) ((double) (this.menu.scrollOffset * (float) offscreenRows) + 0.5D);
			this.menu.updateSlotPos(this.startIndex);

			return true;
		}

		return super.mouseDragged(mouseX, mouseY, mouseButton, dragX, dragY);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollDelta) {

		if (!this.canScroll()) {
			return super.mouseScrolled(mouseX, mouseY, scrollDelta);
		}

		if ( Screen.hasShiftDown() || this.canScrolled(mouseX, mouseY) ) {

			int maxSlots = this.menu.maxSlots;
			int offscreenRows = maxSlots / 9 - 5;

			if (maxSlots > 9 && maxSlots % 9 != 0) {
				offscreenRows += 1;
			}

			this.menu.scrollOffset = (float) ((double) this.menu.scrollOffset - scrollDelta / (double) offscreenRows);
			this.menu.scrollOffset = Mth.clamp(this.menu.scrollOffset, 0F, 1F);
			this.startIndex = (int) ((double) (this.menu.scrollOffset * (float) offscreenRows) + 0.5D);
			this.menu.updateSlotPos(this.startIndex);
			return true;
		}

		return super.mouseScrolled(mouseX, mouseY, scrollDelta);
	}

	public boolean canScrolled(double mouseX, double mouseY) {

		int x = this.getWidth() + 7;
		int y = this.getHeight() + 7;
		boolean mousetweaksLoaded = SweetMagicCore.mousetweaksLoaded;

		if (mousetweaksLoaded &&
			x <= mouseX && mouseX <= x + 161 &&
			y <= mouseY && mouseY <= y + 89 ) {
			return false;
		}

		x = this.getWidth() + 44;
		y = this.getHeight() + 101;

		if (mousetweaksLoaded &&
			x <= mouseX && mouseX <= x + 105 &&
			y <= mouseY && mouseY <= y + 52 ) {
			return false;
		}

		x = this.getWidth() + 7;
		y = this.getHeight() + 158;

		if (mousetweaksLoaded &&
			x <= mouseX && mouseX <= x + 161 &&
			y <= mouseY && mouseY <= y + 75 ) {
			return false;
		}

		return true;
	}

	public void addPage(double scrollDelta) {
		int maxSlots = this.menu.maxSlots;
		int offscreenRows = maxSlots / 9 - 5;

		if (maxSlots > 9 && maxSlots % 9 != 0) {
			offscreenRows += 1;
		}

		this.menu.scrollOffset = (float) ((double) this.menu.scrollOffset - scrollDelta / (double) offscreenRows);
		this.menu.scrollOffset = Mth.clamp(this.menu.scrollOffset, 0F, 1F);
		this.startIndex = (int) ((double) (this.menu.scrollOffset * (float) offscreenRows) + 0.5D);
		this.menu.updateSlotPos(this.startIndex);
	}

	public boolean canScroll() {
		return this.menu.maxSlots >= 45;
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

		if (stackSize.length() > 3) {
			int stackLength = (stackSize.length() - 1) / 3;
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

	protected ResourceLocation getTEX() {
		return TEX;
	}
}
