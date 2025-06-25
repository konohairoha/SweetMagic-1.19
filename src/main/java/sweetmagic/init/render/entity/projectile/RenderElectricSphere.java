package sweetmagic.init.render.entity.projectile;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import sweetmagic.init.BlockInit;
import sweetmagic.init.entity.projectile.ElectricSphere;

public class RenderElectricSphere<T extends ElectricSphere> extends RenderMagicBase<T> {

	private static final ItemStack STACK = new ItemStack(BlockInit.electricsphere);

	public RenderElectricSphere(EntityRendererProvider.Context con) {
		super(con);
	}

	@Override
	public void render(T entity, float yaw, float part, PoseStack pose, MultiBufferSource buf, int light) {
		pose.pushPose();
		pose.translate(0D, 0.5D, 0D);
		this.renderItem(pose, buf, light, STACK);
		pose.popPose();
	}
}
