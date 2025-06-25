package sweetmagic.init.block.magic;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
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
import sweetmagic.init.TileInit;
import sweetmagic.init.block.base.BaseMFBlock;
import sweetmagic.init.tile.sm.TileAbstractSM;
import sweetmagic.init.tile.sm.TileAetherLamplight;
import sweetmagic.init.tile.sm.TileAetherLanp;
import sweetmagic.init.tile.sm.TileHightAetherLamplight;

public class AetherLanp extends BaseMFBlock {

	private int data;
	private static final VoxelShape AABB = Block.box(3D, 0D, 3D, 13D, 15.1D, 13D);

	public AetherLanp(String name, int data) {
		super(name);
		this.data = data;
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext con) {
		return AABB;
	}

	/**
	 * 0 = エーテルランタン
	 * 1 = ハイエーテルランタン
	 * 2 = エーテルランプライト
	 */
	// ブロックでのアクション
	public boolean actionBlock(Level world, BlockPos pos, Player player, ItemStack stack) {
		if (world.isClientSide()) { return false; }
		this.openGUI(world, pos, player, this.getTile(world, pos));
		return true;
	}

	public int getData() {
		return this.data;
	}

	// 最大MFの取得
	public int getMaxMF() {
		switch (this.data) {
		case 1:  return 200000;
		case 2:  return 70000;
		default: return 20000;
		}
	}

	@Override
	public int getTier() {
		return this.data == 1 ? 2 : 1;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		switch (this.data) {
		case 1:  return new TileHightAetherLamplight(pos, state);
		case 2:  return new TileAetherLamplight(pos, state);
		default: return new TileAetherLanp(pos, state);
		}
	}

	public BlockEntityType<? extends TileAbstractSM> getTileType() {
		switch (this.data) {
		case 1: return TileInit.hightAetheLamplight;
		case 2: return TileInit.aetheLamplight;
		default: return TileInit.aetherLanp;
		}
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return this.createMailBoxTicker(world, type, this.getTileType());
	}

	// RS信号で停止するかどうか
	public boolean isRSStop() {
		return true;
	}
}
