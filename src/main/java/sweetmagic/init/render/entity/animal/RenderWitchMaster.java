package sweetmagic.init.render.entity.animal;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.entity.animal.WitchMaster;
import sweetmagic.init.render.entity.layer.WitchWandLayer;
import sweetmagic.init.render.entity.model.SMWitchModel;

public class RenderWitchMaster extends MobRenderer<WitchMaster, SMWitchModel<WitchMaster>> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/entity/witchmaster.png");

	public RenderWitchMaster(EntityRendererProvider.Context con) {
		super(con, new SMWitchModel<WitchMaster>(con.bakeLayer(SMWitchModel.LAYER)), 0.5F);
		this.addLayer(new WitchWandLayer<WitchMaster, SMWitchModel<WitchMaster>>(this, con));
	}

	protected void scale(WitchMaster entity, PoseStack pose, float par1) {
		pose.scale(0.67F, 0.67F, 0.67F);
	}

	public ResourceLocation getTextureLocation(WitchMaster entity) {
		return TEX;
	}
}
