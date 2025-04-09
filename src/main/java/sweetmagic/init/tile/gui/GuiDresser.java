package sweetmagic.init.tile.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.tile.gui.util.SMButton;
import sweetmagic.init.tile.gui.util.SMButton.SMButtonTip;
import sweetmagic.init.tile.menu.DresserMenu;

public class GuiDresser extends GuiSMBase<DresserMenu> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/generic_54.png");

	public GuiDresser(DresserMenu menu, Inventory pInv, Component title) {
		super(menu, pInv, title);
		this.setGuiSize(176, 202);
		this.addButtonMap(0, new SMButton(MISC, 161, -11, 114, 0, 10, 9, new SMButtonTip("sort", -18, 14)));
		this.addButtonMap(1, new SMButton(MISC, 149, -11, 137, 0, 11, 9, new SMButtonTip("quick_stack", -18, 14)));
		this.addButtonMap(2, new SMButton(MISC, 137, -11, 161, 0, 11, 9, new SMButtonTip("restock", -18, 14)));
	}

	protected ResourceLocation getTEX() {
		return TEX;
	}
}
