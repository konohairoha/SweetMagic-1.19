package sweetmagic.init;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.tile.gui.GuiAccessoryTable;
import sweetmagic.init.tile.gui.GuiAetherCraftTable;
import sweetmagic.init.tile.gui.GuiAetherHopper;
import sweetmagic.init.tile.gui.GuiAetherLamplight;
import sweetmagic.init.tile.gui.GuiAetherLantern;
import sweetmagic.init.tile.gui.GuiAetherPlanter;
import sweetmagic.init.tile.gui.GuiAetherRecycler;
import sweetmagic.init.tile.gui.GuiAetherRepair;
import sweetmagic.init.tile.gui.GuiAetherReverse;
import sweetmagic.init.tile.gui.GuiAlstroemeriaAquarium;
import sweetmagic.init.tile.gui.GuiAquariumPot;
import sweetmagic.init.tile.gui.GuiClero;
import sweetmagic.init.tile.gui.GuiCompas;
import sweetmagic.init.tile.gui.GuiEnchantEduce;
import sweetmagic.init.tile.gui.GuiFreezer;
import sweetmagic.init.tile.gui.GuiFurnitureCraft;
import sweetmagic.init.tile.gui.GuiFurnitureTable;
import sweetmagic.init.tile.gui.GuiIrisCreation;
import sweetmagic.init.tile.gui.GuiJuiceMaker;
import sweetmagic.init.tile.gui.GuiMFBottler;
import sweetmagic.init.tile.gui.GuiMFChanger;
import sweetmagic.init.tile.gui.GuiMFFisher;
import sweetmagic.init.tile.gui.GuiMFFurnace;
import sweetmagic.init.tile.gui.GuiMFMinerAdvanced;
import sweetmagic.init.tile.gui.GuiMFTable;
import sweetmagic.init.tile.gui.GuiMFTank;
import sweetmagic.init.tile.gui.GuiMagiaAccelerator;
import sweetmagic.init.tile.gui.GuiMagiaDrawer;
import sweetmagic.init.tile.gui.GuiMagiaRewrite;
import sweetmagic.init.tile.gui.GuiMagicBarrier;
import sweetmagic.init.tile.gui.GuiMagicBook;
import sweetmagic.init.tile.gui.GuiModenRack;
import sweetmagic.init.tile.gui.GuiObMagia;
import sweetmagic.init.tile.gui.GuiParallelInterfere;
import sweetmagic.init.tile.gui.GuiPlate;
import sweetmagic.init.tile.gui.GuiPorch;
import sweetmagic.init.tile.gui.GuiRobe;
import sweetmagic.init.tile.gui.GuiSMBook;
import sweetmagic.init.tile.gui.GuiSMSpawner;
import sweetmagic.init.tile.gui.GuiSMWand;
import sweetmagic.init.tile.gui.GuiSpawnCrystal;
import sweetmagic.init.tile.gui.GuiSpawnStone;
import sweetmagic.init.tile.gui.GuiTrunkCase;
import sweetmagic.init.tile.gui.GuiWarp;
import sweetmagic.init.tile.gui.GuiWoodChest;
import sweetmagic.init.tile.gui.GuiWoodChestLoot;
import sweetmagic.init.tile.menu.AccessoryTableMenu;
import sweetmagic.init.tile.menu.AetherCraftTableMenu;
import sweetmagic.init.tile.menu.AetherHopperMenu;
import sweetmagic.init.tile.menu.AetherLamplightMenu;
import sweetmagic.init.tile.menu.AetherLanternMenu;
import sweetmagic.init.tile.menu.AetherPlanterMenu;
import sweetmagic.init.tile.menu.AetherRecyclerMenu;
import sweetmagic.init.tile.menu.AetherRepairMenu;
import sweetmagic.init.tile.menu.AetherReverseMenu;
import sweetmagic.init.tile.menu.AlstroemeriaAquariumMenu;
import sweetmagic.init.tile.menu.AquariumPotMenu;
import sweetmagic.init.tile.menu.CleroMenu;
import sweetmagic.init.tile.menu.CompasMenu;
import sweetmagic.init.tile.menu.EnchantEduceMenu;
import sweetmagic.init.tile.menu.FreezerMenu;
import sweetmagic.init.tile.menu.FurnitureCraftMenu;
import sweetmagic.init.tile.menu.FurnitureTableMenu;
import sweetmagic.init.tile.menu.IrisCreationMenu;
import sweetmagic.init.tile.menu.JuiceMakerMenu;
import sweetmagic.init.tile.menu.MFBottlerMenu;
import sweetmagic.init.tile.menu.MFChangerMenu;
import sweetmagic.init.tile.menu.MFFisherMenu;
import sweetmagic.init.tile.menu.MFFurnaceMenu;
import sweetmagic.init.tile.menu.MFMinerAdvancedMenu;
import sweetmagic.init.tile.menu.MFTableMenu;
import sweetmagic.init.tile.menu.MFTankMenu;
import sweetmagic.init.tile.menu.MagiaAcceleratorMenu;
import sweetmagic.init.tile.menu.MagiaDrawerMenu;
import sweetmagic.init.tile.menu.MagiaRewriteMenu;
import sweetmagic.init.tile.menu.MagicBarrierMenu;
import sweetmagic.init.tile.menu.MagicBookMenu;
import sweetmagic.init.tile.menu.ModenRackMenu;
import sweetmagic.init.tile.menu.ObMagiaMenu;
import sweetmagic.init.tile.menu.ParallelInterfereMenu;
import sweetmagic.init.tile.menu.PlateMenu;
import sweetmagic.init.tile.menu.SMBookMenu;
import sweetmagic.init.tile.menu.SMPorchMenu;
import sweetmagic.init.tile.menu.SMRoveMenu;
import sweetmagic.init.tile.menu.SMSpawnerMenu;
import sweetmagic.init.tile.menu.SMWandMenu;
import sweetmagic.init.tile.menu.SpawnCrystalMenu;
import sweetmagic.init.tile.menu.SpawnStoneMenu;
import sweetmagic.init.tile.menu.TrunkCaseMenu;
import sweetmagic.init.tile.menu.WarpMenu;
import sweetmagic.init.tile.menu.WoodChestLootMenu;
import sweetmagic.init.tile.menu.WoodChestMenu;
import sweetmagic.init.tile.sm.TileAbstractSM;

