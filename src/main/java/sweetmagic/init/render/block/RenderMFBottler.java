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
import sweetmagic.init.ItemInit;
import sweetmagic.init.tile.sm.TileMFBottler;
import sweetmagic.util.RenderUtil;
import sweetmagic.util.RenderUtil.RenderColor;

public class RenderMFBottler extends RenderAbstractTile<TileMFBottler> {

	private static final ItemStack STACK = new ItemStack(ItemInit.magia_bottle);
	private static final Block SQUARE_BLOCK = BlockInit.magic_square_l;

	public RenderMFBottler(BlockEntityRendererProvider.Context con) {
		super(con);
	}

	@Override
	public void render(TileMFBottler tile, float parTick, PoseStack pose, MultiBufferSource buf, int light, int overlayLight) {
		if (tile.getLevel() == null || tile.isMFEmpty() || tile.isAir()) { return; }
		this.renderItem(tile, parTick, pose, buf, light, overlayLight);
		this.renderSquare(tile, parTick, pose, buf, light, overlayLight);
	}

	public void renderItem(TileMFBottler tile, float parTick, PoseStack pose, MultiBufferSource buf, int light, int overlayLight) {

		float scale = 0.675F;
		double shake = 0.1D;

		pose.pushPose();
		pose.translate(0.5D, 0.5D, 0.5D);
		long gameTime = tile.getTime();
		pose.translate(0D, Math.sin((gameTime + parTick) / 10F) * shake, 0D);
		pose.scale(scale, scale, scale);
		float angle = (gameTime + parTick) / 20F * this.pi;
		pose.mulPose(Vector3f.YP.rotationDegrees(angle));
		ItemStack stack = STACK;

		this.iRender.renderStatic(stack, ItemTransforms.TransformType.FIXED, light, OverlayTexture.NO_OVERLAY, pose, buf, 0);
		pose.popPose();
	}

	public void renderSquare(TileMFBottler tile, float parTick, PoseStack pose, MultiBufferSource buf, int light, int overlayLight) {

		pose.pushPose();

		pose.translate(0.5D, 1.65D, 0.5D);
		long gameTime = tile.getTime();
		pose.translate(0D, Math.sin((gameTime + parTick) / 15F) * 0.02D, 0D);
		pose.scale(2F, 2F, 2F);
		float angle = (gameTime + parTick) / 20F * this.pi;
		pose.mulPose(Vector3f.YP.rotationDegrees(angle));

		pose.translate(-0.25D, -0.25D, -0.25D);
		pose.scale(0.5F, 0.5F, 0.5F);
		RenderColor color = new RenderColor(102F / 255F, 206F / 255F, 1F, light, overlayLight);
		RenderUtil.renderBlock(pose, buf, color, SQUARE_BLOCK);

		pose.popPose();
	}
}
