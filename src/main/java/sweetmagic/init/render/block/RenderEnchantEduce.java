package sweetmagic.init.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemStack;
import sweetmagic.init.tile.sm.TileEnchantEduce;

public class RenderEnchantEduce extends RenderAbstractTile<TileEnchantEduce> {

	public RenderEnchantEduce(BlockEntityRendererProvider.Context con) {
		super(con);
	}

	@Override
	public void render(TileEnchantEduce tile, float parTick, PoseStack pose, MultiBufferSource buf, int light, int overlayLight) {
		if (tile.getLevel() == null || tile.isAir()) { return; }

		pose.pushPose();
		pose.mulPose(Vector3f.YP.rotationDegrees(tile.getRot()));
		this.renderModenRack(tile, pose, buf, light, overlayLight);
		pose.popPose();
	}

	public void renderModenRack (TileEnchantEduce tile, PoseStack pose, MultiBufferSource buf, int light, int overlayLight) {
		this.renderItem(tile, tile.getInputItem(), 1.4F, 0F, 0.85F, pose, buf, light, overlayLight);
		this.renderItem(tile, tile.getBookItem(), 0.9F, 0F, 1.65F, pose, buf, light, overlayLight);
		this.renderItem(tile, tile.getPageItem(), 0.5F, 0F, 0.55F, pose, buf, light, overlayLight);
	}

	public void renderItem (TileEnchantEduce tile, ItemStack stack, float x, float y, float z, PoseStack pose, MultiBufferSource buf, int light, int overlayLight) {
		if (stack.isEmpty()) { return; }

		switch (tile.getFace()) {
		case SOUTH:
			x -= 2D;
			z -= 2D;
			break;
		case WEST:
			x -= 2D;
			break;
		case EAST:
			z -= 2D;
			break;
		}

		pose.pushPose();
		pose.scale(0.5F, 0.5F, 0.5F);
		pose.translate(x, y + 2F, z);
		pose.mulPose(Vector3f.XP.rotationDegrees(90F));
		this.iRender.renderStatic(stack, ItemTransforms.TransformType.FIXED, light, OverlayTexture.NO_OVERLAY, pose, buf, 0);
		pose.popPose();
	}
}

