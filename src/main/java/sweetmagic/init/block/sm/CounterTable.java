package sweetmagic.init.block.sm;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.iblock.IFoodExpBlock;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.block.base.BaseFaceBlock;

public class CounterTable extends BaseFaceBlock implements IFoodExpBlock {

	public static final IntegerProperty CENTER = IntegerProperty.create("center", 0, 4);

	public CounterTable(String name) {
		super(name, setState(Material.STONE, SoundType.STONE, 0.5F, 8192F));
		this.registerDefaultState(this.setState().setValue(CENTER, 0));
		BlockInfo.create(this, SweetMagicCore.smTab, name);
	}

	@Nullable
	public BlockState getStateForPlacement(BlockPlaceContext con) {
		return this.setVertical(super.getStateForPlacement(con), con.getLevel(), con.getClickedPos());
	}

	public BlockState updateShape(BlockState state, Direction face, BlockState state2, LevelAccessor world, BlockPos pos1, BlockPos pos2) {
		return this.setVertical(super.updateShape(state, face, state2, world, pos1, pos2) ,world, pos1);
	}

	public BlockState setVertical(BlockState state, LevelAccessor world, BlockPos pos) {
		return state.setValue(CENTER, this.checkCenter(world, state, pos));
	}

	// 両サイドにブロックがあるかどうか
	public int checkCenter(LevelAccessor world, BlockState state, BlockPos pos) {

		Direction face = state.getValue(FACING);
		BlockState north = world.getBlockState(pos.north());
		BlockState south = world.getBlockState(pos.south());
		BlockState west = world.getBlockState(pos.west());
		BlockState east = world.getBlockState(pos.east());

		switch (face) {
		case NORTH: return this.getConnect(north, south, face);
		case SOUTH: return this.getConnect(south, north, face);
		case WEST: return this.getConnect(west, east, face);
		case EAST: return this.getConnect(east, west, face);
		}

		return 0;
	}

	public int getConnect(BlockState block1, BlockState block2, Direction face) {
		if (this.canConnectBlock(block2, face.getClockWise())) { return 3; }
		if (this.canConnectBlock(block2, face.getCounterClockWise())) { return 4; }
		if (this.canConnectBlock(block1, face.getClockWise())) { return 1; }
		if (this.canConnectBlock(block1, face.getCounterClockWise())) { return 2; }
		return 0;
	}

	// 繋がるかのチェック
	public boolean canConnectBlock(BlockState state, Direction face) {
		return this.isCounter(state) && face == state.getValue(FACING);
	}

	public boolean isCounter(BlockState state) {
		Block block = state.getBlock();
		return block instanceof CounterTable || block instanceof CounterTableSink || block instanceof Oven || block instanceof Stove || block instanceof WoodChest;
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> build) {
		build.add(CENTER, FACING);
	}
}
