package sweetmagic.init.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemStack;
import sweetmagic.init.tile.sm.TilePedalCreate;
import sweetmagic.util.RenderUtil;
import sweetmagic.util.RenderUtil.RenderColor;

public class RenderPedalCreate extends RenderAbstractTile<TilePedalCreate> {

	private static final float size = 0.45F;

	public RenderPedalCreate(BlockEntityRendererProvider.Context con) {
		super(con);
	}

	@Override
	public void render(TilePedalCreate tile, float parTick, PoseStack pose, MultiBufferSource buf, int light, int overlayLight) {

		if (!tile.isHaveBlock) {
			pose.pushPose();
			pose.translate(0D, -0.95D, 0D);
			RenderUtil.renderTransBlock(pose, buf, new RenderColor(1F, 1F, 1F, light, overlayLight), tile.getNeedBlock(true));
			pose.popPose();
		}

		else if (tile.isCraft) {

			int count = tile.craftList.size() - 1;
			int nowTick = tile.nowTick * ( !tile.quickCraft ? 1 : 2 );
			float posY = 1F + nowTick * 0.0065F;
			long gameTime = tile.getTime();
			float rotY = (gameTime + parTick) / 90F;

			for (int i = 1; i < count + 1; i++) {

				ItemStack stack = tile.craftList.get(i);
				if (stack.isEmpty()) { continue; }

				pose.pushPose();
				pose.translate(0.5F, posY, 0.5F);
				pose.translate(0, Math.sin((gameTime + parTick) / 10F) * 0.15F + 0.2F, 0);
				pose.mulPose(Vector3f.YP.rotationDegrees(rotY * this.pi + (i * (360 / count)) + nowTick * 6.75F));
				pose.scale(size, size, size);
				pose.translate(1F - (0.0055F * nowTick) , 0F, 0F);
				this.iRender.renderStatic(stack, ItemTransforms.TransformType.FIXED, light, OverlayTexture.NO_OVERLAY, pose, buf, 0);
				pose.popPose();
			}

			ItemStack stack = tile.craftList.get(0);
			float rot = gameTime % 360;
			pose.pushPose();
			pose.translate(0.5F, posY, 0.5F);
			pose.translate(0, Math.sin((gameTime + parTick) * 0.1F) * 0.15F + 0.2F, 0);
			pose.scale(size, size, size);
			pose.mulPose(Vector3f.YP.rotationDegrees(rot));
			this.iRender.renderStatic(stack, ItemTransforms.TransformType.FIXED, light, OverlayTexture.NO_OVERLAY, pose, buf, 0);
			pose.popPose();
		}
	}
}
