package sweetmagic.init.render.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import sweetmagic.init.entity.block.Cushion;

public class RenderCushion<T extends Cushion> extends EntityRenderer<T> {

	public RenderCushion(EntityRendererProvider.Context con) {
		super(con);
	}

	@Override
	public void render(T entity, float yaw, float part, PoseStack pose, MultiBufferSource buf, int light) {
		pose.pushPose();
		pose.translate(0D, 0.5D, 0D);
		pose.scale(2F, 2F, 2F);
		pose.mulPose(Vector3f.YP.rotationDegrees(-90F + entity.getVisualRotationYInDegrees()));
		ModelBlockRenderer.enableCaching();
		Minecraft.getInstance().getItemRenderer().renderStatic(entity.getStack(), ItemTransforms.TransformType.FIXED, light, OverlayTexture.NO_OVERLAY, pose, buf, 0);
		ModelBlockRenderer.clearCache();
		pose.popPose();
	}

	@Override
	public ResourceLocation getTextureLocation(T entity) {
		return null;
	}

	@Override
	protected void renderNameTag(T entity, Component com, PoseStack pose, MultiBufferSource buf, int light) { }
}
