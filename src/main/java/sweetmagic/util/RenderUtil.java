package sweetmagic.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ForgeHooksClient;
import sweetmagic.init.tile.sm.TileAbstractSM;

public class RenderUtil {

	public static void renderItem (RenderInfo info, TileAbstractSM tile, ItemStack stack, double x, double y, double z) {

		PoseStack pose = info.getPose();
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

		if (stack.getItem() instanceof BlockItem) {
			pose.scale(0.5F, 0.5F, 0.5F);
			pose.translate(x, y, z);
			pose.translate(-0.325D, -y / 3.3D, -0.5D);
		}

		else {
			pose.scale(0.375F, 0.375F, 0.375F);
			pose.translate(x, y, z);
		}

		info.getRender().renderStatic(stack, ItemTransforms.TransformType.FIXED, info.getLight(), info.getOverlayLight(), pose, info.getBuf(), 0);
		pose.popPose();
	}

	// ブロックレンダー
	public static void renderBlock(PoseStack pose, MultiBufferSource buf, RenderColor renderColor, Block block) {

		Minecraft mc = Minecraft.getInstance();
		BlockState state = block.defaultBlockState();
		VertexConsumer vert = buf.getBuffer(RenderType.cutout()).color(0F, 0F, 0F, 1F);
		ModelBlockRenderer render = new ModelBlockRenderer(mc.getBlockColors());

		BakedModel model = mc.getBlockRenderer().getBlockModel(state);
		render.renderModel(pose.last(), vert, state, model, renderColor.getRed(), renderColor.getGreen(), renderColor.getBlue(), renderColor.getLight(), renderColor.getOverlayLight());
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
		render.renderModel(pose.last(), vert, state, model, renderColor.getRed(), renderColor.getGreen(), renderColor.getBlue(), renderColor.getLight(), renderColor.getOverlayLight());
	}

	public static void renderBlock(Level world, BlockPos pos, BlockState state, BlockRenderDispatcher render, PoseStack pose, MultiBufferSource buf, int overlay) {
        ForgeHooksClient.renderPistonMovedBlocks(pos, state, pose, buf, world, false, overlay, render);
    }

	public static record RenderInfo (ItemRenderer render, int light, int overlayLight, PoseStack pose, MultiBufferSource buf) {

		public ItemRenderer getRender () {
			return this.render;
		}

		public int getLight () {
			return this.light;
		}

		public int getOverlayLight () {
			return this.overlayLight;
		}

		public PoseStack getPose () {
			return this.pose;
		}

		public MultiBufferSource getBuf () {
			return this.buf;
		}
	}

	public static record RenderColor (float red, float green, float blue, int light, int overlayLight) {

		public float getRed () {
			return this.red;
		}

		public float getGreen () {
			return this.green;
		}

		public float getBlue () {
			return this.blue;
		}

		public int getLight () {
			return this.light;
		}

		public int getOverlayLight () {
			return this.overlayLight;
		}
	}
}
