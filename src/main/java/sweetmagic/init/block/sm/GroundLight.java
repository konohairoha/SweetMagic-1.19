package sweetmagic.init.block.sm;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class GroundLight extends SMLight {

	private static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.Plane.VERTICAL);
	private final static VoxelShape DOWN = Block.box(4D, 0D, 4D, 12D, 0.00625D, 12D);
	private final static VoxelShape UP = Block.box(4D, 15.99375D, 4D, 12D, 16D, 12D);

	public GroundLight(String name) {
		super(name);
		this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.DOWN));
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext col) {
		return state.getValue(FACING) == Direction.DOWN ? DOWN : UP;
	}

	@Override
	protected void createBlockStateDefinition(@NotNull StateDefinition.Builder<Block, BlockState> build) {
		super.createBlockStateDefinition(build);
		build.add(FACING);
	}

	@NotNull
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext con) {
		if (con.getPlayer() != null) {
			boolean flag = con.getClickLocation().y - (double) con.getClickedPos().getY() > 0.5D;
			return this.defaultBlockState().setValue(FACING, flag ? Direction.UP : Direction.DOWN);
		}
		return this.defaultBlockState();
	}
}
