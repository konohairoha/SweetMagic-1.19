package sweetmagic.init.render.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.Block;
import sweetmagic.init.BlockInit;
import sweetmagic.init.entity.projectile.TripleTornadoShot;
import sweetmagic.util.RenderUtil;
import sweetmagic.util.RenderUtil.RenderColor;

public class RenderTripleTornado<T extends TripleTornadoShot> extends RenderMagicBase<T> {

	private static final Block SQUARE_BLOCK_L = BlockInit.magic_square_l_blank;

	public RenderTripleTornado(EntityRendererProvider.Context con) {
		super(con);
	}

	public void render(T entity, float parTick, float par2, PoseStack pose, MultiBufferSource buf, int light) {
		long gameTime = entity.level.getGameTime();
		float angle = -gameTime / 20F * (180F / (float) Math.PI);

		float scaleRate = Math.min(1F, entity.tickCount * 0.05F);
		float scale = (float) entity.getRange() * scaleRate;
		scale *= 1.1F;

		double rate = 0D;

		if (scale >= 20F) {
			rate = 0.00375D;
		}

		else if (scale >= 15F) {
			rate = 0.00475D;
		}

		else if (scale >= 10F) {
			rate = 0.0065D;
		}

		else if (scale >= 5F) {
			rate = 0.01D;
		}

		else {
			rate = 0.022D;
		}

		pose.scale(scale, scale, scale);
		pose.mulPose(Vector3f.YP.rotationDegrees(angle));
		pose.translate(-0.5D, 0.1D - scale * rate, -0.5D);
		RenderUtil.renderBlock(pose, buf, new RenderColor(108F / 255F, 239F / 255F, 71F / 255F, light, OverlayTexture.NO_OVERLAY), SQUARE_BLOCK_L);
	}
}
