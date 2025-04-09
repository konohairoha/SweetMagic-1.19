package sweetmagic.handler;

import java.util.Objects;

import com.mojang.serialization.Codec;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;
import sweetmagic.SweetMagicCore;
import sweetmagic.config.SMConfig;
import sweetmagic.event.AlstroemeriaClickEvent;
import sweetmagic.event.BlockBreakEvent;
import sweetmagic.event.CompasRenderEvent;
import sweetmagic.event.EntitiySpawnEvent;
import sweetmagic.event.EquipmentChangeEvent;
import sweetmagic.event.KeyPressEvent;
import sweetmagic.event.LivingDethEvent;
import sweetmagic.event.PotionEvent;
import sweetmagic.event.SMLivingDamageEvent;
import sweetmagic.event.SMLivingTickEvent;
import sweetmagic.event.SMPlayerEvent;
import sweetmagic.event.VillagerEvent;
import sweetmagic.event.WandRenderEvent;
import sweetmagic.event.XPPickupEvent;
import sweetmagic.init.BiomeInit;
import sweetmagic.init.BlockInit;
import sweetmagic.init.CapabilityInit;
import sweetmagic.init.EnchantInit;
import sweetmagic.init.EntityInit;
import sweetmagic.init.FeatuerInit;
import sweetmagic.init.ItemInit;
import sweetmagic.init.MenuInit;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.PotionInit;
import sweetmagic.init.RecipeSerializerInit;
import sweetmagic.init.RecipeTypeInit;
import sweetmagic.init.RenderEntityInit;
import sweetmagic.init.SoundInit;
import sweetmagic.init.StructureInit;
import sweetmagic.init.TileInit;
import sweetmagic.init.VillageInit;
import sweetmagic.worldgen.entity.EntityModifier;
import sweetmagic.worldgen.flower.FlowerGurdenModifier;
import sweetmagic.worldgen.flower.FlowerModifier;
import sweetmagic.worldgen.flower.FruitModifier;
import sweetmagic.worldgen.flower.MoonModifier;
import sweetmagic.worldgen.flower.PrismModifier;
import sweetmagic.worldgen.flower.SMFlowerGen;
import sweetmagic.worldgen.flower.SugarModifier;
import sweetmagic.worldgen.loot.LootTableModifier;
import sweetmagic.worldgen.ore.CSOreModifier;
import sweetmagic.worldgen.ore.OreModifier;
import sweetmagic.worldgen.ore.SMOreGen;

public class RegisterHandler {

