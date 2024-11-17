package sweetmagic.init.block.sm;

import java.util.Arrays;
import java.util.List;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.storage.loot.LootContext;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit.BlockInfo;

public class SMSlab extends SlabBlock {

	public SMSlab (String name, Block block) {
		super(BlockBehaviour.Properties.copy(block));
		BlockInfo.create(this, SweetMagicCore.smTab, name);
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder build) {
		int count = state.getValue(TYPE) == SlabType.DOUBLE ? 2 : 1;
		return Arrays.<ItemStack> asList(new ItemStack(this, count));
	}
}
