package sweetmagic.init.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import sweetmagic.init.ItemInit;
import sweetmagic.init.tile.sm.TileFurnitureTable;

public class RenderFurnitureTable extends RenderAbstractTile<TileFurnitureTable> {

	private static final ItemStack PICK = new ItemStack(ItemInit.silverhammer);

	public RenderFurnitureTable(BlockEntityRendererProvider.Context con) {
		super(con);
	}

	@Override
	public void render(TileFurnitureTable tile, float parTick, PoseStack pose, MultiBufferSource buf, int light, int overlayLight) {
		if (tile.getLevel() == null || tile.isAir()) { return; }
		this.renderInv(tile, pose, buf, light, overlayLight);
		this.renderTool(tile, pose, buf, light, overlayLight);
	}

	public void renderInv (TileFurnitureTable tile, PoseStack pose, MultiBufferSource buf, int light, int overlayLight) {

		Direction face = tile.getFace();

		for (int i = 0; i < tile.getInvSize(); i++) {

			ItemStack out = tile.getOutItem(i);
			if (out.isEmpty()) { continue; }

			pose.pushPose();
			pose.mulPose(Vector3f.YP.rotationDegrees(tile.getRot()));

			double addX = 0;
			double addZ = 0;

			switch (face) {
			case WEST:
				addX = i * -0.2D + (i / 9) * + 1.8D - 1D;
				addZ = (i / 9) * -0.2D;
				break;
			case EAST:
				addX = i * -0.2D + (i / 9) * + 1.8D;
				addZ = (i / 9) * -0.2D - 1D;
				break;
			case NORTH:
				addX = i * -0.2D + (i / 9) * + 1.8D;
				addZ = (i / 9) * -0.2D;
				break;
			case SOUTH:
				addX = i * -0.2D + (i / 9) * + 1.8D - 1D;
				addZ = (i / 9) * -0.2D - 1D;
				break;
			}

			pose.translate(0.7875D + addX, 0.275D, 0.7D + addZ);
			pose.scale(0.33F, 0.33F, 0.33F);
			this.iRender.renderStatic(out, ItemTransforms.TransformType.FIXED, light, OverlayTexture.NO_OVERLAY, pose, buf, 0);
			pose.popPose();
		}
	}

	public void renderTool (TileFurnitureTable tile, PoseStack pose, MultiBufferSource buf, int light, int overlayLight) {

		Direction face = tile.getFace();

		pose.pushPose();
		pose.mulPose(Vector3f.YP.rotationDegrees(tile.getRot()));

		double addX = 0;
		double addZ = 0;

		switch (face) {
		case WEST:
			addX = -1D;
			break;
		case EAST:
			addZ = -1D;
			break;
		case SOUTH:
			addX = addZ = -1D;
			break;
		}

		pose.translate(-0.5D + addX, 1.01D, 0.325D + addZ);
		pose.scale(0.5F, 0.5F, 0.5F);
		pose.mulPose(Vector3f.XP.rotationDegrees(90F));
		this.iRender.renderStatic(PICK, ItemTransforms.TransformType.FIXED, light, OverlayTexture.NO_OVERLAY, pose, buf, 0);
		pose.popPose();
	}
}
