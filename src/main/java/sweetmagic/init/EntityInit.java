package sweetmagic.init;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.init.entity.animal.WitchAllay;
import sweetmagic.init.entity.animal.WitchGolem;
import sweetmagic.init.entity.animal.WitchIfrit;
import sweetmagic.init.entity.animal.WitchMaster;
import sweetmagic.init.entity.animal.WitchWindine;
import sweetmagic.init.entity.animal.WitchWolf;
import sweetmagic.init.entity.block.ChairEntity;
import sweetmagic.init.entity.monster.ArchSpider;
import sweetmagic.init.entity.monster.BlazeTempest;
import sweetmagic.init.entity.monster.BlazeTempestTornado;
import sweetmagic.init.entity.monster.CherryPlant;
import sweetmagic.init.entity.monster.CreeperCalamity;
import sweetmagic.init.entity.monster.DwarfZombie;
import sweetmagic.init.entity.monster.DwarfZombieMaster;
import sweetmagic.init.entity.monster.ElectricCube;
import sweetmagic.init.entity.monster.EnderMage;
import sweetmagic.init.entity.monster.EnderShadow;
import sweetmagic.init.entity.monster.EnderShadowMirage;
import sweetmagic.init.entity.monster.PhantomWolf;
import sweetmagic.init.entity.monster.PixeVex;
import sweetmagic.init.entity.monster.SkullFlame;
import sweetmagic.init.entity.monster.SkullFlameArcher;
import sweetmagic.init.entity.monster.SkullFrost;
import sweetmagic.init.entity.monster.SkullFrostRoyalGuard;
import sweetmagic.init.entity.monster.WindWitch;
import sweetmagic.init.entity.monster.WitchCrystal;
import sweetmagic.init.entity.monster.boss.AncientFairy;
import sweetmagic.init.entity.monster.boss.Arlaune;
import sweetmagic.init.entity.monster.boss.BullFight;
import sweetmagic.init.entity.monster.boss.HolyAngel;
import sweetmagic.init.entity.monster.boss.IgnisKnight;
import sweetmagic.init.entity.monster.boss.QueenFrost;
import sweetmagic.init.entity.monster.boss.SilverLandRoad;
import sweetmagic.init.entity.monster.boss.WhiteButler;
import sweetmagic.init.entity.monster.boss.WindWitchMaster;
import sweetmagic.init.entity.projectile.BloodMagicShot;
import sweetmagic.init.entity.projectile.BubbleMagicShot;
import sweetmagic.init.entity.projectile.BulletMagicShot;
import sweetmagic.init.entity.projectile.CalamityBomb;
import sweetmagic.init.entity.projectile.CherryMagicShot;
import sweetmagic.init.entity.projectile.CherryRainMagic;
import sweetmagic.init.entity.projectile.CycloneMagicShot;
import sweetmagic.init.entity.projectile.DigMagicShot;
import sweetmagic.init.entity.projectile.ElectricMagicShot;
import sweetmagic.init.entity.projectile.EnderBall;
import sweetmagic.init.entity.projectile.EvilArrow;
import sweetmagic.init.entity.projectile.ExplosionMagicShot;
import sweetmagic.init.entity.projectile.FireMagicShot;
import sweetmagic.init.entity.projectile.FrostLaserMagic;
import sweetmagic.init.entity.projectile.FrostMagicShot;
import sweetmagic.init.entity.projectile.GravityMagicShot;
import sweetmagic.init.entity.projectile.HolyBusterMagic;
import sweetmagic.init.entity.projectile.IgnisBlastMagic;
import sweetmagic.init.entity.projectile.KnifeShot;
import sweetmagic.init.entity.projectile.LightMagicShot;
import sweetmagic.init.entity.projectile.MagicSquareMagic;
import sweetmagic.init.entity.projectile.MeteorMagicShot;
import sweetmagic.init.entity.projectile.NormalMagicShot;
import sweetmagic.init.entity.projectile.PoisonMagicShot;
import sweetmagic.init.entity.projectile.RainMagicShot;
import sweetmagic.init.entity.projectile.RockBlastMagicShot;
import sweetmagic.init.entity.projectile.SickleShot;
import sweetmagic.init.entity.projectile.ToxicCircle;
import sweetmagic.init.entity.projectile.TripleTornadoShot;
import sweetmagic.init.entity.projectile.WindStormMagic;
import sweetmagic.init.entity.projectile.WindStormShot;

