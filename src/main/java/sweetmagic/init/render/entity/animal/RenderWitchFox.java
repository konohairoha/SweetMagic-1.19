package sweetmagic.init.render.entity.animal;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.entity.animal.WitchFox;
import sweetmagic.init.render.entity.layer.MagicCycleLayer;
import sweetmagic.init.render.entity.model.WitchFoxModel;

public class RenderWitchFox<T extends WitchFox> extends MobRenderer<T, WitchFoxModel<T>> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/entity/witchfox.png");

	public RenderWitchFox(EntityRendererProvider.Context con) {
		super(con, new WitchFoxModel<>(con.bakeLayer(WitchFoxModel.LAYER)), 0.5F);
		this.addLayer(new MagicCycleLayer<T, WitchFoxModel<T>>(this, con));
	}

	public ResourceLocation getTextureLocation(T entity) {
		return TEX;
	}
}
