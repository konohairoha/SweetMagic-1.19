package sweetmagic.init;

import java.util.function.Supplier;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import sweetmagic.init.render.block.RenderAccessoryTable;
import sweetmagic.init.render.block.RenderAetherLamplight;
import sweetmagic.init.render.block.RenderAetherPlanter;
import sweetmagic.init.render.block.RenderAetherRepair;
import sweetmagic.init.render.block.RenderAetherReverse;
import sweetmagic.init.render.block.RenderAlstroemeriaAquarium;
import sweetmagic.init.render.block.RenderAltarCreat;
import sweetmagic.init.render.block.RenderAltarCreatStar;
import sweetmagic.init.render.block.RenderAquariumPot;
import sweetmagic.init.render.block.RenderBossFigurine;
import sweetmagic.init.render.block.RenderBottle;
import sweetmagic.init.render.block.RenderEnchantEduce;
import sweetmagic.init.render.block.RenderFrypan;
import sweetmagic.init.render.block.RenderFurnitureTable;
import sweetmagic.init.render.block.RenderJuiceMaker;
import sweetmagic.init.render.block.RenderMFBottler;
import sweetmagic.init.render.block.RenderMFFisher;
import sweetmagic.init.render.block.RenderMFMinerAdvanced;
import sweetmagic.init.render.block.RenderMFTable;
import sweetmagic.init.render.block.RenderMFTank;
import sweetmagic.init.render.block.RenderMagiaAccelerator;
import sweetmagic.init.render.block.RenderMagiaRewrite;
import sweetmagic.init.render.block.RenderMagicianLectern;
import sweetmagic.init.render.block.RenderMirageGlass;
import sweetmagic.init.render.block.RenderModenRack;
import sweetmagic.init.render.block.RenderParallelInterfere;
import sweetmagic.init.render.block.RenderPedalCreate;
import sweetmagic.init.render.block.RenderPlate;
import sweetmagic.init.render.block.RenderPot;
import sweetmagic.init.render.block.RenderSMSpawner;
import sweetmagic.init.render.block.RenderSMSpawnerBoss;
import sweetmagic.init.render.block.RenderStardustWish;
import sweetmagic.init.render.block.RenderSturdustCrystal;
import sweetmagic.init.render.block.RenderTransferGate;
import sweetmagic.init.render.block.RenderWandPedastal;
import sweetmagic.init.render.entity.animal.RenderWitchAllay;
import sweetmagic.init.render.entity.animal.RenderWitchGolem;
import sweetmagic.init.render.entity.animal.RenderWitchIfrit;
import sweetmagic.init.render.entity.animal.RenderWitchMaster;
import sweetmagic.init.render.entity.animal.RenderWitchWindine;
import sweetmagic.init.render.entity.animal.RenderWitchWolf;
import sweetmagic.init.render.entity.layer.FrostEffectRender;
import sweetmagic.init.render.entity.layer.WandRenderLayer;
import sweetmagic.init.render.entity.model.AncientFairyModel;
import sweetmagic.init.render.entity.model.ArlauneModel;
import sweetmagic.init.render.entity.model.BullfightModel;
import sweetmagic.init.render.entity.model.IgnisModel;
import sweetmagic.init.render.entity.model.PorchModel;
import sweetmagic.init.render.entity.model.QuenModel;
import sweetmagic.init.render.entity.model.SMHolyModel;
import sweetmagic.init.render.entity.model.SMRobeModel;
import sweetmagic.init.render.entity.model.SMWitchModel;
import sweetmagic.init.render.entity.model.TempestModel;
import sweetmagic.init.render.entity.model.WhiteButlerModel;
import sweetmagic.init.render.entity.model.WindWitchModel;
import sweetmagic.init.render.entity.model.WitchAllayModel;
import sweetmagic.init.render.entity.model.WitchGolemModel;
import sweetmagic.init.render.entity.model.WitchWolfModel;
import sweetmagic.init.render.entity.monster.RenderAncientFairy;
import sweetmagic.init.render.entity.monster.RenderArchSpider;
import sweetmagic.init.render.entity.monster.RenderArlaune;
import sweetmagic.init.render.entity.monster.RenderBlazeTempest;
import sweetmagic.init.render.entity.monster.RenderBlazeTempestTornado;
import sweetmagic.init.render.entity.monster.RenderBullfight;
import sweetmagic.init.render.entity.monster.RenderCreeperCalamity;
import sweetmagic.init.render.entity.monster.RenderDwarfZombie;
import sweetmagic.init.render.entity.monster.RenderDwarfZombieMaster;
import sweetmagic.init.render.entity.monster.RenderElectricCube;
import sweetmagic.init.render.entity.monster.RenderEnderMage;
import sweetmagic.init.render.entity.monster.RenderEnderShadow;
import sweetmagic.init.render.entity.monster.RenderEnderShadowMirage;
import sweetmagic.init.render.entity.monster.RenderHolyAngel;
import sweetmagic.init.render.entity.monster.RenderIgnisKnight;
import sweetmagic.init.render.entity.monster.RenderPhantomWolf;
import sweetmagic.init.render.entity.monster.RenderPixeVex;
import sweetmagic.init.render.entity.monster.RenderQueenFrost;
import sweetmagic.init.render.entity.monster.RenderSilverLandRoad;
import sweetmagic.init.render.entity.monster.RenderSkullFalme;
import sweetmagic.init.render.entity.monster.RenderSkullFlameArcher;
import sweetmagic.init.render.entity.monster.RenderSkullFrost;
import sweetmagic.init.render.entity.monster.RenderSkullFrostRoyalGuard;
import sweetmagic.init.render.entity.monster.RenderWhiteButler;
import sweetmagic.init.render.entity.monster.RenderWindWitch;
import sweetmagic.init.render.entity.monster.RenderWindWitchMaster;
import sweetmagic.init.render.entity.projectile.RenderBossMagic;
import sweetmagic.init.render.entity.projectile.RenderCalamityBomb;
import sweetmagic.init.render.entity.projectile.RenderChair;
import sweetmagic.init.render.entity.projectile.RenderCherryPlant;
import sweetmagic.init.render.entity.projectile.RenderEnderBall;
import sweetmagic.init.render.entity.projectile.RenderEvilArrow;
import sweetmagic.init.render.entity.projectile.RenderFireShot;
import sweetmagic.init.render.entity.projectile.RenderFrostShot;
import sweetmagic.init.render.entity.projectile.RenderKnifeShot;
import sweetmagic.init.render.entity.projectile.RenderLightningBolt;
import sweetmagic.init.render.entity.projectile.RenderMagicShot;
import sweetmagic.init.render.entity.projectile.RenderMagicSquare;
import sweetmagic.init.render.entity.projectile.RenderMeteor;
import sweetmagic.init.render.entity.projectile.RenderPoisonMagic;
import sweetmagic.init.render.entity.projectile.RenderRockBlast;
import sweetmagic.init.render.entity.projectile.RenderSickleShot;
import sweetmagic.init.render.entity.projectile.RenderToxicCircle;
import sweetmagic.init.render.entity.projectile.RenderTripleTornado;
import sweetmagic.init.render.entity.projectile.RenderWitchCrystal;

