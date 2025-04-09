package sweetmagic.init.render.block;

import java.util.Map;
import java.util.Map.Entry;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit;
import sweetmagic.init.block.magic.SturdustCrystal;
import sweetmagic.init.tile.sm.TileSturdustCrystal;
import sweetmagic.util.RenderUtil;
import sweetmagic.util.RenderUtil.RenderColor;
import sweetmagic.util.RenderUtil.RenderInfo;

public class RenderSturdustCrystal<T extends TileSturdustCrystal> extends RenderAbstractTile<T> {

	private static final ResourceLocation CRYSTAL = SweetMagicCore.getSRC("textures/entity/stardust_crystal.png");
	private static final Block SQUARE = BlockInit.magic_square_h;
	private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(CRYSTAL);
	private static final float SIN_45 = (float) Math.sin((Math.PI / 4D));
	private final ModelPart cube;
	private final ModelPart glass;

	public RenderSturdustCrystal(BlockEntityRendererProvider.Context con) {
		super(con);
		ModelPart model = con.bakeLayer(ModelLayers.END_CRYSTAL);
		this.glass = model.getChild("glass");
		this.cube = model.getChild("cube");
	}

	public void render(T tile, float parTick, RenderInfo info) {
		this.renderCrystal(tile, parTick, info);
		this.renderSquare(tile, parTick, info);
		this.renderGate(tile, parTick, info);
	}

	public void renderCrystal(T tile, float parTick, RenderInfo info) {

		PoseStack pose = info.pose();
		pose.pushPose();
		int gameTime = tile.getClientTime();
		float f1 = ((float) gameTime + parTick) * 3F;
		VertexConsumer vert = info.buf().getBuffer(RENDER_TYPE).color(0F, 0F, 0F, 1F);
		pose.scale(1F, 1F, 1F);
		pose.translate(0.5D, 1D, 0.5D);

		int overray = OverlayTexture.NO_OVERLAY;
		pose.mulPose(Vector3f.YP.rotationDegrees(f1));
		pose.translate(0D, Math.sin((gameTime + parTick) / 7.5F) * 0.25D, 0D);
		pose.mulPose(new Quaternion(new Vector3f(SIN_45, 0F, SIN_45), 60F, true));
		this.glass.render(pose, vert, info.light(), overray);

		pose.scale(0.875F, 0.875F, 0.875F);
		pose.mulPose(new Quaternion(new Vector3f(SIN_45, 0F, SIN_45), 60F, true));
		pose.mulPose(Vector3f.YP.rotationDegrees(f1));

		this.glass.render(pose, vert, info.light(), overray);
		pose.scale(0.875F, 0.875F, 0.875F);
		pose.mulPose(new Quaternion(new Vector3f(SIN_45, 0F, SIN_45), 60F, true));
		pose.mulPose(Vector3f.YP.rotationDegrees(f1));
		this.cube.render(pose, vert, info.light(), overray);
		pose.popPose();
	}

	public void renderSquare(T tile, float parTick, RenderInfo info) {
		PoseStack pose = info.pose();
		pose.pushPose();
		pose.translate(0.5D, 0.65D, 0.5D);
		int gameTime = tile.getClientTime();
		pose.translate(0D, Math.sin((gameTime + parTick) / 15F) * 0.02D, 0D);
		pose.scale(2F, 2F, 2F);
		float angle = (gameTime + parTick) / 20F * (180F / (float) Math.PI);
		pose.mulPose(Vector3f.YP.rotationDegrees(angle));
		pose.translate(-0.25D, -0.25D, -0.25D);
		pose.scale(0.5F, 0.5F, 0.5F);
		RenderUtil.renderBlock(info, RenderColor.create(info), SQUARE);
		pose.popPose();
	}

	public void renderGate(T tile, float parTick, RenderInfo info) {
		if (!tile.isRender) { return; }

		BlockPos pos = tile.getBlockPos();
		int x = pos.getX(), y = pos.getY(), z = pos.getZ();
		Direction face = tile.getFace();
		boolean isZ = face == Direction.NORTH || face == Direction.SOUTH;
		Map<BlockPos, BlockState> posMap = isZ ? SturdustCrystal.getZPosMap(pos) : SturdustCrystal.getXPosMap(pos);
		RenderColor color = RenderColor.create(info);
		PoseStack pose = info.pose();

		for (Entry<BlockPos, BlockState> map : posMap.entrySet()) {
			pose.pushPose();
			BlockPos p = map.getKey();
			pose.translate(p.getX() - x, p.getY() - y + 0.01D, p.getZ() - z);
			RenderUtil.renderTransBlock(pose, info.buf(), color, map.getValue().getBlock().defaultBlockState(), 0.75F);
			pose.popPose();
		}
	}

	public int getViewDistance() {
		return 48;
	}
}
