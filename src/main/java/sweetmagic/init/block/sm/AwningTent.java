package sweetmagic.init.block.sm;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.block.base.BaseFaceBlock;

public class AwningTent extends BaseFaceBlock {

	private static final VoxelShape AABB = Block.box(0D, 0D, 0D, 16D, 8D, 16D);
	public static final IntegerProperty CENTER = IntegerProperty.create("center", 0, 5);

	public AwningTent(String name) {
		super(name, setState(Material.WOOD, SoundType.WOOL, 0.35F, 8192F));
		this.registerDefaultState(this.setState().setValue(CENTER, 0));
		BlockInfo.create(this, SweetMagicCore.smTab, name);
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext col) {
		return AABB;
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

		if (this.isAwning(north, south, west, east)) { return 5; }
		if (this.isCenter(north, south, west, east)) { return 0; }

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

	public boolean isCenter(BlockState... stateArray) {
		int awningValue = 0;
		int fullValue = 0;
		for (BlockState state : stateArray) {
			if (state.canOcclude()) { fullValue++; }
			else if (this.isAwning(state)) { awningValue++; }
		}
		return awningValue == 3 || ( awningValue >= 2 && fullValue == 1 );
	}

	// 繋がるかのチェック
	public boolean canConnectBlock(BlockState state, Direction face) {
		return this.isAwning(state) && face == state.getValue(FACING);
	}

	public boolean isAwning(BlockState state) {
		return state.getBlock() instanceof AwningTent;
	}

	public boolean isAwning(BlockState... stateArray) {

		int awningValue = 0;
		int fullValue = 0;

		for (BlockState state : stateArray) {
			if (state.isAir()) { return false; }
			else if (this.isAwning(state)) { awningValue++; }
		}
		return awningValue >= 3 && fullValue <= 1;
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> build) {
		build.add(CENTER, FACING);
	}

	@Override
	public void addBlockTip(List<Component> toolTip) {
		toolTip.add(this.getText("awning_tent").withStyle(GREEN));
	}
}
