package sweetmagic.init.render.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import sweetmagic.init.BlockInit;
import sweetmagic.init.entity.projectile.ExplosionThunderShot;

public class RenderExplosionThunder<T extends ExplosionThunderShot> extends RenderMagicBase<T> {

	private static final ItemStack BLOCK = new ItemStack(BlockInit.yellow_glass);

	public RenderExplosionThunder(EntityRendererProvider.Context con) {
		super(con);
	}

	@Override
	public void render(T entity, float yaw, float part, PoseStack pose, MultiBufferSource buf, int light) {
		pose.pushPose();
		float size = 1F;
		pose.translate(0D, 0.5D, 0D);
		pose.scale(size, size, size);
		this.renderItem(pose, buf, light, BLOCK);
		pose.popPose();
	}
}