public class RenderEntityInit {

	@SubscribeEvent
	public static void registerRenderEntity(RegisterRenderers event) {

		// ブロックえんちちーの描画設定
		register(event, TileInit.table, RenderMFTable::new);
		register(event, TileInit.tableAdavance, RenderMFTable::new);
		register(event, TileInit.tableMaster, RenderMFTable::new);
		register(event, TileInit.tank, RenderMFTank::new);
		register(event, TileInit.tankAdavance, RenderMFTank::new);
		register(event, TileInit.tankMaster, RenderMFTank::new);
		register(event, TileInit.tankCreative, RenderMFTank::new);
		register(event, TileInit.pot, RenderPot::new);
		register(event, TileInit.frypan, RenderFrypan::new);
		register(event, TileInit.bottle, RenderBottle::new);
		register(event, TileInit.pedal, RenderPedalCreate::new);
		register(event, TileInit.altarCreat, RenderAltarCreat::new);
		register(event, TileInit.altarCreatStar, RenderAltarCreatStar::new);
		register(event, TileInit.modenRack, RenderModenRack::new);
		register(event, TileInit.wallShelf, RenderModenRack::new);
		register(event, TileInit.wallRack, RenderModenRack::new);
		register(event, TileInit.wallPartition, RenderModenRack::new);
		register(event, TileInit.bottleRack, RenderModenRack::new);
		register(event, TileInit.ceilingShelf, RenderModenRack::new);
		register(event, TileInit.wandPedastal, RenderWandPedastal::new);
		register(event, TileInit.plate, RenderPlate::new);
		register(event, TileInit.showcase, RenderPlate::new);
		register(event, TileInit.juicemaker, RenderJuiceMaker::new);
		register(event, TileInit.enchantEduce, RenderEnchantEduce::new);
		register(event, TileInit.magiaWrite, RenderMagiaRewrite::new);
		register(event, TileInit.aetherRepair, RenderAetherRepair::new);
		register(event, TileInit.aetherReverse, RenderAetherReverse::new);
		register(event, TileInit.mfFisher, RenderMFFisher::new);
		register(event, TileInit.parallelInterfere, RenderParallelInterfere::new);
		register(event, TileInit.stardustWish, RenderStardustWish::new);
		register(event, TileInit.furnitureTable, RenderFurnitureTable::new);
		register(event, TileInit.magicianLecternFrost, RenderMagicianLectern::new);
		register(event, TileInit.magicianLecternLight, RenderMagicianLectern::new);
		register(event, TileInit.magicianLecternFire, RenderMagicianLectern::new);
		register(event, TileInit.magicianLecternWind, RenderMagicianLectern::new);
		register(event, TileInit.sturdust_crystal, RenderSturdustCrystal::new);
		register(event, TileInit.magiaAccelerator, RenderMagiaAccelerator::new);
		register(event, TileInit.accessoryProcessing, RenderAccessoryTable::new);
		register(event, TileInit.alstroemeriaAquarium, RenderAlstroemeriaAquarium::new);
		register(event, TileInit.aetheLamplight, RenderAetherLamplight::new);
		register(event, TileInit.smSpawner, RenderSMSpawner::new);
		register(event, TileInit.smSpawnerBoss, RenderSMSpawnerBoss::new);
		register(event, TileInit.transferGate, RenderTransferGate::new);
		register(event, TileInit.mirageGlass, RenderMirageGlass::new);
		register(event, TileInit.bossFigurine, RenderBossFigurine::new);
		register(event, TileInit.aquariumpot, RenderAquariumPot::new);
		register(event, TileInit.aetherPlanter, RenderAetherPlanter::new);
		register(event, TileInit.mfMinerAdvanced, RenderMFMinerAdvanced::new);
		register(event, TileInit.mfBottler, RenderMFBottler::new);

		// えんちちーのレンダー設定
		register(event, EntityInit.enderBall, RenderEnderBall::new);
		register(event, EntityInit.evilArrow, RenderEvilArrow::new);
		register(event, EntityInit.calamityBomb, RenderCalamityBomb::new);

		register(event, EntityInit.witchWolf, RenderWitchWolf::new);
		register(event, EntityInit.witchGolem, RenderWitchGolem::new);
		register(event, EntityInit.witchAllay, RenderWitchAllay::new);
		register(event, EntityInit.witchMaster, RenderWitchMaster::new);
		register(event, EntityInit.witchWindine, RenderWitchWindine::new);
		register(event, EntityInit.witchIfrit, RenderWitchIfrit::new);

		register(event, EntityInit.lightMagic, RenderMagicShot::new);
		register(event, EntityInit.fireMagic, RenderFireShot::new);
		register(event, EntityInit.digMagic, RenderMagicShot::new);
		register(event, EntityInit.normalMagic, RenderMagicShot::new);
		register(event, EntityInit.bulletMagic, RenderMagicShot::new);
		register(event, EntityInit.frostMagic, RenderFrostShot::new);
		register(event, EntityInit.cycloneMagic, RenderMagicShot::new);
		register(event, EntityInit.explosionMagic, RenderMagicShot::new);
		register(event, EntityInit.gravityMagic, RenderMagicShot::new);
		register(event, EntityInit.poisonMagic, RenderPoisonMagic::new);
		register(event, EntityInit.electricMagic, RenderLightningBolt::new);
		register(event, EntityInit.bubbleMagic, RenderMagicShot::new);
		register(event, EntityInit.bloodMagic, RenderMagicShot::new);
		register(event, EntityInit.rainMagic, RenderMagicShot::new);
		register(event, EntityInit.meteorMagic, RenderMeteor::new);
		register(event, EntityInit.rockBlastMagic, RenderRockBlast::new);
		register(event, EntityInit.frostLaser, RenderBossMagic::new);
		register(event, EntityInit.holyBusert, RenderBossMagic::new);
		register(event, EntityInit.ignisBlast, RenderBossMagic::new);
		register(event, EntityInit.windStorm, RenderBossMagic::new);
		register(event, EntityInit.cherryRain, RenderBossMagic::new);
		register(event, EntityInit.magicSquare, RenderMagicSquare::new);
		register(event, EntityInit.tripleTornado, RenderTripleTornado::new);
		register(event, EntityInit.windBlast, RenderMagicShot::new);
		register(event, EntityInit.cherryMagic, RenderMagicShot::new);
		register(event, EntityInit.toxicCircle, RenderToxicCircle::new);
		register(event, EntityInit.sickleShot, RenderSickleShot::new);
		register(event, EntityInit.knifeShot, RenderKnifeShot::new);

		register(event, EntityInit.chair, RenderChair::new);
		register(event, EntityInit.witchCrystal, RenderWitchCrystal::new);
		register(event, EntityInit.cherryPlant, RenderCherryPlant::new);

		register(event, EntityInit.skullFrost, RenderSkullFrost::new);
		register(event, EntityInit.blazeTempest, RenderBlazeTempest::new);
		register(event, EntityInit.archSpider, RenderArchSpider::new);
		register(event, EntityInit.electricCube, RenderElectricCube::new);
		register(event, EntityInit.creeperCalamity, RenderCreeperCalamity::new);
		register(event, EntityInit.enderMage, RenderEnderMage::new);
		register(event, EntityInit.skullFlame, RenderSkullFalme::new);
		register(event, EntityInit.dwarfZombie, RenderDwarfZombie::new);
		register(event, EntityInit.windWitch, RenderWindWitch::new);
		register(event, EntityInit.pixeVex, RenderPixeVex::new);
		register(event, EntityInit.phantomWolf, RenderPhantomWolf::new);

		register(event, EntityInit.skullFrostRoyalGuard, RenderSkullFrostRoyalGuard::new);
		register(event, EntityInit.skullFlameArcher, RenderSkullFlameArcher::new);
		register(event, EntityInit.blazeTempestTornado, RenderBlazeTempestTornado::new);
		register(event, EntityInit.enderShadow, RenderEnderShadow::new);
		register(event, EntityInit.enderShadowMirage, RenderEnderShadowMirage::new);
		register(event, EntityInit.dwarfZombieMaster, RenderDwarfZombieMaster::new);

		register(event, EntityInit.queenFrost, RenderQueenFrost::new);
		register(event, EntityInit.holyAngel, RenderHolyAngel::new);
		register(event, EntityInit.ignisKnight, RenderIgnisKnight::new);
		register(event, EntityInit.windWitchMaster, RenderWindWitchMaster::new);
		register(event, EntityInit.bullfight, RenderBullfight::new);
		register(event, EntityInit.ancientFairy, RenderAncientFairy::new);
		register(event, EntityInit.arlaune, RenderArlaune::new);
		register(event, EntityInit.silverLandRoad, RenderSilverLandRoad::new);
		register(event, EntityInit.whiteButler, RenderWhiteButler::new);
	}

