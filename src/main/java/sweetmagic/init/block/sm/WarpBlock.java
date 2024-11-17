package sweetmagic.init.block.sm;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.TileInit;
import sweetmagic.init.block.base.BaseModelBlock;
import sweetmagic.init.tile.sm.TileAbstractSM;
import sweetmagic.init.tile.sm.TileWarp;

public class WarpBlock extends BaseModelBlock implements EntityBlock {

	public WarpBlock (String name) {
		super(name, SweetMagicCore.smMagicTab);
	}

	// 右クリック出来るか
	public boolean canRightClick (Player player, ItemStack stack) {
		return true;
	}

	// ブロックでのアクション
	public void actionBlock(Level world, BlockPos pos, Player player, ItemStack stack) {
		if (world.isClientSide) { return; }
		this.openGUI(world, pos, player, (TileWarp) world.getBlockEntity(pos));
	}

	// tileの中身を保持するか
	public boolean isKeepTile () {
		return true;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileWarp(pos, state);
	}

	public BlockEntityType<? extends TileAbstractSM> getTileType () {
		return TileInit.warpBlock;
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		BlockEntityType<? extends TileAbstractSM> tileType = this.getTileType();
		return tileType != null ? this.createMailBoxTicker(world, type, tileType) : null;
	}

	@Override
	public void addBlockTip (List<Component> toolTip) {
		toolTip.add(this.getText(this.name).withStyle(GREEN));
	}

	// ドロップするかどうか
	protected boolean isDrop () {
		return false;
	}
}
