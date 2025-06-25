package sweetmagic.init.render.entity.animal;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.entity.animal.WitchIfrit;
import sweetmagic.init.render.entity.layer.MagicCycleLayer;
import sweetmagic.init.render.entity.layer.WitchWandLayer;
import sweetmagic.init.render.entity.model.SMWitchModel;

public class RenderWitchIfrit<T extends WitchIfrit> extends MobRenderer<T, SMWitchModel<T>> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/entity/witchifrit.png");

	public RenderWitchIfrit(EntityRendererProvider.Context con) {
		super(con, new SMWitchModel<T>(con.bakeLayer(SMWitchModel.LAYER)), 0.5F);
		this.addLayer(new WitchWandLayer<T, SMWitchModel<T>>(this, con));
		this.addLayer(new MagicCycleLayer<T, SMWitchModel<T>>(this, con));
	}

	public ResourceLocation getTextureLocation(T entity) {
		return TEX;
	}
}
