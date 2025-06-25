package sweetmagic.init.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.entity.LivingEntity;
import sweetmagic.init.tile.sm.TileBossFigurine;
import sweetmagic.util.RenderUtil.RenderInfo;

public class RenderBossFigurine<T extends TileBossFigurine> extends RenderAbstractTile<T> {

	public RenderBossFigurine(BlockEntityRendererProvider.Context con) {
		super(con);
	}

	public void render(T tile, float parTick, RenderInfo info) {
		PoseStack pose = info.pose();
		pose.pushPose();
		pose.translate(0.5D, 0.06D, 0.5D);
		pose.mulPose(Vector3f.YP.rotationDegrees(tile.getRot() + 180F));
		pose.translate(0D, 0D, tile.getAddZ());
		LivingEntity entity = tile.getRenderEntity();

		if (tile.renderTick++ % 3 == 0) {
			entity.tickCount++;
			tile.renderTick = 1;
		}

		float f = 1F;
		float f1 = Math.max(entity.getBbWidth(), entity.getBbHeight());
		if ((double) f1 > 1D) { f /= f1; }

		pose.scale(f, f, f);
		this.eRender.render(entity, 0D, 0D, 0D, 0F, 0, pose, info.buf(), info.light());
		pose.popPose();
	}
}
