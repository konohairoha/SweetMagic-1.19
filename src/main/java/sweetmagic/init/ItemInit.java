package sweetmagic.init;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.emagic.SMAcceType;
import sweetmagic.api.emagic.SMDropType;
import sweetmagic.api.emagic.SMElement;
import sweetmagic.api.iitem.IFood.FoodType;
import sweetmagic.init.item.blockitem.SMSeed;
import sweetmagic.init.item.magic.AetherRecoveryBook;
import sweetmagic.init.item.magic.ChargeMagic;
import sweetmagic.init.item.magic.EvilArrowItem;
import sweetmagic.init.item.magic.FieldMagic;
import sweetmagic.init.item.magic.MFStuff;
import sweetmagic.init.item.magic.MFTeleport;
import sweetmagic.init.item.magic.MFTime;
import sweetmagic.init.item.magic.MFWeather;
import sweetmagic.init.item.magic.MagicianWand;
import sweetmagic.init.item.magic.NormalMagic;
import sweetmagic.init.item.magic.RankUpMagic;
import sweetmagic.init.item.magic.SMAcce;
import sweetmagic.init.item.magic.SMChoker;
import sweetmagic.init.item.magic.SMHarness;
import sweetmagic.init.item.magic.SMMagicItem;
import sweetmagic.init.item.magic.SMPorch;
import sweetmagic.init.item.magic.SMRobe;
import sweetmagic.init.item.magic.SMTierItem;
import sweetmagic.init.item.magic.ShotMagic;
import sweetmagic.init.item.magic.StartLightWand;
import sweetmagic.init.item.magic.SummonBossMagic;
import sweetmagic.init.item.magic.SummonMagic;
import sweetmagic.init.item.sm.CosmicWand;
import sweetmagic.init.item.sm.DungeonCompas;
import sweetmagic.init.item.sm.JapaneseUmbrella;
import sweetmagic.init.item.sm.MagicKey;
import sweetmagic.init.item.sm.MagicMeal;
import sweetmagic.init.item.sm.Phone;
import sweetmagic.init.item.sm.SMAxe;
import sweetmagic.init.item.sm.SMBook;
import sweetmagic.init.item.sm.SMBucket;
import sweetmagic.init.item.sm.SMBurnItem;
import sweetmagic.init.item.sm.SMCushion;
import sweetmagic.init.item.sm.SMDoorItem;
import sweetmagic.init.item.sm.SMDropItem;
import sweetmagic.init.item.sm.SMFood;
import sweetmagic.init.item.sm.SMFoodItem;
import sweetmagic.init.item.sm.SMHoe;
import sweetmagic.init.item.sm.SMItem;
import sweetmagic.init.item.sm.SMLootBag;
import sweetmagic.init.item.sm.SMPick;
import sweetmagic.init.item.sm.SMShear;
import sweetmagic.init.item.sm.SMShovel;
import sweetmagic.init.item.sm.SMSickle;
import sweetmagic.init.item.sm.SMSword;
import sweetmagic.init.item.sm.SMWand;
import sweetmagic.init.item.sm.SummonerWand;
import sweetmagic.init.item.sm.TreasureItem;
import sweetmagic.init.item.sm.WaterCup;

public class ItemInit {

	public static Map<Item, String> itemMap = new LinkedHashMap<>();
	public static List<Item> seedList = new ArrayList<>();
	public static List<Item> foodItemList = new ArrayList<>();
	public static List<Item> foodList = new ArrayList<>();

	private static final SMElement NON = SMElement.NON;
	private static final SMElement FLAME = SMElement.FLAME;
	private static final SMElement FROST = SMElement.FROST;
	private static final SMElement CYCLON = SMElement.CYCLON;
	private static final SMElement LIGHTNING = SMElement.LIGHTNING;
	private static final SMElement EARTH = SMElement.EARTH;
	private static final SMElement SHINE = SMElement.SHINE;
	private static final SMElement DARK = SMElement.DARK;
	private static final SMElement WATER = SMElement.WATER;
	private static final SMElement GRAVITY = SMElement.GRAVITY;
	private static final SMElement BLAST = SMElement.BLAST;
	private static final SMElement TOXIC = SMElement.TOXIC;
	private static final SMElement TIME = SMElement.TIME;
	private static final SMElement ALL = SMElement.ALL;

	private static final FoodType Fermentation = FoodType.Fermentation;
	private static final FoodType Baked = FoodType.Baked;
	private static final FoodType Simmered = FoodType.Simmered;
	private static final FoodType Stir = FoodType.Stir;
	private static final FoodType Fried = FoodType.Fried;
	private static final FoodType Rice = FoodType.Rice;
	private static final FoodType Fish = FoodType.Fish;
	private static final FoodType Bread = FoodType.Bread;
	private static final FoodType Japanese = FoodType.Japanese;
	private static final FoodType Western = FoodType.Western;
	private static final FoodType Cookie = FoodType.Cookie;
	private static final FoodType Cake = FoodType.Cake;
	private static final FoodType Chilling = FoodType.Chilling;
	private static final FoodType Salad = FoodType.Salad;
	private static final FoodType Drink = FoodType.Drink;
	private static final FoodType Witch = FoodType.WITCH;
	private static final FoodType Devil = FoodType.DEVIL;

	public static Item pane2_door_i = new SMDoorItem("pane2_door_i", BlockInit.pane2_door);
	public static Item pane4_door_i = new SMDoorItem("pane4_door_i", BlockInit.pane4_door);
	public static Item elegant_door_i = new SMDoorItem("elegant_door_i", BlockInit.elegant_door);
	public static Item arch_door_i = new SMDoorItem("arch_door_i", BlockInit.arch_door);
	public static Item arch_plant_door_i = new SMDoorItem("arch_plant_door_i", BlockInit.arch_plant_door);
	public static Item simple_door_i = new SMDoorItem("simple_door_i", BlockInit.simple_door);
	public static Item simple_net_door_i = new SMDoorItem("simple_net_door_i", BlockInit.simple_net_door);
	public static Item frosted_glass_moden_door_t_i = new SMDoorItem("frosted_glass_moden_door_t_i", BlockInit.frosted_glass_moden_door_t);
	public static Item frosted_glass_moden_door_b_i = new SMDoorItem("frosted_glass_moden_door_b_i", BlockInit.frosted_glass_moden_door_b);
	public static Item frosted_glass_moden_door_d_i = new SMDoorItem("frosted_glass_moden_door_d_i", BlockInit.frosted_glass_moden_door_d);
	public static Item large_frosted_glass_moden_door_t_i = new SMDoorItem("large_frosted_glass_moden_door_t_i", BlockInit.large_frosted_glass_moden_door_t);
	public static Item large_frosted_glass_moden_door_b_i = new SMDoorItem("large_frosted_glass_moden_door_b_i", BlockInit.large_frosted_glass_moden_door_b);
	public static Item large_frosted_glass_moden_door_d_i = new SMDoorItem("large_frosted_glass_moden_door_d_i", BlockInit.large_frosted_glass_moden_door_d);
	public static Item gorgeous_door_b_i = new SMDoorItem("gorgeous_door_b_i", BlockInit.gorgeous_door_b);
	public static Item gorgeous_door_w_i = new SMDoorItem("gorgeous_door_w_i", BlockInit.gorgeous_door_w);

	public static Item cushion_b = new SMCushion("cushion_b");
	public static Item cushion_l = new SMCushion("cushion_l");
	public static Item cushion_o = new SMCushion("cushion_o");
	public static Item cushion_pi = new SMCushion("cushion_pi");
	public static Item cushion_pu = new SMCushion("cushion_pu");
	public static Item cushion_r = new SMCushion("cushion_r");
	public static Item cushion_s = new SMCushion("cushion_s");
	public static Item cushion_y = new SMCushion("cushion_y");

	public static Item phone_b = new Phone("phone_b");
	public static Item phone_l = new Phone("phone_l");
	public static Item phone_o = new Phone("phone_o");
	public static Item phone_pi = new Phone("phone_pi");
	public static Item phone_pu = new Phone("phone_pu");
	public static Item phone_r = new Phone("phone_r");
	public static Item phone_s = new Phone("phone_s");

	public static Item aether_crystal_shard = new SMTierItem("aether_crystal_shard", 1);
	public static Item cosmic_crystal_shard = new SMTierItem("cosmic_crystal_shard", 5);
	public static Item aether_crystal = new SMTierItem("aether_crystal", 1);
	public static Item divine_crystal = new SMTierItem("divine_crystal", 2);
	public static Item pure_crystal = new SMTierItem("pure_crystal", 3);
	public static Item deus_crystal = new SMTierItem("deus_crystal", 4);
	public static Item cosmic_crystal = new SMTierItem("cosmic_crystal", 5);

	public static Item fluorite = new SMTierItem("fluorite", 3);
	public static Item redberyl = new SMTierItem("redberyl", 4);

	public static Item wish_crystal = new TreasureItem("wish_crystal", 2, 0);
	public static Item starlight = new TreasureItem("starlight", 2, 1);

