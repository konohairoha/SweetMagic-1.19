package sweetmagic.init.block.magic;

import java.util.List;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import sweetmagic.init.TileInit;
import sweetmagic.init.block.base.BaseMFBlock;
import sweetmagic.init.tile.sm.TileAetherHopper;

public class AetherHopper extends BaseMFBlock {

	public static final DirectionProperty FACING = BlockStateProperties.FACING;

	public AetherHopper(String name) {
		super(name, false);
		this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.DOWN));
	}

	// ブロックでのアクション
	public boolean actionBlock(Level world, BlockPos pos, Player player, ItemStack stack) {
		if (world.isClientSide) { return false; }
		this.openGUI(world, pos, player, this.getTile(world, pos));
		return true;
	}

	// 最大MFの取得
	public int getMaxMF() {
		return 20000;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileAetherHopper(pos, state);
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return this.createMailBoxTicker(world, type, TileInit.aetherHopper);
	}

	@Override
	protected void createBlockStateDefinition(@NotNull StateDefinition.Builder<Block, BlockState> build) {
		build.add(FACING);
	}

	@NotNull
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext con) {
		return con.getPlayer() != null ? this.defaultBlockState().setValue(FACING, con.getClickedFace().getOpposite()) : this.defaultBlockState();
	}

	@NotNull
	@Override
	@Deprecated
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@NotNull
	@Override
	@Deprecated
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	public void addTip(List<Component> toolTip, ItemStack stack, CompoundTag tags) {
		super.addTip(toolTip, stack, tags);
		toolTip.add(this.getTipArray(this.getText(this.name + "_send"), GREEN));
	}

	// RS信号で停止するかどうか
	public boolean isRSStop() {
		return true;
	}
}
