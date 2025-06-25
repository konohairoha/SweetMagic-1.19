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
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.event.AbstractChopTaskEvent;
import sweetmagic.api.util.ISMTip;
import sweetmagic.event.KeyPressEvent;
import sweetmagic.init.ItemInit;
import sweetmagic.key.SMKeybind;
import sweetmagic.util.WorldHelper;

public class SMAxe extends AxeItem implements ISMTip {

	private final int data;

	public SMAxe(String name, int data, int value) {
		super(data == 2 ? Tiers.NETHERITE :Tiers.DIAMOND, 6F + data * 0.5F, -3.1F + data * 0.25F, SMItem.setItem(value, SweetMagicCore.smMagicTab));
		this.data = data;
		ItemInit.itemMap.put(this, name);
	}

	@Override
	public boolean mineBlock(ItemStack stack, Level world, BlockState state, BlockPos pos, LivingEntity living) {

		if (this.checkBlock(world, pos) && !living.isShiftKeyDown() && living instanceof Player player) {
			List<ItemStack> dropList = new ArrayList<>();
			dropList.addAll(Block.getDrops(state, (ServerLevel) world, pos, world.getBlockEntity(pos), player, player.getMainHandItem()));
			world.destroyBlock(pos, false);

			//リストに入れたアイテムをドロップさせる
			WorldHelper.createLootDrop(dropList, world, player.xo, player.yo, player.zo);
			player.getPersistentData().remove("isCancelAxe");
			MinecraftForge.EVENT_BUS.register(new TreeChopTask(pos, player, EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY, stack) + 1 + this.data));
		}

		return super.mineBlock(stack, world, state, pos, living);
	}

	public class TreeChopTask extends AbstractChopTaskEvent {

		protected final List<Direction> allFace = Arrays.asList(Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);

		public TreeChopTask(BlockPos start, Player player, int blockTick) {
			super(start, player, blockTick);
		}

		@SubscribeEvent
		public void chopChop(TickEvent.ServerTickEvent event) {

			// クライアントなら終了
			if (event.side.isClient() || this.player == null ||this.player.getPersistentData().getBoolean("isCancelAxe")) {
				this.player.getPersistentData().remove("isCancelAxe");
				this.finish();
				return;
			}

			int tick = this.blockTick;
			List<ItemStack> dropList = new ArrayList<>();
			boolean isFirst = this.posSet.isEmpty();

			// 見つかるまで回す
			while (tick > 0) {

				// 空なら終了
				if (this.targetblockList.isEmpty()) {
					this.finish();
					return;
				}

				BlockPos pos = this.targetblockList.get(0);
				this.targetblockList.remove(0);

				if (isFirst) {
					this.allFace.forEach(f -> this.checkLog(pos.relative(f)));
					isFirst = false;
					continue;
				}

				if (!this.checkBlock(this.world, pos)) { continue; }

				BlockState state = this.world.getBlockState(pos);

				// クリエイティブ以外ならアイテムドロップ
				if (!this.isCreative) {
					dropList.addAll(Block.getDrops(state, (ServerLevel) this.world, pos, this.world.getBlockEntity(pos), this.player, this.player.getMainHandItem()));
				}

				boolean isLeave = this.checkLeave(this.world, pos);
				this.world.destroyBlock(pos, false);

				if(isLeave) {
					for(int x = -1; x <= 1; x++) for(int y = -1; y <= 1; y++) for(int z = -1; z <= 1; z++)
						this.checkLog(pos.offset(x, y, z));
				}

				else {
					this.allFace.forEach(f -> this.checkLog(pos.relative(f)));
				}

				tick--;
			}

			//リストに入れたアイテムをドロップさせる
			WorldHelper.createLootDrop(dropList, this.world, this.player.xo, this.player.yo, this.player.zo);
		}

		public void checkLog(BlockPos pos) {
			if (!this.posSet.contains(pos) && this.checkBlock(this.world, pos)) {
				this.targetblockList.add(pos);
				this.posSet.add(pos);
			}
		}

		// ブロックチェック
		public boolean checkBlock(Level world, BlockPos pos) {
			BlockState state = world.getBlockState(pos);
			return state.is(BlockTags.LOGS) || state.is(BlockTags.LEAVES);
		}

		public boolean checkLeave(Level world, BlockPos pos) {
			return world.getBlockState(pos).is(BlockTags.LEAVES);
		}
	}

	// ブロックチェック
	public boolean checkBlock(Level world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);
		return state.is(BlockTags.LOGS) || state.is(BlockTags.LEAVES);
	}

	// ツールチップの表示
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> toolTip, TooltipFlag flag) {
		toolTip.add(this.getText("alternative_axe").withStyle(GREEN));

		// キー操作
		Component key = KeyPressEvent.getKeyName(SMKeybind.SPECIAL);
		toolTip.add(this.getTipArray(key.copy(), this.getText("key"), this.getText("alternative_axe_cancel").withStyle(WHITE)).withStyle(GOLD));
	}

	public void cancelAction(Player player) {
		player.getPersistentData().putBoolean("isCancelAxe", true);
		player.sendSystemMessage(this.getText("axe_cancel").withStyle(GREEN));
		player.getLevel().playSound(null, player.blockPosition(), SoundEvents.UI_BUTTON_CLICK, SoundSource.PLAYERS, 0.33F, player.getLevel().getRandom().nextFloat() * 0.1F + 0.9F);
	}

	// アイテム修理
	public boolean isValidRepairItem(ItemStack stack, ItemStack ingot) {
		return ingot.is(ItemInit.alternative_ingot);
	}
}
