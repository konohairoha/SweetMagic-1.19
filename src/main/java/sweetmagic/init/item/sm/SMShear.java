package sweetmagic.init.item.sm;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.util.ISMTip;
import sweetmagic.init.ItemInit;
import sweetmagic.util.SMDamage;

public class SMShear extends ShearsItem implements ISMTip {

	private final String name;
	private static final ItemStack PICK = new ItemStack(Items.DIAMOND_PICKAXE);

	public SMShear(String name) {
		super(SMItem.setItem(1024, SweetMagicCore.smMagicTab));
		this.name = name;
		ItemInit.itemMap.put(this, name);
	}

	// 右クリック
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {

		// アイテムスタックを取得
		ItemStack stack = player.getItemInHand(hand);
		if (player.getCooldowns().isOnCooldown(stack.getItem())) { return InteractionResultHolder.consume(stack); }

		//リストの作成（めっちゃ大事）
		List<ItemStack> dropList = new ArrayList<>();
		BlockPos p = player.blockPosition();
		int range = 15;

		// 範囲の座標取得
		Iterable<BlockPos> posList = BlockPos.betweenClosed(p.offset(-range, -range, -range), p.offset(range, range, range));

		for (BlockPos pos : posList) {

			BlockState state = world.getBlockState(pos);
			Block block = state.getBlock();
			if (state.is(Blocks.AIR) || !state.is(Blocks.AMETHYST_CLUSTER)) { continue; }

			if (world instanceof ServerLevel server) {
				dropList.addAll(Block.getDrops(state, server, pos, world.getBlockEntity(pos), player, PICK));
			}

			// ブロック破壊
			this.breakBlock(world, player, block, state, pos);
		}

		if (!dropList.isEmpty()) {
			dropList.forEach(s -> world.addFreshEntity( new ItemEntity(world, p.getX(), p.getY(), p.getZ(), s)));
            stack.hurtAndBreak(1, player, e -> e.broadcastBreakEvent(hand));
    		world.playSound(player, p, SoundEvents.GROWING_PLANT_CROP, SoundSource.BLOCKS, 1.0F, 1.0F);
		}

		return InteractionResultHolder.consume(stack);
	}

	// ブロック破壊
	public void breakBlock (Level world, Player player, Block block, BlockState state, BlockPos pos) {
		world.destroyBlock(pos, false);
		world.removeBlock(pos, false);
		player.gameEvent(GameEvent.BLOCK_DESTROY);
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {

		if (entity instanceof Chicken && !player.getCooldowns().isOnCooldown(stack.getItem())) {

			if (entity.level.isClientSide) {
				return InteractionResult.SUCCESS;
			}

            stack.hurtAndBreak(1, player, e -> e.broadcastBreakEvent(hand));
            Level world = player.getLevel();
            BlockPos pos = player.blockPosition();
    		ItemEntity item = new ItemEntity(world, pos.getX() + 0.5F, pos.getY() + 0.75F, pos.getZ() + 0.5F, new ItemStack(Items.FEATHER, world.random.nextInt(3) + 1));
    		world.addFreshEntity(item);
    		world.playSound(player, pos, SoundEvents.GROWING_PLANT_CROP, SoundSource.BLOCKS, 1.0F, 1.0F);
    		entity.hurt(SMDamage.magicDamage, 0.5F);
    		player.getCooldowns().addCooldown(stack.getItem(), 5);

			return InteractionResult.SUCCESS;
		}
		return super.interactLivingEntity(stack, player, entity, hand);
	}

	// ツールチップの表示
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> toolTip, TooltipFlag flag) {
		toolTip.add(this.getText(this.name + "_chicken").withStyle(GREEN));
		toolTip.add(this.getText(this.name + "_ameyjust").withStyle(GREEN));
	}

	// アイテム修理
	public boolean isValidRepairItem(ItemStack stack, ItemStack ingot) {
		return ingot.is(ItemInit.alt_ingot);
	}
}
