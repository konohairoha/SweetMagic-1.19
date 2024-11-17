package sweetmagic.init.render.block;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.Block;
import sweetmagic.init.BlockInit;
import sweetmagic.init.tile.sm.TileAlstroemeriaAquarium;
import sweetmagic.util.RenderUtil;
import sweetmagic.util.RenderUtil.RenderColor;

public class RenderAlstroemeriaAquarium implements BlockEntityRenderer<TileAlstroemeriaAquarium> {

	private static final Block WATER = BlockInit.water_clear;

	public RenderAlstroemeriaAquarium(BlockEntityRendererProvider.Context con) { }

	@Override
	public void render(TileAlstroemeriaAquarium tile, float parTick, PoseStack pose, MultiBufferSource buf, int light, int overlayLight) {
		if (tile.getLevel() == null || tile.isAir()) { return; }

		pose.pushPose();
		pose.translate(0.13D, 0.140625D, 0.13D);
		pose.scale(0.74F, 0.75F, 0.74F);
		RenderColor color = new RenderColor(0.1F, 0.1F, 0.1F, light, overlayLight);
		RenderUtil.renderTransBlock(pose, buf, color, WATER);
		pose.popPose();
	}

	public int getViewDistance() {
		return 24;
	}
}
