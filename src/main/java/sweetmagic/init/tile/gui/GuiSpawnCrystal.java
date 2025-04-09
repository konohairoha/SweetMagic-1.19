package sweetmagic.init.tile.gui;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.tile.gui.util.SMButton;
import sweetmagic.init.tile.menu.SpawnCrystalMenu;
import sweetmagic.init.tile.sm.TileSpawnCrystal;

public class GuiSpawnCrystal extends GuiSMBase<SpawnCrystalMenu> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_smspawner.png");
	private final TileSpawnCrystal tile;

	public GuiSpawnCrystal(SpawnCrystalMenu menu, Inventory pInv, Component title) {
		super(menu, pInv, title);
		this.tile = menu.tile;
		this.setGuiSize(173, 133);
		this.addButtonMap(0, new SMButton(TEX, 136, 7, 176, 7, 11, 9));
		this.addButtonMap(1, new SMButton(TEX, 148, 7, 176, 16, 11, 9));
	}

	protected void renderBGBase(PoseStack pose, float parTick, int mouseX, int mouseY) {
		super.renderBGBase(pose, parTick, mouseX, mouseY);
		this.blit(pose, this.getWidth() + 18, this.getHeight() + 25, 175, 35, 49, 16);
	}

	@Override
	protected void renderBg(PoseStack pose, float parTick, int mouseX, int mouseY) {
		super.renderBg(pose, parTick, mouseX, mouseY);
		this.font.drawShadow(pose, this.getTip("range: " + this.tile.getRange()), this.getWidth() + 8, this.getHeight() + 7, 0x2BC444);
	}

	@Override
	protected ResourceLocation getTEX() {
		return TEX;
	}
}