	public static Item magicpage_attack = new TreasureItem("magicpage_attack", 2, 2);
	public static Item magicpage_defence = new TreasureItem("magicpage_defence", 2, 2);
	public static Item magicpage_heal = new TreasureItem("magicpage_heal", 2, 2);
	public static Item magicpage_mf = new TreasureItem("magicpage_mf", 2, 2);
	public static Item magicpage_recast = new TreasureItem("magicpage_recast", 2, 2);

	public static Item aether_recovery_book1 = new AetherRecoveryBook("aether_recovery_book1", 1);
	public static Item aether_recovery_book2 = new AetherRecoveryBook("aether_recovery_book2", 2);
	public static Item aether_recovery_book3 = new AetherRecoveryBook("aether_recovery_book3", 3);
	public static Item aether_recovery_book4 = new AetherRecoveryBook("aether_recovery_book4", 4);
	public static Item aether_recovery_book5 = new AetherRecoveryBook("aether_recovery_book5", 5);

	// 花弁
	public static Item sugarbell = new SMMagicItem("sugarbell");
	public static Item clero_petal = new MFTeleport("clerodendrum_petal", true);
	public static Item cotton = new SMMagicItem("cotton");
	public static Item paper_mint = new SMMagicItem("paper_mint");
	public static Item sticky_stuff_petal = new SMMagicItem("sticky_stuff_petal");
	public static Item sannyflower_petal = new MFTime("sannyflower_petal", 0);
	public static Item moonblossom_petal = new MFTime("moonblossom_petal", 14000);
	public static Item fire_nasturtium_petal = new MFWeather("fire_nasturtium_petal", FLAME, 1600, 0);
	public static Item dm_flower = new MFWeather("drizzly_mysotis_flower", WATER, -1, 12000);

	public static Item b_mf_bottle = new SMMagicItem("b_mf_bottle");
	public static Item b_magia_bottle = new SMMagicItem("b_magia_bottle");
	public static Item mf_small_bottle = new SMMagicItem("mf_small_bottle");
	public static Item mf_bottle = new SMMagicItem("mf_bottle");
	public static Item magia_bottle = new SMMagicItem("magia_bottle");

	// 魔術本
	public static Item magic_book = new SMBook("magic_book", 0);
	public static Item magic_book_cosmic = new SMBook("magic_book_cosmic", 1);
	public static Item magic_book_scarlet = new SMBook("magic_book_scarlet", 2);

	public static Item dungeon_compas = new DungeonCompas("dungeon_compas");
	public static Item magickey = new MagicKey("magickey");
	public static Item evil_arrow = new EvilArrowItem("evil_arrow");

	// 素材
	public static Item blank_page = new SMTierItem("blank_page", 1);
	public static Item blank_magic = new SMTierItem("blank_magic", 1);
	public static Item mysterious_page = new SMTierItem("mysterious_page", 2);
	public static Item mystical_page = new SMTierItem("mystical_page", 3);
	public static Item mystical_book = new SMTierItem("mystical_book", 4);
	public static Item ender_shard = new SMMagicItem("ender_shard");
	public static Item magic_meal = new MagicMeal("magic_meal");
	public static Item prizmium = new SMMagicItem("prizmium");
	public static Item cotton_cloth = new SMMagicItem("cotton_cloth");
	public static Item clerodendrum_petal_hairpin = new MFTeleport("clerodendrum_petal_hairpin", false);

	public static Item unmeltable_ice = new SMDropItem("unmeltable_ice", 0);
	public static Item tiny_feather = new SMDropItem("tiny_feather", 1);
	public static Item poison_bottle = new SMDropItem("poison_bottle", 2);
	public static Item electronic_orb = new SMDropItem("electronic_orb", 3);
	public static Item grav_powder = new SMDropItem("grav_powder", 4);
	public static Item stray_soul = new SMDropItem("stray_soul", 5);
	public static Item witch_tears = new SMDropItem("witch_tears", 6);

	public static Item alternative_ingot = new SMTierItem("alternative_ingot", 1);
	public static Item cosmos_light_ingot = new SMTierItem("cosmos_light_ingot", 2);

	public static Item seed_bag = new SMLootBag("seed_bag", 0);
	public static Item egg_bag = new SMLootBag("egg_bag", 1);
	public static Item acce_bag = new SMLootBag("acce_bag", 2);
	public static Item flower_bag = new SMLootBag("flower_bag", 3);

	public static Item fire_powder = new SMBurnItem("fire_powder", 900, SweetMagicCore.smMagicTab);
	public static Item plant_chips = new SMBurnItem("plant_chips", 900, SweetMagicCore.smFoodTab);
	public static Item copper_scrap = new SMItem("copper_scrap");
	public static Item iron_scrap = new SMItem("iron_scrap");
	public static Item gold_scrap = new SMItem("gold_scrap");

	public static Item magician_wand = new MagicianWand("magician_wand");
	public static Item wood_wand_b = new StartLightWand("wood_wand_b", 1);
	public static Item startlight_wand = new StartLightWand("startlight_wand", 0);

	public static Item machete = new SMPick("machete", -1, 512);
	public static Item belial_sword = new SMSword("belial_sword", -1, 4096);
	public static Item silverhammer = new SMPick("silverhammer", -1, 512);
	public static Item alt_sword = new SMSword("alternative_sword", 0, 512);
	public static Item alt_pick = new SMPick("alternative_pick", 0, 512);
	public static Item alt_axe = new SMAxe("alternative_axe", 0, 512);
	public static Item alt_shovel = new SMShovel("alternative_shovel", 0, 512);
	public static Item alt_sickle = new SMSickle("alternative_sickle", 0, 512);
	public static Item alt_hoe = new SMHoe("alternative_hoe");
	public static Item alt_shears = new SMShear("alternative_shears");
	public static Item alt_bucket = new SMBucket("alternative_bucket", 0);
	public static Item alt_bucket_water = new SMBucket("alternative_bucket_water", 1);
	public static Item alt_bucket_lava = new SMBucket("alternative_bucket_lava", 2);

	public static Item fluorite_sword = new SMSword("fluorite_sword", 1, 1024);
	public static Item fluorite_pick = new SMPick("fluorite_pick", 1, 1024);
	public static Item fluorite_axe = new SMAxe("fluorite_axe", 1, 1024);
	public static Item fluorite_shovel = new SMShovel("fluorite_shovel", 1, 1024);
	public static Item fluorite_sickle = new SMSickle("fluorite_sickle", 1, 1024);

	public static Item redberyl_sword = new SMSword("redberyl_sword", 2, 2048);
	public static Item redberyl_pick = new SMPick("redberyl_pick", 2, 2048);
	public static Item redberyl_axe = new SMAxe("redberyl_axe", 2, 2048);
	public static Item redberyl_shovel = new SMShovel("redberyl_shovel", 2, 2048);
	public static Item redberyl_sickle = new SMSickle("redberyl_sickle", 2, 2048);

	public static Item summoner_wand = new SummonerWand("summoner_wand");
	public static Item mf_stuff = new MFStuff("mf_stuff");

	public static Item aether_choker = new SMChoker("aether_choker", 0, 10000);
	public static Item pure_choker = new SMChoker("pure_choker", 1, 100000);
	public static Item deus_choker = new SMChoker("deus_choker", 2, 400000);

	public static Item magicians_robe = new SMRobe("magicians_robe", 0, 10000);
	public static Item witchmadame_robe = new SMRobe("witchmadame_robe", 1, 60000);
	public static Item windine_robe = new SMRobe("windine_robe", 1, 60000);
	public static Item ifrite_robe = new SMRobe("ifrite_robe", 1, 60000);
	public static Item windwitch_robe = new SMRobe("windwitch_robe", 1, 60000);
	public static Item windwitch_master_robe = new SMRobe("windwitch_master_robe", 1, 60000);
	public static Item queenfrost_robe = new SMRobe("queenfrost_robe", 1, 60000);
	public static Item holyangel_robe = new SMRobe("holyangel_robe", 1, 60000);
	public static Item ignisknight_robe = new SMRobe("ignisknight_robe", 1, 60000);
	public static Item feary_robe = new SMRobe("feary_robe", 1, 60000);
	public static Item arlaune_robe = new SMRobe("arlaune_robe", 1, 60000);
	public static Item butler_robe = new SMRobe("butler_robe", 1, 60000);
	public static Item blize_robe = new SMRobe("blize_robe", 1, 60000);
	public static Item stella_robe = new SMRobe("stella_robe", 1, 60000);

	public static Item magicians_pouch = new SMPorch("magicians_pouch", 0);
	public static Item master_magia_pouch = new SMPorch("master_magia_pouch", 1);
	public static Item master_magia_magicians_pouch = new SMPorch("master_magia_magicians_pouch", 2);

	public static Item aether_boot = new SMHarness("aether_boot", 0, 10000);
	public static Item angel_harness = new SMHarness("angel_harness", 1, 200000);

