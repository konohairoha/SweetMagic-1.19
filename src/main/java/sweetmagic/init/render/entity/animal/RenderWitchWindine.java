package sweetmagic.init.render.entity.animal;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.entity.animal.WitchWindine;
import sweetmagic.init.render.entity.layer.WitchWandLayer;
import sweetmagic.init.render.entity.model.SMWitchModel;

public class RenderWitchWindine extends MobRenderer<WitchWindine, SMWitchModel<WitchWindine>> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/entity/witchwindine.png");

	public RenderWitchWindine(EntityRendererProvider.Context con) {
		super(con, new SMWitchModel<WitchWindine>(con.bakeLayer(SMWitchModel.LAYER)), 0.5F);
		this.addLayer(new WitchWandLayer<WitchWindine, SMWitchModel<WitchWindine>>(this, con));
	}

	protected void scale(WitchWindine entity, PoseStack pose, float par1) {
		pose.scale(1F, 1F, 1F);
	}

	public ResourceLocation getTextureLocation(WitchWindine entity) {
		return TEX;
	}
}
