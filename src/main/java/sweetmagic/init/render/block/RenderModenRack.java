package sweetmagic.init.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.FlowerBlock;
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
public class RenderModenRack<T extends TileModenRack> extends RenderAbstractTile<T> {

	public RenderModenRack(BlockEntityRendererProvider.Context con) {
		super(con);
	}

	public void render(T tile, float parTick, RenderInfo info) {

		// 未料理徐状態なら終了
		int data = tile.getData();

		switch(data) {
		case 0:
			this.renderModenRack(tile, info);
			break;
		case 1:
			this.renderWallRack(tile, info);
			break;
		case 2:
			this.renderWallShelf(tile, info);
			break;
		case 3:
			this.renderWallPartition(tile, info);
			break;
		case 4:
			this.renderBottleRack(tile, info);
			break;
		case 5:
			this.renderCeilingShelf(tile, info);
			break;
		case 6:
			this.renderFruitCrate(tile, info);
			break;
		case 7:
			this.renderFruitCrateBox(tile, info);
			break;
		case 8:
			this.renderToolBox(tile, info);
			break;
		case 9:
			this.renderBoxShelf(tile, info);
			break;
		}
	}

	public void renderModenRack(T tile, RenderInfo info) {

		// インベントリ分描画
		for (int i = 0; i < tile.getInvSize(); i++) {
			ItemStack stack = tile.getInputItem(i);
			if (stack.isEmpty()) { continue; }

			boolean isBlock = stack.getItem() instanceof BlockItem;
			double addZ = isBlock ? 0.35D + ((int) (i / 3) + (i > 8 ? -3 : 0)) * -0.175D : 0D;
			double x = 2.075D - 0.75D * i + ((int) (i / 3) * 2.25D);
			double y = 3.2D + ((int) (i / 9) * -1.41D);
			double z = 0.5D + addZ + ((int) (i / 3) * 0.815D) + ((int) (i / 9) * -2.45D);

			RenderUtil.renderItem(info, tile, stack, x, y, z);
		}
	}

	public void renderWallRack(T tile, RenderInfo info) {

		// インベントリ分描画
		for (int i = 0; i < tile.getInvSize(); i++) {
			ItemStack stack = tile.getInputItem(i);
			if (stack.isEmpty()) { continue; }

			RenderUtil.renderItem(info, tile, stack, 2.075D - 0.75D * i, 3.2D, 1.75D);
		}
	}

	public void renderWallShelf(T tile, RenderInfo info) {

		// インベントリ分描画
		for (int i = 0; i < tile.getInvSize(); i++) {
			ItemStack stack = tile.getInputItem(i);
			if (stack.isEmpty()) { continue; }

			RenderUtil.renderItem(info, tile, stack, 2.075D - 0.75D * i + (i > 2 ? 2.25D : 0D), 3.2D - (i > 2 ? 1.375D : 0D), 2D);
		}
	}

	public void renderWallPartition(T tile, RenderInfo info) {

		// インベントリ分描画
		for (int i = 0; i < tile.getInvSize(); i++) {
			ItemStack stack = tile.getInputItem(i);
			if (stack.isEmpty()) { continue; }

			boolean isBlock = stack.getItem() instanceof BlockItem;
			double x = 1.9875D - 0.65D * i + ((int) (i / 3) * 1.95D);
			double y = 1.95D + ((int) (i / 3) * -1.25D) + (i > 5 ? 2.5D : 0D) + (isBlock ? 0.175D : 0D);
			double z = 2.415D + ((int) (i / 6) * 0.275D) + ( (isBlock ? -0.325D : 0D) * (i > 5 ? -0.1D : 1D) );
			RenderUtil.renderItem(info, tile, stack, x, y, z);
		}
	}

