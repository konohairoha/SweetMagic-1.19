package sweetmagic.init.render.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import sweetmagic.init.entity.projectile.AbstractMagicShot;

public abstract class RenderBase<T extends AbstractMagicShot> extends EntityRenderer<T> {

	public RenderBase(EntityRendererProvider.Context con) {
		super(con);
	}

	protected int getBlockLightLevel(T entity, BlockPos pos) {
		return 15;
	}

	public void render(T entity, float par1, float par2, PoseStack pose, MultiBufferSource buf, int par3) {
		pose.pushPose();
		pose.scale(2.0F, 2.0F, 2.0F);
		pose.mulPose(this.entityRenderDispatcher.cameraOrientation());
		pose.mulPose(Vector3f.YP.rotationDegrees(180.0F));
		PoseStack.Pose poseStack = pose.last();
		Matrix4f mat4 = poseStack.pose();
		Matrix3f mat3 = poseStack.normal();
		VertexConsumer vert = buf.getBuffer(this.getRenderType());
		vertex(vert, mat4, mat3, par3, 0.0F, 0, 0, 1);
		vertex(vert, mat4, mat3, par3, 1.0F, 0, 1, 1);
		vertex(vert, mat4, mat3, par3, 1.0F, 1, 1, 0);
		vertex(vert, mat4, mat3, par3, 0.0F, 1, 0, 0);
		pose.popPose();
		super.render(entity, par1, par2, pose, buf, par3);
	}

	private static void vertex(VertexConsumer vert, Matrix4f mat4, Matrix3f mat3, int par1, float par2, int par3, int par4, int par5) {
		vert.vertex(mat4, par2 - 0.5F, (float) par3 - 0.25F, 0F).color(255, 255, 255, 255).uv((float) par4, (float) par5).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(par1).normal(mat3, 0F, 1F, 0F).endVertex();
	}

	public abstract RenderType getRenderType();
}
