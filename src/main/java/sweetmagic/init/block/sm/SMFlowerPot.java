package sweetmagic.init.block.sm;

import java.util.Arrays;
import java.util.List;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import sweetmagic.init.BlockInit;
import sweetmagic.init.BlockInit.BlockInfo;

public class SMFlowerPot extends FlowerPotBlock {

	private Block block = null;
	private static final Block POT = Blocks.FLOWER_POT;

	public SMFlowerPot(String name, Block block) {
		super(() -> (FlowerPotBlock) Blocks.FLOWER_POT, () -> block, Block.Properties.copy(Blocks.FLOWER_POT));
		BlockInit.blockMap.put(new BlockInfo(this, null), name);
		this.block = block;
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder build) {
		return Arrays.<ItemStack> asList(new ItemStack(this.block), new ItemStack(POT));
	}

	public String getFlowerName() {
		return ((SMFlower) this.block).name;
	}
}
