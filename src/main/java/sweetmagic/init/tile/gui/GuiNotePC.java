package sweetmagic.init.tile.gui;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit;
import sweetmagic.init.ItemInit;
import sweetmagic.init.capability.icap.ICookingStatus;
import sweetmagic.init.tile.gui.util.SMButton;
import sweetmagic.init.tile.menu.NotePCMenu;
import sweetmagic.init.tile.sm.TileNotePC;
import sweetmagic.init.tile.sm.TileNotePC.TradeInfo;

public class GuiNotePC extends GuiSMBase<NotePCMenu> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_notepc.png");
	private final TileNotePC tile;
	private List<List<TradeInfo>> tradeList = new ArrayList<>();
	private List<Integer> intList = new ArrayList<>();
	private long date;
	private ICookingStatus cook;
	private TradeInfo trade = null;

	private int tabId = 0;
	private boolean tabView[] = new boolean[6];

	private boolean lootView[] = new boolean[5];
	private float scrollOffset = 0F;
	private int startIndex = 0;
	private boolean scrolling = false;
	private boolean click = false;
	private int selectID = -1;

	public GuiNotePC(NotePCMenu menu, Inventory pInv, Component title) {
		super(menu, pInv, title);
		this.setGuiSize(184, 243);
		this.tile = menu.tile;
		this.addButtonMap(0, new SMButton(TEX, 120, 94, 188, 94, 15, 12));
		this.addButtonMap(1, new SMButton(TEX, 135, 94, 189, 106, 22, 12));
		this.addButtonMap(2, new SMButton(TEX, 157, 94, 189, 106, 22, 12));
		this.addButtonMap(3, new SMButton(TEX, 120, 106, 188, 94, 15, 12));
		this.addButtonMap(4, new SMButton(TEX, 135, 106, 189, 106, 22, 12));
		this.addButtonMap(5, new SMButton(TEX, 157, 106, 189, 106, 22, 12));

		this.addButtonMap(6, new SMButton(TEX, 3, 33, 188, 0, 26, 13));
		this.addButtonMap(7, new SMButton(TEX, 153, 72, 188, 72, 26, 18));

		this.tile.setOwner(this.player);
		this.cook = this.tile.getCook(this.player);
		this.clearInfo(this.player);
	}

	@Override
	protected void renderBg(PoseStack pose, float parTick, int mouseX, int mouseY) {
		super.renderBg(pose, parTick, mouseX, mouseY);
		int x = this.getWidth();
		int y = this.getHeight();
		if(this.tradeList.isEmpty()) { return; }

		List<TradeInfo> tradeInfoList = this.tradeList.get(this.tabId);
		int size = Math.min(5, tradeInfoList.size());
		int intSize = this.intList.size();

		// スクロールバーの表示
		int h = (int) (61F * this.scrollOffset);
		this.blit(pose, x + 73, y + 77 + h, 188 + (this.scrolling ? 5 : 0), 143, 5, 15);

		// タブ選択描画
		for(int i = 0; i < intSize; i++) {
			int addX = i != this.tabId ? 18 : 0;
			this.blit(pose, x + 1 + i * 18, y + 50, 188 + addX, 50, 18, 18);

			if(this.tabView[i]) {
				this.blit(pose, x + 1 + i * 18, y + 50, 188 + 36, 50, 18, 17);
			}
		}

		// ラインナップ選択描画
		for(int i = 0; i < size; i++) {
			this.blit(pose, x + 5, y + 72 + i * 17, 188, (this.lootView[i] ? 122 : 225), 67, 17);
		}

		ItemStack input = this.tile.getInputItem();
		if(!input.isEmpty()) {
			this.blit(pose, x + 34, y + 39, 1, 249, this.cook.getExpProgress(144, (int) (this.tile.getValue(this.cook, input) * 0.05F)), 7);
		}

		else if(this.trade != null) {
			int needSP = this.trade.price() * this.tile.buyCount;
			this.blit(pose, x + 34, y + 39, 1, 249, this.cook.getExpProgress(144, (int) (needSP * 0.25F)), 7);
		}

		this.blit(pose, x + 34, y + 39, 1, 243, this.cook.getExpProgress(144), 7);

		// タブアイコン
		for(int i = 0; i < intSize; i++) {
			this.itemRenderer.renderAndDecorateFakeItem(this.getTradeIcon(this.intList.get(i)), x + 1 + i * 18, y + 51);
		}

		// 選択アイテム
		if(this.trade != null) {
			int buyCount = this.tile.buyCount;
			int needSP = this.trade.price() * buyCount;
			int hasSP = this.cook.getTradeSP();
			boolean isSa = hasSP >= needSP;

			MutableComponent spTip = this.getLabel(this.format(needSP) + "sp", isSa ? GREEN : RED);
			this.drawFont(pose, spTip, 50F, x + 102, y + 81, 0xEEEEEE, true);
			this.itemRenderer.renderAndDecorateFakeItem(this.trade.stack(), x + 84, y + 73);

			if (buyCount > 0) {
				pose.pushPose();
				pose.translate(0D, 0D, 200D);
				MutableComponent tip = this.getLabel("×" + this.trade.stack().getCount() * buyCount);
				int nameSize = this.font.width(tip);
				this.drawFont(pose, tip, 50F, x + 101, y + 73, 0xEEEEEE, true);
				pose.popPose();
			}
		}

		// ラインナップアイテム
		for (int i = 0; i < size; i++) {
			TradeInfo info = tradeInfoList.get(i + this.startIndex);
			MutableComponent tip = this.getLabel(this.format(info.price()) + "sp", GREEN);
			this.drawFont(pose, tip, 45F, x + 25, y + 79 + i * 17, 0xEEEEEE, true);
			this.itemRenderer.renderAndDecorateFakeItem(info.stack(), x + 6, y + 73 + i * 17);
		}

		// 売却値
		ItemStack stack = this.tile.getInputItem();
		if(!stack.isEmpty()) {
			String price = this.format(this.tile.getValue(this.cook, stack)) + "sp";
			this.font.drawShadow(pose, this.getText("trade_sell", price).withStyle(GREEN), x + 36, y + 15, 0xEEEEEE);
		}

		this.font.drawShadow(pose, this.getText("trade_rate", this.formatPar(this.cook.getRate() * 100F)).withStyle(GREEN), x + 36, y + 26, 0xEEEEEE);
		this.font.drawShadow(pose, this.getText("possess_sp").withStyle(GOLD), x + 110, y + 51F, 0xEEEEEE);
		this.font.draw(pose, this.getLabel(this.format(this.cook.getTradeSP()) + "sp"), x + 114, y + 59.5F, 0x333333);

		long nextTime = this.tile.getNextTime() / 20;
		long nextMin = nextTime / 60;
		nextTime = nextTime % 60;
		this.font.drawShadow(pose, this.getText("trade_update").withStyle(WHITE), x + 131, y + 15, 0xEEEEEE);
		this.font.drawShadow(pose, this.getText("trade_time", nextMin, nextTime).withStyle(GOLD), x + 131, y + 26F, 0xEEEEEE);

		// 日付更新時にラインナップ更新
		if(this.date != -1 && this.date != this.tile.randDate) {
			this.clearInfo(this.player);
		}

		this.font.drawShadow(pose, this.getText("trade_sale").withStyle(WHITE), x + 6, y + 35, 0xEEEEEE);
		this.font.drawShadow(pose, this.getText("trade_buy").withStyle(WHITE), x + 155, y + 77, 0xEEEEEE);

		this.font.drawShadow(pose, this.getLabel("+1", WHITE), x + 122, y + 97, 0xEEEEEE);
		this.font.drawShadow(pose, this.getLabel("+10", WHITE), x + 136, y + 97, 0xEEEEEE);
		this.font.drawShadow(pose, this.getLabel("+64", WHITE), x + 158, y + 97, 0xEEEEEE);
		this.font.drawShadow(pose, this.getLabel("-1", WHITE), x + 122, y + 109, 0xEEEEEE);
		this.font.drawShadow(pose, this.getLabel("-10", WHITE), x + 136, y + 109, 0xEEEEEE);
		this.font.drawShadow(pose, this.getLabel("-64", WHITE), x + 158, y + 109, 0xEEEEEE);
	}

	@Override
	protected void renderLabels(PoseStack pose, int mouseX, int mouseY) {
		super.renderLabels(pose, mouseX, mouseY);

		int xAxis = mouseX - this.getWidth();
		int yAxis = mouseY - this.getHeight();
		int tipX = this.getWidth() + 5;
		int tipY = this.getHeight() + 72;
		if(this.tradeList.isEmpty()) { return; }

		List<TradeInfo> tradeInfoList = this.tradeList.get(this.tabId);
		int size = Math.min(5, tradeInfoList.size());

		// ラインナップツールチップ
		for (int id = 0; id < size; id++) {

			this.lootView[id] = false;

			if (this.isRender(tipX, tipY, mouseX, mouseY, 67, 16)) {
				this.renderTooltip(pose, tradeInfoList.get(id + this.startIndex).stack(), xAxis, yAxis);
				this.lootView[id] = true;
			}

			tipY += 17;
		}

		tipX = this.getWidth() + 83;
		tipY = this.getHeight() + 72;

		// 購入アイテムツールチップ
		if (this.trade != null && this.isRender(tipX, tipY, mouseX, mouseY, 71, 18)) {
			ItemStack stack = this.trade.stack().copy();
			stack.setCount(this.tile.buyCount);
			this.renderTooltip(pose, stack, xAxis, yAxis);
		}

		tipX = this.getWidth();
		tipY = this.getHeight() + 50;

		// タブツールチップ
		for(int i = 0; i < this.intList.size(); i++) {

			this.tabView[i] = false;

			if (this.isRender(tipX, tipY, mouseX, mouseY, 17, 18)) {
				this.tabView[i] = true;
				this.renderTooltip(pose, this.getText(this.getTradeTile(this.intList.get(i))), xAxis, yAxis);
			}

			tipX += 18;
		}

		tipX = this.getWidth() + 33;
		tipY = this.getHeight() + 39;

		if (this.isRender(tipX, tipY, mouseX, mouseY, 144, 7)) {

			int level = this.cook.getTradeLevel();
			String needExp = this.format(this.cook.needExp());

			List<Component> tipList = new ArrayList<>();
			tipList.add(this.getText("trade_level", level).withStyle(GREEN));
			tipList.add(this.getText("trade_exp", needExp).withStyle(GREEN));
			tipList.add(this.getLabel("===================="));
			tipList.add(this.getText("trade_tip0").withStyle(GOLD));
			tipList.add(this.getText("trade_tip1").withStyle(GOLD));
			tipList.add(this.getText("trade_tip2").withStyle(GOLD));
			this.renderComponentTooltip(pose, tipList, xAxis, yAxis);
		}

		tipX = this.getWidth() + 73;
		tipY = (int) (this.getHeight() + 77 + this.scrollOffset * 60F);
		this.scrolling = false;

		if (this.isRender(tipX, tipY, mouseX, mouseY, 5, 15)) {
			this.scrolling = true;
		}
	}

	public void clickSMButton(int id) {
		if(id == 7) {
			id += this.selectID + this.tabId * (10 + this.cook.getTradeLevel() * 2);
		}

		this.clickButton(id);
	}

	public boolean mouseClicked(double guiX, double guiY, int mouseButton) {

		int aX = this.leftPos + 73;
		int aY = (int) (this.topPos + 77 + (this.scrollOffset * 60F));
		int w = 5;
		int h = 15;
		this.click = false;

		// スクロールバーの当たり判定チェック
		if (guiX >= aX && guiX < aX + w && guiY >= aY && guiY < aY + h) {
			this.click = true;
		}

		aX = this.leftPos;
		aY = this.topPos + 50;
		w = 18;
		h = 18;

		for(int i = 0; i < this.intList.size(); i++) {

			if (guiX >= aX && guiX < aX + w && guiY >= aY && guiY < aY + h) {
				this.tabId = i;
				this.scrollOffset = 0F;
				this.startIndex = 0;
				this.clickButton(-1);
				break;
			}

			aX += 19;
		}

		aX = this.leftPos + 5;
		aY = this.topPos + 72;
		w = 67;
		h = 16;
		List<TradeInfo> tradeInfoList = this.tradeList.get(this.tabId);
		int size = Math.min(5, tradeInfoList.size());

		for (int id = 0; id < size; id++) {

			if (guiX >= aX && guiX < aX + w && guiY >= aY && guiY < aY + h) {

				if(this.selectID == id + this.startIndex) {
					this.selectID = -1;
					this.trade = null;
				}

				else {
					this.selectID = id + this.startIndex;
					this.trade = this.tradeList.get(this.tabId).get(this.selectID);
				}

				this.clickButton(-1);
				break;
			}

			aY += 17;
		}

		return super.mouseClicked(guiX, guiY, mouseButton);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {
		if (!this.scrolling && !this.click) { return super.mouseDragged(mouseX, mouseY, mouseButton, dragX, dragY); }

		int i = this.topPos + 73;
		int j = i + 77;
		int offscreenRows = this.tradeList.get(this.tabId).size() - 5;
		this.scrollOffset = ((float) mouseY - (float) i - 15F) / ((float) (j - i) - 15F);
		this.scrollOffset = Mth.clamp(this.scrollOffset, 0F, 1F);
		this.startIndex = (int) ((double) (this.scrollOffset * (float) offscreenRows) + 0.5D);
		return super.mouseDragged(mouseX, mouseY, mouseButton, dragX, dragY);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollDelta) {
		int offscreenRows = this.tradeList.get(this.tabId).size() - 5;
		this.scrollOffset = (float) ((double) this.scrollOffset - scrollDelta / (double) offscreenRows);
		this.scrollOffset = Mth.clamp(this.scrollOffset, 0F, 1F);
		this.startIndex = (int) ((double) (this.scrollOffset * (float) offscreenRows) + 0.5D);
		return super.mouseScrolled(mouseX, mouseY, scrollDelta);
	}

	public void clearInfo(Player player) {
		this.tradeList = this.tile.getTrade(player);
		this.date = this.tile.randDate;
		int tradeLevel = this.cook.getTradeLevel() + 1;
		this.intList.clear();
		this.tabId = 0;
		this.selectID = -1;
		this.scrollOffset = 0F;
		this.startIndex = 0;
		this.scrolling = false;

		for(int i = 0; i < tradeLevel; i++)
			this.intList.add(this.tile.intList.get(i));
	}

	public ItemStack getTradeIcon(int i) {
		switch(i) {
		case 1: return new ItemStack(ItemInit.aether_crystal);
		case 2: return new ItemStack(BlockInit.wood_chest);
		case 3: return new ItemStack(Items.ENCHANTED_BOOK);
		case 4: return new ItemStack(Items.AMETHYST_SHARD);
		case 5: return new ItemStack(ItemInit.flour);
		default: return new ItemStack(ItemInit.sugarbell);
		}
	}

	public String getTradeTile(int i) {
		switch(i) {
		case 1: return "trade_magic";
		case 2: return "trade_furniture";
		case 3: return "trade_enchant";
		case 4: return "trade_vanilla";
		case 5: return "trade_seasoning";
		default: return "trade_seed";
		}
	}

	protected ResourceLocation getTEX() {
		return TEX;
	}
}
