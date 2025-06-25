package sweetmagic.init.render.block;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import sweetmagic.init.tile.sm.TileWoodChest;
import sweetmagic.util.RenderUtil;
import sweetmagic.util.RenderUtil.RenderInfo;

public class RenderWoodChest<T extends TileWoodChest> extends RenderAbstractTile<T> {

	private final int[] intArray = { 0, 1, 13, 14 };

	public RenderWoodChest(BlockEntityRendererProvider.Context con) {
		super(con);
	}

	public void render(T tile, float parTick, RenderInfo info) {
		if(tile.getData() != 11) { return; }
		this.renderModenRack(tile, info);
	}

	public void renderModenRack(T tile, RenderInfo info) {
		for (int i : this.intArray) {
			ItemStack stack = tile.getInputItem(i);
			if (stack.isEmpty()) { continue; }
			i = i >= 13 ? i + 3 : i;
			RenderUtil.renderItem(info, tile, stack, 1.875D - 1.125D * i % 2, 1.85D - (i > 12 ? 1.21D : 0D), 1.75D);
		}
	}
}
