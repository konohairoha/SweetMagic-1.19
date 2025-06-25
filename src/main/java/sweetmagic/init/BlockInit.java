package sweetmagic.init;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jetbrains.annotations.NotNull;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.block.base.BaseMFBlock;
import sweetmagic.init.block.base.BaseModelBlock;
import sweetmagic.init.block.crop.Alstroemeria;
import sweetmagic.init.block.crop.MagiaFlower;
import sweetmagic.init.block.crop.SweetCrops_DoublePlant;
import sweetmagic.init.block.crop.SweetCrops_STAGE1;
import sweetmagic.init.block.crop.SweetCrops_STAGE2;
import sweetmagic.init.block.crop.SweetCrops_STAGE3;
import sweetmagic.init.block.crop.SweetCrops_STAGE4;
import sweetmagic.init.block.crop.SweetCrops_STAGE5;
import sweetmagic.init.block.crop.Whitenet;
import sweetmagic.init.block.magic.AetherHopper;
import sweetmagic.init.block.magic.AetherLanp;
import sweetmagic.init.block.magic.AetherRecycler;
import sweetmagic.init.block.magic.AquariumPot;
import sweetmagic.init.block.magic.MFChager;
import sweetmagic.init.block.magic.MFFisher;
import sweetmagic.init.block.magic.MFFurnace;
import sweetmagic.init.block.magic.MFLiquidBlock;
import sweetmagic.init.block.magic.MFPot;
import sweetmagic.init.block.magic.MFTable;
import sweetmagic.init.block.magic.MFTank;
import sweetmagic.init.block.magic.MagiaLantern;
import sweetmagic.init.block.magic.MagicLight;
import sweetmagic.init.block.magic.MagicianLectern;
import sweetmagic.init.block.magic.ObMagia;
import sweetmagic.init.block.magic.PedalCreate;
import sweetmagic.init.block.magic.RuneCharacter;
import sweetmagic.init.block.magic.SMSpawner;
import sweetmagic.init.block.magic.SMStone;
import sweetmagic.init.block.magic.SpawnCrystal;
import sweetmagic.init.block.magic.SturdustCrystal;
import sweetmagic.init.block.magic.ToolRepair;
import sweetmagic.init.block.sm.*;

public class BlockInit {

	public static Map<BlockInfo, String> blockMap = new LinkedHashMap<>();
	public static List<Block> saplingList = new ArrayList<>();

	public static final Block chestnut_leaves = new SMLeave("chestnut_leaves", 0);
	public static final Block chestnut_log = new SMLog("chestnut_log");
	public static final Block chestnut_sapling = new SMSapling("chestnut_sapling", 0);
	public static final Block chestnut_planks = new SMPlanks("chestnut_planks");
	public static final Block chestnut_slab = new SMSlab("chestnut_slab", chestnut_planks);
	public static final Block chestnut_stairs = new SMStair("chestnut_stairs", chestnut_planks);
	public static final Block chestnut_planks_plate = new SMPressurePlate("chestnut_planks_plate", 0);

	public static final Block lemon_leaves = new FruitLeaves("lemon_leaves", 0, 4);
	public static final Block lemon_log = new SMLog("lemon_log");
	public static final Block lemon_sapling = new SMSapling("lemon_sapling", 1);
	public static final Block lemon_planks = new SMPlanks("lemon_planks");
	public static final Block lemon_slab = new SMSlab("lemon_slab", lemon_planks);
	public static final Block lemon_stairs = new SMStair("lemon_stairs", lemon_planks);
	public static final Block lemon_planks_plate = new SMPressurePlate("lemon_planks_plate", 0);

	public static final Block orange_leaves = new FruitLeaves("orange_leaves", 1, 4);
	public static final Block orange_log = new SMLog("orange_log");
	public static final Block orange_sapling = new SMSapling("orange_sapling", 2);
	public static final Block orange_planks = new SMPlanks("orange_planks");
	public static final Block orange_planks_mossy = new SMPlanks("orange_planks_mossy");
	public static final Block orange_planks_huti1 = new SMHutiBlock("orange_planks_huti1");
	public static final Block orange_planks_huti3 = new SMHutiBlock("orange_planks_huti3");
	public static final Block orange_slab = new SMSlab("orange_slab", orange_planks);
	public static final Block orange_huti_slab = new SMSlab("orange_huti_slab", orange_planks_huti1);
	public static final Block orange_stairs = new SMStair("orange_stairs", orange_planks);
	public static final Block orange_huti_stairs = new SMStair("orange_huti_stairs", orange_planks_huti1);
	public static final Block orange_planks_plate = new SMPressurePlate("orange_planks_plate", 0);

	public static final Block orange_planks_w = new SMPlanks("orange_planks_w");
	public static final Block orange_planks_w_damage = new SMPlanks("orange_planks_w_damage");
	public static final Block orange_planks_w_mossy = new SMPlanks("orange_planks_w_mossy");
	public static final Block orange_planks_w_huti1 = new SMHutiBlock("orange_planks_w_huti1");
	public static final Block orange_planks_w_huti3 = new SMHutiBlock("orange_planks_w_huti3");
	public static final Block orange_w_slab = new SMSlab("orange_w_slab", orange_planks_w);
	public static final Block orange_w_huti_slab = new SMSlab("orange_w_huti_slab", orange_planks_w_huti1);
	public static final Block orange_w_stairs = new SMStair("orange_w_stairs", orange_planks_w);
	public static final Block orange_w_huti_stairs = new SMStair("orange_w_huti_stairs", orange_planks_w_huti1);
	public static final Block orange_planks_w_plate = new SMPressurePlate("orange_planks_w_plate", 0);

	public static final Block orange_planks_pink = new SMPlanks("orange_planks_pink");
	public static final Block orange_planks_pink_huti1 = new SMHutiBlock("orange_planks_pink_huti1");
	public static final Block orange_planks_pink_huti3 = new SMHutiBlock("orange_planks_pink_huti3");
	public static final Block orange_pink_slab = new SMSlab("orange_pink_slab", orange_planks_pink);
	public static final Block orange_pink_huti_slab = new SMSlab("orange_pink_huti_slab", orange_planks_pink_huti1);
	public static final Block orange_pink_stairs = new SMStair("orange_pink_stairs", orange_planks_pink);
	public static final Block orange_pink_huti_stairs = new SMStair("orange_pink_huti_stairs", orange_planks_pink_huti1);
	public static final Block orange_planks_pink_plate = new SMPressurePlate("orange_planks_pink_plate", 0);

	public static final Block orange_planks_mintgreen = new SMPlanks("orange_planks_mintgreen");
	public static final Block orange_planks_mintgreen_huti1 = new SMHutiBlock("orange_planks_mintgreen_huti1");
	public static final Block orange_planks_mintgreen_huti3 = new SMHutiBlock("orange_planks_mintgreen_huti3");
	public static final Block orange_mintgreen_slab = new SMSlab("orange_mintgreen_slab", orange_planks_mintgreen);
	public static final Block orange_mintgreen_huti_slab = new SMSlab("orange_mintgreen_huti_slab", orange_planks_mintgreen_huti1);
	public static final Block orange_mintgreen_stairs = new SMStair("orange_mintgreen_stairs", orange_planks_mintgreen);
	public static final Block orange_mintgreen_huti_stairs = new SMStair("orange_mintgreen_huti_stairs", orange_planks_mintgreen_huti1);
	public static final Block orange_planks_mintgreen_plate = new SMPressurePlate("orange_planks_mintgreen_plate", 0);

	public static final Block coconut_leaves = new SMLeave("coconut_leaves", 1);
	public static final Block coconut_log = new SMLog("coconut_log");
	public static final Block coconut_sapling = new SMSapling("coconut_sapling", 3);
	public static final Block coconut_planks = new SMPlanks("coconut_planks");
	public static final Block coconut_slab = new SMSlab("coconut_slab", coconut_planks);
	public static final Block coconut_stairs = new SMStair("coconut_stairs", coconut_planks);
	public static final Block coconut_planks_plate = new SMPressurePlate("coconut_planks_plate", 0);

	public static final Block prism_leaves = new SMLeave("prism_leaves", 2);
	public static final Block prism_log = new SMLog("prism_log");
	public static final Block prism_sapling = new SMSapling("prism_sapling", 4);
	public static final Block prism_planks = new SMPlanks("prism_planks");
	public static final Block prism_slab = new SMSlab("prism_slab", prism_planks);
	public static final Block prism_stairs = new SMStair("prism_stairs", prism_planks);
	public static final Block prism_planks_plate = new SMPressurePlate("prism_planks_plate", 0);

	public static final Block banana_leaves = new SMLeave("banana_leaves", 3);
	public static final Block banana_sapling = new SMSapling("banana_sapling", 5);

	public static final Block estor_leaves = new FruitLeaves("estor_leaves", 2, 4);
	public static final Block estor_log = new SMLog("estor_log");
	public static final Block estor_sapling = new SMSapling("estor_sapling", 6);
	public static final Block estor_planks = new SMPlanks("estor_planks");
	public static final Block estor_slab = new SMSlab("estor_slab", estor_planks);
	public static final Block estor_stairs = new SMStair("estor_stairs", estor_planks);
	public static final Block estor_planks_plate = new SMPressurePlate("estor_planks_plate", 0);

	public static final Block peach_leaves = new FruitLeaves("peach_leaves", 3, 4);
	public static final Block peach_log = new SMLog("peach_log");
	public static final Block peach_sapling = new SMSapling("peach_sapling", 7);
	public static final Block peach_planks = new SMPlanks("peach_planks");
	public static final Block peach_slab = new SMSlab("peach_slab", peach_planks);
	public static final Block peach_stairs = new SMStair("peach_stairs", peach_planks);
	public static final Block peach_planks_plate = new SMPressurePlate("peach_planks_plate", 0);

	public static final Block magiawood_leaves = new SMLeave("magiawood_leaves", 4);
	public static final Block magiawood_log = new SMLog("magiawood_log");
	public static final Block magiawood_sapling = new SMSapling("magiawood_sapling", 8);
	public static final Block magiawood_planks = new SMPlanks("magiawood_planks");
	public static final Block magiawood_slab = new SMSlab("magiawood_slab", magiawood_planks);
	public static final Block magiawood_stairs = new SMStair("magiawood_stairs", magiawood_planks);
	public static final Block magiawood_planks_plate = new SMPressurePlate("magiawood_planks_plate", 0);

	public static final Block cherry_blossoms_leaves = new SMLeave("cherry_blossoms_leaves", 5);
	public static final Block cherry_blossoms_log = new SMLog("cherry_blossoms_log");
	public static final Block cherry_blossoms_sapling = new SMSapling("cherry_blossoms_sapling", 9);
	public static final Block cherry_blossoms_planks = new SMPlanks("cherry_blossoms_planks");
	public static final Block cherry_blossoms_slab = new SMSlab("cherry_blossoms_slab", cherry_blossoms_planks);
	public static final Block cherry_blossoms_stairs = new SMStair("cherry_blossoms_stairs", cherry_blossoms_planks);
	public static final Block cherry_blossoms_planks_plate = new SMPressurePlate("cherry_blossoms_planks_plate", 0);
	public static final Block cherry_blossoms_leaves_carpet = new LeaveCarpet("cherry_blossoms_leaves_carpet", 0);

	public static final Block cherry_blossoms_planks_r = new SMPlanks("cherry_blossoms_planks_r");
	public static final Block cherry_blossoms_r_slab = new SMSlab("cherry_blossoms_r_slab", cherry_blossoms_planks_r);
	public static final Block cherry_blossoms_r_stairs = new SMStair("cherry_blossoms_r_stairs", cherry_blossoms_planks_r);
	public static final Block cherry_blossoms_r_planks_plate = new SMPressurePlate("cherry_blossoms_r_planks_plate", 0);

	public static final Block maple_leaves = new MapleLeave("maple_leaves", 6);
	public static final Block maple_log = new SMLog("maple_log");
	public static final Block maple_hole_log = new SweetCrops_STAGE1("maple_hole_log", 0, 3);
	public static final Block maple_sapling = new SMSapling("maple_sapling", 10);
	public static final Block maple_planks = new SMPlanks("maple_planks");
	public static final Block maple_slab = new SMSlab("maple_slab", maple_planks);
	public static final Block maple_stairs = new SMStair("maple_stairs", maple_planks);
	public static final Block maple_planks_plate = new SMPressurePlate("maple_planks_plate", 0);
	public static final Block maple_leaves_carpet = new LeaveCarpet("maple_leaves_carpet", 1);

	public static final Block maple_planks_w = new SMPlanks("maple_planks_w");
	public static final Block maple_w_slab = new SMSlab("maple_w_slab", maple_planks_w);
	public static final Block maple_w_stairs= new SMStair("maple_w_stairs", maple_planks_w);
	public static final Block maple_planks_w_plate = new SMPressurePlate("maple_planks_w_plate", 0);

	// 木製トラップドア
	public static final Block lemon_trapdoor = new SMTrapDoor("lemon_trapdoor", 0);
	public static final Block lemon_window_trapdoor = new SMTrapDoor("lemon_window_trapdoor", 0);
	public static final Block orange_trapdoor = new SMTrapDoor("orange_trapdoor", 0);
	public static final Block orange_trapdoor_w = new SMTrapDoor("orange_trapdoor_w", 0);
	public static final Block orange_trapdoor_p = new SMTrapDoor("orange_trapdoor_p", 0);
	public static final Block orange_trapdoor_m = new SMTrapDoor("orange_trapdoor_m", 0);
	public static final Block wooden_louver_w = new SMTrapDoor("wooden_louver_w", 0);
	public static final Block wooden_louver_l = new SMTrapDoor("wooden_louver_l", 0, wooden_louver_w);
	public static final Block wooden_louver_b = new SMTrapDoor("wooden_louver_b", 0, wooden_louver_w);
	public static final Block wooden_louver_g = new SMTrapDoor("wooden_louver_g", 0, wooden_louver_w);