	public static Item warrior_bracelet = new SMAcce("warrior_bracelet", 0, 2, SMAcceType.TERMS, SMDropType.CHEST_BAGS, true);
	public static Item witch_scroll = new SMAcce("witch_scroll", 1, 2, SMAcceType.TERMS, SMDropType.CHEST_BAGS, false);
	public static Item scorching_jewel = new SMAcce("scorching_jewel", 2, 2, SMAcceType.UPDATE, SMDropType.CHEST_BAGS, false);
	public static Item mermaid_veil = new SMAcce("mermaid_veil", 3, 2, SMAcceType.MUL_UPDATE, SMDropType.CHEST_BAGS, true);
	public static Item blood_sucking_ring = new SMAcce("blood_sucking_ring", 4, 2, SMAcceType.TERMS, SMDropType.CHEST_BAGS, true);
	public static Item emelald_pias = new SMAcce("emelald_pias", 5, 2, SMAcceType.TERMS, SMDropType.CHEST_BAGS, true);
	public static Item fortune_ring = new SMAcce("fortune_ring", 6, 2, SMAcceType.MUL_UPDATE, SMDropType.CHEST_BAGS, true);
	public static Item veil_darkness = new SMAcce("veil_darkness", 7, 2, SMAcceType.MUL_UPDATE, SMDropType.CHEST_BAGS, true);
	public static Item varrier_pendant = new SMAcce("varrier_pendant", 8, 2, SMAcceType.MUL_UPDATE, SMDropType.CHEST_BAGS, true);
	public static Item magicians_grobe = new SMAcce("magicians_grobe", 9, 2, SMAcceType.TERMS, SMDropType.CHEST_BAGS, true);
	public static Item magician_quillpen = new SMAcce("magician_quillpen", 10, 3, SMAcceType.TERMS, SMDropType.MOBDROP, false);
	public static Item gravity_pendant = new SMAcce("gravity_pendant", 11, 2, SMAcceType.MUL_UPDATE, SMDropType.CHEST_BAGS, false);
	public static Item poison_fang = new SMAcce("poison_fang", 12, 3, SMAcceType.TERMS, SMDropType.MOBDROP, false);
	public static Item pendulum_necklace = new SMAcce("pendulum_necklace", 13, 2, SMAcceType.TERMS, SMDropType.CHEST_BAGS, true);
	public static Item ignis_soul = new SMAcce("ignis_soul", 14, 3, SMAcceType.TERMS, SMDropType.MOBDROP, false);
	public static Item frosted_chain = new SMAcce("frosted_chain", 15, 3, SMAcceType.TERMS, SMDropType.MOBDROP, false);
	public static Item holly_charm = new SMAcce("holly_charm", 16, 3, SMAcceType.TERMS, SMDropType.MOBDROP, false);
	public static Item wind_relief = new SMAcce("wind_relief", 17, 3, SMAcceType.TERMS, SMDropType.MOBDROP, false);
	public static Item angel_flugel = new SMAcce("angel_flugel", 18, 3, SMAcceType.TERMS, SMDropType.MOBDROP, false);
	public static Item summon_book = new SMAcce("summon_book", 19, 2, SMAcceType.TERMS, SMDropType.BAG, true);
	public static Item electric_earring = new SMAcce("electric_earring", 20, 2, SMAcceType.MUL_UPDATE, SMDropType.BAG, true);
	public static Item wizard_gauntlet = new SMAcce("wizard_gauntlet", 21, 3, SMAcceType.TERMS, SMDropType.MOBDROP, false);
	public static Item prompt_feather = new SMAcce("prompt_feather", 22, 1, SMAcceType.MUL_UPDATE, SMDropType.CRAFT, true);
	public static Item mysterious_fork = new SMAcce("mysterious_fork", 23, 1, SMAcceType.UPDATE, SMDropType.CRAFT, false);
	public static Item extension_ring = new SMAcce("extension_ring", 24, 2, SMAcceType.TERMS, SMDropType.BAG, true);
	public static Item earth_ruby_ring = new SMAcce("earth_ruby_ring", 25, 2, SMAcceType.TERMS, SMDropType.BAG, false);
	public static Item stretch_band = new SMAcce("stretch_band", 30, 2, SMAcceType.MUL_UPDATE, SMDropType.CHEST_BAGS, true);
	public static Item fairy_wing = new SMAcce("fairy_wing", 26, 3, SMAcceType.TERMS, SMDropType.MOBDROP, false);
	public static Item cherry_ornate_hairpin = new SMAcce("cherry_ornate_hairpin", 27, 3, SMAcceType.TERMS, SMDropType.MOBDROP, false);
	public static Item twilight_hourglass = new SMAcce("twilight_hourglass", 28, 3, SMAcceType.TERMS, SMDropType.MOBDROP, false);
	public static Item warrior_card = new SMAcce("warrior_card", 29, 3, SMAcceType.TERMS, SMDropType.MOBDROP, false);
	public static Item wizard_brooch = new SMAcce("wizard_brooch", 31, 3, SMAcceType.TERMS, SMDropType.MOBDROP, false);
	public static Item belial_heart = new SMAcce("belial_heart", 32, 3, SMAcceType.TERMS, SMDropType.MOBDROP, false);
	public static Item wizard_ribbon = new SMAcce("wizard_ribbon", 33, 2, SMAcceType.TERMS, SMDropType.CHEST_BAGS, true);

	// 杖
	public static Item aether_wand = new SMWand("aether_wand", 1, 5000, 4);
	public static Item aether_wand_b = new SMWand("aether_wand_b", 1, 7500, 6);
	public static Item aether_wand_g = new SMWand("aether_wand_g", 1, 7500, 6);
	public static Item aether_wand_p = new SMWand("aether_wand_p", 1, 7500, 6);
	public static Item aether_wand_r = new SMWand("aether_wand_r", 1, 7500, 6);
	public static Item aether_wand_y = new SMWand("aether_wand_y", 1, 7500, 6);

	public static Item divine_wand = new SMWand("divine_wand", 2, 20000, 10);
	public static Item divine_wand_b = new SMWand("divine_wand_b", 2, 30000, 12);
	public static Item divine_wand_g = new SMWand("divine_wand_g", 2, 30000, 12);
	public static Item divine_wand_p = new SMWand("divine_wand_p", 2, 30000, 12);
	public static Item divine_wand_r = new SMWand("divine_wand_r", 2, 30000, 12);
	public static Item divine_wand_y = new SMWand("divine_wand_y", 2, 30000, 12);

	public static Item purecrystal_wand = new SMWand("purecrystal_wand", 3, 80000, 16);
	public static Item purecrystal_wand_b = new SMWand("purecrystal_wand_b", 3, 100000, 18);
	public static Item purecrystal_wand_g = new SMWand("purecrystal_wand_g", 3, 100000, 18);
	public static Item purecrystal_wand_p = new SMWand("purecrystal_wand_p", 3, 100000, 18);
	public static Item purecrystal_wand_r = new SMWand("purecrystal_wand_r", 3, 100000, 18);
	public static Item purecrystal_wand_y = new SMWand("purecrystal_wand_y", 3, 100000, 18);
	public static Item fluorite_wand = new SMWand("fluorite_wand", 3, 100000, 18);

	public static Item deuscrystal_wand = new SMWand("deuscrystal_wand", 4, 140000, 22);
	public static Item deuscrystal_wand_b = new SMWand("deuscrystal_wand_b", 4, 160000, 24);
	public static Item deuscrystal_wand_g = new SMWand("deuscrystal_wand_g", 4, 160000, 24);
	public static Item deuscrystal_wand_p = new SMWand("deuscrystal_wand_p", 4, 160000, 24);
	public static Item deuscrystal_wand_r = new SMWand("deuscrystal_wand_r", 4, 160000, 24);
	public static Item deuscrystal_wand_y = new SMWand("deuscrystal_wand_y", 4, 160000, 24);
	public static Item redberyl_wand = new SMWand("redberyl_wand", 4, 160000, 24);

	public static Item cosmiccrystal_wand = new SMWand("cosmiccrystal_wand", 5, 200000, 30);
	public static Item cosmic_sacred_wand = new CosmicWand("cosmic_sacred_wand", 5, 300000, 35, SHINE);
	public static Item cosmic_blizzard_wand = new CosmicWand("cosmic_blizzard_wand", 5, 300000, 35, FROST);
	public static Item cosmic_flugel_wand = new CosmicWand("cosmic_flugel_wand", 5, 300000, 35, CYCLON);
	public static Item cosmic_gravity_wand = new CosmicWand("cosmic_gravity_wand", 5, 300000, 35, GRAVITY);
	public static Item cosmic_aquamarine_wand = new CosmicWand("cosmic_aquamarine_wand", 5, 300000, 35, WATER);
	public static Item cosmic_prominence_wand = new CosmicWand("cosmic_prominence_wand", 5, 300000, 35, FLAME);
	public static Item cosmic_voltic_wand = new CosmicWand("cosmic_voltic_wand", 5, 300000, 35, LIGHTNING);
	public static Item cosmic_poison_wand = new CosmicWand("cosmic_poison_wand", 5, 300000, 35, TOXIC);
	public static Item cosmic_explosion_wand = new CosmicWand("cosmic_explosion_wand", 5, 300000, 35, BLAST);
	public static Item cosmic_magia_wand = new CosmicWand("cosmic_magia_wand", 5, 300000, 35, ALL);
	public static Item cosmic_rifle = new CosmicWand("cosmic_rifle", 5, 300000, 35, NON, true);

	public static Item creative_wand = new SMWand("creative_wand", 7, 1, 35);

	public static Item wood_wand_g = new SMItem("wood_wand_g", false);
	public static Item aether_hammer = new SMItem("aether_hammer", false);
	public static Item ignis_armor = new SMItem("ignis_armor", false);

