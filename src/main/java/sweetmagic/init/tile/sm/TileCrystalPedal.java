package sweetmagic.init.tile.sm;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import sweetmagic.init.TileInit;

public class TileCrystalPedal extends TileAbstractSM {

	public TileCrystalPedal(BlockPos pos, BlockState state) {
		super(TileInit.crystalPedal, pos, state);
	}

	public TileCrystalPedal(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}
}