	// ドア
	public static final Block pane2_door = new SMDoor("pane2_door", 0);
	public static final Block pane4_door = new SMDoor("pane4_door", 1);
	public static final Block elegant_door = new SMDoor("elegant_door", 2);
	public static final Block arch_door = new SMDoor("arch_door", 3);
	public static final Block arch_plant_door = new SMDoor("arch_plant_door", 4);
	public static final Block simple_door = new SMDoor("simple_door", 5);
	public static final Block simple_net_door = new SMDoor("simple_net_door", 6);
	public static final Block frosted_glass_moden_door_t = new SMDoor("frosted_glass_moden_door_t", 7);
	public static final Block frosted_glass_moden_door_b = new SMDoor("frosted_glass_moden_door_b", 8);
	public static final Block frosted_glass_moden_door_d = new SMDoor("frosted_glass_moden_door_d", 9);
	public static final Block large_frosted_glass_moden_door_t = new SMDoor("large_frosted_glass_moden_door_t", 10);
	public static final Block large_frosted_glass_moden_door_b = new SMDoor("large_frosted_glass_moden_door_b", 11);
	public static final Block large_frosted_glass_moden_door_d = new SMDoor("large_frosted_glass_moden_door_d", 12);
	public static final Block gorgeous_door_b = new SMDoor3("gorgeous_door_b", 0);
	public static final Block gorgeous_door_w = new SMDoor3("gorgeous_door_w", 1);

	// アンティークレンガ
	public static final Block antique_brick_0 = new SMBrick("antique_brick_0");
	public static final Block antique_brick_1 = new SMBrick("antique_brick_1", antique_brick_0);
	public static final Block antique_brick_2 = new SMBrick("antique_brick_2", antique_brick_0);
	public static final Block antique_brick_0_slab = new SMSlab("antique_brick_0_slab", antique_brick_0);
	public static final Block antique_brick_0_stairs = new SMStair("antique_brick_0_stairs", antique_brick_0);
	public static final Block antique_brick_trapdoor = new SMTrapDoor("antique_brick_trapdoor", 1);
	public static final Block antique_brick_0_plate = new SMPressurePlate("antique_brick_0_plate", 1);

	public static final Block antique_brick_0w = new SMBrick("antique_brick_0w", antique_brick_0);
	public static final Block antique_brick_1w = new SMBrick("antique_brick_1w", antique_brick_0);
	public static final Block antique_brick_2w = new SMBrick("antique_brick_2w", antique_brick_0);
	public static final Block antique_brick_0w_slab = new SMSlab("antique_brick_0w_slab", antique_brick_0w);
	public static final Block antique_brick_0w_stairs = new SMStair("antique_brick_0w_stairs", antique_brick_0w);
	public static final Block antique_brick_0w_trapdoor = new SMTrapDoor("antique_brick_0w_trapdoor", 1);
	public static final Block antique_brick_0w_plate = new SMPressurePlate("antique_brick_0w_plate", 1);

	public static final Block antique_brick_0l = new SMBrick("antique_brick_0l", antique_brick_0);
	public static final Block antique_brick_1l = new SMBrick("antique_brick_1l", antique_brick_0);
	public static final Block antique_brick_2l = new SMBrick("antique_brick_2l", antique_brick_0);
	public static final Block antique_brick_0l_slab = new SMSlab("antique_brick_0l_slab", antique_brick_0l);
	public static final Block antique_brick_0l_stairs = new SMStair("antique_brick_0l_stairs", antique_brick_0l);
	public static final Block antique_brick_0l_trapdoor = new SMTrapDoor("antique_brick_0l_trapdoor", 1);
	public static final Block antique_brick_0l_plate = new SMPressurePlate("antique_brick_0l_plate", 1);

	public static final Block antique_brick_0g = new SMBrick("antique_brick_0g", antique_brick_0);
	public static final Block antique_brick_1g = new SMBrick("antique_brick_1g", antique_brick_0);
	public static final Block antique_brick_2g = new SMBrick("antique_brick_2g", antique_brick_0);
	public static final Block antique_brick_0g_slab = new SMSlab("antique_brick_0g_slab", antique_brick_0g);
	public static final Block antique_brick_0g_stairs = new SMStair("antique_brick_0g_stairs", antique_brick_0g);
	public static final Block antique_brick_0g_trapdoor = new SMTrapDoor("antique_brick_0g_trapdoor", 1);
	public static final Block antique_brick_0g_plate = new SMPressurePlate("antique_brick_0g_plate", 1);

	public static final Block antique_brick_0b = new SMBrick("antique_brick_0b", antique_brick_0);
	public static final Block antique_brick_0b_slab = new SMSlab("antique_brick_0b_slab", antique_brick_0b);
	public static final Block antique_brick_0b_stairs = new SMStair("antique_brick_0b_stairs", antique_brick_0b);
	public static final Block antique_brick_0b_trapdoor = new SMTrapDoor("antique_brick_0b_trapdoor", 1);
	public static final Block antique_brick_0b_plate = new SMPressurePlate("antique_brick_0b_plate", 1);

	// 古びたレンガ
	public static final Block old_brick = new SMBrick("old_brick");
	public static final Block old_brick_slab = new SMSlab("old_brick_slab", old_brick);
	public static final Block old_brick_stairs = new SMStair("old_brick_stairs", old_brick);
	public static final Block old_brick_trapdoor = new SMTrapDoor("old_brick_trapdoor", 1);
	public static final Block old_brick_plate = new SMPressurePlate("old_brick_plate", 1);

	public static final Block old_brick_b = new SMBrick("old_brick_b", old_brick);
	public static final Block old_brick_b_slab = new SMSlab("old_brick_b_slab", old_brick_b);
	public static final Block old_brick_b_stairs = new SMStair("old_brick_b_stairs", old_brick_b);
	public static final Block old_brick_b_trapdoor = new SMTrapDoor("old_brick_b_trapdoor", 1);
	public static final Block old_brick_b_plate = new SMPressurePlate("old_brick_b_plate", 1);

	public static final Block old_brick_g = new SMBrick("old_brick_g", old_brick);
	public static final Block old_brick_g_slab = new SMSlab("old_brick_g_slab", old_brick_g);
	public static final Block old_brick_g_stairs = new SMStair("old_brick_g_stairs", old_brick_g);
	public static final Block old_brick_g_trapdoor = new SMTrapDoor("old_brick_g_trapdoor", 1);
	public static final Block old_brick_g_plate = new SMPressurePlate("old_brick_g_plate", 1);

	public static final Block old_brick_l = new SMBrick("old_brick_l", old_brick);
	public static final Block old_brick_l_slab = new SMSlab("old_brick_l_slab", old_brick_l);
	public static final Block old_brick_l_stairs = new SMStair("old_brick_l_stairs", old_brick_l);
	public static final Block old_brick_l_trapdoor = new SMTrapDoor("old_brick_l_trapdoor", 1);
	public static final Block old_brick_l_plate = new SMPressurePlate("old_brick_l_plate", 1);

	public static final Block old_brick_r = new SMBrick("old_brick_r", old_brick);
	public static final Block old_brick_r_slab = new SMSlab("old_brick_r_slab", old_brick_r);
	public static final Block old_brick_r_stairs = new SMStair("old_brick_r_stairs", old_brick_r);
	public static final Block old_brick_r_trapdoor = new SMTrapDoor("old_brick_r_trapdoor", 1);
	public static final Block old_brick_r_plate = new SMPressurePlate("old_brick_r_plate", 1);

	public static final Block old_brick_s = new SMBrick("old_brick_s", old_brick);
	public static final Block old_brick_s_slab = new SMSlab("old_brick_s_slab", old_brick_s);
	public static final Block old_brick_s_stairs = new SMStair("old_brick_s_stairs", old_brick_s);
	public static final Block old_brick_s_trapdoor = new SMTrapDoor("old_brick_s_trapdoor", 1);
	public static final Block old_brick_s_plate = new SMPressurePlate("old_brick_s_plate", 1);

	public static final Block old_brick_y = new SMBrick("old_brick_y", old_brick);
	public static final Block old_brick_y_slab = new SMSlab("old_brick_y_slab", old_brick_y);
	public static final Block old_brick_y_stairs = new SMStair("old_brick_y_stairs", old_brick_y);
	public static final Block old_brick_y_trapdoor = new SMTrapDoor("old_brick_y_trapdoor", 1);
	public static final Block old_brick_y_plate = new SMPressurePlate("old_brick_y_plate", 1);

	// パステルレンガ
	public static final Block pasteltile_brick_y = new SMBrick("pasteltile_brick_y");
	public static final Block pasteltile_brick_y_slab = new SMSlab("pasteltile_brick_y_slab", pasteltile_brick_y);
	public static final Block pasteltile_brick_y_stairs = new SMStair("pasteltile_brick_y_stairs", pasteltile_brick_y);
	public static final Block pasteltile_brick_y_trapdoor = new SMTrapDoor("pasteltile_brick_y_trapdoor", 1);
	public static final Block pasteltile_brick_y_plate = new SMPressurePlate("pasteltile_brick_y_plate", 1);

	public static final Block pasteltile_brick_b = new SMBrick("pasteltile_brick_b", pasteltile_brick_y);
	public static final Block pasteltile_brick_b_slab = new SMSlab("pasteltile_brick_b_slab", pasteltile_brick_b);
	public static final Block pasteltile_brick_b_stairs = new SMStair("pasteltile_brick_b_stairs", pasteltile_brick_b);
	public static final Block pasteltile_brick_b_trapdoor = new SMTrapDoor("pasteltile_brick_b_trapdoor", 1);
	public static final Block pasteltile_brick_b_plate = new SMPressurePlate("pasteltile_brick_b_plate", 1);

	public static final Block pasteltile_brick_bl = new SMBrick("pasteltile_brick_bl", pasteltile_brick_y);
	public static final Block pasteltile_brick_bl_slab = new SMSlab("pasteltile_brick_bl_slab", pasteltile_brick_bl);
	public static final Block pasteltile_brick_bl_stairs = new SMStair("pasteltile_brick_bl_stairs", pasteltile_brick_bl);
	public static final Block pasteltile_brick_bl_trapdoor = new SMTrapDoor("pasteltile_brick_bl_trapdoor", 1);
	public static final Block pasteltile_brick_bl_plate = new SMPressurePlate("pasteltile_brick_bl_plate", 1);

	public static final Block pasteltile_brick_br = new SMBrick("pasteltile_brick_br", pasteltile_brick_y);
	public static final Block pasteltile_brick_br_slab = new SMSlab("pasteltile_brick_br_slab", pasteltile_brick_br);
	public static final Block pasteltile_brick_br_stairs = new SMStair("pasteltile_brick_br_stairs", pasteltile_brick_br);
	public static final Block pasteltile_brick_br_trapdoor = new SMTrapDoor("pasteltile_brick_br_trapdoor", 1);
	public static final Block pasteltile_brick_br_plate = new SMPressurePlate("pasteltile_brick_br_plate", 1);

	public static final Block pasteltile_brick_gr = new SMBrick("pasteltile_brick_gr", pasteltile_brick_y);
	public static final Block pasteltile_brick_gr_slab = new SMSlab("pasteltile_brick_gr_slab", pasteltile_brick_gr);
	public static final Block pasteltile_brick_gr_stairs = new SMStair("pasteltile_brick_gr_stairs", pasteltile_brick_gr);
	public static final Block pasteltile_brick_gr_trapdoor = new SMTrapDoor("pasteltile_brick_gr_trapdoor", 1);
	public static final Block pasteltile_brick_gr_plate = new SMPressurePlate("pasteltile_brick_gr_plate", 1);

	public static final Block pasteltile_brick_lg = new SMBrick("pasteltile_brick_lg", pasteltile_brick_y);
	public static final Block pasteltile_brick_lg_slab = new SMSlab("pasteltile_brick_lg_slab", pasteltile_brick_lg);
	public static final Block pasteltile_brick_lg_stairs = new SMStair("pasteltile_brick_lg_stairs", pasteltile_brick_lg);
	public static final Block pasteltile_brick_lg_trapdoor = new SMTrapDoor("pasteltile_brick_lg_trapdoor", 1);
	public static final Block pasteltile_brick_lg_plate = new SMPressurePlate("pasteltile_brick_lg_plate", 1);

	public static final Block pasteltile_brick_r = new SMBrick("pasteltile_brick_r", pasteltile_brick_y);
	public static final Block pasteltile_brick_r_slab = new SMSlab("pasteltile_brick_r_slab", pasteltile_brick_r);
	public static final Block pasteltile_brick_r_stairs = new SMStair("pasteltile_brick_r_stairs", pasteltile_brick_r);
	public static final Block pasteltile_brick_r_trapdoor = new SMTrapDoor("pasteltile_brick_r_trapdoor", 1);
	public static final Block pasteltile_brick_r_plate = new SMPressurePlate("pasteltile_brick_r_plate", 1);

	// ホワイトラインレンガ
	public static final Block whiteline_brick_r = new SMBrick("whiteline_brick_r");
	public static final Block whiteline_brick_r_slab = new SMSlab("whiteline_brick_r_slab", whiteline_brick_r);
	public static final Block whiteline_brick_r_stairs = new SMStair("whiteline_brick_r_stairs", whiteline_brick_r);
	public static final Block whiteline_brick_r_trapdoor = new SMTrapDoor("whiteline_brick_r_trapdoor", 1);
	public static final Block whiteline_brick_r_plate = new SMPressurePlate("whiteline_brick_r_plate", 1);

	public static final Block whiteline_brick_b = new SMBrick("whiteline_brick_b", whiteline_brick_r);
	public static final Block whiteline_brick_b_slab = new SMSlab("whiteline_brick_b_slab", whiteline_brick_b);
	public static final Block whiteline_brick_b_stairs = new SMStair("whiteline_brick_b_stairs", whiteline_brick_b);
	public static final Block whiteline_brick_b_trapdoor = new SMTrapDoor("whiteline_brick_b_trapdoor", 1);
	public static final Block whiteline_brick_b_plate = new SMPressurePlate("whiteline_brick_b_plate", 1);

	public static final Block whiteline_brick_y = new SMBrick("whiteline_brick_y", whiteline_brick_r);
	public static final Block whiteline_brick_y_slab = new SMSlab("whiteline_brick_y_slab", whiteline_brick_y);
	public static final Block whiteline_brick_y_stairs = new SMStair("whiteline_brick_y_stairs", whiteline_brick_y);
	public static final Block whiteline_brick_y_trapdoor = new SMTrapDoor("whiteline_brick_y_trapdoor", 1);
	public static final Block whiteline_brick_y_plate = new SMPressurePlate("whiteline_brick_y_plate", 1);

	public static final Block longtile_brick_w = new SMBrick("longtile_brick_w");
	public static final Block longtile_brick_w_slab = new SMSlab("longtile_brick_w_slab", longtile_brick_w);
	public static final Block longtile_brick_w_stairs = new SMStair("longtile_brick_w_stairs", longtile_brick_w);
	public static final Block longtile_brick_w_trapdoor = new SMTrapDoor("longtile_brick_w_trapdoor", 1);
	public static final Block longtile_brick_w_plate = new SMPressurePlate("longtile_brick_w_plate", 1);

