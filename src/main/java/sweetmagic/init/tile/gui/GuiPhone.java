package sweetmagic.init.tile.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import sweetmagic.SweetMagicCore;
import sweetmagic.handler.PacketHandler;
import sweetmagic.init.ItemInit;
import sweetmagic.init.capability.icap.ICookingStatus;
import sweetmagic.init.tile.gui.util.SMButton;
import sweetmagic.init.tile.menu.PhoneMenu;
import sweetmagic.init.tile.sm.TileNotePC;
import sweetmagic.init.tile.sm.TileNotePC.TradeInfo;
import sweetmagic.packet.PhonePKT;

public class GuiPhone extends GuiSMBase<PhoneMenu> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_phone.png");
	private static final ItemStack SUGAR = new ItemStack(ItemInit.sugarbell);
	private static final ItemStack AETHER = new ItemStack(ItemInit.aether_crystal);
	private static final ItemStack ICE = new ItemStack(ItemInit.unmeltable_ice);
	private static final String FORMAT_REGEX = "§.";
	private ICookingStatus cook;
	private List<PlayerInfo> infoList;
	private Map<UUID, Player> playerMap = new HashMap<>();
	private List<UUID> playerList = new ArrayList<>();
	private int size = 0;
	private int maxSize = 0;

	private int tabId = 0;
	private boolean tabView[] = new boolean[5];

	private boolean lootView[] = new boolean[4];
	private float scrollOffset = 0F;
	private int startIndex = 0;
	private boolean scrolling = false;
	private int selectID = -1;
	private boolean click = false;

	public GuiPhone(PhoneMenu menu, Inventory pInv, Component title) {
		super(menu, pInv, title);
		this.setGuiSize(172, 190);
		this.cook = this.menu.cook;
		this.infoList = this.menu.infoList;
		this.maxSize = this.menu.isSingle ? this.menu.tradeList.get(0).size() : this.infoList.size();
		this.size = Math.min(4, this.maxSize);

		if(this.player instanceof LocalPlayer local) {

			for (Entity entity : local.clientLevel.entitiesForRendering()) {
				if(!(entity instanceof Player player) || player.getUUID() == this.player.getUUID()) { continue; }
				this.playerMap.put(player.getUUID(), player);
				this.playerList.add(player.getUUID());
			}
		}

		this.addButtonMap(0, new SMButton(TEX, 83, 57, 176, 57, 22, 12));
		this.addButtonMap(1, new SMButton(TEX, 105, 57, 176, 69, 28, 12));
		this.addButtonMap(2, new SMButton(TEX, 133, 57, 176, 81, 33, 12));
		this.addButtonMap(3, new SMButton(TEX, 83, 69, 176, 57, 22, 12));
		this.addButtonMap(4, new SMButton(TEX, 105, 69, 176, 69, 28, 12));
		this.addButtonMap(5, new SMButton(TEX, 133, 69, 176, 81, 33, 12));
		this.addButtonMap(6, new SMButton(TEX, 141, 35, 176, 35, 26, 18));
	}

	@Override
	protected void renderBg(PoseStack pose, float parTick, int mouseX, int mouseY) {
		super.renderBg(pose, parTick, mouseX, mouseY);
		int x = this.getWidth();
		int y = this.getHeight();

		for (int i = 0; i < this.size; i++) {
			this.blit(pose, x + 5, y + 13 + i * 17, 176, this.lootView[i] ? 0 : 225, 67, 17);
		}

		if(this.menu.isSingle) {
			this.renderSingleBg(pose, parTick, x, y, mouseX, mouseY);
			return;
		}

		pose.pushPose();
		pose.scale(0.5F, 0.5F, 0.5F);

		for (int i = 0; i < this.size; i++) {
			PlayerInfo info = this.infoList.get(i + this.startIndex);
			RenderSystem.setShaderTexture(0, info.getSkinLocation());
			this.blit(pose, (x + 6) * 2, (y + 14 + i * 32) * 2, 32, 32, 32, 32);
			this.blit(pose, (x + 6) * 2, (y + 14 + i * 32) * 2, 160, 32, 32, 32);
		}

		if(this.selectID != -1 && this.tabId != 2) {
			RenderSystem.setShaderTexture(0, this.infoList.get(this.selectID).getSkinLocation());
			this.blit(pose, (x + 84) * 2, (y + 36) * 2, 32, 32, 32, 32);
			this.blit(pose, (x + 84) * 2, (y + 36) * 2, 160, 32, 32, 32);
		}

		pose.popPose();

		pose.pushPose();
		RenderSystem.setShaderTexture(0, TEX);

		if (this.tabId == 2) {
			ItemStack stack = this.menu.inventory.getInv().getStackInSlot(0);

			if(!stack.isEmpty()) {
				String sp = this.format(TileNotePC.getGlobalValue(1F, stack));
				this.itemRenderer.renderAndDecorateFakeItem(stack, x + 84, y + 36);
				this.font.draw(pose, this.getText("trade_sell").withStyle(GREEN), x + 102, y + 35, 0x333333);
				MutableComponent tip = this.getLabel(sp + "sp", GREEN);
				this.drawFont(pose, tip, 38F, x + 102, y + 44, 0x333333, false);
			}
		}

		RenderSystem.setShaderTexture(0, TEX);

		// タブ選択描画
		for(int i = 0; i < 3; i++) {
			int addX = i != this.tabId ? 18 : 0;
			this.blit(pose, x + 59 + i * 18, y + 104, 176 + addX, 104, 18, 1);

			if(this.tabView[i]) {
				this.blit(pose, x + 59 + i * 18, y + 87, 176, 113, 18, 17);
			}
		}

		this.blit(pose, x + 63, y + 89, 200, 114, 13, 14);
		this.blit(pose, x + 81, y + 89, 200, 114, 13, 14);
		this.itemRenderer.renderAndDecorateFakeItem(SUGAR, x + 97, y + 88);

		if(this.selectID != -1 && this.tabId != 2) {

			float maxSize = 38F;
			Player player = this.playerMap.get(this.infoList.get(this.selectID).getProfile().getId());
			if(player == null) { return; }

			MutableComponent tip = this.getLabel(player.getName().getString().replaceAll(FORMAT_REGEX, ""), WHITE);
			this.drawFont(pose, tip, maxSize, x + 102, y + 37, 0x333333, false);

			int sp = this.menu.sp;
			MutableComponent tip2 = this.getLabel(this.format(sp) + "sp", sp >= 0 ? GREEN : RED);
			this.drawFont(pose, tip2, maxSize, x + 102, y + 45, 0x333333, false);
		}

		for (int i = 0; i < this.size; i++) {
			String name = this.infoList.get(i + this.startIndex).getProfile().getName().replaceAll(FORMAT_REGEX, "");
			this.drawFont(pose, this.getLabel(name, WHITE), 48F, x + 25, y + 20, 0xEEEEEE, true);
		}

		this.font.draw(pose, this.getText("trade_level", this.format(this.cook.getTradeLevel())), x + 85, y + 14, 0x333333);

		MutableComponent tip = this.getText("possess_sp", ": " + this.format(this.cook.getTradeSP()) + "sp");
		this.drawFont(pose, tip, 80F, x + 85, y + 22, 0x333333, false);

		this.font.drawShadow(pose, this.getLabel("+10", WHITE), x + 85, y + 60, 0xEEEEEE);
		this.font.drawShadow(pose, this.getLabel("+100", WHITE), x + 107, y + 60, 0xEEEEEE);
		this.font.drawShadow(pose, this.getLabel("+1000", WHITE), x + 135, y + 60, 0xEEEEEE);
		this.font.drawShadow(pose, this.getLabel("-10", WHITE), x + 85, y + 72, 0xEEEEEE);
		this.font.drawShadow(pose, this.getLabel("-100", WHITE), x + 107, y + 72, 0xEEEEEE);
		this.font.drawShadow(pose, this.getLabel("-1000", WHITE), x + 135, y + 72, 0xEEEEEE);

		this.font.draw(pose, this.getLabel("P"), x + 61, y + 97, 0xA8A8A8);
		this.font.draw(pose, this.getLabel("I"), x + 80, y + 97, 0xA8A8A8);

		String send = this.tabId != 2 ? "phone_send" : "trade_sale";
		this.font.drawShadow(pose, this.getText(send), x + 145, y + 40, 0xEEEEEE);
		pose.popPose();
	}

	protected void renderSingleBg(PoseStack pose, float parTick, int x, int y, int mouseX, int mouseY) {

		// タブ選択描画
		for(int i = 0; i < 3; i++) {
			int addX = i != this.tabId ? 18 : 0;
			this.blit(pose, x + 59 + i * 18, y + 104, 176 + addX, 104, 18, 1);

			if(this.tabView[i]) {
				this.blit(pose, x + 59 + i * 18, y + 87, 176, 113, 18, 17);
			}
		}

		if (this.tabId != 2) {
			List<TradeInfo> tradList = this.menu.tradeList.get(this.tabId);

			// ラインナップアイテム
			for (int i = 0; i < this.size; i++) {
				TradeInfo info = tradList.get(i + this.startIndex);

				MutableComponent tip = this.getLabel(this.format(info.price()) + "sp", GREEN);
				this.drawFont(pose, tip, 45F, x + 25, y + 20 + i * 17, 0xEEEEEE, false);
				this.itemRenderer.renderAndDecorateFakeItem(info.stack(), x + 6, y + 14 + i * 17);
			}

			if(this.selectID != -1) {

				TradeInfo info = tradList.get(this.selectID);
				boolean isOver = this.cook.getTradeSP() >= info.price();
				this.itemRenderer.renderAndDecorateFakeItem(info.stack(), x + 84, y + 36);
				MutableComponent tip = this.getLabel(this.format(info.price() * this.menu.sp) + "sp", isOver ? GREEN : RED);
				this.drawFont(pose, tip, 38F, x + 102, y + 43, 0x333333, false);

				if (this.menu.sp > 1) {
					pose.pushPose();
					pose.translate(0D, 0D, 200D);
					MutableComponent tip2 = this.getLabel(this.menu.sp);
					int nameSize = this.font.width(tip2);
					this.font.drawShadow(pose, tip2, x + 101 - nameSize, y + 45, 0xEEEEEE);
					pose.popPose();
				}
			}
		}

		else {

			ItemStack stack = this.menu.inventory.getInv().getStackInSlot(0);

			if(!stack.isEmpty()) {
				String sp = this.format(TileNotePC.getGlobalValue(1F, stack));
				this.itemRenderer.renderAndDecorateFakeItem(stack, x + 84, y + 36);
				this.font.draw(pose, this.getText("trade_sell").withStyle(GREEN), x + 102, y + 35, 0x333333);
				MutableComponent tip = this.getLabel(sp + "sp", GREEN);
				this.drawFont(pose, tip, 38F, x + 102, y + 44, 0x333333, false);
			}
		}

		this.itemRenderer.renderAndDecorateFakeItem(AETHER, x + 61, y + 88);
		this.itemRenderer.renderAndDecorateFakeItem(ICE, x + 79, y + 88);
		this.itemRenderer.renderAndDecorateFakeItem(SUGAR, x + 97, y + 88);
		this.font.draw(pose, this.getText("trade_level", this.format(this.cook.getTradeLevel())), x + 85, y + 14, 0x333333);

		pose.pushPose();
		MutableComponent tip = this.getText("possess_sp", ": " + this.format(this.cook.getTradeSP()) + "sp");
		int spSize = this.font.width(tip.getString());
		pose.scale(spSize < 80F ? 1F : 80F / spSize, 1F, 1F);
		int addX = Math.max(0, spSize - 80);
		this.font.draw(pose, tip, x + 85 + addX * 3, y + 22, 0x333333);
		pose.popPose();

		this.font.drawShadow(pose, this.getLabel("+1", WHITE), x + 85, y + 60, 0xEEEEEE);
		this.font.drawShadow(pose, this.getLabel("+10", WHITE), x + 107, y + 60, 0xEEEEEE);
		this.font.drawShadow(pose, this.getLabel("+64", WHITE), x + 135, y + 60, 0xEEEEEE);
		this.font.drawShadow(pose, this.getLabel("-1", WHITE), x + 85, y + 72, 0xEEEEEE);
		this.font.drawShadow(pose, this.getLabel("-10", WHITE), x + 107, y + 72, 0xEEEEEE);
		this.font.drawShadow(pose, this.getLabel("-64", WHITE), x + 135, y + 72, 0xEEEEEE);

		String send = this.tabId != 2 ? "trade_buy" : "trade_sale";
		this.font.drawShadow(pose, this.getText(send), x + 145, y + 40, 0xEEEEEE);
	}

	@Override
	protected void renderLabels(PoseStack pose, int mouseX, int mouseY) {
		super.renderLabels(pose, mouseX, mouseY);

		if(this.menu.isSingle) {
			this.renderSingleLabels(pose, mouseX, mouseY);
			return;
		}

		int xAxis = mouseX - this.getWidth();
		int yAxis = mouseY - this.getHeight();
		int tipX = this.getWidth() + 5;
		int tipY = this.getHeight() + 13;

		// ラインナップツールチップ
		for (int id = 0; id < this.size; id++) {

			this.lootView[id] = false;

			if (this.isRender(tipX, tipY, mouseX, mouseY, 67, 16)) {
				this.lootView[id] = true;
			}

			tipY += 17;
		}

		tipX = this.getWidth() + 59;
		tipY = this.getHeight() + 87;

		// タブツールチップ
		for(int i = 0; i < 3; i++) {

			this.tabView[i] = false;

			if (this.isRender(tipX, tipY, mouseX, mouseY, 18, 18)) {
				this.tabView[i] = true;
				this.renderTooltip(pose, this.getText("phone_tab" + i), xAxis, yAxis);
			}

			tipX += 18;
		}
	}

	protected void renderSingleLabels(PoseStack pose, int mouseX, int mouseY) {

		int xAxis = mouseX - this.getWidth();
		int yAxis = mouseY - this.getHeight();
		int tipX = this.getWidth() + 5;
		int tipY = this.getHeight() + 13;

		if(this.tabId != 2) {

			List<TradeInfo> tradList = this.menu.tradeList.get(this.tabId);

			// ラインナップツールチップ
			for (int id = 0; id < this.size; id++) {

				this.lootView[id] = false;

				if (this.isRender(tipX, tipY, mouseX, mouseY, 67, 16)) {
					this.renderTooltip(pose, tradList.get(id + this.startIndex).stack(), xAxis, yAxis);
					this.lootView[id] = true;
				}

				tipY += 17;
			}

			tipX = this.getWidth() + 83;
			tipY = this.getHeight() + 35;

			// 購入アイテムツールチップ
			if (this.selectID != -1 && this.isRender(tipX, tipY, mouseX, mouseY, 57, 18)) {
				TradeInfo info = this.menu.tradeList.get(this.tabId).get(this.selectID);
				ItemStack stack = info.stack().copy();
				this.renderTooltip(pose, stack, xAxis, yAxis);
			}
		}

		tipX = this.getWidth() + 59;
		tipY = this.getHeight() + 87;

		// タブツールチップ
		for(int i = 0; i < 3; i++) {

			this.tabView[i] = false;

			if (this.isRender(tipX, tipY, mouseX, mouseY, 18, 18)) {
				this.tabView[i] = true;

				String tip = "";

				switch(i) {
				case 0:
					tip = "trade_magic";
					break;
				case 1:
					tip = "trade_drop";
					break;
				case 2:
					tip = "phone_tab2";
					break;
				}

				this.renderTooltip(pose, this.getText(tip), xAxis, yAxis);
			}

			tipX += 18;
		}
	}

	public void clickSMButton(int id) {
		if(id == 6) {

			if(this.selectID == -1 && this.tabId != 2) {
				id = -1;
			}

			else {
				id += this.tabId;
			}
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
		if (this.maxSize > 4 && guiX >= aX && guiX < aX + w && guiY >= aY && guiY < aY + h) {
			this.click = true;
		}

		aX = this.leftPos + 5;
		aY = this.topPos + 13;
		w = 67;
		h = 16;

		for (int id = 0; id < this.size; id++) {

			if (guiX >= aX && guiX < aX + w && guiY >= aY && guiY < aY + h) {

				if(this.selectID == id + this.startIndex) {
					this.selectID = -1;
					PacketHandler.sendToServer(new PhonePKT(this.player.getUUID(), -1, false));
				}

				else {
					this.selectID = id + this.startIndex;

					if(this.menu.isSingle) {
						if(this.tabId != 2) {
							int selectId = this.selectID + this.tabId * 8;
							PacketHandler.sendToServer(new PhonePKT(this.player.getUUID(), selectId, false));
						}
					}

					else {
						if(this.playerList.size() <= this.selectID) { return super.mouseClicked(guiX, guiY, mouseButton); }
						PacketHandler.sendToServer(new PhonePKT(this.playerMap.get(this.playerList.get(this.selectID)).getUUID(), -1, true));
					}
				}

				this.clickButton(-1);
				break;
			}

			aY += 17;
		}

		aX = this.leftPos + 59;
		aY = this.topPos + 89;
		w = 18;
		h = 18;

		for(int i = 0; i < 3; i++) {

			if (guiX >= aX && guiX < aX + w && guiY >= aY && guiY < aY + h) {
				this.tabId = i;
				this.scrollOffset = 0F;
				this.startIndex = 0;
				this.clickButton(-1);

				if(this.menu.isSingle) {
					this.selectID = -1;
					PacketHandler.sendToServer(new PhonePKT(this.player.getUUID(), this.selectID, false));
				}

				break;
			}

			aX += 19;
		}

		return super.mouseClicked(guiX, guiY, mouseButton);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {
		if ((!this.scrolling && !this.click) || this.maxSize <= 4) { return super.mouseDragged(mouseX, mouseY, mouseButton, dragX, dragY); }

		int i = this.topPos + 73;
		int j = i + 77;
		int offscreenRows = this.maxSize - 4;
		this.scrollOffset = ((float) mouseY - (float) i - 15F) / ((float) (j - i) - 15F);
		this.scrollOffset = Mth.clamp(this.scrollOffset, 0F, 1F);
		this.startIndex = (int) ((double) (this.scrollOffset * (float) offscreenRows) + 0.5D);
		return super.mouseDragged(mouseX, mouseY, mouseButton, dragX, dragY);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double scrollDelta) {
		if(this.maxSize <= 4) { return super.mouseScrolled(mouseX, mouseY, scrollDelta); }

		int offscreenRows = this.maxSize - 4;
		this.scrollOffset = (float) ((double) this.scrollOffset - scrollDelta / (double) offscreenRows);
		this.scrollOffset = Mth.clamp(this.scrollOffset, 0F, 1F);
		this.startIndex = (int) ((double) (this.scrollOffset * (float) offscreenRows) + 0.5D);
		return super.mouseScrolled(mouseX, mouseY, scrollDelta);
	}

	protected ResourceLocation getTEX() {
		return TEX;
	}
}
