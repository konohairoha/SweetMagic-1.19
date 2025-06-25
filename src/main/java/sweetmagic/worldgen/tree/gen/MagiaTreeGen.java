package sweetmagic.worldgen.tree.gen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;

public class MagiaTreeGen extends AbstractTreeGen {

	public int height = 10;
	public int randHeight = 1;
	private static final EnumProperty<Direction.Axis> AXIS = RotatedPillarBlock.AXIS;

	public MagiaTreeGen(BlockState log, BlockState leave, int growValue) {
		super(log, leave, growValue);
	}

	public void generate(Level world, RandomSource rand, BlockPos pos) {

		if (!this.checkBlock(world.getBlockState(pos.below()))) { return; }

		for (int y = 1; y <= 8; y++) {
			Material mate = world.getBlockState(pos.above(y)).getMaterial();
			if (mate != Material.AIR && mate == Material.PLANT){ return; }
		}

		world.setBlock(pos, AIR, 3);
		int maxHeight = rand.nextInt(this.randHeight) + this.height;

		this.subTrunk(world, pos.north(), this.log, Direction.NORTH);
		this.subTrunk(world, pos.south(), this.log, Direction.SOUTH);
		this.subTrunk(world, pos.west(), this.log, Direction.WEST);
		this.subTrunk(world, pos.east(), this.log, Direction.EAST);

		for (int y = 0; y < maxHeight; y++) {
			this.setBlock(world, pos.above(y), log);
		}

		// 原木最上段
		this.setBlock(world, pos.offset(0, maxHeight, -1), this.log.setValue(AXIS, Direction.Axis.Z));
		this.setBlock(world, pos.offset(0, maxHeight, 1), this.log.setValue(AXIS, Direction.Axis.Z));
		this.setBlock(world, pos.offset(-1, maxHeight, 0), this.log.setValue(AXIS, Direction.Axis.X));
		this.setBlock(world, pos.offset(1, maxHeight, 0), this.log.setValue(AXIS, Direction.Axis.X));

		BlockPos top = pos.offset(0, maxHeight - 4, 0);

		// 葉っぱ1段目
		for (int i = -2; i <= 2; i++) {
			for (int k = -2; k <= 2; k++) {
				this.setBlock(world, top.offset(i, 0, k), this.leave);
			}
		}

		this.setSubLeave(world, top, 3);

		// 葉っぱ2段目
		for (int i = -3; i <= 3; i++) {
			for (int k = -2; k <= 2; k++) {
				this.setBlock(world, top.offset(i, 1, k), this.leave);
				this.setBlock(world, top.offset(k, 1, i), this.leave);
			}
		}

		this.setSubLeave(world, top.above(1), 4);

		// 葉っぱ3段目
		for (int i = -3; i <= 3; i++) {
			for (int k = -1; k <= 1; k++) {
				this.setBlock(world, top.offset(i, 2, k), this.leave);
				this.setBlock(world, top.offset(k, 2, i), this.leave);
			}
		}

		// 葉っぱ3、4段目
		for (int x = -2; x <= 2; x++) {
			for (int z = -2; z <= 2; z++) {
				for (int y = 0; y <= 1; y++) {
					this.setBlock(world, top.offset(x, y + 2, z), this.leave);
				}
			}
		}

		this.setSubLeave(world, top.above(3), 3);

		// 葉っぱ5段目
		for (int i = -2; i <= 2; i++) {
			for (int k = -1; k <= 1; k++) {
				this.setBlock(world, top.offset(i, 4, k), this.leave);
				this.setBlock(world, top.offset(k, 4, i), this.leave);
			}
		}

		// 葉っぱ6段目
		for (int i = -1; i <= 1; i++) {
			for (int k = -1; k <= 1; k++) {
				this.setBlock(world, top.offset(i, 5, k), this.leave);
			}
		}

		this.setSubLeave(world, top.above(5), 2);

		// 葉っぱ7段目
		for (int i = -1; i <= 1; i++) {
			this.setBlock(world, top.offset(i, 6, 0), this.leave);
			this.setBlock(world, top.offset(0, 6, i), this.leave);
		}
	}

	// 主軸の横
	public void subTrunk (Level world, BlockPos pos, BlockState state, Direction face) {

		RandomSource rand = world.getRandom();
		int height = rand.nextInt(3) + 2;
	
		for (int y = 0; y < height; y++) {
			this.setBlock(world, pos.above(y), state);
		}

		int horizon = rand.nextInt(2) + 1;
		for (int i = 0; i < horizon; i++) {

			BlockPos pos2 = pos;
			BlockState state2 = state;

			switch (face) {
			case NORTH:
				pos2 = pos2.north(i + 1);
				state2 = state2.setValue(AXIS, Direction.Axis.Z);
				break;
			case SOUTH:
				pos2 = pos2.south(i + 1);
				state2 = state2.setValue(AXIS, Direction.Axis.Z);
				break;
			case WEST:
				pos2 = pos2.west(i + 1);
				state2 = state2.setValue(AXIS, Direction.Axis.X);
				break;
			case EAST:
				pos2 = pos2.east(i + 1);
				state2 = state2.setValue(AXIS, Direction.Axis.X);
				break;
			}

			this.setBlock(world, pos2, state2);
		}
	}

	// 葉っぱ
	public void setSubLeave (Level world, BlockPos pos, int scale) {

		RandomSource rand = world.getRandom();
		float chance = 0.45F;

		if (rand.nextFloat() <= chance) {
			this.setBlock(world, pos.offset(0, 0, scale), this.leave);
		}

		if (rand.nextFloat() <= chance) {
			this.setBlock(world, pos.offset(scale, 0, 0), this.leave);
		}

		if (rand.nextFloat() <= chance) {
			this.setBlock(world, pos.offset(0, 0, -scale), this.leave);
		}

		if (rand.nextFloat() <= chance) {
			this.setBlock(world, pos.offset(-scale, 0, 0), this.leave);
		}
	}
}

