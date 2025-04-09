package sweetmagic.init.block.sm;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.block.base.BaseModelBlock;

public class CeilingLight extends BaseModelBlock {

	private static final BooleanProperty BACK = BooleanProperty.create("back");
	private static final BooleanProperty FORWARD = BooleanProperty.create("forward");
	private static final BooleanProperty LEFT = BooleanProperty.create("left");
	private static final BooleanProperty RIGHT = BooleanProperty.create("right");
	private static final VoxelShape AABB = Block.box(0D, 15D, 0D, 16D, 16D, 16D);

	public CeilingLight(String name) {
		super(name, setState(Material.GLASS, SoundType.GLASS, 0F, 8192F, 15));
		this.registerDefaultState(this.defaultBlockState().setValue(BACK, false).setValue(FORWARD, false).setValue(LEFT, false).setValue(RIGHT, false));
		BlockInfo.create(this, SweetMagicCore.smTab, name);
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext con) {
		return AABB;
	}

	@Nullable
	public BlockState getStateForPlacement(BlockPlaceContext con) {
		return this.setVertical(super.getStateForPlacement(con), con.getLevel(), con.getClickedPos());
	}

	public BlockState updateShape(BlockState state, Direction face, BlockState state2, LevelAccessor world, BlockPos pos1, BlockPos pos2) {
		return this.setVertical(super.updateShape(state, face, state2, world, pos1, pos2) ,world, pos1);
	}

	public BlockState setVertical(BlockState state, LevelAccessor world, BlockPos pos) {
		boolean forward = this.getBlock(world, pos.relative(Direction.NORTH)) == this;
		boolean back = this.getBlock(world, pos.relative(Direction.SOUTH)) == this;
		boolean left = this.getBlock(world, pos.relative(Direction.EAST)) == this;
		boolean right = this.getBlock(world, pos.relative(Direction.WEST)) == this;
		return state.setValue(FORWARD, forward).setValue(BACK, back).setValue(LEFT, left).setValue(RIGHT, right);
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> build) {
		build.add(BACK, FORWARD, LEFT, RIGHT);
	}

	@Override
	public void addBlockTip(List<Component> toolTip) {
		toolTip.add(this.getText("conect").withStyle(GREEN));
	}
}
