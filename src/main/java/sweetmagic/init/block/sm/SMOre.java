package sweetmagic.init.block.sm;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.ItemInit;
import sweetmagic.init.block.base.BaseSMBlock;

public class SMOre extends DropExperienceBlock {

	private final int data;
	private final boolean isDeep;

	public SMOre(String name, int data, boolean isDeep) {
		super(BaseSMBlock.setState(Material.STONE).requiresCorrectToolForDrops().strength(3F, 8192F).lightLevel((l) -> data <= 1 ? 5 : 0), UniformInt.of(4 + data, 8 + data));
		this.data = data;
		this.isDeep = isDeep;
		BlockInfo.create(this, SweetMagicCore.smMagicTab, name);
	}

	// ドロップアイテムの取得
	public ItemLike getDrop() {
		switch (this.data) {
		case 1: return ItemInit.cosmic_crystal_shard;
		case 2: return ItemInit.fluorite;
		case 3: return ItemInit.redberyl;
		default: return ItemInit.aether_crystal;
		}
	}

	public int quantityDropped(RandomSource rand) {
		switch(this.data) {
		case 1:	 return this.isDeep ? rand.nextInt(4) + 3 : rand.nextInt(3) + 2;
		case 2:	 return this.isDeep ? rand.nextInt(2) + 1 : 1;
		case 3:	 return this.isDeep ? rand.nextInt(2) + 1 : 1;
		default: return this.isDeep ? rand.nextInt(3) + 2 : rand.nextInt(2) + 1;
		}
	}

	// シルクタッチでのドロップ
	public ItemStack getSilkDrop() {
		return new ItemStack(this);
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder build) {

		RandomSource rand = build.getLevel().random;
		List<ItemStack> stackList = new ArrayList<ItemStack>();
		ItemStack stack = build.getOptionalParameter(LootContextParams.TOOL);

		// シルクタッチの場合はそのままドロップ
		if (!stack.isEmpty() && stack.getEnchantmentLevel(Enchantments.SILK_TOUCH) > 0) {
			stackList.add(this.getSilkDrop());
			return stackList;
		}

		if (!stack.isEmpty()) {
			int level = stack.getEnchantmentLevel(Enchantments.BLOCK_FORTUNE);
            int addValue = Math.max(0, rand.nextInt(level + 2) - 1) + 1;
			stackList.add(new ItemStack(this.getDrop(), this.quantityDropped(rand) * addValue));
			return stackList;
		}

		else {
			stackList.add(new ItemStack(this.getDrop(), this.quantityDropped(rand)));
			return stackList;
		}
	}
}
