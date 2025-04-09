package sweetmagic.init.render.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.entity.projectile.PoisonMagicShot;

public class RenderPoisonMagic<T extends PoisonMagicShot> extends EntityRenderer<T> {

	private int tickTime = 0;
	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/block/empty.png");
	private final EntityRenderDispatcher render;

	public RenderPoisonMagic(EntityRendererProvider.Context con) {
		super(con);
		this.render = con.getEntityRenderDispatcher();
	}

	@Override
	public void render(T entity, float yaw, float part, PoseStack pose, MultiBufferSource buf, int light) {
		if (!entity.getWolf()) { return; }

		LivingEntity wolf = entity.getRenderEntity();
		if (this.tickTime++ % 3 == 0) {
			entity.tickCount++;
			this.tickTime = 1;
		}

		pose.pushPose();
		pose.mulPose(Vector3f.YP.rotationDegrees(entity.getVisualRotationYInDegrees()));
		this.render.render(wolf, 0D, 0D, 0D, 0F, part, pose, buf, light);
		pose.popPose();
	}

	@Override
	public ResourceLocation getTextureLocation(T entity) {
		return TEX;
	}
}
