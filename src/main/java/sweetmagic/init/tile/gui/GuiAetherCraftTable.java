package sweetmagic.init.tile.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.tile.gui.util.SMButton;
import sweetmagic.init.tile.gui.util.SMButton.SMButtonTip;
import sweetmagic.init.tile.menu.AetherCraftTableMenu;
import sweetmagic.init.tile.sm.TileAetherCraftTable;

public class GuiAetherCraftTable extends GuiSMBase<AetherCraftTableMenu> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_aether_crafttable.png");
	public final TileAetherCraftTable tile;
	public final AetherCraftTableMenu piMenu;

	private int startIndex = 0;
	private boolean scrollingView = false;

	public GuiAetherCraftTable(AetherCraftTableMenu menu, Inventory pInv, Component title) {
		super(menu, pInv, title);

		this.setGuiWidth(190);
		this.setGuiHeight(241);
		this.piMenu = menu;
		this.tile = menu.tile;
		this.getButtonMap().put(0, new SMButton(TEX, 170, 99, 194, 99, 16, 16, new SMButtonTip("id_sort", 10, -4)));
		this.getButtonMap().put(1, new SMButton(TEX, 170, 119, 194, 119, 16, 16, new SMButtonTip("name_sort", 10, -4)));
		this.getButtonMap().put(2, new SMButton(TEX, 170, 139, 194, 139, 16, 16, new SMButtonTip("chest_sort", 10, -4)));
		this.getButtonMap().put(3, new SMButton(TEX, 170, 159, 194, 159, 16, 16, new SMButtonTip("ascending_order", 10, -4)));
		this.getButtonMap().put(4, new SMButton(TEX, 170, 179, 194, 179, 16, 16, new SMButtonTip("inventory_sort", 10, -4)));
	}

	@Override
	protected void renderBg(PoseStack pose, float parTick, int mouseX, int mouseY) {
		super.renderBg(pose, parTick, mouseX, mouseY);

		this.scrollingView = !this.canScroll();
		int x = this.getWidth() + 174;
		int y = (int) (this.getHeight() + 8 + (73F * this.piMenu.scrollOffset));
		RenderSystem.setShaderTexture(0, this.getTEX());

		if (this.isRendeer(x, y, mouseX, mouseY, 12, 15)) {
			this.scrollingView = true;
		}

		// 最後のペーにブランクがあるなら
		if (this.piMenu.scrollOffset >= 1F && this.piMenu.maxSlots % 9 != 0) {
			for (int i = 0; i < 9 - this.piMenu.maxSlots % 9; i++) {
				this.blit(pose, this.getWidth() + 151 - i * 18, this.getHeight() + 79, 216, 0, 18, 18);
			}
		}

		// スロットが最大数以下なら
		else if (this.piMenu.maxSlots < 45) {

			int grayslot = 45 - this.piMenu.maxSlots;

			for (int i = 0; i < grayslot; i++) {
				this.blit(pose, this.getWidth() + 151 - i % 9 * 18, this.getHeight() + 79 - (i / 9) * 18, 216, 0, 18, 18);
			}
		}

		this.blit(pose, x, y, 195 + (this.scrollingView ? 8 : 0), 0, 8, 15);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {

		if (this.canScroll() && this.scrollingView) {

			int i = this.topPos + 23;
			int j = i + 87;
			int maxSlots = this.piMenu.maxSlots;
			int offscreenRows = maxSlots / 9 - 5;

			if (maxSlots > 9 && maxSlots % 9 != 0) {
				offscreenRows += 1;
			}

			this.piMenu.scrollOffset = ((float) mouseY - (float) i + 5F) / ((float) (j - i) - 15F);
			this.piMenu.scrollOffset = Mth.clamp(this.piMenu.scrollOffset, 0F, 1F);
			this.startIndex = (int) ((double) (this.piMenu.scrollOffset * (float) offscreenRows) + 0.5D);
			this.piMenu.updateSlotPos(this.startIndex);

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

			int maxSlots = this.piMenu.maxSlots;
			int offscreenRows = maxSlots / 9 - 5;

			if (maxSlots > 9 && maxSlots % 9 != 0) {
				offscreenRows += 1;
			}

			this.piMenu.scrollOffset = (float) ((double) this.piMenu.scrollOffset - scrollDelta / (double) offscreenRows);
			this.piMenu.scrollOffset = Mth.clamp(this.piMenu.scrollOffset, 0F, 1F);
			this.startIndex = (int) ((double) (this.piMenu.scrollOffset * (float) offscreenRows) + 0.5D);
			this.piMenu.updateSlotPos(this.startIndex);
			return true;
		}

		return super.mouseScrolled(mouseX, mouseY, scrollDelta);
	}

	public boolean canScrolled (double mouseX, double mouseY) {

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

	public void addPage (double scrollDelta) {
		int maxSlots = this.piMenu.maxSlots;
		int offscreenRows = maxSlots / 9 - 5;

		if (maxSlots > 9 && maxSlots % 9 != 0) {
			offscreenRows += 1;
		}

		this.piMenu.scrollOffset = (float) ((double) this.piMenu.scrollOffset - scrollDelta / (double) offscreenRows);
		this.piMenu.scrollOffset = Mth.clamp(this.piMenu.scrollOffset, 0F, 1F);
		this.startIndex = (int) ((double) (this.piMenu.scrollOffset * (float) offscreenRows) + 0.5D);
		this.piMenu.updateSlotPos(this.startIndex);
	}

	public boolean canScroll () {
		return this.piMenu.maxSlots >= 45;
	}

	protected ResourceLocation getTEX () {
		return TEX;
	}
}
