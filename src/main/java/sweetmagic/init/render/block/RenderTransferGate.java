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
import sweetmagic.init.tile.sm.TileTransferGate;
import sweetmagic.util.RenderUtil;
import sweetmagic.util.RenderUtil.RenderColor;

public class RenderTransferGate extends RenderAbstractTile<TileTransferGate> {

	private static final ItemStack GATE = new ItemStack(BlockInit.transfer_gate);
	private static final Block SQUARE_BLOCK_L = BlockInit.magic_square_l_blank;

	public RenderTransferGate(BlockEntityRendererProvider.Context con) {
		super(con);
	}

	@Override
	public void render(TileTransferGate tile, float parTick, PoseStack pose, MultiBufferSource buf, int light, int overlayLight) {
		if (tile.getLevel() == null || tile.isAir()) { return; }
		this.renderModel(tile, parTick, pose, buf, light, overlayLight);	// ブロックの描画
		this.renderSquare(tile, parTick, pose, buf, light, overlayLight);	// 魔法陣の描画
	}

	public void renderModel (TileTransferGate tile, float parTick, PoseStack pose, MultiBufferSource buf, int light, int overlayLight) {
		pose.pushPose();
		pose.translate(0.5D, 0D, 0.5D);
		pose.scale(4F, 4F, 4F);
		pose.mulPose(Vector3f.YP.rotationDegrees(tile.getRot()));
		this.iRender.renderStatic(GATE, ItemTransforms.TransformType.FIXED, light, OverlayTexture.NO_OVERLAY, pose, buf, 0);
		pose.popPose();
	}

	public void renderSquare (TileTransferGate tile, float parTick, PoseStack pose, MultiBufferSource buf, int light, int overlayLight) {

		long gameTime = tile.getTime();
		pose.pushPose();
		pose.translate(0.5D, 0.475D, 1.75D);
		pose.scale(5F, 5F, 5F);
		pose.mulPose(Vector3f.YP.rotationDegrees(tile.getRot()));
		pose.mulPose(Vector3f.XP.rotationDegrees(90F));

		switch (tile.getFace()) {
		case SOUTH:
			pose.translate(0D, 0.5D, 0D);
			break;
		case WEST:
			pose.translate(0.25D, 0.25D, 0D);
			break;
		case EAST:
			pose.translate(-0.25D, 0.25D, 0D);
			break;
		}

		float angle = -(gameTime + parTick) * 0.1F * this.pi;
		pose.mulPose(Vector3f.YP.rotationDegrees(angle));
		pose.translate(-0.25D, -0.25D, -0.25D);
		pose.scale(0.5F, 0.5F, 0.5F);

		float rgb = (float) Math.sin((gameTime + parTick) / 20F) * 40F;
		RenderColor color = new RenderColor((76F + rgb) / 255F, (165F + rgb) / 255F, (215 + rgb) / 255F, light, overlayLight);
		RenderUtil.renderBlock(pose, buf, color, SQUARE_BLOCK_L);
		pose.popPose();
	}
}
