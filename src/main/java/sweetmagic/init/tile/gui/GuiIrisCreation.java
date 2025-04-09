package sweetmagic.init.tile.gui;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.tile.menu.IrisCreationMenu;
import sweetmagic.init.tile.sm.TileIrisCreation;

public class GuiIrisCreation extends GuiSMBase<IrisCreationMenu> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_iris_creation.png");
	private final TileIrisCreation tile;

	public GuiIrisCreation(IrisCreationMenu menu, Inventory pInv, Component title) {
		super(menu, pInv, title);
		this.setGuiSize(176, 176);
		this.tile = menu.tile;
	}

	@Override
	protected void renderBg(PoseStack pose, float parTick, int mouseX, int mouseY) {
		super.renderBg(pose, parTick, mouseX, mouseY);
		if (this.tile.craftTime <= 0) { return; }

		int progress = this.tile.getProgress(60);
		this.blit(pose, this.getWidth() + 55, this.getHeight() + 12, 55, 176, progress, 32);
	}

	protected ResourceLocation getTEX() {
		return TEX;
	}
}
