package sweetmagic.init.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;

import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit;
import sweetmagic.init.tile.sm.TileStardustWish;
import sweetmagic.util.RenderUtil;
import sweetmagic.util.RenderUtil.RenderColor;

public class RenderStardustWish implements BlockEntityRenderer<TileStardustWish> {

	private static final ResourceLocation MAGIC_BOOK = SweetMagicCore.getSRC("textures/entity/stardustbook.png");
	private static final Block SQUARE = BlockInit.magic_square_l;
	private final BookModel bookModel;
	private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(MAGIC_BOOK);

	public RenderStardustWish(BlockEntityRendererProvider.Context con) {
		this.bookModel = new BookModel(con.bakeLayer(ModelLayers.BOOK));
	}

	public void render(TileStardustWish tile, float parTick, PoseStack pose, MultiBufferSource renderer, int light, int overlayLight) {
		this.renderBook(tile, parTick, pose, renderer, light, overlayLight);
		this.renderSquare(tile, parTick, pose, renderer, light, overlayLight);
	}

	public void renderBook (TileStardustWish tile, float parTick, PoseStack pose, MultiBufferSource renderer, int light, int overlayLight) {

		pose.pushPose();
		pose.translate(0.5D, 0.85D, 0.5D);
		float f = (float) tile.time + parTick;
		pose.translate(0.0D, (double) (0.1F + Mth.sin(f * 0.1F) * 0.01F), 0.0D);

		float f1;
		for (f1 = tile.rot - tile.oRot; f1 >= (float) Math.PI; f1 -= ((float) Math.PI * 2F)) { }

		while (f1 < -(float) Math.PI) { f1 += ((float) Math.PI * 2F); }

		float f2 = tile.oRot + f1 * parTick;
		pose.mulPose(Vector3f.YP.rotation(-f2));
		pose.mulPose(Vector3f.ZP.rotationDegrees(80.0F));
		float f3 = Mth.lerp(parTick, tile.oFlip, tile.flip);
		float f4 = Mth.frac(f3 + 0.25F) * 1.6F - 0.3F;
		float f5 = Mth.frac(f3 + 0.75F) * 1.6F - 0.3F;
		float f6 = Mth.lerp(parTick, tile.oOpen, tile.open);
		this.bookModel.setupAnim(f, Mth.clamp(f4, 0.0F, 1.0F), Mth.clamp(f5, 0.0F, 1.0F), f6);
		VertexConsumer vert = renderer.getBuffer(RENDER_TYPE).color(0F, 0F, 0F, 1F);
		this.bookModel.render(pose, vert, light, overlayLight, 1.0F, 1.0F, 1.0F, 1.0F);
		pose.popPose();
	}

	public void renderSquare (TileStardustWish tile, float parTick, PoseStack pose, MultiBufferSource buf, int light, int overlayLight) {
		pose.pushPose();
		long gameTime = tile.getTime();
		pose.translate(0.5D, Math.sin((gameTime + parTick) / 10D) * 0.025D + 0.85D, 0.5D);
		float angle = (gameTime + parTick) / -20F * (180F / (float) Math.PI);
		pose.mulPose(Vector3f.YP.rotationDegrees(angle));
		pose.translate(-0.5D, 0D, -0.5D);
		float r = (float) Math.sin((gameTime + parTick) / 20F) * 185F;
		float g = (float) Math.sin((gameTime + parTick) / 20F) * 62F;
		float b = (float) Math.sin((gameTime + parTick) / 20F) * 155F;
		RenderColor color = new RenderColor((70F + r) / 255F, (180F + g) / 255F, (255F - b)/ 255F, light, overlayLight);
		RenderUtil.renderBlock(pose, buf, color, SQUARE);
		pose.popPose();
	}

	public int getViewDistance() {
		return 32;
	}
}
