package sweetmagic.event;

import java.util.List;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.BasicItemListing;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import sweetmagic.init.ItemInit;
import sweetmagic.init.VillageInit;

public class VillagerEvent {

	@SubscribeEvent
	public static void villagerTrade(VillagerTradesEvent event) {
		VillagerProfession type = event.getType();
		ResourceLocation pro = ForgeRegistries.VILLAGER_PROFESSIONS.getKey(type);
		if (pro == null) { return; }

		Int2ObjectMap<List<VillagerTrades.ItemListing>> tradeList = event.getTrades();

		if (type.equals(VillageInit.COOK_PRO.get())) {

			List<VillagerTrades.ItemListing> tier1 = tradeList.get(1);
			List<VillagerTrades.ItemListing> tier2 = tradeList.get(2);
			List<VillagerTrades.ItemListing> tier3 = tradeList.get(3);
			List<VillagerTrades.ItemListing> tier4 = tradeList.get(4);
			List<VillagerTrades.ItemListing> tier5 = tradeList.get(5);

			tier1.add(sellTrade(1, ItemInit.egg_bag, 2, 10, 4));
			tier1.add(buyTrade(Items.SUGAR, 32, 16, 3));
			tier2.add(sellTrade(3, ItemInit.seed_bag, 1, 8, 8));
			tier2.add(buyTrade(ItemInit.orange, 8, 16, 5));
			tier2.add(buyTrade(ItemInit.lemon, 8, 16, 5));
			tier2.add(buyTrade(ItemInit.peach, 8, 16, 5));
			tier2.add(buyTrade(ItemInit.estor_apple, 8, 16, 5));
			tier2.add(buyTrade(ItemInit.strawberry, 8, 16, 5));
			tier3.add(buyTrade(ItemInit.salt, 24, 16, 5));
			tier3.add(buyTrade(ItemInit.flour, 12, 16, 8));
			tier3.add(buyTrade(ItemInit.butter, 12, 10, 6));
			tier3.add(buyTrade(ItemInit.olive_oil, 10, 16, 7));
			tier3.add(buyTrade(ItemInit.cheese, 2, 16, 7));
			tier3.add(buyTrade(ItemInit.sponge_cake, 2, 16, 2, 10));
			tier4.add(sellTrade(2, ItemInit.strawberry_milk, 10, 10, 8));
			tier4.add(sellTrade(2, ItemInit.peach_compote, 12, 10, 8));
			tier4.add(sellTrade(2, ItemInit.apple_jelly, 10, 10, 8));
			tier4.add(sellTrade(2, ItemInit.marshmallow, 16, 10, 8));
			tier4.add(sellTrade(2, ItemInit.icebox_cookie, 10, 10, 8));
			tier4.add(sellTrade(2, ItemInit.icing_cookies, 10, 10, 8));
			tier4.add(sellTrade(2, ItemInit.coconuts_cookie, 10, 10, 8));
			tier4.add(sellTrade(2, ItemInit.macaroon, 10, 10, 8));
			tier5.add(sellTrade(2, ItemInit.orange_tart, 12, 8, 10));
			tier5.add(sellTrade(2, ItemInit.cream_puff, 8, 8, 10));
			tier5.add(sellTrade(2, ItemInit.gateau_chocolat, 8, 8, 10));
			tier5.add(sellTrade(2, ItemInit.short_cake, 8, 8, 10));
			tier5.add(sellTrade(2, ItemInit.cake_roll, 8, 8, 10));
			tier5.add(sellTrade(2, ItemInit.donut_strawberrychoco, 8, 8, 10));
			tier5.add(sellTrade(2, ItemInit.mont_blanc, 8, 8, 10));
		}

		else if (type.equals(VillageInit.MAGICIAN_PRO.get())) {

			ItemStack pick = new ItemStack(ItemInit.alt_pick);
			pick.enchant(Enchantments.BLOCK_FORTUNE, 2);
			ItemStack axe = new ItemStack(ItemInit.alt_axe);
			axe.enchant(Enchantments.BLOCK_EFFICIENCY, 6);

			List<VillagerTrades.ItemListing> tier1 = tradeList.get(1);
			List<VillagerTrades.ItemListing> tier2 = tradeList.get(2);
			List<VillagerTrades.ItemListing> tier3 = tradeList.get(3);
			List<VillagerTrades.ItemListing> tier4 = tradeList.get(4);
			List<VillagerTrades.ItemListing> tier5 = tradeList.get(5);

			tier1.add(sellTrade(Items.ENDER_PEARL, 6, 16, 4, ItemInit.aether_crystal, 2));
			tier1.add(sellTrade(Items.BLAZE_ROD, 6, 16, 4, ItemInit.aether_crystal, 2));
			tier1.add(buyTrade(Items.GLASS_BOTTLE, 24, 16, 3, ItemInit.aether_crystal, 1));
			tier2.add(sellTrade(ItemInit.alternative_ingot, 5, 16, 7, ItemInit.aether_crystal, 2));
			tier2.add(sellTrade(ItemInit.cotton, 16, 16, 7, ItemInit.aether_crystal, 2));
			tier2.add(sellTrade(ItemInit.sannyflower_petal, 8, 16, 8, ItemInit.aether_crystal, 2));
			tier2.add(sellTrade(ItemInit.moonblossom_petal, 8, 16, 8, ItemInit.aether_crystal, 2));
			tier2.add(buyTrade(ItemInit.sugarbell, 8, 16, 6, ItemInit.aether_crystal, 1));
			tier2.add(buyTrade(ItemInit.paper_mint, 8, 16, 6, ItemInit.aether_crystal, 1));
			tier2.add(buyTrade(ItemInit.sticky_stuff_petal, 8, 16, 6, ItemInit.aether_crystal, 1));
			tier2.add(buyTrade(Items.BONE_MEAL, 12, 16, 6, ItemInit.aether_crystal, 1));
			tier3.add(sellTrade(ItemInit.blank_page, 24, 16, 8, ItemInit.aether_crystal, 2));
			tier3.add(sellTrade(ItemInit.blank_magic, 16, 16, 9, ItemInit.aether_crystal, 2));
			tier3.add(sellTrade(ItemInit.mysterious_page, 10, 16, 10, ItemInit.aether_crystal, 2));
			tier3.add(sellTrade(ItemInit.cotton_cloth, 12, 16, 8, ItemInit.aether_crystal, 2));
			tier3.add(sellTrade(ItemInit.blank_page, 24, 16, 8, ItemInit.aether_crystal, 2));
			tier3.add(sellTrade(pick, 1, 15, ItemInit.aether_crystal, 10));
			tier3.add(sellTrade(axe, 1, 15, ItemInit.aether_crystal, 8));
			tier4.add(sellTrade(ItemInit.unmeltable_ice, 6, 16, 10, ItemInit.divine_crystal, 2));
			tier4.add(sellTrade(ItemInit.tiny_feather, 6, 16, 10, ItemInit.divine_crystal, 2));
			tier4.add(sellTrade(ItemInit.poison_bottle, 6, 16, 10, ItemInit.divine_crystal, 2));
			tier4.add(sellTrade(ItemInit.electronic_orb, 6, 16, 10, ItemInit.divine_crystal, 2));
			tier4.add(sellTrade(ItemInit.grav_powder, 6, 16, 10, ItemInit.divine_crystal, 2));
			tier4.add(sellTrade(ItemInit.stray_soul, 6, 16, 10, ItemInit.divine_crystal, 2));
			tier5.add(sellTrade(ItemInit.cosmos_light_ingot, 2, 16, 12, ItemInit.divine_crystal, 3));
			tier5.add(sellTrade(ItemInit.mystical_page, 3, 16, 12, ItemInit.divine_crystal, 2));
			tier5.add(sellTrade(ItemInit.acce_bag, 1, 8, 12, ItemInit.divine_crystal, 3));
			tier5.add(sellTrade(ItemInit.pure_crystal, 1, 4, 12, ItemInit.divine_crystal, 3));
			tier5.add(sellTrade(ItemInit.magia_bottle, 1, 4, 12, ItemInit.divine_crystal, 3));
		}
	}

