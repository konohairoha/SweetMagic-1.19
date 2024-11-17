package sweetmagic.init.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemStack;
import sweetmagic.init.tile.sm.TileAccessoryTable;

public class RenderAccessoryTable extends RenderAbstractTile<TileAccessoryTable> {

	public RenderAccessoryTable(BlockEntityRendererProvider.Context con) {
		super(con);
	}

	@Override
	public void render(TileAccessoryTable tile, float parTick, PoseStack pose, MultiBufferSource buf, int light, int overlayLight) {
		if (tile.getLevel() == null || tile.isAir()) { return; }

		ItemStack stack = tile.getInputItem();
		if (stack.isEmpty()) {
			stack = tile.outStack;
		}

		if (stack.isEmpty()) { return; }

		float scale = 0.67F;
		pose.pushPose();
		pose.translate(0.5D, 1D, 0.5D);
		long gameTime = tile.getTime();
		pose.translate(0D, Math.sin((gameTime + parTick) / 24F) * 0.05D, 0D);
		pose.scale(scale, scale, scale);
		float angle = (gameTime + parTick) / 20F * this.pi;
		pose.mulPose(Vector3f.YP.rotationDegrees(angle));
		this.iRender.renderStatic(stack, ItemTransforms.TransformType.FIXED, light, OverlayTexture.NO_OVERLAY, pose, buf, 0);
		pose.popPose();
	}
}
