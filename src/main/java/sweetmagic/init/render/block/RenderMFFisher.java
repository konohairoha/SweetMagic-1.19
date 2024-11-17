package sweetmagic.init.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import sweetmagic.init.BlockInit;
import sweetmagic.init.tile.sm.TileMFFisher;
import sweetmagic.util.RenderUtil;
import sweetmagic.util.RenderUtil.RenderColor;

public class RenderMFFisher extends RenderAbstractTile<TileMFFisher> {

	private static final ItemStack SQUARE = new ItemStack(BlockInit.magic_square);
	private static final Block SQUARE_BLOCK = BlockInit.magic_square_s;

	public RenderMFFisher(BlockEntityRendererProvider.Context con) {
		super(con);
	}

	@Override
	public void render(TileMFFisher tile, float parTick, PoseStack pose, MultiBufferSource buf, int light, int overlayLight) {
		if (tile.getLevel() == null || tile.isMFEmpty() || tile.isAir()) { return; }
		int data = tile.getData();
		this.renderItem(tile, parTick, pose, buf, light, overlayLight, data);
		this.renderSquare(tile, parTick, pose, buf, light, overlayLight, data);
	}

	public void renderItem(TileMFFisher tile, float parTick, PoseStack pose, MultiBufferSource buf, int light, int overlayLight, int data) {

		double addY = 0D;
		float scale = 0.5F;
		double shake = 0.1D;

		if (data == 3 || data == 4) {
			addY += 0.5D;
			scale = 0.375F;
			shake = 0D;
		}

		pose.pushPose();
		pose.translate(0.5D, 0.5D + addY, 0.5D);
		long gameTime = tile.getTime();
		pose.translate(0D, Math.sin((gameTime + parTick) / 10F) * shake, 0D);
		pose.scale(scale, scale, scale);
		float angle = (gameTime + parTick) / 20F * this.pi;
		pose.mulPose(Vector3f.YP.rotationDegrees(angle));
		ItemStack stack = ItemStack.EMPTY;

		switch(data) {
		case 0:
			stack = TileMFFisher.FISHING_ROD;
			break;
		case 1:
			stack = TileMFFisher.MACHETE;
			break;
		case 2:
			stack = TileMFFisher.MILK_PACK;
			break;
		case 3:
			stack = TileMFFisher.AETHER_CRYSTAL;
			break;
		case 4:
			stack = TileMFFisher.DIVINE_CRYSTAL;
			break;
		case 5:
			stack = TileMFFisher.ALT_PICK;
			break;
		}

		this.iRender.renderStatic(stack, ItemTransforms.TransformType.FIXED, light, OverlayTexture.NO_OVERLAY, pose, buf, 0);
		pose.popPose();
	}

	public void renderSquare(TileMFFisher tile, float parTick, PoseStack pose, MultiBufferSource buf, int light, int overlayLight, int data) {

		pose.pushPose();
		boolean isFurnace = data == 3 || data == 4;
		double addY = isFurnace ? -0.6D : 0D;

		pose.translate(0.5D, 1.65D + addY, 0.5D);
		long gameTime = tile.getTime();
		pose.translate(0D, Math.sin((gameTime + parTick) / 15F) * 0.02D, 0D);
		pose.scale(2F, 2F, 2F);
		float angle = (gameTime + parTick) / 20F * this.pi;
		pose.mulPose(Vector3f.YP.rotationDegrees(angle));

		if (isFurnace) {
			pose.translate(-0.25D, -0.25D, -0.25D);
			pose.scale(0.5F, 0.5F, 0.5F);
			RenderColor color = data == 3 ? new RenderColor(76F / 255F, 165F / 255F, 1F, light, overlayLight) : new RenderColor(102F / 255F, 206F / 255F, 1F, light, overlayLight);
			RenderUtil.renderBlock(pose, buf, color, SQUARE_BLOCK);
		}

		else {
			this.iRender.renderStatic(SQUARE, ItemTransforms.TransformType.FIXED, light, OverlayTexture.NO_OVERLAY, pose, buf, 0);
		}

		pose.popPose();
	}

	public int getViewDistance() {
		return 32;
	}
}
