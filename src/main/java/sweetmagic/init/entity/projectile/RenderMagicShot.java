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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.entity.projectile.AbstractMagicShot;

public class RenderMagicShot<T extends AbstractMagicShot> extends EntityRenderer<T> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/block/empty.png");
	public static final ResourceLocation ARROW = new ResourceLocation("textures/entity/projectiles/arrow.png");

	public RenderMagicShot(EntityRendererProvider.Context cont) {
		super(cont);
	}

	@Override
	public void render(T entity, float yaw, float part, PoseStack pose, MultiBufferSource buf, int light) {
		if (!entity.getArrow()) { return; }

		pose.pushPose();
		pose.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(part, entity.yRotO, entity.getYRot()) - 90F));
		pose.mulPose(Vector3f.ZP.rotationDegrees(Mth.lerp(part, entity.xRotO, entity.getXRot())));

		float f9 = (float) entity.shakeTime - part;
		if (f9 > 0F) {
			float f10 = -Mth.sin(f9 * 3F) * f9;
			pose.mulPose(Vector3f.ZP.rotationDegrees(f10));
		}

		pose.mulPose(Vector3f.XP.rotationDegrees(45F));
		pose.scale(0.075F, 0.075F, 0.075F);
		pose.translate(-4D, 0D, 0D);
		VertexConsumer ver = buf.getBuffer(RenderType.entityCutout(this.getTextureLocation(entity)));
		PoseStack.Pose pose3 = pose.last();
		Matrix4f mat4 = pose3.pose();
		Matrix3f mat3 = pose3.normal();
		this.vertex(mat4, mat3, ver, -7, -2, -2, 0F, 0.15625F, -1, 0, 0, light);
		this.vertex(mat4, mat3, ver, -7, -2, 2, 0.15625F, 0.15625F, -1, 0, 0, light);
		this.vertex(mat4, mat3, ver, -7, 2, 2, 0.15625F, 0.3125F, -1, 0, 0, light);
		this.vertex(mat4, mat3, ver, -7, 2, -2, 0F, 0.3125F, -1, 0, 0, light);
		this.vertex(mat4, mat3, ver, -7, 2, -2, 0F, 0.15625F, 1, 0, 0, light);
		this.vertex(mat4, mat3, ver, -7, 2, 2, 0.15625F, 0.15625F, 1, 0, 0, light);
		this.vertex(mat4, mat3, ver, -7, -2, 2, 0.15625F, 0.3125F, 1, 0, 0, light);
		this.vertex(mat4, mat3, ver, -7, -2, -2, 0F, 0.3125F, 1, 0, 0, light);

		for (int i = 0; i < 4; ++i) {
			pose.mulPose(Vector3f.XP.rotationDegrees(90F));
			this.vertex(mat4, mat3, ver, -8, -2, 0, 0F, 0F, 0, 1, 0, light);
			this.vertex(mat4, mat3, ver, 8, -2, 0, 0.5F, 0F, 0, 1, 0, light);
			this.vertex(mat4, mat3, ver, 8, 2, 0, 0.5F, 0.15625F, 0, 1, 0, light);
			this.vertex(mat4, mat3, ver, -8, 2, 0, 0F, 0.15625F, 0, 1, 0, light);
		}

		pose.popPose();
		super.render(entity, yaw, part, pose, buf, light);
	}

	@Override
	public ResourceLocation getTextureLocation(T entity) {
		return entity.getArrow() ? ARROW : TEX;
	}

	public void vertex(Matrix4f mat4, Matrix3f mat3, VertexConsumer ver, int par1, int par2, int par3, float par4, float par5, int par6, int par7, int par8,int par9) {
		ver.vertex(mat4, (float) par1, (float) par2, (float) par3).color(255, 255, 255, 255).uv(par4, par5).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(par9).normal(mat3, (float) par6, (float) par8, (float) par7).endVertex();
	}
}
