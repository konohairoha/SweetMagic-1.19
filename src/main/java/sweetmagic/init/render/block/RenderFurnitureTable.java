package sweetmagic.init.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import sweetmagic.init.ItemInit;
import sweetmagic.init.tile.sm.TileFurnitureTable;
import sweetmagic.util.RenderUtil.RenderInfo;

public class RenderFurnitureTable<T extends TileFurnitureTable> extends RenderAbstractTile<T> {

	private static final ItemStack PICK = new ItemStack(ItemInit.silverhammer);

	public RenderFurnitureTable(BlockEntityRendererProvider.Context con) {
		super(con);
	}

	public void render(T tile, float parTick, RenderInfo info) {
		this.renderInv(tile, info);
		this.renderTool(tile, info);
	}

	public void renderInv(T tile, RenderInfo info) {

		Direction face = tile.getFace();
		PoseStack pose = info.pose();

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
			info.itemRender(out);
			pose.popPose();
		}
	}

	public void renderTool(T tile, RenderInfo info) {

		PoseStack pose = info.pose();
		pose.pushPose();
		pose.mulPose(Vector3f.YP.rotationDegrees(tile.getRot()));

		double addX = 0;
		double addZ = 0;

		switch (tile.getFace()) {
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
		info.itemRender(PICK);
		pose.popPose();
	}
}
