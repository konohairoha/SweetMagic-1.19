package sweetmagic.init;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
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
import sweetmagic.init.entity.animal.StellaWizard;
import sweetmagic.init.entity.animal.WitchAllay;
import sweetmagic.init.entity.animal.WitchCat;
import sweetmagic.init.entity.animal.WitchFox;
import sweetmagic.init.entity.animal.WitchGolem;
import sweetmagic.init.entity.animal.WitchIfrit;
import sweetmagic.init.entity.animal.WitchMaster;
import sweetmagic.init.entity.animal.WitchWindine;
import sweetmagic.init.entity.animal.WitchWolf;
import sweetmagic.init.entity.block.Chair;
import sweetmagic.init.entity.block.Cushion;
import sweetmagic.init.entity.monster.ArchSpider;
import sweetmagic.init.entity.monster.BlazeTempest;
import sweetmagic.init.entity.monster.BlazeTempestTornado;
import sweetmagic.init.entity.monster.BlitzWizard;
import sweetmagic.init.entity.monster.CherryPlant;
import sweetmagic.init.entity.monster.CreeperCalamity;
import sweetmagic.init.entity.monster.DwarfZombie;
import sweetmagic.init.entity.monster.DwarfZombieMaster;
import sweetmagic.init.entity.monster.ElectricCube;
import sweetmagic.init.entity.monster.ElectricGolem;
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
import sweetmagic.init.entity.monster.boss.BlitzWizardMaster;
import sweetmagic.init.entity.monster.boss.BraveSkeleton;
import sweetmagic.init.entity.monster.boss.BullFight;
import sweetmagic.init.entity.monster.boss.DemonsBelial;
import sweetmagic.init.entity.monster.boss.ElshariaCurious;
import sweetmagic.init.entity.monster.boss.HolyAngel;
import sweetmagic.init.entity.monster.boss.IgnisKnight;
import sweetmagic.init.entity.monster.boss.QueenFrost;
import sweetmagic.init.entity.monster.boss.SilverLandRoad;
import sweetmagic.init.entity.monster.boss.StellaWizardMaster;
import sweetmagic.init.entity.monster.boss.TwilightHora;
import sweetmagic.init.entity.monster.boss.WhiteButler;
import sweetmagic.init.entity.monster.boss.WindWitchMaster;
import sweetmagic.init.entity.monster.boss.WitchSandryon;
import sweetmagic.init.entity.projectile.BelialFlameShot;
import sweetmagic.init.entity.projectile.BelialSword;
import sweetmagic.init.entity.projectile.BloodMagicShot;
import sweetmagic.init.entity.projectile.BraveShot;
import sweetmagic.init.entity.projectile.BubbleMagicShot;
import sweetmagic.init.entity.projectile.BulletMagicShot;
import sweetmagic.init.entity.projectile.CalamityBomb;
import sweetmagic.init.entity.projectile.CherryMagicShot;
import sweetmagic.init.entity.projectile.CherryRainMagic;
import sweetmagic.init.entity.projectile.CommetBulet;
import sweetmagic.init.entity.projectile.CycloneMagicShot;
import sweetmagic.init.entity.projectile.DigMagicShot;
import sweetmagic.init.entity.projectile.ElectricMagicShot;
import sweetmagic.init.entity.projectile.ElectricSphere;
import sweetmagic.init.entity.projectile.EnderBall;
import sweetmagic.init.entity.projectile.EvilArrow;
import sweetmagic.init.entity.projectile.ExplosionMagicShot;
import sweetmagic.init.entity.projectile.ExplosionThunderShot;
import sweetmagic.init.entity.projectile.FireMagicShot;
import sweetmagic.init.entity.projectile.FrostLaserMagic;
import sweetmagic.init.entity.projectile.FrostMagicShot;
import sweetmagic.init.entity.projectile.GravityMagicShot;
import sweetmagic.init.entity.projectile.HolyBusterMagic;
import sweetmagic.init.entity.projectile.IgnisBlastMagic;
import sweetmagic.init.entity.projectile.InfinitWandMagic;
import sweetmagic.init.entity.projectile.KnifeShot;
import sweetmagic.init.entity.projectile.LightMagicShot;
import sweetmagic.init.entity.projectile.LightningRod;
import sweetmagic.init.entity.projectile.LigningBulletShot;
import sweetmagic.init.entity.projectile.MagicSquareMagic;
import sweetmagic.init.entity.projectile.NormalMagicShot;
import sweetmagic.init.entity.projectile.PoisonMagicShot;
import sweetmagic.init.entity.projectile.RockBlastMagicShot;
import sweetmagic.init.entity.projectile.ShootingStar;
import sweetmagic.init.entity.projectile.SickleShot;
import sweetmagic.init.entity.projectile.SoulBlazeShot;
import sweetmagic.init.entity.projectile.ToxicCircle;
import sweetmagic.init.entity.projectile.TridentThunder;
import sweetmagic.init.entity.projectile.TripleTornadoShot;
import sweetmagic.init.entity.projectile.TwiLightShot;
import sweetmagic.init.entity.projectile.WindStormMagic;
import sweetmagic.init.entity.projectile.WindStormShot;

