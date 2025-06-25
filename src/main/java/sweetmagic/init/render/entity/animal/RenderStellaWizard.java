package sweetmagic.init.render.entity.animal;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.entity.animal.StellaWizard;
import sweetmagic.init.render.entity.layer.MagicCycleLayer;
import sweetmagic.init.render.entity.layer.StellaLayer;
import sweetmagic.init.render.entity.model.StellaWizardModel;

public class RenderStellaWizard<T extends StellaWizard> extends MobRenderer<T, StellaWizardModel<T>> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/entity/stella_wizard.png");

	public RenderStellaWizard(EntityRendererProvider.Context con) {
		super(con, new StellaWizardModel<T>(con.bakeLayer(StellaWizardModel.LAYER)), 0.5F);
		this.addLayer(new StellaLayer<T, StellaWizardModel<T>>(this, con));
		this.addLayer(new MagicCycleLayer<T, StellaWizardModel<T>>(this, con));
	}

	public ResourceLocation getTextureLocation(T entity) {
		return TEX;
	}
}
