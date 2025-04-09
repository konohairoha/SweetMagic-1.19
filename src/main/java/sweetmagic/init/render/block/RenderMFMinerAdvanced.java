package sweetmagic.init.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import sweetmagic.init.BlockInit;
import sweetmagic.init.ItemInit;
import sweetmagic.init.tile.sm.TileMFMinerAdvanced;
import sweetmagic.util.RenderUtil;
import sweetmagic.util.RenderUtil.RenderColor;
import sweetmagic.util.RenderUtil.RenderInfo;

public class RenderMFMinerAdvanced<T extends TileMFMinerAdvanced> extends RenderAbstractTile<T> {

	private static final ItemStack STACK = new ItemStack(ItemInit.fluorite_pick);
	private static final Block SQUARE = BlockInit.magic_square_l;

	public RenderMFMinerAdvanced(BlockEntityRendererProvider.Context con) {
		super(con);
	}

	public void render(T tile, float parTick, RenderInfo info) {
		if (tile.isMFEmpty()) { return; }
		this.renderItem(tile, parTick, info);
		this.renderSquare(tile, parTick, info);
	}

	public void renderItem(T tile, float parTick, RenderInfo info) {

		float scale = 0.5F;
		double shake = 0.1D;

		PoseStack pose = info.pose();
		pose.pushPose();
		pose.translate(0.5D, 0.5D, 0.5D);
		int gameTime = tile.getClientTime();
		pose.translate(0D, Math.sin((gameTime + parTick) / 10F) * shake, 0D);
		pose.scale(scale, scale, scale);
		float angle = (gameTime + parTick) / 20F * this.pi;
		pose.mulPose(Vector3f.YP.rotationDegrees(angle));
		info.itemRender(STACK);
		pose.popPose();
	}

	public void renderSquare(T tile, float parTick, RenderInfo info) {

		PoseStack pose = info.pose();
		pose.pushPose();
		pose.translate(0.5D, 1.65D, 0.5D);
		int gameTime = tile.getClientTime();
		pose.translate(0D, Math.sin((gameTime + parTick) / 15F) * 0.02D, 0D);
		pose.scale(2F, 2F, 2F);
		float angle = (gameTime + parTick) / 20F * this.pi;
		pose.mulPose(Vector3f.YP.rotationDegrees(angle));

		pose.translate(-0.25D, -0.25D, -0.25D);
		pose.scale(0.5F, 0.5F, 0.5F);
		RenderColor color = new RenderColor(102F / 255F, 206F / 255F, 1F, info.light(), info.overlay());
		RenderUtil.renderBlock(info, color, SQUARE);
		pose.popPose();
	}
}
