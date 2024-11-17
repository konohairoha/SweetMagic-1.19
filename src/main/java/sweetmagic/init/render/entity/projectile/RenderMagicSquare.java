package sweetmagic.init.render.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import sweetmagic.init.BlockInit;
import sweetmagic.init.entity.projectile.MagicSquareMagic;
import sweetmagic.util.RenderUtil;

public class RenderMagicSquare extends EntityRenderer<MagicSquareMagic> {

	private static final Block SQUARE_BLOCK_L = BlockInit.magic_square_l;

	public RenderMagicSquare(EntityRendererProvider.Context con) {
		super(con);
	}

	public ResourceLocation getTextureLocation(MagicSquareMagic entity) {
		return TextureAtlas.LOCATION_BLOCKS;
	}

	public void render(MagicSquareMagic entity, float parTick, float par2, PoseStack pose, MultiBufferSource buf, int light) {
		long gameTime = entity.level.getGameTime();
		float angle = -(gameTime + parTick) / 20.0F * (180F / (float) Math.PI);
		float scale = (float) entity.getRange();
		scale *= 1.1F;

		double rate = 0D;

		if (scale >= 20F) {
			rate = 0.00375D;
		}

		else if (scale >= 15F) {
			rate = 0.00525D;
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
		RenderUtil.renderBlock(pose, buf, entity.getColor(light), SQUARE_BLOCK_L);
	}
}
