package sweetmagic.init.block.sm;

import java.util.Arrays;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import sweetmagic.init.block.base.BaseSMBlock;

public class BuildGlass extends SMGlass {

	public static final List<Direction> faceList = Arrays.<Direction> asList(
		Direction.NORTH, Direction.WEST, Direction.SOUTH, Direction.EAST
	);

	public BuildGlass(String name) {
		super(name, false, false, null, BaseSMBlock.setState(Material.GLASS, SoundType.GLASS, 0.5F, 8192F).isSuffocating((a, b, c) -> false).isViewBlocking((a, b, c) -> false).randomTicks());
	}

	public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource rand) {
		world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);

		for (Direction face : faceList) {
			BlockPos facePos = pos.relative(face);
			BlockState faceState = world.getBlockState(facePos);
			if (!faceState.isAir()) {
				faceState.getBlock().tick(faceState, world, facePos, rand);
			}
		}
	}
}