	public static BasicItemListing buyTrade(ItemLike item, int count, int maxTrades, int xp) {
		return buyTrade(item, count, maxTrades, 1, xp);
	}

	public static BasicItemListing buyTrade(ItemLike item, int count, int maxTrades, int emeCount, int xp) {
		return new BasicItemListing(new ItemStack(item, count), new ItemStack(Items.EMERALD, emeCount), maxTrades, xp, 0.05F);
	}

	public static BasicItemListing sellTrade(int value,ItemLike item, int count, int maxTrades, int xp) {
		return new BasicItemListing(value, new ItemStack(item,count), maxTrades, xp, 0.05F);
	}

	public static BasicItemListing buyTrade(ItemLike item, int count, int maxTrades, int xp, Item crystal, int emeCount) {
		return new BasicItemListing(new ItemStack(item, count), new ItemStack(crystal, emeCount), maxTrades, xp, 0.05F);
	}

	public static BasicItemListing sellTrade(ItemLike item, int count, int maxTrades, int xp, Item crystal, int emeCount) {
		return new BasicItemListing(new ItemStack(crystal, emeCount), new ItemStack(item, count), maxTrades, xp, 0.05F);
	}

	public static BasicItemListing sellTrade(ItemStack stack, int maxTrades, int xp, Item crystal, int emeCount) {
		return new BasicItemListing(new ItemStack(crystal, emeCount), stack, maxTrades, xp, 0.05F);
	}
}
