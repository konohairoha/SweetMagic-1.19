package sweetmagic.init.render.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.entity.projectile.AbstractMagicShot;
import sweetmagic.util.RenderUtil;

public class RenderFireShot<T extends AbstractMagicShot> extends EntityRenderer<T> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/block/empty.png");
	private final BlockRenderDispatcher render;
	private static final BlockState STATE = Blocks.MAGMA_BLOCK.defaultBlockState();

	public RenderFireShot(EntityRendererProvider.Context con) {
		super(con);
		this.render = con.getBlockRenderDispatcher();
	}

	@Override
	public void render(T entity, float yaw, float part, PoseStack pose, MultiBufferSource buf, int light) {
		if (entity.getData() <= 1) { return; }

		pose.pushPose();
		pose.translate(-0.5D, 0D, -0.5D);
		ModelBlockRenderer.enableCaching();
		RenderUtil.renderBlock(entity.level, entity.blockPosition(), STATE, this.render, pose, buf, OverlayTexture.NO_OVERLAY);
		ModelBlockRenderer.clearCache();
		pose.popPose();
	}

	@Override
	public ResourceLocation getTextureLocation(T entity) {
		return TEX;
	}
}
