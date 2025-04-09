package sweetmagic.init.block.sm;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.block.base.BaseModelBlock;

public class WoodPole extends BaseModelBlock {

	private static final VoxelShape AABB = Block.box(6.5D, 0D, 6.5D, 9.5D, 16D, 9.5D);
	public static final IntegerProperty ISTENT = IntegerProperty.create("istent", 0, 2);

	public WoodPole(String name, int data) {
		super(name, setState(Material.WOOD, data == 1 ? SoundType.METAL : SoundType.WOOD, 0.35F, 8192F));
		this.registerDefaultState(this.defaultBlockState().setValue(ISTENT, 0));
		BlockInfo.create(this, SweetMagicCore.smTab, name);
	}

	// 右クリック出来るか
	public boolean canRightClick(Player player, ItemStack stack) {
		return !stack.isEmpty();
	}

	// ブロックでのアクション
	public boolean actionBlock(Level world, BlockPos pos, Player player, ItemStack stack) {
		if (world.isClientSide) { return true; }

		if (stack.getItem() instanceof BlockItem blockItem && this.canSetBlock(blockItem.getBlock())) {

			Block block = blockItem.getBlock();

			for (int i = 1; i < 11; i++) {

				BlockPos targetPos = pos.above(i);
				BlockState state = world.getBlockState(targetPos);
				Block targetBlock = state.getBlock();
				if ( !state.isAir() && !this.canSetBlock(targetBlock) ) { return false; }
				if (!state.isAir()) { continue; }

				world.setBlock(targetPos, this.setVertical(block.defaultBlockState(), world, targetPos), 3);
				this.blockSound(world, block, targetPos, player);
				if (!player.isCreative()) { stack.shrink(1); }
				break;
			}
		}
		return true;
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

		BlockState upState = world.getBlockState(pos.above());
		if (upState.getBlock() instanceof AwningTent) {
			return state.setValue(ISTENT, upState.getValue(AwningTent.CENTER) == 5 ? 1 : 2);
		}

		return state.hasProperty(ISTENT) ? state.setValue(ISTENT, 0) : state;
	}

	public boolean canSetBlock(Block block) {
		return block == this || block instanceof AwningTent;
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> build) {
		build.add(ISTENT);
	}
}
