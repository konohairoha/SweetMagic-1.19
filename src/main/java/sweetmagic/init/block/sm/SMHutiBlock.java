package sweetmagic.init.block.sm;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public class SMHutiBlock extends SMPlanks {

	private static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.Plane.VERTICAL);

	public SMHutiBlock(String name) {
		super(name);
		this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.DOWN));
	}

	@Override
	protected void createBlockStateDefinition(@NotNull StateDefinition.Builder<Block, BlockState> props) {
		super.createBlockStateDefinition(props);
		props.add(FACING);
	}

	@NotNull
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {

		Player player = ctx.getPlayer();

		if (player != null) {

			boolean flag = ctx.getClickLocation().y - (double) ctx.getClickedPos().getY() > 0.5D;
			flag = player.isShiftKeyDown() ? !flag : flag;
			return this.defaultBlockState().setValue(FACING, flag ? Direction.UP : Direction.DOWN);
		}
		return this.defaultBlockState();
	}
}
