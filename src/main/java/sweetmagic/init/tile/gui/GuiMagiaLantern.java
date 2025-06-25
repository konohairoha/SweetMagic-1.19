package sweetmagic.init.tile.gui;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.tile.gui.util.SMButton;
import sweetmagic.init.tile.gui.util.SMButton.SMButtonTip;
import sweetmagic.init.tile.gui.util.SMRenderTex;
import sweetmagic.init.tile.gui.util.SMRenderTex.MFRenderGage;
import sweetmagic.init.tile.menu.MagiaLanternMenu;
import sweetmagic.init.tile.sm.TileMagiaLantern;

public class GuiMagiaLantern extends GuiSMBase<MagiaLanternMenu> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_magia_lantern.png");
	private final TileMagiaLantern tile;

	public GuiMagiaLantern(MagiaLanternMenu menu, Inventory pInv, Component title) {
		super(menu, pInv, title);
		this.tile = menu.tile;
		this.setGuiSize(173, 132);
		this.addButtonMap(0, new SMButton(TEX, 56, 28, 0, 134, 15, 12));
		this.addButtonMap(1, new SMButton(TEX, 31, 28, 0, 147, 23, 12));
		this.addButtonMap(2, new SMButton(TEX, 6, 28, 0, 147, 23, 12));
		this.addButtonMap(3, new SMButton(TEX, 102, 28, 0, 134, 15, 12));
		this.addButtonMap(4, new SMButton(TEX, 119, 28, 0, 147, 23, 12));
		this.addButtonMap(5, new SMButton(TEX, 144, 28, 0, 147, 23, 12));
		this.addRenderTexList(new SMRenderTex(TEX, 73, 28, 0, 200, 27, 13, new SMButtonTip("lantern_range", 0, -4)));
		this.addRenderTexList(new SMRenderTex(TEX, 48, 9, 0, 0, 77, 10, new MFRenderGage(this.tile, false)));
	}

	@Override
	protected void renderBg(PoseStack pose, float parTick, int mouseX, int mouseY) {
		super.renderBg(pose, parTick, mouseX, mouseY);
		int x = this.getWidth();
		int y = this.getHeight();

		this.font.drawShadow(pose, this.getLabel(this.tile.getRange()), x + 75, y + 31, 0x2BC444);

		this.font.drawShadow(pose, this.getLabel("-1"), x + 58, y + 31, 0xFFFFFF);
		this.font.drawShadow(pose, this.getLabel("-10"), x + 33, y + 31, 0xFFFFFF);
		this.font.drawShadow(pose, this.getLabel("-64"), x + 8, y + 31, 0xFFFFFF);

		this.font.drawShadow(pose, this.getLabel("+1"), x + 104, y + 31, 0xFFFFFF);
		this.font.drawShadow(pose, this.getLabel("+10"), x + 121, y + 31, 0xFFFFFF);
		this.font.drawShadow(pose, this.getLabel("+64"), x + 146, y + 31, 0xFFFFFF);
	}

	@Override
	protected ResourceLocation getTEX() {
		return TEX;
	}
}