public class EntityInit {

	private static final MobCategory MISC = MobCategory.MISC;
	private static final MobCategory CREATURE = MobCategory.CREATURE;
	private static final MobCategory MONSTER = MobCategory.MONSTER;
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPE = SweetMagicCore.getDef(ForgeRegistries.ENTITY_TYPES);
	public static Map<EntityType<?>, String> entityMap = new HashMap<>();
	public static Map<EntityInfo, String> entityEggMap = new HashMap<>();

	public static EntityType<EnderBall> enderBall = getEntity("enderball", EnderBall::new);
	public static EntityType<EvilArrow> evilArrow = getEntity("evilarrow", EvilArrow::new);
	public static EntityType<CalamityBomb> calamityBomb = getEntity("calamitybomb", CalamityBomb::new);
	public static EntityType<LightningRod> lightningRod = getEntity("lightningrod", LightningRod::new);
	public static EntityType<TridentThunder> tridentThunder = getEntity("tridentthunder", TridentThunder::new, MISC, 0.5F, 0.5F);

	public static EntityType<LightMagicShot> lightMagic = getEntity("lightmagic", LightMagicShot::new);
	public static EntityType<FireMagicShot> fireMagic = getEntity("firemagic", FireMagicShot::new);
	public static EntityType<FrostMagicShot> frostMagic = getEntity("frostmagic", FrostMagicShot::new);
	public static EntityType<CycloneMagicShot> cycloneMagic = getEntity("cyclonemagic", CycloneMagicShot::new);
	public static EntityType<ExplosionMagicShot> explosionMagic = getEntity("explosionmagic", ExplosionMagicShot::new);
	public static EntityType<GravityMagicShot> gravityMagic = getEntity("gravitymagic", GravityMagicShot::new);
	public static EntityType<PoisonMagicShot> poisonMagic = getEntity("poisonmagic", PoisonMagicShot::new);
	public static EntityType<ElectricMagicShot> electricMagic = getEntity("electricmagic", ElectricMagicShot::new);
	public static EntityType<DigMagicShot> digMagic = getEntity("digmagic", DigMagicShot::new);
	public static EntityType<NormalMagicShot> normalMagic = getEntity("normalmagic", NormalMagicShot::new);
	public static EntityType<BulletMagicShot> bulletMagic = getEntity("bulletmagic", BulletMagicShot::new);
	public static EntityType<BubbleMagicShot> bubbleMagic = getEntity("bubblemagic", BubbleMagicShot::new);
	public static EntityType<BloodMagicShot> bloodMagic = getEntity("bloodmagic", BloodMagicShot::new);
	public static EntityType<RockBlastMagicShot> rockBlastMagic = getEntity("rockblastmagic", RockBlastMagicShot::new);
	public static EntityType<FrostLaserMagic> frostLaser = getEntity("frost_laser", FrostLaserMagic::new);
	public static EntityType<HolyBusterMagic> holyBusert = getEntity("holy_buster", HolyBusterMagic::new);
	public static EntityType<IgnisBlastMagic> ignisBlast = getEntity("ignis_blast", IgnisBlastMagic::new);
	public static EntityType<WindStormMagic> windStorm = getEntity("wind_storm", WindStormMagic::new);
	public static EntityType<MagicSquareMagic> magicSquare = getEntity("magic_square", MagicSquareMagic::new, MISC, 1F, 0.001F);
	public static EntityType<TripleTornadoShot> tripleTornado = getEntity("triple_tprnado", TripleTornadoShot::new, MISC, 1F, 0.001F);
	public static EntityType<WindStormShot> windBlast = getEntity("wind_blast", WindStormShot::new);
	public static EntityType<CherryRainMagic> cherryRain = getEntity("cherry_rain", CherryRainMagic::new);
	public static EntityType<CherryMagicShot> cherryMagic = getEntity("cherrymagic", CherryMagicShot::new);
	public static EntityType<ToxicCircle> toxicCircle = getEntity("toxic_circle", ToxicCircle::new);
	public static EntityType<KnifeShot> knifeShot = getEntity("knifeshot", KnifeShot::new);
	public static EntityType<SickleShot> sickleShot = getEntity("sickleshot", SickleShot::new);
	public static EntityType<SoulBlazeShot> soulBlazeShot = getEntity("soulblaze", SoulBlazeShot::new);
	public static EntityType<TwiLightShot> twiLightShot = getEntity("twilightshot", TwiLightShot::new);
	public static EntityType<BraveShot> braveShot = getEntity("braveshot", BraveShot::new);
	public static EntityType<ExplosionThunderShot> explosionThunder = getEntity("explosionthunder", ExplosionThunderShot::new);
	public static EntityType<LigningBulletShot> ligningBullet = getEntity("ligningbullet", LigningBulletShot::new);
	public static EntityType<InfinitWandMagic> infinitWand = getEntity("infinit_wand", InfinitWandMagic::new);
	public static EntityType<ElectricSphere> electricSphere = getEntity("electricsphere", ElectricSphere::new);
	public static EntityType<CommetBulet> commetBulet = getEntity("commetbulet", CommetBulet::new);
	public static EntityType<ShootingStar> shootingStar = getEntity("shooting_star", ShootingStar::new);
	public static EntityType<BelialFlameShot> belialFlameShot = getEntity("belial_flame", BelialFlameShot::new);
	public static EntityType<BelialSword> belialSword = getEntity("belial_sword", BelialSword::new, MISC, 1.5F, 3F);

