package sweetmagic.init.render.entity.projectile;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import sweetmagic.init.entity.projectile.EnderBall;

public class RenderEnderBall extends RenderBase<EnderBall> {

	private static final ResourceLocation TEX = new ResourceLocation("textures/item/ender_pearl.png");
	private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(TEX);

	public RenderEnderBall(EntityRendererProvider.Context con) {
		super(con);
	}

	public ResourceLocation getTextureLocation(EnderBall entity) {
		return TEX;
	}

	@Override
	public RenderType getRenderType() {
		return RENDER_TYPE;
	}
}
