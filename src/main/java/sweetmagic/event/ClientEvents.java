package sweetmagic.event;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.ItemInit;
import sweetmagic.init.item.sm.JapaneseUmbrella;

@Mod.EventBusSubscriber(modid = SweetMagicCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {

	public static final ResourceLocation ACTIVE_OVERRIDE = SweetMagicCore.getSRC("active");

	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(() ->
			addPropertyOverrides(ACTIVE_OVERRIDE, (stack, world, entity, seed) -> stack.getOrCreateTag().getBoolean(JapaneseUmbrella.ACTIVE) ? 1F : 0F, ItemInit.japanese_umbrella)
		);
	}

	public static void addPropertyOverrides(ResourceLocation override, ItemPropertyFunction pro, ItemLike... items) {
		for (ItemLike item : items) {
			ItemProperties.register(item.asItem(), override, pro);
		}
	}
}
