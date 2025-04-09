package sweetmagic.init.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import sweetmagic.init.tile.sm.TileEnchantEduce;
import sweetmagic.util.RenderUtil.RenderInfo;

public class RenderEnchantEduce<T extends TileEnchantEduce> extends RenderAbstractTile<T> {

	public RenderEnchantEduce(BlockEntityRendererProvider.Context con) {
		super(con);
	}

	public void render(T tile, float parTick, RenderInfo info) {
		PoseStack pose = info.pose();
		pose.pushPose();
		pose.mulPose(Vector3f.YP.rotationDegrees(tile.getRot()));
		this.renderItem(tile, tile.getInputItem(), 1.4F, 0F, 0.85F, info);
		this.renderItem(tile, tile.getBookItem(), 0.9F, 0F, 1.65F, info);
		this.renderItem(tile, tile.getPageItem(), 0.5F, 0F, 0.55F, info);
		pose.popPose();
	}

	public void renderItem(T tile, ItemStack stack, float x, float y, float z, RenderInfo info) {
		if (stack.isEmpty()) { return; }

		switch (tile.getFace()) {
		case SOUTH:
			x -= 2D;
			z -= 2D;
			break;
		case WEST:
			x -= 2D;
			break;
		case EAST:
			z -= 2D;
			break;
		}

		PoseStack pose = info.pose();
		pose.pushPose();
		pose.scale(0.5F, 0.5F, 0.5F);
		pose.translate(x, y + 2F, z);
		pose.mulPose(Vector3f.XP.rotationDegrees(90F));
		info.itemRender(stack);
		pose.popPose();
	}
}
