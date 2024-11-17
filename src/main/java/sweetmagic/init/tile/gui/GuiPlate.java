package sweetmagic.init.tile.gui;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.tile.menu.PlateMenu;

public class GuiPlate extends GuiSMBase<PlateMenu> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_moden_rack.png");
	public int data = 0;

	public GuiPlate(PlateMenu menu, Inventory pInv, Component title) {
		super(menu, pInv, title);
		this.setGuiWidth(173);
		this.setGuiHeight(132);
		this.data = menu.data;
	}

	@Override
	protected void renderBg(PoseStack pose, float parTick, int mouseX, int mouseY) {
		super.renderBg(pose, parTick, mouseX, mouseY);
		this.renderSlot(pose, this.getWidth(), this.getHeight());
	}

	public void renderSlot (PoseStack pose, int x, int y) {
		switch (this.data) {
		case 3:
			for (int py = 0; py < 2; py++)
				this.blit(pose, x + 78, y + 8 + py * 18, 173, 0, 18, 18);
		break;
		default :
			this.blit(pose, x + 78, y + 13, 173, 0, 18, 18);
		break;
		}
	}

	protected ResourceLocation getTEX () {
		return TEX;
	}
}
