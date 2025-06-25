package sweetmagic.worldgen.tree.gen;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractTreeGen {

	protected static final BlockState AIR = Blocks.AIR.defaultBlockState();

	protected BlockState log;
	protected BlockState leave;
	protected int growValue;

	public AbstractTreeGen(BlockState log, BlockState leave, int growValue) {
		this.log = log;
		this.leave = leave;
		this.growValue = growValue;

		if (this.leave.getBlock() instanceof LeavesBlock) {
			this.leave = this.leave.hasProperty(LeavesBlock.PERSISTENT) ? this.leave.setValue(LeavesBlock.PERSISTENT, false) : this.leave;
			this.leave = this.leave.hasProperty(LeavesBlock.DISTANCE) ? this.leave.setValue(LeavesBlock.DISTANCE, 1) : this.leave;
		}
	}

	public abstract void generate(Level world, RandomSource rand, BlockPos pos);

	// ブロックの設置
	public void setBlock(Level world, BlockPos pos, BlockState state) {
		if (this.isAir(world, pos)) {
			world.setBlock(pos, state, 3);
		}
	}

	public boolean isAir(Level world, BlockPos pos) {
		return world.isEmptyBlock(pos);
	}

	// 草か土かチェック
	public boolean checkBlock(BlockState state) {
		return state.is(BlockTags.DIRT) || state.is(Blocks.GRASS);
	}
}
