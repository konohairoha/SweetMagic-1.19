package sweetmagic.init.tile.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.tile.gui.util.SMRenderTex;
import sweetmagic.init.tile.gui.util.SMRenderTex.MFRenderGage;
import sweetmagic.init.tile.menu.MFFurnaceMenu;
import sweetmagic.init.tile.sm.TileMFFurnace;

public class GuiMFFurnace extends GuiSMBase<MFFurnaceMenu> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_mffurnace.png");
	private final TileMFFurnace tile;

	public GuiMFFurnace(MFFurnaceMenu menu, Inventory pInv, Component title) {
		super(menu, pInv, title);
		this.setGuiWidth(176);
		this.setGuiHeight(212);
		this.tile = menu.tile;
		this.getRenderTexList().add(new SMRenderTex(TEX, 7, 7, 0, 0, 11, 77, new MFRenderGage(this.tile, 179, 7, 11, 76, 76, true)));
	}

	@Override
	protected void renderBg(PoseStack pose, float parTick, int mouseX, int mouseY) {
		super.renderBg(pose, parTick, mouseX, mouseY);

		// MFの表示
		if (this.tile.isCraft) {
			RenderSystem.setShaderTexture(0, TEX);
			int progress = this.tile.getCraftProgressScaled(22);
			this.blit(pose, this.getWidth() + 144, this.getHeight() + 59, 194, 59, 15, progress);
		}
	}

	protected ResourceLocation getTEX () {
		return TEX;
	}
}
