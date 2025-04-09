package sweetmagic.init.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import sweetmagic.init.tile.sm.TileMFTable;
import sweetmagic.util.RenderUtil.RenderInfo;

public class RenderMFTable<T extends TileMFTable> extends RenderAbstractTile<T> {

	public RenderMFTable(BlockEntityRendererProvider.Context con) {
		super(con);
	}

	public void render(T tile, float parTick, RenderInfo info) {

		int gameTime = tile.getClientTime();
		int count = tile.getInvSize();

		if (count <= 1) {

			ItemStack stack = tile.getInputItem(0);
			if (stack.isEmpty()) { return; }

			this.renderStack(tile, parTick, info, gameTime, stack);
		}

		else {

			float rotY = (gameTime + parTick) * 0.05F;
			PoseStack pose = info.pose();

			for (int i = 0; i < count; i++) {

				ItemStack stack = tile.getInputItem(i);
				if (stack.isEmpty()) { continue; }

				pose.pushPose();
				pose.translate(0.5F, 1.3F, 0.5F);
				pose.mulPose(Vector3f.YP.rotationDegrees(rotY * this.pi + (i * (360 / count))));
				pose.scale(0.5F, 0.5F, 0.5F);
				pose.translate(-1F, 0F, 0F);
				info.itemRenderNo(stack);
				pose.popPose();
			}

			ItemStack stack = tile.getFuelItem();

			if (!stack.isEmpty()) {
				this.renderStack(tile, parTick, info, gameTime, stack);
			}
		}
	}

	public void renderStack(T tile, float parTick, RenderInfo info, int gameTime, ItemStack stack) {
		PoseStack pose = info.pose();
		pose.pushPose();
		pose.translate(0.5D, 1.4D, 0.5D);
		pose.translate(0D, Math.sin((gameTime + parTick) * 0.1D) * 0.1D, 0D);
		float rotY = (gameTime + parTick) * 0.05F;
		pose.mulPose(Vector3f.YP.rotationDegrees(rotY * this.pi));
		pose.scale(0.5F, 0.5F, 0.5F);
		info.itemRenderNo(stack);
		pose.popPose();
	}
}
