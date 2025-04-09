package sweetmagic.init.block.sm;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.block.base.BaseModelBlock;
import sweetmagic.util.FaceAABB;

public class CrystalLight extends BaseModelBlock {

	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	private static final VoxelShape UP = Block.box(5D, 0D, 5D, 11D, 6.5D, 11D);
	private static final VoxelShape DOWN = Block.box(5D, 9.5D, 5D, 11D, 16D, 11D);
	private static final VoxelShape[] AABB = FaceAABB.create(5D, 5D, 0D, 11D, 11D, 6.5D);

	public CrystalLight(String name) {
		super(name, setState(Material.WOOD, SoundType.METAL, 0F, 8192F, 15));
		this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
		BlockInfo.create(this, SweetMagicCore.smTab, name);
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

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext con) {
		switch (state.getValue(FACING)) {
		case UP: return DOWN;
		case DOWN: return UP;
		default: return FaceAABB.getAABBUP(AABB, state);
		}
	}
}
