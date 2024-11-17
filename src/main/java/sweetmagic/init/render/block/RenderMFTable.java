package sweetmagic.init.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sweetmagic.init.tile.sm.TileMFTable;

@OnlyIn(Dist.CLIENT)
public class RenderMFTable extends RenderAbstractTile<TileMFTable> {

	public RenderMFTable(BlockEntityRendererProvider.Context con) {
		super(con);
	}

	@Override
	public void render(TileMFTable tile, float parTick, PoseStack pose, MultiBufferSource buf, int light, int overlayLight) {
		if (tile.getLevel() == null) { return; }

		long gameTime = tile.getTime();
		int count = tile.getInvSize();

		if (count <= 1) {

			ItemStack stack = tile.getInputItem(0);
			if (stack.isEmpty()) { return; }

			this.renderStack(tile, parTick, pose, buf, light, overlayLight, gameTime, stack);
		}

		else {

			float rotY = (gameTime + parTick) * 0.05F;

			for (int i = 0; i < count; i++) {

				ItemStack stack = tile.getInputItem(i);
				if (stack.isEmpty()) { continue; }

				pose.pushPose();
				pose.translate(0.5F, 1.3F, 0.5F);
				pose.mulPose(Vector3f.YP.rotationDegrees(rotY * this.pi + (i * (360 / count))));
				pose.scale(0.5F, 0.5F, 0.5F);
				pose.translate(-1F, 0F, 0F);
				this.iRender.renderStatic(stack, ItemTransforms.TransformType.FIXED, light, OverlayTexture.NO_OVERLAY, pose, buf, 0);
				pose.popPose();
			}

			ItemStack stack = tile.getFuelItem();

			if (!stack.isEmpty()) {
				this.renderStack(tile, parTick, pose, buf, light, overlayLight, gameTime, stack);
			}
		}
	}

	public void renderStack (TileMFTable tile, float parTick, PoseStack pose, MultiBufferSource buf, int light, int overlayLight, long gameTime, ItemStack stack) {
		pose.pushPose();
		pose.translate(0.5D, 1.4D, 0.5D);
		pose.translate(0D, Math.sin((gameTime + parTick) * 0.1D) * 0.1D, 0D);
		float rotY = (gameTime + parTick) * 0.05F;
		pose.mulPose(Vector3f.YP.rotationDegrees(rotY * this.pi));
		pose.scale(0.5F, 0.5F, 0.5F);
		this.iRender.renderStatic(stack, ItemTransforms.TransformType.FIXED, light, OverlayTexture.NO_OVERLAY, pose, buf, 0);
		pose.popPose();
	}
}
