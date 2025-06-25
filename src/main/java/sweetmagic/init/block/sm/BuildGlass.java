package sweetmagic.init.block.sm;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import sweetmagic.init.block.base.BaseSMBlock;
import sweetmagic.util.WorldHelper;

public class BuildGlass extends SMGlass {

	private final int data;

	public BuildGlass(String name, int data) {
		super(name, false, false, null, BaseSMBlock.setState(Material.GLASS, SoundType.GLASS, 0.5F, 8192F).isSuffocating((a, b, c) -> false).isViewBlocking((a, b, c) -> false).randomTicks());
		this.data = data;
	}

	public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource rand) {
		world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);

		for (Direction face : Direction.values()) {
			BlockPos facePos = pos.relative(face);
			BlockState faceState = world.getBlockState(facePos);
			if (!faceState.isAir()) {
				faceState.getBlock().tick(faceState, world, facePos, rand);
			}
		}

		if (this.data != 1) { return; }

		int range = 32;
		Iterable<BlockPos> posList = WorldHelper.getRangePos(pos, range);

		for (BlockPos p : posList) {
			if (!world.getFluidState(p).is(Fluids.WATER)) { continue; }

			BlockState state2 = world.getBlockState(p);
			if (state2.hasProperty(BlockStateProperties.WATERLOGGED)) {
				world.setBlock(p, state2.setValue(BlockStateProperties.WATERLOGGED, false), 3);
			}
		}
	}
}
