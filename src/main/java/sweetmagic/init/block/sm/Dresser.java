package sweetmagic.init.block.sm;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.block.base.BaseFaceBlock;
import sweetmagic.init.block.base.BaseSMBlock;
import sweetmagic.init.tile.sm.TileAbstractSM;
import sweetmagic.init.tile.sm.TileDresser;
import sweetmagic.init.tile.sm.TileStove;
import sweetmagic.util.FaceAABB;

public class Dresser extends BaseFaceBlock implements EntityBlock {

	private static final BooleanProperty ISTOP = BooleanProperty.create("top");
	private static final VoxelShape BOT = Block.box(0D, 0D, 0D, 16D, 16D, 16D);
	private static final VoxelShape[] TOP = FaceAABB.create(2D, 0D, 15D, 14D, 14D, 16D);

	public Dresser(String name) {
		super(name, setState(Material.WOOD, SoundType.WOOD, 0.5F, 8192F));
		this.registerDefaultState(this.setState().setValue(ISTOP, false));
		BlockInfo.create(this, SweetMagicCore.smMagicTab, name);
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext con) {
		return state.getValue(ISTOP) ? FaceAABB.getAABB(TOP, state) : BOT;
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> build) {
		build.add(ISTOP, FACING);
	}

	// 右クリック出来るか
	public boolean canRightClick(Player player, ItemStack stack) {
		return true;
	}

	// ブロックでのアクション
	public boolean actionBlock(Level world, BlockPos pos, Player player, ItemStack stack) {
		if (world.isClientSide) { return true; }
		pos = world.getBlockState(pos).getValue(ISTOP) ? pos.below() : pos;

		BlockEntity tile = this.getTile(world, pos);

		if (tile != null && tile instanceof TileDresser dre) {
			this.openGUI(world, pos, player, dre);
		}

		else if (this.getBlock(world, pos) instanceof BaseSMBlock sm) {
			sm.actionBlock(world, pos, player, stack);
		}

		this.playerSound(world, pos, SoundEvents.BARREL_OPEN, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
		return true;
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
		return world.getBlockState(pos.above()).isAir();
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		world.setBlockAndUpdate(pos.above(), this.defaultBlockState().setValue(FACING, world.getBlockState(pos).getValue(FACING)).setValue(ISTOP, true));
	}

	@Override
	public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {

		BlockPos targetPos = state.getValue(ISTOP) ? pos.below() : pos.above();
		BlockState upState = world.getBlockState(targetPos);

		if (upState.getBlock() instanceof Dresser) {
			world.setBlock(targetPos, Blocks.AIR.defaultBlockState(), 35);
			world.levelEvent(player, 2001, targetPos, Block.getId(upState));
		}

		super.playerWillDestroy(world, pos, state, player);
	}

	public void onRemove(Level world, BlockPos pos, BlockState state, TileAbstractSM tile) {
		if (state.getValue(ISTOP)) { return; }
		this.spawnItem(world, pos, tile.getDropStack(new ItemStack(this)));
	}

	// ドロップするかどうか
	protected boolean isDrop() {
		return false;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return state.getValue(ISTOP) ? new TileStove(pos, state) : new TileDresser(pos, state);
	}

	@Override
	public void addBlockTip(List<Component> toolTip) {
		toolTip.add(this.getText(this.name).withStyle(GREEN));
	}
}
