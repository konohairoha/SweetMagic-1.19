package sweetmagic.init.render.block;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import sweetmagic.init.tile.sm.TileAetherReverse;
import sweetmagic.util.RenderUtil.RenderInfo;

public class RenderAetherReverse<T extends TileAetherReverse> extends RenderAbstractTile<T> {

	public RenderAetherReverse(BlockEntityRendererProvider.Context con) {
		super(con);
	}

	public void render(T tile, float parTick, RenderInfo info) {

		PoseStack pose = info.pose();
		ItemStack stack = tile.getInputItem();
		if (stack.isEmpty()) { stack = tile.stack; }

		if (!stack.isEmpty()) {
			pose.pushPose();
			pose.translate(0.5D, 1.575D, 0.5D);
			int gameTime = tile.getClientTime();
			pose.translate(0D, Math.sin((gameTime + parTick) / 24F) * 0.05D, 0D);
			pose.scale(0.35F, 0.35F, 0.35F);
			float angle = (gameTime + parTick) / 20.0F * (180F / (float) Math.PI);
			pose.mulPose(Vector3f.YP.rotationDegrees(angle));
			info.itemRender(stack);
			pose.popPose();
		}

		this.renderModenRack(tile, info);
	}

	public void renderModenRack(T tile, RenderInfo info) {

		List<ItemStack> stackList = tile.craftList;
		PoseStack pose = info.pose();

		// インベントリ分描画
		for (int i = 0; i < stackList.size(); i++) {
			pose.pushPose();
			pose.scale(0.25F, 0.25F, 0.25F);
			double x = i * -0.8775D + (i / 3) * 2.625D;
			double y = (i / 3) * 0.88D;
			pose.translate(1.1125D + y, 4.0D, 2.875D + x);
			pose.mulPose(Vector3f.YP.rotationDegrees(tile.getRot()));
			pose.mulPose(Vector3f.XP.rotationDegrees(90F));
			info.itemRender(stackList.get(i));
			pose.popPose();
		}
	}
}
