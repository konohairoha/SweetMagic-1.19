package sweetmagic.init.render.block;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import sweetmagic.init.tile.sm.TileMirageGlass;
import sweetmagic.util.RenderUtil;
import sweetmagic.util.RenderUtil.RenderInfo;

public class RenderMirageGlass<T extends TileMirageGlass> extends RenderAbstractTile<T> {

	public RenderMirageGlass(BlockEntityRendererProvider.Context con) {
		super(con);
	}

	public void render(T tile, float parTick, RenderInfo info) {
		if (tile.state == null) { return; }
		ModelBlockRenderer.enableCaching();
		PoseStack pose = info.pose();
		pose.pushPose();
		RenderUtil.renderBlock(tile.getLevel(), tile.getBlockPos(), tile.getState(), this.bRender, pose, info.buf(), info.overlay());
		pose.popPose();
		ModelBlockRenderer.clearCache();
	}

	public int getViewDistance() {
		return 80;
	}
}
