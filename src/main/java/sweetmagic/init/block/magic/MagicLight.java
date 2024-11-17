package sweetmagic.init.block.magic;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.iblock.IWaterBlock;
import sweetmagic.api.util.ISMTip;
import sweetmagic.event.HasItemEvent;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.block.base.BaseSMBlock;

public class MagicLight extends BaseSMBlock implements IWaterBlock, ISMTip {

	private static final VoxelShape LIGHT = Block.box(3.2D, 3.2D, 3.2D, 12.8D, 12.8D, 12.8D);
	public final int data;
	private final float enchaPower;

	public MagicLight(String name, int data) {
		super(name, setState(Material.GLASS, SoundType.STONE, 0F, 8192F, 15).noCollission());
		this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, Boolean.valueOf(false)));
		this.data = data;
		BlockInfo.create(this, this.data == 0 ? null : SweetMagicCore.smMagicTab, name);
		this.enchaPower = data == 1 ? 3.75F : 0F;
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext col) {
		return this.data == 1 || HasItemEvent.hasThisItem ? LIGHT : Shapes.empty();
	}

	@Nullable
	public BlockState getStateForPlacement(BlockPlaceContext con) {

		BlockPos pos = con.getClickedPos();
		BlockState state = con.getLevel().getBlockState(pos);

		if (state.is(this)) {
			return state.setValue(WATERLOGGED, Boolean.valueOf(false));
		}

		else {
			FluidState fluid = con.getLevel().getFluidState(pos);
			return this.defaultBlockState().setValue(WATERLOGGED, Boolean.valueOf(fluid.getType() == Fluids.WATER));
		}
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> build) {
		build.add(WATERLOGGED);
	}

	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	public BlockState updateShape(BlockState state, Direction face, BlockState newState, LevelAccessor world, BlockPos pos1, BlockPos pos2) {
		this.setWater(world, state, pos1);
		return super.updateShape(state, face, newState, world, pos1, pos2);
	}

	public boolean isPathfindable(BlockState state, BlockGetter get, BlockPos pos, PathComputationType type) {
		return type == PathComputationType.WATER ? get.getFluidState(pos).is(FluidTags.WATER) : false;
	}

	public float getEnchantPower () {
		return this.enchaPower;
	}

	@Override
	public void addBlockTip (List<Component> toolTip) {
		if (this.data == 0) { return; }
		toolTip.add(this.tierTip(1));
		toolTip.add(this.getText(this.name).withStyle(GREEN));
		super.addBlockTip(toolTip);
	}
}
