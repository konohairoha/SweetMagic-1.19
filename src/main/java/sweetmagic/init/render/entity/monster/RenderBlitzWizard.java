package sweetmagic.init.render.entity.monster;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.entity.monster.BlitzWizard;
import sweetmagic.init.render.entity.layer.BlitzWizardLayer;
import sweetmagic.init.render.entity.model.SMWitchModel;

public class RenderBlitzWizard<T extends BlitzWizard> extends MobRenderer<T, SMWitchModel<T>> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/entity/blitz_wizard.png");

	public RenderBlitzWizard(EntityRendererProvider.Context con) {
		super(con, new SMWitchModel<>(con.bakeLayer(SMWitchModel.LAYER)), 0.5F);
		this.addLayer(new BlitzWizardLayer<>(this, con));
	}

	protected void scale(T entity, PoseStack pose, float par1) {
		pose.scale(1.15F, 1.15F, 1.15F);
	}

	public ResourceLocation getTextureLocation(T entity) {
		return TEX;
	}
}
