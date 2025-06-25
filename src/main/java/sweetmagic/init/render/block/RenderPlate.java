package sweetmagic.init.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import sweetmagic.init.block.sm.Plate;
import sweetmagic.init.tile.sm.TilePlate;
import sweetmagic.util.RenderUtil;
import sweetmagic.util.RenderUtil.RenderInfo;

public class RenderPlate<T extends TilePlate> extends RenderAbstractTile<T> {

	public RenderPlate(BlockEntityRendererProvider.Context con) {
		super(con);
	}

	public void render(T tile, float parTick, RenderInfo info) {

		int data = tile.getData();
		PoseStack pose = info.pose();

		if (data == 0) {
			pose.pushPose();
			RenderUtil.renderBlock(tile.getLevel(), tile.getBlockPos(), tile.getBlockState(), this.bRender, pose, info.buf(), info.overlay());
			pose.popPose();
		}

		ItemStack stack = tile.getInputItem(0);
		if (stack.isEmpty() && data != 3) { return; }

		switch (data) {
		case 0:
			this.renderPlateBlock(tile, stack, info);
			break;
		case 1:
			this.renderTrayBlock(tile, stack, info);
			break;
		case 2:
			this.renderBasketBlock(tile, stack, info);
			break;
		case 3:
			this.renderShowCase(tile, info);
			break;
		case 4:
			this.renderDigTrayBlock(tile, stack, info);
			break;
		}
	}

	// 料理皿のレンダー
	public void renderPlateBlock(T tile, ItemStack stack, RenderInfo info) {

		PoseStack pose = info.pose();
		pose.pushPose();
		pose.translate(0.5D, 0.1D, 0.5D);
		pose.mulPose(Vector3f.YP.rotationDegrees(tile.getRot()));

		if (stack.getItem() instanceof BlockItem blockItem) {

			if (blockItem.getBlock() instanceof Plate plate) {
				this.renderPlate(tile, plate, stack, info);
			}

			else {
				this.renderBlock(stack, info);
			}
		}

		else {
			this.renderItem(stack, info);
		}

		pose.popPose();
	}

	// ウッドトレーのレンダー
	public void renderTrayBlock(T tile, ItemStack stack, RenderInfo info) {

		boolean isBlock = stack.getItem() instanceof BlockItem;
		double addY = isBlock ? -0.05D : 0D;
		double addZ = isBlock ? 0.275D : 0D;

		for (int x = 0; x < 2; x++)
			for (int z = 0; z < 2; z++)
				RenderUtil.renderItem(info, tile, stack, 1.825D - x * 1D, 0.775D + addY, 0.85D + z * 0.75D + addZ);

		PoseStack pose = info.pose();
		pose.pushPose();
		pose.mulPose(Vector3f.YP.rotationDegrees(tile.getRot()));
		this.rotPosFix(pose, tile.getFace());
		pose.translate(0.5D, 0.74D, 0.955D);
		pose.scale(-0.01F, -0.01F, 0.01F);
		int nameSize = this.font.width(stack.getHoverName().getString());
		float f3 = (float) (-nameSize / 2F);
		pose.scale(nameSize < 45F ? 1F : 45F / nameSize, 1F, 1F);
		this.font.draw(pose, stack.getHoverName(), f3, 0.5F, 0x000000);
		pose.popPose();
	}

	// バスケットのレンダー
	public void renderBasketBlock(T tile, ItemStack stack, RenderInfo info) {

		boolean isBlock = stack.getItem() instanceof BlockItem;
		double addY = isBlock ? -0.05D : 0D;
		double addZ = isBlock ? 0.125D : 0D;
		for (int x = 0; x < 2; x++)
			for (int z = 0; z < 2; z++)
				RenderUtil.renderItem(info, tile, stack, 1.825D - x * 1D, 0.525D + addY, 1.075D + z * 0.75D + addZ);

		PoseStack pose = info.pose();
		pose.pushPose();
		pose.mulPose(Vector3f.YP.rotationDegrees(tile.getRot()));
		this.rotPosFix(pose, tile.getFace());
		pose.translate(0.5D, 0.145D, 0.0445D);
		pose.scale(-0.01F, -0.01F, 0.01F);
		int nameSize = this.font.width(stack.getHoverName().getString());
		float f3 = (float) (-nameSize / 2F);
		pose.scale(nameSize < 45F ? 1F : 45F / nameSize, 1F, 1F);
		this.font.draw(pose, stack.getHoverName(), f3 + 0.75F, 0.5F, 0xFFFFFF);
		pose.popPose();
	}

