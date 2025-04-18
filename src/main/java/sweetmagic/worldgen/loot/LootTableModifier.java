package sweetmagic.worldgen.loot;

import java.util.function.Supplier;

import javax.annotation.Nonnull;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;

public class LootTableModifier extends LootModifier {

	public static final Supplier<Codec<LootTableModifier>> CODEC = Suppliers.memoize(() ->
			RecordCodecBuilder.create(inst -> codecStart(inst).and(ResourceLocation.CODEC.fieldOf("lootTable")
					.forGetter((m) -> m.lootTable)).apply(inst, LootTableModifier::new)));

	private final ResourceLocation lootTable;

	protected LootTableModifier(LootItemCondition[] conditions, ResourceLocation lootTable) {
		super(conditions);
		this.lootTable = lootTable;
	}

	@Nonnull
	@Override
	protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> lootList, LootContext con) {
		LootTable loot = con.getLootTable(this.lootTable);
		loot.getRandomItems(con, lootList::add);
		return lootList;
	}

	@Override
	public Codec<? extends IGlobalLootModifier> codec() {
		return CODEC.get();
	}
}
