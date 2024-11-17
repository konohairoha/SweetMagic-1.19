package sweetmagic.init.tile.gui;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.SweetMagicAPI;
import sweetmagic.handler.PacketHandler;
import sweetmagic.init.tile.gui.util.SMButton;
import sweetmagic.init.tile.gui.util.SMRenderTex;
import sweetmagic.init.tile.gui.util.SMRenderTex.MFRenderGage;
import sweetmagic.init.tile.menu.MFBottlerMenu;
import sweetmagic.init.tile.sm.TileMFBottler;
import sweetmagic.packet.BottlerPKT;

public class GuiMFBottler extends GuiSMBase<MFBottlerMenu> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_mf_bottler.png");
	private final TileMFBottler tile;
	private boolean enchaView[] = new boolean[4];
	private boolean craftView = false;
	private int selectId = -1;

	public GuiMFBottler(MFBottlerMenu menu, Inventory pInv, Component title) {
		super(menu, pInv, title);
		this.setGuiWidth(183);
		this.setGuiHeight(214);
		this.tile = menu.tile;

		if (this.tile.selectId != -1) {
			this.selectId = this.tile.selectId;
		}

		this.getButtonMap().put(0, new SMButton(TEX, 19, 16, 198, 229, 20, 11));
		this.getButtonMap().put(1, new SMButton(TEX, 40, 16, 198, 229, 20, 11));
		this.getButtonMap().put(2, new SMButton(TEX, 19, 50, 198, 229, 20, 11));
		this.getButtonMap().put(3, new SMButton(TEX, 40, 50, 198, 229, 20, 11));

		this.getRenderTexList().add(new SMRenderTex(TEX, 5, 7, 0, 0, 11, 77, new MFRenderGage(menu.tile, 207, 7, 11, 76, 76, true)));
	}

	@Override
	protected void renderBg(PoseStack pose, float parTick, int mouseX, int mouseY) {
		super.renderBg(pose, parTick, mouseX, mouseY);
		int x = this.getWidth();
		int y = this.getHeight();

		List<ItemStack> stackList = this.tile.getStackList();
		int maxItemList = Math.min(4, stackList.size());

		for (int i = 0; i < maxItemList; i++) {
			this.blit(pose, x + 64, y + 7 + i * 20, this.enchaView[i] ? 98 : 0, 216, 98, 20);
		}

		int addY = 0;

		if (this.craftView && this.selectId != -1) {
			addY += 24;
		}

		else if (this.selectId != -1) {
			addY += 12;
		}

		this.blit(pose, x + 23, y + 70, 198, 172 + addY, 32, 12);

		if (this.selectId > -1) {
			this.blit(pose, x + 64, y + 7 + this.selectId * 20, 0, 236, 98, 20);
		}

		this.font.drawShadow(pose, this.getLabel("+1"), x + 24, y + 18, 0xEEEEEE);
		this.font.drawShadow(pose, this.getLabel("-1"), x + 45, y + 18, 0xEEEEEE);
		this.font.drawShadow(pose, this.getLabel("+10"), x + 20, y + 52, 0xEEEEEE);
		this.font.drawShadow(pose, this.getLabel("-10"), x + 41, y + 52, 0xEEEEEE);

		MutableComponent com = this.getLabel("" + this.tile.setCount);
		this.font.drawShadow(pose, com, x + 55 - this.font.width(com.getString()), y + 34, 0xEEEEEE);

		for (int i = 0; i < maxItemList; i++) {
			MutableComponent tip = ((MutableComponent) stackList.get(i).getHoverName()).withStyle(GREEN);
			this.font.drawShadow(pose, tip, x + 81, y + 12 + i * 20, 0xEEEEEE);
			this.itemRenderer.renderAndDecorateFakeItem(stackList.get(i), x + 67, y + 8 + i * 19);
		}
	}


	@Override
	protected void renderLabels(PoseStack pose, int mouseX, int mouseY) {
		super.renderLabels(pose, mouseX, mouseY);

		int xAxis = mouseX - this.getWidth();
		int yAxis = mouseY - this.getHeight();
		int tipX = this.getWidth() + 64;
		int tipY = this.getHeight() + 6;
		List<ItemStack> stackList = this.tile.getStackList();
		int maxItemList = Math.min(4, stackList.size());

		for (int id = 0; id < maxItemList; id++) {

			this.enchaView[id] = false;

			if (this.isRendeer(tipX, tipY, mouseX, mouseY, 98, 19)) {
				int cost = SweetMagicAPI.getMF(stackList.get(id));
				String tip = String.format("%,d", cost);
	            this.renderTooltip(pose, this.getTipArray(this.getText("needmf"), this.getLabel(tip).withStyle(WHITE)).withStyle(GOLD), xAxis, yAxis);
	            this.enchaView[id] = true;
			}

            tipY += 20;
		}

		this.craftView = false;
		tipX = this.getWidth() + 23;
		tipY = this.getHeight() + 70;

		if (this.isRendeer(tipX, tipY, mouseX, mouseY, 32, 14)) {
			this.craftView = true;
			boolean isSelect = this.selectId != -1;

			List<Component> tipList = new ArrayList<>();
			tipList.add(this.getText(isSelect ? "caraft_start" : "select_recipe").withStyle(GREEN));

			if (isSelect) {
				String tipMF = String.format("%,d", SweetMagicAPI.getMF(stackList.get(this.selectId)) * this.tile.setCount);
				tipList.add(this.getTipArray(this.getText("needmf"), this.getLabel(tipMF).withStyle(WHITE)).withStyle(GOLD));
			}

            this.renderComponentTooltip(pose, tipList, xAxis, yAxis);
		}
	}

	public boolean mouseClicked(double guiX, double guiY, int mouseButton) {

		int x = this.getWidth();
		int y = this.getHeight();
		double dX = guiX - x - 64;
		List<ItemStack> stackList = this.tile.getStackList();
		int maxItemList = Math.min(4, stackList.size());

		for (int id = 0; id < maxItemList; id++) {

			double dY = guiY - y - 6 - id * 20;

			if (dX >= 0D && dX <= 98D && dY >= 0D && dY < 20D) {
				this.selectId = this.selectId == id ? -1 : id;
				this.menu.clickMenuButton(this.player, this.selectId == -1 ? 99: this.selectId + 4);
			}
		}

		dX = guiX - x - 23;
		double dY = guiY - y - 71;

		if (dX >= 0D && dX <= 32D && dY >= 0D && dY < 12D) {
			PacketHandler.sendToServer(new BottlerPKT(this.selectId, this.tile.getBlockPos()));
		}

		return super.mouseClicked(guiX, guiY, mouseButton);
	}

	protected ResourceLocation getTEX () {
		return TEX;
	}
}
