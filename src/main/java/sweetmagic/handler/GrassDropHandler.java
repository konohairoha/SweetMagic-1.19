package sweetmagic.handler;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries.Keys;
import net.minecraftforge.registries.RegistryObject;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.ItemInit;

public class GrassDropHandler extends LootModifier {

	private List<Item> seedList = new ArrayList<>();
	public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> REGISTER = DeferredRegister.create(Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, SweetMagicCore.MODID);
	private static final RegistryObject<Codec<GrassDropHandler>> GRASS_DROPS = REGISTER.register("addseed_drops", () -> RecordCodecBuilder.create(i -> codecStart(i).apply(i, GrassDropHandler::new)));

	protected GrassDropHandler(LootItemCondition[] con) {
		super(con);
	}

	public static void init() {
		REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
	}

	@Nonnull
	@Override
	protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> dropList, LootContext con) {
		List<Item> seedList = this.getSeedList();
		dropList.add(new ItemStack(seedList.get(con.getLevel().random.nextInt(seedList.size()))));
		return dropList;
	}

	public List<Item> getSeedList () {
		if (this.seedList.isEmpty()) {
			this.seedList = ItemInit.seedList;
		}
		return this.seedList;
	}

	@Override
	public Codec<? extends IGlobalLootModifier> codec() {
		return GRASS_DROPS.get();
	}
}
