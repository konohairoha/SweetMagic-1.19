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

public class FlowerBuscket extends BaseModelBlock {

	private static final VoxelShape AABB = Block.box(5D, 0D, 5D, 11D, 16D, 11D);
	public static final EnumProperty<EnumVertical> VERTICAL = EnumProperty.create("vertical", EnumVertical.class);

	public FlowerBuscket(String name) {
		super(name, setState(Material.WOOD, SoundType.GRASS, 0.05F, 8192F));
		this.registerDefaultState(this.defaultBlockState().setValue(VERTICAL, EnumVertical.NOR));
		BlockInfo.create(this, SweetMagicCore.smTab, name);
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext con) {
		return AABB;
	}

	// 右クリック出来るか
	public boolean canRightClick(Player player, ItemStack stack) {
		return player != null && !stack.isEmpty();
	}

	// ブロックでのアクション
	public boolean actionBlock(Level world, BlockPos pos, Player player, ItemStack stack) {
		if (world.isClientSide) { return true; }
		if (!(stack.getItem() instanceof BlockItem blockItem) || !(blockItem.getBlock() instanceof FlowerBuscket) ) { return false; }

		Block block = this.getBlock(stack);
		if (block != this) { return false; }

		for (int i = 1; i < 11; i++) {

			BlockPos targetPos = pos.below(i);
			BlockState state = world.getBlockState(targetPos);
			Block targetBlock = state.getBlock();
			if ( !state.isAir() && !(targetBlock instanceof FlowerBuscket) ) { return false; }
			if (!state.isAir()) { continue; }

			world.setBlock(targetPos, block.defaultBlockState(), 3);
			this.blockSound(world, block, targetPos, player);
			if (!player.isCreative()) { stack.shrink(1); }
			break;
		}
		return true;
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> build) {
		build.add(VERTICAL);
	}

	@Nullable
	public BlockState getStateForPlacement(BlockPlaceContext con) {
		return this.setVertical(con.getLevel(), con.getClickedPos());
	}

	public BlockState setVertical(LevelAccessor world, BlockPos pos) {
		boolean bot = this.getBlock(world, pos.below()) instanceof FlowerBuscket;
		boolean top = this.getBlock(world, pos.above()) instanceof FlowerBuscket;
		return this.defaultBlockState().setValue(VERTICAL, EnumVertical.getVertical(bot, top));
	}

	public BlockState updateShape(BlockState state, Direction face, BlockState state2, LevelAccessor world, BlockPos pos1, BlockPos pos2) {
		return this.setVertical(world, pos1);
	}

	@Override
	public void addBlockTip(List<Component> toolTip) {
		toolTip.add(this.getText("is_vertical").withStyle(GOLD));
	}
}
