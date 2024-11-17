package sweetmagic.init.tile.sm;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import sweetmagic.init.TileInit;

public class TileWallRack extends TileModenRack {

	public TileWallRack(BlockPos pos, BlockState state) {
		super(TileInit.wallRack, pos, state);
	}

	public TileWallRack(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	// インベントリサイズの取得
	@Override
	public int getInvSize () {
		return 3;
	}
}
