package sweetmagic.init.render.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.ItemInit;
import sweetmagic.init.entity.projectile.AbstractMagicShot;

public class RenderBelialSword<T extends AbstractMagicShot> extends EntityRenderer<T> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/block/empty.png");
	private final ItemRenderer render;
	private static final ItemStack STACK = new ItemStack(ItemInit.belial_sword);

	public RenderBelialSword(EntityRendererProvider.Context con) {
		super(con);
		this.render = con.getItemRenderer();
	}

	@Override
	public void render(T entity, float yaw, float part, PoseStack pose, MultiBufferSource buf, int light) {
		pose.pushPose();
		pose.translate(0D, 0.5D, 0D);
		float scale = 2F;
		pose.scale(scale, scale, scale);
		float addX = entity.getXRot() > 0F ? Math.max(0F, entity.getXRot()) : Math.min(0F, entity.getXRot());
		pose.mulPose(Vector3f.ZP.rotationDegrees(0F));
		pose.mulPose(Vector3f.YP.rotationDegrees(90F + entity.getVisualRotationYInDegrees()));
		pose.mulPose(Vector3f.XP.rotationDegrees(180F + addX));

		this.render.renderStatic(STACK, ItemTransforms.TransformType.FIXED, light, OverlayTexture.NO_OVERLAY, pose, buf, 0);
		pose.popPose();
	}

	@Override
	public ResourceLocation getTextureLocation(T entity) {
		return TEX;
	}
}
