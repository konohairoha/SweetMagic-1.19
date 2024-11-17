package sweetmagic.init.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.tile.sm.TileAbstractMagicianLectern;
import sweetmagic.init.tile.sm.TileAbstractMagicianLectern.SummonType;

public class RenderMagicianLectern implements BlockEntityRenderer<TileAbstractMagicianLectern> {

	private static final ResourceLocation END_CRYSTAL_LOCATION = SweetMagicCore.getSRC("textures/entity/magician_lectern_crystal.png");
	private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(END_CRYSTAL_LOCATION);
	private static final float SIN_45 = (float) Math.sin((Math.PI / 4D));
	private final ModelPart glass;

	public RenderMagicianLectern(BlockEntityRendererProvider.Context con) {
		ModelPart model = con.bakeLayer(ModelLayers.END_CRYSTAL);
		this.glass = model.getChild("glass");
	}

	public void render(TileAbstractMagicianLectern tile, float parTick, PoseStack pose, MultiBufferSource buf, int light, int overlayLight) {
		if (tile.summonType.is(SummonType.END)) { return; }

		float f = 0;
		float f1 = ((float) tile.getTime() + parTick) * 3F;
		float size = 1F + Math.min(1F, tile.tileTime * 0.00625F);
		float addY = Math.min(0.5F, tile.tileTime * 0.003125F);
		VertexConsumer vert = buf.getBuffer(RENDER_TYPE).color(0F, 0F, 0F, 1F);

		pose.pushPose();
		pose.scale(size, size, size);
		pose.translate(0.5D - addY / 2F, -0.3D + addY, 0.5D - addY / 2F);

		int overray = OverlayTexture.NO_OVERLAY;
		pose.mulPose(Vector3f.YP.rotationDegrees(f1));
		pose.translate(0.0D, (double) (1.5F + f / 2F), 0.0D);
		pose.mulPose(new Quaternion(new Vector3f(SIN_45, 0F, SIN_45), 60F, true));
		this.glass.render(pose, vert, light, overray);

		pose.scale(0.875F, 0.875F, 0.875F);
		pose.mulPose(new Quaternion(new Vector3f(SIN_45, 0F, SIN_45), 60F, true));
		pose.mulPose(Vector3f.YP.rotationDegrees(f1));

		this.glass.render(pose, vert, light, overray);
		pose.scale(0.875F, 0.875F, 0.875F);
		pose.mulPose(new Quaternion(new Vector3f(SIN_45, 0F, SIN_45), 60F, true));
		pose.mulPose(Vector3f.YP.rotationDegrees(f1));
		pose.popPose();
	}
}
