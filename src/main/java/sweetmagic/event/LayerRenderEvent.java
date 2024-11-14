package sweetmagic.event;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.RenderEntityInit;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD, modid = SweetMagicCore.MODID)
public class LayerRenderEvent {

	private static Field rendererField;

	@SubscribeEvent
	public static void attachRenderLayers(EntityRenderersEvent.AddLayers event) {

		if (rendererField == null) {
			try {
				rendererField = EntityRenderersEvent.AddLayers.class.getDeclaredField("renderers");
				rendererField.setAccessible(true);
			}

			catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
		}

		if (rendererField != null) {

			event.getSkins().forEach(render -> {
				LivingEntityRenderer<Player, EntityModel<Player>> skin = event.getSkin(render);
				RenderEntityInit.addLayer(Objects.requireNonNull(skin));
			});

			try {
				((Map<EntityType<?>, EntityRenderer<?>>) rendererField.get(event)).values().stream().filter(LivingEntityRenderer.class::isInstance).map(LivingEntityRenderer.class::cast).forEach(RenderEntityInit::addLayer);
			}

			catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
}
