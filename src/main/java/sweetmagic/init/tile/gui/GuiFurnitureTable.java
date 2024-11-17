package sweetmagic.init.tile.gui;

import java.util.Arrays;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.tile.gui.util.SMButton;
import sweetmagic.init.tile.gui.util.SMButton.SMButtonTip;
import sweetmagic.init.tile.menu.FurnitureTableMenu;
import sweetmagic.init.tile.sm.TileFurnitureTable;

public class GuiFurnitureTable extends GuiSMBase<FurnitureTableMenu> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_furniture_processing_table.png");
	private final TileFurnitureTable tile;
	private final FurnitureTableMenu menu;

	private float scrollOffset = 0F;
	private int startIndex = 0;
	private boolean scrolling = false;
	private boolean isView[] = new boolean[24];

	public GuiFurnitureTable(FurnitureTableMenu menu, Inventory pInv, Component title) {
		super(menu, pInv, title);

		this.tile = menu.tile;
		this.menu = (FurnitureTableMenu) menu;
		this.setGuiWidth(176);
		this.setGuiHeight(252);

		SMButtonTip buttonTip = new SMButtonTip("", 40, -3, this.tile) {

			public boolean isFlagText (TileFurnitureTable tile) {
				return tile.isSelect;
			}

			public String getTip () {
				return this.isFlagText(tile) ? "caraft_start" : "select_recipe";
			}
		};

		SMButton button = new SMButton(MISC, 9, 90, 114, 15, 32, 12, buttonTip) {

			public boolean isButtonRender () {
				return tile.isSelect;
			}
		};

		this.getButtonMap().put(0, button);

		SMButtonTip buttonTip2 = new SMButtonTip("", 30, -3, this.tile) {

			public boolean isFlagText (TileFurnitureTable tile) {
				return tile.isSelect;
			}

			public String getTip () {
				return this.isFlagText(tile) ? "select_count" : "select_recipe";
			}
		};

		this.getButtonMap().put(1, new SMButton(TEX, 36, 71, 178, 77, 18, 11, buttonTip2));
	}

	@Override
	protected void renderBg(PoseStack pose, float parTick, int mouseX, int mouseY) {
		super.renderBg(pose, parTick, mouseX, mouseY);

		RenderSystem.setShaderTexture(0, this.getTEX());
		List<Recipe<?>> recipeList = this.getRecipeList();

		// レシピがないならスクロールバーの初期化
		if (recipeList.isEmpty()) {
			this.startIndex = 0;
			this.scrollOffset = 0F;
			return;
		}

		int x = this.getWidth() + 58;
		int y = this.getHeight() + 31;
		int index = this.startIndex * 6;
		int yIndex = -this.startIndex * 18;

		// スロットの描画
		for (int i = index; i < recipeList.size(); i++ ) {

			if (i - index >= 24) { break; }

			int pX = x + i * 16 - (i / 6) * 96;
			int pY = y + (i / 6) * 18 + yIndex;
			boolean isView = (i >= index || i < index + 24) ? this.isView[i - index] : false;
			int viewY = 20 + (isView ? 18 : 0);

			if (this.tile.isSelect) {
				viewY = i + 2 == this.tile.selectId ? 56 : viewY;
			}

			this.blit(pose, pX, pY, 178, viewY, 18, 18);
		}

		// スクロールバーの表示
		int h = (int) (60F * this.scrollOffset);
		boolean isActive = this.scrollbarActive();
		this.blit(pose, x + 102, y + 7 + h - 8, 178 + (isActive ? 0 : 8), 0, 8, 15);

		// スクロール出来ないなら初期位置に戻す
		if (!isActive && !tile.isCraft) {
			this.startIndex = 0;
			this.scrollOffset = 0F;
		}

		// クラフトアイテムの描画
		for (int i = index; i < recipeList.size(); i++ ) {

			// 24を超えた場合は終了
			if (i - index >= 24) { break; }

			Recipe<?> recipe = recipeList.get(i);
			ItemStack result = recipe.getResultItem();

			int pX = x + i * 16 - (i / 6) * 96;
			int pY = y + (i / 6) * 18 + 1 + yIndex;

			this.itemRenderer.renderAndDecorateFakeItem(result, pX, pY);
			this.itemRenderer.renderGuiItemDecorations(this.font, result, pX, pY, null);
		}

		// クラフト後のアイテムを描画
		if (!this.menu.inputSlot.getItem().isEmpty() && this.tile.outStack.isEmpty() && this.tile.isSelect) {
			this.tile.outStack = this.menu.recipeList.get(this.tile.selectId - 1).getResultItem().copy();
		}

		this.renderSlotItem(this.menu.resultSlot, this.tile.outStack, this.tile.setCount, pose);
	}

	@Override
	protected void renderLabels(PoseStack pose, int mouseX, int mouseY) {
		super.renderLabels(pose, mouseX, mouseY);

		//GUIの左上からの位置
		int x = this.getWidth() + 58;
		int y = this.getHeight() + 31;
		int w = 16;
		int h = 17;
		int texSizeX = this.getWidth();
		int texSizeY = this.getHeight();

		// カーソルを合わせたときにアイテムのツールチップ表示とスロット選択状態を設定
		for (int pX = 0; pX < 6; pX++) {
			for (int pY = 0; pY < 4; pY++) {

				int index = pX + pY * 6;
				this.isView[index] = false;

				int tX = x + pX * 16;
				int tY = y + pY * 18;

				if (tX <= mouseX && mouseX <= tX + w - 1
						&& tY <= mouseY && mouseY <= tY + h) {

					this.isView[index] = true;
					index += this.startIndex * 6;
					if (index >= this.getRecipeList().size()) { continue; }

					int xAxis = mouseX - texSizeX;
					int yAxis = mouseY - texSizeY;
					this.renderTooltip(pose, this.getRecipeList().get(pX + (pY + this.startIndex) * 6).getResultItem(), xAxis, yAxis);
				}
			}
		}

		if (!this.menu.inputSlot.getItem().isEmpty() && this.tile.outStack.isEmpty() && this.tile.isSelect) {
			this.tile.outStack = this.menu.recipeList.get(this.tile.selectId - 1).getResultItem().copy();
		}

		// クラフト後のアイテムのチップを表示
		if (!this.tile.outStack.isEmpty()) {
			this.renderItemLabel(this.menu.resultSlot, this.tile.outStack, pose, mouseX, mouseY, Arrays.<Component> asList(this.getText("craft_item").withStyle(GREEN), this.tile.outStack.getHoverName()));
		}
	}

	public boolean mouseClicked(double guiX, double guiY, int mouseButton) {

		this.scrolling = false;
		int aX = this.leftPos + 160;
		int aY = (int) (this.topPos + 30 + (this.scrollOffset * 60F));
		int w = 8;
		int h = 14;

		// スクロールバーの当たり判定チェック
		if (guiX >= aX && guiX < aX + w && guiY >= aY && guiY < aY + h) {
			this.scrolling = true;
		}

		int x = this.getWidth() + 58;
		int y = this.getHeight() + 31;
		double dX = guiX - x;
		double dY = guiY - y;
		w = 16;
		h = 18;

		for (int pX = 0; pX < 6; pX++) {
			for (int pY = 0; pY < 4; pY++) {

				int i = pX + pY * 6;
				int index = i + this.startIndex * 6;
				if ((i >= index + 24)) { break; }

				double tX = dX - pX * w;
				double tY = dY - pY * h;

				if (tX >= 0D && tX < w && tY >= 0D && tY < h) {
					int buttonId = pX + pY * 6 + 2 + this.startIndex * 6;
					this.clickButton(buttonId);
				}
			}
		}

		return super.mouseClicked(guiX, guiY, mouseButton);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {

		List<Recipe<?>> recipeList = this.getRecipeList();
		int size = recipeList.size();
		if (!this.scrolling || !this.scrollbarActive()) { return super.mouseDragged(mouseX, mouseY, mouseButton, dragX, dragY); }

		int i = topPos + 20;
		int j = i + 73;
		int offscreenRows = size / 6 - 3;
		this.scrollOffset = ((float) mouseY - (float) i - 15F) / ((float) (j - i) - 15F);
		this.scrollOffset = Mth.clamp(this.scrollOffset, 0F, 1F);
		this.startIndex = (int) ((double) (this.scrollOffset * (float) offscreenRows) + 0.5D);
		return true;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollDelta) {
		if (!this.scrollbarActive()) { return super.mouseScrolled(mouseX, mouseY, scrollDelta); }

		int x = this.getWidth() + 33;
		int y = this.getHeight() + 25;

		if (this.isRendeer(x, y, (int) mouseX, (int) mouseY, 142, 86) || Screen.hasShiftDown()) {

			List<Recipe<?>> recipeList = this.getRecipeList();
			int size = recipeList.size();
			int offscreenRows = size / 6 - 3;

			this.scrollOffset = (float) ((double) this.scrollOffset - scrollDelta / (double) offscreenRows);
			this.scrollOffset = Mth.clamp(this.scrollOffset, 0F, 1F);
			this.startIndex = (int) ((double) (this.scrollOffset * (float) offscreenRows) + 0.5D);
			return true;
		}

		return super.mouseScrolled(mouseX, mouseY, scrollDelta);
	}

	protected ResourceLocation getTEX () {
		return TEX;
	}

	private boolean scrollbarActive() {
		return this.getRecipeList().size() > 24;
	}

	public List<Recipe<?>> getRecipeList () {
		return this.menu.recipeList;
	}
}
