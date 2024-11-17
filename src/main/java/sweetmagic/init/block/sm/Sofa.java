package sweetmagic.init.block.sm;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.block.base.BaseFaceBlock;
import sweetmagic.init.entity.block.ChairEntity;
import sweetmagic.util.FaceAABB;

public class Sofa extends BaseFaceBlock {

	private static final VoxelShape[] AABB = FaceAABB.create(0D, 0D, 1D, 16D, 12.5D, 16D);
	public static final IntegerProperty CENTER = IntegerProperty.create("center", 0, 4);
	public static final BooleanProperty LEFT = BooleanProperty.create("left");
	public static final BooleanProperty RIGHT = BooleanProperty.create("right");

	public Sofa (String name) {
		super(name, setState(Material.WOOL, SoundType.WOOL, 0.5F, 8192F));
		this.registerDefaultState(this.setState().setValue(CENTER, 0).setValue(LEFT, false).setValue(RIGHT, false));
		BlockInfo.create(this, SweetMagicCore.smTab, name);
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext col) {
		return FaceAABB.getAABB(AABB, state);
	}

	// 右クリックしない
	public boolean canRightClick (Player player, ItemStack stack) {
		return true;
	}

	// ブロックでのアクション
	public void actionBlock(Level world, BlockPos pos, Player player, ItemStack stack) {
		double y = 0.35D;
		ChairEntity.create(world, pos, y, player, world.getBlockState(pos).getValue(FACING));
	}

	@Nullable
	public BlockState getStateForPlacement(BlockPlaceContext con) {
		return this.setVertical(super.getStateForPlacement(con), con.getLevel(), con.getClickedPos());
	}

	public BlockState updateShape(BlockState state, Direction face, BlockState state2, LevelAccessor world, BlockPos pos1, BlockPos pos2) {
		return this.setVertical(super.updateShape(state, face, state2, world, pos1, pos2) ,world, pos1);
	}

	public BlockState setVertical (BlockState state, LevelAccessor world, BlockPos pos) {

		Direction face = state.getValue(FACING);
		BlockState north = world.getBlockState(pos.north());
		BlockState south = world.getBlockState(pos.south());
		BlockState west = world.getBlockState(pos.west());
		BlockState east = world.getBlockState(pos.east());
		int center = this.checkCenter(world, state, pos);
		state = state.setValue(CENTER, center);

		switch (face) {
		case NORTH: return state.setValue(RIGHT, this.isShowCase(face, west, center)).setValue(LEFT, this.isShowCase(face, east, center));
		case SOUTH: return state.setValue(RIGHT, this.isShowCase(face, east, center)).setValue(LEFT, this.isShowCase(face, west, center));
		case WEST: return state.setValue(RIGHT, this.isShowCase(face, south, center)).setValue(LEFT, this.isShowCase(face, north, center));
		case EAST: return state.setValue(RIGHT, this.isShowCase(face, north, center)).setValue(LEFT, this.isShowCase(face, south, center));
		}

		return state;
	}

	// 両サイドにブロックがあるかどうか
	public int checkCenter (LevelAccessor world, BlockState state, BlockPos pos) {

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

	public int getConnect (BlockState block1, BlockState block2, Direction face) {
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

	// ショーケース
	public boolean isShowCase (Direction face, BlockState state, int center) {
		return state.getBlock() == this && (face == state.getValue(FACING) || face.getClockWise() == state.getValue(FACING) || face.getCounterClockWise() == state.getValue(FACING));
	}

	public boolean isCounter (BlockState state) {
		return state.getBlock() == this;
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> build) {
		build.add(CENTER, LEFT, RIGHT, FACING);
	}

	@Override
	public void addBlockTip (List<Component> toolTip) {
		toolTip.add(this.getText("conect").withStyle(GREEN));
	}
}
