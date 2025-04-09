package sweetmagic.init.block.sm;

import java.util.List;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.TileInit;
import sweetmagic.init.block.base.BaseFaceBlock;
import sweetmagic.init.tile.sm.TileFurnitureTable;
import sweetmagic.init.tile.sm.TileStove;

public class FurnitureTable extends BaseFaceBlock implements EntityBlock {

	public static final BooleanProperty ISDROP = BooleanProperty.create("isdrop");

	public FurnitureTable(String name) {
		super(name, setState(Material.WOOD, SoundType.WOOD, 0.5F, 8192F));
		this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(ISDROP, true));
		BlockInit.blockMap.put(new BlockInfo(this, SweetMagicCore.smTab), this.name);
	}

	// 右クリック出来るか
	public boolean canRightClick(Player player, ItemStack stack) {
		return true;
	}

	// ブロックでのアクション
	public boolean actionBlock(Level world, BlockPos pos, Player player, ItemStack stack) {
		if (world.isClientSide) { return true; }

		BlockState state = world.getBlockState(pos);

		if (!state.getValue(ISDROP)) {
			pos = pos.relative(state.getValue(FACING).getClockWise());
		}

		this.openGUI(world, pos, player, (TileFurnitureTable) world.getBlockEntity(pos));
		return true;
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader reader, BlockPos pos) {
		Direction face = state.getValue(FACING).getCounterClockWise();
		return reader.getBlockState(pos.relative(face)).isAir();
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		Direction face = state.getValue(FACING);
		world.setBlock(pos.relative(face.getCounterClockWise()), BlockInit.furniture_processing_table.defaultBlockState().setValue(ISDROP, false).setValue(FACING, face), 2);
	}

	@Override
	@Deprecated
	public void onRemove(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {

		// ドロップするブロックが破壊されたらアイテムドロップ
		if (state.getValue(ISDROP) && newState.isAir()) {

			// ブロックえんちちーを取得
			BlockEntity bEntity = world.getBlockEntity(pos);
			if (bEntity != null && bEntity instanceof TileFurnitureTable tile) {
				this.spawnItem(world, pos, tile.getDropStack(new ItemStack(this)));
				world.removeBlockEntity(pos);
			}
		}

		// ブロックの状態が変わった場合
		if (state.getBlock() != newState.getBlock() && !world.isClientSide) {

			Direction face = state.getValue(FACING);
			BlockPos upPos = pos.relative(state.getValue(ISDROP) ? face.getCounterClockWise() : face.getClockWise());
			BlockState upState = world.getBlockState(upPos);
			if (upState.getBlock() instanceof FurnitureTable pole) {
				this.breakBlock(world, upPos);
				world.removeBlockEntity(upPos);
			}
		}
	}

	@Override
	public void addBlockTip(List<Component> toolTip) {
		toolTip.add(this.getText(this.name).withStyle(GREEN));
		toolTip.add(this.getText(this.name + "_cut").withStyle(GOLD));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return state.getValue(ISDROP) ? new TileFurnitureTable(pos, state) : new TileStove(pos, state);
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		if (!state.getValue(ISDROP)) { return null; }
		return this.createMailBoxTicker(level, type, TileInit.furnitureTable);
	}

	// ドロップするかどうか
	protected boolean isDrop() {
		return false;
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> build) {
		build.add(FACING, ISDROP);
	}
}
