package sweetmagic.init.tile.gui;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.TagInit;
import sweetmagic.init.tile.gui.util.SMRenderTex;
import sweetmagic.init.tile.gui.util.SMRenderTex.MFRenderGage;
import sweetmagic.init.tile.menu.MagiaRewriteMenu;
import sweetmagic.init.tile.sm.TileMagiaRewrite;

public class GuiMagiaRewrite extends GuiSMBase<MagiaRewriteMenu> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_magiawrite.png");
	private static final ResourceLocation MISC = SweetMagicCore.getSRC("textures/gui/gui_misc.png");
	private final MagiaRewriteMenu menu;
	public final TileMagiaRewrite tile;
	private int tickTime = 0;
	private int counter = 0;
	private float scrollOffset = 0F;
	private int startIndex = 0;
	private boolean scrolling = false;
	private boolean enchaView[] = new boolean[4];
	private boolean addLevelView = false;
	private boolean subLevelView = false;
	private final static List<ItemStack> MAGICBOOK_LIST = GuiSMBase.getTagStack(TagInit.WISH_CRYSTAL);

	public GuiMagiaRewrite(MagiaRewriteMenu menu, Inventory pInv, Component title) {
		super(menu, pInv, title);
		this.setGuiSize(217, 190);
		this.menu = menu;
		this.tile = menu.tile;
		this.scrolling = false;
		this.startIndex = 0;
		this.scrollOffset = 0F;
		this.addRenderTexList(new SMRenderTex(TEX, 35, 29, 0, 0, 11, 77, new MFRenderGage(menu.tile, true)));
	}

	protected void renderBGBase (PoseStack pose, float parTick, int mouseX, int mouseY) {
		RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
		RenderSystem.setShaderTexture(0, this.getTEX());
		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
		this.blit(pose, this.leftPos + 24, this.topPos, 0, 0, this.imageWidth - 24, this.imageHeight);
	}

	@Override
	protected void renderBg(PoseStack pose, float parTick, int mouseX, int mouseY) {
		super.renderBg(pose, parTick, mouseX, mouseY);

		int x = this.getWidth() + 24;
		int y = this.getHeight();
		RenderSystem.setShaderTexture(0, MISC);
		this.blit(pose, x - 24, y + 110, 70, 0, 24, 78);
		RenderSystem.setShaderTexture(0, TEX);

		// 魔術書を順番に表示
		if (this.tickTime++ > 120) {
			this.tickTime = 0;

			if (this.counter++ >= MAGICBOOK_LIST.size() - 1) {
				this.counter = 0;
			}
		}

		TileMagiaRewrite tile = this.tile;

		// 作成時間の表示
		if (tile.craftTime > 0) {
			int progress = tile.getCraftProgress(30);
			this.blit(pose, x + 44, y + 45, 224, 29, 15, progress);
		}

		// スクロールバーの表示
		int h = (int) (60F * this.scrollOffset);
		boolean isActive = this.scrollbarActive();
		RenderSystem.setShaderTexture(0, MISC);
		this.blit(pose, x + 178, y + 30 + h, 83 + (isActive ? 0 : 8), 93, 8, 15);

		// スクロール出来ないなら初期位置に戻す
		if (!isActive && !tile.isCraft) {
			this.startIndex = 0;
			this.scrollOffset = 0F;
		}

		for (int id = 0; id < 4; id++)
			this.blit(pose, x + 76, y + 26 + id * 21, 99, 93, 101, 20);

		// エンチャリストがあるならボタンの表示
		ItemStack stack = tile.getBookItem();
		List<Enchantment> enchaList = tile.getEnchaList();

		if (!enchaList.isEmpty() && !stack.isEmpty() && !tile.isCraft) {

			int size = enchaList.size();

			for (int id = 0; id < 4; id++) {

				if (id + this.startIndex >= size) { break; }

				int cost = tile.getEnchantCost(id + this.startIndex);
				if (cost <= 0) { continue; }

				this.blit(pose, x + 76, y + 26 + id * 21, 99, 113 + (this.enchaView[id] ? 20 : 0), 101, 20);
			}
		}

		if (!stack.isEmpty()) {

			int nowLevel = tile.getNowLevel();
			boolean canSub = nowLevel > 1;
			boolean canAdd = (tile.getAddMaxEnchantLevel() - 1) > nowLevel;
			RenderSystem.setShaderTexture(0, TEX);

			if (canSub) {
				this.blit(pose, x + 177, y + 17, 212 + (this.subLevelView ? 7 : 0), 11, 7, 10);
			}

			if (canAdd) {
				this.blit(pose, x + 177, y + 6, 212 + (this.addLevelView ? 7 : 0), 0, 7, 10);
			}

			this.font.drawShadow(pose, this.getTipArray("Level: ", this.getLabel("" + nowLevel).withStyle(WHITE)), x + 112, y + 12, 0x2BC444);
		}

		// アイテム描画
		this.renderSlotItem(this.menu.magicBookSlot, MAGICBOOK_LIST.get(this.counter), pose);
		if (enchaList.isEmpty() || stack.isEmpty() || tile.isCraft) { return; }

		int size = enchaList.size();

		for (int id = 0; id < 4; id++) {

			if (id + this.startIndex >= size) { break; }
			MutableComponent name = this.getTip(enchaList.get(id + this.startIndex).getDescriptionId());

			int color = 0x2BC444;

			if (this.enchaView[id]) {
				color = 0xEF5672;
			}

			else if (tile.getEnchantCost(id + this.startIndex) <= 0) {
				color = 0xD0C3C1;
			}

			this.font.drawShadow(pose, name, x + 84, y + 32 + id * 21, color);
		}
	}

	@Override
	protected void renderLabels(PoseStack pose, int mouseX, int mouseY) {
		super.renderLabels(pose, mouseX, mouseY);

		// 要求アイテムの描画
		this.renderItemLabel(this.menu.magicBookSlot, MAGICBOOK_LIST.get(this.counter), pose, mouseX, mouseY);
		this.renderItemLabel(this.menu.bookSlot, ItemStack.EMPTY, pose, mouseX, mouseY);

		int addX = 24;
		int x = this.getWidth();
		int y = this.getHeight();
		int tipX = x + 106 + addX;
		int tipY = y / 2 + 8;
		int xAxis = mouseX - x;
		int yAxis = mouseY - y;

		if (this.isRender(tipX, tipY, mouseX, mouseY, 65, 15)) {
			this.renderTooltip(pose, this.getText("add_level").withStyle(GOLD), xAxis, yAxis);
		}

		//描画位置を計算
		tipX = x + 11 + addX;
		tipY = y + 29;
		TileMagiaRewrite tile = this.tile;
		ItemStack magicBook = tile.getBookItem();
		if (tile.getInputItem().isEmpty() || magicBook.isEmpty() || tile.isCraft) { return; }

		//描画位置を計算
		tipX = x + 177 + addX;
		tipY = y + 6;
		this.addLevelView = false;
		this.subLevelView = false;

		if (this.isRender(tipX, tipY, mouseX, mouseY, 7, 10)) {
			this.addLevelView = true;
		}

		tipY += 11;

		if (this.isRender(tipX, tipY, mouseX, mouseY, 7, 10)) {
			this.subLevelView = true;
		}

		//描画位置を計算
		tipX = this.getWidth() + 76 + addX;
		tipY = this.getHeight() + 30;

		for (int id = 0; id < 4; id++) {

			this.enchaView[id] = false;

			if (!this.isRender(tipX, tipY, mouseX, mouseY, 98, 18)) {
				tipY += 19;
				continue;
			}

			int buttonId = id + this.startIndex;
			int cost = tile.getEnchantCost(buttonId);

			if (cost <= 0) {
				int changedLevel = tile.getChanedLevel(buttonId);
				int nowLevel = tile.getNowEnchantLevel(buttonId);

				if (changedLevel > nowLevel) {
					String tip = cost == -1 ? "needwish_count" : "needmaxlevel";
					List<Component> tipList = new ArrayList<>();
					tipList.add(this.getText(tip).withStyle(GOLD));
					this.renderComponentTooltip(pose, tipList, xAxis - 80, yAxis - 7);
				}
				break;
			}

			String level = "" + tile.getEnchaLevel(buttonId);
			int maxLevel = tile.getEnchant(buttonId).getMaxLevel();
			String addLevel = "" + tile.getChanedLevel(buttonId);
			String canMaxLevel = "" + tile.getMaxRwiteLevel(maxLevel);

			String tip = String.format("%,d", cost);
			List<Component> tipList = new ArrayList<>();
			tipList.add(this.getTipArray(this.getText("needmf"), this.getLabel(tip).withStyle(WHITE)).withStyle(GOLD));
			tipList.add(this.getTipArray(this.getText("nowlevel"), ": ", this.getLabel(level).withStyle(WHITE), " → ", this.getLabel(addLevel).withStyle(RED)).withStyle(GREEN));
			tipList.add(this.getTipArray(this.getText("maxlevel"), ": ", this.getLabel("" + maxLevel).withStyle(WHITE), " (", this.getLabel(canMaxLevel).withStyle(RED), ")").withStyle(GREEN));
			this.renderComponentTooltip(pose, tipList, xAxis - 80, yAxis - 24);
			this.enchaView[id] = true;
			tipY += 19;
		}
	}

	public boolean mouseClicked(double guiX, double guiY, int mouseButton) {

		int addX = 24;
		this.scrolling = false;
		int aX = this.leftPos + 178 + addX;
		int aY = (int) (this.topPos + 30 + (this.scrollOffset * 60F));
		int w = 8;
		int h = 14;

		// スクロールバーの当たり判定チェック
		if (guiX >= aX && guiX < aX + w && guiY >= aY && guiY < aY + h) {
			this.scrolling = true;
		}

		int x = this.getWidth() + addX;
		int y = this.getHeight();
		double dX = guiX - (double) (x + 177) ;
		double dY = guiY - (double) (y + 6);

		// 各エンチャントの当たり判定チェック
		if (dX >= 0D && dX <= 7 && dY >= 0D && dY < 10) {
			this.clickButton(0);
		}

		// 各エンチャントの当たり判定チェック
		else if (dX >= 0D && dX <= 7 && dY >= 10 && dY < 20) {
			this.clickButton(1);
		}

		dX = guiX - (double) (x + 76);
		for (int id = 0; id < 4; ++id) {

			dY = guiY - (double) (y + 30 + 18 * id);

			// 各エンチャントの当たり判定チェック
			if (dX >= 0D && dX <= 95 && dY >= 0D && dY < 18D) {
				this.clickButton(id + this.startIndex + 2);
			}
		}

		return super.mouseClicked(guiX, guiY, mouseButton);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {
		if (!this.scrolling || !this.scrollbarActive()) { return super.mouseDragged(mouseX, mouseY, mouseButton, dragX, dragY); }

		int i = this.topPos + 44;
		int j = i + 73;
		List<Enchantment> enchaList = this.tile.getEnchaList();
		int size = enchaList.size();
		int offscreenRows = size - 4;
		this.scrollOffset = ((float) mouseY - (float) i + 5F) / ((float) (j - i) - 15F);
		this.scrollOffset = Mth.clamp(this.scrollOffset, 0F, 1F);
		this.startIndex = (int) ((double) (this.scrollOffset * (float) offscreenRows) + 0.5D);
		return true;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollDelta) {
		if (!this.scrollbarActive()) { return super.mouseScrolled(mouseX, mouseY, scrollDelta); }

		List<Enchantment> enchaList = this.tile.getEnchaList();
		int size = enchaList.size();
		int offscreenRows = size - 4;
		this.scrollOffset = (float) ((double) this.scrollOffset - scrollDelta / (double) offscreenRows);
		this.scrollOffset = Mth.clamp(this.scrollOffset, 0F, 1F);
		this.startIndex = (int) ((double) (this.scrollOffset * (float) offscreenRows) + 0.5D);
		return true;
	}

	// アイテム描画
	public void renderSlotItem (Slot slot, ItemStack stack, PoseStack pose) {
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

	private boolean scrollbarActive() {
		return this.tile.getEnchaList().size() > 4 && !this.tile.isCraft && !this.tile.getBookItem().isEmpty();
	}

	@Override
	protected ResourceLocation getTEX() {
		return TEX;
	}
}
