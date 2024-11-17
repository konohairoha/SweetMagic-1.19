package sweetmagic.init.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import sweetmagic.init.BlockInit;
import sweetmagic.init.TagInit;
import sweetmagic.init.tile.sm.TileAltarCreat;
import sweetmagic.util.RenderUtil;
import sweetmagic.util.RenderUtil.RenderColor;

public class RenderAltarCreat extends RenderAbstractTile<TileAltarCreat> {

	private static final float size = 0.45F;
	private static final Block SQUARE = BlockInit.magic_square_s;

	public RenderAltarCreat(BlockEntityRendererProvider.Context con) {
		super(con);
	}

	@Override
	public void render(TileAltarCreat tile, float parTick, PoseStack pose, MultiBufferSource buf, int light, int overlayLight) {

		if (!tile.isHaveBlock || !tile.isRangeBlock) {

			if (!tile.isHaveBlock) {
				this.renderHaveBlock(tile, parTick, pose, buf, light, overlayLight);
			}

			if (!tile.isRangeBlock) {
				for (int x = -1; x <= 1; x++) {
					for (int z = -1; z <= 1; z++) {
						if (x == 0 && z == 0 || tile.getState(tile.getBlockPos().offset(x, -1, z)).is(TagInit.AC_BLOCK)) { continue; }
						this.renderRangeBlock(tile, parTick, pose, buf, light, overlayLight, x, z);
					}
				}
			}
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
				this.iRender.renderStatic(stack, ItemTransforms.TransformType.FIXED, light, overlayLight, pose, buf, 0);
				pose.popPose();
			}

			ItemStack stack = tile.craftList.get(0);

			float rot = gameTime % 360;
			pose.pushPose();
			pose.translate(0.5F, posY, 0.5F);
			pose.translate(0, Math.sin((gameTime + parTick) * 0.1F) * 0.15F + 0.2F, 0);
			pose.scale(size, size, size);
			pose.mulPose(Vector3f.YP.rotationDegrees(rot));
			this.iRender.renderStatic(stack, ItemTransforms.TransformType.FIXED, light, overlayLight, pose, buf, 0);
			pose.popPose();

			this.renderSquare(tile, parTick, pose, buf, light, overlayLight);
		}
	}

	public void renderHaveBlock (TileAltarCreat tile, float parTick, PoseStack pose, MultiBufferSource buf, int light, int overlayLight) {
		pose.pushPose();
		pose.translate(0D, -0.95D, 0D);
		RenderUtil.renderTransBlock(pose, buf, new RenderColor(1F, 1F, 1F, light, overlayLight), tile.getNeedBlock(true));
		pose.popPose();
	}

	public void renderRangeBlock (TileAltarCreat tile, float parTick, PoseStack pose, MultiBufferSource buf, int light, int overlayLight, int x, int z) {
		pose.pushPose();
		pose.translate(x, -0.97D, z);
		RenderUtil.renderTransBlock(pose, buf, new RenderColor(1F, 1F, 1F, light, overlayLight), tile.getRangeBlock(true));
		pose.popPose();
	}

	public void renderSquare (TileAltarCreat tile, float parTick, PoseStack pose, MultiBufferSource buf, int light, int overlayLight) {

		float maxSize = 1F;
		float addValue = 0.0125F;
		float size = Math.min(maxSize, 0.125F + (tile.nowTick * addValue));

		pose.pushPose();
		pose.translate(0.5D, 1D, 0.5D);
		long gameTime = tile.getTime();
		pose.scale(size, size, size);
		float angle = (gameTime + parTick) / -20F * this.pi;
		pose.mulPose(Vector3f.YP.rotationDegrees(angle));
		pose.translate(-0.5D, 0D, -0.5D);
		RenderUtil.renderBlock(pose, buf, new RenderColor(72F / 255F, 1F, 1F, light, overlayLight), SQUARE);
		pose.popPose();
	}
}
