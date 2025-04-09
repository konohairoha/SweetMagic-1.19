package sweetmagic.init.render.block;

import java.util.Arrays;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import sweetmagic.init.BlockInit;
import sweetmagic.init.TagInit;
import sweetmagic.init.tile.sm.TileAltarCreatStar;
import sweetmagic.util.RenderUtil;
import sweetmagic.util.RenderUtil.RenderColor;
import sweetmagic.util.RenderUtil.RenderInfo;

public class RenderAltarCreatStar<T extends TileAltarCreatStar> extends RenderAbstractTile<T> {

	private static final float size = 0.45F;
	private static final Block SQUARE = BlockInit.magic_square_l;
	private static final Block SQUARE_BLANK = BlockInit.magic_square_l_blank;

	public RenderAltarCreatStar(BlockEntityRendererProvider.Context con) {
		super(con);
	}

	public void render(T tile, float parTick, RenderInfo info) {

		if (!tile.isHaveBlock || !tile.isRangeBlock) {

			if (!tile.isHaveBlock) {
				this.renderHaveBlock(tile, parTick, info);
			}

			if (!tile.isRangeBlock) {

				BlockPos pos = tile.getBlockPos().below();

				List<BlockPos> dcPosList = Arrays.<BlockPos> asList(
					pos.north(), pos.south(), pos.east(), pos.west()
				);

				List<BlockPos> acPosList = Arrays.<BlockPos> asList(
					pos.north(2), pos.south(2), pos.east(2), pos.west(2),
					pos.offset(-1, 0, -1), pos.offset(1, 0, -1), pos.offset(-1, 0, 1), pos.offset(1, 0, 1)
				);

				Block dc = tile.getRangeBlock();
				Block ac = tile.getOverRangeBlock();
				dcPosList.forEach(p -> this.renderRangeBlock(tile, parTick, info, p, TagInit.DC_BLOCK, dc));
				acPosList.forEach(p -> this.renderRangeBlock(tile, parTick, info, p, TagInit.AC_BLOCK, ac));
			}
		}

		else if (tile.isCraft) {

			int count = tile.craftList.size() - 1;
			int nowTick = tile.nowTick * ( !tile.quickCraft ? 1 : 2 );
			float posY = 1F + nowTick * 0.0065F;
			int gameTime = tile.getClientTime();
			float rotY = (gameTime + parTick) / 90F;
			PoseStack pose = info.pose();

			for (int i = 1; i < count + 1; i++) {

				ItemStack stack = tile.craftList.get(i);
				if (stack.isEmpty()) { continue; }

				pose.pushPose();
				pose.translate(0.5F, posY, 0.5F);
				pose.translate(0, Math.sin((gameTime + parTick) / 10F) * 0.15F + 0.2F, 0);
				pose.mulPose(Vector3f.YP.rotationDegrees(rotY * this.pi + (i * (360 / count)) + nowTick * 6.75F));
				pose.scale(size, size, size);
				pose.translate(1F - (0.0055F * nowTick) , 0F, 0F);
				info.itemRender(stack);
				pose.popPose();
			}

			ItemStack stack = tile.craftList.get(0);

			float rot = gameTime % 360;
			pose.pushPose();
			pose.translate(0.5F, posY, 0.5F);
			pose.translate(0, Math.sin((gameTime + parTick) * 0.1F) * 0.15F + 0.2F, 0);
			pose.scale(size, size, size);
			pose.mulPose(Vector3f.YP.rotationDegrees(rot));
			info.itemRender(stack);
			pose.popPose();
			this.renderSquare(tile, parTick, info);
		}
	}

	public void renderHaveBlock(T tile, float parTick, RenderInfo info) {
		PoseStack pose = info.pose();
		pose.pushPose();
		pose.translate(0D, -0.95D, 0D);
		RenderUtil.renderTransBlock(pose, info.buf(), RenderColor.create(info), tile.getNeedBlock().defaultBlockState(), 0.67F);
		pose.popPose();
	}

	public void renderRangeBlock(T tile, float parTick, RenderInfo info, BlockPos pos, TagKey<Block> tag, Block crystal) {
		if (tile.getState(pos).is(TagInit.AC_BLOCK)) { return; }

		PoseStack pose = info.pose();
		pose.pushPose();
		BlockPos p = tile.getBlockPos();
		pose.translate(pos.getX() - p.getX(), -0.97D, pos.getZ() - p.getZ());
		RenderUtil.renderTransBlock(pose, info.buf(), RenderColor.create(info), crystal.defaultBlockState(), 0.5F);
		pose.popPose();
	}

	public void renderSquare(T tile, float parTick, RenderInfo info) {

		float maxSize = 1.5F;
		float addValue = 0.0125F;
		float size = Math.min(maxSize, 0.125F + (tile.nowTick * addValue));

		PoseStack pose = info.pose();
		pose.pushPose();
		pose.translate(0.5D, 1.35D, 0.5D);
		int gameTime = tile.getClientTime();
		pose.scale(size, size, size);
		float angle = (gameTime + parTick) / -20.0F * this.pi;
		pose.mulPose(Vector3f.YP.rotationDegrees(angle));
		pose.translate(-0.5D, 0D, -0.5D);
		RenderUtil.renderBlock(info, new RenderColor(72F / 255F, 1F, 1F, info.light(), info.overlay()), SQUARE);
		pose.popPose();

		maxSize = 3F;
		addValue = 0.025F;
		size = Math.min(maxSize, 0.25F + (tile.nowTick * addValue));

		pose.pushPose();
		pose.translate(0.5D, 0.15D, 0.5D);
		pose.scale(size, size, size);
		pose.mulPose(Vector3f.YP.rotationDegrees(-angle));
		pose.translate(-0.5D, 0D, -0.5D);
		RenderUtil.renderBlock(info, new RenderColor(72F / 255F, 1F, 1F, info.light(), info.overlay()), SQUARE_BLANK);
		pose.popPose();
	}
}