	public static final Block longtile_brick_r = new SMBrick("longtile_brick_r", longtile_brick_w);
	public static final Block longtile_brick_r_slab = new SMSlab("longtile_brick_r_slab", longtile_brick_r);
	public static final Block longtile_brick_r_stairs = new SMStair("longtile_brick_r_stairs", longtile_brick_r);
	public static final Block longtile_brick_r_trapdoor = new SMTrapDoor("longtile_brick_r_trapdoor", 1);
	public static final Block longtile_brick_r_plate = new SMPressurePlate("longtile_brick_r_plate", 1);

	public static final Block longtile_brick_y = new SMBrick("longtile_brick_y", longtile_brick_w);
	public static final Block longtile_brick_y_slab = new SMSlab("longtile_brick_y_slab", longtile_brick_y);
	public static final Block longtile_brick_y_stairs = new SMStair("longtile_brick_y_stairs", longtile_brick_y);
	public static final Block longtile_brick_y_trapdoor = new SMTrapDoor("longtile_brick_y_trapdoor", 1);
	public static final Block longtile_brick_y_plate = new SMPressurePlate("longtile_brick_y_plate", 1);

	public static final Block longtile_brick_g = new SMBrick("longtile_brick_g", longtile_brick_w);
	public static final Block longtile_brick_g_slab = new SMSlab("longtile_brick_g_slab", longtile_brick_g);
	public static final Block longtile_brick_g_stairs = new SMStair("longtile_brick_g_stairs", longtile_brick_g);
	public static final Block longtile_brick_g_trapdoor = new SMTrapDoor("longtile_brick_g_trapdoor", 1);
	public static final Block longtile_brick_g_plate = new SMPressurePlate("longtile_brick_g_plate", 1);

	public static final Block longtile_brick_o = new SMBrick("longtile_brick_o", longtile_brick_w);
	public static final Block longtile_brick_o_slab = new SMSlab("longtile_brick_o_slab", longtile_brick_o);
	public static final Block longtile_brick_o_stairs = new SMStair("longtile_brick_o_stairs", longtile_brick_o);
	public static final Block longtile_brick_o_trapdoor = new SMTrapDoor("longtile_brick_o_trapdoor", 1);
	public static final Block longtile_brick_o_plate = new SMPressurePlate("longtile_brick_o_plate", 1);

	public static final Block longtile_brick_p = new SMBrick("longtile_brick_p", longtile_brick_w);
	public static final Block longtile_brick_p_slab = new SMSlab("longtile_brick_p_slab", longtile_brick_p);
	public static final Block longtile_brick_p_stairs = new SMStair("longtile_brick_p_stairs", longtile_brick_p);
	public static final Block longtile_brick_p_trapdoor = new SMTrapDoor("longtile_brick_p_trapdoor", 1);
	public static final Block longtile_brick_p_plate = new SMPressurePlate("longtile_brick_p_plate", 1);

	public static final Block longtile_brick_bu = new SMBrick("longtile_brick_bu", longtile_brick_w);
	public static final Block longtile_brick_bu_slab = new SMSlab("longtile_brick_bu_slab", longtile_brick_bu);
	public static final Block longtile_brick_bu_stairs = new SMStair("longtile_brick_bu_stairs", longtile_brick_bu);
	public static final Block longtile_brick_bu_trapdoor = new SMTrapDoor("longtile_brick_bu_trapdoor", 1);
	public static final Block longtile_brick_bu_plate = new SMPressurePlate("longtile_brick_bu_plate", 1);

	public static final Block longtile_brick_bl = new SMBrick("longtile_brick_bl", longtile_brick_w);
	public static final Block longtile_brick_bl_slab = new SMSlab("longtile_brick_bl_slab", longtile_brick_bl);
	public static final Block longtile_brick_bl_stairs = new SMStair("longtile_brick_bl_stairs", longtile_brick_bl);
	public static final Block longtile_brick_bl_trapdoor = new SMTrapDoor("longtile_brick_bl_trapdoor", 1);
	public static final Block longtile_brick_bl_plate = new SMPressurePlate("longtile_brick_bl_plate", 1);

	public static final Block longtile_brick_br = new SMBrick("longtile_brick_br", longtile_brick_w);
	public static final Block longtile_brick_br_slab = new SMSlab("longtile_brick_br_slab", longtile_brick_br);
	public static final Block longtile_brick_br_stairs = new SMStair("longtile_brick_br_stairs", longtile_brick_br);
	public static final Block longtile_brick_br_trapdoor = new SMTrapDoor("longtile_brick_br_trapdoor", 1);
	public static final Block longtile_brick_br_plate = new SMPressurePlate("longtile_brick_br_plate", 1);

	// デザインレンガ
	public static final Block design_brick_w = new SMBrick("design_brick_w");
	public static final Block design_brick_w_slab = new SMSlab("design_brick_w_slab", design_brick_w);
	public static final Block design_brick_w_stairs = new SMStair("design_brick_w_stairs", design_brick_w);
	public static final Block design_brick_w_trapdoor = new SMTrapDoor("design_brick_w_trapdoor", 1);
	public static final Block design_brick_w_plate = new SMPressurePlate("design_brick_w_plate", 1);

	public static final Block design_brick_r = new SMBrick("design_brick_r", design_brick_w);
	public static final Block design_brick_r_slab = new SMSlab("design_brick_r_slab", design_brick_r);
	public static final Block design_brick_r_stairs = new SMStair("design_brick_r_stairs", design_brick_r);
	public static final Block design_brick_r_trapdoor = new SMTrapDoor("design_brick_r_trapdoor", 1);
	public static final Block design_brick_r_plate = new SMPressurePlate("design_brick_r_plate", 1);

	public static final Block design_brick_g = new SMBrick("design_brick_g", design_brick_w);
	public static final Block design_brick_g_slab = new SMSlab("design_brick_g_slab", design_brick_g);
	public static final Block design_brick_g_stairs = new SMStair("design_brick_g_stairs", design_brick_g);
	public static final Block design_brick_g_trapdoor = new SMTrapDoor("design_brick_g_trapdoor", 1);
	public static final Block design_brick_g_plate = new SMPressurePlate("design_brick_g_plate", 1);

	public static final Block design_brick_bd = new SMBrick("design_brick_bd", design_brick_w);
	public static final Block design_brick_bd_slab = new SMSlab("design_brick_bd_slab", design_brick_bd);
	public static final Block design_brick_bd_stairs = new SMStair("design_brick_bd_stairs", design_brick_bd);
	public static final Block design_brick_bd_trapdoor = new SMTrapDoor("design_brick_bd_trapdoor", 1);
	public static final Block design_brick_bd_plate = new SMPressurePlate("design_brick_bd_plate", 1);

	public static final Block design_brick_bu = new SMBrick("design_brick_bu", design_brick_w);
	public static final Block design_brick_bu_slab = new SMSlab("design_brick_bu_slab", design_brick_bu);
	public static final Block design_brick_bu_stairs = new SMStair("design_brick_bu_stairs", design_brick_bu);
	public static final Block design_brick_bu_trapdoor = new SMTrapDoor("design_brick_bu_trapdoor", 1);
	public static final Block design_brick_bu_plate = new SMPressurePlate("design_brick_bu_plate", 1);

	public static final Block design_brick_bl = new SMBrick("design_brick_bl", design_brick_w);
	public static final Block design_brick_bl_slab = new SMSlab("design_brick_bl_slab", design_brick_bl);
	public static final Block design_brick_bl_stairs = new SMStair("design_brick_bl_stairs", design_brick_bl);
	public static final Block design_brick_bl_trapdoor = new SMTrapDoor("design_brick_bl_trapdoor", 1);
	public static final Block design_brick_bl_plate = new SMPressurePlate("design_brick_bl_plate", 1);

	public static final Block simplestonebrick = new SMBrick("simplestonebrick");
	public static final Block simplestonebrick_slab = new SMSlab("simplestonebrick_slab", simplestonebrick);
	public static final Block simplestonebrick_stairs = new SMStair("simplestonebrick_stairs", simplestonebrick);
	public static final Block simplestonebrick_trapdoor = new SMTrapDoor("simplestonebrick_trapdoor", 1);
	public static final Block simplestonebrick_plate = new SMPressurePlate("simplestonebrick_plate", 1);

	public static final Block simplestonebrick_cracked = new SMBrick("simplestonebrick_cracked", simplestonebrick);
	public static final Block simplestonebrick_cracked_slab = new SMSlab("simplestonebrick_cracked_slab", simplestonebrick_cracked);
	public static final Block simplestonebrick_cracked_stairs = new SMStair("simplestonebrick_cracked_stairs", simplestonebrick_cracked);
	public static final Block simplestonebrick_cracked_trapdoor = new SMTrapDoor("simplestonebrick_cracked_trapdoor", 1);
	public static final Block simplestonebrick_cracked_plate = new SMPressurePlate("simplestonebrick_cracked_plate", 1);

	public static final Block simplestonebrick_mossy = new SMBrick("simplestonebrick_mossy", simplestonebrick);
	public static final Block simplestonebrick_mossy_slab = new SMSlab("simplestonebrick_mossy_slab", simplestonebrick_mossy);
	public static final Block simplestonebrick_mossy_stairs = new SMStair("simplestonebrick_mossy_stairs", simplestonebrick_mossy);
	public static final Block simplestonebrick_mossy_trapdoor = new SMTrapDoor("simplestonebrick_mossy_trapdoor", 1);
	public static final Block simplestonebrick_mossy_plate = new SMPressurePlate("simplestonebrick_mossy_plate", 1);

	public static final Block simplestonebrick_b = new SMBrick("simplestonebrick_b", simplestonebrick);
	public static final Block simplestonebrick_b_slab = new SMSlab("simplestonebrick_b_slab", simplestonebrick_b);
	public static final Block simplestonebrick_b_stairs = new SMStair("simplestonebrick_b_stairs", simplestonebrick_b);
	public static final Block simplestonebrick_b_trapdoor = new SMTrapDoor("simplestonebrick_b_trapdoor", 1);
	public static final Block simplestonebrick_b_plate = new SMPressurePlate("simplestonebrick_b_plate", 1);

	public static final Block simplestonebrick_b_cracked = new SMBrick("simplestonebrick_b_cracked", simplestonebrick);
	public static final Block simplestonebrick_b_cracked_slab = new SMSlab("simplestonebrick_b_cracked_slab", simplestonebrick_b_cracked);
	public static final Block simplestonebrick_b_cracked_stairs = new SMStair("simplestonebrick_b_cracked_stairs", simplestonebrick_b_cracked);
	public static final Block simplestonebrick_b_cracked_trapdoor = new SMTrapDoor("simplestonebrick_b_cracked_trapdoor", 1);
	public static final Block simplestonebrick_b_cracked_plate = new SMPressurePlate("simplestonebrick_b_cracked_plate", 1);

	public static final Block simplestonebrick_b_mossy = new SMBrick("simplestonebrick_b_mossy", simplestonebrick);
	public static final Block simplestonebrick_b_mossy_slab = new SMSlab("simplestonebrick_b_mossy_slab", simplestonebrick_b_mossy);
	public static final Block simplestonebrick_b_mossy_stairs = new SMStair("simplestonebrick_b_mossy_stairs", simplestonebrick_b_mossy);
	public static final Block simplestonebrick_b_mossy_trapdoor = new SMTrapDoor("simplestonebrick_b_mossy_trapdoor", 1);
	public static final Block simplestonebrick_b_mossy_plate = new SMPressurePlate("simplestonebrick_b_mossy_plate", 1);

	public static final Block simplestonebrick_g = new SMBrick("simplestonebrick_g", simplestonebrick);
	public static final Block simplestonebrick_g_slab = new SMSlab("simplestonebrick_g_slab", simplestonebrick_g);
	public static final Block simplestonebrick_g_stairs = new SMStair("simplestonebrick_g_stairs", simplestonebrick_g);
	public static final Block simplestonebrick_g_trapdoor = new SMTrapDoor("simplestonebrick_g_trapdoor", 1);
	public static final Block simplestonebrick_g_plate = new SMPressurePlate("simplestonebrick_g_plate", 1);

	public static final Block simplestonebrick_g_cracked = new SMBrick("simplestonebrick_g_cracked", simplestonebrick);
	public static final Block simplestonebrick_g_cracked_slab = new SMSlab("simplestonebrick_g_cracked_slab", simplestonebrick_g_cracked);
	public static final Block simplestonebrick_g_cracked_stairs = new SMStair("simplestonebrick_g_cracked_stairs", simplestonebrick_g_cracked);
	public static final Block simplestonebrick_g_cracked_trapdoor = new SMTrapDoor("simplestonebrick_g_cracked_trapdoor", 1);
	public static final Block simplestonebrick_g_cracked_plate = new SMPressurePlate("simplestonebrick_g_cracked_plate", 1);

	public static final Block simplestonebrick_g_mossy = new SMBrick("simplestonebrick_g_mossy", simplestonebrick);
	public static final Block simplestonebrick_g_mossy_slab = new SMSlab("simplestonebrick_g_mossy_slab", simplestonebrick_g_mossy);
	public static final Block simplestonebrick_g_mossy_stairs = new SMStair("simplestonebrick_g_mossy_stairs", simplestonebrick_g_mossy);
	public static final Block simplestonebrick_g_mossy_trapdoor = new SMTrapDoor("simplestonebrick_g_mossy_trapdoor", 1);
	public static final Block simplestonebrick_g_mossy_plate = new SMPressurePlate("simplestonebrick_g_mossy_plate", 1);

	// 石畳
	public static final Block flagstone = new SMBrick("flagstone");
	public static final Block flagstone_slab = new SMSlab("flagstone_slab", flagstone);
	public static final Block flagstone_stairs = new SMStair("flagstone_stairs", flagstone);
	public static final Block flagstone_trapdoor = new SMTrapDoor("flagstone_trapdoor", 1);
	public static final Block flagstone_plate = new SMPressurePlate("flagstone_plate", 1);

	public static final Block flagstone_color = new SMBrick("flagstone_color", flagstone);
	public static final Block flagstone_color_slab = new SMSlab("flagstone_color_slab", flagstone_color);
	public static final Block flagstone_color_stairs = new SMStair("flagstone_color_stairs", flagstone_color);
	public static final Block flagstone_color_trapdoor = new SMTrapDoor("flagstone_color_trapdoor", 1);
	public static final Block flagstone_color_plate = new SMPressurePlate("flagstone_color_plate", 1);

