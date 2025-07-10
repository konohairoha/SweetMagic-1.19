package sweetmagic.util;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ForgeHooksClient;
import sweetmagic.init.tile.sm.TileAbstractSM;

public class RenderUtil {

	public static void renderItem(RenderInfo info, TileAbstractSM tile, ItemStack stack, double x, double y, double z) {
		RenderUtil.renderItem(info, tile, stack, x, y, z, 1F, false);
	}


	public static void renderItem(RenderInfo info, TileAbstractSM tile, ItemStack stack, double x, double y, double z, float rate, boolean isBlock) {

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

		if (!isBlock && stack.getItem() instanceof BlockItem) {
			pose.scale(0.5F, 0.5F, 0.5F);
			pose.translate(x, y, z);
			pose.translate(-0.325D, -y / 3.3D, -0.5D);
		}

		else {
			float scale = 0.375F * rate;
			pose.scale(scale, scale, scale);
			pose.translate(x, y, z);
		}

		info.render().renderStatic(stack, ItemTransforms.TransformType.FIXED, info.light(), info.overlay(), pose, info.buf(), 0);
		pose.popPose();
	}

	public static void renderBlock(RenderInfo info, RenderColor color, Block block) {
		renderBlock(info.pose(), info.buf(), color, block);
	}

	// ブロックレンダー
	public static void renderBlock(PoseStack pose, MultiBufferSource buf, RenderColor color, Block block) {
		Minecraft mc = Minecraft.getInstance();
		BlockState state = block.defaultBlockState();
		VertexConsumer vert = buf.getBuffer(RenderType.cutout()).color(0F, 0F, 0F, 1F);
		ModelBlockRenderer render = new ModelBlockRenderer(mc.getBlockColors());
		BakedModel model = mc.getBlockRenderer().getBlockModel(state);
		render.renderModel(pose.last(), vert, state, model, color.red(), color.green(), color.blue(), color.light(), color.overlayLight());
	}

	// ブロックレンダー
	public static void renderTransBlock(PoseStack pose, MultiBufferSource buf, RenderColor renderColor, Block block) {
		RenderUtil.renderTransBlock(pose, buf, renderColor, block.defaultBlockState());
	}

	// ブロックレンダー
	public static void renderTransBlock(PoseStack pose, MultiBufferSource buf, RenderColor renderColor, BlockState state) {
		Minecraft mc = Minecraft.getInstance();
		VertexConsumer vert = buf.getBuffer(RenderType.translucent()).color(0F, 0F, 0F, 1F);
		ModelBlockRenderer render = new ModelBlockRenderer(mc.getBlockColors());
		BakedModel model = mc.getBlockRenderer().getBlockModel(state);
		render.renderModel(pose.last(), vert, state, model, renderColor.red(), renderColor.green(), renderColor.blue(), renderColor.light(), renderColor.overlayLight());
	}

	// ブロックレンダー
	public static void renderTransBlock(PoseStack pose, MultiBufferSource buf, RenderColor color, BlockState state, float alpha) {
		Minecraft mc = Minecraft.getInstance();
		VertexConsumer ver = buf.getBuffer(RenderType.translucent()).color(0F, 0F, 0F, 1F);
		BakedModel model = mc.getBlockRenderer().getBlockModel(state);
		int light = color.light();
		int over = color.overlayLight();

		for (Direction face : Direction.values()) {

			List<BakedQuad> quadList = model.getQuads(state, face, RandomSource.create(Mth.getSeed(1, 1, 1)));
			for (BakedQuad quad : quadList) {
				boolean flag = quad.isTinted();
				float f = flag ? 0F : 1F;
				float f1 = flag ? 0F : 1F;
				float f2 = flag ? 0F : 1F;
				ver.putBulkData(pose.last(), quad, f, f1, f2, alpha, light, over, true);
			}
		}
	}

	public static void renderBlock(Level world, BlockPos pos, BlockState state, BlockRenderDispatcher render, PoseStack pose, MultiBufferSource buf, int overlay) {
		ForgeHooksClient.renderPistonMovedBlocks(pos, state, pose, buf, world, false, overlay, render);
	}

	public static record RenderInfo(ItemRenderer render, int light, int overlay, PoseStack pose, MultiBufferSource buf) {

		public void itemRender(ItemStack stack) {
			this.render().renderStatic(stack, ItemTransforms.TransformType.FIXED, this.light(), this.overlay(), this.pose(), this.buf(), 0);
		}

		public void itemRenderNo(ItemStack stack) {
			this.render().renderStatic(stack, ItemTransforms.TransformType.FIXED, this.light(), OverlayTexture.NO_OVERLAY, this.pose(), this.buf(), 0);
		}
	}

	public static record RenderColor(float red, float green, float blue, int light, int overlayLight) {

		public static RenderColor create(int light) {
			return new RenderColor(1F, 1F, 1F, light, OverlayTexture.NO_OVERLAY);
		}

		public static RenderColor create(RenderInfo info) {
			return new RenderColor(1F, 1F, 1F, info.light(), OverlayTexture.NO_OVERLAY);
		}
	}

	public static record RGBColor(int red, int green, int blue) { }
}
