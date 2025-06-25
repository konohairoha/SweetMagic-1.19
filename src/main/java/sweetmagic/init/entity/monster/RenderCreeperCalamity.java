package sweetmagic.init.render.entity.monster;

import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.entity.monster.CreeperCalamity;

public class RenderCreeperCalamity<T extends CreeperCalamity> extends MobRenderer<T, CreeperModel<T>> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/entity/creepercal.png");

	public RenderCreeperCalamity(EntityRendererProvider.Context cont) {
		super(cont, new CreeperModel<>(cont.bakeLayer(ModelLayers.CREEPER)), 0.5F);
	}

	public ResourceLocation getTextureLocation(T entity) {
		return TEX;
	}
}