	public void renderBottleRack(T tile, RenderInfo info) {

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
				RenderUtil.renderItem(info, tile, stack, x, y, z);
			}
		}
	}

	public void renderCeilingShelf(T tile, RenderInfo info) {

		PoseStack pose = info.pose();

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
						RenderUtil.renderBlock(tile.getLevel(), tile.getBlockPos(), plate.defaultBlockState(), this.bRender, pose, info.buf(), info.overlay());
						pose.translate(0D, 0.075D, 0D);
					}
				}

				else {

					BlockState state = block.defaultBlockState();
					if (state.hasProperty(BaseCookBlock.COOK)) {
						state = state.setValue(BaseCookBlock.COOK, 1);
					}

					RenderUtil.renderBlock(tile.getLevel(), tile.getBlockPos(), state, this.bRender, pose, info.buf(), info.overlay());
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
				RenderUtil.renderItem(info, tile, stack, 0D, 0D, 0D);
			}

			pose.popPose();
		}
	}

	public void renderFruitCrate(T tile, RenderInfo info) {

		// インベントリ分描画
		for (int i = 0; i < tile.getInvSize(); i++) {
			ItemStack stack = tile.getInputItem(i);
			if (stack.isEmpty()) { continue; }

			double x = 1.35D - (i / 4) * 0.75D;
			int id = i >= 4 ? i - 4 : i;
			double y = 0.65D + id * 0.25D;
			double z = 0.15D + id * 0.3D;
			float scale = 1.35F;

			if(stack.getItem() instanceof BlockItem bItem) {

				Block block = bItem.getBlock();
				if( (block instanceof FlowerBlock || block instanceof BushBlock) && !(bItem instanceof ItemNameBlockItem)) {
					scale = 1.75F;
					x = 1D - (i / 4) * 0.475D;
					y = 0.65D + id * 0.105D;
					z = 0.15D + id * 0.25D;
				}
			}

			RenderUtil.renderItem(info, tile, stack, x, y, z, scale, true);
		}
	}

	public void renderFruitCrateBox(T tile, RenderInfo info) {

		// インベントリ分描画
		for (int i = 0; i < tile.getInvSize(); i++) {
			ItemStack stack = tile.getInputItem(i);
			if (stack.isEmpty()) { continue; }

			double x = 1.3D - (i / 4) * 0.625D;
			int id = i >= 4 ? i - 4 : i;
			double y = 0.6D;
			double z = 0.45D + id * 0.35D;
			float scale = 1.35F;

			if(stack.getItem() instanceof BlockItem bItem) {

				Block block = bItem.getBlock();
				if ((block instanceof FlowerBlock || block instanceof BushBlock) && !(bItem instanceof ItemNameBlockItem)) {
					scale = 1.75F;
					x = 1D - (i / 4) * 0.475D;
					z = 0.4D + id * 0.25D;
				}
			}

			RenderUtil.renderItem(info, tile, stack, x, y, z, scale, true);
		}
	}

	public void renderToolBox(T tile, RenderInfo info) {

		PoseStack pose = info.pose();

		// インベントリ分描画
		for (int i = 0; i < tile.getInvSize(); i++) {
			ItemStack stack = tile.getInputItem(i);
			if (stack.isEmpty()) { continue; }

			pose.pushPose();
			pose.mulPose(Vector3f.YP.rotationDegrees(tile.getRot()));
			pose.translate(0.5D, 0.9D, 0.25D + i * 0.175D);

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

			pose.mulPose(Vector3f.ZP.rotationDegrees(-225F));
			pose.scale(4F, 4F, 4F);
			pose.scale(0.375F, 0.375F, 0.375F);
			info.render().renderStatic(stack, ItemTransforms.TransformType.FIXED, info.light(), info.overlay(), pose, info.buf(), 0);
			pose.popPose();
		}
	}

	public void renderBoxShelf(T tile, RenderInfo info) {

		// インベントリ分描画
		for (int i = 0; i < tile.getInvSize(); i++) {
			ItemStack stack = tile.getInputItem(i);
			if (stack.isEmpty()) { continue; }

			boolean isBlock = stack.getItem() instanceof BlockItem block;
			double x = isBlock ? 1.875D - 0.55D * i : 2.05D - 0.7D * i;
			double y = 0.5D;
			double z = (isBlock ? 1.8D : 1.6D) + ((int) (i / 3) * 0.815D);

			RenderUtil.renderItem(info, tile, stack, x, y, z);
		}
	}
}
