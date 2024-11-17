package sweetmagic.init.block.magic;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import sweetmagic.init.ItemInit;
import sweetmagic.init.TileInit;
import sweetmagic.init.block.base.BaseMFBlock;
import sweetmagic.init.tile.sm.TileAbstractSM;
import sweetmagic.init.tile.sm.TileMFFisher;

public class MFFisher extends BaseMFBlock {

	private final int data;
	private static final VoxelShape AABB = Block.box(2D, 0D, 2D, 14D, 6D, 14D);

	public MFFisher(String name, int data) {
		super(name);
		this.data = data;
	}

	/**
	 * 0 =
	 * 1 = フォーアラー
	 * 2 =
	 */

	// 最大MFの取得
	public int getMaxMF () {
		switch(this.data) {
		case 3: return 40000;
		case 4: return 400000;
		default : return 10000;
		}
	}

	@Override
	public int getTier() {
		switch(this.data) {
		case 4: return 2;
		default : return 1;
		}
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext col) {
		return this.data == 3 || this.data == 4 ? AABB : super.getShape(state, get, pos, col);
	}

	// ブロックでのアクション
	public void actionBlock(Level world, BlockPos pos, Player player, ItemStack stack) {
		if (world.isClientSide) { return; }
		this.openGUI(world, pos, player, (TileMFFisher) world.getBlockEntity(pos));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileMFFisher(pos, state);
	}

	public BlockEntityType<? extends TileAbstractSM> getTileType () {
		return TileInit.mfFisher;
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		BlockEntityType<? extends TileAbstractSM> tileType = this.getTileType();
		return tileType != null ? this.createMailBoxTicker(world, type, tileType) : null;
	}

	public int getData () {
		return this.data;
	}

	public void addTip (List<Component> toolTip, ItemStack stack, CompoundTag tags) {

		if (this.data == 3 || this.data == 4) {
			toolTip.add(this.getTipArray(this.getText("aehter_furnace"), GREEN));
			toolTip.add(this.data == 3 ? new ItemStack(ItemInit.aether_crystal).getDisplayName() : new ItemStack(ItemInit.divine_crystal).getDisplayName());
			return;
		}

		toolTip.add(this.getTipArray(this.getText(this.name), GREEN));

		if (this.data == 2) {
			toolTip.add(this.getTipArray(this.getText(this.name + "_mf"), GREEN));
		}
	}

	// RS信号で停止するかどうか
	public boolean isRSStop () {
		return true;
	}
}
