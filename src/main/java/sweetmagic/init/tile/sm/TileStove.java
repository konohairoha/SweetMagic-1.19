package sweetmagic.init.tile.sm;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import sweetmagic.init.TileInit;

public class TileStove extends TileAbstractSM {

	public TileStove(BlockPos pos, BlockState state) {
		super(TileInit.stove, pos, state);
	}

	public TileStove(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}
}
