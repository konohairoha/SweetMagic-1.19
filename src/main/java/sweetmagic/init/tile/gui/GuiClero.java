package sweetmagic.init.tile.gui;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundRenameItemPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.util.ISMTip;
import sweetmagic.handler.PacketHandler;
import sweetmagic.init.tile.gui.util.SMButton;
import sweetmagic.init.tile.menu.CleroMenu;
import sweetmagic.packet.CleroPKT;

public class GuiClero extends GuiSMBase<CleroMenu> implements ISMTip {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_clerodendrum_petal.png");

	private Player player;			// プレイヤー
	private ItemStack stack; 		// 杖のアイテムスタック
	private EditBox name;

	public GuiClero(CleroMenu menu, Inventory pInv, Component title) {
		super(menu, pInv, title);
		this.setGuiWidth(173);
		this.setGuiHeight(107);
		this.player = pInv.player;
		this.stack = this.player.getMainHandItem();
		this.getButtonMap().put(0, new SMButton(TEX, 136, 6, 175, 6, 28, 13));
	}

	public boolean mouseClicked(double guiX, double guiY, int mouseButton) {

		int aX = this.leftPos + 136;
		int aY = this.topPos + 6;
		int w = 27;
		int h = 12;

		// スクロールバーの当たり判定チェック
		if (guiX >= aX && guiX < aX + w && guiY >= aY && guiY < aY + h) {
			PacketHandler.sendToServer(new CleroPKT(this.name.getValue()));
			this.removed();
			this.onClose();
		}

		return super.mouseClicked(guiX, guiY, mouseButton);
	}

	public void containerTick() {
		super.containerTick();
		this.name.tick();
	}

	protected void init() {
		super.init();
		this.subInit();
		this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
	}

	protected void subInit() {
		this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
		int i = this.getWidth();
		int j = (this.height - this.imageHeight) / 2;
		this.name = new EditBox(this.font, i + 8, j + 10, 116, 12, Component.translatable("container.repair"));
		this.name.setCanLoseFocus(false);
		this.name.setTextColor(-1);
		this.name.setTextColorUneditable(-1);
		this.name.setBordered(false);
		this.name.setMaxLength(40);
		this.name.setResponder(this::onNameChanged);
		this.name.setValue(stack.getHoverName().getString());
		this.addWidget(this.name);
		this.setInitialFocus(this.name);
		this.setFocused(this.name);
	}

	public void resize(Minecraft mc, int x, int y) {
		String s = this.name.getValue();
		this.init(mc, x, y);
		this.name.setValue(s);
	}

	public boolean keyPressed(int id, int par1, int par2) {
		if (id == 256) {
			this.minecraft.player.closeContainer();
		}

		else if (id == 69) {
			return true;
		}

		return !this.name.keyPressed(id, par1, par2) ? super.keyPressed(id, par1, par2) : true;
	}

	private void onNameChanged(String name) {
		if (!name.isEmpty()) {
			String s = name;
			this.minecraft.player.connection.send(new ServerboundRenameItemPacket(s));
		}
	}

	public void render(PoseStack pose, int mouseX, int mouseY, float parTick) {
		super.render(pose, mouseX, mouseY, parTick);
		this.name.render(pose, mouseX, mouseY, parTick);
	}

	@Override
	protected void renderBg(PoseStack pose, float parTick, int mouseX, int mouseY) {
		super.renderBg(pose, parTick, mouseX, mouseY);
		this.font.drawShadow(pose, this.getText("save"), this.getWidth() + 141, this.getHeight() + 9, 0xFFFFFF);
	}

	@Override
	protected ResourceLocation getTEX() {
		return TEX;
	}
}
