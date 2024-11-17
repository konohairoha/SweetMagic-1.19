package sweetmagic.init.tile.sm;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import sweetmagic.init.TileInit;

public class TileFreezerChest extends TileWoodChest {

	public TileFreezerChest(BlockPos pos, BlockState state) {
		super(TileInit.freezerChest, pos, state);
	}

	public TileFreezerChest(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public int getData () {
		return 3;
	}
}
