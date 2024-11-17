package sweetmagic.init.render.entity.projectile;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.entity.projectile.EvilArrow;

public class RenderEvilArrow extends RenderBase<EvilArrow> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/item/dangerous_fruits.png");
	private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(TEX);

	public RenderEvilArrow(EntityRendererProvider.Context con) {
		super(con);
	}

	public ResourceLocation getTextureLocation(EvilArrow entity) {
		return TEX;
	}

	@Override
	public RenderType getRenderType() {
		return RENDER_TYPE;
	}
}
