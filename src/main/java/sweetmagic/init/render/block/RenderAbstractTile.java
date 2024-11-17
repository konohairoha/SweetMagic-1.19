package sweetmagic.init.render.block;

import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import sweetmagic.init.tile.sm.TileAbstractSM;

public abstract class RenderAbstractTile<T extends TileAbstractSM> implements BlockEntityRenderer<T> {

	protected final float pi = 180F / (float) Math.PI;
	protected final Font font;
	protected final ItemRenderer iRender;
	protected final BlockRenderDispatcher bRender;
	protected final EntityRenderDispatcher eRender;

	public RenderAbstractTile (BlockEntityRendererProvider.Context con) {
		this.font = con.getFont();
		this.iRender = con.getItemRenderer();
		this.bRender = con.getBlockRenderDispatcher();
		this.eRender = con.getEntityRenderer();
	}

	public int getViewDistance() {
		return 48;
	}
}
