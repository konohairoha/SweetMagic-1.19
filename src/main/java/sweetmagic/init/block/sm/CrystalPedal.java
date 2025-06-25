package sweetmagic.init.block.sm;

import java.util.List;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.TileInit;
import sweetmagic.init.block.base.BaseModelBlock;
import sweetmagic.init.tile.sm.TileAbstractSM;
import sweetmagic.init.tile.sm.TileCrystalPedal;
import sweetmagic.init.tile.sm.TileSMSpawnerBoss;
import sweetmagic.init.tile.sm.TileStove;
import sweetmagic.util.WorldHelper;

public class CrystalPedal extends BaseModelBlock implements EntityBlock {

	public static final BooleanProperty TOP = BooleanProperty.create("top");

	public CrystalPedal(String name) {
		super(name, setState(Material.METAL, SoundType.METAL, 3F, 8192F));
		this.registerDefaultState(this.defaultBlockState().setValue(TOP, false));
		BlockInfo.create(this, SweetMagicCore.smTab, name);
	}

	// ドロップするかどうか
	protected boolean isDrop() {
		return false;
	}

	@Override
	protected void createBlockStateDefinition(@NotNull StateDefinition.Builder<Block, BlockState> build) {
		build.add(TOP);
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return state.getValue(TOP) ? RenderShape.ENTITYBLOCK_ANIMATED : super.getRenderShape(state);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return state.getValue(TOP) ? new TileCrystalPedal(pos, state) : new TileStove(pos, state);
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return this.createMailBoxTicker(world, type, state.getValue(TOP) ? TileInit.crystalPedal : TileInit.stove);
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		world.setBlock(pos.above(), BlockInit.crystal_pedal.defaultBlockState().setValue(TOP, !state.getValue(TOP)), 3);
	}

	@Override
	@Deprecated
	public void onRemove(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {

		// ブロックの状態が変わった場合
		if (state.getBlock() != newState.getBlock() && !world.isClientSide()) {
			BlockPos targetPos = state.getValue(TOP) ? pos.below() : pos.above();
			this.breakBlock(world, targetPos);
			Iterable<BlockPos> posList = WorldHelper.getRangePos(targetPos.above(80), -96, 0, -96, 96, 16, 96);

			for (BlockPos p : posList) {
				if(world.isEmptyBlock(p)) { continue; }

				TileAbstractSM tile = this.getTile(world, pos);
				if(tile == null || !(tile instanceof TileSMSpawnerBoss boss)) { continue; }

				boss.breakCrystal += 1;
				this.playerSound(world, pos, SoundEvents.AMETHYST_BLOCK_BREAK, 1F, 1F);
			}
		}

		super.onRemove(state, world, pos, newState, isMoving);
	}

	@Override
	public void addBlockTip(List<Component> toolTip) {
		toolTip.add(this.getText("dungen_only").withStyle(GREEN));
	}
}
