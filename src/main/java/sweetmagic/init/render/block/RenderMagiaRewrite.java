package sweetmagic.init.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import sweetmagic.init.BlockInit;
import sweetmagic.init.tile.sm.TileMagiaRewrite;
import sweetmagic.util.RenderUtil;
import sweetmagic.util.RenderUtil.RenderInfo;

public class RenderMagiaRewrite<T extends TileMagiaRewrite> extends RenderAbstractTile<T> {

	private static final ItemStack BOT = new ItemStack(BlockInit.magia_rewrite_bot);
	private static final ItemStack TOP = new ItemStack(BlockInit.magia_rewrite_top);
	private static final BlockState MAIN = BlockInit.magia_rewrite.defaultBlockState();

	public RenderMagiaRewrite(BlockEntityRendererProvider.Context con) {
		super(con);
	}

	@Override
	public void render(T tile, float parTick, PoseStack pose, MultiBufferSource buf, int light, int overlayLight) {
		if (tile.getLevel() == null || tile.isAir()) { return; }
		RenderInfo info = new RenderInfo(this.iRender, light, OverlayTexture.NO_OVERLAY, pose, buf);
		this.renderModelItem(tile, BOT, 0F, parTick, info, false);
		this.renderModelItem(tile, TOP, 0.0125F, parTick, info, true);
		this.renderModelMain(tile, parTick, info);
		this.renderItem(tile, parTick, info);	// アイテム描画
	}

	// アイテム描画
	public void renderModelItem(T tile, ItemStack stack, float y, float parTick, RenderInfo info, boolean isReverce) {

		boolean isCraft = tile.isCraft;
		float addY = 0F;
		float addAngle = 0F;

		if (isCraft) {

			int craftTick = tile.craftTick;
			float rate = 1F;
			addY = isReverce ? Math.min(craftTick * 0.00375F, 0.3F) : Math.min(craftTick * 0.00125F, 0.1F);
			rate += Math.min(craftTick * 0.03F, 5.5F);

			if (tile.craftTime > 25) {

				int time = 300 - craftTick;

				if (tile.craftTime >= 27) {
					addY = isReverce ? Math.max(time * 0.01F, 0F) : Math.max(time * 0.0033333F, 0F);
				}
			}

			addAngle += craftTick * rate;
		}

		PoseStack pose = info.pose();
		pose.pushPose();
		pose.translate(0.5D, 0.5D + y + addY, 0.5D);
		int gameTime = tile.getClientTime();
		pose.translate(0D, Math.sin((gameTime + parTick) / 10F) * 0.05D, 0D);
		pose.scale(1F, 1F, 1F);
		float angle = (gameTime + parTick + addAngle) / 40F * this.pi * (isReverce ? -1 : 1);
		pose.mulPose(Vector3f.YP.rotationDegrees(angle));
		info.itemRender(stack);
		pose.popPose();
	}

	// アイテム描画
	public void renderModelMain(T tile, float parTick, RenderInfo info) {
		PoseStack pose = info.pose();
		pose.pushPose();
		pose.translate(0D, 0D, 0D);
		RenderUtil.renderBlock(tile.getLevel(), tile.getBlockPos(), MAIN, this.bRender, pose, info.buf(), info.overlay());
		pose.popPose();
	}

	// アイテム描画
	public void renderItem(T tile, float parTick, RenderInfo info) {

		boolean isCraft = tile.isCraft;
		ItemStack stack = tile.getInputItem().copy();

		if (stack.isEmpty()) {

			stack = tile.outStack;

			if (stack.isEmpty()) {
				stack = tile.getOutItem();
			}
		}

		if (stack.isEmpty() && !isCraft) { return; }

		float addY = 0F;

		if (isCraft) {
			stack = tile.outStack;

			int craftTick = tile.craftTick;
			addY = Math.min(craftTick * 0.00625F, 0.5F);

			if (tile.craftTime >= 26) {
				int time = 300 - craftTick;
				addY = Math.max(time * 0.0125F, 0F);
			}
		}

		PoseStack pose = info.pose();
		pose.pushPose();
		pose.translate(0.5D, 0.55D + addY, 0.5D);
		int gameTime = tile.getClientTime();
		pose.translate(0D, Math.sin((gameTime + parTick) / 15F) * 0.025D, 0D);
		pose.scale(0.25F, 0.25F, 0.25F);
		float angle = (gameTime + parTick) / 40F * this.pi;
		pose.mulPose(Vector3f.YP.rotationDegrees(angle));
		info.itemRender(stack);
		pose.popPose();
	}
}
