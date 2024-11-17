package sweetmagic.init.tile.gui;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.tile.gui.util.SMRenderTex;
import sweetmagic.init.tile.gui.util.SMRenderTex.MFRenderGage;
import sweetmagic.init.tile.menu.AetherRecyclerMenu;
import sweetmagic.init.tile.sm.TileAetherRecycler;

public class GuiAetherRecycler extends GuiSMBase<AetherRecyclerMenu> {

	public final TileAetherRecycler tile;
	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/gui/gui_aether_recycler.png");

	public GuiAetherRecycler(AetherRecyclerMenu menu, Inventory pInv, Component title) {
		super(menu, pInv, title);
		this.setGuiWidth(176);
		this.setGuiHeight(230);
		this.tile = menu.tile;
		this.getRenderTexList().add(new SMRenderTex(TEX, 7, 7, 0, 0, 11, 77, new MFRenderGage(menu.tile, 179, 7, 11, 76, 76, true)));
	}

	@Override
	protected void renderBg(PoseStack pose, float parTick, int mouseX, int mouseY) {
		super.renderBg(pose, parTick, mouseX, mouseY);

		// クラフトゲージの値を設定
		if (this.tile.craftTime > 0) {
			int x = this.getWidth();
			int y = this.getHeight();
			int progress = this.tile.getCraftProgressScaled(22);
			this.blit(pose, x + 143, y + 60, 194, 60, 15, progress);
		}
	}

	protected ResourceLocation getTEX () {
		return TEX;
	}
}
