package sweetmagic.init.block.sm;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import sweetmagic.api.util.EnumConect;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.ItemInit;
import sweetmagic.init.block.base.BaseSMBlock;

public class SMDoor3 extends BaseSMBlock {

	private final int data;
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
	public static final EnumProperty<DoorHingeSide> HINGE = BlockStateProperties.DOOR_HINGE;
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	public static final EnumProperty<EnumConect> HALF = EnumProperty.create("vertical", EnumConect.class);
	protected static final float AABB_DOOR_THICKNESS = 3.0F;
	protected static final VoxelShape SOUTH_AABB = Block.box(0D, 0D, 0D, 16D, 16D, 3D);
	protected static final VoxelShape NORTH_AABB = Block.box(0D, 0D, 13D, 16D, 16D, 16D);
	protected static final VoxelShape WEST_AABB = Block.box(13D, 0D, 0D, 16D, 16D, 16D);
	protected static final VoxelShape EAST_AABB = Block.box(0D, 0D, 0D, 3D, 16D, 16D);

	public SMDoor3(String name, int data) {
		super(name, setState(Material.WOOD, SoundType.WOOD, 0.35F, 8192F));
		this.data = data;
		BlockInfo.create(this, null, name);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH)
				.setValue(OPEN, Boolean.valueOf(false)).setValue(HINGE, DoorHingeSide.LEFT)
				.setValue(POWERED, Boolean.valueOf(false)).setValue(HALF, EnumConect.BOT));
	}

	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext con) {
		boolean flag = !state.getValue(OPEN);
		boolean flag1 = state.getValue(HINGE) == DoorHingeSide.RIGHT;
		switch (state.getValue(FACING)) {
		case EAST:
		default: return flag ? EAST_AABB : (flag1 ? NORTH_AABB : SOUTH_AABB);
		case SOUTH: return flag ? SOUTH_AABB : (flag1 ? EAST_AABB : WEST_AABB);
		case WEST: return flag ? WEST_AABB : (flag1 ? SOUTH_AABB : NORTH_AABB);
		case NORTH: return flag ? NORTH_AABB : (flag1 ? WEST_AABB : EAST_AABB);
		}
	}

	public BlockState updateShape(BlockState state1, Direction face, BlockState state2, LevelAccessor world, BlockPos pos1, BlockPos pos2) {
		if (face.getAxis() == Direction.Axis.Y && pos1 != pos2 && !state1.isAir() && !state2.isAir() && state1.getBlock() == this && state2.getBlock() == this) {
			return state1.setValue(FACING, state2.getValue(FACING)).setValue(OPEN, state2.getValue(OPEN)).setValue(HINGE, state2.getValue(HINGE)).setValue(POWERED, state2.getValue(POWERED));
		}

		return state1;
	}

	@Override
	@Deprecated
	public void onRemove(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {

		if (state != newState && newState.isAir()) {
			switch (state.getValue(HALF)) {
			case TOP:
				this.breakDoor(world, pos.below(1));
				this.breakDoor(world, pos.below(2));
				break;
			case CEN:
				this.breakDoor(world, pos.above(1));
				this.breakDoor(world, pos.below(1));
				break;
			case BOT:
				this.breakDoor(world, pos.above(1));
				this.breakDoor(world, pos.above(2));
				break;
			}
		}
	}

	public void breakDoor(Level world, BlockPos pos) {
		if (world.getBlockState(pos).getBlock() == this) {
			world.destroyBlock(pos, false);
			world.removeBlock(pos, false);
		}
	}

	public boolean isPathfindable(BlockState state, BlockGetter world, BlockPos pos, PathComputationType comType) {
		switch (comType) {
		case LAND: return state.getValue(OPEN);
		case WATER: return false;
		case AIR: return state.getValue(OPEN);
		default: return false;
		}
	}

	private int getCloseSound() {
		return this.material == Material.METAL ? 1011 : 1012;
	}

	private int getOpenSound() {
		return this.material == Material.METAL ? 1005 : 1006;
	}

	@Nullable
	public BlockState getStateForPlacement(BlockPlaceContext con) {

		Level world = con.getLevel();
		BlockPos pos = con.getClickedPos();

		if (pos.getY() < world.getMaxBuildHeight() - 1 && world.getBlockState(pos.above()).canBeReplaced(con)) {
			boolean flag = world.hasNeighborSignal(pos) || world.hasNeighborSignal(pos.above());
			return this.defaultBlockState().setValue(FACING, con.getHorizontalDirection()).setValue(HINGE, this.getHinge(con)).setValue(POWERED, Boolean.valueOf(flag)).setValue(OPEN, Boolean.valueOf(flag)).setValue(HALF, EnumConect.BOT);
		}

		return null;
	}

	public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
		world.setBlock(pos.above(1), state.setValue(HALF, EnumConect.CEN), 3);
		world.setBlock(pos.above(2), state.setValue(HALF, EnumConect.TOP), 3);
	}

	private DoorHingeSide getHinge(BlockPlaceContext con) {

		BlockGetter world = con.getLevel();
		BlockPos pos = con.getClickedPos();
		Direction face = con.getHorizontalDirection();

		BlockPos pos1 = pos.above();
		Direction face1 = face.getCounterClockWise();

		BlockPos pos2 = pos.relative(face1);
		BlockState state = world.getBlockState(pos2);

		BlockPos pos3 = pos1.relative(face1);
		BlockState state1 = world.getBlockState(pos3);
		Direction face2 = face.getClockWise();

		BlockPos pos4 = pos.relative(face2);
		BlockState state2 = world.getBlockState(pos4);

		BlockPos pos5 = pos1.relative(face2);
		BlockState state3 = world.getBlockState(pos5);

		int i = (this.isFullBlock(world, state, pos2) ? -1 : 0) + (this.isFullBlock(world, state1, pos3) ? -1 : 0) + (this.isFullBlock(world, state2, pos4) ? 1 : 0) + (this.isFullBlock(world, state3, pos5) ? 1 : 0);
		boolean flag = state.is(this) && state.getValue(HALF).is(EnumConect.BOT);
		boolean flag1 = state2.is(this) && state2.getValue(HALF).is(EnumConect.BOT);

		if ((!flag || flag1) && i <= 0) {

			if ((!flag1 || flag) && i >= 0) {
				int x = face.getStepX();
				int y = face.getStepZ();
				Vec3 vec3 = con.getClickLocation();
				double d0 = vec3.x - (double) pos.getX();
				double d1 = vec3.z - (double) pos.getZ();
				return (x >= 0 || !(d1 < 0.5D)) && (x <= 0 || !(d1 > 0.5D)) && (y >= 0 || !(d0 > 0.5D)) && (y <= 0 || !(d0 < 0.5D)) ? DoorHingeSide.LEFT : DoorHingeSide.RIGHT;
			}

			else {
				return DoorHingeSide.LEFT;
			}
		}

		return DoorHingeSide.RIGHT;
	}

	public boolean isFullBlock (BlockGetter world, BlockState state, BlockPos pos) {
		return state.isCollisionShapeFullBlock(world, pos);
	}

	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
		if (this.material == Material.METAL) { return InteractionResult.PASS; }

		state = state.cycle(OPEN);
		world.setBlock(pos, state, 10);
		world.levelEvent(player, state.getValue(OPEN) ? this.getOpenSound() : this.getCloseSound(), pos, 0);
		world.gameEvent(player, this.isOpen(state) ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
		return InteractionResult.sidedSuccess(world.isClientSide);
	}

	public boolean isOpen(BlockState state) {
		return state.getValue(OPEN);
	}

	public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos pos1, boolean flag0) {

		boolean flag = world.hasNeighborSignal(pos) || world.hasNeighborSignal(pos.relative(state.getValue(HALF).is(EnumConect.BOT) ? Direction.UP : Direction.DOWN));

		if (!this.defaultBlockState().is(block) && flag != state.getValue(POWERED)) {

			if (flag != state.getValue(OPEN)) {
				this.playSound(world, pos, flag);
				world.gameEvent((Entity) null, flag ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
			}

			Direction face = state.getValue(FACING);

			switch (state.getValue(HALF)) {
			case TOP:
				this.setDoor(world, pos.below(1), face, flag);
				this.setDoor(world, pos.below(2), face, flag);
				break;
			case CEN:
				this.setDoor(world, pos.below(1), face, flag);
				this.setDoor(world, pos.above(1), face, flag);
				break;
			case BOT:
				this.setDoor(world, pos.above(1), face, flag);
				this.setDoor(world, pos.above(2), face, flag);
				break;
			}

			world.setBlock(pos, state.setValue(POWERED, flag).setValue(OPEN, flag), 2);
		}
	}

	public void setDoor(Level world, BlockPos pos, Direction face, boolean flag) {
		world.setBlock(pos.below(1), world.getBlockState(pos.below(1)).setValue(POWERED, flag).setValue(OPEN, flag).setValue(FACING, face), 3);
	}

	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
		BlockPos posDown = pos.below();
		BlockState stateDown = world.getBlockState(posDown);
		return state.getValue(HALF).is(EnumConect.BOT) ? stateDown.isFaceSturdy(world, posDown, Direction.UP) : stateDown.is(this);
	}

	private void playSound(Level world, BlockPos pos, boolean flag) {
		world.levelEvent((Player) null, flag ? this.getOpenSound() : this.getCloseSound(), pos, 0);
	}

	public PushReaction getPistonPushReaction(BlockState state) {
		return PushReaction.DESTROY;
	}

	public BlockState rotate(BlockState state, Rotation face) {
		return state.setValue(FACING, face.rotate(state.getValue(FACING)));
	}

	public BlockState mirror(BlockState state, Mirror mirror) {
		return mirror == Mirror.NONE ? state : state.rotate(mirror.getRotation(state.getValue(FACING))).cycle(HINGE);
	}

	public long getSeed(BlockState state, BlockPos pos) {
		return Mth.getSeed(pos.getX(), pos.below(state.getValue(HALF).is(EnumConect.BOT) ? 0 : 1).getY(), pos.getZ());
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> build) {
		build.add(HALF, FACING, OPEN, HINGE, POWERED);
	}

	public static boolean isWoodenDoor(Level world, BlockPos pos) {
		return isWoodenDoor(world.getBlockState(pos));
	}

	public static boolean isWoodenDoor(BlockState state) {
		return state.getBlock() instanceof DoorBlock && (state.getMaterial() == Material.WOOD || state.getMaterial() == Material.NETHER_WOOD);
	}

	public ItemLike getItem() {
		switch (this.data) {
		case 1: return ItemInit.gorgeous_door_w_i;
		default: return ItemInit.gorgeous_door_b_i;
		}
	}

	@Override
	public ItemStack getCloneItemStack(BlockGetter get, BlockPos pos, BlockState state) {
		return new ItemStack(this.getItem());
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder build) {
		List<ItemStack> stackList = new ArrayList<>();

		if (state.getValue(HALF).is(EnumConect.BOT)) {
			stackList.add(new ItemStack(this.getItem()));
		}

		return stackList;
	}
}
