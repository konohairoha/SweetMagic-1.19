package sweetmagic.init.render.entity.animal;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.entity.animal.WitchCat;
import sweetmagic.init.render.entity.layer.MagicCycleLayer;
import sweetmagic.init.render.entity.layer.WitchCatLayer;
import sweetmagic.init.render.entity.model.WitchCatModel;

public class RenderWitchCat<T extends WitchCat> extends MobRenderer<T, WitchCatModel<T>> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/entity/witchcat.png");

	public RenderWitchCat(EntityRendererProvider.Context con) {
		super(con, new WitchCatModel<>(con.bakeLayer(WitchCatModel.LAYER)), 0.5F);
		this.addLayer(new MagicCycleLayer<T, WitchCatModel<T>>(this, con));
		this.addLayer(new WitchCatLayer<T, WitchCatModel<T>>(this, con));
	}

	public ResourceLocation getTextureLocation(T entity) {
		return TEX;
	}
}
