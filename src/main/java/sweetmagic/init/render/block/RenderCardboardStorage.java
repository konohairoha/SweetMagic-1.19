package sweetmagic.init.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import sweetmagic.init.tile.sm.TileCardboardStorage;
import sweetmagic.util.RenderUtil.RenderInfo;

public class RenderCardboardStorage <T extends TileCardboardStorage>extends RenderAbstractTile<T> {

	public RenderCardboardStorage(BlockEntityRendererProvider.Context con) {
		super(con);
	}

	public void render(T tile, float parTick, RenderInfo info) {
		ItemStack stack = tile.getInputItem(0);
		if (stack.isEmpty()) { return; }

		PoseStack pose = info.pose();
		pose.pushPose();
		pose.translate(0.5D, 0.5D, 0.5D);
		pose.mulPose(Vector3f.YP.rotationDegrees(tile.getRot()));
		pose.translate(0D, 0D, -0.5D);
		pose.scale(0.75F, 0.75F, 0.75F);
		info.itemRender(stack);
		pose.popPose();
	}
}
