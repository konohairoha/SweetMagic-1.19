package sweetmagic.init.block.sm;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class WoodenFrameWindow extends SMGlassPane {

	public static final BooleanProperty UP = BooleanProperty.create("up");
	public static final BooleanProperty DOWN = BooleanProperty.create("down");

	public WoodenFrameWindow(String name) {
		super(name, false, false);
		this.registerDefaultState(this.stateDefinition.any().setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false).setValue(WEST, false).setValue(WATERLOGGED, false).setValue(UP, false).setValue(DOWN, false));
	}

	public BlockState getStateForPlacement(BlockPlaceContext con) {
		return this.setVertical(super.getStateForPlacement(con), con.getLevel(), con.getClickedPos());
	}

	public BlockState updateShape(BlockState state, Direction face, BlockState state2, LevelAccessor world, BlockPos pos1, BlockPos pos2) {
		return this.setVertical(super.updateShape(state, face, state2, world, pos1, pos2) ,world, pos1);
	}

	public BlockState setVertical (BlockState state, LevelAccessor world, BlockPos pos) {
		Block upBlock = world.getBlockState(pos.above()).getBlock();
		Block downBlock = world.getBlockState(pos.below()).getBlock();
		return state.setValue(UP, this.isConect(upBlock)).setValue(DOWN, this.isConect(downBlock));
	}

	public boolean isConect (Block block) {
		return block instanceof WoodenFrameWindow;
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> build) {
		super.createBlockStateDefinition(build);
		build.add(UP, DOWN);
	}
}