	public static final RegisterHandler INSTANCE = new RegisterHandler();
	public static final DeferredRegister<Codec<? extends BiomeModifier>> BIOME_REGISTER = DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, SweetMagicCore.MODID);
	private static final DeferredRegister<BiomeModifier> BIOME_MODIFIER = DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIERS, SweetMagicCore.MODID);
	public static RegistryObject<Codec<EntityModifier>> ENHTITY_REGISTER = BIOME_REGISTER.register("smmob_spawn", () -> Codec.unit(EntityModifier.INSTANCE));
	public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIER = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, SweetMagicCore.MODID);

	// 初期化
	public void registerInit (IEventBus event) {

		event.addListener(BlockInit::registerBlock);
		event.addListener(ItemInit::registerItem);
		event.addListener(TileInit::registerTile);
		event.addListener(EntityInit::registerEntity);
		event.addListener(EnchantInit::registerEnchant);
		event.addListener(EntityInit::registerAttribute);
		event.addListener(PotionInit::registerPotion);
		ParticleInit.PARTICLE_TYPES.register(event);
		RecipeTypeInit.REGISTRY.register(event);
		RecipeSerializerInit.REGISTRY.register(event);
		FeatuerInit.REGISTER.register(event);
		VillageInit.PRO_TYPE.register(event);
		VillageInit.POI_TYPE.register(event);
		StructureInit.STRUCTURE_TYPE.register(event);
		StructureInit.init();
		BIOME_REGISTER.register(event);
		LOOT_MODIFIER.register(event);
		LOOT_MODIFIER.register("add_loot_table", LootTableModifier.CODEC);

		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			event.addListener(RenderEntityInit::registerRenderEntity);
			event.addListener(RenderEntityInit::registerRenderLayer);
			event.addListener(ParticleInit::registerParticle);
			MinecraftForge.EVENT_BUS.addListener(WandRenderEvent::onWandRenderEvent);
			MinecraftForge.EVENT_BUS.addListener(WandRenderEvent::onFOVEvent);
			MinecraftForge.EVENT_BUS.addListener(CompasRenderEvent::onWandRenderEvent);
			MenuInit.registerGUI();
		});

		event.addListener(SoundInit::registerSound);
		MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, CapabilityInit::attachEntityCapability);
		event.addListener(CapabilityInit::registerCapabilities);
	}

	// イベント登録
	public void registerEvent (IEventBus event) {

		IEventBus bus = MinecraftForge.EVENT_BUS;
		bus.register(this);
		bus.addListener(LivingDethEvent::onEvent);
		bus.addListener(LivingDethEvent::onPlayerRespawn);
		bus.addListener(LivingDethEvent::onLivingDeathEvent);
		bus.addListener(LivingDethEvent::onPlayerLogout);
		bus.addListener(LivingDethEvent::cloneEvent);
		bus.addListener(LivingDethEvent::loginEvent);
		bus.addListener(LivingDethEvent::dropEvent);
		bus.addListener(LivingDethEvent::dropExpEvent);
		bus.addListener(XPPickupEvent::onBulletRenderEvent);
		bus.addListener(SMLivingDamageEvent::onHurt);
		bus.addListener(EntitiySpawnEvent::onEntityCheckSpawn);
		bus.addListener(EntitiySpawnEvent::onEntityCheckSpecialSpawn);
		bus.addListener(AlstroemeriaClickEvent::rightClickBlock);
		bus.addListener(BlockBreakEvent::onBlockBreakEvent);
		bus.addListener(BlockBreakEvent::onBlockPlaceEvent);
		bus.addListener(SMLivingTickEvent::onTickEvent);
		bus.addListener(SMLivingTickEvent::mobGriefingEvent);
		bus.addListener(EquipmentChangeEvent::changeEvent);
		bus.addListener(PotionEvent::healEvent);
		bus.addListener(PotionEvent::potionAddEvent);
		bus.addListener(PotionEvent::potionRemoveEvent);
		bus.addListener(PotionEvent::knockBackEvent);
		bus.addListener(PotionEvent::teleportEvent);
		bus.addListener(VillagerEvent::villagerTrade);
		bus.addListener(SMPlayerEvent::sleepEvent);
		bus.addListener(SMPlayerEvent::onPlayerSetSpawn);
		bus.addListener(SMPlayerEvent::onSleepTimeCheck);
		bus.addListener(SMPlayerEvent::onPlayerSleep);
		bus.addListener(SMPlayerEvent::onPickup);
	}

	// コンフィグ登録
	public void registerConfig () {
		SMConfig.loadConfig(SMConfig.SPEC, FMLPaths.CONFIGDIR.get().resolve(SweetMagicCore.MODID + "-server.toml"));
	}

	// 草ドロップ登録
	public void registerGrassDrop(IEventBus event) {
		GrassDropHandler.REGISTER.register(event);
		BIOME_MODIFIER.register(event);
		BIOME_REGISTER.register("smflowers_moon", MoonModifier::makeCodec);
		BIOME_REGISTER.register("smflowers_sugar", SugarModifier::makeCodec);
		BIOME_REGISTER.register("smflower_flower", FlowerModifier::makeCodec);
		BIOME_REGISTER.register("smflower_prism", PrismModifier::makeCodec);
		BIOME_REGISTER.register("smflower_fruit", FruitModifier::makeCodec);
		BIOME_REGISTER.register("smflower_garden", FlowerGurdenModifier::makeCodec);
		BIOME_REGISTER.register("smores", OreModifier::makeCodec);
		BIOME_REGISTER.register("cssmores", CSOreModifier::makeCodec);
	}

	// RegisterEventの実行
	public static void onRegisterEvent(RegisterEvent event) {

		ResourceKey<? extends Registry<?>> key = event.getRegistryKey();

		if (key.equals(ForgeRegistries.Keys.MENU_TYPES)) {
			MenuInit.registerMenu(event);
		}

		if (key.equals(ForgeRegistries.Keys.ITEMS)) {
			SMOreGen.register(BIOME_MODIFIER);
			SMFlowerGen.register(BIOME_MODIFIER);
		}

		if (key.equals(ForgeRegistries.Keys.BIOMES)) {
			BiomeInit.registerBiome(Objects.requireNonNull(event.getForgeRegistry()));
		}
	}

	@SubscribeEvent
	public static void registerKeybind(RegisterKeyMappingsEvent event) {
		KeyPressEvent.registerKeybind(event);
	}
}
