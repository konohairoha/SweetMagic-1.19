package sweetmagic.init.tile.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.util.ISMTip;
import sweetmagic.init.tile.gui.util.SMRenderTex;
import sweetmagic.init.tile.gui.util.SMRenderTex.MFRenderGage;
import sweetmagic.init.tile.menu.AetherRepairMenu;
import sweetmagic.init.tile.sm.TileAetherRepair;

public class GuiAetherRepair extends GuiSMBase<AetherRepairMenu> implements ISMTip {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_toolrepair.png");
	private static final ResourceLocation MISC = SweetMagicCore.getSRC("textures/gui/gui_misc.png");

	public final TileAetherRepair tile;

	public GuiAetherRepair(AetherRepairMenu menu, Inventory pInv, Component title) {
		super(menu, pInv, title);
		this.setGuiWidth(200);
		this.setGuiHeight(178);
		this.tile = menu.tile;
		this.getRenderTexList().add(new SMRenderTex(TEX, 36, 10, 0, 0, 11, 77, new MFRenderGage(this.tile, 179, 0, 11, 76, 76, true)));
	}

	protected void renderBGBase (PoseStack pose, float parTick, int mouseX, int mouseY) {
		RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
		RenderSystem.setShaderTexture(0, this.getTEX());
		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
		this.blit(pose, this.leftPos + 24, this.topPos, 0, 0, this.getGuiWidth() - 24, this.getGuiHeight());
	}

	@Override
	protected void renderBg(PoseStack pose, float parTick, int mouseX, int mouseY) {
		super.renderBg(pose, parTick, mouseX, mouseY);
		RenderSystem.setShaderTexture(0, MISC);
		this.blit(pose, this.getWidth(), this.getHeight() + 92, 70, 0, 24, 78);
	}

	protected ResourceLocation getTEX () {
		return TEX;
	}
}
