package sweetmagic.init.render.block;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemStack;
import sweetmagic.init.tile.sm.TileAetherReverse;

public class RenderAetherReverse extends RenderAbstractTile<TileAetherReverse> {

	public RenderAetherReverse(BlockEntityRendererProvider.Context con) {
		super(con);
	}

	@Override
	public void render(TileAetherReverse tile, float parTick, PoseStack pose, MultiBufferSource buf, int light, int overlayLight) {
		if (tile.getLevel() == null || tile.isAir() || tile.craftList.isEmpty()) { return; }

		ItemStack stack = tile.getInputItem();

		if (stack.isEmpty()) {
			stack = tile.stack;
		}

		if (!stack.isEmpty()) {
			pose.pushPose();
			pose.translate(0.5D, 1.575D, 0.5D);
			long gameTime = tile.getTime();
			pose.translate(0D, Math.sin((gameTime + parTick) / 24F) * 0.05D, 0D);
			pose.scale(0.35F, 0.35F, 0.35F);
			float angle = (gameTime + parTick) / 20.0F * (180F / (float) Math.PI);
			pose.mulPose(Vector3f.YP.rotationDegrees(angle));
			this.iRender.renderStatic(stack, ItemTransforms.TransformType.FIXED, light, OverlayTexture.NO_OVERLAY, pose, buf, 0);
			pose.popPose();
		}

		this.renderModenRack(tile, pose, buf, light, overlayLight);
	}

	public void renderModenRack (TileAetherReverse tile, PoseStack pose, MultiBufferSource buf, int light, int overlayLight) {

		List<ItemStack> stackList = tile.craftList;

        // インベントリ分描画
        for (int i = 0; i < stackList.size(); i++) {

			ItemStack stack = stackList.get(i);

			pose.pushPose();
			pose.scale(0.25F, 0.25F, 0.25F);
			double x = i * -0.8775D + (i / 3) * 2.625D;
			double y = (i / 3) * 0.88D;
			pose.translate(1.1125D + y, 4.0D, 2.875D + x);
			pose.mulPose(Vector3f.YP.rotationDegrees(tile.getRot()));
			pose.mulPose(Vector3f.XP.rotationDegrees(90F));
			this.iRender.renderStatic(stack, ItemTransforms.TransformType.FIXED, light, OverlayTexture.NO_OVERLAY, pose, buf, 0);
			pose.popPose();
		}
	}
}
