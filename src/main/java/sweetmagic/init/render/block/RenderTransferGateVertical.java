package sweetmagic.init.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import sweetmagic.init.BlockInit;
import sweetmagic.init.tile.sm.TileTransferGateVertical;
import sweetmagic.util.RenderUtil;
import sweetmagic.util.RenderUtil.RenderColor;
import sweetmagic.util.RenderUtil.RenderInfo;

public class RenderTransferGateVertical <T extends TileTransferGateVertical>extends RenderAbstractTile<T> {

	private static final ItemStack GATE = new ItemStack(BlockInit.transfer_gate_vertical);
	private static final ItemStack TOP = new ItemStack(BlockInit.transfer_gate_vertical_top);
	private static final Block SQUARE = BlockInit.magic_square_l_blank;

	public RenderTransferGateVertical(BlockEntityRendererProvider.Context con) {
		super(con);
	}

	public void render(T tile, float parTick, RenderInfo info) {
		this.renderModel(tile, parTick, info);	// ブロックの描画
		this.renderSquare(tile, parTick, info);	// 魔法陣の描画
	}

	public void renderModel(T tile, float parTick, RenderInfo info) {
		PoseStack pose = info.pose();
		pose.pushPose();
		pose.translate(0.5D, -0.5D, 0.5D);
		pose.scale(2F, 2F, 2F);
		pose.mulPose(Vector3f.YP.rotationDegrees(tile.getRot()));
		info.itemRenderNo(GATE);
		pose.popPose();

		pose.pushPose();
		pose.translate(0.5D, 0D, 0.5D);
		int gameTime = tile.getClientTime();
		float angle = (gameTime + parTick) * 0.067F * this.pi;
		pose.mulPose(Vector3f.YP.rotationDegrees(angle));
		pose.scale(3.5F, 3.5F, 3.5F);
		pose.mulPose(Vector3f.XP.rotationDegrees(90F));
		pose.translate(-0.0025D, -0.1125D, -0.2D);
		info.itemRenderNo(TOP);
		pose.popPose();
	}

	public void renderSquare(T tile, float parTick, RenderInfo info) {

		int gameTime = tile.getClientTime();
		PoseStack pose = info.pose();
		pose.pushPose();
		pose.translate(0.5D, 0.75D, 0.5D);
		pose.scale(5F, 5F, 5F);

		float angle = -(gameTime + parTick) * 0.1F * this.pi;
		pose.mulPose(Vector3f.YP.rotationDegrees(angle));
		pose.translate(-0.25D, -0.25D, -0.25D);
		pose.scale(0.5F, 0.5F, 0.5F);

		float rgb = (float) Math.sin((gameTime + parTick) / 20F) * 40F;
		RenderColor color = new RenderColor((76F + rgb) / 255F, (165F + rgb) / 255F, (215 + rgb) / 255F, info.light(), info.overlay());
		RenderUtil.renderBlock(info, color, SQUARE);
		pose.popPose();
	}
}