	public static EntityType<Chair> chair = getEntity("chair", Chair::new);
	public static EntityType<Cushion> cushion = getEntity("cushion", Cushion::new, MISC, 0.75F, 0.0625F);
	public static EntityType<WitchCrystal> witchCrystal = getEntity("witch_crystal", WitchCrystal::new, MONSTER, 1F, 2F);
	public static EntityType<CherryPlant> cherryPlant = getEntity("cherry_plant", CherryPlant::new, MONSTER, 1.75F, 1.75F);

	public static EntityType<WitchWolf> witchWolf = getEntityFire("witch_wolf", WitchWolf::new, CREATURE, 0.85F, 0.9F);
	public static EntityType<WitchAllay> witchAllay = getEntityFire("witch_allay", WitchAllay::new, CREATURE, 0.75F, 0.75F);
	public static EntityType<WitchGolem> witchGolem = getEntityFire("witch_golem", WitchGolem::new, CREATURE, 1.25F, 2.75F);
	public static EntityType<WitchFox> witchFox = getEntityFire("witch_fox", WitchFox::new, CREATURE, 0.85F, 0.9F);
	public static EntityType<WitchCat> witchCat = getEntityFire("witch_cat", WitchCat::new, CREATURE, 0.85F, 0.9F);
	public static EntityType<WitchMaster> witchMaster = getEntityFire("witch_master", WitchMaster::new, CREATURE, 0.7F, 1.275F);
	public static EntityType<WitchWindine> witchWindine = getEntityFire("witch_windine", WitchWindine::new, CREATURE, 0.8F, 1.95F);
	public static EntityType<WitchIfrit> witchIfrit = getEntityFire("witch_ifrit", WitchIfrit::new, CREATURE, 0.8F, 1.95F);
	public static EntityType<StellaWizard> stellaWizard = getEntityFire("stella_wizard", StellaWizard::new, CREATURE, 1F, 2.25F);

