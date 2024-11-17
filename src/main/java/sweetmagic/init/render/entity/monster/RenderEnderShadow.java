package sweetmagic.init.render.entity.monster;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.EndermanModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.PotionInit;
import sweetmagic.init.entity.monster.EnderShadow;
import sweetmagic.init.render.entity.layer.EnderMageHandLayer;

public class RenderEnderShadow extends MobRenderer<EnderShadow, EndermanModel<EnderShadow>> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/entity/endershadow.png");

	public RenderEnderShadow(EntityRendererProvider.Context con) {
		super(con, new EndermanModel<>(con.bakeLayer(ModelLayers.ENDERMAN)), 0.5F);
		this.addLayer(new EnderMageHandLayer<>(this, con));
	}

	protected void scale(EnderShadow entity, PoseStack pose, float par1) {
		float size = this.getSize(entity, 1.25F);
		pose.scale(size, size, size);
	}

	public float getSize (Mob mob, float size) {
		return mob.hasEffect(PotionInit.leader_flag) ? size + 0.35F : size;
	}

	public ResourceLocation getTextureLocation(EnderShadow entity) {
		return TEX;
	}
}