public class MenuInit {

	private static final Map<MenuType<?>, String> menuMap = new HashMap<>();

	public static final MenuType<SMWandMenu> wandMenu = register("wand_menu", SMWandMenu::new);
	public static final MenuType<SMRoveMenu> robeMenu = register("robe_menu", SMRoveMenu::new);
	public static final MenuType<SMPorchMenu> porchMenu = register("porch_menu", SMPorchMenu::new);
	public static final MenuType<MagicBookMenu> magicBookMenu = register("magicbook_menu", MagicBookMenu::new);
	public static final MenuType<SMBookMenu> bookMenu = register("book_menu", SMBookMenu::new);
	public static final MenuType<MFChangerMenu> changerMenu = register("changer_menu", MFChangerMenu::new);
	public static final MenuType<MFTableMenu> tableMenu = register("table_menu", MFTableMenu::new);
	public static final MenuType<MFTankMenu> tankMenu = register("tank_menu", MFTankMenu::new);
	public static final MenuType<WoodChestMenu> woodChestMenu = register("wood_chest_menu", WoodChestMenu::new);
	public static final MenuType<WoodChestLootMenu> woodChestLootMenu = register("wood_chest_loot_menu", WoodChestLootMenu::new);
	public static final MenuType<ParallelInterfereMenu> parallelInterfereMenu = register("parallel_interfere_menu", ParallelInterfereMenu::new);
	public static final MenuType<ModenRackMenu> modenRackMenu = register("moden_rack_menu", ModenRackMenu::new);
	public static final MenuType<PlateMenu> plateMenu = register("plate_menu", PlateMenu::new);
	public static final MenuType<IrisCreationMenu> irisMenu = register("iris_menu", IrisCreationMenu::new);
	public static final MenuType<FreezerMenu> freezerMenu = register("freezer_menu", FreezerMenu::new);
	public static final MenuType<JuiceMakerMenu> juiceMakerMenu = register("juice_maker_menu", JuiceMakerMenu::new);
	public static final MenuType<EnchantEduceMenu> enchantEduceMenu = register("enchant_educe_menu", EnchantEduceMenu::new);
	public static final MenuType<MagiaRewriteMenu> magiaRewriteMenu = register("magia_rewrite_menu", MagiaRewriteMenu::new);
	public static final MenuType<ObMagiaMenu> obMagiaMenu = register("ob_magia_menu", ObMagiaMenu::new);
	public static final MenuType<AetherRepairMenu> aetherRepairMenu = register("aether_repair_menu", AetherRepairMenu::new);
	public static final MenuType<AetherReverseMenu> aetherReverseMenu = register("aether_reverse_menu", AetherReverseMenu::new);
	public static final MenuType<AetherHopperMenu> aetherHopperMenu = register("aether_hopper_menu", AetherHopperMenu::new);
	public static final MenuType<MagiaDrawerMenu> magiaDrawerMenu = register("magia_drawer_menu", MagiaDrawerMenu::new);
	public static final MenuType<WarpMenu> warpMenu = register("warp_menu", WarpMenu::new);
	public static final MenuType<AetherLanternMenu> aetherLanternMenu = register("aetherlantern_menu", AetherLanternMenu::new);
	public static final MenuType<AetherLamplightMenu> aetherLamplightMenu = register("aether_lamplight_menu", AetherLamplightMenu::new);
	public static final MenuType<CleroMenu> cleroMenu = register("clero_menu", CleroMenu::new);
	public static final MenuType<CompasMenu> compasMenu = register("compas_menu", CompasMenu::new);
	public static final MenuType<MFFisherMenu> mfFisherMenu = register("mffisher_menu", MFFisherMenu::new);
	public static final MenuType<FurnitureTableMenu> furnitureTableMenu = register("furnitureprocessingtable_menu", FurnitureTableMenu::new);
	public static final MenuType<FurnitureCraftMenu> furnitureCraftMenu = register("furniturecraft_menu", FurnitureCraftMenu::new);
	public static final MenuType<MFFurnaceMenu> mfFurnace_menu = register("mffurnace_menu", MFFurnaceMenu::new);
	public static final MenuType<MagiaAcceleratorMenu> magiaaccelerator_menu = register("magiaaccelerator_menu", MagiaAcceleratorMenu::new);
	public static final MenuType<AccessoryTableMenu> accessoryProcessingMenu = register("accessory_processing_menu", AccessoryTableMenu::new);
	public static final MenuType<AlstroemeriaAquariumMenu> alstroemeriaAquariumMenu = register("alstroemeria_aquarium_menu", AlstroemeriaAquariumMenu::new);
	public static final MenuType<SMSpawnerMenu> smSpawmerMenu = register("smspawner_menu", SMSpawnerMenu::new);
	public static final MenuType<SpawnStoneMenu> spawnStoneMenu = register("spawnstone_menu", SpawnStoneMenu::new);
	public static final MenuType<SpawnCrystalMenu> spawnCrystalMenu = register("spawncrystal_menu", SpawnCrystalMenu::new);
	public static final MenuType<TrunkCaseMenu> trankCaseMenu = register("trankcase_menu", TrunkCaseMenu::new);
	public static final MenuType<MagicBarrierMenu> magicBarrierMenu = register("magicbarrier_menu", MagicBarrierMenu::new);
	public static final MenuType<AquariumPotMenu> aquariumPotMenu = register("aquariumpot_menu", AquariumPotMenu::new);
	public static final MenuType<AetherRecyclerMenu> aetherRecyclerMenu = register("aether_recycler_menu", AetherRecyclerMenu::new);
	public static final MenuType<AetherCraftTableMenu> aetherCraftTableMenu = register("aether_crafttable_menu", AetherCraftTableMenu::new);
	public static final MenuType<AetherPlanterMenu> aetherPlanterMenu = register("aether_planter_menu", AetherPlanterMenu::new);
	public static final MenuType<MFMinerAdvancedMenu> mfMinerMenu = register("mfminer_menu", MFMinerAdvancedMenu::new);
	public static final MenuType<MFBottlerMenu> mfBottlerMenu = register("mfbottler_menu", MFBottlerMenu::new);

