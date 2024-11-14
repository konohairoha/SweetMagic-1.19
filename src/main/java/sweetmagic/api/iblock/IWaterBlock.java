package sweetmagic.api.iblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public interface IWaterBlock extends SimpleWaterloggedBlock {

	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	default boolean placeLiquid(LevelAccessor world, BlockPos pos, BlockState state, FluidState fluid) {
		return SimpleWaterloggedBlock.super.placeLiquid(world, pos, state, fluid);
	}

	default boolean canPlaceLiquid(BlockGetter get, BlockPos pos, BlockState state, Fluid fluid) {
		return SimpleWaterloggedBlock.super.canPlaceLiquid(get, pos, state, fluid);
	}

	default void setWater(LevelAccessor world, BlockState state, BlockPos pos) {
		if (state.getValue(WATERLOGGED)) {
			world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
		}
	}
}
