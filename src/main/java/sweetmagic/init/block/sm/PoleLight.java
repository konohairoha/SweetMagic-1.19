package sweetmagic.init.block.sm;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
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

public class PoleLight extends BaseFaceBlock {

	private final int data;
	private static final VoxelShape AABB = Block.box(5D, 0D, 5D, 11D, 16D, 11D);
	public static final IntegerProperty VERTICAL = IntegerProperty.create("vertical", 0, 4);
	public static final BooleanProperty ISDROP = BooleanProperty.create("isdrop");
	private final static Direction[] ALLFACE = new Direction[] {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

	public PoleLight (String name, int data) {
		super(name, setState(Material.WOOD, SoundType.METAL, 0.5F, 8192F, 15));
		this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH).setValue(VERTICAL, 0).setValue(ISDROP, false));
		this.data = data;
		BlockInfo.create(this, SweetMagicCore.smTab, name);
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext con) {
		return AABB;
	}

	// 右クリック出来るか
	public boolean canRightClick(Player player, ItemStack stack) {
		return !stack.isEmpty();
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {

		boolean canPlace = false;
		boolean canPlaceFace = false;

		for (int i = 0; i < 5; i++) {
			canPlace = world.isEmptyBlock(pos.above(i));
			if(!canPlace) { break; }
		}

		Direction baseFace = state.getValue(FACING);
		List<Direction> faceList = new ArrayList<>();
		if (this.data == 0) {
			faceList.add(baseFace);
		}

		else {
			faceList.add(baseFace.getClockWise());
			faceList.add(baseFace.getCounterClockWise());
		}

		for (Direction face : faceList) {
			canPlaceFace = world.getBlockState(pos.above(4).relative(face)).isAir();
			if(!canPlaceFace) { break; }
		}

		return canPlace && canPlaceFace;
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {

		Direction face = state.getValue(FACING);

		for (int i = 0; i < 4; i++) {

			int data = 0;

			switch (i) {
			case 3:
				data = 2;
				break;
			default:
				data = 1;
				break;
			}

			world.setBlockAndUpdate(pos.above(i + 1), this.defaultBlockState().setValue(VERTICAL, data).setValue(FACING, face));
		}

		Direction face1 = face.getClockWise();
		Direction face2 = face.getCounterClockWise();
		BlockState sta = this.defaultBlockState();

		if (this.data == 1) {

			world.setBlockAndUpdate(pos.above(4).relative(face1), sta.setValue(VERTICAL, 3).setValue(FACING, face2));
			world.setBlockAndUpdate(pos.above(3).relative(face1), sta.setValue(VERTICAL, 4).setValue(FACING, face2));
			world.setBlockAndUpdate(pos.above(4).relative(face2), sta.setValue(VERTICAL, 3).setValue(FACING, face1));
			world.setBlockAndUpdate(pos.above(3).relative(face2), sta.setValue(VERTICAL, 4).setValue(FACING, face1));
		}

		else {
			world.setBlockAndUpdate(pos.above(4).relative(face), sta.setValue(VERTICAL, 2).setValue(FACING, face1.getClockWise()));
			world.setBlockAndUpdate(pos.above(3).relative(face), sta.setValue(VERTICAL, 4).setValue(FACING, face1.getClockWise()));
		}
	}

	@Override
	@Deprecated
	public void onRemove(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {

		// ドロップするブロックが破壊されたらアイテムドロップ
		if (state.getValue(ISDROP) && newState.isAir()) {
			ItemEntity entity = new ItemEntity(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, new ItemStack(this));
			entity.setDefaultPickUpDelay();
			world.addFreshEntity(entity);
		}

		// ブロックの状態が変わった場合
		if (state.getBlock() != newState.getBlock() && !world.isClientSide()) {

			BlockPos upPos = pos.above(1);
			BlockState upState = world.getBlockState(upPos);
			if (upState.getBlock() == this) {
				this.breakBlock(world, upPos);
			}

			BlockPos downPos = pos.below(1);
			BlockState downState = world.getBlockState(downPos);
			if (downState.getBlock() == this) {
				this.breakBlock(world, downPos);
			}

			for (Direction face : ALLFACE) {
				BlockPos facePos = pos.relative(face);
				if (world.getBlockState(facePos).getBlock() == this) {
					this.breakBlock(world, facePos);
				}
			}
		}
	}

	@Nullable
	public BlockState getStateForPlacement(BlockPlaceContext con) {
		return super.getStateForPlacement(con).setValue(VERTICAL, 0).setValue(ISDROP, true);
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> build) {
		build.add(VERTICAL, ISDROP, FACING);
	}

	// ドロップするかどうか
	protected boolean isDrop() {
		return false;
	}
}
