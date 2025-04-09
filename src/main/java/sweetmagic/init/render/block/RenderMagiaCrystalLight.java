package sweetmagic.init.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit;
import sweetmagic.init.tile.sm.TileMagiaCrystalLight;
import sweetmagic.util.RenderUtil;
import sweetmagic.util.RenderUtil.RenderColor;
import sweetmagic.util.RenderUtil.RenderInfo;

public class RenderMagiaCrystalLight<T extends TileMagiaCrystalLight> extends RenderAbstractTile<T> {

	private static final Block SQUARE_BLOCK_L = BlockInit.magic_square_l_blank;
	private static final Block SQUARE_BLOCK_S = BlockInit.magic_square_s_blank;
	private static final float SIN_45 = (float) Math.sin((Math.PI / 4D));
	private static final ResourceLocation CRYSTAL = SweetMagicCore.getSRC("textures/entity/magiaflux_core.png");
	private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(CRYSTAL);
	private final ModelPart cube;

	public RenderMagiaCrystalLight(BlockEntityRendererProvider.Context con) {
		super(con);
		ModelPart model = con.bakeLayer(ModelLayers.END_CRYSTAL);
		this.cube = model.getChild("cube");
	}

	@Override
	public void render(T tile, float parTick, RenderInfo info) {
		this.renderCrystal(tile, parTick, info);
		this.renderSquare(tile, parTick, info);
	}

	public void renderCrystal(T tile, float parTick, RenderInfo info) {

		PoseStack pose = info.pose();
		pose.pushPose();
		int gameTime = tile.getClientTime();
		float f1 = ((float) gameTime + parTick) * 1.25F;
		VertexConsumer vert = info.buf().getBuffer(RENDER_TYPE).color(0F, 0F, 0F, 1F);
		pose.translate(0.5D, 1.25D, 0.5D);
		pose.scale(1.3F, 1.3F, 1.35F);
		pose.mulPose(new Quaternion(new Vector3f(SIN_45, 0F, SIN_45), 60F, true));
		pose.mulPose(Vector3f.XP.rotationDegrees(f1));
		pose.mulPose(Vector3f.YP.rotationDegrees(f1));
		pose.mulPose(Vector3f.ZP.rotationDegrees(f1));
		this.cube.render(pose, vert, info.light(), info.overlay());
		pose.popPose();
	}

	public void renderSquare(T tile, float parTick, RenderInfo info) {

		int gameTime = tile.getClientTime();
		PoseStack pose = info.pose();
		pose.pushPose();
		pose.translate(0.5D, Math.sin((gameTime + parTick) / 15F) * 0.02D + 0.35D, 0.5D);
		pose.scale(2F, 2F, 2F);
		float angle = -(gameTime + parTick) / 20.0F * this.pi;
		pose.mulPose(Vector3f.YP.rotationDegrees(angle));
		pose.translate(-0.5D, 0.2D, -0.5D);

		RenderColor color = new RenderColor(76F / 255F, 165F / 255F, 1F, info.light(), info.overlay());
		RenderUtil.renderBlock(info, color, SQUARE_BLOCK_L);
		pose.translate(0.125D, -0.15D, 0.125D);
		pose.scale(0.75F, 0.75F, 0.75F);
		RenderUtil.renderBlock(info, color, SQUARE_BLOCK_S);
		pose.popPose();
	}
}