public class EntityInit {

	private static final MobCategory MISC = MobCategory.MISC;
	private static final MobCategory CREATURE = MobCategory.CREATURE;
	private static final MobCategory MONSTER = MobCategory.MONSTER;

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPE = SweetMagicCore.getDef(ForgeRegistries.ENTITY_TYPES);
	public static final DeferredRegister<Item> SPAWN_EGGS = SweetMagicCore.getDef(ForgeRegistries.ITEMS);

	public static Map<EntityType<?>, String> entityMap = new HashMap<>();
	public static Map<EntityInfo, String> entityEggMap = new HashMap<>();

	public static EntityType<EnderBall> enderBall = getEntity("enderball", EnderBall::new, MISC);
	public static EntityType<EvilArrow> evilArrow = getEntity("evilarrow", EvilArrow::new, MISC);
	public static EntityType<CalamityBomb> calamityBomb = getEntity("calamitybomb", CalamityBomb::new, MISC);

	public static EntityType<LightMagicShot> lightMagic = getEntity("lightmagic", LightMagicShot::new, MISC);
	public static EntityType<FireMagicShot> fireMagic = getEntity("firemagic", FireMagicShot::new, MISC);
	public static EntityType<FrostMagicShot> frostMagic = getEntity("frostmagic", FrostMagicShot::new, MISC);
	public static EntityType<CycloneMagicShot> cycloneMagic = getEntity("cyclonemagic", CycloneMagicShot::new, MISC);
	public static EntityType<ExplosionMagicShot> explosionMagic = getEntity("explosionmagic", ExplosionMagicShot::new, MISC);
	public static EntityType<GravityMagicShot> gravityMagic = getEntity("gravitymagic", GravityMagicShot::new, MISC);
	public static EntityType<PoisonMagicShot> poisonMagic = getEntity("poisonmagic", PoisonMagicShot::new, MISC);
	public static EntityType<ElectricMagicShot> electricMagic = getEntity("electricmagic", ElectricMagicShot::new, MISC);
	public static EntityType<DigMagicShot> digMagic = getEntity("digmagic", DigMagicShot::new, MISC);
	public static EntityType<NormalMagicShot> normalMagic = getEntity("normalmagic", NormalMagicShot::new, MISC);
	public static EntityType<BulletMagicShot> bulletMagic = getEntity("bulletmagic", BulletMagicShot::new, MISC);
	public static EntityType<BubbleMagicShot> bubbleMagic = getEntity("bubblemagic", BubbleMagicShot::new, MISC);
	public static EntityType<BloodMagicShot> bloodMagic = getEntity("bloodmagic", BloodMagicShot::new, MISC);
	public static EntityType<RainMagicShot> rainMagic = getEntity("rain_magic", RainMagicShot::new, MISC);
	public static EntityType<MeteorMagicShot> meteorMagic = getEntity("meteomagic", MeteorMagicShot::new, MISC);
	public static EntityType<RockBlastMagicShot> rockBlastMagic = getEntity("rockblastmagic", RockBlastMagicShot::new, MISC);
	public static EntityType<FrostLaserMagic> frostLaser = getEntity("frost_laser", FrostLaserMagic::new, MISC);
	public static EntityType<HolyBusterMagic> holyBusert = getEntity("holy_buster", HolyBusterMagic::new, MISC);
	public static EntityType<IgnisBlastMagic> ignisBlast = getEntity("ignis_blast", IgnisBlastMagic::new, MISC);
	public static EntityType<WindStormMagic> windStorm = getEntity("wind_storm", WindStormMagic::new, MISC);
	public static EntityType<MagicSquareMagic> magicSquare = getEntity("magic_square", MagicSquareMagic::new, MISC, 1F, 0.001F);
	public static EntityType<TripleTornadoShot> tripleTornado = getEntity("triple_tprnado", TripleTornadoShot::new, MISC, 1F, 0.001F);
	public static EntityType<WindStormShot> windBlast = getEntity("wind_blast", WindStormShot::new, MISC);
	public static EntityType<CherryRainMagic> cherryRain = getEntity("cherry_rain", CherryRainMagic::new, MISC);
	public static EntityType<CherryMagicShot> cherryMagic = getEntity("cherrymagic", CherryMagicShot::new, MISC);
	public static EntityType<ToxicCircle> toxicCircle = getEntity("toxic_circle", ToxicCircle::new, MISC);
	public static EntityType<KnifeShot> knifeShot = getEntity("knifeshot", KnifeShot::new, MISC);
	public static EntityType<SickleShot> sickleShot = getEntity("sickleshot", SickleShot::new, MISC);

