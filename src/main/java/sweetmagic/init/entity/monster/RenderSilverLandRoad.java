package sweetmagic.init.render.entity.monster;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.entity.monster.boss.SilverLandRoad;
import sweetmagic.init.render.entity.model.WitchWolfModel;

public class RenderSilverLandRoad<T extends SilverLandRoad> extends MobRenderer<T, WitchWolfModel<T>> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/entity/silver_land_road.png");
	private int tick = 0;

	public RenderSilverLandRoad(EntityRendererProvider.Context con) {
		super(con, new WitchWolfModel<>(con.bakeLayer(WitchWolfModel.LAYER)), 0.5F);
	}

	public void render(T entity, float yaw, float part, PoseStack pose, MultiBufferSource buf, int light) {

		if (entity.getAttackTick() > 0) {
			pose.mulPose(Vector3f.YP.rotationDegrees(this.tick++ * 12));
		}

		else {
			this.tick = 0;
		}

		super.render(entity, yaw, part, pose, buf, light);
	}

	protected void scale(T entity, PoseStack pose, float par1) {
		pose.scale(2F, 2F, 2F);
	}

	public ResourceLocation getTextureLocation(T entity) {
		return TEX;
	}
}
