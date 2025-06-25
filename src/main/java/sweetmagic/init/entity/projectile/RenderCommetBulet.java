package sweetmagic.init.render.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;

import net.minecraft.client.model.ShulkerBulletModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import sweetmagic.init.entity.projectile.CommetBulet;

public class RenderCommetBulet<T extends CommetBulet> extends EntityRenderer<T> {

	private static final ResourceLocation TEX = new ResourceLocation("textures/entity/shulker/spark.png");
	private static final RenderType RENDER_TYPE = RenderType.entityTranslucent(TEX);
	private final ShulkerBulletModel<CommetBulet> model;

	public RenderCommetBulet(EntityRendererProvider.Context con) {
		super(con);
		this.model = new ShulkerBulletModel<>(con.bakeLayer(ModelLayers.SHULKER_BULLET));
	}

	public void render(T entity, float yaw, float part, PoseStack pose, MultiBufferSource buf, int light) {
		pose.pushPose();
		float f = Mth.rotlerp(entity.yRotO, entity.getYRot(), part);
		float f1 = Mth.lerp(part, entity.xRotO, entity.getXRot());
		float f2 = (float) entity.tickCount + part;
		pose.translate(0D, 0D, 0D);
		pose.mulPose(Vector3f.YP.rotationDegrees(Mth.sin(f2 * 0.1F) * 180F));
		pose.mulPose(Vector3f.XP.rotationDegrees(Mth.cos(f2 * 0.1F) * 180F));
		pose.mulPose(Vector3f.ZP.rotationDegrees(Mth.sin(f2 * 0.15F) * 360F));
		pose.scale(-0.5F, -0.5F, 0.5F);
		this.model.setupAnim(entity, 0F, 0F, 0F, f, f1);
		VertexConsumer ver = buf.getBuffer(this.model.renderType(TEX));
		this.model.renderToBuffer(pose, ver, light, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1F);
		pose.scale(1.5F, 1.5F, 1.5F);
		VertexConsumer ver1 = buf.getBuffer(RENDER_TYPE);
		this.model.renderToBuffer(pose, ver1, light, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 0.15F);
		pose.popPose();
		super.render(entity, yaw, part, pose, buf, light);
	}

	public ResourceLocation getTextureLocation(T entity) {
		return TEX;
	}
}
