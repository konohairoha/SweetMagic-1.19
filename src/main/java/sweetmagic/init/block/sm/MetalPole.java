package sweetmagic.init.block.sm;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.util.EnumVertical;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.block.base.BaseModelBlock;

public class MetalPole extends BaseModelBlock {

	private static final VoxelShape CEN = Block.box(6.5D, 0D, 6.5D, 9.5D, 16D, 9.5D);
	private static final VoxelShape AABB = Block.box(5D, 0D, 5D, 11D, 16D, 11D);
	public static final EnumProperty<EnumVertical> VERTICAL = EnumProperty.create("vertical", EnumVertical.class);

	public MetalPole(String name, int data) {
		super(name, setState(Material.WOOD, data == 1 ? SoundType.METAL : SoundType.WOOD, 0.35F, 8192F));
		this.registerDefaultState(this.defaultBlockState().setValue(VERTICAL, EnumVertical.NOR));
		BlockInfo.create(this, SweetMagicCore.smTab, name);
	}

	// 右クリック出来るか
	public boolean canRightClick(Player player, ItemStack stack) {
		return !stack.isEmpty();
	}

	// ブロックでのアクション
	public boolean actionBlock(Level world, BlockPos pos, Player player, ItemStack stack) {
		if (!(stack.getItem() instanceof BlockItem blockItem) || !this.canSetBlock(blockItem.getBlock())) { return false; }

		if (world.isClientSide) { return true; }
		Block block = blockItem.getBlock();

		for (int i = 1; i < 11; i++) {

			BlockPos targetPos = pos.above(i);
			BlockState state = world.getBlockState(targetPos);
			Block targetBlock = state.getBlock();
			if (!state.isAir() && !this.canSetBlock(targetBlock)) { return false; }
			if (!state.isAir()) { continue; }

			world.setBlock(targetPos, this.setVertical(block.defaultBlockState(), world, targetPos), 3);
			this.blockSound(world, block, targetPos, player);
			if (!player.isCreative()) { stack.shrink(1); }
			break;
		}
		return true;
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext con) {
		return state.getValue(VERTICAL).is(EnumVertical.CEN) ? CEN : AABB;
	}

	@Nullable
	public BlockState getStateForPlacement(BlockPlaceContext con) {
		return this.setVertical(super.getStateForPlacement(con), con.getLevel(), con.getClickedPos());
	}

	public BlockState updateShape(BlockState state, Direction face, BlockState state2, LevelAccessor world, BlockPos pos1, BlockPos pos2) {
		return this.setVertical(super.updateShape(state, face, state2, world, pos1, pos2) ,world, pos1);
	}

	public BlockState setVertical(BlockState state, LevelAccessor world, BlockPos pos) {
		boolean bot = this.getBlock(world, pos.below()) == this;
		boolean top = this.getBlock(world, pos.above()) == this;
		return this.defaultBlockState().setValue(VERTICAL, EnumVertical.getVertical(bot, top));
	}

	public boolean canSetBlock(Block block) {
		return block == this;
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> build) {
		build.add(VERTICAL);
	}

	@Override
	public void addBlockTip(List<Component> toolTip) {
		toolTip.add(this.getText("is_vertical").withStyle(GOLD));
	}
}
