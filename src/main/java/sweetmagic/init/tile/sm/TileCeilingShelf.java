package sweetmagic.init.tile.sm;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import sweetmagic.init.TileInit;

public class TileCeilingShelf extends TileModenRack {

	public TileCeilingShelf(BlockPos pos, BlockState state) {
		super(TileInit.ceilingShelf, pos, state);
	}

	public TileCeilingShelf(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	// インベントリサイズの取得
	@Override
	public int getInvSize() {
		return 2;
	}
}
