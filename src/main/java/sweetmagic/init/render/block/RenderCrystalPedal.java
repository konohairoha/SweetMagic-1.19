package sweetmagic.init.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import sweetmagic.init.BlockInit;
import sweetmagic.init.tile.sm.TileCrystalPedal;
import sweetmagic.util.RenderUtil.RenderInfo;

public class RenderCrystalPedal<T extends TileCrystalPedal> extends RenderAbstractTile<T> {

	public static final ResourceLocation BEAM_LOCATION = new ResourceLocation("textures/entity/beacon_beam.png");

	public RenderCrystalPedal(BlockEntityRendererProvider.Context con) {
		super(con);
	}

	public void render(T tile, float parTick, RenderInfo info) {

		int gameTime = tile.getClientTime();
		PoseStack pose = info.pose();
		pose.pushPose();
		pose.translate(0.5D, 0.875D, 0.5D);
		pose.translate(0D, Math.sin((gameTime + parTick) * 0.1D) * 0.05D, 0D);
		float rotY = (gameTime + parTick) * 0.03F;
		pose.mulPose(Vector3f.YP.rotationDegrees(rotY * this.pi));
		pose.scale(2F, 2F, 2F);
		info.itemRenderNo(new ItemStack(BlockInit.spawn_stone_d));
		pose.popPose();

		float[] par6 = {1F, 1F, 1F};
        this.renderBeam(info.pose(), info.buf(), parTick, gameTime, 0, 1024, par6);
	}

	public void renderBeam(PoseStack pose, MultiBufferSource buf, float par1, long par3, int par4, int par5, float[] par6) {
		ResourceLocation tex = BEAM_LOCATION;
		float par2 = 1F;
		float par7 = 0.125F;
		float par8 = 0.125F;
		int i = par4 + par5;
		pose.pushPose();
		pose.translate(0.5D, 0D, 0.5D);
		float f = (float) Math.floorMod(par3, 40) + par1;
		float f1 = par5 < 0 ? f : -f;
		float f2 = Mth.frac(f1 * 0.2F - (float) Mth.floor(f1 * 0.1F));
		float f3 = par6[0];
		float f4 = par6[1];
		float f5 = par6[2];
		pose.pushPose();
		pose.mulPose(Vector3f.YP.rotationDegrees(f * 2.25F - 45F));
		float f6 = 0F;
		float f8 = 0F;
		float f9 = -par7;
		float f12 = -par7;
		float f15 = -1F + f2;
		float f16 = (float) par5 * par2 * (0.5F / par7) + f15;
		this.renderPart(pose, buf.getBuffer(RenderType.beaconBeam(tex, false)), f3, f4, f5, 1F, par4, i, 0F, par7, par7, 0F, f9, 0F, 0F, f12, 0F, 1F, f16, f15);
		pose.popPose();
		float f7 = f6 = f8 = f9 = -par8;
		f15 = -1F + f2;
		f16 = (float) par5 * par2 + f15;
		this.renderPart(pose, buf.getBuffer(RenderType.beaconBeam(tex, true)), f3, f4, f5, 0.125F, par4, i, f6, f7, par8, f8, f9, par8, par8, par8, 0F, 1F, f16, f15);
		pose.popPose();
	}

	private void renderPart(PoseStack pose, VertexConsumer ver, float r, float g, float b, float alpha, int par1, int par2, float par3, float par4, float par5, float par6, float par7, float par8, float par9, float par10, float par11, float par12, float par13, float par14) {
		PoseStack.Pose poseLast = pose.last();
		Matrix4f ma4 = poseLast.pose();
		Matrix3f ma3 = poseLast.normal();
		this.renderQuad(ma4, ma3, ver, r, g, b, alpha, par1, par2, par3, par4, par5, par6, par11, par12, par13, par14);
		this.renderQuad(ma4, ma3, ver, r, g, b, alpha, par1, par2, par9, par10, par7, par8, par11, par12, par13, par14);
		this.	renderQuad(ma4, ma3, ver, r, g, b, alpha, par1, par2, par5, par6, par9, par10, par11, par12, par13, par14);
		this.renderQuad(ma4, ma3, ver, r, g, b, alpha, par1, par2, par7, par8, par3, par4, par11, par12, par13, par14);
	}

	private void renderQuad(Matrix4f ma4, Matrix3f ma3, VertexConsumer ver, float r, float g, float b, float alpha, int par1, int par2, float par3, float par4, float par5, float par6, float par7, float par8, float par9, float par10) {
		this.addVertex(ma4, ma3, ver, r, g, b, alpha, par2, par3, par4, par8, par9);
		this.addVertex(ma4, ma3, ver, r, g, b, alpha, par1, par3, par4, par8, par10);
		this.addVertex(ma4, ma3, ver, r, g, b, alpha, par1, par5, par6, par7, par10);
		this.addVertex(ma4, ma3, ver, r, g, b, alpha, par2, par5, par6, par7, par9);
	}

	private void addVertex(Matrix4f ma4, Matrix3f ma3, VertexConsumer ver, float r, float g, float b, float alpha, int par1, float par2, float par3, float par4, float par5) {
		ver.vertex(ma4, par2, (float) par1, par3).color(r, g, b, alpha).uv(par4, par5).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(ma3, 0F, 1F, 0F).endVertex();
	}

	public boolean shouldRenderOffScreen(T tile) {
		return true;
	}

	public int getViewDistance() {
		return 256;
	}

	public boolean shouldRender(T tile, Vec3 vec) {
		return Vec3.atCenterOf(tile.getBlockPos()).multiply(1D, 0D, 1D).closerThan(vec.multiply(1D, 0D, 1D), (double) this.getViewDistance());
	}
}