	public static EntityType<ChairEntity> chair = getEntity("chair", ChairEntity::new, MISC);
	public static EntityType<WitchCrystal> witchCrystal = getEntity("witch_crystal", WitchCrystal::new, MONSTER, 1F, 2F);
	public static EntityType<CherryPlant> cherryPlant = getEntity("cherry_plant", CherryPlant::new, MONSTER, 1.75F, 1.75F);

	public static EntityType<WitchWolf> witchWolf = getEntityFire("witch_wolf", WitchWolf::new, CREATURE, 0.85F, 0.9F);
	public static EntityType<WitchGolem> witchGolem = getEntityFire("witch_golem", WitchGolem::new, CREATURE, 1.25F, 2.75F);
	public static EntityType<WitchAllay> witchAllay = getEntityFire("witch_allay", WitchAllay::new, CREATURE, 0.75F, 0.75F);
	public static EntityType<WitchMaster> witchMaster = getEntityFire("witch_master", WitchMaster::new, CREATURE, 0.7F, 1.275F);
	public static EntityType<WitchWindine> witchWindine = getEntityFire("witch_windine", WitchWindine::new, CREATURE, 0.8F, 1.95F);
	public static EntityType<WitchIfrit> witchIfrit = getEntityFire("witch_ifrit", WitchIfrit::new, CREATURE, 0.8F, 1.95F);

	public static EntityType<SkullFrost> skullFrost = getEntityEgg("skullfrost", SkullFrost::new, MONSTER, true, 0.6F, 1.99F, 7842303);
	public static EntityType<BlazeTempest> blazeTempest = getEntityEgg("blazetempest", BlazeTempest::new, MONSTER, true, 0.6F, 1.8F, 1753367);
	public static EntityType<ArchSpider> archSpider = getEntityEgg("archspider", ArchSpider::new, MONSTER, false, 2F, 1.2F, 12197663);
	public static EntityType<ElectricCube> electricCube = getEntityEgg("electriccube", ElectricCube::new, MONSTER, false, 2.04F, 2.04F, 12169572);
	public static EntityType<CreeperCalamity> creeperCalamity = getEntityEgg("creepercalamity", CreeperCalamity::new, MONSTER, false, 0.6F, 1.7F, 5987163);
	public static EntityType<EnderMage> enderMage = getEntityEgg("endermage", EnderMage::new, MONSTER, false, 0.85F, 2.9F, 1447446);
	public static EntityType<SkullFlame> skullFlame = getEntityEgg("skullflame", SkullFlame::new, MONSTER, true, 0.6F, 1.99F, 16732454);
	public static EntityType<DwarfZombie> dwarfZombie = getEntityEgg("dwarfzombie", DwarfZombie::new, MONSTER, true, 0.6F, 1.99F, 15040352);
	public static EntityType<WindWitch> windWitch = getEntityEgg("windwitch", WindWitch::new, MONSTER, true, 0.45F, 1.4F, 6507327);
	public static EntityType<PixeVex> pixeVex = getEntityEgg("pixevex", PixeVex::new, MONSTER, true, 0.525F, 1.4F, 9237157);
	public static EntityType<PhantomWolf> phantomWolf = getEntityEgg("phantomwolf", PhantomWolf::new, MONSTER, true, 0.525F, 1.4F, 9237157);

