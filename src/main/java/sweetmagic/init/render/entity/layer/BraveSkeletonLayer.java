package sweetmagic.init.render.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import sweetmagic.init.entity.monster.boss.BraveSkeleton;

public class BraveSkeletonLayer<T extends BraveSkeleton, M extends HumanoidModel<T>> extends AbstractEntityLayer<T, M> {

	private static final ItemStack SWORD = new ItemStack(Items.DIAMOND_SWORD);
	private static final ItemStack BOW = new ItemStack(Items.BOW);

	public BraveSkeletonLayer(RenderLayerParent<T, M> layer, EntityRendererProvider.Context con) {
		super(layer, con);
	}

	public void render(PoseStack pose, MultiBufferSource buf, int light, T entity) {
		ItemStack tool1 = entity.getArrow() ? BOW : SWORD;
		ItemStack tool2 = entity.getArrow() ? SWORD : BOW;
		this.renderItem(entity, pose, buf, light, tool1, true);
		this.renderItem(entity, pose, buf, light, tool2, false);
	}

	protected void renderItem(T entity, PoseStack pose, MultiBufferSource buf, int light, ItemStack stack, boolean isHand) {

		pose.pushPose();
		boolean isSword = stack.is(Items.DIAMOND_SWORD);

		if (isSword) {
			pose.scale(1.25F, 1.75F, 1.25F);
		}

		if (isHand) {
			double addX = isSword ? -0.05D : 0D;
			double addZ = isSword ? 0.125D : 0D;
			this.getParentModel().translateToHand(HumanoidArm.RIGHT, pose);
			pose.mulPose(Vector3f.XP.rotationDegrees(-90F));
			pose.mulPose(Vector3f.YP.rotationDegrees(180F));
			pose.translate(0D + addX, 0.15D, -0.65D + addZ);
		}

		else {

			double addY = isSword ? -0.5D : 0D;
			double addZ = isSword ? 0.15D : 0D;

			if (isSword) {
				pose.mulPose(Vector3f.ZP.rotationDegrees(-30F));
				pose.mulPose(Vector3f.YP.rotationDegrees(-90F));
			}

			else {
				pose.mulPose(Vector3f.XP.rotationDegrees(10F));
				pose.mulPose(Vector3f.YP.rotationDegrees(-90F));
			}

			pose.translate(0.1D, 0.5D + addY, 0D + addZ);
		}

		this.renderItem(entity, stack, pose, buf, light);
		pose.popPose();
	}
}
