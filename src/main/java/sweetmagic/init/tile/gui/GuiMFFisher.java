package sweetmagic.init.tile.gui;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.tile.gui.util.SMRenderTex;
import sweetmagic.init.tile.gui.util.SMRenderTex.MFRenderGage;
import sweetmagic.init.tile.menu.MFFisherMenu;
import sweetmagic.init.tile.sm.TileMFFisher;

public class GuiMFFisher extends GuiSMBase<MFFisherMenu> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_mffisher.png");
	private final TileMFFisher tile;

	public GuiMFFisher(MFFisherMenu menu, Inventory pInv, Component title) {
		super(menu, pInv, title);
		this.setGuiSize(176, 194);
		this.tile = menu.tile;
		this.addRenderTexList(new SMRenderTex(TEX, 48, 9, 0, 0, 76, 10, new MFRenderGage(menu.tile, false)));
	}

	@Override
	protected void renderBg(PoseStack pose, float parTick, int mouseX, int mouseY) {
		super.renderBg(pose, parTick, mouseX, mouseY);

		if (this.tile.craftTime > 0) {
			int progress = this.tile.getCraftProgress(22);
			this.blit(pose, this.getWidth() + 80, this.getHeight() + 26, 179, 26, 15, progress);
		}
	}

	@Override
	protected void renderLabels(PoseStack pose, int mouseX, int mouseY) {
		super.renderLabels(pose, mouseX, mouseY);

		int tipX = this.getWidth() + 80;
		int tipY = this.getHeight() + 26;

		if (this.isRender(tipX, tipY, mouseX, mouseY, 15, 22)) {
			int xAxis = mouseX - this.getWidth();
			int yAxis = mouseY - this.getHeight();
			this.renderTooltip(pose, this.getText("progress", this.tile.getCraftProgress(100)).withStyle(GOLD), xAxis, yAxis);
		}
	}

	protected ResourceLocation getTEX() {
		return TEX;
	}
}
