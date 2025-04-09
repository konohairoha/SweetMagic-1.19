package sweetmagic.init.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.Block;
import sweetmagic.init.BlockInit;
import sweetmagic.init.tile.sm.TileCeilingFan;
import sweetmagic.util.RenderUtil;
import sweetmagic.util.RenderUtil.RenderInfo;

public class RenderCeilingFan<T extends TileCeilingFan> extends RenderAbstractTile<T> {

	private static final Block FAN = BlockInit.ceiling_fan;
	private static final Block BLADE = BlockInit.ceiling_fan_blade;

	public RenderCeilingFan(BlockEntityRendererProvider.Context con) {
		super(con);
	}

	public void render(T tile, float parTick, RenderInfo info) {

		PoseStack pose = info.pose();
		ModelBlockRenderer.enableCaching();
		pose.pushPose();
		RenderUtil.renderBlock(tile.getLevel(), tile.getBlockPos(), FAN.defaultBlockState(), this.bRender, pose, info.buf(), info.overlay());
		pose.popPose();

		pose.pushPose();
		pose.translate(0.5D, 0D, 0.5D);
		pose.mulPose(Vector3f.YP.rotationDegrees(tile.rote));
		pose.translate(-0.5D, 0D, -0.5D);
		RenderUtil.renderBlock(tile.getLevel(), tile.getBlockPos(), BLADE.defaultBlockState(), this.bRender, pose, info.buf(), info.overlay());
		pose.popPose();

		ModelBlockRenderer.clearCache();
	}
}