	public static final Block flagstone_brown = new SMBrick("flagstone_brown", flagstone);
	public static final Block flagstone_brown_slab = new SMSlab("flagstone_brown_slab", flagstone_brown);
	public static final Block flagstone_brown_stairs = new SMStair("flagstone_brown_stairs", flagstone_brown);
	public static final Block flagstone_brown_trapdoor = new SMTrapDoor("flagstone_brown_trapdoor", 1);
	public static final Block flagstone_brown_plate = new SMPressurePlate("flagstone_brown_plate", 1);

	public static final Block white_stone = new SMBrick("white_stone");
	public static final Block white_stone_slab = new SMSlab("white_stone_slab", white_stone);
	public static final Block white_stone_stairs = new SMStair("white_stone_stairs", white_stone);
	public static final Block white_stone_trapdoor = new SMTrapDoor("white_stone_trapdoor", 1);
	public static final Block white_stone_plate = new SMPressurePlate("white_stone_plate", 1);

	public static final Block white_stone_polished = new SMBrick("white_stone_polished", white_stone);
	public static final Block white_stone_polished_slab = new SMSlab("white_stone_polished_slab", white_stone_polished);
	public static final Block white_stone_polished_stairs = new SMStair("white_stone_polished_stairs", white_stone_polished);
	public static final Block white_stone_polished_trapdoor = new SMTrapDoor("white_stone_polished_trapdoor", 1);
	public static final Block white_stone_polished_plate = new SMPressurePlate("white_stone_polished_plate", 1);

	public static final Block black_polished = new SMBrick("black_polished", white_stone);
	public static final Block black_polished_slab = new SMSlab("black_polished_slab", black_polished);
	public static final Block black_polished_stairs = new SMStair("black_polished_stairs", black_polished);
	public static final Block black_polished_trapdoor = new SMTrapDoor("black_polished_trapdoor", 1);
	public static final Block black_polished_plate = new SMPressurePlate("black_polished_plate", 1);

	public static final Block half_timber_b = new PillarStone("half_timber_b");
	public static final Block half_timber_l = new PillarStone("half_timber_l");
	public static final Block half_timber_o = new PillarStone("half_timber_o");
	public static final Block half_timber_pi = new PillarStone("half_timber_pi");
	public static final Block half_timber_pu = new PillarStone("half_timber_pu");
	public static final Block half_timber_r = new PillarStone("half_timber_r");
	public static final Block half_timber_s = new PillarStone("half_timber_s");
	public static final Block half_timber_y = new PillarStone("half_timber_y");
	public static final Block half_timber_w = new PillarStone("half_timber_w");

	public static final Block half_timber_w_b = new PillarStone("half_timber_w_b");
	public static final Block half_timber_w_l = new PillarStone("half_timber_w_l");
	public static final Block half_timber_w_o = new PillarStone("half_timber_w_o");
	public static final Block half_timber_w_pi = new PillarStone("half_timber_w_pi");
	public static final Block half_timber_w_pu = new PillarStone("half_timber_w_pu");
	public static final Block half_timber_w_r = new PillarStone("half_timber_w_r");
	public static final Block half_timber_w_s = new PillarStone("half_timber_w_s");
	public static final Block half_timber_w_y = new PillarStone("half_timber_w_y");

	public static final Block pillar_stone_s = new PillarStone("pillar_stone_s");
	public static final Block pillar_stone_w = new PillarStone("pillar_stone_w");
	public static final Block pillar_stone_ws = new PillarStone("pillar_stone_ws");

	public static final Block potting_soil = new PottingSoil("potting_soil", 8);

	public static final Block antique_brick_pot_r = new PlantPot("antique_brick_pot_r", 4);
	public static final Block antique_brick_pot_w = new PlantPot("antique_brick_pot_w", 4);
	public static final Block antique_brick_pot_l = new PlantPot("antique_brick_pot_l", 4);
	public static final Block antique_brick_pot_g = new PlantPot("antique_brick_pot_g", 4);
	public static final Block whiteline_brick_pot_r = new PlantPot("whiteline_brick_pot_r", 4);
	public static final Block whiteline_brick_pot_b = new PlantPot("whiteline_brick_pot_b", 4);
	public static final Block whiteline_brick_pot_y = new PlantPot("whiteline_brick_pot_y", 4);
	public static final Block pasteltile_brick_pot_y = new PlantPot("pasteltile_brick_pot_y", 4);
	public static final Block pasteltile_brick_pot_r = new PlantPot("pasteltile_brick_pot_r", 4);
	public static final Block pasteltile_brick_pot_g = new PlantPot("pasteltile_brick_pot_g", 4);
	public static final Block pasteltile_brick_pot_b = new PlantPot("pasteltile_brick_pot_b", 4);
	public static final Block pasteltile_brick_pot_bl = new PlantPot("pasteltile_brick_pot_bl", 4);
	public static final Block pasteltile_brick_pot_gr = new PlantPot("pasteltile_brick_pot_gr", 4);
	public static final Block longtile_brick_pot_w = new PlantPot("longtile_brick_pot_w", 4);
	public static final Block longtile_brick_pot_r = new PlantPot("longtile_brick_pot_r", 4);
	public static final Block longtile_brick_pot_y = new PlantPot("longtile_brick_pot_y", 4);
	public static final Block longtile_brick_pot_g = new PlantPot("longtile_brick_pot_g", 4);
	public static final Block longtile_brick_pot_o = new PlantPot("longtile_brick_pot_o", 4);
	public static final Block longtile_brick_pot_p = new PlantPot("longtile_brick_pot_p", 4);
	public static final Block longtile_brick_pot_bu = new PlantPot("longtile_brick_pot_bu", 4);
	public static final Block longtile_brick_pot_bl = new PlantPot("longtile_brick_pot_bl", 4);
	public static final Block longtile_brick_pot_br = new PlantPot("longtile_brick_pot_br", 4);
	public static final Block design_brick_pot_w = new PlantPot("design_brick_pot_w", 4);
	public static final Block design_brick_pot_bl = new PlantPot("design_brick_pot_bl", 4);
	public static final Block design_brick_pot_bd = new PlantPot("design_brick_pot_bd", 4);
	public static final Block orange_planks_pot_n = new PlantPot("orange_planks_pot_n", 4);
	public static final Block orange_planks_pot_w = new PlantPot("orange_planks_pot_w", 4);
	public static final Block orange_planks_pot_p = new PlantPot("orange_planks_pot_p", 4);
	public static final Block orange_planks_pot_m = new PlantPot("orange_planks_pot_m", 4);

	// ガラス
	public static final Block sugarglass = new SMGlass("sugarglass", false, false);
	public static final Block shading_sugarglass = new SMGlass("shading_sugarglass", true, false);
	public static final Block frosted_glass = new SMGlass("frosted_glass", false, false);
	public static final Block frosted_glass_line = new SMGlass("frosted_glass_line", false, false);
	public static final Block prismglass = new SMGlass("prismglass", false, true);
	public static final Block shading_prismglass = new SMGlass("shading_prismglass", true, true);
	public static final Block mirage_glass = new MirageGlass("mirage_glass", 0);
	public static final Block mirage_gate_glass = new MirageGlass("mirage_gate_glass", 1);
	public static final Block mirage_wall_glass = new MirageGlass("mirage_wall_glass", 2);

	public static final Block green4panel_glass = new VerticalGlass("green4panel_glass");
	public static final Block lightbrown4panel_glass = new VerticalGlass("lightbrown4panel_glass");
	public static final Block brown4panel_glass = new VerticalGlass("brown4panel_glass");
	public static final Block darkbrown4panel_glass = new VerticalGlass("darkbrown4panel_glass");
	public static final Block ami_glass = new VerticalGlass("ami_glass");
	public static final Block gorgeous_glass = new VerticalGlass("gorgeous_glass");
	public static final Block gorgeous_glass_w = new VerticalGlass("gorgeous_glass_w");

	public static final Block magicbarrier = new MagicBarrierGlass("magicbarrier");
	public static final Block magicbarrier_lock = new MagicBarrierGlassLock("magicbarrier_lock");

	public static final Block sugarglass_pane = new SMGlassPane("sugarglass_pane", false, false);
	public static final Block shading_sugarglass_pane = new SMGlassPane("shading_sugarglass_pane", true, false);
	public static final Block frosted_glass_pane = new SMGlassPane("frosted_glass_pane", false, false);
	public static final Block frosted_glass_line_pane = new SMGlassPane("frosted_glass_line_pane", false, false);
	public static final Block prismglass_pane = new SMGlassPane("prismglass_pane", false, true);
	public static final Block shading_prismglass_pane = new SMGlassPane("shading_prismglass_pane", true, true);

	public static final Block green4panel_glass_pane = new VerticalGlassPane("green4panel_glass_pane");
	public static final Block lightbrown4panel_glass_pane = new VerticalGlassPane("lightbrown4panel_glass_pane");
	public static final Block brown4panel_glass_pane = new VerticalGlassPane("brown4panel_glass_pane");
	public static final Block darkbrown4panel_glass_pane = new VerticalGlassPane("darkbrown4panel_glass_pane");
	public static final Block ami_glass_pane = new VerticalGlassPane("ami_glass_pane");
	public static final Block gorgeous_glass_pane = new VerticalGlassPane("gorgeous_glass_pane");
	public static final Block gorgeous_glass_w_pane = new VerticalGlassPane("gorgeous_glass_w_pane");

	public static final Block wooden_frame_window_w = new WoodenFrameWindow("wooden_frame_window_w");
	public static final Block wooden_frame_window_b = new WoodenFrameWindow("wooden_frame_window_b");

	// 光源
	public static final Block glow_light = new SMLight("glow_light");
	public static final Block glow_light_o = new SMLight("glow_light_o");
	public static final Block gorgeous_lamp = new SMLight("gorgeous_lamp");

	public static final Block stendglass_lamp_g = new StendGlassLamp("stendglass_lamp_g");
	public static final Block stendglass_lamp_b = new StendGlassLamp("stendglass_lamp_b");

	public static final Block table_lamp = new TableLanp("table_lamp");
	public static final Block table_lantern = new TableLanp("table_lantern");

	public static final Block bedside_lamp = new BedSideLamp("bedside_lamp", 0);
	public static final Block bedside_lamp_on = new BedSideLamp("bedside_lamp_on", 1);

	public static final Block candle = new Candle("candle", 0);
	public static final Block fluorite_lantern = new Candle("fluorite_lantern", 0);
	public static final Block candle_ex = new Candle("candle_ex", 1);

	public static final Block moden_lamp = new ModenLamp("moden_lamp", 0);
	public static final Block wall_lamp = new ModenLamp("wall_lamp", 1);
	public static final Block wall_lamp_long = new ModenLamp("wall_lamp_long", 2);
	public static final Block ceiling_cue_lamp = new ModenLamp("ceiling_cue_lamp", 0);

	public static final Block ceiling_light = new CeilingLight("ceiling_light");

	public static final Block ground_light = new GroundLight("ground_light");

	public static final Block aether_crystal_light = new CrystalLight("aether_crystal_light");
	public static final Block divine_crystal_light = new CrystalLight("divine_crystal_light");

	public static final Block magia_crystal_light = new MagiaCrystalLight("magia_crystal_light");

	public static final Block wall_lantern = new WallLantern("wall_lantern", 0);
	public static final Block wall_lantern_gothic = new WallLantern("wall_lantern_gothic", 1);

	public static final Block pole = new Pole("pole");
	public static final Block pole_sling = new PoleLight("pole_sling", 0);
	public static final Block pole_sling_side = new PoleLight("pole_sling_side", 1);

	public static final Block iberis_umbellata_basket = new FlowerBuscket("iberis_umbellata_basket");
	public static final Block campanula_basket = new FlowerBuscket("campanula_basket");
	public static final Block primula_polyansa_basket = new FlowerBuscket("primula_polyansa_basket");
	public static final Block christmas_rose_basket = new FlowerBuscket("christmas_rose_basket");
	public static final Block portulaca_basket = new FlowerBuscket("portulaca_basket");
	public static final Block pansy_yellowmazenta_basket = new FlowerBuscket("pansy_yellowmazenta_basket");
	public static final Block pansy_blue_basket = new FlowerBuscket("pansy_blue_basket");
	public static final Block surfinia_basket = new FlowerBuscket("surfinia_basket");
	public static final Block marigold_basket = new FlowerBuscket("marigold_basket");

	public static final Block twilight_alstroemeria = new Alstroemeria("twilight_alstroemeria");
	public static final Block alstroemeria_aquarium = new ToolRepair("alstroemeria_aquarium", 7);
	public static final Block magiclight = new MagicLight("magiclight", 0);
	public static final Block twilightlight = new MagicLight("twilightlight", 1);

	// 鉱石
	public static final Block aether_crystal_ore = new SMOre("aether_crystal_ore", 0, false);
	public static final Block deep_aether_crystal_ore = new SMOre("deep_aether_crystal_ore", 0, true);
	public static final Block cosmic_crystal_ore = new SMOre("cosmic_crystal_ore", 1, false);
	public static final Block deep_cosmic_crystal_ore = new SMOre("deep_cosmic_crystal_ore", 1, true);
	public static final Block fluorite_ore = new SMOre("fluorite_ore", 2, false);
	public static final Block deep_fluorite_ore = new SMOre("deep_fluorite_ore", 2, true);
	public static final Block redberyl_ore = new SMOre("redberyl_ore", 3, false);
	public static final Block deep_redberyl_ore = new SMOre("deep_redberyl_ore", 3, true);

	// 鉱石ブロック
	public static final Block alt_block = new SMIron("alternative_block", 0.5F);
	public static final Block cosmos_light_block = new SMIron("cosmos_light_block", 0.5F);
	public static final Block aethercrystal_block = new SMIron("aethercrystal_block", 1F);
	public static final Block divinecrystal_block = new SMIron("divinecrystal_block", 2.5F);
	public static final Block purecrystal_block = new SMIron("purecrystal_block", 4F);
	public static final Block magiaflux_block = new SMIron("magiaflux_block", 5F);
	public static final Block fluorite_block = new SMIron("fluorite_block", 2.5F, 15);
	public static final Block redberyl_block = new SMIron("redberyl_block", 4F, 15);

