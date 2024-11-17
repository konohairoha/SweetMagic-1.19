package sweetmagic.init.item.sm;

import java.util.ArrayList;
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

	private final String name;

	public SMAxe(String name, Tiers tier, int speed, float swing, int value) {
		super(tier, speed, swing, SMItem.setItem(value, SweetMagicCore.smMagicTab));
		this.name = name;
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
	        MinecraftForge.EVENT_BUS.register(new TreeChopTask(pos, player, EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY, stack) + 1));
		}

		return super.mineBlock(stack, world, state, pos, living);
	}

    public class TreeChopTask extends AbstractChopTaskEvent {

        public TreeChopTask(BlockPos start, Player player, int blockTick) {
            super(start, player, blockTick);
        }

		@SubscribeEvent
		public void chopChop(TickEvent.ServerTickEvent event) {

			// クライアントなら終了
        	if(event.side.isClient() || this.player == null || player.getPersistentData().getBoolean("isCancelAxe")) {
        		player.getPersistentData().remove("isCancelAxe");
                this.finish();
                return;
            }

            BlockPos pos;
            int left = this.blockTick;
			List<ItemStack> dropList = new ArrayList<>();
            Direction[] allFace = new Direction[] { Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST };

            boolean isFirst = posSet.isEmpty();

            // 見つかるまで回す
            while(left > 0) {

            	// 空なら終了
                if(this.targetblockList.isEmpty()) {
                	this.finish();
                    return;
                }

				pos = this.targetblockList.get(0);
				this.targetblockList.remove(0);

				if (isFirst) {

					for (Direction face : allFace) {
						BlockPos posFace = pos.relative(face);

						// 原木なら
						if (this.checkBlock(this.world, posFace)) {
							this.targetblockList.add(posFace);
							this.posSet.add(posFace);
						}

						if (face == Direction.UP) {
							this.checkLog(pos);
						}
					}

					isFirst = false;
					continue;
				}

                if (!this.checkBlock(this.world, pos)) { continue; }

				BlockState state = world.getBlockState(pos);

				// クリエイティブ以外ならアイテムドロップ
				if (!this.isCreative) {
					dropList.addAll(Block.getDrops(state, (ServerLevel) world, pos, world.getBlockEntity(pos), player, player.getMainHandItem()));
				}

				this.world.destroyBlock(pos, false);

				// 4方向確認
				for (Direction face : allFace) {

                    BlockPos posFace = pos.relative(face);

					// 未チェック領域なら追加
                    this.checkPos(posFace);

                    if (face != Direction.UP) {
                        this.checkPos(posFace.offset(1, 0, 0));
                        this.checkPos(posFace.offset(0, 0, 1));
                        this.checkPos(posFace.offset(-1, 0, 0));
                        this.checkPos(posFace.offset(0, 0, -1));
                    }
                }

				left--;
            }

			//リストに入れたアイテムをドロップさせる
			WorldHelper.createLootDrop(dropList, this.world, this.player.xo, this.player.yo, this.player.zo);
        }

		public void checkLog (BlockPos pos) {

			// 原木なら
			if (this.checkBlock(this.world, pos)) {
				this.targetblockList.add(pos);
				this.posSet.add(pos);
			}
		}

	    // ブロックチェック
	    public boolean checkBlock(Level world, BlockPos pos) {
	    	BlockState state = world.getBlockState(pos);
	    	return state.is(BlockTags.LOGS) || state.is(BlockTags.LEAVES);
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
		toolTip.add(this.getText(this.name).withStyle(GREEN));

		// キー操作
		Component key = KeyPressEvent.getKeyName(SMKeybind.SPECIAL);
		toolTip.add(this.getTipArray(key.copy(), this.getText("key"), this.getText(this.name + "_cancel").withStyle(WHITE)).withStyle(GOLD));
	}

	public void cancelAction (Player player) {
		player.getPersistentData().putBoolean("isCancelAxe", true);
		player.sendSystemMessage(this.getText("axe_cancel").withStyle(GREEN));
		player.level.playSound(null, player.blockPosition(), SoundEvents.UI_BUTTON_CLICK, SoundSource.PLAYERS, 0.33F, player.level.random.nextFloat() * 0.1F + 0.9F);
	}

	// アイテム修理
	public boolean isValidRepairItem(ItemStack stack, ItemStack ingot) {
		return ingot.is(ItemInit.alt_ingot);
	}
}
