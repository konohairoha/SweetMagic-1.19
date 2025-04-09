package sweetmagic.init.block.base;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public abstract class BaseFaceBlock extends BaseModelBlock {

	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

	public BaseFaceBlock(String name, BlockBehaviour.Properties pro) {
		super(name, pro);
	}

	public BlockState setState() {
		return this.setState(Direction.NORTH);
	}

	public BlockState setState(Direction face) {
		return this.defaultBlockState().setValue(FACING, face);
	}

	@Override
	protected void createBlockStateDefinition(@NotNull StateDefinition.Builder<Block, BlockState> buil) {
		super.createBlockStateDefinition(buil);
		buil.add(FACING);
	}

	@NotNull
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext con) {
		return con.getPlayer() != null ? this.setState(con.getPlayer().getDirection().getOpposite()) : this.defaultBlockState();
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
}
