package sweetmagic.init.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;

import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit;
import sweetmagic.init.tile.sm.TileParallelInterfere;
import sweetmagic.util.RenderUtil.RenderInfo;

public class RenderParallelInterfere<T extends TileParallelInterfere> extends RenderAbstractTile<T> {

	private static final ResourceLocation MAGIC_BOOK = SweetMagicCore.getSRC("textures/entity/magicbook.png");
	private static final ItemStack SQUARE = new ItemStack(BlockInit.magic_square_h);
	private final BookModel bookModel;
	private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(MAGIC_BOOK);

	public RenderParallelInterfere(BlockEntityRendererProvider.Context con) {
		super(con);
		this.bookModel = new BookModel(con.bakeLayer(ModelLayers.BOOK));
	}

	public void render(T tile, float parTick, RenderInfo info) {
		this.renderBook(tile, parTick, info);
		this.renderSquare(tile, parTick, info);
	}

	public void renderBook(T tile, float parTick, RenderInfo info) {

		PoseStack pose = info.pose();
		pose.pushPose();
		pose.translate(0.5D, 0.85D, 0.5D);
		float f = (float) tile.time + parTick;
		pose.translate(0D, (double) (0.1F + Mth.sin(f * 0.1F) * 0.01F), 0D);

		float f1;
		for (f1 = tile.rot - tile.oRot; f1 >= (float) Math.PI; f1 -= ((float) Math.PI * 2F)) { }

		while (f1 < -(float) Math.PI) { f1 += ((float) Math.PI * 2F); }

		float f2 = tile.oRot + f1 * parTick;
		pose.mulPose(Vector3f.YP.rotation(-f2));
		pose.mulPose(Vector3f.ZP.rotationDegrees(80F));
		float f3 = Mth.lerp(parTick, tile.oFlip, tile.flip);
		float f4 = Mth.frac(f3 + 0.25F) * 1.6F - 0.3F;
		float f5 = Mth.frac(f3 + 0.75F) * 1.6F - 0.3F;
		float f6 = Mth.lerp(parTick, tile.oOpen, tile.open);
		this.bookModel.setupAnim(f, Mth.clamp(f4, 0F, 1F), Mth.clamp(f5, 0F, 1F), f6);
		VertexConsumer vert = info.buf().getBuffer(RENDER_TYPE).color(0F, 0F, 0F, 1F);
		this.bookModel.render(pose, vert, info.light(), info.overlay(), 1F, 1F, 1F, 1F);
		pose.popPose();
	}

	public void renderSquare(T tile, float parTick, RenderInfo info) {
		PoseStack pose = info.pose();
		pose.pushPose();
		pose.translate(0.5D, 1.35D, 0.5D);
		int gameTime = tile.getClientTime();
		pose.translate(0D, Math.sin((gameTime + parTick) / 10D) * 0.025D, 0D);
		float size = 2F;
		pose.scale(size, size, size);
		float angle = (gameTime + parTick) / -20F * this.pi;
		pose.mulPose(Vector3f.YP.rotationDegrees(angle));
		info.itemRender(SQUARE);
		pose.popPose();
	}
}
