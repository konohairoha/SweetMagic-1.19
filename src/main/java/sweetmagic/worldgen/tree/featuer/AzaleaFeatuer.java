package sweetmagic.worldgen.tree.featuer;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class AzaleaFeatuer extends AbstractTreeFeatuer {

	public AzaleaFeatuer() {
		super();
	}

	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> con) {

		BlockPos pos = con.origin();
		WorldGenLevel world = con.level();
		if (!this.checkBlock(world.getBlockState(pos.below()).getBlock())) { return false; }

		for (Direction face : ALLFACE) {
			if (!this.checkBlock(world.getBlockState(pos.relative(face).below()).getBlock())) { return false; }
		}

		BlockState log = this.getLog();
		Random rand = new Random();
		boolean isSmall = rand.nextBoolean();

		if (isSmall) {
			this.setBlock(world, pos, log);
			this.setleave(world, pos.above(), rand);
			ALLFACE.forEach(f -> this.setleave(world, pos.relative(f), rand));
		}

		else {
			this.setBlock(world, pos, log);

			for (Direction face : ALLFACE) {
				this.setleave(world, pos.relative(face), rand);
				this.setleave(world, pos.relative(face).above(), rand);
			}

			this.setleave(world, pos.offset(-1, 0, -1), rand);
			this.setleave(world, pos.offset(1, 0, -1), rand);
			this.setleave(world, pos.offset(-1, 0, 1), rand);
			this.setleave(world, pos.offset(1, 0, 1), rand);
		}

		return true;
	}

	// 葉っぱ
	public void setleave(WorldGenLevel world, BlockPos pos, Random rand) {
		this.setBlock(world, pos, rand.nextBoolean() ? this.getLeave() : this.getFlower());
	}

	public BlockState getLog() {
		return Blocks.OAK_LOG.defaultBlockState();
	}

	public BlockState getLeave() {
		return Blocks.AZALEA_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true);
	}

	public BlockState getFlower () {
		return Blocks.FLOWERING_AZALEA_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true);
	}
}