	public static EntityType<SkullFrostRoyalGuard> skullFrostRoyalGuard = getEntityUniqueEgg("skullfrost_royalguard", SkullFrostRoyalGuard::new, MONSTER, true, 1F, 2.6F, 7842303);
	public static EntityType<SkullFlameArcher> skullFlameArcher = getEntityUniqueEgg("skullflame_archer", SkullFlameArcher::new, MONSTER, true, 1F, 2.6F, 6507327);
	public static EntityType<BlazeTempestTornado> blazeTempestTornado = getEntityUniqueEgg("blazetempest_tornado", BlazeTempestTornado::new, MONSTER, false, 1F, 2.6F, 1753367);
	public static EntityType<EnderShadow> enderShadow = getEntityUniqueEgg("endershadow", EnderShadow::new, MONSTER, false, 0.85F, 3.5F, 1447446);
	public static EntityType<EnderShadowMirage> enderShadowMirage = getEntity("endershadow_mirage", EnderShadowMirage::new, MONSTER, 0.85F, 2.9F);
	public static EntityType<DwarfZombieMaster> dwarfZombieMaster = getEntityUniqueEgg("dwarfzombie_master", DwarfZombieMaster::new, MONSTER, true, 1F, 2.6F, 15040352);

	public static EntityType<QueenFrost> queenFrost = getEntityBossEgg("queenfrost", QueenFrost::new, MONSTER, 1.2F, 3.75F, 8106999);
	public static EntityType<HolyAngel> holyAngel = getEntityBossEgg("holyangel", HolyAngel::new, MONSTER, 1.2F, 3.75F, 6821167);
	public static EntityType<IgnisKnight> ignisKnight = getEntityBossEgg("ignisknight", IgnisKnight::new, MONSTER, 1.2F, 2.25F, 5066165);
	public static EntityType<WindWitchMaster> windWitchMaster = getEntityBossEgg("windwitch_master", WindWitchMaster::new, MONSTER, 1.2F, 2.25F, 6507327);

	public static EntityType<BullFight> bullfight = getEntitySMBossEgg("bullfight", BullFight::new, MONSTER, 3F, 2.25F, 6167571);
	public static EntityType<AncientFairy> ancientFairy = getEntitySMBossEgg("ancientfairy", AncientFairy::new, MONSTER, 1.2F, 3.5F, 9237157);
	public static EntityType<Arlaune> arlaune = getEntitySMBossEgg("arlaune", Arlaune::new, MONSTER, 1.2F, 2.5F, 16418982);
	public static EntityType<SilverLandRoad> silverLandRoad = getEntitySMBossEgg("silver_landroad", SilverLandRoad::new, MONSTER, 2F, 2F, 16418982);
	public static EntityType<WhiteButler> whiteButler = getEntitySMBossEgg("white_butler", WhiteButler::new, MONSTER, 1.2F, 2.5F, 16418982);

	// えんちちーのステータスを設定
	@SubscribeEvent
	public static void registerAttribute(EntityAttributeCreationEvent event) {
		event.put(skullFrost, SkullFrost.registerAttributes().build());
		event.put(blazeTempest, BlazeTempest.registerAttributes().build());
		event.put(archSpider, ArchSpider.registerAttributes().build());
		event.put(electricCube, ElectricCube.registerAttributes().build());
		event.put(creeperCalamity, CreeperCalamity.registerAttributes().build());
		event.put(enderMage, EnderMage.registerAttributes().build());
		event.put(skullFlame, SkullFlame.registerAttributes().build());
		event.put(dwarfZombie, DwarfZombie.registerAttributes().build());
		event.put(windWitch, WindWitch.registerAttributes().build());
		event.put(skullFrostRoyalGuard, SkullFrostRoyalGuard.registerAttributes().build());
		event.put(skullFlameArcher, SkullFlameArcher.registerAttributes().build());
		event.put(blazeTempestTornado, BlazeTempestTornado.registerAttributes().build());
		event.put(enderShadow, EnderShadow.registerAttributes().build());
		event.put(enderShadowMirage, EnderShadowMirage.registerAttributes().build());
		event.put(dwarfZombieMaster, DwarfZombieMaster.registerAttributes().build());
		event.put(witchCrystal, WitchCrystal.registerAttributes().build());
		event.put(cherryPlant, CherryPlant.registerAttributes().build());
		event.put(witchWolf, WitchWolf.registerAttributes().build());
		event.put(witchGolem, WitchGolem.registerAttributes().build());
		event.put(witchAllay, WitchAllay.registerAttributes().build());
		event.put(witchMaster, WitchMaster.registerAttributes().build());
		event.put(witchWindine, WitchWindine.registerAttributes().build());
		event.put(witchIfrit, WitchIfrit.registerAttributes().build());
		event.put(pixeVex, PixeVex.registerAttributes().build());
		event.put(phantomWolf, PhantomWolf.registerAttributes().build());
		event.put(queenFrost, QueenFrost.registerAttributes().build());
		event.put(holyAngel, HolyAngel.registerAttributes().build());
		event.put(ignisKnight, IgnisKnight.registerAttributes().build());
		event.put(windWitchMaster, WindWitchMaster.registerAttributes().build());
		event.put(bullfight, BullFight.registerAttributes().build());
		event.put(ancientFairy, AncientFairy.registerAttributes().build());
		event.put(arlaune, Arlaune.registerAttributes().build());
		event.put(silverLandRoad, SilverLandRoad.registerAttributes().build());
		event.put(whiteButler, WhiteButler.registerAttributes().build());
	}

