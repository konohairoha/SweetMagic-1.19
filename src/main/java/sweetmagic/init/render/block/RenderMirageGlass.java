package sweetmagic.init.render.block;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import sweetmagic.init.tile.sm.TileMirageGlass;
import sweetmagic.util.RenderUtil;

public class RenderMirageGlass extends RenderAbstractTile<TileMirageGlass> {

	public RenderMirageGlass(BlockEntityRendererProvider.Context con) {
		super(con);
	}

	@Override
	public void render(TileMirageGlass tile, float parTick, PoseStack pose, MultiBufferSource buf, int light, int overlayLight) {
		if (tile.getLevel() == null || tile.isAir() || tile.state == null) { return; }
        ModelBlockRenderer.enableCaching();
        pose.pushPose();
		RenderUtil.renderBlock(tile.getLevel(), tile.getBlockPos(), tile.getState(), this.bRender, pose, buf, overlayLight);
		pose.popPose();
        ModelBlockRenderer.clearCache();
	}
}
