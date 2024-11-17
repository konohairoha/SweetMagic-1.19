package sweetmagic.init;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.AlternativeLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LocationCheck;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import sweetmagic.SweetMagicCore;

@Mod.EventBusSubscriber(modid = SweetMagicCore.MODID)
public class LootInit {

	public static List<ResourceLocation> lootList = new ArrayList<>();

	public static void init () {
		setLoot("biginer/biginer_chest");
		setLoot("witchhouse/witchhouse_chest_0");
		setLoot("witchhouse/witchhouse_chest_1");
		setLoot("skyland/skyland_chest_0");
		setLoot("skyland/skyland_chest_1");
		setLoot("skyland/skyland_old");
		setLoot("tomb/tomb_chest_0");
		setLoot("tomb/tomb_chest_1");
		setLoot("tomb/tomb_chest_2");
		setLoot("submarine_ruins/submarine_ruins");
		setLoot("ruins_site/ruins_site_light");
		setLoot("ruins_site/ruins_site_fire");
		setLoot("well/well_0");
		setLoot("well/well_1");
		setLoot("witch_tower/witch_tower_0");
		setLoot("witch_tower/witch_tower_1");
		setLoot("witch_tower/witch_tower_2");
		setLoot("labyrinth/labyrinth_0");
		setLoot("labyrinth/labyrinth_1");
		setLoot("labyrinth/labyrinth_2");
		setLoot("desert_mine/mine_0");
		setLoot("desert_mine/mine_1");
		setLoot("desert_mine/mine_2");
		setLoot("ruins_site/ruins_site_wind_0");
		setLoot("ruins_site/ruins_site_wind_1");
		setLoot("ruins_site/ruins_site_wind_2");
		setLoot("arena/arena_0");
		setLoot("arena/arena_1");
		setLoot("witch_large_house/chest_0");
		setLoot("witch_large_house/chest_1");
		setLoot("witch_large_house/chest_2");
		setLoot("well_old/well_0");
		setLoot("well_old/well_1");
		setLoot("well_old/well_2");
		setLoot("well_old/well_0");
		setLoot("well_old/well_1");
		setLoot("white_silver_house/house_0");
		setLoot("white_silver_house/house_1");
		setLoot("white_silver_house/house_2");
	}

	public static void setLoot (String name) {
		LootInit.lootList.add(SweetMagicCore.getSRC("chests/" + name));
	}

	@SubscribeEvent
	public static void onLootTableLoad(LootTableLoadEvent event) {

		ResourceLocation name = event.getName();

		// 大量のルートテーブルかチェック
		if (LootInit.is(name, BuiltInLootTables.FISHING_FISH, BuiltInLootTables.FISHING, BuiltInLootTables.FISHING_JUNK)) {

        	// 対象バイオームの取得
			AlternativeLootItemCondition.Builder alt = LootInit.getAlt(Biomes.RIVER, Biomes.OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.DEEP_OCEAN, Biomes.FROZEN_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.WARM_OCEAN);
        	addLootTable(event.getTable(), LootInit.getLoot(ItemInit.shrimp, 9, alt));	// 釣れるアイテムの設定
        	addLootTable(event.getTable(), LootInit.getLoot(ItemInit.seaweed, 7, alt));	// 釣れるアイテムの設定
        }
    }

	// 大量のルートテーブルかチェック
	public static boolean is (ResourceLocation name, ResourceLocation... targetArray) {
		for (ResourceLocation target : targetArray) {
			if (name.equals(target)) { return true; }
		}
		return false;
	}

	// 対象バイオームの取得
	public static AlternativeLootItemCondition.Builder getAlt (ResourceKey<Biome>... biomesArray) {

		List<LootItemCondition.Builder> buildList = new ArrayList<>();

		for (ResourceKey<Biome> biome : biomesArray) {
			buildList.add(LocationCheck.checkLocation(LocationPredicate.Builder.location().setBiome(biome)));
		}

		return AlternativeLootItemCondition.alternative(buildList.toArray(new LootItemCondition.Builder[buildList.size()]));
	}

	// ルートテーブルの取得
	public static LootPoolEntryContainer getLoot (ItemLike item, int weiight, AlternativeLootItemCondition.Builder alt) {
		return LootItem.lootTableItem(item).setWeight(weiight).when(alt).build();
	}

    public static void addLootTable(LootTable loot, LootPoolEntryContainer... entry) {
        LootPool pool = Objects.requireNonNull(loot.getPool("main"));
        try {
            for (LootPoolEntryContainer lootEntry : entry) {
                addEntryToLootPool(pool, lootEntry);
            }
        }

        catch (IllegalAccessException e) { }
    }

    private static void addEntryToLootPool(LootPool pool, LootPoolEntryContainer entry) throws IllegalAccessException {

        Field entries = ObfuscationReflectionHelper.findField(LootPool.class, "f_79023_");
        LootPoolEntryContainer[] entryArray = (LootPoolEntryContainer[]) entries.get(pool);
        List<LootPoolEntryContainer> newLoot = new ArrayList<>(List.of(entryArray));

        if (newLoot.stream().anyMatch(e -> e == entry)) {
            throw new RuntimeException("Attempted to add a duplicate entry to pool: " + entry);
        }

        newLoot.add(entry);
        entries.set(pool, newLoot.toArray(new LootPoolEntryContainer[0]));
    }
}
