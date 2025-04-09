package sweetmagic.init.render.block;

import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import sweetmagic.init.RenderTypeInit;
import sweetmagic.init.tile.sm.TileAlternativeTank;
import sweetmagic.util.RenderUtil.RenderInfo;

public class RenderAlternativeTank<T extends TileAlternativeTank> extends RenderAbstractTile<T> {

	public RenderAlternativeTank(BlockEntityRendererProvider.Context con) {
		super(con);
	}

	public void render(T tile, float parTick, RenderInfo info) {
		FluidStack fluid = tile.getContent();
		if (fluid.isEmpty()) { return; }

		int xd = 0;
		int zd = 0;
		if (xd < 0 || zd < 0) { return; }
		float[] xBounds = getBlockBounds(xd);
		float[] zBounds = getBlockBounds(zd);

		VertexConsumer ver = info.buf().getBuffer(RenderTypeInit.SMELTERY_FLUID);
		float curY = 0.125F;
		float h = tile.getFluidProgressScaled(0.775F);
		IClientFluidTypeExtensions ex = IClientFluidTypeExtensions.of(fluid.getFluid());
		TextureAtlasSprite tex = this.getBlockSprite(ex.getStillTexture(fluid));
		int color = ex.getTintColor(fluid);
		this.renderLargeFluidCuboid(info.pose(), ver, info.light(), xd, xBounds, zd, zBounds, curY, curY + h, tex, color);
		curY += h;
	}
}
