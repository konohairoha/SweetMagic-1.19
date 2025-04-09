package sweetmagic.init.render.block;

import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.RenderTypeInit;
import sweetmagic.init.tile.sm.TileMFTank;
import sweetmagic.util.RenderUtil.RenderInfo;

public class RenderMFTank<T extends TileMFTank> extends RenderAbstractTile<T> {

	private static final ResourceLocation SRC = SweetMagicCore.getSRC("block/mf_water_still");

	public RenderMFTank(BlockEntityRendererProvider.Context con) {
		super(con);
	}

	public void render(T tile, float parTick, RenderInfo info) {
		if (tile.isMFEmpty()) { return; }

		int xd = 0;
		int zd = 0;
		if (xd < 0 || zd < 0) { return; }
		float[] xBounds = this.getBlockBounds(xd);
		float[] zBounds = this.getBlockBounds(zd);

		VertexConsumer ver = info.buf().getBuffer(RenderTypeInit.SMELTERY_FLUID);
		float curY = 0.005F;
		float scale = Math.min(1F, (float) tile.getMF() / (float) tile.getMaxMF());
		float h = 0.99F * scale;
		TextureAtlasSprite tex = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(SRC);
		this.renderLargeFluidCuboid(info.pose(), ver, info.light(), xd, xBounds, zd, zBounds, curY, curY + h, tex, -1);
		curY += h;
	}
}
