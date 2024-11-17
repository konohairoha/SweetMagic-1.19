package sweetmagic.init.render.entity.animal;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.entity.animal.WitchAllay;
import sweetmagic.init.render.entity.layer.WitchAllayLayer;
import sweetmagic.init.render.entity.model.WitchAllayModel;

public class RenderWitchAllay extends MobRenderer<WitchAllay, WitchAllayModel<WitchAllay>> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/entity/witchallay.png");

	public RenderWitchAllay(EntityRendererProvider.Context con) {
		super(con, new WitchAllayModel<>(con.bakeLayer(WitchAllayModel.LAYER)), 0.4F);
		this.addLayer(new WitchAllayLayer<WitchAllay, WitchAllayModel<WitchAllay>>(this, con));
	}

	public ResourceLocation getTextureLocation(WitchAllay entity) {
		return TEX;
	}

	protected int getBlockLightLevel(WitchAllay entity, BlockPos pos) {
		return 15;
	}
}