	// ショケースのレンダー
	public void renderShowCase(T tile, RenderInfo info) {

		// インベントリ分描画
		for (int i = 0; i < tile.getInvSize(); i++) {
			ItemStack stack = tile.getInputItem(i);
			if (stack.isEmpty()) { continue; }

			boolean isBlock = stack.getItem() instanceof BlockItem;
			double addY = isBlock ? 0.15D : 0D;

			for (int x = 0; x < 2; x++) {
				for (int z = 0; z < 2; z++) {
					double pX = isBlock ? 1.675D - x * 0.675D : 1.8D - x;
					RenderUtil.renderItem(info, tile, stack, pX, 1.75D - i * 1.15D + addY, 1.25D + z * 0.75D);
				}
			}
		}
	}

	public void renderPlate(T tile, Plate plate, ItemStack stack, RenderInfo info) {

		ModelBlockRenderer.enableCaching();
		PoseStack pose = info.pose();
		pose.translate(-0.5D, -0.025D, -0.5D);
		int value = Math.min(stack.getCount(), 12);

		for (int i = 0; i < value; i++) {
			RenderUtil.renderBlock(tile.getLevel(), tile.getBlockPos(), plate.defaultBlockState(), this.bRender, pose, info.buf(), info.overlay());
			pose.translate(0D, 0.075D, 0D);
		}
		ModelBlockRenderer.clearCache();
	}

	// 斜めウッドトレーのレンダー
	public void renderDigTrayBlock(T tile, ItemStack stack, RenderInfo info) {

		boolean isBlock = stack.getItem() instanceof BlockItem;
		double addY = isBlock ? -0.05D : 0D;
		double addZ = isBlock ? 0.125D : 0D;
		for (int x = 0; x < 2; x++)
			for (int z = 0; z < 2; z++)
				RenderUtil.renderItem(info, tile, stack, 1.825D - x * 1D, 0.8675D + addY + z * 0.45D, 1.075D + z * 0.75D + addZ);

		PoseStack pose = info.pose();
		pose.pushPose();
		pose.mulPose(Vector3f.YP.rotationDegrees(tile.getRot()));
		this.rotPosFix(pose, tile.getFace());
		pose.translate(0.5D, 0.145D, 0.0445D);
		pose.scale(-0.01F, -0.01F, 0.01F);
		int nameSize = this.font.width(stack.getHoverName().getString());
		float f3 = (float) (-nameSize / 2F);
		pose.scale(nameSize < 45F ? 1F : 45F / nameSize, 1F, 1F);
		this.font.draw(pose, stack.getHoverName(), f3 + 0.75F, 0.5F, 0xFFFFFF);
		pose.popPose();
	}

	public void renderItem(ItemStack stack, RenderInfo info) {
		PoseStack pose = info.pose();
		pose.scale(0.675F, 0.675F, 0.675F);
		pose.translate(0F, 0.25F, 0F);
		info.itemRenderNo(stack);
	}

	public void renderBlock(ItemStack stack, RenderInfo info) {
		PoseStack pose = info.pose();
		pose.translate(0D, 0D, 0D);
		pose.scale(0.75F, 0.75F, 0.75F);
		pose.mulPose(Vector3f.XN.rotationDegrees(-90F));
		info.itemRenderNo(stack);
	}

	public void rotPosFix(PoseStack pose, Direction face) {
		switch (face) {
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
	}

	public int getViewDistance() {
		return 24;
	}
}
