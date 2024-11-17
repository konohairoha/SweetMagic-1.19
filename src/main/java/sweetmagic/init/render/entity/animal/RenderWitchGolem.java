package sweetmagic.init.render.entity.animal;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.entity.animal.WitchGolem;
import sweetmagic.init.render.entity.model.WitchGolemModel;

public class RenderWitchGolem extends MobRenderer<WitchGolem, WitchGolemModel<WitchGolem>> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/entity/witchgolem.png");

	public RenderWitchGolem(EntityRendererProvider.Context con) {
		super(con, new WitchGolemModel<>(con.bakeLayer(WitchGolemModel.LAYER)), 0.7F);
	}

	public ResourceLocation getTextureLocation(WitchGolem entity) {
		return TEX;
	}

	protected void setupRotations(WitchGolem entity, PoseStack pose, float par1, float par2, float par3) {
		super.setupRotations(entity, pose, par1, par2, par3);
		if (((double) entity.animationSpeed < 0.01D)) { return; }

		float f1 = entity.animationPosition - entity.animationSpeed * (1F - par3) + 6F;
		float f2 = (Math.abs(f1 % 13F - 6.5F) - 3.25F) / 3.25F;
		pose.mulPose(Vector3f.ZP.rotationDegrees(6.5F * f2));
	}
}