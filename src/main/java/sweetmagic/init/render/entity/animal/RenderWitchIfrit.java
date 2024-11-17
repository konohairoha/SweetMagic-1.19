package sweetmagic.init.render.entity.animal;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.entity.animal.WitchIfrit;
import sweetmagic.init.render.entity.layer.WitchWandLayer;
import sweetmagic.init.render.entity.model.SMWitchModel;

public class RenderWitchIfrit extends MobRenderer<WitchIfrit, SMWitchModel<WitchIfrit>> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/entity/witchifrit.png");

	public RenderWitchIfrit(EntityRendererProvider.Context con) {
		super(con, new SMWitchModel<WitchIfrit>(con.bakeLayer(SMWitchModel.LAYER)), 0.5F);
		this.addLayer(new WitchWandLayer<WitchIfrit, SMWitchModel<WitchIfrit>>(this, con));
	}

	protected void scale(WitchIfrit entity, PoseStack pose, float par1) {
		pose.scale(1F, 1F, 1F);
	}

	public ResourceLocation getTextureLocation(WitchIfrit entity) {
		return TEX;
	}
}
