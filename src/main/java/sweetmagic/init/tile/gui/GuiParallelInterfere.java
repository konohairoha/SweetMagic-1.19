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
import sweetmagic.init.tile.menu.ParallelInterfereMenu;
import sweetmagic.init.tile.sm.TileParallelInterfere;

public class GuiParallelInterfere extends GuiSMBase<ParallelInterfereMenu> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_parallel_book.png");
	public final TileParallelInterfere tile;
	public final ParallelInterfereMenu piMenu;

	private float scrollOffset = 0F;
	private int startIndex = 0;
	private boolean scrolling = false;
	private boolean scrollingView = false;
	private boolean upPageView = false;
	private boolean downPageView = false;

	public GuiParallelInterfere(ParallelInterfereMenu menu, Inventory pInv, Component title) {
		super(menu, pInv, title);
		this.setGuiWidth(192);
		this.setGuiHeight(202);
		this.piMenu = menu;
		this.tile = menu.tile;

		this.getButtonMap().put(0, new SMButton(MISC, 150, -11, 114, 0, 10, 9, new SMButtonTip("sort", -18, 14)));
		this.getButtonMap().put(1, new SMButton(MISC, 163, -11, 114, 33, 9, 9, new SMButtonTip("parallel_interfere_info", -18, 14)));
	}

	@Override
	protected void renderBg(PoseStack pose, float parTick, int mouseX, int mouseY) {
		super.renderBg(pose, parTick, mouseX, mouseY);

		this.scrollingView = false;
		int x = this.getWidth() + 174;
		int y = (int) (this.getHeight() + 8 + (91F * this.scrollOffset));
		RenderSystem.setShaderTexture(0, this.getTEX());

		if (this.isRendeer(x, y, mouseX, mouseY, 12, 15)) {
			this.scrollingView = true;
		}

		this.blit(pose, x, y, 0 + (this.scrollingView ? 12 : 0), 202, 12, 15);
		x = this.getWidth();
		y = this.getHeight();
		RenderSystem.setShaderTexture(0, MISC);
		this.blit(pose, x + 122, y - 11, 137 + (this.upPageView ? 11 : 0), 0, 11, 9);
		this.blit(pose, x + 136, y- 11, 161 + (this.downPageView ? 11 : 0), 0, 11, 9);
	}

	@Override
	protected void renderLabels(PoseStack pose, int mouseX, int mouseY) {
		super.renderLabels(pose, mouseX, mouseY);

		this.upPageView = false;
		this.downPageView = false;
		int x = this.getWidth() + 122;
		int y = this.getHeight() - 11;

		int xAxis = mouseX - this.getWidth();
		int yAxis = mouseY - this.getHeight();

		if (this.isRendeer(x, y, mouseX, mouseY, 10, 9)) {
			this.upPageView = true;
            this.renderTooltip(pose, this.getText("page_up"), xAxis - 72, yAxis + 12);
		}

		x = this.getWidth() + 136;

		if (this.isRendeer(x, y, mouseX, mouseY, 10, 9)) {
			this.downPageView = true;
            this.renderTooltip(pose, this.getText("page_down"), xAxis - 72, yAxis + 12);
		}
	}

	public boolean mouseClicked(double guiX, double guiY, int mouseButton) {

		this.scrolling = false;
		int aX = this.leftPos + 178;
		int aY = (int) (this.topPos + 8 + (91F * this.scrollOffset));
		int w = 12;
		int h = 15;

		// スクロールバーの当たり判定チェック
		if (guiX >= aX && guiX < aX + w && guiY >= aY && guiY < aY + h) {
			this.scrolling = true;
		}

		aX = this.leftPos + 122;
		aY = this.topPos - 11;
		w = 10;
		h = 9;

		if (guiX >= aX && guiX < aX + w && guiY >= aY && guiY < aY + h) {
			this.addPage(6);
			this.clickButton(2);
			return true;
		}

		aX = this.leftPos + 136;

		if (guiX >= aX && guiX < aX + w && guiY >= aY && guiY < aY + h) {
			this.addPage(-6);
			this.clickButton(2);
			return true;
		}

		return super.mouseClicked(guiX, guiY, mouseButton);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {

		if (!this.scrolling) { return super.mouseDragged(mouseX, mouseY, mouseButton, dragX, dragY); }

		int i = this.topPos + 23;
		int j = i + 105;
		int offscreenRows = this.tile.getInvSize() / 9 - 6;
		this.scrollOffset = ((float) mouseY - (float) i + 5F) / ((float) (j - i) - 15F);
		this.scrollOffset = Mth.clamp(this.scrollOffset, 0F, 1F);
		this.startIndex = (int) ((double) (this.scrollOffset * (float) offscreenRows) + 0.5D);
		this.piMenu.updateSlotPositions(this.startIndex);
		return true;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollDelta) {

		int x = this.getWidth() + 174;
		int y = this.getHeight() + 8;

		if ( Screen.hasShiftDown() || this.isRendeer(x, y, (int) mouseX, (int) mouseY, 12, 105)) {
			int offscreenRows = this.tile.getInvSize() / 9 - 6;
			this.scrollOffset = (float) ((double) this.scrollOffset - scrollDelta / (double) offscreenRows);
			this.scrollOffset = Mth.clamp(this.scrollOffset, 0F, 1F);
			this.startIndex = (int) ((double) (this.scrollOffset * (float) offscreenRows) + 0.5D);
			this.piMenu.updateSlotPositions(this.startIndex);
			return true;
		}

		return super.mouseScrolled(mouseX, mouseY, scrollDelta);
	}

	public void addPage (double scrollDelta) {
		int offscreenRows = this.tile.getInvSize() / 9 - 6;
		this.scrollOffset = (float) ((double) this.scrollOffset - scrollDelta / (double) offscreenRows);
		this.scrollOffset = Mth.clamp(this.scrollOffset, 0F, 1F);
		this.startIndex = (int) ((double) (this.scrollOffset * (float) offscreenRows) + 0.5D);
		this.piMenu.updateSlotPositions(this.startIndex);
	}

	protected ResourceLocation getTEX () {
		return TEX;
	}
}
