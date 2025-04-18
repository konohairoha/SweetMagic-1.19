package sweetmagic.init.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import sweetmagic.init.tile.sm.TileAetherPlanter;
import sweetmagic.util.RenderUtil.RenderInfo;

public class RenderAetherPlanter<T extends TileAetherPlanter> extends RenderAbstractTile<TileAetherPlanter> {

	public RenderAetherPlanter(BlockEntityRendererProvider.Context con) {
		super(con);
	}

	public void render(T tile, float parTick, RenderInfo info) {
		ItemStack stack = tile.stack;
		if (stack.isEmpty()) { return; }

		PoseStack pose = info.pose();
		pose.pushPose();
		pose.translate(0.5D, 0.5D, 0.5D);
		pose.mulPose(Vector3f.YP.rotationDegrees(tile.getRot()));
		pose.translate(0D, 0.225D, -0.505D);
		pose.scale(0.5F, 0.5F, 0.5F);
		info.itemRender(stack);
		pose.popPose();
	}
}
