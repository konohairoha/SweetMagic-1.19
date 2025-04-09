package sweetmagic.init.render.entity.animal;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.entity.animal.WitchWolf;
import sweetmagic.init.render.entity.layer.MagicCycleLayer;
import sweetmagic.init.render.entity.model.WitchWolfModel;

public class RenderWitchWolf<T extends WitchWolf> extends MobRenderer<T, WitchWolfModel<T>> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/entity/witchwalf.png");
	private int tick = 0;

	public RenderWitchWolf(EntityRendererProvider.Context con) {
		super(con, new WitchWolfModel<>(con.bakeLayer(WitchWolfModel.LAYER)), 0.5F);
		this.addLayer(new MagicCycleLayer<T, WitchWolfModel<T>>(this, con));
	}

	public void render(T entity, float yaw, float part, PoseStack pose, MultiBufferSource buf, int light) {

		if (entity.isWet()) {
			float f = entity.getWetShade(part);
			this.model.setColor(f, f, f);
		}

		if (entity.getAttackTick() > 0) {
			pose.mulPose(Vector3f.YP.rotationDegrees(this.tick++ * 12));
		}

		else {
			this.tick = 0;
		}

		super.render(entity, yaw, part, pose, buf, light);

		if (entity.isWet()) {
			this.model.setColor(1F, 1F, 1F);
		}
	}

	public ResourceLocation getTextureLocation(T entity) {
		return TEX;
	}
}
