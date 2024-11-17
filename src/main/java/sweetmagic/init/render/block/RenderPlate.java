package sweetmagic.init.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import sweetmagic.init.block.sm.Plate;
import sweetmagic.init.tile.sm.TilePlate;
import sweetmagic.util.RenderUtil;
import sweetmagic.util.RenderUtil.RenderInfo;

public class RenderPlate extends RenderAbstractTile<TilePlate> {


	public RenderPlate(BlockEntityRendererProvider.Context con) {
		super(con);
	}

	@Override
	public void render(TilePlate tile, float parTick, PoseStack pose, MultiBufferSource buf, int light, int overlayLight) {
		if (tile.getLevel() == null || tile.isAir()) { return; }

		int data = tile.getData();

		if (data == 0) {
			pose.pushPose();
			RenderUtil.renderBlock(tile.getLevel(), tile.getBlockPos(), tile.getBlockState(), this.bRender, pose, buf, overlayLight);
			pose.popPose();
		}

		ItemStack stack = tile.getInputItem(0);
		if (stack.isEmpty() && data != 3) { return; }

		RenderInfo renderInfo = new RenderInfo(this.iRender, light, overlayLight, pose, buf);

		switch (data) {
		case 0:
			this.renderPlateBlock(tile, stack, pose, buf, light, overlayLight);
			break;
		case 1:
			this.renderTrayBlock(tile, stack, renderInfo);
			break;
		case 2:
			this.renderBasketBlock(tile, stack, renderInfo);
			break;
		case 3:
			this.renderShowCase(tile, renderInfo);
			break;
		}
	}

	// 料理皿のレンダー
	public void renderPlateBlock (TilePlate tile, ItemStack stack, PoseStack pose, MultiBufferSource buf, int light, int overlayLight) {

		pose.pushPose();
		pose.translate(0.5D, 0.1D, 0.5D);
		pose.mulPose(Vector3f.YP.rotationDegrees(tile.getRot()));

		if (stack.getItem() instanceof BlockItem blockItem) {

			if (blockItem.getBlock() instanceof Plate plate) {
				this.renderPlate(tile, plate, stack, pose, buf, light, overlayLight);
			}

			else {
				this.renderBlock(stack, pose, buf, light, overlayLight);
			}
		}

		else {
			this.renderItem(stack, pose, buf, light, overlayLight);
		}

		pose.popPose();
	}

	// ウッドトレーのレンダー
	public void renderTrayBlock (TilePlate tile, ItemStack stack, RenderInfo renderInfo) {

		boolean isBlock = stack.getItem() instanceof BlockItem;
		double addY = isBlock ? -0.05D : 0D;
		double addZ = isBlock ? 0.275D : 0D;

		for (int x = 0; x < 2; x++)
			for (int z = 0; z < 2; z++)
				RenderUtil.renderItem(renderInfo, tile, stack, 1.825D - x * 1D, 0.775D + addY, 0.85D + z * 0.75D + addZ);

		PoseStack pose = renderInfo.getPose();

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
	public void renderBasketBlock (TilePlate tile, ItemStack stack, RenderInfo renderInfo) {

		boolean isBlock = stack.getItem() instanceof BlockItem;
		double addY = isBlock ? -0.05D : 0D;
		double addZ = isBlock ? 0.125D : 0D;
		for (int x = 0; x < 2; x++)
			for (int z = 0; z < 2; z++)
				RenderUtil.renderItem(renderInfo, tile, stack, 1.825D - x * 1D, 0.525D + addY, 1.075D + z * 0.75D + addZ);

		PoseStack pose = renderInfo.getPose();
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
	public void renderShowCase (TilePlate tile, RenderInfo renderInfo) {

        // インベントリ分描画
        for (int i = 0; i < tile.getInvSize(); i++) {
			ItemStack stack = tile.getInputItem(i);
			if (stack.isEmpty()) { continue; }

			boolean isBlock = stack.getItem() instanceof BlockItem;
			double addY = isBlock ? 0.15D : 0D;

			for (int x = 0; x < 2; x++) {
				for (int z = 0; z < 2; z++) {
					double pX = isBlock ? 1.675D - x * 0.675D : 1.8D - x;
					RenderUtil.renderItem(renderInfo, tile, stack, pX, 1.75D - i * 1.15D + addY, 1.25D + z * 0.75D);
				}
			}
        }
	}

	public void renderPlate (TilePlate tile, Plate plate, ItemStack stack, PoseStack pose, MultiBufferSource buf, int light, int overlayLight) {

        ModelBlockRenderer.enableCaching();
		pose.translate(-0.5D, -0.025D, -0.5D);
		int value = Math.min(stack.getCount(), 12);

		for (int i = 0; i < value; i++) {
			RenderUtil.renderBlock(tile.getLevel(), tile.getBlockPos(), plate.defaultBlockState(), this.bRender, pose, buf, overlayLight);
			pose.translate(0D, 0.075D, 0D);
		}
        ModelBlockRenderer.clearCache();
	}

	public void renderItem (ItemStack stack, PoseStack pose, MultiBufferSource buf, int light, int overlayLight) {
		pose.scale(0.675F, 0.675F, 0.675F);
		pose.translate(0F, 0.25F, 0F);
		this.iRender.renderStatic(stack, ItemTransforms.TransformType.FIXED, light, OverlayTexture.NO_OVERLAY, pose, buf, 0);
	}

	public void renderBlock (ItemStack stack, PoseStack pose, MultiBufferSource buf, int light, int overlayLight) {
		pose.translate(0D, 0D, 0D);
		pose.scale(0.75F, 0.75F, 0.75F);
		pose.mulPose(Vector3f.XN.rotationDegrees(-90F));
		this.iRender.renderStatic(stack, ItemTransforms.TransformType.FIXED, light, OverlayTexture.NO_OVERLAY, pose, buf, 0);
	}

	public void rotPosFix (PoseStack pose, Direction face) {
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
