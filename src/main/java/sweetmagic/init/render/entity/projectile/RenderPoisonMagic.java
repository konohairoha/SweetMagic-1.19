package sweetmagic.init.render.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.LivingEntity;
import sweetmagic.init.entity.projectile.PoisonMagicShot;

public class RenderPoisonMagic<T extends PoisonMagicShot> extends RenderMagicBase<T> {

	private int tickTime = 0;

	public RenderPoisonMagic(EntityRendererProvider.Context con) {
		super(con);
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
		this.eRender.render(wolf, 0D, 0D, 0D, 0F, part, pose, buf, light);
		pose.popPose();
	}
}
