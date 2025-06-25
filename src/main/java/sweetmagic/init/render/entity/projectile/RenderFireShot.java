package sweetmagic.init.render.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import sweetmagic.init.entity.projectile.AbstractMagicShot;

public class RenderFireShot<T extends AbstractMagicShot> extends RenderMagicBase<T> {

	private static final BlockState STATE = Blocks.MAGMA_BLOCK.defaultBlockState();

	public RenderFireShot(EntityRendererProvider.Context con) {
		super(con);
	}

	@Override
	public void render(T entity, float yaw, float part, PoseStack pose, MultiBufferSource buf, int light) {
		if (entity.getData() <= 1) { return; }

		pose.pushPose();
		pose.translate(-0.5D, 0D, -0.5D);
		ModelBlockRenderer.enableCaching();
		this.renderBlock(entity, pose, buf, STATE);
		ModelBlockRenderer.clearCache();
		pose.popPose();
	}
}
