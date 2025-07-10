package sweetmagic.init.tile.sm;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import sweetmagic.init.TileInit;

public class TileTrashCan extends TileWoodChest {

	public TileTrashCan(BlockPos pos, BlockState state) {
		super(TileInit.trashCan, pos, state);
	}

	public TileTrashCan(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	// サーバー側処理
	public void serverTick(Level world, BlockPos pos, BlockState state) {
		super.serverTick(world, pos, state);
		if (this.tickTime % 60 != 0 || !this.isRSPower()) { return; }
		this.invTrash(true);
	}
}
