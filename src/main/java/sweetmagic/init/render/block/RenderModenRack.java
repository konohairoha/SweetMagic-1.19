package sweetmagic.init.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.ItemStackHandler;
import sweetmagic.init.block.base.BaseCookBlock;
import sweetmagic.init.block.sm.Plate;
import sweetmagic.init.tile.sm.TileModenRack;
import sweetmagic.util.RenderUtil;
import sweetmagic.util.RenderUtil.RenderInfo;

@OnlyIn(Dist.CLIENT)
public class RenderModenRack extends RenderAbstractTile<TileModenRack> {

	public RenderModenRack(BlockEntityRendererProvider.Context con) {
		super(con);
	}

	@Override
	public void render(TileModenRack tile, float parTick, PoseStack pose, MultiBufferSource buf, int light, int overlayLight) {
		if (tile.getLevel() == null || tile.isAir()) { return; }

		// 未料理徐状態なら終了
		int data = tile.getData();
		RenderInfo renderInfo = new RenderInfo(this.iRender, light, OverlayTexture.NO_OVERLAY, pose, buf);

		switch(data) {
		case 0:
			this.renderModenRack(tile, renderInfo);
			break;
		case 1:
			this.renderWallRack(tile, renderInfo);
			break;
		case 2:
			this.renderWallShelf(tile, renderInfo);
			break;
		case 3:
			this.renderWallPartition(tile, renderInfo);
			break;
		case 4:
			this.renderBottleRack(tile, renderInfo);
			break;
		case 5:
			this.renderCeilingShelf(tile, renderInfo);
			break;
		}
	}

	public void renderModenRack (TileModenRack tile, RenderInfo renderInfo) {

        // インベントリ分描画
        for (int i = 0; i < tile.getInvSize(); i++) {

			ItemStack stack = tile.getInputItem(i);
			if (stack.isEmpty()) { continue; }

			boolean isBlock = stack.getItem() instanceof BlockItem;
			double addZ = isBlock ? 0.35D + ((int) (i / 3) + (i > 8 ? -3 : 0)) * -0.175D : 0D;
			double x = 2.075D - 0.75D * i + ((int) (i / 3) * 2.25D);
			double y = 3.2D + ((int) (i / 9) * -1.41D);
			double z = 0.5D + addZ + ((int) (i / 3) * 0.815D) + ((int) (i / 9) * -2.45D);

			RenderUtil.renderItem(renderInfo, tile, stack, x, y, z);
		}
	}

	public void renderWallRack (TileModenRack tile, RenderInfo renderInfo) {

        // インベントリ分描画
        for (int i = 0; i < tile.getInvSize(); i++) {
			ItemStack stack = tile.getInputItem(i);
			if (stack.isEmpty()) { continue; }

			RenderUtil.renderItem(renderInfo, tile, stack, 2.075D - 0.75D * i, 3.2D, 1.75D);
		}
	}

	public void renderWallShelf (TileModenRack tile, RenderInfo renderInfo) {

        // インベントリ分描画
        for (int i = 0; i < tile.getInvSize(); i++) {
			ItemStack stack = tile.getInputItem(i);
			if (stack.isEmpty()) { continue; }

			RenderUtil.renderItem(renderInfo, tile, stack, 2.075D - 0.75D * i + (i > 2 ? 2.25D : 0D), 3.2D - (i > 2 ? 1.375D : 0D), 2D);
		}
	}

	public void renderWallPartition (TileModenRack tile, RenderInfo renderInfo) {

        // インベントリ分描画
        for (int i = 0; i < tile.getInvSize(); i++) {

			ItemStack stack = tile.getInputItem(i);
			if (stack.isEmpty()) { continue; }

			boolean isBlock = stack.getItem() instanceof BlockItem;
			double x = 1.9875D - 0.65D * i + ((int) (i / 3) * 1.95D);
			double y = 1.95D + ((int) (i / 3) * -1.25D) + (i > 5 ? 2.5D : 0D) + (isBlock ? 0.175D : 0D);
			double z = 2.415D + ((int) (i / 6) * 0.275D) + ( (isBlock ? -0.325D : 0D) * (i > 5 ? -0.1D : 1D) );
			RenderUtil.renderItem(renderInfo, tile, stack, x, y, z);
		}
	}

	public void renderBottleRack (TileModenRack tile, RenderInfo renderInfo) {

        // インベントリ分描画
        for (int i = 0; i < tile.getInvSize(); i++) {

			ItemStack stack = tile.getInputItem(i);
			if (stack.isEmpty()) { continue; }

			boolean isBlock = stack.getItem() instanceof BlockItem;
			double addZ = isBlock ? 0.35D + ((int) (i / 3) + (i > 8 ? -3 : 0)) * -0.175D : 0D;

			for (int k = 0; k < 3; k++) {
				double x = 2.075D - k * 0.75D;
				double y = 1.85D + (i * -1.25D);
				double z = 2D + addZ;
				RenderUtil.renderItem(renderInfo, tile, stack, x, y, z);
			}
		}
	}

	public void renderCeilingShelf (TileModenRack tile, RenderInfo renderInfo) {

		PoseStack pose = renderInfo.getPose();

        // インベントリ分描画
        for (int i = 0; i < tile.getInvSize(); i++) {

			ItemStack stack = tile.getInputItem(i);
			if (stack.isEmpty()) { continue; }

			pose.pushPose();
			boolean isBlock = stack.getItem() instanceof BlockItem bItem;

			if (isBlock) {

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

				pose.translate(0D, 0.06D + 0.5D * -(i - 1), 0D);
				Block block = ((BlockItem) stack.getItem()).getBlock();
		        ModelBlockRenderer.enableCaching();

				if (block instanceof Plate plate) {

					int size = 1;

					// 皿に保持した皿の数分描画
					if (stack.getOrCreateTag().contains("BlockEntityTag")) {
						ItemStackHandler inputInv = new ItemStackHandler(1);
						inputInv.deserializeNBT(stack.getTagElement("BlockEntityTag"));
						size += Math.min(5, inputInv.getStackInSlot(0).getCount());
					}

					else if (stack.getCount() > 1) {
						size += stack.getCount() - 1;
					}

					for (int k = 0; k < size; k++) {
						RenderUtil.renderBlock(tile.getLevel(), tile.getBlockPos(), plate.defaultBlockState(), this.bRender, pose, renderInfo.getBuf(), renderInfo.getOverlayLight());
						pose.translate(0D, 0.075D, 0D);
					}
				}

				else {

					BlockState state = block.defaultBlockState();
					if (state.hasProperty(BaseCookBlock.COOK)) {
						state = state.setValue(BaseCookBlock.COOK, 1);
					}

					RenderUtil.renderBlock(tile.getLevel(), tile.getBlockPos(), state, this.bRender, pose, renderInfo.getBuf(), renderInfo.getOverlayLight());
				}

		        ModelBlockRenderer.clearCache();
			}

			else {

				switch (tile.getFace()) {
				case NORTH:
					pose.translate(1D, 0D, 0D);
					break;
				case SOUTH:
					pose.translate(0D, 0D, -1D);
					break;
				case WEST:
					pose.translate(1D, 0D, -1D);
					break;
				}

				pose.translate(-0.5D, 0.21D + 0.5D * -(i - 1), 0.5D);
				RenderUtil.renderItem(renderInfo, tile, stack, 0D, 0D, 0D);
			}

			pose.popPose();
		}
	}
}
