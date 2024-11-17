package sweetmagic.init.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;
import sweetmagic.init.BlockInit;
import sweetmagic.init.tile.sm.TileAetherLamplight;
import sweetmagic.init.tile.sm.TileAetherLanp;
import sweetmagic.init.tile.sm.TileSMMagic;
import sweetmagic.util.RenderUtil;
import sweetmagic.util.RenderUtil.RenderColor;

public class RenderAetherLamplight implements BlockEntityRenderer<TileAetherLamplight> {

	private static final Block LAMP = BlockInit.aether_lamplight_render;
	private static final Block SQUARE_BLOCK_L = BlockInit.magic_square_l_blank;
	private static final Block SQUARE_BLOCK_S = BlockInit.magic_square_l;

	public RenderAetherLamplight(BlockEntityRendererProvider.Context con) { }

	@Override
	public void render(TileAetherLamplight tile, float parTick, PoseStack pose, MultiBufferSource buf, int light, int overlayLight) {
		if (tile.getLevel() == null || tile.isAir()) { return; }

		long gameTime = tile.getTime();
		float sin = (float) Math.sin((gameTime + parTick) / 10D);
		float fSin = sin * 50F;
		float angle = (gameTime + parTick) / 20.0F * (180F / (float) Math.PI);

		pose.pushPose();
		pose.translate(0D, sin * 0.067D + 0.2D, 0D);
		RenderColor color = new RenderColor(1F, 1F, 1F, light, OverlayTexture.NO_OVERLAY);
		RenderUtil.renderBlock(pose, buf, color, LAMP);
		pose.popPose();

		pose.pushPose();
		pose.translate(0.5D, sin * 0.067D + 0.2D, 0.5D);
		pose.mulPose(Vector3f.YP.rotationDegrees(angle));
		pose.translate(-0.5D, 0D, -0.5D);
		RenderColor color2 = new RenderColor( (63F + fSin) / 255F, (184F + fSin) / 255F, (205F + fSin) / 255F, light, OverlayTexture.NO_OVERLAY);
		RenderUtil.renderBlock(pose, buf, color2, SQUARE_BLOCK_L);
		pose.popPose();

		pose.pushPose();
		pose.translate(0.5D, sin * 0.067D + 1.4D, 0.5D);
		pose.mulPose(Vector3f.YP.rotationDegrees(-angle * 1.1F));
		pose.translate(-0.5D, 0D, -0.5D);
		RenderColor color3 = new RenderColor( (63F + sin * 49F) / 255F, (184F - sin * 71F) / 255F, 1F, light, OverlayTexture.NO_OVERLAY);
		RenderUtil.renderBlock(pose, buf, color3, SQUARE_BLOCK_S);
		pose.popPose();

		if (!tile.isRangeView) { return; }

		// 範囲の座標取得
		Iterable<BlockPos> posList = tile.getRangePosUnder(tile.range);

		Level world = tile.getLevel();
        double posX = tile.getBlockPos().getX();
        double posY = tile.getBlockPos().getY();
        double posZ = tile.getBlockPos().getZ();

		// リスト分まわす
		for (BlockPos pos : posList) {

			// MFブロック以外なら終了
			if ( !(tile.getTile(pos) instanceof TileSMMagic magic) || !magic.getReceive()) { continue; }

			if (magic instanceof TileAetherLamplight || magic instanceof TileAetherLanp) { continue; }

			VoxelShape voxel = world.getBlockState(pos).getCollisionShape(world, pos);
			VertexConsumer con = buf.getBuffer(RenderType.lineStrip());
			this.drawShape(pose, con, voxel, -posX + pos.getX(), -posY + pos.getY() + 0.01D, -posZ + pos.getZ());
		}
	}


	private void drawShape(PoseStack pose, VertexConsumer con, VoxelShape voxel, double x, double y, double z) {
		Matrix4f mat4 = pose.last().pose();
		Matrix3f mat3 = pose.last().normal();
		voxel.forAllEdges((x1, y1, z1, x2, y2, z2) -> {
			con.vertex(mat4, (float) (x1 + x), (float) (y1 + y), (float) (z1 + z)).color(1F, 38F / 255F, 49F / 255F, 1F).normal(mat3, 5F, 5F, 5F).endVertex();
			con.vertex(mat4, (float) (x2 + x), (float) (y2 + y), (float) (z2 + z)).color(1F, 38F / 255F, 49F / 255F, 1F).normal(mat3, 5F, 5F, 5F).endVertex();
		});
    }
}
