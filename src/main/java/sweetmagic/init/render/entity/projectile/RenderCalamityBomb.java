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
import sweetmagic.init.entity.projectile.CalamityBomb;

public class RenderCalamityBomb extends EntityRenderer<CalamityBomb> {

	private static final ItemStack stack = new ItemStack(BlockInit.calamity_bomb);
	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/block/empty.png");

	public RenderCalamityBomb(EntityRendererProvider.Context con) {
		super(con);
	}

	@Override
	public void render(CalamityBomb entity, float yaw, float part, PoseStack pose, MultiBufferSource buf, int light) {

		pose.pushPose();
		float size = 1.0F;
		double posY = 0.6D;

		switch (entity.getCount()) {
		case 0:
			size = 0.67F;
			posY = 0.45D;
			break;
		case 1:
			size = 0.85F;
			posY = 0.525D;
			break;
		}

		pose.translate(0D, posY, 0D);
		pose.scale(size, size, size);
		Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.FIXED, light, OverlayTexture.NO_OVERLAY, pose, buf, 0);
		pose.popPose();
	}

	@Override
	public ResourceLocation getTextureLocation(CalamityBomb entity) {
		return TEX;
	}
}
