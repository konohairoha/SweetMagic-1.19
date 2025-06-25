package sweetmagic.init.render.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;
import sweetmagic.init.BlockInit;
import sweetmagic.init.entity.projectile.AbstractMagicShot;

public class RenderToxicCircle<T extends AbstractMagicShot> extends RenderMagicBase<T> {

	private static final BlockState STATE = BlockInit.poison_block.defaultBlockState();

	public RenderToxicCircle(EntityRendererProvider.Context con) {
		super(con);
	}

	@Override
	public void render(T entity, float yaw, float part, PoseStack pose, MultiBufferSource buf, int light) {
		pose.pushPose();
		float f1 = entity.tickCount * 3F;
		pose.mulPose(Vector3f.YP.rotationDegrees(f1));
		pose.scale(0.67F, 0.67F, 0.67F);
		pose.translate(-0.5D, 0.25D, -0.5D);
		ModelBlockRenderer.enableCaching();
		this.renderBlock(entity, pose, buf, STATE);
		ModelBlockRenderer.clearCache();
		pose.popPose();
	}
}
