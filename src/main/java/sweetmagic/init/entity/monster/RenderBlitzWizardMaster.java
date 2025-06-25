package sweetmagic.init.render.entity.monster;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.entity.monster.boss.BlitzWizardMaster;
import sweetmagic.init.render.entity.layer.BlitzWizarMasterLayer;
import sweetmagic.init.render.entity.model.BlitzWizardModel;

public class RenderBlitzWizardMaster<T extends BlitzWizardMaster> extends MobRenderer<T, BlitzWizardModel<T>> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/entity/blitz_wizard_master.png");

	public RenderBlitzWizardMaster(EntityRendererProvider.Context con) {
		super(con, new BlitzWizardModel<T>(con.bakeLayer(BlitzWizardModel.LAYER)), 0.5F);
		this.addLayer(new BlitzWizarMasterLayer<T, BlitzWizardModel<T>>(this, con));
	}

	public void render(T entity, float par1, float par2, PoseStack pose, MultiBufferSource buf, int light) {
		super.render(entity, par1, par2, pose, buf, light);

		if (entity.getLighningWeb()) {

			double dis = entity.getTarget() == null ? -2D : entity.yo - entity.getTarget().yo - 2;
			VertexConsumer ver = buf.getBuffer(RenderType.lightning());
			for (int k = 0; k < 4; k++) {
				for (int i = 0; i < 4; i++) {
					this.renderThunder(entity, pose, ver, k, dis, i * 1.5F);
				}
			}
		}
	}

	public void renderThunder(T entity, PoseStack pose, VertexConsumer ver, int tick, double dis, float angle) {

		pose.pushPose();
		int tickCount = entity.tickCount;
		float addX = (float) Math.sin(angle + tickCount / 20F) * ((float) Math.cos(tickCount / 20F) * 12.5F + 15F);
		float addY = -(float) Math.cos(angle + tickCount / 20F) * ((float) Math.cos(tickCount / 20F) * 12.5F + 15F);
		pose.translate(0D, dis, 0D);
		pose.scale(0.1F, 1F, 0.1F);

		float[] afloat = new float[8];
		float[] afloat1 = new float[8];
		float f = 0F;
		float f1 = 0F;
		Matrix4f mat = pose.last().pose();
		RandomSource rand1 = RandomSource.create(entity.getId() + tickCount + tick);

		for (int j = 0; j < 4; ++j) {

			int l = 7;
			int i1 = 0;
			float f2 = afloat[l] - f;
			float f3 = afloat1[l] - f1;

			for (int j1 = l; j1 >= i1; --j1) {

				float f4 = f2;
				float f5 = f3;
				float f10 = 0.1F + (float) j * 0.2F;
				float f11 = 0.1F + (float) j * 0.2F;
				f2 += addX + (float) (rand1.nextInt(5) - 2);
				f3 += addY + (float) (rand1.nextInt(5) - 2);
				f10 *= (float) j1 * 0.1F + 1F;
				f11 *= ((float) j1 - 1F) * 0.1F + 1F;

				quad(mat, ver, f2, f3, j1, f4, f5, 0.45F, 0.45F, 0.5F, f10, f11, false, false, true, false);
				quad(mat, ver, f2, f3, j1, f4, f5, 0.45F, 0.45F, 0.5F, f10, f11, true, false, true, true);
				quad(mat, ver, f2, f3, j1, f4, f5, 0.45F, 0.45F, 0.5F, f10, f11, true, true, false, true);
				quad(mat, ver, f2, f3, j1, f4, f5, 0.45F, 0.45F, 0.5F, f10, f11, false, true, false, false);
			}
		}
		pose.popPose();
	}

	private static void quad(Matrix4f mat, VertexConsumer ver, float par1, float par2, int par3, float par4, float par5, float r, float g, float b, float par9, float par10, boolean par11, boolean par12, boolean par13, boolean par14) {
		ver.vertex(mat, par1 + (par11 ? par10 : -par10), par3 * 1.35F, par2 + (par12 ? par10 : -par10)).color(r, g, b, 0.15F).endVertex();
		ver.vertex(mat, par4 + (par11 ? par9 : -par9), (par3 + 1F) * 1.35F, par5 + (par12 ? par9 : -par9)).color(r, g, b, 0.15F).endVertex();
		ver.vertex(mat, par4 + (par13 ? par9 : -par9), (par3 + 1F) * 1.35F, par5 + (par14 ? par9 : -par9)).color(r, g, b, 0.15F).endVertex();
		ver.vertex(mat, par1 + (par13 ? par10 : -par10), par3 * 1.35F, par2 + (par14 ? par10 : -par10)).color(r, g, b, 0.15F).endVertex();
	}

	protected void scale(T entity, PoseStack pose, float par1) {
		pose.scale(1.25F, 1.25F, 1.25F);
	}

	public ResourceLocation getTextureLocation(T entity) {
		return TEX;
	}
}