	@SubscribeEvent
	public static void registerRenderLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
		register(event, SMWitchModel.LAYER, SMWitchModel::createBodyLayer);
		register(event, WindWitchModel.LAYER, WindWitchModel::createBodyLayer);
		register(event, QuenModel.LAYER, QuenModel::createBodyLayer);
		register(event, SMHolyModel.LAYER, SMHolyModel::createBodyLayer);
		register(event, SMRobeModel.LAYER, SMRobeModel::createBodyLayer);
		register(event, PorchModel.LAYER, PorchModel::createBodyLayer);
		register(event, IgnisModel.LAYER, IgnisModel::createBodyLayer);
		register(event, TempestModel.LAYER, TempestModel::createBodyLayer);
		register(event, BullfightModel.LAYER, BullfightModel::createBodyLayer);
		register(event, AncientFairyModel.LAYER, AncientFairyModel::createBodyLayer);
		register(event, ArlauneModel.LAYER, ArlauneModel::createBodyLayer);
		register(event, WitchWolfModel.LAYER, WitchWolfModel::createBodyLayer);
		register(event, WitchGolemModel.LAYER, WitchGolemModel::createBodyLayer);
		register(event, WitchAllayModel.LAYER, WitchAllayModel::createBodyLayer);
		register(event, WhiteButlerModel.LAYER, WhiteButlerModel::createBodyLayer);
	}

    public static void register(EntityRenderersEvent.RegisterLayerDefinitions event, ModelLayerLocation layer, Supplier<LayerDefinition> sup) {
		event.registerLayerDefinition(layer, sup);
    }

	public static <T extends LivingEntity, M extends EntityModel<T>> void addLayer(LivingEntityRenderer<T, M> render) {
		render.addLayer(new WandRenderLayer<>(render));
		render.addLayer(new FrostEffectRender<>(render));
	}

	public static <T extends Entity> void register(RegisterRenderers event, EntityType<? extends T> entityType, EntityRendererProvider<T> render) {
		event.registerEntityRenderer(entityType, render);
	}

	public static <T extends BlockEntity> void register(RegisterRenderers event, BlockEntityType<? extends T> entityType, BlockEntityRendererProvider<T> render) {
		event.registerBlockEntityRenderer(entityType, render);
	}
}