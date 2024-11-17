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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import sweetmagic.api.util.EnumVertical;

public class VerticalGlass extends SMGlass {

	public static final EnumProperty<EnumVertical> VERTICAL = EnumProperty.create("vertical", EnumVertical.class);

	public VerticalGlass(String name) {
		super(name, false, false);
	}

	@Nullable
	public BlockState getStateForPlacement(BlockPlaceContext con) {
		return this.setVertical(con.getLevel(), con.getClickedPos());
	}

	public BlockState setVertical (LevelAccessor world, BlockPos pos) {
		boolean bot = this.getBlock(world, pos.below()) == this;
		boolean top = this.getBlock(world, pos.above()) == this;
		return this.defaultBlockState().setValue(VERTICAL, EnumVertical.getVertical(bot, top));
	}

	public BlockState updateShape(BlockState state, Direction face, BlockState state2, LevelAccessor world, BlockPos pos1, BlockPos pos2) {
		return this.setVertical(world, pos1);
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> build) {
		build.add(VERTICAL);
	}

	@Override
	public void addBlockTip (List<Component> toolTip) {
		toolTip.add(this.getText("is_vertical").withStyle(GOLD));
	}
}
