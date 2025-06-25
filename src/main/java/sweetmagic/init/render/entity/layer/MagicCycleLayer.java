package sweetmagic.init.render.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import sweetmagic.init.BlockInit;
import sweetmagic.init.PotionInit;
import sweetmagic.util.RenderUtil;
import sweetmagic.util.RenderUtil.RenderColor;

public class MagicCycleLayer<T extends LivingEntity, M extends EntityModel<T>> extends AbstractEntityLayer<T, M> {

	private static final Block SQUARE = BlockInit.magic_square_l_blank;

	public MagicCycleLayer(RenderLayerParent<T, M> layer, EntityRendererProvider.Context con) {
		super(layer, con);
	}

	@Override
	public void render(PoseStack pose, MultiBufferSource buf, int light, T entity, float swing, float swingAmount, float parTick, float ageTick, float netHeadYaw, float headPitch) {
		if (entity.tickCount > 100 || !entity.hasEffect(PotionInit.magic_array)) { return; }

		float maxSize = 3F;
		float addValue = 0.0875F;
		int tickCount = entity.tickCount;
		float size = Math.min(maxSize, 0.3F + (tickCount * addValue));

		if (tickCount >= 90) {
			size -= (tickCount - 90) * 0.225F;
		}

		pose.pushPose();
		pose.translate(0D, 1.5D, 0D);
		pose.scale(size, size, size);
		float angle = (tickCount + parTick) / 10F * this.pi;
		pose.mulPose(Vector3f.YP.rotationDegrees(angle));
		pose.translate(-0.5D, 0D, -0.5D);
		float rgb = (float) Math.sin((tickCount + parTick) / 10F) * 40F;
		RenderUtil.renderBlock(pose, buf, new RenderColor((72F + rgb) / 255F, (200F + rgb) / 255F, (200F + rgb) / 255F, light, OverlayTexture.NO_OVERLAY), SQUARE);
		pose.popPose();
	}
}
