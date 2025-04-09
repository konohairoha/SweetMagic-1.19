package sweetmagic.init.block.sm;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraftforge.common.MinecraftForge;
import sweetmagic.init.BlockInit;
import sweetmagic.init.ItemInit;
import sweetmagic.init.TileInit;
import sweetmagic.init.tile.sm.TileAbstractSM;
import sweetmagic.init.tile.sm.TileMagicBarrier;

public class MagicBarrierGlassLock extends MagicBarrierGlass implements EntityBlock {

	public MagicBarrierGlassLock(String name) {
		super(name);
	}

	// 右クリック出来るか
	public boolean canRightClick(Player player, ItemStack stack) {
		return player != null && !stack.isEmpty() && !(stack.getItem() instanceof BlockItem);
	}

	// ブロックでのアクション
	public boolean actionBlock(Level world, BlockPos pos, Player player, ItemStack stack) {

		if (stack.is(ItemInit.creative_wand) && !world.isClientSide) {
			this.openGUI(world, pos, player, (TileMagicBarrier) this.getTile(world, pos));
		}

		if (!stack.is(ItemInit.magickey)) { return false; }

		if (!player.isCreative()) {
			stack.shrink(1);
		}

		((TileMagicBarrier) this.getTile(world, pos)).removeDestructive(world);

		if (world instanceof ServerLevel) {
			MinecraftForge.EVENT_BUS.register(new GlassChopTask(pos, player, 1));
		}
		return true;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileMagicBarrier(pos, state);
	}

	public BlockEntityType<? extends TileAbstractSM> getTileType() {
		return TileInit.barrierGlass;
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return this.createMailBoxTicker(world, type, this.getTileType());
	}

	@Override
	public void addBlockTip(List<Component> toolTip) {
		toolTip.add(this.getText("dungen_only").withStyle(GREEN));
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder build) {
		return Arrays.<ItemStack> asList(new ItemStack(BlockInit.magicbarrier));
	}
}
