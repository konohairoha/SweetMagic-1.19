package sweetmagic.init.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import sweetmagic.init.tile.sm.TileAccessoryTable;
import sweetmagic.util.RenderUtil.RenderInfo;

public class RenderAccessoryTable<T extends TileAccessoryTable> extends RenderAbstractTile<T> {

	public RenderAccessoryTable(BlockEntityRendererProvider.Context con) {
		super(con);
	}

	public void render(T tile, float parTick, RenderInfo info) {
		ItemStack stack = tile.getInputItem();
		if (stack.isEmpty()) { stack = tile.outStack; }
		if (stack.isEmpty()) { return; }

		float scale = 0.67F;
		PoseStack pose = info.pose();
		pose.pushPose();
		pose.translate(0.5D, 1D, 0.5D);
		int gameTime = tile.getClientTime();
		pose.translate(0D, Math.sin((gameTime + parTick) / 24F) * 0.05D, 0D);
		pose.scale(scale, scale, scale);
		float angle = (gameTime + parTick) / 20F * this.pi;
		pose.mulPose(Vector3f.YP.rotationDegrees(angle));
		info.itemRenderNo(stack);
		pose.popPose();
	}
}
