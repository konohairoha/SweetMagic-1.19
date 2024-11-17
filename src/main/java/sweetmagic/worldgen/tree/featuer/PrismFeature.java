package sweetmagic.worldgen.tree.featuer;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import sweetmagic.api.iblock.ISMCrop;
import sweetmagic.init.BlockInit;

public class PrismFeature extends AbstractTreeFeatuer {

	private final int height = 19;
    private final int randHeight = 24;
    private final boolean isSmall;
    private final int data;

	public PrismFeature(int data, boolean isSmall) {
		super();
		this.data = data;
		this.isSmall = isSmall;
	}

	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> con) {

		BlockPos pos = con.origin();
		WorldGenLevel world = con.level();
		if (!this.checkBlock(world.getBlockState(pos.below()).getBlock())) { return false; }

		for (Direction face : ALLFACE) {
			if (!this.checkBlock(world.getBlockState(pos.relative(face).below()).getBlock())) { return false; }
		}

		BlockState log = this.getLog();
		BlockState leave = this.getLeave();
		Random rand = new Random();

		this.setBlock(world, pos, AIR);

		if (this.isSmall) {
			this.setBlock(world, pos, log);
			this.setBlock(world, pos.north(), leave);
			this.setBlock(world, pos.south(), leave);
			this.setBlock(world, pos.east(), leave);
			this.setBlock(world, pos.west(), leave);
			this.setBlock(world, pos.above(), leave);
			return true;
		}

		int maxHeight = rand.nextInt(this.randHeight) + this.randHeight;

		this.subTrunk(world, rand, pos.north(), log);
		this.subTrunk(world, rand, pos.south(), log);
		this.subTrunk(world, rand, pos.west(), log);
		this.subTrunk(world, rand, pos.east(), log);

		for (int y = 0; y < maxHeight; y++) {
			this.setBlock(world, pos.above(y), log);
		}

		for (int x = -2; x <= 2; x++) {
			for (int z = -2; z <= 2; z++) {
				for (int y = this.height; y <= maxHeight - 4; y++) {

					if (rand.nextInt(20) != 0) { continue; }
					this.setleave(world, pos.offset(x, y, z), log, leave);

					boolean isOverX = x == 2 || x == -2;
					boolean isOverZ = z == 2 || z == -2;

					if ( isOverX && isOverZ ) {
						this.setBlock(world, pos.offset(x / 2, y - 1, z / 2), log);
					}

					else if (isOverX) {
						this.setBlock(world, pos.offset(x / 2, y - 1, z), log);
					}

					else if (isOverZ) {
						this.setBlock(world, pos.offset(x, y - 1, z / 2), log);
					}
				}
			}
		}

		BlockPos top = pos.offset(0, maxHeight - 1, 0);

		for (int x = -2; x <= 2; x++) {
			for (int z = -2; z <= 2; z++) {
				this.setBlock(world, top.offset(x, 0, z), leave);
			}
		}

		this.setAir(world, top.offset(-2, 0, -2), leave);
		this.setAir(world, top.offset(2, 0, -2), leave);
		this.setAir(world, top.offset(-2, 0, 2), leave);
		this.setAir(world, top.offset(2, 0, 2), leave);

		this.setBlock(world, top.offset(0, 1, -1), leave);
		this.setBlock(world, top.offset(-1, 1, 0), leave);
		this.setBlock(world, top.offset(0, 1, 1), leave);
		this.setBlock(world, top.offset(1, 1, 0), leave);
		this.setBlock(world, top.above(), leave);
		this.setBlock(world, top.above(2), leave);
		return true;
	}

    // 主軸の横
	public void subTrunk (WorldGenLevel world, Random rand, BlockPos pos, BlockState state) {

		int height = rand.nextInt(6) + 8;

		for (int y = 0; y <= height; y++) {
			this.setBlock(world, pos.above(y), state);
		}
	}

	// 葉っぱ
	public void setleave (WorldGenLevel world, BlockPos pos, BlockState log, BlockState leave) {

		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				this.setBlock(world, pos.offset(x, 0, z), leave);
				this.setBlock(world, pos.offset(x, 1, z), leave);
			}
		}

		this.setAir(world, pos.offset(-1, 1, -1), leave);
		this.setAir(world, pos.offset(1, 1, -1), leave);
		this.setAir(world, pos.offset(-1, 1, 1), leave);
		this.setAir(world, pos.offset(1, 1, 1), leave);
		this.setBlock(world, pos, log);
	}

	// 空気
	public void setAir (WorldGenLevel world, BlockPos pos, BlockState state) {
		if (world.getBlockState(pos).getBlock() == state.getBlock()) {
			this.setBlock(world, pos, AIR);
		}
	}

	protected void setBlock(WorldGenLevel world, BlockPos pos, BlockState state) {
		if (world.getBlockState(pos).isAir() || state.isAir()) {
			world.setBlock(pos, state, 3);
		}
	}

	public BlockState getLog() {
		switch (this.data) {
		case 1:  return BlockInit.magiawood_log.defaultBlockState();
		case 2: return BlockInit.lemon_log.defaultBlockState();
		case 3: return BlockInit.orange_log.defaultBlockState();
		case 4: return BlockInit.estor_log.defaultBlockState();
		case 5: return BlockInit.peach_log.defaultBlockState();
		case 6: return BlockInit.chestnut_log.defaultBlockState();
		default: return BlockInit.prism_log.defaultBlockState();
		}
	}

	public BlockState getLeave() {
		switch (this.data) {
		case 1:  return BlockInit.magiawood_leaves.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true);
		case 2: return BlockInit.lemon_leaves.defaultBlockState().setValue(ISMCrop.AGE2, 0);
		case 3: return BlockInit.orange_leaves.defaultBlockState().setValue(ISMCrop.AGE2, 0);
		case 4: return BlockInit.estor_leaves.defaultBlockState().setValue(ISMCrop.AGE2, 0);
		case 5: return BlockInit.peach_leaves.defaultBlockState().setValue(ISMCrop.AGE2, 0);
		case 6: return BlockInit.chestnut_leaves.defaultBlockState();
		default: return BlockInit.prism_leaves.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true);
		}
	}
}
