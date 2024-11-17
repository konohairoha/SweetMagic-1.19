package sweetmagic.worldgen.tree.featuer;

import java.util.Arrays;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public abstract class AbstractTreeFeatuer extends Feature<NoneFeatureConfiguration> {

	protected static final BlockState AIR = Blocks.AIR.defaultBlockState();
	protected final static List<Direction> ALLFACE = Arrays.<Direction>asList(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);

	public AbstractTreeFeatuer() {
		super(NoneFeatureConfiguration.CODEC);
	}

	public abstract BlockState getLog();

	public abstract BlockState getLeave();

	// 空気
	public void setAir (WorldGenLevel world, BlockPos pos, BlockState state) {
		if (world.getBlockState(pos).getBlock() == state.getBlock()) {
			this.setBlock(world, pos, AIR);
		}
	}

	// 草か土かチェック
	public boolean checkBlock (Block block) {
		return block == Blocks.DIRT || block == Blocks.GRASS_BLOCK;
	}
}