	public static EntityType<SkullFrost> skullFrost = getEntityEgg("skullfrost", SkullFrost::new, true, 0.6F, 1.99F, 7842303);
	public static EntityType<BlazeTempest> blazeTempest = getEntityEgg("blazetempest", BlazeTempest::new, true, 0.6F, 1.8F, 1753367);
	public static EntityType<ArchSpider> archSpider = getEntityEgg("archspider", ArchSpider::new, false, 2F, 1.2F, 12197663);
	public static EntityType<ElectricCube> electricCube = getEntityEgg("electriccube", ElectricCube::new, false, 2.04F, 2.04F, 0XEAC23F);
	public static EntityType<CreeperCalamity> creeperCalamity = getEntityEgg("creepercalamity", CreeperCalamity::new, false, 0.6F, 1.7F, 5987163);
	public static EntityType<EnderMage> enderMage = getEntityEgg("endermage", EnderMage::new, false, 0.85F, 2.9F, 0XC437EF);
	public static EntityType<SkullFlame> skullFlame = getEntityEgg("skullflame", SkullFlame::new, true, 0.6F, 1.99F, 0XF46738);
	public static EntityType<DwarfZombie> dwarfZombie = getEntityEgg("dwarfzombie", DwarfZombie::new, true, 0.6F, 1.99F, 0X964D34);
	public static EntityType<WindWitch> windWitch = getEntityEgg("windwitch", WindWitch::new, true, 0.45F, 1.4F, 6507327);
	public static EntityType<PixeVex> pixeVex = getEntityEgg("pixevex", PixeVex::new, true, 0.525F, 1.4F, 0XFF9EA4);
	public static EntityType<PhantomWolf> phantomWolf = getEntityEgg("phantomwolf", PhantomWolf::new, true, 0.525F, 1.4F, 15066597);

	public static EntityType<SkullFrostRoyalGuard> skullFrostRoyalGuard = getEntityUniqueEgg("skullfrost_royalguard", SkullFrostRoyalGuard::new, true, 1F, 2.6F, 7842303);
	public static EntityType<SkullFlameArcher> skullFlameArcher = getEntityUniqueEgg("skullflame_archer", SkullFlameArcher::new, true, 1F, 2.6F, 0XF46738);
	public static EntityType<BlazeTempestTornado> blazeTempestTornado = getEntityUniqueEgg("blazetempest_tornado", BlazeTempestTornado::new, false, 1F, 2.6F, 1753367);
	public static EntityType<EnderShadow> enderShadow = getEntityUniqueEgg("endershadow", EnderShadow::new, false, 0.85F, 3.5F, 0XC437EF);
	public static EntityType<EnderShadowMirage> enderShadowMirage = getEntity("endershadow_mirage", EnderShadowMirage::new, MONSTER, 0.85F, 2.9F);
	public static EntityType<DwarfZombieMaster> dwarfZombieMaster = getEntityUniqueEgg("dwarfzombie_master", DwarfZombieMaster::new, true, 1F, 2.6F, 0X964D34);
	public static EntityType<ElectricGolem> electricGolem = getEntityUniqueEgg("electricgolem", ElectricGolem::new, true, 1.25F, 2.75F, 0XEAC23F);
	public static EntityType<BlitzWizard> blitzWizard = getEntityUniqueEgg("blitz_wizard", BlitzWizard::new, true, 1.2F, 2.25F, 0XAA254F);

	public static EntityType<QueenFrost> queenFrost = getEntityBossEgg("queenfrost", QueenFrost::new, 1.2F, 3.75F, 8106999);
	public static EntityType<HolyAngel> holyAngel = getEntityBossEgg("holyangel", HolyAngel::new, 1.2F, 3.75F, 6821167);
	public static EntityType<IgnisKnight> ignisKnight = getEntityBossEgg("ignisknight", IgnisKnight::new, 1.2F, 2.25F, 5066165);
	public static EntityType<WindWitchMaster> windWitchMaster = getEntityBossEgg("windwitch_master", WindWitchMaster::new, 1.2F, 2.25F, 1753367);
	public static EntityType<BullFight> bullfight = getEntityBossEgg("bullfight", BullFight::new, 3F, 2.25F, 0XFF490C);
	public static EntityType<AncientFairy> ancientFairy = getEntityBossEgg("ancientfairy", AncientFairy::new, 1.2F, 3.5F, 9237157);
	public static EntityType<Arlaune> arlaune = getEntityBossEgg("arlaune", Arlaune::new, 1.2F, 2.5F, 0XFA94AF);
	public static EntityType<SilverLandRoad> silverLandRoad = getEntityBossEgg("silver_landroad", SilverLandRoad::new, 2F, 2F, 15066597);
	public static EntityType<WhiteButler> whiteButler = getEntityBossEgg("white_butler", WhiteButler::new, 1.2F, 2.5F, 0XA5A5A5);
	public static EntityType<BlitzWizardMaster> blitzWizardMaster = getEntityBossEgg("blitz_wizard_master", BlitzWizardMaster::new, 1.2F, 2.5F, 0XAA254F);
	public static EntityType<StellaWizardMaster> stellaWizardMaster = getEntityBossEgg("stella_wizard_master", StellaWizardMaster::new, 1.2F, 2.5F, 0XE6EA62);
	public static EntityType<DemonsBelial> demonsBelial = getEntityBossEgg("demons_belial", DemonsBelial::new, 1.75F, 3.25F, 0X060C1E);
	public static EntityType<TwilightHora> twilightHora = getEntityBossEgg("twilight_hora", TwilightHora::new, 1.2F, 2.5F, 0XA53B3A);
	public static EntityType<BraveSkeleton> braveSkeleton = getEntityBossEgg("brave_skeleton", BraveSkeleton::new, 1.2F, 3F, 0XFC5700);
	public static EntityType<ElshariaCurious> elshariaCurious = getEntityBossEgg("elsharia_curious", ElshariaCurious::new, 1.2F, 3.5F, 0X0F344C);
	public static EntityType<WitchSandryon> witchSandryon = getEntityBossEgg("witch_sandryon", WitchSandryon::new, 1.2F, 2.25F, 0X9FECF9);

