package sweetmagic.init.block.magic;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import sweetmagic.init.TileInit;
import sweetmagic.init.block.base.BaseMFBlock;
import sweetmagic.init.tile.sm.TileAetherRecycler;

public class AetherRecycler extends BaseMFBlock {

	public AetherRecycler(String name) {
		super(name);
	}

	// 最大MFの取得
	public int getMaxMF() {
		return 20000;
	}

	@Override
	public int getTier() {
		return 1;
	}

	// RS信号で停止するかどうか
	public boolean isRSStop() {
		return true;
	}

	// ブロックでのアクション
	public boolean actionBlock(Level world, BlockPos pos, Player player, ItemStack stack) {
		if (world.isClientSide()) { return false; }
		this.openGUI(world, pos, player, this.getTile(world, pos));
		return true;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileAetherRecycler(pos, state);
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return this.createMailBoxTicker(world, type, TileInit.aetherRecycler);
	}
}
