package sweetmagic.init.render.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import sweetmagic.init.ItemInit;
import sweetmagic.init.entity.projectile.AbstractMagicShot;

public class RenderSickleShot<T extends AbstractMagicShot> extends RenderMagicBase<T> {

	private static final ItemStack STACK = new ItemStack(ItemInit.alt_sickle);

	public RenderSickleShot(EntityRendererProvider.Context con) {
		super(con);
	}

	@Override
	public void render(T entity, float yaw, float part, PoseStack pose, MultiBufferSource buf, int light) {
		pose.pushPose();
		pose.translate(0D, 0.5D, 0D);
		float scale = (float) entity.getRange();
		pose.scale(scale, scale * 0.67F, scale);
		float angle = -entity.tickCount * 0.4F * (180F / (float) Math.PI);
		pose.mulPose(Vector3f.XP.rotationDegrees(90F));
		pose.mulPose(Vector3f.ZP.rotationDegrees(angle));
		this.renderItem(pose, buf, light, STACK);
		pose.popPose();
	}
}
