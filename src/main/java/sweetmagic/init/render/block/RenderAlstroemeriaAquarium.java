package sweetmagic.init.render.block;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.Block;
import sweetmagic.init.BlockInit;
import sweetmagic.init.tile.sm.TileAlstroemeriaAquarium;
import sweetmagic.util.RenderUtil;
import sweetmagic.util.RenderUtil.RenderColor;
import sweetmagic.util.RenderUtil.RenderInfo;

public class RenderAlstroemeriaAquarium<T extends TileAlstroemeriaAquarium> extends RenderAbstractTile<T> {

	private static final Block WATER = BlockInit.water_clear;

	public RenderAlstroemeriaAquarium(BlockEntityRendererProvider.Context con) {
		super(con);
	}

	public void render(T tile, float parTick, RenderInfo info) {

		PoseStack pose = info.pose();
		pose.pushPose();
		pose.translate(0.13D, 0.140625D, 0.13D);
		pose.scale(0.74F, 0.75F, 0.74F);
		RenderColor color = new RenderColor(0.1F, 0.1F, 0.1F, info.light(), info.overlay());
		RenderUtil.renderTransBlock(pose, info.buf(), color, WATER);
		pose.popPose();
	}

	public int getViewDistance() {
		return 24;
	}
}
