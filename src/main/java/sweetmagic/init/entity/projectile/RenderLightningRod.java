package sweetmagic.init.render.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import sweetmagic.init.entity.projectile.LightningRod;

public class RenderLightningRod<T extends LightningRod> extends RenderMagicBase<T> {

	private static final BlockState ROD = Blocks.LIGHTNING_ROD.defaultBlockState();

	public RenderLightningRod(EntityRendererProvider.Context con) {
		super(con);
	}

	@Override
	public void render(T entity, float yaw, float part, PoseStack pose, MultiBufferSource buf, int light) {
		pose.pushPose();
		pose.translate(-0.5D, 0D, -0.5D);
		ModelBlockRenderer.enableCaching();
		this.renderBlock(entity, pose, buf, ROD);
		ModelBlockRenderer.clearCache();
		pose.popPose();
		if (!entity.getLighning()) { return; }

		pose.translate(0D, -2D, 0D);
		float[] afloat = new float[8];
		float[] afloat1 = new float[8];
		float f = 0F;
		float f1 = 0F;
		RandomSource rand = RandomSource.create(entity.getId() + (entity.tickCount / 2));

		for (int i = 7; i >= 0; --i) {
			afloat[i] = f;
			afloat1[i] = f1;
			f += (float) (rand.nextInt(11) - 5);
			f1 += (float) (rand.nextInt(11) - 5);
		}

		VertexConsumer ver = buf.getBuffer(RenderType.lightning());
		Matrix4f mat = pose.last().pose();

		for (int j = 0; j < 4; ++j) {

			RandomSource rand1 = RandomSource.create(entity.getId() + (entity.tickCount / 2));

			for (int k = 0; k < 3; ++k) {

				int l = k > 0 ? 7 - k : 7;
				int i1 = k > 0 ? l - 2 : 0;
				float f2 = afloat[l] - f;
				float f3 = afloat1[l] - f1;

				for (int j1 = l; j1 >= i1; --j1) {

					float f4 = f2;
					float f5 = f3;

					if (k == 0) {
						f2 += (float) (rand1.nextInt(11) - 5);
						f3 += (float) (rand1.nextInt(11) - 5);
					}

					else {
						f2 += (float) (rand1.nextInt(31) - 15);
						f3 += (float) (rand1.nextInt(31) - 15);
					}

					float f10 = 0.1F + (float) j * 0.2F;

					if (k == 0) {
						f10 *= (float) j1 * 0.1F + 1F;
					}

					float f11 = 0.1F + (float) j * 0.2F;

					if (k == 0) {
						f11 *= ((float) j1 - 1F) * 0.1F + 1F;
					}

					quad(mat, ver, f2, f3, j1, f4, f5, 0.45F, 0.45F, 0.5F, f10, f11, false, false, true, false);
					quad(mat, ver, f2, f3, j1, f4, f5, 0.45F, 0.45F, 0.5F, f10, f11, true, false, true, true);
					quad(mat, ver, f2, f3, j1, f4, f5, 0.45F, 0.45F, 0.5F, f10, f11, true, true, false, true);
					quad(mat, ver, f2, f3, j1, f4, f5, 0.45F, 0.45F, 0.5F, f10, f11, false, true, false, false);
				}
			}
		}
	}

	private static void quad(Matrix4f mat, VertexConsumer ver, float par1, float par2, int par3, float par4, float par5, float par6, float par7, float par8, float par9, float par10, boolean par11, boolean par12, boolean par13, boolean par14) {
		ver.vertex(mat, par1 + (par11 ? par10 : -par10), (float) (par3 * 16), par2 + (par12 ? par10 : -par10)).color(par6, par7, par8, 0.3F).endVertex();
		ver.vertex(mat, par4 + (par11 ? par9 : -par9), (float) ((par3 + 1) * 16), par5 + (par12 ? par9 : -par9)).color(par6, par7, par8, 0.3F).endVertex();
		ver.vertex(mat, par4 + (par13 ? par9 : -par9), (float) ((par3 + 1) * 16), par5 + (par14 ? par9 : -par9)).color(par6, par7, par8, 0.3F).endVertex();
		ver.vertex(mat, par1 + (par13 ? par10 : -par10), (float) (par3 * 16), par2 + (par14 ? par10 : -par10)).color(par6, par7, par8, 0.3F).endVertex();
	}
}
