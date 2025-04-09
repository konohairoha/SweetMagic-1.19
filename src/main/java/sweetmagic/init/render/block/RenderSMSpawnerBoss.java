package sweetmagic.init.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.entity.LivingEntity;
import sweetmagic.init.tile.sm.TileSMSpawnerBoss;
import sweetmagic.util.RenderUtil.RenderInfo;

public class RenderSMSpawnerBoss<T extends TileSMSpawnerBoss> extends RenderAbstractTile<T> {

	private int tickTime = 0;

	public RenderSMSpawnerBoss(BlockEntityRendererProvider.Context con) {
		super(con);
	}

	public void render(T tile, float parTick, RenderInfo info) {
		if (tile.isPeace || tile.getMobType() == -1) { return; }

		LivingEntity entity = tile.getRenderEntity();
		if (this.tickTime++ % 3 == 0) {
			entity.tickCount++;
			this.tickTime = 1;
		}

		float f = 0.675F;
		float f1 = Math.max(entity.getBbWidth(), entity.getBbHeight());
		if ((double) f1 > 1D) { f /= f1; }
		PoseStack pose = info.pose();
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
		this.eRender.render(entity, 0D, 0D, 0D, 0F, parTick, pose, info.buf(), info.light());
		pose.popPose();
	}
}
