package sweetmagic.init.render.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import sweetmagic.init.BlockInit;
import sweetmagic.init.entity.projectile.CalamityBomb;

public class RenderCalamityBomb<T extends CalamityBomb> extends RenderMagicBase<T> {

	private static final ItemStack STACK = new ItemStack(BlockInit.calamity_bomb);

	public RenderCalamityBomb(EntityRendererProvider.Context con) {
		super(con);
	}

	@Override
	public void render(T entity, float yaw, float part, PoseStack pose, MultiBufferSource buf, int light) {

		pose.pushPose();
		float size = 1F - entity.getCount() * 0.1F;
		double posY = 0.6D;

		switch (entity.getCount()) {
		case 0:
			size = 0.67F;
			posY = 0.45D;
			break;
		case 1:
			size = 0.85F;
			posY = 0.525D;
			break;
		}

		pose.translate(0D, posY, 0D);
		pose.scale(size, size, size);
		this.renderItem(pose, buf, light, STACK);
		pose.popPose();
	}
}