	// 魔法関連
	public static final Block mfchanger = new MFChager("mfchanger", 0);
	public static final Block mfchanger_adavance = new MFChager("mfchanger_adavance", 1);
	public static final Block mfchanger_master = new MFChager("mfchanger_master", 2);
	public static final Block mftable = new MFTable("mftable", 0);
	public static final Block mftable_adavance = new MFTable("mftable_adavance", 1);
	public static final Block mftable_master = new MFTable("mftable_master", 2);
	public static final Block mftank = new MFTank("mftank", 0);
	public static final Block mftank_adavance = new MFTank("mftank_adavance", 1);
	public static final Block mftank_master = new MFTank("mftank_master", 2);
	public static final Block mftank_creative = new MFTank("mftank_creative", 3);
	public static final Block pedestal_creat = new PedalCreate("pedestal_creat", 0);
	public static final Block altar_creat = new PedalCreate("altar_creat", 1);
	public static final Block altar_creation_star = new PedalCreate("altar_creation_star", 2);
	public static final Block iris_creation = new IrisCreation("iris_creation");
	public static final Block aether_lanp = new AetherLanp("aether_lanp", 0);
	public static final Block high_aether_lantern = new AetherLanp("high_aether_lantern", 1);
	public static final Block aether_lamplight = new AetherLanp("aether_lamplight", 2);
	public static final Block aether_hopper = new AetherHopper("aether_hopper");
	public static final Block mffurnace = new MFFurnace("mffurnace", 0);
	public static final Block mffurnace_advance = new MFFurnace("mffurnace_advance", 1);
	public static final Block magia_lantern = new MagiaLantern("magia_lantern");

	public static final Block obmagia = new ObMagia("obmagia", false);
	public static final Block obmagia_top = new ObMagia("obmagia_top", true);
	public static final Block magia_table = new ToolRepair("magia_table", 13);

	public static final Block mf_fisher = new MFFisher("mf_fisher", 0);
	public static final Block mf_forer = new MFFisher("mf_forer", 1);
	public static final Block mf_squeezer = new MFFisher("mf_squeezer", 2);
	public static final Block mf_egg_launcher = new MFFisher("mf_egg_launcher", 6);
	public static final Block mf_woodcutter = new ToolRepair("mf_woodcutter", 14);
	public static final Block mf_miner = new MFFisher("mf_miner", 5);
	public static final Block mf_miner_advanced = new ToolRepair("mf_miner_advanced", 9);
	public static final Block mf_generater = new ToolRepair("mf_generater", 12);
	public static final Block mf_bottler = new ToolRepair("mf_bottler", 10);
	public static final Block mf_bottler_advance = new ToolRepair("mf_bottler_advance", 11);
	public static final Block aehter_furnace = new MFFisher("aehter_furnace", 3);
	public static final Block aehter_furnace_adavance = new MFFisher("aehter_furnace_adavance", 4);

	public static final Block aether_repair = new ToolRepair("aether_repair", 0);
	public static final Block enchant_educe = new ToolRepair("enchant_educe", 1);
	public static final Block magia_rewrite = new ToolRepair("magia_rewrite", 2);
	public static final Block aether_reverse = new ToolRepair("aether_reverse", 3);
	public static final Block magia_drawer = new ToolRepair("magia_drawer", 4);
	public static final Block magia_accelerator = new ToolRepair("magia_accelerator", 5);
	public static final Block accessory_processing_table = new ToolRepair("accessory_processing_table", 6);
	public static final Block aether_planter = new ToolRepair("aether_planter", 8);

	public static final Block aether_recycler = new AetherRecycler("aether_recycler");
	public static final Block warp_block = new WarpBlock("warp_block");

	public static final Block sturdust_crystal = new SturdustCrystal("sturdust_crystal");

	// 花瓶
	public static final Block carnation_crayola_pot = new MFPot("carnation_crayola_pot", 8, 1);
	public static final Block dm_pot = new MFPot("drizzly_mysotis_pot", 0, 1);
	public static final Block alstroemeria_pot = new MFPot("twilightalstroemeria_pot", 1, 2);
	public static final Block snowdrop_pot = new MFPot("snowdrop_pot", 2, 1);
	public static final Block turkey_balloonflower_pot = new MFPot("turkey_balloonflower_pot", 3, 1);
	public static final Block ultramarine_rose_pot = new MFPot("ultramarine_rose_pot", 4, 1);
	public static final Block solid_star_pot = new MFPot("solid_star_pot", 5, 2);
	public static final Block zinnia_pot = new MFPot("zinnia_pot", 6, 2);
	public static final Block hydrangea_pot = new MFPot("hydrangea_pot", 7, 1);
	public static final Block christmarose_pot = new MFPot("christmarose_ericsmithii_pot", 9, 1);
	public static final Block cosmos_pot = new MFPot("cosmos_pot", 10, 1);

	public static final Block carnation_crayola_aquarium = new AquariumPot("carnation_crayola_aquarium", carnation_crayola_pot, 8, 2);
	public static final Block dm_aquarium = new AquariumPot("drizzly_mysotis_aquarium", dm_pot, 0, 3);
	public static final Block snowdrop_aquarium = new AquariumPot("snowdrop_aquarium", snowdrop_pot, 2, 2);
	public static final Block turkey_balloonflower_aquarium = new AquariumPot("turkey_balloonflower_aquarium", turkey_balloonflower_pot, 3, 2);
	public static final Block ultramarine_rose_aquarium = new AquariumPot("ultramarine_rose_aquarium", ultramarine_rose_pot, 4, 2);
	public static final Block solid_star_aquarium = new AquariumPot("solid_star_aquarium", solid_star_pot, 5, 3);
	public static final Block zinnia_aquarium = new AquariumPot("zinnia_aquarium", zinnia_pot, 6, 3);
	public static final Block hydrangea_aquarium = new AquariumPot("hydrangea_aquarium", hydrangea_pot, 7, 2);
	public static final Block christmarose_aquarium = new AquariumPot("christmarose_ericsmithii_aquarium", christmarose_pot, 9, 2);
	public static final Block cosmos_aquarium = new AquariumPot("cosmos_aquarium", cosmos_pot, 10, 2);

	public static final Block rune_character = new RuneCharacter("rune_character");

	public static final Block summon_pedal = new SummonPedal("summon_pedal");
	public static final Block figurine_queenfrost = new BossFigurine("figurine_queenfrost", 0);
	public static final Block figurine_holyangel = new BossFigurine("figurine_holyangel", 1);
	public static final Block figurine_ignisknight = new BossFigurine("figurine_ignisknight", 2);
	public static final Block figurine_windwitch_master = new BossFigurine("figurine_windwitch_master", 3);
	public static final Block figurine_bullfight = new BossFigurine("figurine_bullfight", 4);
	public static final Block figurine_ancientfairy = new BossFigurine("figurine_ancientfairy", 5);
	public static final Block figurine_arlaune = new BossFigurine("figurine_arlaune", 6);
	public static final Block figurine_landroad = new BossFigurine("figurine_landroad", 7);
	public static final Block figurine_butler = new BossFigurine("figurine_butler", 8);
	public static final Block figurine_hora = new BossFigurine("figurine_hora", 9);
	public static final Block figurine_brave = new BossFigurine("figurine_brave", 10);
	public static final Block figurine_curious = new BossFigurine("figurine_curious", 11);
	public static final Block figurine_sandryon = new BossFigurine("figurine_sandryon", 12);
	public static final Block figurine_blitz = new BossFigurine("figurine_blitz", 13);
	public static final Block figurine_stella = new BossFigurine("figurine_stella", 14);
	public static final Block figurine_belial = new BossFigurine("figurine_belial", 15);

	public static final Block alternative_tank = new AlternativeTank("alternative_tank", 0);
	public static final Block cosmos_light_tank = new AlternativeTank("cosmos_light_tank", 1);

	// 調理ブロック
	public static final Block mill = new Mill("mill");
	public static final Block oven = new Oven("oven");
	public static final Block frypan_r = new Frypan("frypan_r");
	public static final Block pot_w = new Pot("pot_w");
	public static final Block bottle = new Bottle("bottle");
	public static final Block stove = new Stove("stove");

	public static final Block juice_maker = new JuiceMaker("juice_maker");
	public static final Block freezer_top = new Freezer("freezer_top", 0);
	public static final Block freezer = new FreezerChest("freezer", freezer_top);

	public static final Block frypan_l = new Frypan("frypan_l", 1);
	public static final Block frypan_o = new Frypan("frypan_o", 1);
	public static final Block frypan_pi = new Frypan("frypan_pi", 1);
	public static final Block frypan_pu = new Frypan("frypan_pu", 1);
	public static final Block frypan_s = new Frypan("frypan_s", 1);

	public static final Block pot_r = new Pot("pot_r", 1);
	public static final Block pot_l = new Pot("pot_l", 1);
	public static final Block pot_o = new Pot("pot_o", 1);
	public static final Block pot_pi = new Pot("pot_pi", 1);
	public static final Block pot_pu = new Pot("pot_pu", 1);
	public static final Block pot_s = new Pot("pot_s", 1);

	public static final Block single_pot_r = new Pot("single_pot_r", 2);
	public static final Block single_pot_l = new Pot("single_pot_l", 2);
	public static final Block single_pot_o = new Pot("single_pot_o", 2);
	public static final Block single_pot_pi = new Pot("single_pot_pi", 2);
	public static final Block single_pot_pu = new Pot("single_pot_pu", 2);
	public static final Block single_pot_s = new Pot("single_pot_s", 2);

	public static final Block mixer_r = new Mill("mixer_r", 1);
	public static final Block mixer_l = new Mill("mixer_l", 1);
	public static final Block mixer_o = new Mill("mixer_o", 1);
	public static final Block mixer_pi = new Mill("mixer_pi", 1);
	public static final Block mixer_pu = new Mill("mixer_pu", 1);
	public static final Block mixer_s = new Mill("mixer_s", 1);

	public static final Block coffee_maker_r = new JuiceMaker("coffee_maker_r", 1);
	public static final Block coffee_maker_l = new JuiceMaker("coffee_maker_l", 1);
	public static final Block coffee_maker_o = new JuiceMaker("coffee_maker_o", 1);
	public static final Block coffee_maker_pi = new JuiceMaker("coffee_maker_pi", 1);
	public static final Block coffee_maker_pu = new JuiceMaker("coffee_maker_pu", 1);
	public static final Block coffee_maker_s = new JuiceMaker("coffee_maker_s", 1);

	public static final Block toaster_r = new Oven("toaster_r", 2);
	public static final Block toaster_l = new Oven("toaster_l", 2);
	public static final Block toaster_o = new Oven("toaster_o", 2);
	public static final Block toaster_pi = new Oven("toaster_pi", 2);
	public static final Block toaster_pu = new Oven("toaster_pu", 2);
	public static final Block toaster_s = new Oven("toaster_s", 2);

	public static final Block freezer_top_r = new Freezer("freezer_top_r", 1);
	public static final Block freezer_r = new FreezerChest("freezer_r", freezer_top_r, 1);
	public static final Block freezer_top_l = new Freezer("freezer_top_l", 1);
	public static final Block freezer_l = new FreezerChest("freezer_l", freezer_top_l, 1);
	public static final Block freezer_top_o = new Freezer("freezer_top_o", 1);
	public static final Block freezer_o = new FreezerChest("freezer_o", freezer_top_o, 1);
	public static final Block freezer_top_pi = new Freezer("freezer_top_pi", 1);
	public static final Block freezer_pi = new FreezerChest("freezer_pi", freezer_top_pi, 1);
	public static final Block freezer_top_pu = new Freezer("freezer_top_pu", 1);
	public static final Block freezer_pu = new FreezerChest("freezer_pu", freezer_top_pu, 1);
	public static final Block freezer_top_s = new Freezer("freezer_top_s", 1);
	public static final Block freezer_s = new FreezerChest("freezer_s", freezer_top_s, 1);

	public static final Block couter_table_white_brick_r = new CounterTable("couter_table_white_brick_r");
	public static final Block couter_table_sink_white_brick_r = new CounterTableSink("couter_table_sink_white_brick_r");
	public static final Block couter_table_oven_white_brick_r = new Oven("couter_table_oven_white_brick_r", 1);
	public static final Block couter_table_stove_white_brick_r = new Stove("couter_table_stove_white_brick_r", SweetMagicCore.smTab);
	public static final Block couter_table_chest_white_brick_r = new WoodChest("couter_table_chest_white_brick_r", 7);

	public static final Block couter_table_white_brick_y = new CounterTable("couter_table_white_brick_y");
	public static final Block couter_table_sink_white_brick_y = new CounterTableSink("couter_table_sink_white_brick_y");
	public static final Block couter_table_oven_white_brick_y = new Oven("couter_table_oven_white_brick_y", 1);
	public static final Block couter_table_stove_white_brick_y = new Stove("couter_table_stove_white_brick_y", SweetMagicCore.smTab);
	public static final Block couter_table_chest_white_brick_y = new WoodChest("couter_table_chest_white_brick_y", 7);

	public static final Block couter_table_white_brick_b = new CounterTable("couter_table_white_brick_b");
	public static final Block couter_table_sink_white_brick_b = new CounterTableSink("couter_table_sink_white_brick_b");
	public static final Block couter_table_oven_white_brick_b = new Oven("couter_table_oven_white_brick_b", 1);
	public static final Block couter_table_stove_white_brick_b = new Stove("couter_table_stove_white_brick_b", SweetMagicCore.smTab);
	public static final Block couter_table_chest_white_brick_b = new WoodChest("couter_table_chest_white_brick_b", 7);

	public static final Block couter_table_white_brick_m = new CounterTable("couter_table_white_brick_m");
	public static final Block couter_table_sink_white_brick_m = new CounterTableSink("couter_table_sink_white_brick_m");
	public static final Block couter_table_oven_white_brick_m = new Oven("couter_table_oven_white_brick_m", 1);
	public static final Block couter_table_stove_white_brick_m = new Stove("couter_table_stove_white_brick_m", SweetMagicCore.smTab);
	public static final Block couter_table_chest_white_brick_m = new WoodChest("couter_table_chest_white_brick_m", 7);

	public static final Block couter_table_white_brick_w = new CounterTable("couter_table_white_brick_w");
	public static final Block couter_table_sink_white_brick_w = new CounterTableSink("couter_table_sink_white_brick_w");
	public static final Block couter_table_oven_white_brick_w = new Oven("couter_table_oven_white_brick_w", 1);
	public static final Block couter_table_stove_white_brick_w = new Stove("couter_table_stove_white_brick_w", SweetMagicCore.smTab);
	public static final Block couter_table_chest_white_brick_w = new WoodChest("couter_table_chest_white_brick_w", 7);

	public static final Block range_food = new RangeFood("range_food", 0);
	public static final Block range_food_light = new RangeFood("range_food_light", 1);

