package sweetmagic.init.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import sweetmagic.init.BlockInit;
import sweetmagic.init.tile.sm.TileJuiceMaker;
import sweetmagic.util.RenderUtil.RenderInfo;

public class RenderJuiceMaker<T extends TileJuiceMaker> extends RenderAbstractTile<T> {

	private static final ItemStack STACK = new ItemStack(BlockInit.water);

	public RenderJuiceMaker(BlockEntityRendererProvider.Context con) {
		super(con);
	}

	public void render(T tile, float parTick, RenderInfo info) {

		PoseStack pose = info.pose();
		pose.pushPose();
		float scale = Math.min(1F, (float) tile.getFluidValue() / (float) tile.getMaxFuildValue());
		pose.translate(0.5D, 0.565D - (1D - scale) / 2D - 0.25F * scale, 0.792D);
		pose.mulPose(Vector3f.YP.rotationDegrees(tile.getRot()));
		pose.scale(0.73F, scale * 0.975F, 0.425F);

		switch (tile.getFace()) {
		case SOUTH:
			pose.translate(0D, 0D, 1.375D);
			break;
		case WEST:
			pose.translate(0.4D, 0D, 0.6875D);
			break;
		case EAST:
			pose.translate(-0.4D, 0D, 0.6875D);
			break;
		}
		info.itemRender(STACK);
		pose.popPose();
	}

	public int getViewDistance() {
		return 32;
	}
}
