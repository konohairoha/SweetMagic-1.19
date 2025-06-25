package sweetmagic.init.block.sm;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.PathComputationType;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.iblock.IWaterBlock;
import sweetmagic.init.BlockInit;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.block.base.BaseFaceBlock;
import sweetmagic.init.tile.sm.TileAbstractSM;
import sweetmagic.init.tile.sm.TileAetherCraftTable;

public class AetherCraftTable extends BaseFaceBlock implements EntityBlock, IWaterBlock {

	public AetherCraftTable(String name) {
		super(name, setState(Material.WOOD, SoundType.WOOD, 0.5F, 8192F));
		this.registerDefaultState(this.setState().setValue(WATERLOGGED, false));
		BlockInfo.create(this, SweetMagicCore.smTab, name);
	}


	// 右クリック出来るか
	public boolean canRightClick(Player player, ItemStack stack) {
		return true;
	}

	// ブロックでのアクション
	public boolean actionBlock(Level world, BlockPos pos, Player player, ItemStack stack) {
		if (world.isClientSide()) { return true; }
		this.openGUI(world, pos, player, this.getTile(world, pos));
		return true;
	}

	// ドロップするかどうか
	protected boolean isDrop() {
		return false;
	}

	// tileの中身を保持するか
	public boolean isKeepTile() {
		return true;
	}

	public ItemStack getDropStack(TileAbstractSM tile) {
		return tile.getDropStack(new ItemStack(this));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileAetherCraftTable(pos, state);
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> build) {
		build.add(FACING, WATERLOGGED);
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

	@Override
	public void addBlockTip(List<Component> toolTip) {
		toolTip.add(this.getText(this.name, BlockInit.chest_reader.getName().getString(), 4).withStyle(GREEN));
		toolTip.add(this.getText(this.name + "_chest").withStyle(GOLD));
		toolTip.add(this.getText(this.name + "_craft").withStyle(GOLD));
	}
}
