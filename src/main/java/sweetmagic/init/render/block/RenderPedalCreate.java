package sweetmagic.init.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import sweetmagic.init.tile.sm.TilePedalCreate;
import sweetmagic.util.RenderUtil;
import sweetmagic.util.RenderUtil.RenderColor;
import sweetmagic.util.RenderUtil.RenderInfo;

public class RenderPedalCreate<T extends TilePedalCreate> extends RenderAbstractTile<T> {

	private static final float size = 0.45F;

	public RenderPedalCreate(BlockEntityRendererProvider.Context con) {
		super(con);
	}

	public void render(T tile, float parTick, RenderInfo info) {

		PoseStack pose = info.pose();
		if (!tile.isHaveBlock) {
			pose.pushPose();
			pose.translate(0D, -0.95D, 0D);
			RenderUtil.renderTransBlock(pose, info.buf(), RenderColor.create(info), tile.getNeedBlock().defaultBlockState(), 0.55F);
			pose.popPose();
		}

		else if (tile.isCraft) {

			int count = tile.craftList.size() - 1;
			int nowTick = tile.nowTick * ( !tile.quickCraft ? 1 : 2 );
			float posY = 1F + nowTick * 0.0065F;
			int gameTime = tile.getClientTime();
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
				info.itemRenderNo(stack);
				pose.popPose();
			}

			ItemStack stack = tile.craftList.get(0);
			float rot = gameTime % 360;
			pose.pushPose();
			pose.translate(0.5F, posY, 0.5F);
			pose.translate(0, Math.sin((gameTime + parTick) * 0.1F) * 0.15F + 0.2F, 0);
			pose.scale(size, size, size);
			pose.mulPose(Vector3f.YP.rotationDegrees(rot));
			info.itemRenderNo(stack);
			pose.popPose();
		}
	}
}
