package sweetmagic.init.tile.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.tile.gui.util.SMRenderTex;
import sweetmagic.init.tile.gui.util.SMRenderTex.MFRenderGage;
import sweetmagic.init.tile.menu.AetherPlanterMenu;
import sweetmagic.init.tile.sm.TileAetherPlanter;

public class GuiAetherPlanter extends GuiSMBase<AetherPlanterMenu> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_mffisher.png");
	private final TileAetherPlanter tile;

	public GuiAetherPlanter(AetherPlanterMenu menu, Inventory pInv, Component title) {
		super(menu, pInv, title);
		this.setGuiSize(176, 194);
		this.tile = menu.tile;
		this.addRenderTexList(new SMRenderTex(TEX, 48, 9, 0, 0, 76, 10, new MFRenderGage(this.tile, false)));
	}

	protected ResourceLocation getTEX() {
		return TEX;
	}
}
