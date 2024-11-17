package sweetmagic.init.render.entity.monster;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.entity.monster.boss.SilverLandRoad;
import sweetmagic.init.render.entity.model.WitchWolfModel;

public class RenderSilverLandRoad extends MobRenderer<SilverLandRoad, WitchWolfModel<SilverLandRoad>> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/entity/silver_land_road.png");

	public RenderSilverLandRoad(EntityRendererProvider.Context con) {
		super(con, new WitchWolfModel<>(con.bakeLayer(WitchWolfModel.LAYER)), 0.5F);
	}
	protected void scale(SilverLandRoad entity, PoseStack pose, float par1) {
		pose.scale(2F, 2F, 2F);
	}

	public ResourceLocation getTextureLocation(SilverLandRoad entity) {
		return TEX;
	}
}
