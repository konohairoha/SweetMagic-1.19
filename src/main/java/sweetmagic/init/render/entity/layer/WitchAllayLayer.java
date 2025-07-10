package sweetmagic.init.render.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import sweetmagic.init.ItemInit;
import sweetmagic.init.entity.animal.WitchAllay;
import sweetmagic.init.render.entity.model.WitchAllayModel;

public class WitchAllayLayer<T extends WitchAllay, M extends WitchAllayModel<T>> extends AbstractEntityLayer<T, M> {

	private static final ItemStack WAND = new ItemStack(ItemInit.divine_wand);

	public WitchAllayLayer(RenderLayerParent<T, M> layer, EntityRendererProvider.Context con) {
		super(layer, con);
		this.setModel(new WitchAllayModel<>(this.getModel(con, WitchAllayModel.LAYER)));
	}

	public void render(PoseStack pose, MultiBufferSource buf, int light, T entity) {
		this.renderArmWithItem(entity, pose, buf, light);
	}

	protected void renderArmWithItem(T entity, PoseStack pose, MultiBufferSource buf, int light) {
		pose.pushPose();

		if (entity.getShit()) {
			this.getParentModel().translateToBody(pose);
			pose.mulPose(Vector3f.XP.rotationDegrees(90F));
			pose.mulPose(Vector3f.ZP.rotationDegrees(270F));
			pose.mulPose(Vector3f.XN.rotationDegrees(-10F));
			pose.translate(-0.1D, -0.5D, -1.4D);
		}

		else {
			this.getParentModel().translateToHand(HumanoidArm.RIGHT, pose);
			pose.mulPose(Vector3f.XP.rotationDegrees(-140F));
			pose.mulPose(Vector3f.YP.rotationDegrees(180F));
			pose.translate(0.225D, 0D, -0.525D);
		}

		pose.scale(0.85F, 0.85F, 0.85F);
		this.renderItem(entity, WAND, pose, buf, light);
		pose.popPose();
	}
}
