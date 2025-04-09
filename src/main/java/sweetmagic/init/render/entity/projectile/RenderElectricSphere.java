package sweetmagic.init.render.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit;
import sweetmagic.init.entity.projectile.ElectricSphere;

public class RenderElectricSphere<T extends ElectricSphere> extends EntityRenderer<T> {

	private static final ItemStack STACK = new ItemStack(BlockInit.electricsphere);
	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/block/empty.png");

	public RenderElectricSphere(EntityRendererProvider.Context con) {
		super(con);
	}

	@Override
	public void render(T entity, float yaw, float part, PoseStack pose, MultiBufferSource buf, int light) {
		pose.pushPose();
		float size = 1F;
		pose.translate(0D, 0.5D, 0D);
		pose.scale(size, size, size);
		Minecraft mc = Minecraft.getInstance();
		mc.getItemRenderer().renderStatic(STACK, ItemTransforms.TransformType.FIXED, light, OverlayTexture.NO_OVERLAY, pose, buf, 0);
		pose.popPose();
	}

	@Override
	public ResourceLocation getTextureLocation(T entity) {
		return TEX;
	}
}