	// えんちちースポーンの設定
	public static void registerSpawnSetting (List<MobSpawnSettings.SpawnerData> spawnSet) {
		spawnSet(spawnSet, EntityInit.skullFrost, 100, 2, 2);
		spawnSet(spawnSet, EntityInit.blazeTempest, 100, 2, 2);
		spawnSet(spawnSet, EntityInit.archSpider, 100, 2, 2);
		spawnSet(spawnSet, EntityInit.creeperCalamity, 100, 2, 2);
		spawnSet(spawnSet, EntityInit.electricCube, 100, 2, 2);
		spawnSet(spawnSet, EntityInit.enderMage, 100, 2, 2);
		spawnSet(spawnSet, EntityInit.windWitch, 100, 2, 2);
		spawnSet(spawnSet, EntityInit.skullFlame, 100, 2, 2);
		spawnSet(spawnSet, EntityInit.dwarfZombie, 100, 2, 2);
	}

	public static void spawnSet(List<MobSpawnSettings.SpawnerData> spawnSet, EntityType<?> eType, int weight, int min, int max) {
		spawnSet.add(new MobSpawnSettings.SpawnerData(eType, weight, min, max));
	}

	// えんちちースポーンの条件時設定
	@SubscribeEvent
	public static void registerSpawn(SpawnPlacementRegisterEvent event) {
		register(skullFrost, ISMMob::checkMonsterSpawnRules);
		register(blazeTempest, ISMMob::checkMonsterSpawnRules);
		register(archSpider, ISMMob::checkMonsterSpawnRules);
		register(creeperCalamity, ISMMob::checkMonsterSpawnRules);
		register(electricCube, ISMMob::checkMonsterSpawnRules);
		register(enderMage, ISMMob::checkMonsterSpawnRules);
		register(windWitch, ISMMob::checkMonsterSpawnRules);

		register(skullFlame, ISMMob::checkMonsterSpawnRulesSM);
		register(dwarfZombie, ISMMob::checkMonsterSpawnRulesSM);
	}

	// スポーンエッグなしのえんちちー設定
	public static <T extends Entity> EntityType<T> getEntity (String name, EntityType.EntityFactory<T> fact, MobCategory cate, float xSize, float ySize) {
		EntityType<T> eType = entityBuild(fact, cate).sized(xSize, ySize).clientTrackingRange(8).setTrackingRange(512).setUpdateInterval(1).build(name);
		entityMap.put(eType, name);
		return eType;
	}

	// スポーンエッグなしのえんちちー設定
	public static <T extends Entity> EntityType<T> getEntityFire (String name, EntityType.EntityFactory<T> fact, MobCategory cate, float xSize, float ySize) {
		EntityType<T> eType = entityBuild(fact, cate).fireImmune().sized(xSize, ySize).clientTrackingRange(8).setTrackingRange(512).setUpdateInterval(1).build(name);
		entityMap.put(eType, name);
		return eType;
	}

