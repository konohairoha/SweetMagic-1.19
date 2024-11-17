package sweetmagic.init.render.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit;
import sweetmagic.init.entity.projectile.AbstractMagicShot;
import sweetmagic.util.RenderUtil;

public class RenderToxicCircle extends EntityRenderer<AbstractMagicShot> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/block/empty.png");
	private final BlockRenderDispatcher render;
	private static final BlockState STATE = BlockInit.poison_block.defaultBlockState();

	public RenderToxicCircle(EntityRendererProvider.Context con) {
		super(con);
		this.render = con.getBlockRenderDispatcher();
	}

	@Override
	public void render(AbstractMagicShot entity, float yaw, float part, PoseStack pose, MultiBufferSource buf, int light) {
		pose.pushPose();

		float f1 = entity.tickCount * 3F;
		pose.mulPose(Vector3f.YP.rotationDegrees(f1));
		pose.scale(0.67F, 0.67F, 0.67F);
		pose.translate(-0.5D, 0.25D, -0.5D);

        ModelBlockRenderer.enableCaching();
		RenderUtil.renderBlock(entity.level, entity.blockPosition(), STATE, this.render, pose, buf, OverlayTexture.NO_OVERLAY);
        ModelBlockRenderer.clearCache();
		pose.popPose();
	}

	@Override
	public ResourceLocation getTextureLocation(AbstractMagicShot entity) {
		return TEX;
	}
}
