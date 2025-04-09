package sweetmagic.init.block.sm;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import sweetmagic.init.block.base.BaseCookBlock;
import sweetmagic.init.tile.sm.TileStove;

public class Stove extends BaseCookBlock {

	private final boolean isOriginal;

	public Stove(String name) {
		super(name);
		this.isOriginal = true;
	}

	public Stove(String name, CreativeModeTab tab) {
		super(name, tab);
		this.isOriginal = false;
	}

	// 右クリック処理
	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
		if (player == null || result.getDirection() != Direction.UP) { return InteractionResult.PASS; }
		return super.use(state, world, pos, player, hand, result);
	}

	// ブロックでのアクション
	public boolean actionBlock(Level world, BlockPos pos, Player player, ItemStack stack) {
		if (stack.isEmpty() || !( stack.getItem() instanceof BlockItem blockItem ) || !world.getBlockState(pos.above()).isAir()) { return false; }

		Block block = blockItem.getBlock();
		if (!(block instanceof Pot) && !(block instanceof Frypan)) { return false; }
		if (world.isClientSide) { return true; }

		world.setBlock(pos.above(), block.defaultBlockState().setValue(FACING, world.getBlockState(pos).getValue(FACING)), 3);
		this.blockSound(world, block, pos, player);
		if (!player.isCreative()) { stack.shrink(1); }
		return true;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileStove(pos, state);
	}

	@Override
	public void addBlockTip(List<Component> toolTip) {
		toolTip.add(this.getText("stove").withStyle(GREEN));

		if (!this.isOriginal) {
			toolTip.add(this.getText("stove_use").withStyle(GOLD));
		}
	}
}
