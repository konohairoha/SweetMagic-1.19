package sweetmagic.init.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import sweetmagic.init.tile.sm.TileMagiaStorage;
import sweetmagic.util.RenderUtil;
import sweetmagic.util.RenderUtil.RenderColor;
import sweetmagic.util.RenderUtil.RenderInfo;

public class RenderMagiaStorage<T extends TileMagiaStorage> extends RenderAbstractTile<T> {

	public RenderMagiaStorage(BlockEntityRendererProvider.Context con) {
		super(con);
	}

	public void render(T tile, float parTick, RenderInfo info) {
		PoseStack pose = info.pose();
		pose.translate(0.5D, 0.5D, 0.5D);
		int gameTime = tile.getClientTime();
		pose.translate(0D, Math.sin((gameTime + parTick) * 0.1F) * 0.1F - 0.35F, 0D);
		float angle = (gameTime + parTick) / 20F * this.pi;
		pose.mulPose(Vector3f.YP.rotationDegrees(angle));
		pose.translate(-0.5D, 0D, -0.5D);
		RenderUtil.renderTransBlock(pose, info.buf(), RenderColor.create(info), tile.getBlock());
	}
}
