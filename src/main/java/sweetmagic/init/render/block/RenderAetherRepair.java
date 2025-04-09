package sweetmagic.init.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import sweetmagic.init.tile.sm.TileAetherRepair;
import sweetmagic.util.RenderUtil.RenderInfo;

public class RenderAetherRepair<T extends TileAetherRepair> extends RenderAbstractTile<T> {

	public RenderAetherRepair(BlockEntityRendererProvider.Context con) {
		super(con);
	}

	public void render(T tile, float parTick, RenderInfo info) {
		this.renderRepairStack(tile, tile.getInputItem(0), 0F, 0.2F, parTick, info);
		this.renderRepairStack(tile, tile.getInputItem(1), -0.2F, 0F, parTick, info);
		this.renderRepairStack(tile, tile.getInputItem(2), 0F, -0.2F, parTick, info);
		this.renderRepairStack(tile, tile.getInputItem(3), 0.2F, 0F, parTick, info);
	}

	public void renderRepairStack(T tile, ItemStack stack, float x, float z, float parTick, RenderInfo info) {
		if (stack.isEmpty()) { return; }

		PoseStack pose = info.pose();
		pose.pushPose();
		pose.mulPose(Vector3f.YP.rotationDegrees(tile.getRot()));

		switch (tile.getFace()) {
		case SOUTH:
			pose.translate(-1D, 0D, -1D);
			break;
		case WEST:
			pose.translate(-1D, 0D, 0D);
			break;
		case EAST:
			pose.translate(0D, 0D, -1D);
			break;
		}

		pose.translate(0.5D + x, 0.8D, 0.5D + z);
		int gameTime = tile.getClientTime();
		pose.translate(0D, Math.sin((gameTime + parTick) / 10F) * 0.05D, 0D);
		pose.scale(0.25F, 0.25F, 0.25F);
		float angle = (gameTime + parTick) / 20F * this.pi;
		pose.mulPose(Vector3f.YP.rotationDegrees(angle));
		info.itemRender(stack);
		pose.popPose();
	}
}
