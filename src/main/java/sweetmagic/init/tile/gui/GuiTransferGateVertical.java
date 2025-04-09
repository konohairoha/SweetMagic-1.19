package sweetmagic.init.tile.gui;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.tile.menu.TransferGateVerticalMenu;

public class GuiTransferGateVertical extends GuiSMBase<TransferGateVerticalMenu> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_moden_rack.png");

	public GuiTransferGateVertical(TransferGateVerticalMenu menu, Inventory pInv, Component title) {
		super(menu, pInv, title);
		this.setGuiSize(173, 132);
	}

	@Override
	protected void renderBg(PoseStack pose, float parTick, int mouseX, int mouseY) {
		super.renderBg(pose, parTick, mouseX, mouseY);
		this.blit(pose, this.getWidth() + 78, this.getHeight() + 13, 173, 0, 18, 18);
	}

	protected ResourceLocation getTEX() {
		return TEX;
	}
}
