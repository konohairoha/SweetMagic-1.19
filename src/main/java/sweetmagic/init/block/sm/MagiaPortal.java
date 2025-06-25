package sweetmagic.init.block.sm;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.DimentionInit;
import sweetmagic.init.block.base.BaseModelBlock;
import sweetmagic.util.DimentionTeleporter;
import sweetmagic.util.FaceAABB;

public class MagiaPortal extends BaseModelBlock {

	private final Block frame;
	public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;
	private static final VoxelShape BOT = Block.box(0D, 6D, 0D, 16D, 10D, 16D);
	protected static final VoxelShape[] AABB = FaceAABB.create(0D, 0D, 6D, 16D, 16D, 10D);
	private final Direction[] faceArrayX = { Direction.NORTH, Direction.SOUTH, Direction.UP, Direction.DOWN };
	private final Direction[] faceArrayZ = { Direction.WEST, Direction.EAST, Direction.UP, Direction.DOWN };
	private final Direction[] faceArrayY = { Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST };

	public MagiaPortal(String name, Block frame) {
		super(name, setState(Material.PORTAL, SoundType.GLASS, -1F, 8192F, 15).noCollission());
		this.frame = frame;
		this.registerDefaultState(this.defaultBlockState().setValue(AXIS, Direction.Axis.X));
		BlockInfo.create(this, null, name);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext con) {
		return FaceAABB.getAABB(AABB, BOT, state);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction face, BlockState state2, LevelAccessor world, BlockPos pos, BlockPos pos2) {
		if (state2.getBlock() == this || !state2.isAir()) { return super.updateShape(state, face, state2, world, pos, pos2); }

		Direction[] faceArray = null;

		switch (state.getValue(AXIS)) {
		case X:
			faceArray = this.faceArrayX;
			break;
		case Z:
			faceArray = this.faceArrayZ;
			break;
		case Y:
			faceArray = this.faceArrayY;
			break;
		}

		for (Direction fa : faceArray) {
			if (!world.isEmptyBlock(pos.relative(fa))) { continue; }
			this.breakBlock(world, pos);
		}

		return super.updateShape(state, face, state2, world, pos, pos2);
	}

	// ドロップするかどうか
	protected boolean isDrop() {
		return false;
	}

	@Override
	public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
		if (entity.isPassenger() || entity.isVehicle() || !entity.canChangeDimensions() || entity.getLevel().isClientSide() || world == null || world.dimension() == null) { return; }

		if (entity.isOnPortalCooldown()) {
			entity.setPortalCooldown();
		}


		if (entity.isOnPortalCooldown() || !(entity instanceof LivingEntity liv)) { return; }

		entity.getLevel().getProfiler().push(world.dimension().location().getPath());
		ResourceKey<Level> smDim = DimentionInit.SweetMagicWorld;
		ResourceKey<Level> key = world.dimension() == smDim ? Level.OVERWORLD : smDim;
		ServerLevel dim = world.getServer().getLevel(key);

		if (dim != null) {
			entity.changeDimension(dim, new DimentionTeleporter(this, this.frame, state.getValue(AXIS), state.getValue(AXIS) == Direction.Axis.Z));
		}
	}

	@Override
	public ItemStack getCloneItemStack(BlockGetter world, BlockPos pos, BlockState state) {
		return ItemStack.EMPTY;
	}

	public BlockState rotate(BlockState state, Rotation rot) {
		return rotatePillar(state, rot);
	}

	public static BlockState rotatePillar(BlockState state, Rotation rot) {
		switch (rot) {
		case COUNTERCLOCKWISE_90:
		case CLOCKWISE_90:
			switch ((Direction.Axis) state.getValue(AXIS)) {
			case X: return state.setValue(AXIS, Direction.Axis.Z);
			case Z: return state.setValue(AXIS, Direction.Axis.X);
			default: return state;
			}
		default: return state;
		}
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> bui) {
		bui.add(AXIS);
	}

	public BlockState getStateForPlacement(BlockPlaceContext con) {
		return this.defaultBlockState().setValue(AXIS, con.getClickedFace().getAxis());
	}
}
