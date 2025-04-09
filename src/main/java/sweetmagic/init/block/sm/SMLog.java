package sweetmagic.init.block.sm;

import java.util.Arrays;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraftforge.common.MinecraftForge;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.event.AbstractChopTaskEvent;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.block.base.BaseSMBlock;

public class SMLog extends RotatedPillarBlock {

	public SMLog(String name) {
		super(BaseSMBlock.setState(Material.WOOD).sound(SoundType.WOOD).strength(1F, 8192F));
		BlockInfo.create(this, SweetMagicCore.smTab, name);
	}

	@Override
	public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {

		if (this.isLog(world, pos.above()) && world instanceof ServerLevel && !player.isShiftKeyDown()) {
			MinecraftForge.EVENT_BUS.register(new TreeChopTask(pos.above(), player, 1));
		}

		super.playerWillDestroy(world, pos, state, player);
	}

	public class TreeChopTask extends AbstractChopTaskEvent {

		public TreeChopTask(BlockPos start, Player player, int blockTick) {
			super(start, player, blockTick);
		}

		// ブロックチェック
		public boolean checkBlock(Level world, BlockPos pos) {
			return world.getBlockState(pos).getBlock() instanceof FruitLeaves;
		}
	}

	// 原木チェック
	public boolean isLog(Level world, BlockPos pos) {
		return world.getBlockState(pos).getBlock() instanceof FruitLeaves;
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder build) {
		return Arrays.<ItemStack> asList(new ItemStack(this));
	}
}
