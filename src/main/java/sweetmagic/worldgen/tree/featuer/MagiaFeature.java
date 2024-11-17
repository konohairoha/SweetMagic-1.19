package sweetmagic.worldgen.tree.featuer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.material.Material;
import sweetmagic.init.BlockInit;

public class MagiaFeature extends AbstractTreeFeatuer {

    public int height = 10;
    public int randHeight = 1;
    private static final EnumProperty<Direction.Axis> AXIS = RotatedPillarBlock.AXIS;

	public MagiaFeature() {
		super();
	}

	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> con) {

		BlockPos pos = con.origin();
		WorldGenLevel world = con.level();
		if (!this.checkBlock(world.getBlockState(pos.below()).getBlock())) { return false; }

		for (int y = 1; y <= 8; y++) {
			Material mate = world.getBlockState(pos.above(y)).getMaterial();
			if (mate != Material.AIR && mate == Material.PLANT){ return false; }
		}

		int maxHeight = world.getRandom().nextInt(this.randHeight) + this.height;
		BlockState log = this.getLog();
		BlockState leave = this.getLeave();

		this.subTrunk(world, pos.north(), log, Direction.NORTH);
		this.subTrunk(world, pos.south(), log, Direction.SOUTH);
		this.subTrunk(world, pos.west(), log, Direction.WEST);
		this.subTrunk(world, pos.east(), log, Direction.EAST);

		for (int y = 0; y < maxHeight; y++) {
			this.setBlock(world, pos.above(y), log);
		}

		// 原木最上段
		this.setBlock(world, pos.offset(0, maxHeight, -1), log.setValue(AXIS, Direction.Axis.Z));
		this.setBlock(world, pos.offset(0, maxHeight, 1), log.setValue(AXIS, Direction.Axis.Z));
		this.setBlock(world, pos.offset(-1, maxHeight, 0), log.setValue(AXIS, Direction.Axis.X));
		this.setBlock(world, pos.offset(1, maxHeight, 0), log.setValue(AXIS, Direction.Axis.X));

		BlockPos top = pos.offset(0, maxHeight - 4, 0);

		// 葉っぱ1段目
		for (int i = -2; i <= 2; i++) {
			for (int k = -2; k <= 2; k++) {
				this.setBlock(world, top.offset(i, 0, k), leave);
			}
		}

		this.setSubLeave(world, top, 3, leave);

		// 葉っぱ2段目
		for (int i = -3; i <= 3; i++) {
			for (int k = -2; k <= 2; k++) {
				this.setBlock(world, top.offset(i, 1, k), leave);
				this.setBlock(world, top.offset(k, 1, i), leave);
			}
		}

		this.setSubLeave(world, top.above(1), 4, leave);

		// 葉っぱ3段目
		for (int i = -3; i <= 3; i++) {
			for (int k = -1; k <= 1; k++) {
				this.setBlock(world, top.offset(i, 2, k), leave);
				this.setBlock(world, top.offset(k, 2, i), leave);
			}
		}

		// 葉っぱ3、4段目
		for (int x = -2; x <= 2; x++) {
			for (int z = -2; z <= 2; z++) {
				for (int y = 0; y <= 1; y++) {
					this.setBlock(world, top.offset(x, y + 2, z), leave);
				}
			}
		}

		this.setSubLeave(world, top.above(3), 3, leave);

		// 葉っぱ5段目
		for (int i = -2; i <= 2; i++) {
			for (int k = -1; k <= 1; k++) {
				this.setBlock(world, top.offset(i, 4, k), leave);
				this.setBlock(world, top.offset(k, 4, i), leave);
			}
		}

		// 葉っぱ6段目
		for (int i = -1; i <= 1; i++) {
			for (int k = -1; k <= 1; k++) {
				this.setBlock(world, top.offset(i, 5, k), leave);
			}
		}

		this.setSubLeave(world, top.above(5), 2, leave);

		// 葉っぱ7段目
		for (int i = -1; i <= 1; i++) {
			this.setBlock(world, top.offset(i, 6, 0), leave);
			this.setBlock(world, top.offset(0, 6, i), leave);
		}

		return true;
	}


    // 主軸の横
	public void subTrunk (WorldGenLevel world, BlockPos pos, BlockState state, Direction face) {

		RandomSource rand = world.getRandom();
		int height = rand.nextInt(3) + 2;

		for (int y = 0; y < height + 1; y++) {
			this.setBlock(world, pos.above(y), state);
		}

		this.setBlock(world, pos.below(), Blocks.GRASS_BLOCK.defaultBlockState());

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

			if (world.getBlockState(pos2.below()).isAir()) {
				for (int k = 0; k < 4; k++) {
					pos2 = pos2.below(1);
					pos = pos.below(1);
					this.setBlock(world, pos2, state2.setValue(AXIS, Direction.Axis.Y));

					if (!world.getBlockState(pos2.below(1)).isAir()) { break; }
				}
			}
		}
	}

	// 葉っぱ
	public void setSubLeave (WorldGenLevel world, BlockPos pos, int scale, BlockState leave) {

		RandomSource rand = world.getRandom();
		float chance = 0.45F;

		if (rand.nextFloat() <= chance) {
			this.setBlock(world, pos.offset(0, 0, scale), leave);
		}

		if (rand.nextFloat() <= chance) {
			this.setBlock(world, pos.offset(scale, 0, 0), leave);
		}

		if (rand.nextFloat() <= chance) {
			this.setBlock(world, pos.offset(0, 0, -scale), leave);
		}

		if (rand.nextFloat() <= chance) {
			this.setBlock(world, pos.offset(-scale, 0, 0), leave);
		}
	}

	public BlockState getLog() {
		return BlockInit.magiawood_log.defaultBlockState();
	}

	public BlockState getLeave() {
		return BlockInit.magiawood_leaves.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true);
	}

	protected void setBlock(WorldGenLevel world, BlockPos pos, BlockState state) {
		if (world.getBlockState(pos).isAir() || state.equals(this.getLog())) {
			world.setBlock(pos, state, 3);
		}
	}
}