	// 魔法
	public static Item magic_quickheal = new NormalMagic("magic_quickheal", WATER, 1, 500, 100, 0, "magic_heal");
	public static Item magic_hiheal = new NormalMagic("magic_hiheal", WATER, 2, 1200, 300, 1, "magic_heal");
	public static Item magic_roudheal = new NormalMagic("magic_roudheal", WATER, 3, 1800, 800, 10, "magic_heal");
	public static Item magic_healingwish = new NormalMagic("magic_healingwish", WATER, 4, 2500, 1200, 16, "magic_heal");

	public static Item magic_effectremover = new NormalMagic("magic_effectremover", WATER, 1, 1800, 200, 4, "magic_reflasheffect");
	public static Item magic_reflasheffect = new NormalMagic("magic_reflasheffect", WATER, 2, 3000, 500, 5);
	public static Item magic_reflashresurrection = new NormalMagic("magic_reflashresurrection", WATER, 3, 6000, 1200, 11, "magic_reflasheffect");
	public static Item magic_reflashfield = new FieldMagic("magic_reflashfield", WATER, 4, 8000, 1500, 12, "magic_reflasheffect");

	public static Item magic_light = new ShotMagic("magic_light", SHINE, 1, 20, 20, 0);
	public static Item magic_holy_light = new ShotMagic("magic_holy_light", SHINE, 2, 160, 120, 23, "magic_light");
	public static Item magic_illuminate = new ShotMagic("magic_illuminate", SHINE, 3, 240, 500, 24, "magic_light");
	public static Item magic_shining_flare = new ShotMagic("magic_shining_flare", SHINE, 4, 360, 1200, 39, "magic_light");

	public static Item magic_aether_armor = new NormalMagic("magic_aether_armor", SHINE, 1, 1800, 300, 2, "magic_aether_barrier");
	public static Item magic_aether_barrier = new NormalMagic("magic_aether_barrier", SHINE, 2, 3000, 500, 3);
	public static Item magic_magia_protection = new NormalMagic("magic_magia_protection", SHINE, 3, 4800, 1200, 12, "magic_aether_barrier");
	public static Item magic_expand_barrier = new NormalMagic("magic_expand_barrier", SHINE, 4, 6000, 2000, 17, "magic_aether_barrier");

	public static Item magic_fire = new ShotMagic("magic_fire", FLAME, 1, 50, 30, 1);
	public static Item magic_flamenova = new ShotMagic("magic_flamenova", FLAME, 2, 200, 100, 2, "magic_fire");
	public static Item magic_meteor = new ShotMagic("magic_meteor", FLAME, 3, 400, 600, 34, "magic_fire");
	public static Item magic_meteorite_impact = new ShotMagic("magic_meteorite_impact", FLAME, 4, 600, 1400, 40, "magic_fire");

	public static Item magic_frost = new ShotMagic("magic_frost", FROST, 1, 50, 30, 3);
	public static Item magic_frostspear = new ShotMagic("magic_frostspear", FROST, 2, 160, 100, 4, "magic_frost");
	public static Item magic_frostrain = new ShotMagic("magic_frostrain", FROST, 3, 400, 600, 35, "magic_frost");
	public static Item magic_absolute_zero = new ShotMagic("magic_absolute_zero", FROST, 4, 600, 1400, 41, "magic_frost");

	public static Item magic_tornado = new ShotMagic("magic_tornado", CYCLON, 1, 100, 80, 5);
	public static Item magic_storm = new ShotMagic("magic_storm", CYCLON, 2, 240, 200, 6, "magic_tornado");
	public static Item magic_gale = new ShotMagic("magic_gale", CYCLON, 3, 290, 800, 25, "magic_tornado");
	public static Item magic_tempest_storm = new ShotMagic("magic_tempest_storm", CYCLON, 4, 520, 1400, 42, "magic_tornado");

	public static Item magic_burst = new ShotMagic("magic_burst", BLAST, 1, 120, 100, 7);
	public static Item magic_blast = new ShotMagic("magic_blast", BLAST, 2, 250, 200, 8, "magic_burst");
	public static Item magic_magia_destroy = new ShotMagic("magic_magia_destroy", BLAST, 3, 280, 800, 22, "magic_burst");
	public static Item magic_supernova = new ShotMagic("magic_supernova", BLAST, 4, 520, 1400, 43, "magic_burst");

	public static Item magic_ballast = new ShotMagic("magic_ballast", GRAVITY, 1, 100, 80, 9, "magic_gravitywave");
	public static Item magic_gravitywave = new ShotMagic("magic_gravitywave", GRAVITY, 2, 160, 240, 10);
	public static Item magic_gravity_break = new ShotMagic("magic_gravity_break", GRAVITY, 3, 400, 800, 26, "magic_gravitywave");
	public static Item magic_blackhole = new ShotMagic("magic_blackhole", GRAVITY, 4, 600, 1400, 44, "magic_gravitywave");

	public static Item magic_poison_shoot = new ShotMagic("magic_poison_shoot", TOXIC, 1, 100, 120, 11, "magic_toxic");
	public static Item magic_toxic = new ShotMagic("magic_toxic", TOXIC, 2, 240, 250, 12);
	public static Item magic_deadly_poison = new ShotMagic("magic_deadly_poison", TOXIC, 3, 360, 800, 27, "magic_toxic");
	public static Item magic_end_poison = new ShotMagic("magic_end_poison", TOXIC, 4, 560, 1400, 45, "magic_toxic");

	public static Item magic_lightningbolt = new ChargeMagic("magic_lightningbolt", LIGHTNING, 1, 140, 160, 0, "magic_thunder");
	public static Item magic_thunder = new ChargeMagic("magic_thunder", LIGHTNING, 2, 340, 300, 1);
	public static Item magic_thunderrain = new ChargeMagic("magic_thunderrain", LIGHTNING, 3, 500, 800, 6, "magic_thunder");
	public static Item magic_thunderforce = new ChargeMagic("magic_thunderforce", LIGHTNING, 4, 720, 1500, 8, "magic_thunder");

	public static Item magic_dig = new ShotMagic("magic_dig", EARTH, 1, 15, 10, 13);
	public static Item magic_silk_dig = new ShotMagic("magic_silk_dig", EARTH, 2, 25, 15, 14, "magic_dig");
	public static Item magic_range_dig = new ShotMagic("magic_range_dig", EARTH, 3, 50, 100, 33, "magic_dig");
	public static Item magic_magia_dig = new ShotMagic("magic_magia_dig", EARTH, 4, 75, 300, 46, "magic_dig");

	public static Item magic_rockblast = new ShotMagic("magic_rockblast", EARTH, 1, 100, 100, 29);
	public static Item magic_ironblast = new ShotMagic("magic_ironblast", EARTH, 2, 200, 250, 30, "magic_rockblast");
	public static Item magic_diamondblast = new ShotMagic("magic_diamondblast", EARTH, 3, 300, 800, 31, "magic_rockblast");
	public static Item magic_netheriteblast = new ShotMagic("magic_netheriteblast", EARTH, 4, 500, 1400, 47, "magic_rockblast");

	public static Item magic_cherry = new ShotMagic("magic_cherry", EARTH, 1, 80, 60, 36);
	public static Item magic_cherry_wind = new ShotMagic("magic_cherry_wind", EARTH, 2, 200, 180, 37, "magic_cherry");
	public static Item magic_cherry_storm = new ShotMagic("magic_cherry_storm", EARTH, 3, 350, 700, 38, "magic_cherry");
	public static Item magic_cherry_blizzard = new ShotMagic("magic_cherry_blizzard", EARTH, 4, 600, 1400, 48, "magic_cherry");

	public static Item magic_normal = new ShotMagic("magic_normal", NON, 1, 15, 20, 15);
	public static Item magic_completion = new ShotMagic("magic_completion", NON, 2, 30, 40, 16, "magic_normal");
	public static Item magic_advance = new ShotMagic("magic_advance", NON, 3, 45, 100, 21, "magic_normal");
	public static Item magic_additional = new ShotMagic("magic_additional", NON, 4, 60, 200, 49, "magic_normal");

	public static Item magic_bubleprison = new ShotMagic("magic_bubleprison", WATER, 1, 180, 80, 17);
	public static Item magic_scumefang = new ShotMagic("magic_scumefang", WATER, 2, 250, 200, 18, "magic_bubleprison");
	public static Item magic_bublehell = new ShotMagic("magic_bublehell", WATER, 3, 280, 800, 28, "magic_bubleprison");
	public static Item magic_bubleburst = new ShotMagic("magic_bubleburst", WATER, 4, 420, 1400, 50, "magic_bubleprison");

	public static Item magic_bloodshot = new ShotMagic("magic_bloodshot", DARK, 1, 130, 100, 19, "magic_shadow");
	public static Item magic_bloodwave = new ShotMagic("magic_bloodwave", DARK, 2, 280, 250, 20, "magic_shadow");
	public static Item magic_bloodvortex = new ShotMagic("magic_bloodvortex", DARK, 3, 400, 600, 32, "magic_shadow");
	public static Item magic_bloodspear = new ShotMagic("magic_bloodspear", DARK, 4, 600, 1400, 51, "magic_shadow");

