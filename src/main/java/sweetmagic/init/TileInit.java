package sweetmagic.init;

import java.util.HashMap;
import java.util.Map;

import com.mojang.datafixers.types.Type;

import net.minecraft.Util;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.tile.sm.*;

public class TileInit {

	public static Map<BlockEntityType<?>, String> tileMap = new HashMap<>();
	public static final BlockEntityType<TileAlstroemeria> alst = getTile("alstroemeria", TileAlstroemeria::new, BlockInit.twilight_alstroemeria);
	public static final BlockEntityType<TileMFChanger> changer = getTile("changer", TileMFChanger::new, BlockInit.mfchanger);
	public static final BlockEntityType<TileMFChangerAdvanced> changerAdavance = getTile("changer_adavance", TileMFChangerAdvanced::new, BlockInit.mfchanger_adavance);
	public static final BlockEntityType<TileMFChangerMaster> changerMaster = getTile("changer_master", TileMFChangerMaster::new, BlockInit.mfchanger_master);
	public static final BlockEntityType<TileMFTable> table = getTile("table", TileMFTable::new, BlockInit.mftable);
	public static final BlockEntityType<TileMFTableAdvanced> tableAdavance = getTile("table_adavance", TileMFTableAdvanced::new, BlockInit.mftable_adavance);
	public static final BlockEntityType<TileMFTableMaster> tableMaster = getTile("table_master", TileMFTableMaster::new, BlockInit.mftable_master);
	public static final BlockEntityType<TileMFTank> tank = getTile("tank", TileMFTank::new, BlockInit.mftank);
	public static final BlockEntityType<TileMFTankAdavance> tankAdavance = getTile("tank_adavance", TileMFTankAdavance::new, BlockInit.mftank_adavance);
	public static final BlockEntityType<TileMFTankMaster> tankMaster = getTile("tank_master", TileMFTankMaster::new, BlockInit.mftank_master);
	public static final BlockEntityType<TileMFTankCreative> tankCreative = getTile("tank_creative", TileMFTankCreative::new, BlockInit.mftank_creative);
	public static final BlockEntityType<TileWoodChest> woodChest = getTile("wood_chest", TileWoodChest::new, BlockInit.moden_shelf_y, BlockInit.moden_shelf_i, BlockInit.moden_shelf_l, BlockInit.moden_shelf_o, BlockInit.moden_shelf_p, BlockInit.moden_shelf_r, BlockInit.moden_shelf_s, BlockInit.woodbox, BlockInit.trunk_case_s, BlockInit.trunk_case_pi, BlockInit.trunk_case_pu, BlockInit.trunk_case_o, BlockInit.trunk_case_l, BlockInit.trunk_case_bu, BlockInit.trunk_case_bl, BlockInit.trunk_case_w, BlockInit.couter_table_chest_white_brick_r, BlockInit.couter_table_chest_white_brick_y, BlockInit.couter_table_chest_white_brick_b, BlockInit.couter_table_chest_white_brick_m, BlockInit.couter_table_chest_white_brick_w, BlockInit.japanese_dance, BlockInit.western_style_dance);
	public static final BlockEntityType<TileParallelInterfere> parallelInterfere = getTile("parallel_interfere", TileParallelInterfere::new, BlockInit.parallel_interfere);
	public static final BlockEntityType<TileStardustWish> stardustWish = getTile("stardust_wish", TileStardustWish::new, BlockInit.stardust_wish);
	public static final BlockEntityType<TileFreezerChest> freezerChest = getTile("freezer_chest", TileFreezerChest::new, BlockInit.freezer, BlockInit.freezer_r, BlockInit.freezer_l, BlockInit.freezer_o, BlockInit.freezer_pi, BlockInit.freezer_pu, BlockInit.freezer_s);
	public static final BlockEntityType<TileModenRack> modenRack = getTile("moden_rack", TileModenRack::new, BlockInit.moden_rack, BlockInit.moden_rack_b, BlockInit.moden_rack_m, BlockInit.moden_rack_t);
	public static final BlockEntityType<TileWallRack> wallRack = getTile("wall_rack", TileWallRack::new, BlockInit.wall_rack, BlockInit.wall_rack_b, BlockInit.wall_rack_m, BlockInit.wall_rack_t, BlockInit.wall_rack_i);
	public static final BlockEntityType<TileWallShelf> wallShelf = getTile("wall_shelf", TileWallShelf::new, BlockInit.wall_shelf, BlockInit.wall_shelf_b, BlockInit.wall_shelf_m, BlockInit.wall_shelf_t, BlockInit.wall_shelf_i);
	public static final BlockEntityType<TileWallPartition> wallPartition = getTile("wall_partition", TileWallPartition::new, BlockInit.wall_partition, BlockInit.wall_partition_b, BlockInit.wall_partition_m, BlockInit.wall_partition_t);
	public static final BlockEntityType<TileCeilingShelf> ceilingShelf = getTile("ceiling_shelf", TileCeilingShelf::new, BlockInit.ceiling_shelf);
	public static final BlockEntityType<TileWandPedastal> wandPedastal = getTile("wand_pedastal", TileWandPedastal::new, BlockInit.wand_pedastal, BlockInit.corkboard, BlockInit.wallboard, BlockInit.wallboard_b, BlockInit.hanging_sign, BlockInit.item_menu_b, BlockInit.item_menu_w, BlockInit.decorative_stand, BlockInit.hanging_sign_wood);
	public static final BlockEntityType<TileBottleRack> bottleRack = getTile("bottle_rack", TileBottleRack::new, BlockInit.bottle_rack_t, BlockInit.bottle_rack_b, BlockInit.bottle_rack_d);
	public static final BlockEntityType<TilePlate> plate = getTile("plate", TilePlate::new, BlockInit.plate, BlockInit.wood_plate, BlockInit.plate_o, BlockInit.plate_p, BlockInit.plate_i, BlockInit.plate_s, BlockInit.wood_tray, BlockInit.basket);
	public static final BlockEntityType<TileShowCase> showcase = getTile("showcase", TileShowCase::new, BlockInit.showcase);
	public static final BlockEntityType<TileIrisCreation> iris = getTile("iris", TileIrisCreation::new, BlockInit.iris_creation);
	public static final BlockEntityType<TileMill> mill = getTile("mill", TileMill::new, BlockInit.mill, BlockInit.mixer_r, BlockInit.mixer_l, BlockInit.mixer_o, BlockInit.mixer_pi, BlockInit.mixer_pu, BlockInit.mixer_s);
	public static final BlockEntityType<TileOven> oven = getTile("oven", TileOven::new, BlockInit.oven, BlockInit.couter_table_oven_white_brick_r, BlockInit.couter_table_oven_white_brick_y, BlockInit.couter_table_oven_white_brick_b, BlockInit.couter_table_oven_white_brick_m, BlockInit.couter_table_oven_white_brick_w, BlockInit.toaster_r, BlockInit.toaster_l, BlockInit.toaster_o, BlockInit.toaster_pi, BlockInit.toaster_pu, BlockInit.toaster_s);
	public static final BlockEntityType<TileFrypan> frypan = getTile("frypan", TileFrypan::new, BlockInit.frypan_r, BlockInit.frypan_l, BlockInit.frypan_o, BlockInit.frypan_pi, BlockInit.frypan_pu, BlockInit.frypan_s);
	public static final BlockEntityType<TilePot> pot = getTile("pot", TilePot::new, BlockInit.pot_w, BlockInit.pot_r, BlockInit.pot_l, BlockInit.pot_o, BlockInit.pot_pi, BlockInit.pot_pu, BlockInit.pot_s, BlockInit.single_pot_r, BlockInit.single_pot_l, BlockInit.single_pot_o, BlockInit.single_pot_pi, BlockInit.single_pot_pu, BlockInit.single_pot_s);
	public static final BlockEntityType<TileFreezer> freezer = getTile("freezer", TileFreezer::new, BlockInit.freezer_top, BlockInit.freezer_top_r, BlockInit.freezer_top_l, BlockInit.freezer_top_o, BlockInit.freezer_top_pi, BlockInit.freezer_top_pu, BlockInit.freezer_top_s);
	public static final BlockEntityType<TileJuiceMaker> juicemaker = getTile("juicemaker", TileJuiceMaker::new, BlockInit.juice_maker, BlockInit.coffee_maker_r, BlockInit.coffee_maker_l, BlockInit.coffee_maker_o, BlockInit.coffee_maker_pi, BlockInit.coffee_maker_pu, BlockInit.coffee_maker_s);
	public static final BlockEntityType<TileBottle> bottle = getTile("bottle", TileBottle::new, BlockInit.bottle);
	public static final BlockEntityType<TilePedalCreate> pedal = getTile("pedal", TilePedalCreate::new, BlockInit.pedestal_creat);
	public static final BlockEntityType<TileAltarCreat> altarCreat = getTile("altar_creat", TileAltarCreat::new, BlockInit.altar_creat);
	public static final BlockEntityType<TileAltarCreatStar> altarCreatStar = getTile("altar_creat_star", TileAltarCreatStar::new, BlockInit.altar_creation_star);
	public static final BlockEntityType<TileMFPot> mfpot = getTile("mfpot", TileMFPot::new, BlockInit.dm_pot, BlockInit.alstroemeria_pot, BlockInit.snowdrop_pot, BlockInit.ultramarine_rose_pot, BlockInit.solid_star_pot, BlockInit.zinnia_pot, BlockInit.hydrangea_pot, BlockInit.christmarose_pot, BlockInit.cosmos_pot, BlockInit.turkey_balloonflower_pot, BlockInit.carnation_crayola_pot);
	public static final BlockEntityType<TileAquariumPot> aquariumpot = getTile("aquarium_pot", TileAquariumPot::new, BlockInit.dm_aquarium, BlockInit.carnation_crayola_aquarium, BlockInit.hydrangea_aquarium, BlockInit.christmarose_aquarium, BlockInit.snowdrop_aquarium, BlockInit.turkey_balloonflower_aquarium, BlockInit.ultramarine_rose_aquarium, BlockInit.cosmos_aquarium, BlockInit.solid_star_aquarium, BlockInit.zinnia_aquarium);
	public static final BlockEntityType<TileAetherLanp> aetherLanp = getTile("aether_lanp", TileAetherLanp::new, BlockInit.aether_lanp);
	public static final BlockEntityType<TileHightAetherLamplight> hightAetheLamplight = getTile("hi_aether_lamplight", TileHightAetherLamplight::new, BlockInit.high_aether_lantern);
	public static final BlockEntityType<TileAetherLamplight> aetheLamplight = getTile("aether_lamplight", TileAetherLamplight::new, BlockInit.aether_lamplight);
	public static final BlockEntityType<TileEnchantEduce> enchantEduce = getTile("enchant_educe", TileEnchantEduce::new, BlockInit.enchant_educe);
	public static final BlockEntityType<TileMagiaRewrite> magiaWrite = getTile("magia_write", TileMagiaRewrite::new, BlockInit.magia_rewrite);
	public static final BlockEntityType<TileObMagia> obmagia = getTile("ob_magia", TileObMagia::new, BlockInit.obmagia, BlockInit.obmagia_top);
	public static final BlockEntityType<TileAetherRepair> aetherRepair = getTile("aether_repair", TileAetherRepair::new, BlockInit.aether_repair);
	public static final BlockEntityType<TileAetherReverse> aetherReverse = getTile("aether_reverse", TileAetherReverse::new, BlockInit.aether_reverse);
	public static final BlockEntityType<TileAetherHopper> aetherHopper = getTile("aether_hopper", TileAetherHopper::new, BlockInit.aether_hopper);
	public static final BlockEntityType<TileMagiaDrawer> magiarDrawer = getTile("magiar_drawer", TileMagiaDrawer::new, BlockInit.magia_drawer);
	public static final BlockEntityType<TileWarp> warpBlock = getTile("warp_block", TileWarp::new, BlockInit.warp_block);
	public static final BlockEntityType<TileMFFisher> mfFisher = getTile("mffisher", TileMFFisher::new, BlockInit.mf_fisher, BlockInit.mf_forer, BlockInit.mf_squeezer, BlockInit.aehter_furnace, BlockInit.aehter_furnace_adavance, BlockInit.mf_miner, BlockInit.mf_egg_launcher);
	public static final BlockEntityType<TileTrashCan> trashCan = getTile("trash_can", TileTrashCan::new, BlockInit.trash_can_s, BlockInit.trash_can_s, BlockInit.trash_can_y, BlockInit.trash_can_r, BlockInit.trash_can_o, BlockInit.trash_can_pu, BlockInit.trash_can_pi);
	public static final BlockEntityType<TileFurnitureTable> furnitureTable = getTile("furniture_processing_table", TileFurnitureTable::new, BlockInit.furniture_processing_table);
	public static final BlockEntityType<TileMFFurnace> mffurnace = getTile("mffurnace", TileMFFurnace::new, BlockInit.mffurnace);
	public static final BlockEntityType<TileMFFurnaceAdvanced> mffurnaceAdavance = getTile("mffurnace_adavance", TileMFFurnaceAdvanced::new, BlockInit.mffurnace_advance);
	public static final BlockEntityType<TileMagiaAccelerator> magiaAccelerator = getTile("magia_accelerator", TileMagiaAccelerator::new, BlockInit.magia_accelerator);
	public static final BlockEntityType<TileMagicianLecternFrost> magicianLecternFrost = getTile("magician_lectern", TileMagicianLecternFrost::new, BlockInit.magician_lectern);
	public static final BlockEntityType<TileMagicianLecternLight> magicianLecternLight = getTile("magician_lectern_light", TileMagicianLecternLight::new, BlockInit.magician_lectern_light);
	public static final BlockEntityType<TileMagicianLecternFire> magicianLecternFire = getTile("magician_lectern_fire", TileMagicianLecternFire::new, BlockInit.magician_lectern_fire);
	public static final BlockEntityType<TileMagicianLecternWind> magicianLecternWind= getTile("magician_lectern_wind", TileMagicianLecternWind::new, BlockInit.magician_lectern_wind);
	public static final BlockEntityType<TileSturdustCrystal> sturdust_crystal = getTile("sturdust_crystal", TileSturdustCrystal::new, BlockInit.sturdust_crystal);
	public static final BlockEntityType<TileSpawnCrystal> spawn_crystal = getTile("spawn_crystal", TileSpawnCrystal::new, BlockInit.spawn_stone_a, BlockInit.spawn_stone_d, BlockInit.spawn_stone_p, BlockInit.spawn_stone_f, BlockInit.spawn_stone_r, BlockInit.spawn_stone_m);
	public static final BlockEntityType<TileStove> stove = getTile("stove", TileStove::new, BlockInit.stove, BlockInit.couter_table_stove_white_brick_r, BlockInit.couter_table_stove_white_brick_y, BlockInit.couter_table_stove_white_brick_b, BlockInit.couter_table_stove_white_brick_m, BlockInit.couter_table_stove_white_brick_w, BlockInit.transfer_gate, BlockInit.sturdust_crystal);
	public static final BlockEntityType<TileAccessoryTable> accessoryProcessing = getTile("accessory_processing", TileAccessoryTable::new, BlockInit.accessory_processing_table);
	public static final BlockEntityType<TileAlstroemeriaAquarium> alstroemeriaAquarium = getTile("alstroemeria_aquarium", TileAlstroemeriaAquarium::new, BlockInit.alstroemeria_aquarium);
	public static final BlockEntityType<TileSMSpawner> smSpawner = getTile("smspawner", TileSMSpawner::new, BlockInit.smspawner);
	public static final BlockEntityType<TileSMSpawnerBoss> smSpawnerBoss = getTile("smspawner_boss", TileSMSpawnerBoss::new, BlockInit.smspawner_boss);
	public static final BlockEntityType<TileSpawnStone> spawnStone = getTile("spawnstone", TileSpawnStone::new, BlockInit.spawn_stone);
	public static final BlockEntityType<TileTransferGate> transferGate = getTile("transfer_gate", TileTransferGate::new, BlockInit.transfer_gate_top);
	public static final BlockEntityType<TileTransferGateVertical> transferGateVertical = getTile("transfer_gate_vertical", TileTransferGateVertical::new, BlockInit.transfer_gate_vertical_top);
	public static final BlockEntityType<TileMirageGlass> mirageGlass = getTile("mirage_glass", TileMirageGlass::new, BlockInit.mirage_glass, BlockInit.mirage_gate_glass, BlockInit.mirage_wall_glass);
	public static final BlockEntityType<TileMagicBarrier> barrierGlass = getTile("barrier_glass", TileMagicBarrier::new, BlockInit.magicbarrier_lock);
	public static final BlockEntityType<TileBossFigurine> bossFigurine = getTile("boss_figurine", TileBossFigurine::new, BlockInit.figurine_queenfrost, BlockInit.figurine_holyangel, BlockInit.figurine_ignisknight, BlockInit.figurine_windwitch_master, BlockInit.figurine_bullfight, BlockInit.figurine_ancientfairy, BlockInit.figurine_arlaune, BlockInit.figurine_landroad, BlockInit.figurine_butler, BlockInit.figurine_hora, BlockInit.figurine_brave, BlockInit.figurine_curious, BlockInit.figurine_sandryon, BlockInit.figurine_blitz);
	public static final BlockEntityType<TileAetherRecycler> aetherRecycler = getTile("aether_recycler", TileAetherRecycler::new, BlockInit.aether_recycler);
	public static final BlockEntityType<TileAetherCraftTable> aetherCraftTable = getTile("aether_crafttable", TileAetherCraftTable::new, BlockInit.aether_crafttable);
	public static final BlockEntityType<TileAetherPlanter> aetherPlanter = getTile("aether_planter", TileAetherPlanter::new, BlockInit.aether_planter);
	public static final BlockEntityType<TileMFMinerAdvanced> mfMinerAdvanced = getTile("mf_miner_advanced", TileMFMinerAdvanced::new, BlockInit.mf_miner_advanced);
	public static final BlockEntityType<TileMFBottler> mfBottler = getTile("mf_bottle", TileMFBottler::new, BlockInit.mf_bottler);
	public static final BlockEntityType<TileMFBottlerAdcanced> mfBottlerAdvance = getTile("mf_bottle_advance", TileMFBottlerAdcanced::new, BlockInit.mf_bottler_advance);
	public static final BlockEntityType<TileAlternativeTank> alternativeTank = getTile("alternative_tank", TileAlternativeTank::new, BlockInit.alternative_tank);
	public static final BlockEntityType<TileCosmosLightTank> cosmosLightTank = getTile("cosmos_light_tank", TileCosmosLightTank::new, BlockInit.cosmos_light_tank);
	public static final BlockEntityType<TileMFGenerater> mfGenerater = getTile("mf_generater", TileMFGenerater::new, BlockInit.mf_generater);
	public static final BlockEntityType<TileMagiaStorage> magiaStorage = getTile("magia_storage", TileMagiaStorage::new, BlockInit.magia_storage_1, BlockInit.magia_storage_2, BlockInit.magia_storage_3, BlockInit.magia_storage_4, BlockInit.magia_storage_5);
	public static final BlockEntityType<TileCardboardStorage> cardboardStorage = getTile("cardboard_storage", TileCardboardStorage::new, BlockInit.cardboard_storage);
	public static final BlockEntityType<TileDresser> dresser = getTile("dresser", TileDresser::new, BlockInit.dresser);
	public static final BlockEntityType<TileSummonPedal> summonPedal = getTile("summon_pedal", TileSummonPedal::new, BlockInit.summon_pedal);
	public static final BlockEntityType<TileCeilingFan> ceilingFan = getTile("ceiling_fan", TileCeilingFan::new, BlockInit.ceiling_fan);
	public static final BlockEntityType<TileMagiaCrystalLight> magiaCrystalLight = getTile("magia_crystal_light", TileMagiaCrystalLight::new, BlockInit.magia_crystal_light);
	public static final BlockEntityType<TileMagiaTable> magiaTable = getTile("magia_table", TileMagiaTable::new, BlockInit.magia_table);

	public static <T extends BlockEntity> BlockEntityType<T> getTile(String name, BlockEntityType.BlockEntitySupplier<T> tile, Block... blocks) {
		Type<?> type = Util.fetchChoiceType(References.BLOCK_ENTITY, name);
		BlockEntityType<T> tileType = BlockEntityType.Builder.of(tile, blocks).build(type);
		tileMap.put(tileType, name);
		return tileType;
	}

	@SubscribeEvent
	public static void registerTile(RegisterEvent event) {
		event.register(ForgeRegistries.Keys.BLOCK_ENTITY_TYPES, h -> tileMap.forEach((key, val) -> h.register(SweetMagicCore.getSRC(val), key)));
	}
}
