package sweetmagic.init.tile.sm;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import sweetmagic.init.TileInit;

public class TileWoodenToolBox extends TileModenRack {

	public TileWoodenToolBox(BlockPos pos, BlockState state) {
		super(TileInit.woodenToolBox, pos, state);
	}

	public TileWoodenToolBox(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	// インベントリサイズの取得
	@Override
	public int getInvSize() {
		return 4;
	}
}
