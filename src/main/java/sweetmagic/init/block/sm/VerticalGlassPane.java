package sweetmagic.init.block.sm;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import sweetmagic.api.util.EnumVertical;

public class VerticalGlassPane extends SMGlassPane {

	public static final EnumProperty<EnumVertical> VERTICAL = EnumProperty.create("vertical", EnumVertical.class);

	public VerticalGlassPane(String name) {
		super(name, false, false);
	}

	@Nullable
	public BlockState getStateForPlacement(BlockPlaceContext con) {
		return this.setVertical(super.getStateForPlacement(con), con.getLevel(), con.getClickedPos());
	}

	public BlockState setVertical(BlockState state, LevelAccessor world, BlockPos pos) {
		boolean bot = world.getBlockState(pos.below()).getBlock() == this;
		boolean top = world.getBlockState(pos.above()).getBlock() == this;
		return state.setValue(VERTICAL, EnumVertical.getVertical(bot, top));
	}

	public BlockState updateShape(BlockState state, Direction face, BlockState state2, LevelAccessor world, BlockPos pos1, BlockPos pos2) {
		return this.setVertical(super.updateShape(state, face, state2, world, pos1, pos2) ,world, pos1);
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> build) {
		build.add(VERTICAL, NORTH, EAST, WEST, SOUTH, WATERLOGGED);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter get, List<Component> toolTip, TooltipFlag flag) {
		toolTip.add(this.getText("is_vertical").withStyle(GOLD));
	}
}