	// えんちちーのステータスを設定
	@SubscribeEvent
	public static void registerAttribute(EntityAttributeCreationEvent event) {
		Map<EntityType<? extends LivingEntity>, AttributeSupplier.Builder> map = new HashMap<>();
		map.put(skullFrost, SkullFrost.registerAttributes());
		map.put(blazeTempest, BlazeTempest.registerAttributes());
		map.put(archSpider, ArchSpider.registerAttributes());
		map.put(electricCube, ElectricCube.registerAttributes());
		map.put(creeperCalamity, CreeperCalamity.registerAttributes());
		map.put(enderMage, EnderMage.registerAttributes());
		map.put(skullFlame, SkullFlame.registerAttributes());
		map.put(dwarfZombie, DwarfZombie.registerAttributes());
		map.put(windWitch, WindWitch.registerAttributes());
		map.put(skullFrostRoyalGuard, SkullFrostRoyalGuard.registerAttributes());
		map.put(skullFlameArcher, SkullFlameArcher.registerAttributes());
		map.put(blazeTempestTornado, BlazeTempestTornado.registerAttributes());
		map.put(enderShadow, EnderShadow.registerAttributes());
		map.put(enderShadowMirage, EnderShadowMirage.registerAttributes());
		map.put(dwarfZombieMaster, DwarfZombieMaster.registerAttributes());
		map.put(witchCrystal, WitchCrystal.registerAttributes());
		map.put(cherryPlant, CherryPlant.registerAttributes());
		map.put(witchCat, WitchCat.registerAttributes());
		map.put(witchWolf, WitchWolf.registerAttributes());
		map.put(witchAllay, WitchAllay.registerAttributes());
		map.put(witchGolem, WitchGolem.registerAttributes());
		map.put(witchFox, WitchFox.registerAttributes());
		map.put(witchMaster, WitchMaster.registerAttributes());
		map.put(witchWindine, WitchWindine.registerAttributes());
		map.put(witchIfrit, WitchIfrit.registerAttributes());
		map.put(stellaWizard, StellaWizard.registerAttributes());
		map.put(pixeVex, PixeVex.registerAttributes());
		map.put(phantomWolf, PhantomWolf.registerAttributes());
		map.put(electricGolem, ElectricGolem.registerAttributes());
		map.put(blitzWizard, BlitzWizard.registerAttributes());
		map.put(queenFrost, QueenFrost.registerAttributes());
		map.put(holyAngel, HolyAngel.registerAttributes());
		map.put(ignisKnight, IgnisKnight.registerAttributes());
		map.put(windWitchMaster, WindWitchMaster.registerAttributes());
		map.put(bullfight, BullFight.registerAttributes());
		map.put(ancientFairy, AncientFairy.registerAttributes());
		map.put(arlaune, Arlaune.registerAttributes());
		map.put(silverLandRoad, SilverLandRoad.registerAttributes());
		map.put(whiteButler, WhiteButler.registerAttributes());
		map.put(blitzWizardMaster, BlitzWizardMaster.registerAttributes());
		map.put(stellaWizardMaster, StellaWizardMaster.registerAttributes());
		map.put(demonsBelial, DemonsBelial.registerAttributes());
		map.put(twilightHora, TwilightHora.registerAttributes());
		map.put(braveSkeleton, BraveSkeleton.registerAttributes());
		map.put(elshariaCurious, ElshariaCurious.registerAttributes());
		map.put(witchSandryon, WitchSandryon.registerAttributes());

		map.forEach((e, a) -> event.put(e, a.build()));
	}