	public static Item magic_summon_cat = new SummonMagic("magic_summon_cat", TIME, 0, 6000, 1000, 7, "magic_summon_wolf");
	public static Item magic_summon_wolf = new SummonMagic("magic_summon_wolf", TIME, 1, 4800, 600, 0);
	public static Item magic_summon_allay = new SummonMagic("magic_summon_allay", TIME, 2, 6000, 1000, 1, "magic_summon_wolf");
	public static Item magic_summon_golem = new SummonMagic("magic_summon_golem", TIME, 3, 9600, 2000, 2, "magic_summon_wolf");
	public static Item magic_summon_fox = new SummonMagic("magic_summon_fox", TIME, 4, 12000, 4000, 6, "magic_summon_wolf");

	public static Item magic_witchmaster = new SummonMagic("magic_witchmaster", TIME, 3, 12000, 3000, 3);
	public static Item magic_wotchwindine = new SummonMagic("magic_wotchwindine", TIME, 3, 12000, 3000, 4);
	public static Item magic_witchifrit = new SummonMagic("magic_witchifrit", TIME, 3, 12000, 3000, 5);

	public static Item magic_growth_effect = new ChargeMagic("magic_growth_effect", EARTH, 1, 300, 100, 2, "magic_growth_aura");
	public static Item magic_growth_aura = new ChargeMagic("magic_growth_aura", EARTH, 2, 400, 300, 3);
	public static Item magic_growth_wide = new ChargeMagic("magic_growth_wide", EARTH, 3, 600, 900, 7, "magic_growth_aura");
	public static Item magic_growth_harvest = new ChargeMagic("magic_growth_harvest", EARTH, 4, 1000, 2000, 9, "magic_growth_aura");

	public static Item magic_invisible = new NormalMagic("magic_invisible", SHINE, 1, 1800, 300, 13, "magic_aether_shield");
	public static Item magic_aether_shield = new NormalMagic("magic_aether_shield", SHINE, 2, 2400, 600, 14);
	public static Item magic_aether_shield2 = new NormalMagic("magic_aether_shield2", SHINE, 3, 3600, 1000, 15, "magic_aether_shield");
	public static Item magic_aether_shield3 = new NormalMagic("magic_aether_shield3", SHINE, 4, 4800, 2000, 18, "magic_aether_shield");

	public static Item magic_gravityfield = new FieldMagic("magic_gravityfield", GRAVITY, 1, 2400, 300, 0);
	public static Item magic_gravityfield2 = new FieldMagic("magic_gravityfield2", GRAVITY, 2, 3600, 600, 1, "magic_gravityfield");
	public static Item magic_gravityfield3 = new FieldMagic("magic_gravityfield3", GRAVITY, 3, 4800, 1000, 2, "magic_gravityfield");
	public static Item magic_gravityfield4 = new FieldMagic("magic_gravityfield4", GRAVITY, 4, 6000, 2000, 13, "magic_gravityfield");

	public static Item magic_windfield = new FieldMagic("magic_windfield", CYCLON, 1, 2400, 300, 3);
	public static Item magic_windfield2 = new FieldMagic("magic_windfield2", CYCLON, 2, 3600, 600, 4, "magic_windfield");
	public static Item magic_windfield3 = new FieldMagic("magic_windfield3", CYCLON, 3, 4800, 1000, 5, "magic_windfield");
	public static Item magic_windfield4 = new FieldMagic("magic_windfield4", CYCLON, 4, 6000, 2000, 14, "magic_windfield");

	public static Item magic_rainfield = new FieldMagic("magic_rainfield", WATER, 1, 2400, 300, 6);
	public static Item magic_rainfield2 = new FieldMagic("magic_rainfield2", WATER, 2, 3600, 600, 7, "magic_rainfield");
	public static Item magic_rainfield3 = new FieldMagic("magic_rainfield3", WATER, 3, 4800, 1000, 8, "magic_rainfield");
	public static Item magic_rainfield4 = new FieldMagic("magic_rainfield4", WATER, 4, 6000, 2000, 15, "magic_rainfield");

	public static Item magic_future_visionfiled = new FieldMagic("magic_future_visionfiled", WATER, 1, 3000, 400, 9);
	public static Item magic_future_visionfiled2 = new FieldMagic("magic_future_visionfiled2", WATER, 2, 4200, 800, 10, "magic_future_visionfiled");
	public static Item magic_future_visionfiled3 = new FieldMagic("magic_future_visionfiled3", WATER, 3, 5400, 1200, 11, "magic_future_visionfiled");
	public static Item magic_future_visionfiled4 = new FieldMagic("magic_future_visionfiled4", WATER, 4, 7200, 2400, 16, "magic_future_visionfiled");

	public static Item magic_water_breath = new NormalMagic("magic_water_breath", ALL, 0, 2400, 100, 6, "magic_potion");
	public static Item magic_night_vision = new NormalMagic("magic_night_vision", ALL, 1, 2400, 200, 7, "magic_potion");
	public static Item magic_speed = new NormalMagic("magic_speed", ALL, 2, 2400, 300, 8, "magic_potion");
	public static Item magic_strength = new NormalMagic("magic_strength", ALL, 3, 2400, 500, 9, "magic_potion");
	public static Item magic_regeneration = new NormalMagic("magic_regeneration", ALL, 4, 2400, 1200, 19, "magic_potion");

	public static Item magic_aether_force = new RankUpMagic("magic_aether_force", ALL, 1, 10, 0, 0, "aether_wand");
	public static Item magic_divine_force = new RankUpMagic("magic_divine_force", ALL, 2, 10, 0, 1, "divine_wand");
	public static Item magic_pure_force = new RankUpMagic("magic_pure_force", ALL, 3, 10, 0, 2, "purecrystal_wand");
	public static Item magic_deus_force = new RankUpMagic("magic_deus_force", ALL, 4, 10, 0, 3, "deuscrystal_wand");
	public static Item magic_cosmic_force = new RankUpMagic("magic_cosmic_force", ALL, 5, 10, 0, 4, "cosmiccrystal_wand");
	public static Item magic_creative = new RankUpMagic("magic_creative", ALL, 0, 10, 0, 5, "creative_wand");

	public static Item magic_frostlaser = new SummonBossMagic("magic_frostlaser", FROST, 3, 14400, 3000, 0);
	public static Item magic_holybuster = new SummonBossMagic("magic_holybuster", LIGHTNING, 3, 14400, 3000, 1);
	public static Item magic_ignisblast = new SummonBossMagic("magic_ignisblast", FLAME, 3, 14400, 3000, 2);
	public static Item magic_windstorm = new SummonBossMagic("magic_windstorm", CYCLON, 3, 14400, 3000, 3);
	public static Item magic_cherryrain = new SummonBossMagic("magic_cherryrain", EARTH, 3, 14400, 3000, 4);
	public static Item magic_infinitwand = new SummonBossMagic("magic_infinitwand", LIGHTNING, 5, 30000, 6000, 5);
	public static Item magic_magia_frostlaser = new SummonBossMagic("magic_magia_frostlaser", FROST, 5, 30000, 6000, 6, "magic_frostlaser");
	public static Item magic_magia_holybuster = new SummonBossMagic("magic_magia_holybuster", LIGHTNING, 5, 30000, 6000, 7, "magic_holybuster");
	public static Item magic_magia_ignisblast = new SummonBossMagic("magic_magia_ignisblast", FLAME, 5, 30000, 6000, 8, "magic_ignisblast");
	public static Item magic_magia_windstorm = new SummonBossMagic("magic_magia_windstorm", CYCLON, 5, 30000, 6000, 9, "magic_windstorm");
	public static Item magic_magia_cherryrain = new SummonBossMagic("magic_magia_cherryrain", EARTH, 5, 30000, 6000, 10, "magic_cherryrain");
	public static Item magic_stella_wizard = new SummonMagic("magic_stella_wizard", TIME, 5, 36000, 6000, 8);
	public static Item magic_aether_barrier_origin = new NormalMagic("magic_aether_barrier_origin", SHINE, 0, 36000, 1000, 20, "magic_aether_barrier");

	// 種
	public static Item sugarbell_seed = new SMSeed("sugarbell_seed", BlockInit.sugarbell_plant, false);
	public static Item sannyflower_seed = new SMSeed("sannyflower_seed", BlockInit.sannyflower_plant, 0);
	public static Item moonblossom_seed = new SMSeed("moonblossom_seed", BlockInit.moonblossom_plant, 0);
	public static Item fire_nasturtium_seed = new SMSeed("fire_nasturtium_seed", BlockInit.fire_nasturtium_plant);
	public static Item drizzly_mysotis_seed = new SMSeed("drizzly_mysotis_seed", BlockInit.drizzly_mysotis_plant, 0);
	public static Item clerodendrum_seed = new SMSeed("clerodendrum_seed", BlockInit.clerodendrum_plant);
	public static Item cotton_seed = new SMSeed("cotton_seed", BlockInit.cotton_plant);
	public static Item glowflower_seed = new SMSeed("glowflower_seed", BlockInit.glowflower_plant, false);
	public static Item sticky_stuff_seed = new SMSeed("sticky_stuff_seed", BlockInit.sticky_stuff_plant);
	public static Item quartz_seed = new SMSeed("quartz_seed", BlockInit.quartz_plant, 2);
	public static Item lapislazuli_seed = new SMSeed("lapislazuli_seed", BlockInit.lapislazuli_plant, 1);
	public static Item redstone_seed = new SMSeed("redstone_seed", BlockInit.redstone_plant, 1);

