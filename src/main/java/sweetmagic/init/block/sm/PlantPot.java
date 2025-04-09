package sweetmagic.init.block.sm;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class PlantPot extends PottingSoil {

	private static final BooleanProperty BACK = BooleanProperty.create("back");
	private static final BooleanProperty FORWARD = BooleanProperty.create("forward");
	private static final BooleanProperty LEFT = BooleanProperty.create("left");
	private static final BooleanProperty RIGHT = BooleanProperty.create("right");

	public PlantPot(String name, int chance) {
		super(name, chance);
	}

	@Nullable
	public BlockState getStateForPlacement(BlockPlaceContext con) {
		return this.setVertical(super.getStateForPlacement(con), con.getLevel(), con.getClickedPos());
	}

	public BlockState updateShape(BlockState state, Direction face, BlockState state2, LevelAccessor world, BlockPos pos1, BlockPos pos2) {
		return this.setVertical(super.updateShape(state, face, state2, world, pos1, pos2) ,world, pos1);
	}

	public BlockState setVertical(BlockState state, LevelAccessor world, BlockPos pos) {

		boolean forward = this.isPot(world, pos, Direction.NORTH);
		boolean back = this.isPot(world, pos, Direction.SOUTH);
		boolean left = this.isPot(world, pos, Direction.EAST);
		boolean right = this.isPot(world, pos, Direction.WEST);

		return state.setValue(FORWARD, forward).setValue(BACK, back).setValue(LEFT, left).setValue(RIGHT, right);
	}

	public boolean isPot(LevelAccessor world, BlockPos pos, Direction face) {
		return this.getBlock(world, pos.relative(face)) instanceof PlantPot;
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> build) {
		build.add(BACK, FORWARD, LEFT, RIGHT);
	}

	@Override
	public void addBlockTip(List<Component> toolTip) {
		toolTip.add(this.getText("plantpot").withStyle(GOLD));
	}
}
