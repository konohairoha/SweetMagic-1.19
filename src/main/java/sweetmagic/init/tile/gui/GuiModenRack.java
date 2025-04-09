package sweetmagic.init.tile.gui;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.tile.menu.ModenRackMenu;

public class GuiModenRack extends GuiSMBase<ModenRackMenu> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_moden_rack.png");
	private int data = 0;

	public GuiModenRack(ModenRackMenu menu, Inventory pInv, Component title) {
		super(menu, pInv, title);
		this.setGuiSize(173, 132);
		this.data = menu.data;
	}

	@Override
	protected void renderBg(PoseStack pose, float parTick, int mouseX, int mouseY) {
		super.renderBg(pose, parTick, mouseX, mouseY);
		this.renderSlot(pose, this.getWidth(), this.getHeight());
	}

	public void renderSlot (PoseStack pose, int x, int y) {
		switch (this.data) {
		case 0:
			for (int addX = 0; addX < 3; addX++)
				for (int addY = 0; addY < 2; addY++)
					this.blit(pose, x + 6 + addX * 54, y + 7 + addY * 18, 173, 0, 54, 18);
			break;
		case 1:
			this.blit(pose, x + 60, y + 7, 173, 0, 54, 18);
			break;
		case 2:
			for (int addY = 0; addY < 2; addY++)
				this.blit(pose, x + 60, y + 7 + addY * 18, 173, 0, 54, 18);
			break;
		case 3:
			for (int addY = 0; addY < 2; addY++) {
				this.blit(pose, x + 60 - 36, y + 7 + addY * 18, 173, 0, 54, 18);
				this.blit(pose, x + 60 + 36, y + 7 + addY * 18, 173, 0, 54, 18);
			}
			break;
		case 4:
		case 5:
			for (int addY = 0; addY < 2; addY++)
				this.blit(pose, x + 78, y + 7 + addY * 18, 173, 0, 18, 18);
			break;
		}
	}

	protected ResourceLocation getTEX() {
		return TEX;
	}
}
