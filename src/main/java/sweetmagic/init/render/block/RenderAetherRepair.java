package sweetmagic.init.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemStack;
import sweetmagic.init.tile.sm.TileAetherRepair;

public class RenderAetherRepair extends RenderAbstractTile<TileAetherRepair> {

	public RenderAetherRepair(BlockEntityRendererProvider.Context con) {
		super(con);
	}

	@Override
	public void render(TileAetherRepair tile, float parTick, PoseStack pose, MultiBufferSource buf, int light, int overlayLight) {
		if (tile.getLevel() == null || tile.isAir()) { return; }

		this.renderRepairStack(tile, tile.getInputItem(0), 0F, 0.2F, parTick, pose, buf, light, overlayLight);
		this.renderRepairStack(tile, tile.getInputItem(1), -0.2F, 0F, parTick, pose, buf, light, overlayLight);
		this.renderRepairStack(tile, tile.getInputItem(2), 0F, -0.2F, parTick, pose, buf, light, overlayLight);
		this.renderRepairStack(tile, tile.getInputItem(3), 0.2F, 0F, parTick, pose, buf, light, overlayLight);
	}

	public void renderRepairStack (TileAetherRepair tile, ItemStack stack, float x, float z, float parTick, PoseStack pose, MultiBufferSource buf, int light, int overlayLight) {
		if (stack.isEmpty()) { return; }

		pose.pushPose();
		pose.mulPose(Vector3f.YP.rotationDegrees(tile.getRot()));

		switch (tile.getFace()) {
		case SOUTH:
			pose.translate(-1D, 0D, -1D);
			break;
		case WEST:
			pose.translate(-1D, 0D, 0D);
			break;
		case EAST:
			pose.translate(0D, 0D, -1D);
			break;
		}

		pose.translate(0.5D + x, 0.8D, 0.5D + z);
		long gameTime = tile.getTime();
		pose.translate(0D, Math.sin((gameTime + parTick) / 10F) * 0.05D, 0D);
		pose.scale(0.25F, 0.25F, 0.25F);
		float angle = (gameTime + parTick) / 20F * this.pi;
		pose.mulPose(Vector3f.YP.rotationDegrees(angle));
		this.iRender.renderStatic(stack, ItemTransforms.TransformType.FIXED, light, OverlayTexture.NO_OVERLAY, pose, buf, 0);
		pose.popPose();
	}
}