	// 作物種
	public static Item olive = new SMSeed("olive_seed", BlockInit.olive_plant);
	public static Item rice_plants = new SMFoodItem("rice_plants");
	public static Item rice_seed = new SMSeed("rice_seed", BlockInit.rice_plant);
	public static Item coffee_seed = new SMSeed("coffee_seed", BlockInit.coffee_plant);
	public static Item corn_seed = new SMSeed("corn_seed", BlockInit.corn_plant);
	public static Item eggplant_seed = new SMSeed("eggplant_seed", BlockInit.eggplant_plant);
	public static Item cabbage_seed = new SMSeed("cabbage_seed", BlockInit.cabbage_plant);
	public static Item lettuce_seed = new SMSeed("lettuce_seed", BlockInit.lettuce_plant);
	public static Item paper_mint_seed = new SMSeed("paper_mint_seed", BlockInit.mint_plant);
	public static Item vannila_pods = new SMSeed("vannila_pods", BlockInit.vannila_plant);
	public static Item pineapple_seed = new SMSeed("pineapple_seed", BlockInit.pineapple_plant);
	public static Item greenpepper_seed = new SMSeed("greenpepper_seed", BlockInit.greenpepper_plant);
	public static Item spinach_seed = new SMSeed("spinach_seed", BlockInit.spinach_plant);
	public static Item j_radish_seed = new SMSeed("j_radish_seed", BlockInit.j_radish_plant);
	public static Item whitenet_seed = new SMSeed("whitenet_seed", BlockInit.whitenet_plant);
	public static Item azuki_seed = new SMSeed("azuki_seed", BlockInit.azuki_plant);

	// 調味料
	public static Item salt = new SMFoodItem("salt");
	public static Item flour = new SMFoodItem("flour");
	public static Item butter = new SMFoodItem("butter");
	public static Item custard = new SMFoodItem("custard");
	public static Item olive_oil = new SMFoodItem("olive_oil");
	public static Item whipping_cream = new SMFoodItem("whipping_cream");
	public static Item soybean_flour = new SMFoodItem("soybean_flour");
	public static Item cocoa_powder = new SMFoodItem("cocoa_powder");
	public static Item gelatin = new SMFoodItem("gelatin");
	public static Item vannila_essence = new SMFoodItem("vannila_essence");
	public static Item vinegar = new SMFoodItem("vinegar");
	public static Item mayonnaise = new SMFoodItem("mayonnaise");
	public static Item salad_dressing = new SMFoodItem("salad_dressing");
	public static Item soy_sauce = new SMFoodItem("soy_sauce");
	public static Item breadcrumbs = new SMFoodItem("breadcrumbs");
	public static Item seaweed = new SMFoodItem("seaweed");
	public static Item bonito_flakes = new SMFoodItem("bonito_flakes");
	public static Item dry_seaweed = new SMFoodItem("dry_seaweed");
	public static Item miso = new SMFoodItem("miso");
	public static Item maple_syrup = new SMFoodItem("maple_syrup", 1);

	// 食べ物作物
	public static Item raspberry = new SMSeed("raspberry_seed", BlockInit.raspberry_plant, 4, 0.35F, false);
	public static Item strawberry = new SMSeed("strawberry_seed", BlockInit.strawberry_plant, 4, 0.3F);
	public static Item blueberry = new SMSeed("blueberry_seed", BlockInit.blueberry_plant, 4, 0.3F);
	public static Item onion = new SMSeed("onion_seed", BlockInit.onion_plant, 3, 0.33F);
	public static Item sweetpotato = new SMSeed("sweetpotato_seed", BlockInit.sweetpotato_plant, 3, 0.4F, false);
	public static Item soybean = new SMSeed("soybean_seed", BlockInit.soybean_plant, 3, 0.4F);
	public static Item tomato_seed = new SMSeed("tomato_seed", BlockInit.tomato_plant, 3, 0.33F);

	// 食べ物
	public static Item orange = new SMFood("orange", 3, 0.3F, 0, false);
	public static Item lemon = new SMFood("lemon", 3, 0.3F, 0, false);
	public static Item peach = new SMFood("peach", 3, 0.3F, 0, false);
	public static Item estor_apple = new SMFood("estor_apple", 3, 0.3F, 0, false);
	public static Item chestnut = new SMFood("chestnut", 4, 0.75F, 0, false);
	public static Item coconut = new SMFood("coconut", 4, 0.75F, 0, false);
	public static Item banana = new SMFood("banana", 4, 0.75F, 0, false);
	public static Item pineapple = new SMFood("pineapple", 3, 0.85F, 0, false);
	public static Item shrimp = new SMFood("shrimp", 3, 0.25F, 0, false);

	public static Item edamame = new SMFood("edamame", 3, 0.4F, 0, false);
	public static Item corn = new SMFood("corn", 3, 0.3F, 0, false);
	public static Item eggplant = new SMFood("eggplant", 3, 0.3F, 0, false);
	public static Item cabbage = new SMFood("cabbage", 2, 0.5F, 0, false);
	public static Item lettuce = new SMFood("lettuce", 2, 0.5F, 0, false);
	public static Item whitenet = new SMFood("whitenet", 5, 0.5F, 0, false);
	public static Item greenpepper = new SMFood("greenpepper", 3, 0.3F, 0, false);
	public static Item spinach = new SMFood("spinach", 3, 0.3F, 0, false);
	public static Item j_radish = new SMFood("j_radish", 3, 0.3F, 0, false);

	// 発酵系
	public static Item sponge_cake = new SMFood("sponge_cake", 4, 0.45F, 0, false);
	public static Item cheese = new SMFood("cheese", 5, 0.5F, 0, Fermentation, 2, false);
	public static Item yogurt = new SMFood("yogurt", 2, 0.5F, 0, Fermentation, 1, false);
	public static Item natto = new SMFood("natto", 4, 0.375F, 0, Fermentation, 1, false);

	// 焼き物系
	public static Item baked_banana = new SMFood("baked_banana", 6, 0.4F, 0, Baked, 2, false);
	public static Item baked_sweetpotato = new SMFood("baked_sweetpotato", 5, 0.35F, 0, Baked, 1, false);
	public static Item baked_eggplant = new SMFood("baked_eggplant", 5, 0.3F, 0, Baked, 1, false);
	public static Item baked_corn = new SMFood("baked_corn", 8, 0.3F, 0, Baked, 1, false);
	public static Item baked_shrimp = new SMFood("baked_shrimp", 7, 0.35F, 0, Baked, 2, false);
	public static Item baked_rice_cake = new SMFood("baked_rice_cake", 6, 0.65F, 0, Baked, 2, false);
	public static Item potatobutter = new SMFood("potatobutter", 7, 0.5F, 0, Baked, 2, false);
	public static Item sunny_side_up = new SMFood("sunny_side_up", 6, 0.2F, 0, Baked, 2, false);
	public static Item omelet = new SMFood("omelet", 6, 0.5F, 0, Baked, 2, false);
	public static Item sweet_potato = new SMFood("sweet_potato", 6, 0.75F, 0, Baked, 4, false);
	public static Item hamburger = new SMFood("hamburger", 16, 0.625F, 0, Baked, 8, false);
	public static Item salmon_meuniere = new SMFood("salmon_meuniere", 4, 0.6F, 0, Baked, 3, false);
	public static Item pizza = new SMFood("pizza", 10, 0.7F, 8, Baked, 7, false);
	public static Item gratin = new SMFood("gratin", 8, 0.6F, 3, Baked, 6, false);
	public static Item steak_hamburg = new SMFood("steak_hamburg", 10, 0.45F, 7, Baked, 7, false);
	public static Item sauteed_mushrooms = new SMFood("sauteed_mushrooms", 7, 0.6F, 0, Baked, 3, false);

	// 煮物
	public static Item boiled_edamame = new SMFood("boiled_edamame", 6, 0.2F, 0, Simmered, 2, false);
	public static Item pumpkin_nituke = new SMFood("pumpkin_nituke", 7, 0.6F, 0, Simmered, 2, false);
	public static Item nikujaga = new SMFood("nikujaga", 16, 0.45F, 0, Simmered, 7, false);
	public static Item buridaikon = new SMFood("buridaikon", 8, 0.4F, 0, Simmered, 3, false);
	public static Item saba_miso = new SMFood("saba_miso", 8, 0.5F, 0, Simmered, 2, false);
	public static Item roll_cabbage = new SMFood("roll_cabbage", 11, 0.75F, 0, Simmered, 6, false);
	public static Item beefstew = new SMFood("beefstew", 10, 0.75F, 0, Simmered, 7, false);
	public static Item stew = new SMFood("stew", 9, 0.95F, 0, Simmered, 7, false);
	public static Item sukiyaki = new SMFood("sukiyaki", 11, 0.5F, 0, Simmered, 7, false);

