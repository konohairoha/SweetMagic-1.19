package sweetmagic.init.block.sm;

import java.util.Arrays;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.ItemInit;
import sweetmagic.init.block.base.BaseFaceBlock;

public class CounterTableSink extends BaseFaceBlock {

	public CounterTableSink(String name) {
		super(name, BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.WOOD).sound(SoundType.WOOD).strength(0.5F, 8192.0F).noOcclusion());
		this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
		BlockInit.blockMap.put(new BlockInfo(this, SweetMagicCore.smTab), name);
	}

	// 右クリック出来るか
	public boolean canRightClick (Player player, ItemStack stack) {
		return true;
	}

	// ブロックでのアクション
	public void actionBlock (Level world, BlockPos pos, Player player, ItemStack stack) {
		if (world.isClientSide) { return; }

		this.playerSound(world, pos, SoundEvents.BUCKET_FILL, 0.2F, world.random.nextFloat() * 0.1F + 1.4F);

		if (stack.is(Items.BUCKET)) {
			stack.shrink(1);
			this.spawnItemList(world, player.blockPosition(), Arrays.<ItemStack> asList(new ItemStack(Items.WATER_BUCKET)));
		}

		else {
			this.spawnItemList(world, player.blockPosition(), Arrays.<ItemStack> asList(new ItemStack(ItemInit.watercup, 16)));
		}
	}

	@Override
	public void addBlockTip (List<Component> toolTip) {
		toolTip.add(this.getText("couter_table_sink_bucket").withStyle(GOLD));
		toolTip.add(this.getText("couter_table_sink").withStyle(GOLD));
	}
}
