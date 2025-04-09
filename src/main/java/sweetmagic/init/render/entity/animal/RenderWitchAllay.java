package sweetmagic.init.render.entity.animal;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.entity.animal.WitchAllay;
import sweetmagic.init.render.entity.layer.MagicCycleLayer;
import sweetmagic.init.render.entity.layer.WitchAllayLayer;
import sweetmagic.init.render.entity.model.WitchAllayModel;

public class RenderWitchAllay<T extends WitchAllay> extends MobRenderer<T, WitchAllayModel<T>> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/entity/witchallay.png");

	public RenderWitchAllay(EntityRendererProvider.Context con) {
		super(con, new WitchAllayModel<>(con.bakeLayer(WitchAllayModel.LAYER)), 0.4F);
		this.addLayer(new WitchAllayLayer<T, WitchAllayModel<T>>(this, con));
		this.addLayer(new MagicCycleLayer<T, WitchAllayModel<T>>(this, con));
	}

	public ResourceLocation getTextureLocation(T entity) {
		return TEX;
	}

	protected int getBlockLightLevel(T entity, BlockPos pos) {
		return 15;
	}
}