	// チェスト
	public static final Block wood_chest = new WoodChest("wood_chest", 5);
	public static final Block wood_chest_m = new WoodChest("wood_chest_m", 5);
	public static final Block wood_chest_t = new WoodChest("wood_chest_t", 5);
	public static final Block wood_chest_b = new WoodChest("wood_chest_b", 5);
	public static final Block moden_shelf_y = new WoodChest("moden_shelf_y", 0);
	public static final Block moden_shelf_i = new WoodChest("moden_shelf_i", 0);
	public static final Block moden_shelf_l = new WoodChest("moden_shelf_l", 0);
	public static final Block moden_shelf_o = new WoodChest("moden_shelf_o", 0);
	public static final Block moden_shelf_p = new WoodChest("moden_shelf_p", 0);
	public static final Block moden_shelf_r = new WoodChest("moden_shelf_r", 0);
	public static final Block moden_shelf_s = new WoodChest("moden_shelf_s", 0);
	public static final Block woodbox = new WoodChest("woodbox", 1);
	public static final Block treasure_chest = new WoodChest("treasure_chest", 2);
	public static final Block trash_can_s = new TrashCan("trash_can_s", 4);
	public static final Block trash_can_y = new TrashCan("trash_can_y", 4);
	public static final Block trash_can_r = new TrashCan("trash_can_r", 4);
	public static final Block trash_can_o = new TrashCan("trash_can_o", 4);
	public static final Block trash_can_pi = new TrashCan("trash_can_pi", 4);
	public static final Block trash_can_pu = new TrashCan("trash_can_pu", 4);
	public static final Block japanese_dance = new WoodChest("japanese_dance", 5);
	public static final Block western_style_dance = new WoodChest("western_style_dance", 5);
	public static final Block lowshelf = new WoodChest("lowshelf", 5);
	public static final Block post_w = new WoodChest("post_w", 9);
	public static final Block post_r = new WoodChest("post_r", 9);
	public static final Block post_wood_orange = new WoodChest("post_wood_orange", 10);
	public static final Block post_wood_orange_w = new WoodChest("post_wood_orange_w", 10);
	public static final Block post_wood_orange_m = new WoodChest("post_wood_orange_m", 10);
	public static final Block post_wood_orange_pi = new WoodChest("post_wood_orange_pi", 10);
	public static final Block post_wood_orange_magia = new WoodChest("post_wood_orange_magia", 10);
	public static final Block wall_cabinet_l = new WoodChest("wall_cabinet_l", 11);
	public static final Block wall_cabinet_o = new WoodChest("wall_cabinet_o", 11);
	public static final Block wall_cabinet_pi = new WoodChest("wall_cabinet_pi", 11);
	public static final Block wall_cabinet_pu = new WoodChest("wall_cabinet_pu", 11);
	public static final Block wall_cabinet_r = new WoodChest("wall_cabinet_r", 11);
	public static final Block wall_cabinet_s = new WoodChest("wall_cabinet_s", 11);
	public static final Block wall_cabinet_y = new WoodChest("wall_cabinet_y", 11);
	public static final Block wall_cabinet_lemon = new WoodChest("wall_cabinet_lemon", 11);
	public static final Block wall_cabinet_magia = new WoodChest("wall_cabinet_magia", 11);
	public static final Block wall_cabinet_maple = new WoodChest("wall_cabinet_maple", 11);

	public static final Block cardboard_storage = new CardboardStorage("cardboard_storage");
	public static final Block dresser = new Dresser("dresser");

	public static final Block trunk_case_s = new TrunkCase("trunk_case_s", 0);
	public static final Block trunk_case_pi = new TrunkCase("trunk_case_pi", 0);
	public static final Block trunk_case_pu = new TrunkCase("trunk_case_pu", 0);
	public static final Block trunk_case_o = new TrunkCase("trunk_case_o", 0);
	public static final Block trunk_case_l = new TrunkCase("trunk_case_l", 0);
	public static final Block trunk_case_bu = new TrunkCase("trunk_case_bu", 0);
	public static final Block trunk_case_bl = new TrunkCase("trunk_case_bl", 0);
	public static final Block trunk_case_w = new TrunkCase("trunk_case_w", 0);
	public static final Block sugarbell_switch_br = new TrunkCase("sugarbell_switch_br", 1);
	public static final Block sugarbell_switch_by = new TrunkCase("sugarbell_switch_by", 1);
	public static final Block sugarbell_switch_ls = new TrunkCase("sugarbell_switch_ls", 1);
	public static final Block sugarbell_switch_pul = new TrunkCase("sugarbell_switch_pul", 1);
	public static final Block sugarbell_switch_w = new TrunkCase("sugarbell_switch_w", 1);

	public static final Block parallel_interfere = new ParallelInterfere("parallel_interfere", 0);
	public static final Block stardust_wish = new ParallelInterfere("stardust_wish", 1);

	public static final Block magia_storage_1 = new MagiaStorage("magia_storage_1", 0);
	public static final Block magia_storage_2 = new MagiaStorage("magia_storage_2", 1);
	public static final Block magia_storage_3 = new MagiaStorage("magia_storage_3", 2);
	public static final Block magia_storage_4 = new MagiaStorage("magia_storage_4", 3);
	public static final Block magia_storage_5 = new MagiaStorage("magia_storage_5", 4);

	public static final Block aether_crafttable = new AetherCraftTable("aether_crafttable");
	public static final Block chest_reader = new ChestReader("chest_reader");

	public static final Block moden_rack = new ModenRack("moden_rack", 0, false);
	public static final Block moden_rack_m = new ModenRack("moden_rack_m", 0, false);
	public static final Block moden_rack_t = new ModenRack("moden_rack_t", 0, false);
	public static final Block moden_rack_b = new ModenRack("moden_rack_b", 0, false);

	public static final Block wall_rack = new ModenRack("wall_rack", 1, false);
	public static final Block wall_rack_m = new ModenRack("wall_rack_m", 1, false);
	public static final Block wall_rack_t = new ModenRack("wall_rack_t", 1, false);
	public static final Block wall_rack_b = new ModenRack("wall_rack_b", 1, false);
	public static final Block wall_rack_i = new ModenRack("wall_rack_i", 1, true);

	public static final Block wall_shelf = new ModenRack("wall_shelf", 2, false);
	public static final Block wall_shelf_m = new ModenRack("wall_shelf_m", 2, false);
	public static final Block wall_shelf_t = new ModenRack("wall_shelf_t", 2, false);
	public static final Block wall_shelf_b = new ModenRack("wall_shelf_b", 2, false);
	public static final Block wall_shelf_i = new ModenRack("wall_shelf_i", 2, true);

	public static final Block wall_partition = new ModenRack("wall_partition", 3, false);
	public static final Block wall_partition_m = new ModenRack("wall_partition_m", 3, false);
	public static final Block wall_partition_t = new ModenRack("wall_partition_t", 3, false);
	public static final Block wall_partition_b = new ModenRack("wall_partition_b", 3, false);

	public static final Block bottle_rack_t = new ModenRack("bottle_rack_t", 4, false);
	public static final Block bottle_rack_d = new ModenRack("bottle_rack_d", 4, false);
	public static final Block bottle_rack_b = new ModenRack("bottle_rack_b", 4, false);

	public static final Block ceiling_shelf = new ModenRack("ceiling_shelf", 5, false);

	public static final Block fruit_crate_chestnut = new ModenRack("fruit_crate_chestnut", 6, false);
	public static final Block fruit_crate_cherry = new ModenRack("fruit_crate_cherry", 6, false);
	public static final Block fruit_crate_cherry_r = new ModenRack("fruit_crate_cherry_r", 6, false);
	public static final Block fruit_crate_coconut = new ModenRack("fruit_crate_coconut", 6, false);
	public static final Block fruit_crate_orange_w = new ModenRack("fruit_crate_orange_w", 6, false);
	public static final Block fruit_crate_orange_m = new ModenRack("fruit_crate_orange_m", 6, false);
	public static final Block fruit_crate_orange_pi = new ModenRack("fruit_crate_orange_pi", 6, false);
	public static final Block fruit_crate_magia = new ModenRack("fruit_crate_magia", 6, false);
	public static final Block fruit_crate_black = new ModenRack("fruit_crate_black", 6, false);

	public static final Block fruit_crate_box_chestnut = new ModenRack("fruit_crate_box_chestnut", 7, false);
	public static final Block fruit_crate_box_cherry = new ModenRack("fruit_crate_box_cherry", 7, false);
	public static final Block fruit_crate_box_cherry_r = new ModenRack("fruit_crate_box_cherry_r", 7, false);
	public static final Block fruit_crate_box_coconut = new ModenRack("fruit_crate_box_coconut", 7, false);
	public static final Block fruit_crate_box_orange_w = new ModenRack("fruit_crate_box_orange_w", 7, false);
	public static final Block fruit_crate_box_orange_m = new ModenRack("fruit_crate_box_orange_m", 7, false);
	public static final Block fruit_crate_box_orange_pi = new ModenRack("fruit_crate_box_orange_pi", 7, false);
	public static final Block fruit_crate_box_magia = new ModenRack("fruit_crate_box_magia", 7, false);
	public static final Block fruit_crate_box_black = new ModenRack("fruit_crate_box_black", 7, false);

	public static final Block wooden_tool_box_chestnut = new ModenRack("wooden_tool_box_chestnut", 8, false);
	public static final Block wooden_tool_box_coconut = new ModenRack("wooden_tool_box_coconut", 8, false);

	public static final Block wall_boxshelf = new ModenRack("wall_boxshelf", 9, false);
	public static final Block wall_boxshelf_m = new ModenRack("wall_boxshelf_m", 9, false);
	public static final Block wall_boxshelf_t = new ModenRack("wall_boxshelf_t", 9, false);
	public static final Block wall_boxshelf_b = new ModenRack("wall_boxshelf_b", 9, false);

	public static final Block wand_pedastal = new WandPedastal("wand_pedastal", 0);
	public static final Block decorative_stand = new WandPedastal("decorative_stand", 4);
	public static final Block corkboard = new WandPedastal("corkboard", 1);
	public static final Block wallboard = new WandPedastal("wallboard", 1);
	public static final Block wallboard_b = new WandPedastal("wallboard_b", 1);
	public static final Block hanging_sign = new WandPedastal("hanging_sign", 2);
	public static final Block hanging_sign_wood = new WandPedastal("hanging_sign_wood", 2);
	public static final Block item_menu_b = new WandPedastal("item_menu_b", 3);
	public static final Block item_menu_w = new WandPedastal("item_menu_w", 3);

	public static final Block plate = new Plate("plate", 0, null);
	public static final Block wood_plate = new Plate("wood_plate", 0, plate);
	public static final Block plate_o = new Plate("plate_o", 0, plate);
	public static final Block plate_p = new Plate("plate_p", 0, plate);
	public static final Block plate_i = new Plate("plate_i", 0, plate);
	public static final Block plate_s = new Plate("plate_s", 0, plate);
	public static final Block wood_tray = new Plate("wood_tray", 1, null);
	public static final Block basket = new Plate("basket", 2, null);
	public static final Block diagonal_basket = new Plate("diagonal_basket", 4, null);
	public static final Block showcase = new ShowCase("showcase", 3);

	public static final Block register_bu = new Register("register_bu", null );
	public static final Block register_br = new Register("register_br", register_bu);
	public static final Block register_bl = new Register("register_bl", register_bu);
	public static final Block register_l = new Register("register_l", register_bu);
	public static final Block register_o = new Register("register_o", register_bu);
	public static final Block register_pi = new Register("register_pi", register_bu);
	public static final Block register_pu = new Register("register_pu", register_bu);
	public static final Block register_r = new Register("register_r", register_bu);
	public static final Block register_s = new Register("register_s", register_bu);

	public static final Block note_pc_b = new NotePC("note_pc_b", null);
	public static final Block note_pc_l = new NotePC("note_pc_l", note_pc_b);
	public static final Block note_pc_o = new NotePC("note_pc_o", note_pc_b);
	public static final Block note_pc_pi = new NotePC("note_pc_pi", note_pc_b);
	public static final Block note_pc_pu = new NotePC("note_pc_pu", note_pc_b);
	public static final Block note_pc_r = new NotePC("note_pc_r", note_pc_b);
	public static final Block note_pc_s = new NotePC("note_pc_s", note_pc_b);

	public static final Block chopping_board = new ChoppingBoard("chopping_board", null);
	public static final Block wood_chopping_board = new ChoppingBoard("wood_chopping_board", chopping_board);
	public static final Block chopping_board_o = new ChoppingBoard("chopping_board_o", chopping_board);
	public static final Block chopping_board_p = new ChoppingBoard("chopping_board_p", chopping_board);
	public static final Block chopping_board_i = new ChoppingBoard("chopping_board_i", chopping_board);
	public static final Block chopping_board_s = new ChoppingBoard("chopping_board_s", chopping_board);

	public static final Block garden_fence_white = new IronFence("garden_fence_white", 0);
	public static final Block garden_fence_black = new IronFence("garden_fence_black", 0);
	public static final Block gothic_fance_white = new IronFence("gothic_fance_white", 0);
	public static final Block gothic_fance_black = new IronFence("gothic_fance_black", 0);

	public static final Block iron_railings_b = new IronFence("iron_railings_b", 0);
	public static final Block iron_railings_w = new IronFence("iron_railings_w", 0);

	public static final Block frosted_glass_handrail = new IronFence("frosted_glass_handrail", 0);

	public static final Block log_fence_pearch_v = new IronFence("log_fence_pearch_v", 1);
	public static final Block log_fence_pearch_h = new IronFence("log_fence_pearch_h", 1);
	public static final Block log_fence_prism_v = new IronFence("log_fence_prism_v", 1);
	public static final Block log_fence_prism_h = new IronFence("log_fence_prism_h", 1);
	public static final Block log_fence_magia_v = new IronFence("log_fence_magia_v", 1);
	public static final Block log_fence_magia_h = new IronFence("log_fence_magia_h", 1);
	public static final Block log_fence_maple_v = new IronFence("log_fence_maple_v", 1);
	public static final Block log_fence_maple_h = new IronFence("log_fence_maple_h", 1);

	public static final Block stone_fence_antique_r = new IronFence("stone_fence_antique_r", 2);
	public static final Block stone_fence_antique_w = new IronFence("stone_fence_antique_w", 2);
	public static final Block stone_fence_antique_b = new IronFence("stone_fence_antique_b", 2);
	public static final Block stone_fence_whiteline_r = new IronFence("stone_fence_whiteline_r", 2);
	public static final Block stone_fence_whiteline_b = new IronFence("stone_fence_whiteline_b", 2);
	public static final Block stone_fence_whiteline_y = new IronFence("stone_fence_whiteline_y", 2);
	public static final Block stone_fence_design_w = new IronFence("stone_fence_design_w", 2);
	public static final Block stone_fence_design_bl = new IronFence("stone_fence_design_bl", 2);
	public static final Block stone_fence_simplestone_n = new IronFence("stone_fence_simplestone_n", 2);
	public static final Block stone_fence_simplestone_g = new IronFence("stone_fence_simplestone_g", 2);

