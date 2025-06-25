package sweetmagic.init.render.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import sweetmagic.init.entity.projectile.RockBlastMagicShot;

public class RenderRockBlast<T extends RockBlastMagicShot> extends RenderMagicBase<T> {

	public RenderRockBlast(EntityRendererProvider.Context con) {
		super(con);
	}

	@Override
	public void render(T entity, float yaw, float part, PoseStack pose, MultiBufferSource buf, int light) {
		pose.pushPose();
		float size = 1.5F;
		pose.translate(0D, 0.5D, 0D);
		pose.scale(size, size, size);
		this.renderItem(pose, buf, light, entity.getRockStack());
		pose.popPose();
	}
}