	// 炒め物
	public static Item peppers_stuffed_with_meat = new SMFood("peppers_stuffed_with_meat", 8, 0.6F, 0, Stir, 7, false);
	public static Item salad_mixoil = new SMFood("salad_mixoil", 12, 0.5F, 0, Stir, 4, false);
	public static Item spinach_egg = new SMFood("spinach_egg", 6, 0.75F, 0, Stir, 3, false);
	public static Item sweet_and_sour_pork = new SMFood("sweet_and_sour_pork", 12, 0.75F, 7, Stir, 8, false);

	// 揚げ物
	public static Item fried_potato = new SMFood("fried_potato", 7, 0.43F, 0, Fried, 2, false);
	public static Item imokenpi = new SMFood("imokenpi", 5, 0.35F, 0, Fried, 2, false);
	public static Item croquette = new SMFood("croquette", 7, 0.65F, 0, Fried, 8, false);
	public static Item fish_and_chips = new SMFood("fish_and_chips", 10, 0.75F, 9, Fried, 7, false);
	public static Item japanese_fried_chicken = new SMFood("japanese_fried_chicken", 10, 0.3F, 9, Fried, 4, false);
	public static Item pork_cutlet = new SMFood("pork_cutlet", 10, 0.4F, 9, Fried, 5, false);
	public static Item fried_shrimp = new SMFood("fried_shrimp", 10, 0.5F, 9, Fried, 5, false);

	// 米・餅類
	public static Item riceball_salt = new SMFood("riceball_salt", 4, 0.5F, 0, false);
	public static Item riceball_salmon = new SMFood("riceball_salmon", 5, 0.6F, 0, false);
	public static Item riceball_grilled = new SMFood("riceball_grilled", 8, 0.4F, 0, Rice, 3, false);
	public static Item kiritanpo = new SMFood("kiritanpo", 8, 0.4F, 0, Rice, 2, false);
	public static Item oyakodon = new SMFood("oyakodon", 10, 0.675F, 3, Rice, 7, false);
	public static Item butadon = new SMFood("butadon", 8, 0.8F, 0, Rice, 5, false);
	public static Item natto_rice = new SMFood("natto_rice", 7, 0.65F, 0, false);
	public static Item gyuudon = new SMFood("gyuudon", 10, 0.5F, 3, Rice, 5, false);
	public static Item cheese_gyudon = new SMFood("cheese_gyudon", 13, 0.65F, 3, Rice, 5, false);
	public static Item kurigohan = new SMFood("kurigohan", 8, 0.65F, 0, Rice, 3, false);
	public static Item japanese_mixed_rice = new SMFood("japanese_mixed_rice", 10, 0.65F, 0, Rice, 8, false);
	public static Item fried_rice = new SMFood("fried_rice", 10, 0.625F, 3, Rice, 6, false);
	public static Item cooked_rice = new SMFood("cooked_rice", 6, 0.5F, 0, Rice, 1, false);
	public static Item mochi = new SMFood("mochi", 6, 0.35F, 0, Rice, 1, false);
	public static Item kinakomochi = new SMFood("kinakomochi", 8, 0.6F, 3, Rice, 2, false);
	public static Item zunda = new SMFood("zunda", 7, 0.8F, 3, Rice, 3, false);
	public static Item omelet_rice = new SMFood("omelet_rice", 14, 0.8F, 8, Rice, 6, false);

	// 魚
	public static Item sushi_egg = new SMFood("sushi_egg", 5, 0.5F, 0, Fish, 4, false);
	public static Item sushi_salmon = new SMFood("sushi_salmon", 5, 0.5F, 0, Fish, 4, false);
	public static Item salmondon = new SMFood("salmondon", 8, 0.5F, 0, Fish, 4, false);
	public static Item fugu_sashimi = new SMFood("fugu_sashimi", 5, 0.35F, 0, Fish, 4, false);
	public static Item salmon_sashimi = new SMFood("salmon_sashimi", 5, 0.35F, 0, Fish, 2, false);

	// パン類
	public static Item toast = new SMFood("toast", 6, 0.3F, 0, Bread, 3, false);
	public static Item butter_toast = new SMFood("butter_toast", 7, 0.4F, 0, Bread, 3, false);
	public static Item jam_toast = new SMFood("jam_toast", 10, 0.4F, 3, Bread, 3, false);
	public static Item french_toast = new SMFood("french_toast", 10, 0.4F, 3, Bread, 4, false);
	public static Item ogura_toast = new SMFood("ogura_toast", 9, 0.6F, 3, Bread, 4, false);
	public static Item butter_role = new SMFood("butter_role", 6, 0.4F, 0, Bread, 4, false);
	public static Item cream_filled_roll = new SMFood("cream_filled_roll", 5, 0.75F, 0, Bread, 3, false);
	public static Item croissant = new SMFood("croissant", 7, 0.65F, 0, Bread, 4, false);
	public static Item melon_bread = new SMFood("melon_bread", 7, 0.45F, 0, Bread, 4, false);
	public static Item pretzel = new SMFood("pretzel", 5, 0.775F, 0, Bread, 4, false);
	public static Item choko_cornet = new SMFood("choko_cornet", 8, 0.4F, 0, Bread, 6, false);
	public static Item sandwich = new SMFood("sandwich", 7, 0.6F, 0, Bread, 5, false);
	public static Item sandwitch_egg = new SMFood("sandwitch_egg", 6, 0.75F, 0, Bread, 4, false);

	// 和菓子
	public static Item itigo_daihuku = new SMFood("itigo_daihuku", 5, 0.5F, 0, Japanese, 3, false);
	public static Item kurikinton = new SMFood("kurikinton", 7, 0.55F, 0, Japanese, 3, false);
	public static Item ohagi = new SMFood("ohagi", 6, 0.7F, 0, Japanese, 3, false);
	public static Item sweet_azuki_bean_soup = new SMFood("sweet_azuki_bean_soup", 8, 0.65F, 0, Japanese, 2, false);
	public static Item youkan = new SMFood("youkan", 4, 1.5F, 0, Japanese, 3, false);

	// 洋菓子
	public static Item chocolate = new SMFood("chocolate", 4, 1F, 0, Western, 2, false);
	public static Item white_chocolate = new SMFood("white_chocolate", 4, 1F, 0, Western, 2, false);
	public static Item pudding = new SMFood("pudding", 7, 0.65F, 0, Western, 2, false);
	public static Item salt_popcorn = new SMFood("salt_popcorn", 5, 0.25F, 0, Western, 1, false);
	public static Item caramel_popcorn = new SMFood("caramel_popcorn", 6, 0.38F, 0, Western, 2, false);
	public static Item cream_brulee = new SMFood("cream_brulee", 8, 0.4F, 0, Western, 3, false);
	public static Item panna_cotta = new SMFood("panna_cotta", 6, 0.65F, 0, Western, 5, false);
	public static Item marshmallow = new SMFood("marshmallow", 4, 0.5F, 0, Western, 3, false);

	// 洋菓子（クッキー）
	public static Item icing_cookies = new SMFood("icing_cookies", 6, 0.5F, 0, Cookie, 4, false);
	public static Item lemon_cookie = new SMFood("lemon_cookie", 6, 0.5F, 0, Cookie, 5, false);
	public static Item icebox_cookie = new SMFood("icebox_cookie", 6, 0.5F, 0, Cookie, 4, false);
	public static Item cookie_jam = new SMFood("cookie_jam", 6, 0.5F, 0, Cookie, 5, false);
	public static Item coconuts_cookie = new SMFood("coconuts_cookie", 6, 0.5F, 0, Cookie, 4, false);