	public static void registerGUI () {
        MenuScreens.register(MenuInit.wandMenu, GuiSMWand::new);
        MenuScreens.register(MenuInit.robeMenu, GuiRobe::new);
        MenuScreens.register(MenuInit.porchMenu, GuiPorch::new);
        MenuScreens.register(MenuInit.bookMenu, GuiSMBook::new);
        MenuScreens.register(MenuInit.magicBookMenu, GuiMagicBook::new);
        MenuScreens.register(MenuInit.changerMenu, GuiMFChanger::new);
        MenuScreens.register(MenuInit.tableMenu, GuiMFTable::new);
        MenuScreens.register(MenuInit.tankMenu, GuiMFTank::new);
        MenuScreens.register(MenuInit.woodChestMenu, GuiWoodChest::new);
        MenuScreens.register(MenuInit.parallelInterfereMenu, GuiParallelInterfere::new);
        MenuScreens.register(MenuInit.modenRackMenu, GuiModenRack::new);
        MenuScreens.register(MenuInit.plateMenu, GuiPlate::new);
        MenuScreens.register(MenuInit.irisMenu, GuiIrisCreation::new);
        MenuScreens.register(MenuInit.freezerMenu, GuiFreezer::new);
        MenuScreens.register(MenuInit.juiceMakerMenu, GuiJuiceMaker::new);
        MenuScreens.register(MenuInit.enchantEduceMenu, GuiEnchantEduce::new);
        MenuScreens.register(MenuInit.magiaRewriteMenu, GuiMagiaRewrite::new);
        MenuScreens.register(MenuInit.obMagiaMenu, GuiObMagia::new);
        MenuScreens.register(MenuInit.aetherRepairMenu, GuiAetherRepair::new);
        MenuScreens.register(MenuInit.aetherReverseMenu, GuiAetherReverse::new);
        MenuScreens.register(MenuInit.aetherHopperMenu, GuiAetherHopper::new);
        MenuScreens.register(MenuInit.magiaDrawerMenu, GuiMagiaDrawer::new);
        MenuScreens.register(MenuInit.warpMenu, GuiWarp::new);
        MenuScreens.register(MenuInit.aetherLanternMenu, GuiAetherLantern::new);
        MenuScreens.register(MenuInit.aetherLamplightMenu, GuiAetherLamplight::new);
        MenuScreens.register(MenuInit.cleroMenu, GuiClero::new);
        MenuScreens.register(MenuInit.compasMenu, GuiCompas::new);
        MenuScreens.register(MenuInit.mfFisherMenu, GuiMFFisher::new);
        MenuScreens.register(MenuInit.furnitureTableMenu, GuiFurnitureTable::new);
        MenuScreens.register(MenuInit.furnitureCraftMenu, GuiFurnitureCraft::new);
        MenuScreens.register(MenuInit.mfFurnace_menu, GuiMFFurnace::new);
        MenuScreens.register(MenuInit.magiaaccelerator_menu, GuiMagiaAccelerator::new);
        MenuScreens.register(MenuInit.woodChestLootMenu, GuiWoodChestLoot::new);
        MenuScreens.register(MenuInit.accessoryProcessingMenu, GuiAccessoryTable::new);
        MenuScreens.register(MenuInit.alstroemeriaAquariumMenu, GuiAlstroemeriaAquarium::new);
        MenuScreens.register(MenuInit.smSpawmerMenu, GuiSMSpawner::new);
        MenuScreens.register(MenuInit.spawnStoneMenu, GuiSpawnStone::new);
        MenuScreens.register(MenuInit.spawnCrystalMenu, GuiSpawnCrystal::new);
        MenuScreens.register(MenuInit.trankCaseMenu, GuiTrunkCase::new);
        MenuScreens.register(MenuInit.magicBarrierMenu, GuiMagicBarrier::new);
        MenuScreens.register(MenuInit.aquariumPotMenu, GuiAquariumPot::new);
        MenuScreens.register(MenuInit.aetherRecyclerMenu, GuiAetherRecycler::new);
        MenuScreens.register(MenuInit.aetherCraftTableMenu, GuiAetherCraftTable::new);
        MenuScreens.register(MenuInit.aetherPlanterMenu, GuiAetherPlanter::new);
        MenuScreens.register(MenuInit.mfMinerMenu, GuiMFMinerAdvanced::new);
        MenuScreens.register(MenuInit.mfBottlerMenu, GuiMFBottler::new);
	}

	public static TileAbstractSM getTile (Inventory inv, FriendlyByteBuf buf) {
		return (TileAbstractSM) inv.player.level.getBlockEntity(buf.readBlockPos());
	}

	private static <T extends AbstractContainerMenu> MenuType<T> register(String name, IContainerFactory<T> fact) {
        MenuType<T> menu = IForgeMenuType.create(fact);
        menuMap.put(menu, name);
		return menu;
	}

	@SubscribeEvent
	public static void registerMenu(RegisterEvent event) {
		event.register(ForgeRegistries.Keys.MENU_TYPES, h -> menuMap.forEach((key, val) -> h.register(SweetMagicCore.getSRC(val), key)));
	}
}