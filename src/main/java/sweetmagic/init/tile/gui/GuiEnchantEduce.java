package sweetmagic.init.tile.gui;

import java.util.Arrays;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.ItemInit;
import sweetmagic.init.TagInit;
import sweetmagic.init.item.sm.SMBook;
import sweetmagic.init.tile.gui.util.SMRenderTex;
import sweetmagic.init.tile.gui.util.SMRenderTex.MFRenderGage;
import sweetmagic.init.tile.menu.EnchantEduceMenu;
import sweetmagic.init.tile.sm.TileEnchantEduce;

public class GuiEnchantEduce extends GuiSMBase<EnchantEduceMenu> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_enchant_educe.png");
	private final TileEnchantEduce tile;
	private int tickTime = 0;
	private int counter = 0;
	private float scrollOffset = 0F;
	private int startIndex = 0;
	private boolean scrolling = false;
	private boolean enchaView[] = new boolean[4];
	private boolean addLevelView = false;
	private boolean subLevelView = false;
	private final static ItemStack BOOK = new ItemStack(Items.BOOK);
	private final static ItemStack PAGE = new ItemStack(ItemInit.mysterious_page);
	private final static List<ItemStack> MAGICBOOK_LIST = GuiSMBase.getTagStack(TagInit.MAGIC_BOOK);

	public GuiEnchantEduce(EnchantEduceMenu menu, Inventory pInv, Component title) {
		super(menu, pInv, title);
		this.setGuiSize(217, 190);
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

		// 作成時間の表示
		if (this.tile.craftTime > 0) {
			int progress = this.tile.getCraftProgress(30);
			this.blit(pose, x + 39, y + 45, 210, 45, 25, progress);
		}

		// スクロールバーの表示
		int h = (int) (60F * this.scrollOffset);
		boolean isActive = this.scrollbarActive();
		RenderSystem.setShaderTexture(0, MISC);
		this.blit(pose, x + 178, y + 30 + h, 83 + (isActive ? 0 : 8), 93, 8, 15);

		// スクロール出来ないなら初期位置に戻す
		if (!isActive && !this.tile.isCraft) {
			this.startIndex = 0;
			this.scrollOffset = 0F;
		}

		for (int id = 0; id < 4; id++)
			this.blit(pose, x + 76, y + 26 + id * 21, 99, 93, 101, 20);

		ItemStack stack = this.tile.getBookItem();

		// エンチャリストがあるならボタンの表示
		List<Enchantment> enchaList = this.tile.getEnchaList();
		if (!enchaList.isEmpty() && !stack.isEmpty() && !this.tile.isCraft) {

			int size = enchaList.size();
			for (int id = 0; id < 4; id++) {

				if (id + this.startIndex >= size) { break; }

				if (stack.isEmpty()) { continue; }

				int cost = this.tile.getEnchantCost(id + this.startIndex, (SMBook) stack.getItem());
				if (cost > 0) {
					this.blit(pose, x + 76, y + 26 + id * 21, 99, 113 + (this.enchaView[id] ? 20 : 0), 101, 20);
				}
			}
		}

		if (!stack.isEmpty()) {

			int tier = this.tile.getPageCount(stack);
			int nowLevel = this.tile.getNowLevel();
			boolean canSub = nowLevel > this.tile.getMinLevel();
			boolean canAdd = this.tile.getMaxLevel(tier) > nowLevel;
			RenderSystem.setShaderTexture(0, TEX);

			if (canSub) {
				this.blit(pose, x + 182, y + 14, 210 + (this.subLevelView ? 7 : 0), 11, 7, 10);
			}

			if (canAdd) {
				this.blit(pose, x + 182, y + 3, 210 + (this.addLevelView ? 7 : 0), 0, 7, 10);
			}

			this.font.drawShadow(pose, this.getTipArray("Level: ", this.getLabel(nowLevel, WHITE)), x + 112, y + 12, 0x2BC444);
		}

		// アイテム描画
		this.renderSlotItem(this.menu.pageSlot, PAGE, pose);
		this.renderSlotItem(this.menu.magicBookSlot, MAGICBOOK_LIST.get(this.counter), pose);
		if (enchaList.isEmpty() || stack.isEmpty() || tile.isCraft) { return; }

		int size = enchaList.size();

		for (int id = 0; id < 4; id++) {
			if (id + this.startIndex >= size) { break; }
			MutableComponent name = this.getTip(enchaList.get(id + this.startIndex).getDescriptionId());

			int color = this.enchaView[id] ? 0xEF5672 : 0x2BC444;
			this.font.drawShadow(pose, name, x + 84, y + 32 + id * 21, color);
		}
	}

	@Override
	protected void renderLabels(PoseStack pose, int mouseX, int mouseY) {
		super.renderLabels(pose, mouseX, mouseY);

		// 要求アイテムの描画
		this.renderItemLabel(this.menu.bookSlot, BOOK, pose, mouseX, mouseY);
		this.renderItemLabel(this.menu.pageSlot, PAGE, pose, mouseX, mouseY);
		this.renderItemLabel(this.menu.magicBookSlot, MAGICBOOK_LIST.get(this.counter), pose, mouseX, mouseY);

		//描画位置を計算
		int addX = 24;
		int tipX = this.getWidth() + 106 + addX;
		int tipY = this.getHeight() + 8;
		int xAxis = mouseX - this.getWidth();
		int yAxis = mouseY - this.getHeight();

		if (this.isRender(tipX, tipY, mouseX, mouseY, 65, 15)) {
			this.renderTooltip(pose, this.getText("range_level").withStyle(GOLD), xAxis, yAxis);
		}

		//描画位置を計算
		tipX = this.getWidth() + 11 + addX;
		tipY = this.getHeight() + 29;
		ItemStack magicBook = this.tile.getBookItem();
		if (this.tile.getInputItem().isEmpty() || magicBook.isEmpty() || this.tile.isCraft) { return; }

		//描画位置を計算
		tipX = this.getWidth() + 182 + addX;
		tipY = this.getHeight() + 3;
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
		tipY = this.getHeight() + 26;

		for (int id = 0; id < 4; id++) {

			this.enchaView[id] = false;

			if (this.isRender(tipX, tipY, mouseX, mouseY, 98, 20)) {

				int cost = this.tile.getEnchantCost(id + this.startIndex, (SMBook) magicBook.getItem());
				if (cost <= 0) { break; }

				String tip = String.format("%,d", cost);
				this.renderTooltip(pose, this.getTipArray(this.getText("needmf"), this.getLabel(tip, WHITE)).withStyle(GOLD), xAxis - 80, yAxis - 6);
				this.enchaView[id] = true;
			}

			tipY += 21;
		}
	}

	public boolean mouseClicked(double guiX, double guiY, int mouseButton) {

		this.scrolling = false;
		int addX = 24;
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
		double dX = guiX - (double) (x + 182);
		double dY = guiY - (double) (y + 3);

		// 各エンチャントの当たり判定チェック
		if (dX >= 0D && dX <= 7 && dY >= 0D && dY < 10) {
			this.clickButton(0);
		}

		// 各エンチャントの当たり判定チェック
		else if (dX >= 0D && dX <= 7 && dY >= 10 && dY < 20) {
			this.clickButton(1);
		}

		dX = guiX - (double) (x + 101) + addX;
		for (int id = 0; id < 4; ++id) {

			dY = guiY - (double) (y + 26 + 21D * id);

			// 各エンチャントの当たり判定チェック
			if (dX >= 0D && dX <= 95 && dY >= 0D && dY < 21D) {
				this.clickButton(id + this.startIndex + 2);
				break;
			}
		}

		return super.mouseClicked(guiX, guiY, mouseButton);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {
		List<Enchantment> enchaList = this.menu.tile.getEnchaList();
		int size = enchaList.size();
		if (!this.scrolling || !this.scrollbarActive()) { return super.mouseDragged(mouseX, mouseY, mouseButton, dragX, dragY); }

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
		if (!this.scrollbarActive()) { return super.mouseScrolled(mouseX, mouseY, scrollDelta); }

		List<Enchantment> enchaList = this.menu.tile.getEnchaList();
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

	public void setTip (List<Component> tipList, ItemStack stack) {
		if (stack.is(Items.BOOK)) {
			tipList.add(this.getText("can_encha").withStyle(GREEN));
		}

		else {
			tipList = Arrays.<Component> asList(this.getText("slot_need").withStyle(GREEN), stack.getDisplayName());
		}
	}

	private boolean scrollbarActive() {
		return this.tile.getEnchaList().size() > 4 && !this.tile.isCraft && !this.tile.getBookItem().isEmpty();
	}

	@Override
	protected ResourceLocation getTEX() {
		return TEX;
	}
}