	// スポーンエッグなしのえんちちー設定（サイズ固定）
	public static <T extends Entity> EntityType<T> getEntity (String name, EntityType.EntityFactory<T> fact, MobCategory cate) {
		return getEntity(name, fact, cate, 1F, 1F);
	}

	// スポーンエッグ付きえんちちー設定
	public static <T extends Entity> EntityType<T> getEntityEgg (String name, EntityType.EntityFactory<T> fact, MobCategory cate, boolean isFire, float x, float z, int secondary) {
		EntityType.Builder<T> build = isFire ? entityBuild(fact, cate).fireImmune() : entityBuild(fact, cate);
		EntityType<T> eType = build.sized(x, z).clientTrackingRange(8).build(SweetMagicCore.MODID + ":" + name);
		entityEggMap.put(new EntityInfo(eType, 15985789, secondary), name);
		return eType;
	}

	// スポーンエッグ付き中ボスえんちちー設定
	public static <T extends Entity> EntityType<T> getEntityUniqueEgg (String name, EntityType.EntityFactory<T> fact, MobCategory cate, boolean isFire, float x, float z, int secondary) {
		EntityType.Builder<T> build = isFire ? entityBuild(fact, cate).fireImmune() : entityBuild(fact, cate);
		EntityType<T> eType = build.sized(x, z).clientTrackingRange(8).build(SweetMagicCore.MODID + ":" + name);
		entityEggMap.put(new EntityInfo(eType, 16237196, secondary), name);
		return eType;
	}

	// スポーンエッグ付き大ボスえんちちー設定
	public static <T extends Entity> EntityType<T> getEntityBossEgg (String name, EntityType.EntityFactory<T> fact, MobCategory cate, float x, float z, int secondary) {
		EntityType<T> eType = entityBuild(fact, cate).fireImmune().sized(x, z).clientTrackingRange(8).build(SweetMagicCore.MODID + ":" + name);
		entityEggMap.put(new EntityInfo(eType, 15250701, secondary), name);
		return eType;
	}

	// スポーンエッグ付き大ボスえんちちー設定
	public static <T extends Entity> EntityType<T> getEntitySMBossEgg (String name, EntityType.EntityFactory<T> fact, MobCategory cate, float x, float z, int secondary) {
		EntityType<T> eType = entityBuild(fact, cate).fireImmune().sized(x, z).clientTrackingRange(8).build(SweetMagicCore.MODID + ":" + name);
		entityEggMap.put(new EntityInfo(eType, 14710285, secondary), name);
		return eType;
	}

	// えんちちータイプ設定
	public static <T extends Entity> EntityType.Builder<T> entityBuild(EntityType.EntityFactory<T> fact, MobCategory cate) {
        return EntityType.Builder.<T>of(fact, cate);
	}

	@SubscribeEvent
    public static void registerEntity(RegisterEvent event) {
        event.register(ForgeRegistries.Keys.ENTITY_TYPES, h -> {
    		entityMap.forEach((key, val) -> h.register(SweetMagicCore.getSRC(val), key));
    		entityEggMap.forEach((key, val) -> h.register(SweetMagicCore.getSRC(val), key.getEntityType()));
		});

        event.register(ForgeRegistries.Keys.ITEMS, h -> {
        	for (Entry<EntityInfo, String> map : entityEggMap.entrySet()) {
        		EntityInfo enInfo = map.getKey();
        		Item item = new ForgeSpawnEggItem(() -> (EntityType<? extends Mob>) enInfo.getEntityType(), enInfo.getPrimary(), enInfo.getSecondary(), new Item.Properties().tab(CreativeModeTab.TAB_MISC));
    			h.register(SweetMagicCore.getSRC(map.getValue() + "_spawn_egg"), item);
        	}
		});
    }

	public static <T extends Mob> void register (EntityType<T> eType, SpawnPlacements.SpawnPredicate<T> spawnRule) {
		SpawnPlacements.register(eType, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, spawnRule);
	}

	public static record EntityInfo(EntityType<?> eType, int primary, int secondary) {

		public EntityType<?> getEntityType () {
			return this.eType;
		}

		public int getPrimary () {
			return this.primary;
		}

		public int getSecondary () {
			return this.secondary;
		}
	}
}
