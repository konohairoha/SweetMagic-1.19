package sweetmagic.init.block.sm;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.block.base.BaseFaceBlock;

public class BookShelf extends BaseFaceBlock {

	public static final BooleanProperty ISTOP = BooleanProperty.create("istop");

	public BookShelf(String name) {
		super(name, setState(Material.WOOD, SoundType.WOOD, 0.5F, 8192F));
		this.registerDefaultState(this.setState().setValue(ISTOP, false));
		BlockInfo.create(this, SweetMagicCore.smTab, name);
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
		return world.getBlockState(pos.above()).isAir();
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		world.setBlockAndUpdate(pos.above(), this.setState(state.getValue(FACING)).setValue(ISTOP, true));
	}

	@Override
	@Deprecated
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {

		// ブロックの状態が変わった場合
		if (state.getBlock() != newState.getBlock() && !world.isClientSide) {
			boolean isTop = state.getValue(ISTOP);
			BlockPos targetPos = isTop ? pos.below() : pos.above();
			world.setBlock(targetPos, Blocks.AIR.defaultBlockState(), 3);
			world.destroyBlock(targetPos, false);
			world.removeBlock(targetPos, false);
		}

		super.onRemove(state, world, pos, newState, isMoving);
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> build) {
		build.add(ISTOP, FACING);
	}

	@Override
	public float getEnchantPower () {
		return 1F;
	}
}