	// 洋菓子（ケーキ）
	public static Item applepie = new SMFood("applepie", 7, 0.65F, 0, Western, 5, false);
	public static Item applecandy = new SMFood("applecandy", 6, 0.65F, 0, Cake, 1, false);
	public static Item strawberry_tart = new SMFood("strawberry_tart", 8, 1F, 0, Cake, 5, false);
	public static Item cake_roll = new SMFood("cake_roll", 5, 1.2F, 0, Cake, 4, false);
	public static Item short_cake = new SMFood("short_cake", 6, 1F, 0, Cake, 4, false);
	public static Item cup_cake = new SMFood("cup_cake", 6, 0.6F, 0, Cake, 4, false);
	public static Item scone = new SMFood("scone", 5, 0.6F, 0, Cake, 3, false);
	public static Item waffle = new SMFood("waffle", 6, 0.65F, 0, Cake, 4, false);
	public static Item sandwich_fruit = new SMFood("sandwich_fruit", 5, 0.85F, 0, Cake, 4, false);
	public static Item choco_pie = new SMFood("choco_pie", 6, 0.5F, 0, Cake, 5, false);
	public static Item blueberry_muffin = new SMFood("blueberry_muffin", 7, 0.5F, 0, Cake, 5, false);
	public static Item chocolate_muffin = new SMFood("chocolate_muffin", 8, 0.35F, 0, Cake, 5, false);
	public static Item apple_muffin = new SMFood("apple_muffin", 5, 0.8F, 0, Cake, 5, false);
	public static Item canele = new SMFood("canele", 6, 0.725F, 0, Cake, 5, false);
	public static Item madeleine = new SMFood("madeleine", 5, 0.65F, 0, Cake, 4, false);
	public static Item macaroon = new SMFood("macaroon", 6, 0.5F, 0, Cake, 3, false);
	public static Item donut_plane = new SMFood("donut_plane", 5, 0.7F, 0, Cake, 4, false);
	public static Item donut_strawberrychoco = new SMFood("donut_strawberrychoco", 7, 0.75F, 0, Cake, 5, false);
	public static Item donut_choco = new SMFood("donut_choco", 6, 0.725F, 0, Cake, 5, false);
	public static Item cream_puff = new SMFood("cream_puff", 8, 0.65F, 0, Cake, 4, false);
	public static Item cheese_cake = new SMFood("cheese_cake", 4, 1.5F, 0, Cake, 5, false);
	public static Item chocolate_cake = new SMFood("chocolate_cake", 5, 1.1F, 0, Cake, 5, false);
	public static Item gateau_chocolat = new SMFood("gateau_chocolat", 7, 0.65F, 0, Cake, 5, false);
	public static Item raspberrypie = new SMFood("raspberrypie", 5, 0.7F, 0, Cake, 5, false);
	public static Item peach_tart = new SMFood("peach_tart", 6, 0.75F, 0, Cake, 5, false);
	public static Item orange_tart = new SMFood("orange_tart", 6, 0.75F, 0, Cake, 5, false);
	public static Item mont_blanc = new SMFood("mont_blanc", 7, 0.8F, 0, Cake, 4, false);
	public static Item talttatan = new SMFood("talttatan", 6, 0.75F, 0, Cake, 6, false);
	public static Item german_tree_cake = new SMFood("german_tree_cake", 8, 0.5F, 0, Cake, 5, false);
	public static Item cake_chiffon = new SMFood("cake_chiffon", 7, 0.6F, 0, Cake, 3, false);
	public static Item eclair = new SMFood("eclair", 6, 0.6F, 0, Cake, 7, false);
	public static Item hotcake = new SMFood("hotcake", 8, 0.65F, 0, Cake, 5, false);
	public static Item fruit_crepe = new SMFood("fruit_crepe", 8, 0.25F, 0, Cake, 7, false);
	public static Item maritozzo = new SMFood("maritozzo", 8, 0.25F, 0, Cake, 4, false);
	public static Item yule_log = new SMFood("yule_log", 8, 0.25F, 0, Cake, 8, false);
	public static Item mille_feuille = new SMFood("mille_feuille", 6, 0.35F, 0, Western, 6, false);
	public static Item castella = new SMFood("castella", 5, 0.45F, 0, Western, 6, false);
	public static Item tiramisu = new SMFood("tiramisu", 7, 0.25F, 0, Western, 6, false);

	// 冷蔵庫類
	public static Item orange_jelly = new SMFood("orange_jelly", 4, 0.75F, 4, Chilling, 3, false);
	public static Item strawberry_jelly = new SMFood("strawberry_jelly", 4, 0.75F, 4, Chilling, 3, false);
	public static Item apple_jelly = new SMFood("apple_jelly", 4, 0.75F, 4, Chilling, 3, false);
	public static Item peach_jelly = new SMFood("peach_jelly", 4, 0.75F, 4, Chilling, 3, false);
	public static Item lemon_shaved_ice = new SMFood("lemon_shaved_ice", 8, 0F, 4, Chilling, 3, false);
	public static Item strawberry_shaved_ice = new SMFood("strawberry_shaved_ice", 8, 0F, 4, Chilling, 3, false);
	public static Item condensed_milk_shaved_ice = new SMFood("condensed_milk_shaved_ice", 8, 0F, 4, Chilling, 3, false);
	public static Item fluit_mix = new SMFood("fluit_mix", 12, 0.1F, 7, Chilling, 4, false);
	public static Item peach_compote = new SMFood("peach_compote", 5, 0.6F, 0, Chilling, 4, false);
	public static Item softcream_vannila = new SMFood("softcream_vannila", 8, 0.3F, 4, Chilling, 4, false);
	public static Item softcream_strawberry = new SMFood("softcream_strawberry", 6, 0.45F, 4, Chilling, 4, false);
	public static Item softcream_chocolate = new SMFood("softcream_chocolate", 7, 0.75F, 4, Chilling, 4, false);
	public static Item ice_strawberrie = new SMFood("ice_strawberrie", 7, 0.45F, 4, Chilling, 5, false);
	public static Item ice_choco = new SMFood("ice_choco", 8, 0.75F, 4, Chilling, 5, false);

	// サラダ
	public static Item salad_mixcorn = new SMFood("salad_mixcorn", 8, 0.285F, 10, Salad, 3, false);
	public static Item salad_coleslaw = new SMFood("salad_coleslaw", 6, 0.375F, 10, Salad, 4, false);
	public static Item salad_caprese = new SMFood("salad_caprese", 6, 0.333F, 10, Salad, 4, false);
	public static Item salad_potate = new SMFood("salad_potate", 7, 0.4285F, 10, Salad, 4, false);
	public static Item salad_ohitasi = new SMFood("salad_ohitasi", 6, 0.5F, 10, Salad, 3, false);
	public static Item salad_caesar = new SMFood("salad_caesar", 7, 0.6F, 10, Salad, 5, false);
	public static Item salad_green = new SMFood("salad_green", 6, 0.45F, 10, Salad, 4, false);
	public static Item peppers_with_soy_sauce_and_bonito = new SMFood("peppers_with_soy_sauce_and_bonito", 6, 0.6F, 10, Salad, 3, false);
	public static Item salad_fruit = new SMFood("salad_fruit", 6, 0.6F, 10, Salad, 4, false);
	public static Item salmon_carpaccio = new SMFood("salmon_carpaccio", 7, 0.6F, 0, Salad, 5, false);

	// 飲み物
	public static Item milk_pack = new SMFood("milk_pack", 1, 0.5F, 1, true);
	public static Item watercup = new WaterCup("watercup", 1, 0.5F, 2, true);
	public static Item soy_milk = new SMFood("soy_milk", 3, 0.5F, 1, Drink, 1, true);

	public static Item corn_soup = new SMFood("corn_soup", 7, 0.5F, 0, Drink, 2, true);
	public static Item strawberry_milk = new SMFood("strawberry_milk", 4, 1.25F, 0, Drink, 2, true);
	public static Item coconut_juice = new SMFood("coconut_juice", 6, 0.65F, 0, Drink, 3, true);
	public static Item pumpkin_soup = new SMFood("pumpkin_soup", 6, 0.8F, 0, Drink, 2, true);
	public static Item banana_smoothy = new SMFood("banana_smoothy", 6, 0.8F, 0, Drink, 2, true);
	public static Item apple_juice = new SMFood("apple_juice", 6, 0.8F, 0, Drink, 2, true);
	public static Item orange_juice = new SMFood("orange_juice", 6, 0.8F, 0, Drink, 2, true);
	public static Item pine_smoothy = new SMFood("pine_smoothy", 8, 0.4F, 0, Drink, 2, true);
	public static Item mixed_juice = new SMFood("mixed_juice", 6, 0.8F, 0, Drink, 2, true);
	public static Item coffee = new SMFood("coffee", 5, 0F, 5, Drink, 2, true);
	public static Item cafe_latte = new SMFood("cafe_latte", 6, 0.4F, 5, Drink, 2, true);
	public static Item vienna_coffee = new SMFood("vienna_coffee", 6, 0.7F, 5, Drink, 2, true);
	public static Item cocoa = new SMFood("cocoa", 8, 0.5F, 0, Drink, 2, true);
	public static Item seaplant_soup = new SMFood("seaplant_soup", 7, 0.3F, 0, Drink, 2, true);
	public static Item soy_soup = new SMFood("soy_soup", 7, 0.4F, 1, Drink, 2, true);
	public static Item pork_soup = new SMFood("pork_soup", 9, 0.65F, 3, Drink, 2, true);
	public static Item fruit_wine = new SMFood("fruit_wine", 2, 0.5F, 0, true);
	public static Item lemonade = new SMFood("lemonade", 8, 0.6F, 0, Drink, 2, true);
	public static Item berryorange_juice = new SMFood("berryorange_juice", 9, 0.25F, 0, Drink, 2, true);
	public static Item milkshake = new SMFood("milkshake", 6, 0.4F, 0, Drink, 2, true);

	public static Item witch_cake = new SMFood("witch_cake", 8, 0.5F, 0, Witch, 10, false);
	public static Item devil_cake = new SMFood("devil_cake", 9, 0.3F, 0, Devil, 10, false);

	public static Item angel_wing_old = new SMItem("angel_wing_old", (CreativeModeTab) null);
	public static Item angel_wing = new SMItem("angel_wing", (CreativeModeTab) null);
	public static Item angel_wing_b = new SMItem("angel_wing_b", (CreativeModeTab) null);
	public static Item cat_wing = new SMItem("cat_wing", (CreativeModeTab) null);
	public static Item japanese_umbrella = new JapaneseUmbrella("japanese_umbrella");

	@SubscribeEvent
	public static void registerItem(RegisterEvent event) {
		event.register(ForgeRegistries.Keys.ITEMS, h -> itemMap.forEach((key, val) -> h.register(SweetMagicCore.getSRC(val), key)));
	}
}