	public static final Block fence_pole_chestnut = new WoodPole("fence_pole_chestnut", 0);
	public static final Block fence_pole_lemon = new WoodPole("fence_pole_lemon", 0);
	public static final Block fence_pole_orange = new WoodPole("fence_pole_orange", 0);
	public static final Block fence_pole_coconut = new WoodPole("fence_pole_coconut", 0);
	public static final Block fence_pole_prism = new WoodPole("fence_pole_prism", 0);
	public static final Block fence_pole_estor = new WoodPole("fence_pole_estor", 0);
	public static final Block fence_pole_pearch = new WoodPole("fence_pole_pearch", 0);
	public static final Block fence_pole_magiawood = new WoodPole("fence_pole_magiawood", 0);
	public static final Block fence_pole_maple = new WoodPole("fence_pole_maple", 0);
	public static final Block fence_pole_iron = new WoodPole("fence_pole_iron", 1);

	public static final Block awning_tent_b = new AwningTent("awning_tent_b");
	public static final Block awning_tent_l = new AwningTent("awning_tent_l");
	public static final Block awning_tent_o = new AwningTent("awning_tent_o");
	public static final Block awning_tent_pi = new AwningTent("awning_tent_pi");
	public static final Block awning_tent_p = new AwningTent("awning_tent_p");
	public static final Block awning_tent_r = new AwningTent("awning_tent_r");
	public static final Block awning_tent_s = new AwningTent("awning_tent_s");

	public static final Block planting_chestnut = new Planting("planting_chestnut");
	public static final Block planting_orange = new Planting("planting_orange");
	public static final Block planting_estor = new Planting("planting_estor");
	public static final Block planting_peach = new Planting("planting_peach");

	public static final Block metal_pole = new MetalPole("metal_pole", 1);

	public static final Block armrest_dining_chair = new SMChair("armrest_dining_chair", 0);
	public static final Block armrest_dining_chair_b = new SMChair("armrest_dining_chair_b", 0);
	public static final Block armrest_dining_chair_l = new SMChair("armrest_dining_chair_l", 0);
	public static final Block dining_chair = new SMChair("dining_chair", 1);
	public static final Block dining_chair_b = new SMChair("dining_chair_b", 1);
	public static final Block dining_chair_l = new SMChair("dining_chair_l", 1);
	public static final Block cafe_chair = new SMChair("cafe_chair", 2);
	public static final Block cafe_chair_b = new SMChair("cafe_chair_b", 2);
	public static final Block cafe_chair_l = new SMChair("cafe_chair_l", 2);
	public static final Block dresser_chair = new SMChair("dresser_chair", 4);
	public static final Block backrest_woodbench_d = new WoodBench("backrest_woodbench_d", 3);
	public static final Block backrest_woodbench_t = new WoodBench("backrest_woodbench_t", 3);
	public static final Block backrest_woodbench_b = new WoodBench("backrest_woodbench_b", 3);
	public static final Block woodbench_d = new WoodBench("woodbench_d", 3);
	public static final Block woodbench_t = new WoodBench("woodbench_t", 3);
	public static final Block woodbench_b = new WoodBench("woodbench_b", 3);
	public static final Block simple_chair = new SMChair("simple_chair", 1);
	public static final Block counter_chair_b = new SMChair("counter_chair_b", 2);
	public static final Block counter_chair_l = new SMChair("counter_chair_l", 2);
	public static final Block counter_chair_o = new SMChair("counter_chair_o", 2);
	public static final Block counter_chair_pi = new SMChair("counter_chair_pi", 2);
	public static final Block counter_chair_pu = new SMChair("counter_chair_pu", 2);
	public static final Block counter_chair_r = new SMChair("counter_chair_r", 2);
	public static final Block counter_chair_s = new SMChair("counter_chair_s", 2);
	public static final Block counter_chair_y = new SMChair("counter_chair_y", 2);
	public static final Block counter_chair_w = new SMChair("counter_chair_w", 2);

	public static final Block sofa_b = new Sofa("sofa_b");
	public static final Block sofa_l = new Sofa("sofa_l");
	public static final Block sofa_o = new Sofa("sofa_o");
	public static final Block sofa_pi = new Sofa("sofa_pi");
	public static final Block sofa_pu = new Sofa("sofa_pu");
	public static final Block sofa_r = new Sofa("sofa_r");
	public static final Block sofa_s = new Sofa("sofa_s");
	public static final Block sofa_y = new Sofa("sofa_y");

	public static final Block carpet_o = new WoolCarpet("carpet_o", null);
	public static final Block carpet_p = new WoolCarpet("carpet_p", carpet_o);
	public static final Block carpet_s = new WoolCarpet("carpet_s", carpet_o);

	public static final Block moden_table_d = new SMTable("moden_table_d", 0);
	public static final Block moden_table_t = new SMTable("moden_table_t", 0);
	public static final Block moden_table_b = new SMTable("moden_table_b", 0);
	public static final Block smtable = new SMTable("smtable", 0);
	public static final Block smtable_lace = new SMTable("smtable_lace", 0);
	public static final Block simple_table = new SMTable("simple_table", 2);
	public static final Block cafe_table = new SMTable("cafe_table", 2);

	public static final Block moden_stairs_t = new ModenStair("moden_stairs_t");
	public static final Block moden_stairs_b = new ModenStair("moden_stairs_b");
	public static final Block moden_stairs_w = new ModenStair("moden_stairs_w");
	public static final Block moden_stairs_chestnut = new ModenStair("moden_stairs_chestnut");
	public static final Block moden_stairs_lemon = new ModenStair("moden_stairs_lemon");
	public static final Block moden_stairs_orange = new ModenStair("moden_stairs_orange");
	public static final Block moden_stairs_orange_w = new ModenStair("moden_stairs_orange_w");
	public static final Block moden_stairs_orange_p = new ModenStair("moden_stairs_orange_p");
	public static final Block moden_stairs_orange_m = new ModenStair("moden_stairs_orange_m");
	public static final Block moden_stairs_coconut = new ModenStair("moden_stairs_coconut");
	public static final Block moden_stairs_magiawood = new ModenStair("moden_stairs_magiawood");
	public static final Block moden_stairs_cherry = new ModenStair("moden_stairs_cherry");
	public static final Block moden_stairs_cherry_r = new ModenStair("moden_stairs_cherry_r");
	public static final Block moden_stairs_maple = new ModenStair("moden_stairs_maple");
	public static final Block moden_stairs_maple_w = new ModenStair("moden_stairs_maple_w");
	public static final Block moden_stairs_unsupport_t = new ModenStair("moden_stairs_unsupport_t");
	public static final Block moden_stairs_unsupport_b = new ModenStair("moden_stairs_unsupport_b");
	public static final Block moden_stairs_unsupport_w = new ModenStair("moden_stairs_unsupport_w");
	public static final Block moden_stairs_unsupport_chestnut = new ModenStair("moden_stairs_unsupport_chestnut");
	public static final Block moden_stairs_unsupport_lemon = new ModenStair("moden_stairs_unsupport_lemon");
	public static final Block moden_stairs_unsupport_orange = new ModenStair("moden_stairs_unsupport_orange");
	public static final Block moden_stairs_unsupport_orange_w = new ModenStair("moden_stairs_unsupport_orange_w");
	public static final Block moden_stairs_unsupport_orange_p = new ModenStair("moden_stairs_unsupport_orange_p");
	public static final Block moden_stairs_unsupport_orange_m = new ModenStair("moden_stairs_unsupport_orange_m");
	public static final Block moden_stairs_unsupport_coconut = new ModenStair("moden_stairs_unsupport_coconut");
	public static final Block moden_stairs_unsupport_magiawood = new ModenStair("moden_stairs_unsupport_magiawood");
	public static final Block moden_stairs_unsupport_cherry = new ModenStair("moden_stairs_unsupport_cherry");
	public static final Block moden_stairs_unsupport_cherry_r = new ModenStair("moden_stairs_unsupport_cherry_r");
	public static final Block moden_stairs_unsupport_maple = new ModenStair("moden_stairs_unsupport_maple");
	public static final Block moden_stairs_unsupport_maple_w = new ModenStair("moden_stairs_unsupport_maple_w");

	public static final Block furniture_processing_table = new FurnitureTable("furniture_processing_table");

	public static final Block roller_blind_b = new RollerBlind("roller_blind_b");
	public static final Block roller_blind_l = new RollerBlind("roller_blind_l");
	public static final Block roller_blind_o = new RollerBlind("roller_blind_o");
	public static final Block roller_blind_pi = new RollerBlind("roller_blind_pi");
	public static final Block roller_blind_pu = new RollerBlind("roller_blind_pu");
	public static final Block roller_blind_r = new RollerBlind("roller_blind_r");
	public static final Block roller_blind_s = new RollerBlind("roller_blind_s");
	public static final Block roller_blind_y = new RollerBlind("roller_blind_y");

	public static final Block ceiling_fan = new CeilingFan("ceiling_fan");

	public static final Block cafeboard_b = new CafeBoard("cafeboard_b", 0);
	public static final Block cafeboard_w = new CafeBoard("cafeboard_w", 0);

	public static final Block menu_list_b = new CafeBoard("menu_list_b", 1);
	public static final Block menu_list_w = new CafeBoard("menu_list_w", 1);
	public static final Block sm_poster = new CafeBoard("sm_poster", 1);
	public static final Block sm_poster_als = new CafeBoard("sm_poster_als", 1);
	public static final Block sm_poster_sweet = new CafeBoard("sm_poster_sweet", 1);

	public static final Block wallhung_cafesign = new WallHungCafeSign("wallhung_cafesign");

	public static final Block book_shelf_t = new BookShelf("book_shelf_t");
	public static final Block book_shelf_b = new BookShelf("book_shelf_b");
	public static final Block book_shelf_m = new BookShelf("book_shelf_m");

	public static final Block tong_stand = new FaceWood("tong_stand", 0);
	public static final Block tinplate_bucket = new FaceWood("tinplate_bucket", 1);
	public static final Block bread_baskets = new FaceWood("bread_baskets", 2);
	public static final Block hardbread_basket = new FaceWood("hardbread_basket", 2);
	public static final Block stacks_books_vertical = new FaceWood("stacks_books_vertical", 3);
	public static final Block stacks_books = new FaceWood("stacks_books", 4);
	public static final Block candlestick = new CandleStick("candlestick");
	public static final Block showcase_stand = new FaceWood("showcase_stand", 6);

	public static final Block wall_towel = new WallTowel("wall_towel");

	public static final Block woodbed_b = new WoodBed("woodbed_b");
	public static final Block woodbed_l = new WoodBed("woodbed_l");
	public static final Block woodbed_o = new WoodBed("woodbed_o");
	public static final Block woodbed_pi = new WoodBed("woodbed_pi");
	public static final Block woodbed_pu = new WoodBed("woodbed_pu");
	public static final Block woodbed_r = new WoodBed("woodbed_r");
	public static final Block woodbed_s = new WoodBed("woodbed_s");

	// 花
	public static final Block cornflower = new SMFlowerDiff("cornflower");
	public static final Block lily_valley = new SMFlowerDiff("lily_valley");
	public static final Block cosmos = new SMFlower("cosmos");
	public static final Block blackrose = new SMFlower("blackrose");
	public static final Block white_clover = new SMFlowerDiff("white_clover");
	public static final Block foxtail_grass = new SMFlower("foxtail_grass");
	public static final Block snowdrop = new SMFlower("snowdrop");
	public static final Block turkey_balloonflower = new SMFlower("turkey_balloonflower");
	public static final Block iberis_umbellata = new SMFlower("iberis_umbellata");
	public static final Block ultramarine_rose = new SMFlower("ultramarine_rose");
	public static final Block solid_star = new SMFlower("solid_star");
	public static final Block zinnia = new SMFlower("zinnia");
	public static final Block campanula = new SMFlower("campanula");
	public static final Block primula_polyansa = new SMFlower("primula_polyansa");
	public static final Block hydrangea = new SMFlowerDiff("hydrangea");
	public static final Block carnation_crayola = new SMFlower("carnation_crayola");
	public static final Block christmas_rose = new SMFlower("christmas_rose");
	public static final Block portulaca = new SMFlower("portulaca");
	public static final Block surfinia = new SMFlower("surfinia");
	public static final Block pansy_blue = new SMFlowerDiff("pansy_blue");
	public static final Block pansy_yellowmazenta = new SMFlower("pansy_yellowmazenta");
	public static final Block marigold = new SMFlower("marigold");
	public static final Block christmarose_ericsmithii = new SMFlower("christmarose_ericsmithii");

	public static final Block cornflower_flowerpot = new SMFlowerPot("cornflower_flowerpot", cornflower);
	public static final Block lily_valley_flowerpot = new SMFlowerPot("lily_valley_flowerpot", lily_valley);
	public static final Block cosmos_flowerpot = new SMFlowerPot("cosmos_flowerpot", cosmos);
	public static final Block blackrose_flowerpot = new SMFlowerPot("blackrose_flowerpot", blackrose);
	public static final Block white_clover_flowerpot = new SMFlowerPot("white_clover_flowerpot", white_clover);
	public static final Block foxtail_grass_flowerpot = new SMFlowerPot("foxtail_grass_flowerpot", foxtail_grass);
	public static final Block snowdrop_flowerpot = new SMFlowerPot("snowdrop_flowerpot", snowdrop);
	public static final Block turkey_balloonflower_flowerpot = new SMFlowerPot("turkey_balloonflower_flowerpot", turkey_balloonflower);
	public static final Block iberis_umbellata_flowerpot = new SMFlowerPot("iberis_umbellata_flowerpot", iberis_umbellata);
	public static final Block ultramarine_rose_flowerpot = new SMFlowerPot("ultramarine_rose_flowerpot", ultramarine_rose);
	public static final Block solid_star_flowerpot = new SMFlowerPot("solid_star_flowerpot", solid_star);
	public static final Block zinnia_flowerpot = new SMFlowerPot("zinnia_flowerpot", zinnia);
	public static final Block campanula_flowerpot = new SMFlowerPot("campanula_flowerpot", campanula);
	public static final Block primula_polyansa_flowerpot = new SMFlowerPot("primula_polyansa_flowerpot", primula_polyansa);
	public static final Block hydrangea_flowerpot = new SMFlowerPot("hydrangea_flowerpot", hydrangea);
	public static final Block carnation_crayola_flowerpot = new SMFlowerPot("carnation_crayola_flowerpot", carnation_crayola);
	public static final Block christmas_rose_flowerpot = new SMFlowerPot("christmas_rose_flowerpot", christmas_rose);
	public static final Block portulaca_flowerpot = new SMFlowerPot("portulaca_flowerpot", portulaca);
	public static final Block surfinia_flowerpot = new SMFlowerPot("surfinia_flowerpot", surfinia);
	public static final Block pansy_blue_flowerpot = new SMFlowerPot("pansy_blue_flowerpot", pansy_blue);
	public static final Block pansy_yellowmazenta_flowerpot = new SMFlowerPot("pansy_yellowmazenta_flowerpot", pansy_yellowmazenta);
	public static final Block marigold_flowerpot = new SMFlowerPot("marigold_flowerpot", marigold);
	public static final Block christmarose_ericsmithii_flowerpot = new SMFlowerPot("christmarose_ericsmithii_flowerpot", christmarose_ericsmithii);

