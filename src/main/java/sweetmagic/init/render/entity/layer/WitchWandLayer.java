package sweetmagic.init.render.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.init.entity.animal.AbstractWitch;
import sweetmagic.init.render.entity.model.SMWitchModel;

public class WitchWandLayer <T extends Mob, M extends EntityModel<T>> extends AbstractEntityLayer<T, M> {

	public WitchWandLayer(RenderLayerParent<T, M> layer, EntityRendererProvider.Context con) {
		super(layer, con);
	}

	public void render(PoseStack pose, MultiBufferSource buf, int light, T entity, float swing, float swingAmount, float parTick, float ageTick, float netHeadYaw, float headPitch) {
		this.renderArmWithItem(entity, pose, buf, light);

		if (this.getParentModel() instanceof SMWitchModel model) {
			this.renderHead(entity, model.getHead(), pose, buf, light, 0.67F, -0.33F, -0.1F, -0.33F);
		}
	}

	protected void renderArmWithItem(T entity, PoseStack pose, MultiBufferSource buf, int light) {

		pose.pushPose();
		boolean flag = true;
		ItemStack wand = ItemStack.EMPTY;

		if (entity instanceof ISMMob smMob) {

			wand = smMob.getStack();

			if (!smMob.isTarget()) {
				flag = false;
				pose.mulPose(Vector3f.XP.rotationDegrees(90F));
				pose.mulPose(Vector3f.ZP.rotationDegrees(270F));
				pose.mulPose(Vector3f.XN.rotationDegrees(-10F));
				pose.translate(-0.17D, -0.275D, -0.425D);
			}
		}

		else if (entity instanceof AbstractWitch witch) {

			wand = witch.getStack();

			if (witch.getShit()) {
				flag = false;
				pose.mulPose(Vector3f.XP.rotationDegrees(90F));
				pose.mulPose(Vector3f.ZP.rotationDegrees(270F));
				pose.mulPose(Vector3f.XN.rotationDegrees(-10F));
				pose.translate(-0.17D, -0.275D, -0.425D);
			}
		}

		if(flag) {

			if (this.getParentModel() instanceof SMWitchModel model) {
				model.translateAndRotate(model.getArm(false), pose);
			}

			pose.mulPose(Vector3f.XP.rotationDegrees(225F));
			pose.mulPose(Vector3f.YP.rotationDegrees(180F));
			pose.translate(0.0D, -0.2D, -0.5D);
		}

		this.render.renderItem(entity, wand, ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, false, pose, buf, light);
		pose.popPose();
	}
}
