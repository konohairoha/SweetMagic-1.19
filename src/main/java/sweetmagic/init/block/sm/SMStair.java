package sweetmagic.init.block.sm;

import java.util.Arrays;
import java.util.List;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit.BlockInfo;

public class SMStair extends StairBlock {

	public SMStair(String name, Block block) {
		super(block.defaultBlockState(), BlockBehaviour.Properties.copy(block));
		BlockInfo.create(this, SweetMagicCore.smTab, name);
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder build) {
		return Arrays.<ItemStack> asList(new ItemStack(this));
	}
}
