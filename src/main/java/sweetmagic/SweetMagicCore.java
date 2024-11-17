package sweetmagic;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegisterEvent;
import sweetmagic.api.SweetMagicAPI;
import sweetmagic.api.magiaflux.MFItemList;
import sweetmagic.api.magiaflux.MagiaFluxInfo;
import sweetmagic.handler.ComposterHandler;
import sweetmagic.handler.PacketHandler;
import sweetmagic.handler.RegisterHandler;
import sweetmagic.init.BlockInit;
import sweetmagic.init.DimentionInit;
import sweetmagic.init.EntityInit;
import sweetmagic.init.LootInit;
import sweetmagic.init.tile.menu.SMBookMenu;
import sweetmagic.tab.SMFoodTab;
import sweetmagic.tab.SMMagicTab;
import sweetmagic.tab.SMTab;
import sweetmagic.worldgen.biome.SMBiomeRegion;

@Mod(SweetMagicCore.MODID)
public class SweetMagicCore {

    public static boolean terrablenderLoaded = false;
    public static boolean mousetweaksLoaded = false;

	public static final String MODID = "sweetmagic";
	public static final CreativeModeTab smTab = new SMTab("sweetmagic_tab");
	public static final CreativeModeTab smMagicTab = new SMMagicTab("sweetmagic_magic_tab");
	public static final CreativeModeTab smFoodTab = new SMFoodTab("sweetmagic_food_tab");

	public SweetMagicCore() {

		IEventBus event = FMLJavaModLoadingContext.get().getModEventBus();
        terrablenderLoaded = ModList.get().isLoaded("terrablender");
        mousetweaksLoaded = ModList.get().isLoaded("mousetweaks");

		// commonSetup呼び出し
		event.addListener(this::commonSetup);
		event.addListener(this::enqueueEvent);

		RegisterHandler handler = RegisterHandler.INSTANCE;	// RegisterHandlerの初期化
		handler.registerInit(event);						// 初期化
		handler.registerConfig();							// コンフィグ読み込み
		handler.registerEvent(event);						// イベント登録
		event.register(this);								// eventを登録
		MFItemList.INSTANCE.addPluginList();				// MFアイテムのプラグインの追加
		handler.registerGrassDrop(event);					// 草ドロップ登録
		LootInit.init();									// ルートテーブル登録
	}

	private void commonSetup(final FMLCommonSetupEvent event) {

		event.enqueueWork(() -> {
			PacketHandler.register();
			ComposterHandler.registerCompostables();
			BlockInit.registerPots();
		});

		if (terrablenderLoaded) {
			SMBiomeRegion.register();
        }

		DimentionInit.init();

		//登録インスタンス
		MagiaFluxInfo info = new MagiaFluxInfo();
		SweetMagicAPI.getMFPluginList().forEach(mf -> mf.setMF(info));
	}

	private void enqueueEvent(final InterModEnqueueEvent event) {
		InterModComms.sendTo("craftingtweaks", "RegisterProvider", () -> {
			CompoundTag tags = new CompoundTag();
			tags.putString("ContainerClass", SMBookMenu.class.getName());
			tags.putString("AlignToGrid", "left");
			return tags;
		});
	}

	@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	public static class ClientModEvents {

		@SubscribeEvent
		public static void registerKeybindings(RegisterKeyMappingsEvent event) {
			RegisterHandler.registerKeybind(event);
		}
	}

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {

        @SubscribeEvent
        public static void onRegister(RegisterEvent event) {
        	RegisterHandler.onRegisterEvent(event);
        }
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegisterSpawn {

    	@SubscribeEvent
    	public static void registerSpawn(SpawnPlacementRegisterEvent event) {
    		EntityInit.registerSpawn(event);
    	}
    }

	public static ResourceLocation getSRC (String name) {
		return new ResourceLocation(SweetMagicCore.MODID, name);
	}

	public static <T> DeferredRegister<T> getDef(IForgeRegistry<T> reg) {
		return DeferredRegister.create(reg, SweetMagicCore.MODID);
	}
}
