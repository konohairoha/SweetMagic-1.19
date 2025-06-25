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
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import sweetmagic.init.TileInit;
import sweetmagic.init.block.base.BaseMFBlock;
import sweetmagic.init.capability.icap.IWorldData;
import sweetmagic.init.tile.sm.TileMagiaLantern;

public class MagiaLantern extends BaseMFBlock {

	private static final VoxelShape AABB = Block.box(3D, 0D, 3D, 13D, 13.25D, 13D);

	public MagiaLantern(String name) {
		super(name, setState(Material.PISTON, SoundType.METAL, 1F, 8192F, 5));
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext con) {
		return AABB;
	}

	// 最大MFの取得
	public int getMaxMF() {
		return 200000;
	}

	@Override
	public int getTier() {
		return 2;
	}

	// ブロックでのアクション
	public boolean actionBlock(Level world, BlockPos pos, Player player, ItemStack stack) {
		if (world.isClientSide()) { return false; }
		this.openGUI(world, pos, player, this.getTile(world, pos));
		return true;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileMagiaLantern(pos, state);
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return this.createMailBoxTicker(world, type, TileInit.magiaLantern);
	}

	// RS信号で停止するかどうか
	public boolean isRSStop() {
		return true;
	}

	@Override
	public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean moving) {
		super.onPlace(state, world, pos, oldState, moving);
		IWorldData.registerPos(world, pos);
	}

	@Override
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean moving) {
		super.onRemove(state, world, pos, oldState, moving);
		IWorldData.removePos(world, pos);
	}

	public void addTip(List<Component> toolTip, ItemStack stack, CompoundTag tags) {
		toolTip.add(this.getText(this.name).withStyle(GREEN));
		toolTip.add(this.getText(this.name + "_mf").withStyle(GOLD));
		toolTip.add(this.getText(this.name + "_heal").withStyle(GREEN));
	}
}
