package sweetmagic.util;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.VoxelShape;
import sweetmagic.init.block.base.BaseFaceBlock;
import sweetmagic.init.block.sm.MagiaPortal;

public record FaceAABB (double x1, double y1, double z1, double x2, double y2, double z2) {

	public static VoxelShape[] create(double x1, double y1, double z1, double x2, double y2, double z2) {
		return new FaceAABB(x1, y1, z1, x2, y2, z2).getRotatedBounds();
	}

	public static VoxelShape getAABB(VoxelShape[] aabbArray, BlockState state) {
		return aabbArray[state.getValue(BaseFaceBlock.FACING).get3DDataValue() - 2];
	}

	public static VoxelShape getAABBUP(VoxelShape[] aabbArray, BlockState state) {
		return aabbArray[state.getValue(BlockStateProperties.FACING).get3DDataValue() - 2];
	}

	public static VoxelShape getAABB(VoxelShape[] aabbArray, VoxelShape aabb, BlockState state) {
		Direction.Axis axis = state.getValue(MagiaPortal.AXIS);
		int value = 0;
		switch (axis) {
		case X:
			value = 2;
			break;
		case Y: return aabb;
		}
		return aabbArray[value];
	}

	public VoxelShape[] getRotatedBounds() {
		VoxelShape north = this.getBlockAABB(Direction.NORTH, this);
		VoxelShape south = this.getBlockAABB(Direction.SOUTH, this);
		VoxelShape west = this.getBlockAABB(Direction.WEST, this);
		VoxelShape east = this.getBlockAABB(Direction.EAST, this);
		return new VoxelShape[] { north, south, west, east };
	}

	public VoxelShape getBlockAABB(Direction face, FaceAABB aabb) {
		double[] fixeAABB = this.fixRot(face, aabb.x1, aabb.z1, aabb.x2, aabb.z2);
		return Block.box(fixeAABB[0], aabb.y1, fixeAABB[1], fixeAABB[2], aabb.y2, fixeAABB[3]);
	}

	private double[] fixRot(Direction face, double var1, double var2, double var3, double var4) {
		switch (face) {
		case SOUTH:
			double sVar1 = var1;
			double sVar2 = var2;
			var1 = 16F - var3;
			var2 = 16F - var4;
			var3 = 16F - sVar1;
			var4 = 16F - sVar2;
			break;
		case WEST:
			double wVar1 = var1;
			var1 = var2;
			var2 = 16F - var3;
			var3 = var4;
			var4 = 16F - wVar1;
			break;
		case EAST:
			double eVar1 = var1;
			double eVar2 = var2;
			double eVar3 = var3;
			var1 = 16F - var4;
			var2 = eVar1;
			var3 = 16F - eVar2;
			var4 = eVar3;
		default:
			break;
		}
		return new double[] { var1, var2, var3, var4 };
	}
}
