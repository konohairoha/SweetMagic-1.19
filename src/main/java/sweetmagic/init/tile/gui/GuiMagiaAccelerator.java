package sweetmagic.init.tile.gui;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.tile.gui.util.SMButton;
import sweetmagic.init.tile.gui.util.SMRenderTex;
import sweetmagic.init.tile.gui.util.SMRenderTex.MFRenderGage;
import sweetmagic.init.tile.menu.MagiaAcceleratorMenu;
import sweetmagic.init.tile.sm.TileMagiaAccelerator;

public class GuiMagiaAccelerator extends GuiSMBase<MagiaAcceleratorMenu> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_aether_lantern.png");
	private final TileMagiaAccelerator tile;

	public GuiMagiaAccelerator(MagiaAcceleratorMenu menu, Inventory pInv, Component title) {
		super(menu, pInv, title);
		this.tile = menu.tile;
		this.setGuiWidth(173);
		this.setGuiHeight(132);

		this.getButtonMap().put(0, new SMButton(TEX, 87, 24, 0, 133, 11, 9));
		this.getButtonMap().put(1, new SMButton(TEX, 99, 24, 0, 142, 15, 9));
		this.getButtonMap().put(2, new SMButton(TEX, 87, 36, 30, 133, 11, 9));
		this.getButtonMap().put(3, new SMButton(TEX, 99, 36, 30, 142, 15, 9));
		this.getButtonMap().put(4, new SMButton(TEX, 65, 27, 175, 21, 14, 14));
		this.getRenderTexList().add(new SMRenderTex(TEX, 48, 9, 0, 0, 77, 10, new MFRenderGage(this.tile, 175, 9, 77, 10, 76, false)));
	}

	protected void renderBGBase (PoseStack pose, float parTick, int mouseX, int mouseY) {
		super.renderBGBase(pose, parTick, mouseX, mouseY);
		this.blit(pose, this.getWidth() + 18, this.getHeight() + 25, 175, 35, 49, 16);
	}

	@Override
	protected void renderBg(PoseStack pose, float parTick, int mouseX, int mouseY) {
		super.renderBg(pose, parTick, mouseX, mouseY);
		this.font.drawShadow(pose, this.getTip("" + this.tile.range), this.getWidth() + 125, this.getHeight() + 30, 0x2BC444);
	}

	@Override
	protected void renderLabels(PoseStack pose, int mouseX, int mouseY) {
		super.renderLabels(pose, mouseX, mouseY);
		int tipX = this.getWidth() + 65;
		int tipY = this.getHeight() + 27;

		if (this.isRendeer(tipX, tipY, mouseX, mouseY, 14, 14)) {
			int xAxis = (mouseX - this.getWidth());
			int yAxis = (mouseY - this.getHeight());
            this.renderTooltip(pose, this.getTipArray(this.getText("isrange"), this.getTip("" + this.tile.isRangeView)).withStyle(GOLD), xAxis, yAxis);
		}
	}

	@Override
	protected ResourceLocation getTEX () {
		return TEX;
	}
}
