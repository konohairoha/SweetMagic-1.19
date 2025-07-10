package sweetmagic.init.render.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import sweetmagic.init.ItemInit;
import sweetmagic.init.entity.animal.WitchCat;
import sweetmagic.init.render.entity.model.WitchCatModel;

public class WitchCatLayer<T extends WitchCat, M extends WitchCatModel<T>> extends AbstractEntityLayer<T, M> {

	private static final ItemStack STACK = new ItemStack(ItemInit.cat_wing);

	public WitchCatLayer(RenderLayerParent<T, M> layer, EntityRendererProvider.Context con) {
		super(layer, con);
	}

	public void render(PoseStack pose, MultiBufferSource buf, int light, T entity) {
		this.renderAngelWingBig(entity, pose, buf, light, false);
		this.renderAngelWingBig(entity, pose, buf, light, true);
		this.renderArmWithItem(entity, pose, buf, light);
	}

	public void renderAngelWingBig(T entity, PoseStack pose, MultiBufferSource buf, int light, boolean isReverse) {
		float scale = 1.35F;
		float reverseRate = (isReverse ? -1F : 1F);
		pose.pushPose();
		pose.translate(-0.075F * reverseRate, 0.55F, -0.05F);
		pose.mulPose(Vector3f.YN.rotationDegrees((60F + 45F * Mth.sin(entity.tickCount * 0.2F)) * reverseRate));
		pose.scale(scale, -scale, scale);
		this.renderItemFix(entity, STACK, pose, buf, light);
		pose.popPose();
	}


	protected void renderArmWithItem(T entity, PoseStack pose, MultiBufferSource buf, int light) {
		ItemStack stack = entity.getMainHandItem();
		if (stack.isEmpty()) { return; }

		pose.pushPose();
		this.getParentModel().translateToHand(HumanoidArm.RIGHT, pose);
		pose.mulPose(Vector3f.XP.rotationDegrees(180F));
		pose.translate(-0.075D, -0.6D, 0.4D);
		this.renderItem(entity, stack, pose, buf, light);
		pose.popPose();
	}
}
