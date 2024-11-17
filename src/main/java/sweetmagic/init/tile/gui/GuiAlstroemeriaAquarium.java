package sweetmagic.init.tile.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.tile.gui.util.SMRenderTex;
import sweetmagic.init.tile.gui.util.SMRenderTex.MFRenderGage;
import sweetmagic.init.tile.menu.AlstroemeriaAquariumMenu;

public class GuiAlstroemeriaAquarium extends GuiSMBase<AlstroemeriaAquariumMenu> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_alstroemeria_aquarium.png");

	public GuiAlstroemeriaAquarium(AlstroemeriaAquariumMenu menu, Inventory pInv, Component title) {
		super(menu, pInv, title);
		this.setGuiWidth(176);
		this.setGuiHeight(230);
		this.getRenderTexList().add(new SMRenderTex(TEX, 7, 7, 0, 0, 11, 77, new MFRenderGage(menu.tile, 179, 7, 11, 76, 76, true)));
	}

	protected ResourceLocation getTEX () {
		return TEX;
	}
}
