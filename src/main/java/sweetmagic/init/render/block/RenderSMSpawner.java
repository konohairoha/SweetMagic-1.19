package sweetmagic.init.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.entity.LivingEntity;
import sweetmagic.init.tile.sm.TileSMSpawner;

public class RenderSMSpawner extends RenderAbstractTile<TileSMSpawner> {

	private int tickTime = 0;

	public RenderSMSpawner(BlockEntityRendererProvider.Context con) {
		super(con);
	}

	@Override
	public void render(TileSMSpawner tile, float parTick, PoseStack pose, MultiBufferSource buf, int light, int overlayLight) {
		if (tile.isPeace || tile.getMobType() == -1) { return; }

		LivingEntity entity = tile.getRenderEntity();
		if (this.tickTime++ % 3 == 0) {
			entity.tickCount++;
			this.tickTime = 1;
		}

		float f = 0.675F;
		float f1 = Math.max(entity.getBbWidth(), entity.getBbHeight());
		if ((double) f1 > 1D) { f /= f1; }
		pose.pushPose();
		pose.translate(0.5D, 0D, 0.5D);
		pose.translate(0D, (double) 0.4F, 0D);

		if (tile.isPlayer) {
			long gameTime = entity.tickCount;
			float angle = (gameTime + parTick) * 0.25F * this.pi;
			pose.mulPose(Vector3f.YP.rotationDegrees(angle));
		}

		pose.translate(0D, (double) -0.2F, 0D);
		pose.mulPose(Vector3f.XP.rotationDegrees(-30F));
		pose.scale(f, f, f);
		this.eRender.render(entity, 0D, 0D, 0D, 0F, parTick, pose, buf, light);
		pose.popPose();
	}
}
