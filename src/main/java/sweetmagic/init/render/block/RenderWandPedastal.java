package sweetmagic.init.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.block.Block;
import sweetmagic.api.iitem.IWand;
import sweetmagic.init.BlockInit;
import sweetmagic.init.item.magic.MFStuff;
import sweetmagic.init.tile.sm.TileWandPedastal;
import sweetmagic.util.RenderUtil;
import sweetmagic.util.RenderUtil.RenderColor;

public class RenderWandPedastal extends RenderAbstractTile<TileWandPedastal> {

	private static final Block SQUARE_BLOCK_L = BlockInit.magic_square_l_blank;
	private static final Block SQUARE_BLOCK_S = BlockInit.magic_square_s_blank;

	public RenderWandPedastal(BlockEntityRendererProvider.Context con) {
		super(con);
	}

	@Override
	public void render(TileWandPedastal tile, float parTick, PoseStack pose, MultiBufferSource buf, int light, int overlayLight) {
		if (tile.getLevel() == null || tile.isAir()) { return; }

		ItemStack stack = tile.getInputItem(0);
		if (stack.isEmpty()) { return; }

		int data = tile.getData();
		pose.pushPose();
		pose.translate(0.5D, 0.5D, 0.5D);
		pose.mulPose(Vector3f.YP.rotationDegrees(tile.getRot()));

		switch(data) {
		case 0:
			this.renderWandPedastal(stack, pose, buf, light, overlayLight);
			break;
		case 1:
			this.renderWallBoard(stack, pose, buf, light, overlayLight);
			break;
		case 2:
			this.renderShopBoard(stack, pose, buf, light, overlayLight);
			break;
		case 3:
			this.renderItemMenu(stack, pose, buf, light, overlayLight);
			break;
		case 4:
			this.renderdecorativeStand(tile, parTick, stack, pose, buf, light, overlayLight);
			break;
		}

		pose.popPose();

		if (data == 4) {
			pose.pushPose();
			pose.translate(0.5D, 0.5D, 0.5D);
			pose.mulPose(Vector3f.YP.rotationDegrees(tile.getRot()));
			this.renderdecorativeStandSquare(tile, parTick, stack, pose, buf, light, overlayLight);
			pose.popPose();
		}
	}

	public void renderWandPedastal (ItemStack stack, PoseStack pose, MultiBufferSource buf, int light, int overlayLight) {

		Item item = stack.getItem();

		if (item instanceof IWand || item instanceof DiggerItem || item instanceof MFStuff) {
			pose.translate(0D, 0.15D, 0D);
			pose.mulPose(Vector3f.ZP.rotationDegrees(-45F));
		}

		else if (item instanceof SwordItem) {
			pose.translate(0D, 0.1D, 0D);
			pose.mulPose(Vector3f.ZP.rotationDegrees(135F));
		}

		this.iRender.renderStatic(stack, ItemTransforms.TransformType.FIXED, light, overlayLight, pose, buf, 0);
	}

	public void renderWallBoard (ItemStack stack, PoseStack pose, MultiBufferSource buf, int light, int overlayLight) {
		pose.translate(0D, 0D, 0.45D);
		pose.scale(0.75F, 0.75F, 0.75F);
		this.iRender.renderStatic(stack, ItemTransforms.TransformType.FIXED, light, overlayLight, pose, buf, 0);
	}

	public void renderShopBoard (ItemStack stack, PoseStack pose, MultiBufferSource buf, int light, int overlayLight) {
		pose.translate(0D, -0.1D, 0D);
		pose.scale(0.5F, 0.5F, 0.5F);
		pose.mulPose(Vector3f.YN.rotationDegrees(90F));
		this.iRender.renderStatic(stack, ItemTransforms.TransformType.FIXED, light, overlayLight, pose, buf, 0);
	}

	public void renderItemMenu (ItemStack stack, PoseStack pose, MultiBufferSource buf, int light, int overlayLight) {
		pose.translate(0D, 0D, 0.475D);
		pose.scale(0.55F, 0.55F, 0.55F);
		this.iRender.renderStatic(stack, ItemTransforms.TransformType.FIXED, light, overlayLight, pose, buf, 0);
	}

	public void renderdecorativeStand (TileWandPedastal tile, float parTick, ItemStack stack, PoseStack pose, MultiBufferSource buf, int light, int overlayLight) {

		Item item = stack.getItem();

		double shake = 0.05D;
		long gameTime = tile.getTime();
		float rotY = (gameTime + parTick) / 30F;
		pose.mulPose(Vector3f.YP.rotationDegrees(rotY * this.pi + 360));
		pose.translate(0D, Math.sin((gameTime + parTick) / 10F) * shake, 0D);

		if (item instanceof IWand || item instanceof MFStuff) {
			pose.translate(0D, 0.25D, 0D);
			pose.mulPose(Vector3f.ZP.rotationDegrees(-45F));
		}

		else if (item instanceof DiggerItem || item instanceof SwordItem) {
			pose.translate(0D, 0.45D, 0D);
			pose.mulPose(Vector3f.ZP.rotationDegrees(-45F));
		}

		else if (item instanceof BlockItem) {
			pose.translate(0D, 0.15D, 0D);
		}

		else {
			pose.scale(0.67F, 0.67F, 0.67F);
			pose.translate(0D, 0.25D, 0D);
		}

		this.iRender.renderStatic(stack, ItemTransforms.TransformType.FIXED, light, overlayLight, pose, buf, 0);
	}

	public void renderdecorativeStandSquare (TileWandPedastal tile, float parTick, ItemStack stack, PoseStack pose, MultiBufferSource buf, int light, int overlayLight) {

		long gameTime = tile.getTime();
		pose.translate(0D, Math.sin((gameTime + parTick) / 15F) * 0.02D + 0.35D, 0D);
		pose.scale(2F, 2F, 2F);
		float angle = -(gameTime + parTick) / 20.0F * this.pi;
		pose.mulPose(Vector3f.YP.rotationDegrees(angle));
		pose.translate(-0.25D, -0.25D, -0.25D);
		pose.scale(0.5F, 0.5F, 0.5F);

		RenderColor color = new RenderColor(76F / 255F, 165F / 255F, 1F, light, overlayLight);
		RenderUtil.renderBlock(pose, buf, color, SQUARE_BLOCK_L);
		pose.translate(0.3D, 0.65D, 0.3D);
		pose.scale(0.4F, 0.4F, 0.4F);
		RenderUtil.renderBlock(pose, buf, color, SQUARE_BLOCK_S);
	}
}
