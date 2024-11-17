package sweetmagic.init.render.entity.animal;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.entity.animal.WitchWolf;
import sweetmagic.init.render.entity.model.WitchWolfModel;

public class RenderWitchWolf extends MobRenderer<WitchWolf, WitchWolfModel<WitchWolf>> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/entity/witchwalf.png");

	public RenderWitchWolf(EntityRendererProvider.Context con) {
		super(con, new WitchWolfModel<>(con.bakeLayer(WitchWolfModel.LAYER)), 0.5F);
	}

	public void render(WitchWolf entity, float yaw, float part, PoseStack pose, MultiBufferSource buf, int light) {

		if (entity.isWet()) {
			float f = entity.getWetShade(part);
			this.model.setColor(f, f, f);
		}

		super.render(entity, yaw, part, pose, buf, light);

		if (entity.isWet()) {
			this.model.setColor(1F, 1F, 1F);
		}
	}

	public ResourceLocation getTextureLocation(WitchWolf entity) {
		return TEX;
	}
}
