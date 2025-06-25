package sweetmagic.init.item.sm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.TallGrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.util.ISMTip;
import sweetmagic.init.ItemInit;
import sweetmagic.util.WorldHelper;

public class SMHoe extends HoeItem implements ISMTip {

	private List<Block> dirtList = Arrays.asList(Blocks.GRASS_BLOCK, Blocks.DIRT_PATH, Blocks.DIRT, Blocks.COARSE_DIRT);
	private static final BlockState FARM = Blocks.FARMLAND.defaultBlockState().setValue(BlockStateProperties.MOISTURE, 7);

	public SMHoe(String name) {
		super(Tiers.DIAMOND, -3, 0F, SMItem.setItem(1024, SweetMagicCore.smMagicTab));
		ItemInit.itemMap.put(this, name);
	}

	// 右クリック
	@Override
	public InteractionResult useOn(UseOnContext con) {
		Level world = con.getLevel();
		BlockPos pos = con.getClickedPos();

		boolean isFarm = false;
		Iterable<BlockPos> posList = WorldHelper.getRangePos(pos, -1, 0, -1, 1, 0, 1);

		for(BlockPos p : posList) {
			if(this.getHoeState(world, p) == null) { continue; }
			isFarm = true;
			world.setBlock(p, FARM, 3);
		}

		if(isFarm) {
			Player player = con.getPlayer();
			player.getItemInHand(con.getHand()).hurtAndBreak(1, player, e -> e.broadcastBreakEvent(con.getHand()));
			world.playSound(player, pos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1F, 1F);
		}

		else {
			return this.use(world, con.getPlayer(), con.getHand()).getResult();
		}

		return InteractionResult.SUCCESS;
	}

	public BlockState getHoeState(Level world, BlockPos pos) {

		Block block = world.getBlockState(pos).getBlock();

		if (block == Blocks.ROOTED_DIRT) {
			if (world.isClientSide()) {
				Block.popResourceFromFace(world, pos, Direction.UP, new ItemStack(Items.HANGING_ROOTS));
			}

			return Blocks.DIRT.defaultBlockState();
		}

		else if (this.dirtList.contains(block) && world.isEmptyBlock(pos.above())) {
			return block == Blocks.COARSE_DIRT ? Blocks.DIRT.defaultBlockState() : Blocks.FARMLAND.defaultBlockState();
		}

		return null;
	}

	// 右クリック
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		BlockPos p = player.blockPosition();
		List<ItemStack> dropList = new ArrayList<>();
		Iterable<BlockPos> posList = WorldHelper.getRangePos(p, 15);

		for(BlockPos pos : posList) {
			BlockState state = world.getBlockState(pos);
			Block block = state.getBlock();
			if(!this.checkBlock(player, block)) { continue; }

			if (world instanceof ServerLevel server) {
				dropList.addAll(Block.getDrops(state, server, pos, world.getBlockEntity(pos), player, stack));
			}

			// ブロック破壊
			this.breakBlock(world, player, block, state, pos);
		}

		if (!dropList.isEmpty()) {
			stack.hurtAndBreak(1, player, e -> e.broadcastBreakEvent(hand));
			dropList.forEach(s -> world.addFreshEntity(new ItemEntity(world, p.getX(), p.getY(), p.getZ(), s)));
		}

		return InteractionResultHolder.success(stack);
	}

	public boolean checkBlock(Player player, Block block) {
		return player.isShiftKeyDown() ? (block instanceof TallGrassBlock || block instanceof DoublePlantBlock) : (block instanceof TallGrassBlock || block instanceof DoublePlantBlock || block instanceof FlowerBlock);
	}

	// ブロック破壊
	public void breakBlock(Level world, Player player, Block block, BlockState state, BlockPos pos) {
		world.destroyBlock(pos, false);
		world.removeBlock(pos, false);
		player.gameEvent(GameEvent.BLOCK_DESTROY);
	}


	// ツールチップの表示
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> toolTip, TooltipFlag flag) {
		for(int i = 0; i < 3; i++)
			toolTip.add(this.getText("alternative_hoe" + i).withStyle(GREEN));
	}
}
