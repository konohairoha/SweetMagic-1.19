package sweetmagic.init.render.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.entity.projectile.ShootingStar;

public class RenderShootingStar<T extends ShootingStar> extends EntityRenderer<T> {

	private static final ResourceLocation CRYSTAL = SweetMagicCore.getSRC("textures/entity/shooting_star.png");
	private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(CRYSTAL);
	private static final float SIN_45 = (float) Math.sin((Math.PI / 4D));
	private final ModelPart cube;
	private final ModelPart glass;
	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/block/empty.png");

	public RenderShootingStar(EntityRendererProvider.Context con) {
		super(con);
		ModelPart model = con.bakeLayer(ModelLayers.END_CRYSTAL);
		this.glass = model.getChild("glass");
		this.cube = model.getChild("cube");
	}

	@Override
	public void render(T entity, float yaw, float part, PoseStack pose, MultiBufferSource buf, int light) {
		pose.pushPose();
		int tickCount = entity.tickCount;
		float size = 1.5F + Math.min(1F, 0.01F * tickCount);
		int overray = OverlayTexture.NO_OVERLAY;
		pose.scale(size, size, size);
		pose.translate(0D, 0.75D, 0D);
		float f1 = tickCount * 3F;
		VertexConsumer vert = buf.getBuffer(RENDER_TYPE).color(0F, 0F, 0F, 1F);
		pose.mulPose(Vector3f.YP.rotationDegrees(f1));
		pose.translate(0D, Math.sin(tickCount / 20F) * 0.2D, 0D);
		pose.mulPose(new Quaternion(new Vector3f(SIN_45, 0F, SIN_45), 60F, true));
		this.glass.render(pose, vert, light, overray);

		pose.scale(0.875F, 0.875F, 0.875F);
		pose.mulPose(new Quaternion(new Vector3f(SIN_45, 0F, SIN_45), 60F, true));
		pose.mulPose(Vector3f.YP.rotationDegrees(f1));

		this.glass.render(pose, vert, light, overray);
		pose.scale(0.875F, 0.875F, 0.875F);
		pose.mulPose(new Quaternion(new Vector3f(SIN_45, 0F, SIN_45), 60F, true));
		pose.mulPose(Vector3f.YP.rotationDegrees(f1));

		pose.scale(0.875F, 0.875F, 0.875F);
		pose.mulPose(new Quaternion(new Vector3f(SIN_45, 0F, SIN_45), 60F, true));
		pose.mulPose(Vector3f.YP.rotationDegrees(f1));
		this.cube.render(pose, vert, light, overray);
		pose.popPose();
	}

	@Override
	public ResourceLocation getTextureLocation(T entity) {
		return TEX;
	}
}
