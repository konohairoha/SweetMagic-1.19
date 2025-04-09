package sweetmagic.init.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.VoxelShape;
import sweetmagic.init.tile.sm.TileMagiaAccelerator;
import sweetmagic.init.tile.sm.TileSMMagic;
import sweetmagic.util.RenderUtil.RenderInfo;

public class RenderMagiaAccelerator<T extends TileMagiaAccelerator> extends RenderAbstractTile<T> {

	public RenderMagiaAccelerator(BlockEntityRendererProvider.Context con) {
		super(con);
	}

	public void render(T tile, float parTick, RenderInfo info) {
		if (!tile.isRangeView) { return; }

		Level world = tile.getLevel();
		double posX = tile.getBlockPos().getX();
		double posY = tile.getBlockPos().getY();
		double posZ = tile.getBlockPos().getZ();
		Iterable<BlockPos> posList = tile.getRangePosUnder(tile.getBlockPos(), tile.range);

		// リスト分まわす
		for (BlockPos pos : posList) {

			// MFブロック以外または送信側なら終了
			if ( !(tile.getTile(pos) instanceof TileSMMagic magic) || !magic.getReceive() || magic instanceof TileMagiaAccelerator) { continue; }

			VoxelShape voxel = world.getBlockState(pos).getCollisionShape(world, pos);
			VertexConsumer con = info.buf().getBuffer(RenderType.lineStrip());
			this.drawShape(info.pose(), con, voxel, -posX + pos.getX(), -posY + pos.getY() + 0.01D, -posZ + pos.getZ());
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
