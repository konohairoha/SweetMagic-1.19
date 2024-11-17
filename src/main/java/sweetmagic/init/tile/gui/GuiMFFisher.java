package sweetmagic.init.tile.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.tile.gui.util.SMRenderTex;
import sweetmagic.init.tile.gui.util.SMRenderTex.MFRenderGage;
import sweetmagic.init.tile.menu.MFFisherMenu;

public class GuiMFFisher extends GuiSMBase<MFFisherMenu> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_mffisher.png");

	public GuiMFFisher(MFFisherMenu menu, Inventory pInv, Component title) {
		super(menu, pInv, title);
		this.setGuiWidth(176);
		this.setGuiHeight(199);
		this.getRenderTexList().add(new SMRenderTex(TEX, 48, 9, 0, 0, 76, 10, new MFRenderGage(menu.tile, 178, 9, 76, 10, 76, false)));
	}

	protected ResourceLocation getTEX () {
		return TEX;
	}
}