	// 作物
	public static final Block sugarbell_plant = new SweetCrops_STAGE3("sugarbell_plant", 0, 3, false);
	public static final Block fire_nasturtium_plant = new SweetCrops_STAGE3("fire_nasturtium_plant", 1, 3, false);
	public static final Block clerodendrum_plant = new SweetCrops_STAGE3("clerodendrum_plant", 3, 3, false);
	public static final Block cotton_plant = new SweetCrops_STAGE3("cotton_plant", 4, 3, false);
	public static final Block glowflower_plant = new SweetCrops_STAGE3("glowflower_plant", 5, 3, false);
	public static final Block sweetpotato_plant = new SweetCrops_STAGE3("sweetpotato_plant", 6, 3, true);
	public static final Block strawberry_plant = new SweetCrops_STAGE3("strawberry_plant", 7, 3, true);
	public static final Block cabbage_plant = new SweetCrops_STAGE3("cabbage_plant", 8, 3, true);
	public static final Block lettuce_plant = new SweetCrops_STAGE3("lettuce_plant", 9, 3, true);
	public static final Block spinach_plant = new SweetCrops_STAGE3("spinach_plant", 10, 3, false);
	public static final Block mint_plant = new SweetCrops_STAGE3("mint_plant", 11, 3, false);
	public static final Block greenpepper_plant = new SweetCrops_STAGE3("greenpepper_plant", 12, 3, false);
	public static final Block j_radish_plant = new SweetCrops_STAGE3("j_radish_plant", 13, 3, true);

	public static final Block whitenet_plant = new Whitenet("whitenet_plant");

	public static final Block sticky_stuff_plant = new SweetCrops_STAGE4("sticky_stuff_plant", 0, 3, false);
	public static final Block onion_plant = new SweetCrops_STAGE4("onion_plant", 1, 3, true);
	public static final Block olive_plant = new SweetCrops_STAGE4("olive_plant", 2, 3, true);
	public static final Block coffee_plant = new SweetCrops_STAGE4("coffee_plant", 3, 3, true);
	public static final Block blueberry_plant = new SweetCrops_STAGE4("blueberry_plant", 4, 3, false);
	public static final Block vannila_plant = new SweetCrops_STAGE4("vannila_plant", 5, 3, true);
	public static final Block pineapple_plant = new SweetCrops_STAGE4("pineapple_plant", 6, 3, true);

	public static final Block corn_plant = new SweetCrops_DoublePlant("corn_plant", 0, 3);
	public static final Block eggplant_plant = new SweetCrops_DoublePlant("eggplant_plant", 1, 3);
	public static final Block tomato_plant = new SweetCrops_DoublePlant("tomato_plant", 2, 3);

	public static final Block quartz_plant = new SweetCrops_STAGE5("quartz_plant", 0, 4, true);
	public static final Block raspberry_plant = new SweetCrops_STAGE5("raspberry_plant", 1, 3, false);
	public static final Block soybean_plant = new SweetCrops_STAGE5("soybean_plant", 2, 3, true);
	public static final Block rice_plant = new SweetCrops_STAGE5("rice_plant", 3, 3, true);
	public static final Block azuki_plant = new SweetCrops_STAGE5("azuki_plant", 4, 3, true);
	public static final Block lapislazuli_plant = new SweetCrops_STAGE5("lapislazuli_plant", 5, 4, true);
	public static final Block redstone_plant = new SweetCrops_STAGE5("redstone_plant", 6, 4, true);

	public static final Block sannyflower_plant = new MagiaFlower("sannyflower_plant", 0, 2);
	public static final Block moonblossom_plant = new MagiaFlower("moonblossom_plant", 1, 2);
	public static final Block drizzly_mysotis_plant = new MagiaFlower("drizzly_mysotis_plant", 2, 2);

	public static final Block chestnut_plant = new SweetCrops_STAGE2("chestnut_plant", 0, 3);
	public static final Block coconut_plant = new SweetCrops_STAGE2("coconut_plant", 1, 3);
	public static final Block banana_plant = new SweetCrops_STAGE2("banana_plant", 2, 3);

	public static final Block magia_rewrite_bot = new BaseModelBlock("magia_rewrite_bot");
	public static final Block magia_rewrite_top = new BaseModelBlock("magia_rewrite_top");

	public static final Block magic_square = new BaseModelBlock("magic_square");
	public static final Block magic_square_s = new BaseModelBlock("magic_square_s");
	public static final Block magic_square_l = new BaseModelBlock("magic_square_l");
	public static final Block magic_square_h = new BaseModelBlock("magic_square_h");
	public static final Block magic_square_s_blank = new BaseModelBlock("magic_square_s_blank");
	public static final Block magic_square_l_blank = new BaseModelBlock("magic_square_l_blank");
	public static final Block kogen = new BaseModelBlock("kogen");
	public static final Block yellow_glass = new BaseModelBlock("yellow_glass");
	public static final Block calamity_bomb = new BaseModelBlock("calamity_bomb");
	public static final Block aether_lamplight_render = new BaseModelBlock("aether_lamplight_render");
	public static final Block tornado = new BaseModelBlock("tornado");
	public static final Block cherry_plant = new CherryPlantBlock("cherry_plant");
	public static final Block poison_block = new PoisonBlock("poison_block");
	public static final Block select_block = new BaseModelBlock("select_block");
	public static final Block ceiling_fan_blade = new BaseModelBlock("ceiling_fan_blade");
	public static final Block electricsphere = new BaseModelBlock("electricsphere");
	public static final Block belial_flame = new BelialFlame("belial_flame");

	public static final Block water_clear = new MFLiquidBlock("water_clear");
	public static final Block water = new MFLiquidBlock("water");

	public static final Block spawn_stone_a = new SpawnCrystal("spawn_stone_a", 0);
	public static final Block spawn_stone_d = new SpawnCrystal("spawn_stone_d", 1);
	public static final Block spawn_stone_p = new SpawnCrystal("spawn_stone_p", 2);
	public static final Block spawn_stone_f = new SpawnCrystal("spawn_stone_f", 3);
	public static final Block spawn_stone_r = new SpawnCrystal("spawn_stone_r", 4);
	public static final Block spawn_stone_m = new SpawnCrystal("spawn_stone_m", 5);
	public static final Block spawn_stone = new SMStone("spawn_stone");
	public static final Block smspawner = new SMSpawner("smspawner", 0);
	public static final Block smspawner_boss = new SMSpawner("smspawner_boss", 1);
	public static final Block magician_lectern = new MagicianLectern("magician_lectern", 0);
	public static final Block magician_lectern_light = new MagicianLectern("magician_lectern_light", 1);
	public static final Block magician_lectern_fire = new MagicianLectern("magician_lectern_fire", 2);
	public static final Block magician_lectern_wind = new MagicianLectern("magician_lectern_wind", 3);
	public static final Block transfer_gate = new TransferGate("transfer_gate", 0);
	public static final Block transfer_gate_top = new TransferGate("transfer_gate_top", 1);
	public static final Block transfer_gate_vertical = new TransferGateVertical("transfer_gate_vertical", 0);
	public static final Block transfer_gate_vertical_top = new TransferGateVertical("transfer_gate_vertical_top", 1);
	public static final Block build_glass = new BuildGlass("build_glass", 0);
	public static final Block build_water_glass = new BuildGlass("build_water_glass", 1);
	public static final Block crystal_pedal = new CrystalPedal("crystal_pedal");
	public static final Block magia_portal = new MagiaPortal("magia_portal", design_brick_w);

	@SubscribeEvent
	public static void registerBlock(RegisterEvent event) {

		// ブロックの登録
		event.register(ForgeRegistries.Keys.BLOCKS, h -> blockMap.forEach((key, val) -> h.register(SweetMagicCore.getSRC(val), key.getBlock())));

		// アイテムの登録
		event.register(ForgeRegistries.Keys.ITEMS, h -> {

			for (Entry<BlockInfo, String> map : blockMap.entrySet()) {

				BlockInfo info = map.getKey();
				Block block = info.getBlock();
				BlockItem bItem = null;
				Properties pro = info.getTab() != null ? new Item.Properties().tab(info.getTab()) : new Item.Properties();

				if (info.fireResist()) {
					pro.fireResistant();
				}

				if (block instanceof BaseMFBlock mfBlock) {

					bItem = new BlockItem(block, pro) {

						@Override
						public int getBarColor(@NotNull ItemStack stack) {
							return this.getMF(stack) >= mfBlock.getMaxMF() ? 0X30FF89 : 0X00C3FF;
						}

						@Override
						public boolean isBarVisible(@NotNull ItemStack stack) {
							return this.getMF(stack) != 0;
						}

						@Override
						public int getBarWidth(@NotNull ItemStack stack) {
							return Math.min(13, Math.round(13F * (float) this.getMF(stack) / (float) mfBlock.getMaxMF()));
						}

						public int getMF(ItemStack stack) {
							return stack.getOrCreateTag().getInt("mf");
						}
					};
				}

				else if (block instanceof AlternativeTank tank) {

					bItem = new BlockItem(block, pro.stacksTo(1)) {

						@Override
						public int getBarColor(@NotNull ItemStack stack) {
							return this.getFluidValue(stack) >= tank.getMaxFluidValue() ? 0X30FF89 : 0X00C3FF;
						}

						@Override
						public boolean isBarVisible(@NotNull ItemStack stack) {
							return this.getFluidValue(stack) != 0;
						}

						@Override
						public int getBarWidth(@NotNull ItemStack stack) {
							return Math.min(13, Math.round(13F * (float) this.getFluidValue(stack) / (float) tank.getMaxFluidValue()));
						}

						public int getFluidValue(ItemStack stack) {
							if (!stack.getOrCreateTag().contains("BlockEntityTag")) { return 0; }
							return FluidStack.loadFluidStackFromNBT(stack.getTagElement("BlockEntityTag").getCompound("fluid")).getAmount();
						}
					};
				}

				else if (block instanceof TrunkCase trunk) {

					bItem = new BlockItem(block, pro.stacksTo(1)) {
						public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
							ItemStack stack = player.getItemInHand(hand);
							trunk.openGui(world, player, stack);
							return InteractionResultHolder.consume(stack);
						}

						public InteractionResult place(BlockPlaceContext con) {

							InteractionResult result = super.place(con);
							Player player = con.getPlayer();

							if (result == InteractionResult.FAIL && !player.isShiftKeyDown()) {
								trunk.openGui(con.getLevel(), player, con.getItemInHand());
							}

							return result;
						}
					};
				}

				else {
					bItem = new BlockItem(block, pro);
				}

				h.register(SweetMagicCore.getSRC(map.getValue()), bItem);
			}
		});
	}

	public static class BlockInfo {

		private final Block block;
		private final CreativeModeTab tab;
		private final boolean fireResist;

		public BlockInfo(Block block, CreativeModeTab tab) {
			this.block = block;
			this.tab = tab;
			this.fireResist = false;
		}

		public BlockInfo(Block block, CreativeModeTab tab, boolean fireResist) {
			this.block = block;
			this.tab = tab;
			this.fireResist = fireResist;
		}

		public Block getBlock() {
			return this.block;
		}

		public CreativeModeTab getTab() {
			return this.tab;
		}

		public boolean fireResist() {
			return this.fireResist;
		}

		public static void create(Block block, CreativeModeTab tab, String name) {
			BlockInit.blockMap.put(new BlockInfo(block, tab), name);
		}
	}

	public static void registerPots() {
		FlowerPotBlock pot = (FlowerPotBlock) Blocks.FLOWER_POT;
		registerPot(pot, BlockInit.cornflower_flowerpot);
		registerPot(pot, BlockInit.lily_valley_flowerpot);
		registerPot(pot, BlockInit.cosmos_flowerpot);
		registerPot(pot, BlockInit.blackrose_flowerpot);
		registerPot(pot, BlockInit.white_clover_flowerpot);
		registerPot(pot, BlockInit.foxtail_grass_flowerpot);
		registerPot(pot, BlockInit.snowdrop_flowerpot);
		registerPot(pot, BlockInit.turkey_balloonflower_flowerpot);
		registerPot(pot, BlockInit.iberis_umbellata_flowerpot);
		registerPot(pot, BlockInit.ultramarine_rose_flowerpot);
		registerPot(pot, BlockInit.solid_star_flowerpot);
		registerPot(pot, BlockInit.zinnia_flowerpot);
		registerPot(pot, BlockInit.campanula_flowerpot);
		registerPot(pot, BlockInit.primula_polyansa_flowerpot);
		registerPot(pot, BlockInit.hydrangea_flowerpot);
		registerPot(pot, BlockInit.carnation_crayola_flowerpot);
		registerPot(pot, BlockInit.christmas_rose_flowerpot);
		registerPot(pot, BlockInit.portulaca_flowerpot);
		registerPot(pot, BlockInit.surfinia_flowerpot);
		registerPot(pot, BlockInit.pansy_blue_flowerpot);
		registerPot(pot, BlockInit.pansy_yellowmazenta_flowerpot);
		registerPot(pot, BlockInit.marigold_flowerpot);
		registerPot(pot, BlockInit.christmarose_ericsmithii_flowerpot);
	}

	public static void registerPot(FlowerPotBlock pot, Block block) {
		SMFlowerPot fp = (SMFlowerPot) block;
		pot.addPlant(SweetMagicCore.getSRC(fp.getFlowerName()), () -> fp);
	}
}