	// えんちちースポーンの設定
	public static void registerSpawnSetting(List<MobSpawnSettings.SpawnerData> spawnSet) {
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
	public static <T extends Entity> EntityType<T> getEntity(String name, EntityType.EntityFactory<T> fact, MobCategory cate, float xSize, float ySize) {
		EntityType<T> eType = entityBuild(fact, cate).sized(xSize, ySize).clientTrackingRange(8).setTrackingRange(512).setUpdateInterval(1).build(name);
		entityMap.put(eType, name);
		return eType;
	}

	// スポーンエッグなしのえんちちー設定
	public static <T extends Entity> EntityType<T> getEntityFire(String name, EntityType.EntityFactory<T> fact, MobCategory cate, float xSize, float ySize) {
		EntityType<T> eType = entityBuild(fact, cate).fireImmune().sized(xSize, ySize).clientTrackingRange(8).setTrackingRange(512).setUpdateInterval(1).build(name);
		entityMap.put(eType, name);
		return eType;
	}

	// スポーンエッグなしのえんちちー設定（サイズ固定）
	public static <T extends Entity> EntityType<T> getEntity(String name, EntityType.EntityFactory<T> fact) {
		return getEntity(name, fact, MISC, 1F, 1F);
	}

	// スポーンエッグ付きえんちちー設定
	public static <T extends Entity> EntityType<T> getEntityEgg(String name, EntityType.EntityFactory<T> fact, boolean isFire, float x, float z, int secondary) {
		EntityType.Builder<T> build = isFire ? entityBuild(fact, MONSTER).fireImmune() : entityBuild(fact, MONSTER);
		EntityType<T> eType = build.sized(x, z).clientTrackingRange(8).build(SweetMagicCore.MODID + ":" + name);
		entityEggMap.put(new EntityInfo(eType, 15985789, secondary), name);
		return eType;
	}

	// スポーンエッグ付き中ボスえんちちー設定
	public static <T extends Entity> EntityType<T> getEntityUniqueEgg(String name, EntityType.EntityFactory<T> fact, boolean isFire, float x, float z, int secondary) {
		EntityType.Builder<T> build = isFire ? entityBuild(fact, MONSTER).fireImmune() : entityBuild(fact, MONSTER);
		EntityType<T> eType = build.sized(x, z).clientTrackingRange(8).build(SweetMagicCore.MODID + ":" + name);
		entityEggMap.put(new EntityInfo(eType, 16237196, secondary), name);
		return eType;
	}

	// スポーンエッグ付き大ボスえんちちー設定
	public static <T extends Entity> EntityType<T> getEntityBossEgg(String name, EntityType.EntityFactory<T> fact, float x, float z, int secondary) {
		EntityType<T> eType = entityBuild(fact, MONSTER).fireImmune().sized(x, z).clientTrackingRange(8).build(SweetMagicCore.MODID + ":" + name);
		entityEggMap.put(new EntityInfo(eType, 15250701, secondary), name);
		return eType;
	}

	// えんちちータイプ設定
	public static <T extends Entity> EntityType.Builder<T> entityBuild(EntityType.EntityFactory<T> fact, MobCategory cate) {
		return EntityType.Builder.<T> of(fact, cate);
	}

	@SubscribeEvent
	public static void registerEntity(RegisterEvent event) {
		event.register(ForgeRegistries.Keys.ENTITY_TYPES, h -> {
			entityMap.forEach((key, val) -> h.register(SweetMagicCore.getSRC(val), key));
			entityEggMap.forEach((key, val) -> h.register(SweetMagicCore.getSRC(val), key.eType()));
		});

		event.register(ForgeRegistries.Keys.ITEMS, h -> {
			for (Entry<EntityInfo, String> map : entityEggMap.entrySet()) {
				EntityInfo info = map.getKey();
				Item item = new ForgeSpawnEggItem(() -> (EntityType<? extends Mob>) info.eType(), info.primary(), info.secondary(), new Item.Properties().tab(CreativeModeTab.TAB_MISC));
				h.register(SweetMagicCore.getSRC(map.getValue() + "_spawn_egg"), item);
			}
		});
	}

	public static <T extends Mob> void register(EntityType<T> eType, SpawnPlacements.SpawnPredicate<T> spawnRule) {
		SpawnPlacements.register(eType, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, spawnRule);
	}

	public static record EntityInfo(EntityType<?> eType, int primary, int secondary) { }
}
