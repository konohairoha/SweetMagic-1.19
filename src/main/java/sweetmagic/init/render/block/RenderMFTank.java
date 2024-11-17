package sweetmagic.init.render.block;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import sweetmagic.init.BlockInit;
import sweetmagic.init.tile.sm.TileMFTank;

public class RenderMFTank extends RenderAbstractTile<TileMFTank> {

	private static final float SIZE = (15.9F / 16F) * 2F;
	private static final ItemStack STACK = new ItemStack(BlockInit.magiaflux_liquidblock);

	public RenderMFTank(BlockEntityRendererProvider.Context con) {
		super(con);
	}

	@Override
	public void render(TileMFTank tile, float parTick, PoseStack pose, MultiBufferSource buf, int light, int overlayLight) {
		if (tile.isMFEmpty()) { return; }

		pose.pushPose();
		float scale = Math.min(1F, (float) tile.getMF() / (float) tile.getMaxMF());
		pose.translate(0.5D, 0.5D - (1D - scale) / 2D, 0.5D);
		pose.scale(SIZE, SIZE * scale, SIZE);
		this.iRender.renderStatic(STACK, ItemTransforms.TransformType.FIXED, light, overlayLight, pose, buf, 0);
		pose.popPose();
	}
}
