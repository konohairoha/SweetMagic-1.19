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
import sweetmagic.SweetMagicCore;
import sweetmagic.init.tile.sm.TileMagiaTable;
import sweetmagic.util.RenderUtil.RenderInfo;

public class RenderMagiaTable<T extends TileMagiaTable> extends RenderAbstractTile<T> {

	private static final ResourceLocation MAGIC_BOOK = SweetMagicCore.getSRC("textures/entity/magia_book.png");
	private final BookModel bookModel;
	private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(MAGIC_BOOK);

	public RenderMagiaTable(BlockEntityRendererProvider.Context con) {
		super(con);
		this.bookModel = new BookModel(con.bakeLayer(ModelLayers.BOOK));
	}

	public void render(T tile, float parTick, RenderInfo info) {
		this.renderBook(tile, parTick, info);
		this.renderItem(tile, parTick, info);
	}

	public void renderBook(T tile, float parTick, RenderInfo info) {

		PoseStack pose = info.pose();
		pose.pushPose();
		pose.translate(0.5D, 1D, 0.5D);
		float f = (float) tile.time + parTick;
		pose.translate(0D, (double) (0.1F + Mth.sin(f * 0.1F) * 0.01F), 0D);

		float rot = 1.5825F;

		switch (tile.getFace()) {
		case SOUTH:
			rot = 4.7F;
			break;
		case EAST:
			rot = 0F;
			break;
		case WEST:
			rot = 3.15F;
			break;
		}

		pose.mulPose(Vector3f.YP.rotation(rot));
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

	public void renderItem(T tile, float parTick, RenderInfo info) {
		if(!tile.isCraft || tile.copyMagic.isEmpty()) { return; }

		PoseStack pose = info.pose();
		pose.pushPose();
		pose.translate(0.5D, 1.75D, 0.5D);
		int gameTime = tile.getClientTime();
		pose.translate(0D, Math.sin((gameTime + parTick) * 0.15D) * 0.075D, 0D);
		float rotY = (gameTime + parTick) * 0.0375F;
		pose.mulPose(Vector3f.YP.rotationDegrees(rotY * this.pi));
		pose.scale(0.5F, 0.5F, 0.5F);
		info.itemRenderNo(tile.copyMagic);
		pose.popPose();
	}
}
