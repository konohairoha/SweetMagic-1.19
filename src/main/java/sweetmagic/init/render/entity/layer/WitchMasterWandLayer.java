package sweetmagic.init.render.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.init.ItemInit;
import sweetmagic.init.entity.monster.boss.WindWitchMaster;
import sweetmagic.init.render.entity.model.WindWitchModel;

public class WitchMasterWandLayer <T extends WindWitchMaster, M extends EntityModel<T>> extends AbstractEntityLayer<T, M> {

	private static final ItemStack WAND = new ItemStack(ItemInit.deuscrystal_wand_g);
	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/entity/windwitch_master.png");

	public WitchMasterWandLayer(RenderLayerParent<T, M> layer, EntityRendererProvider.Context con) {
		super(layer, con);
		this.setModel(new WindWitchModel<>(con.getModelSet().bakeLayer(WindWitchModel.LAYER)));
	}

	public void render(PoseStack pose, MultiBufferSource buf, int light, T entity, float swing, float swingAmount, float parTick, float ageTick, float netHeadYaw, float headPitch) {
		this.renderArmWithItem(entity, pose, buf, light);
		this.renderShadow(entity, pose, buf, swing, swingAmount, parTick, light, ageTick, netHeadYaw, headPitch, 0.5F, -0.65F, 1.15F);
	}

	protected void renderArmWithItem(T entity, PoseStack pose, MultiBufferSource buf, int light) {

		pose.pushPose();
		ISMMob smMob = (ISMMob) entity;

		if (!smMob.isTarget()) {
			pose.mulPose(Vector3f.XP.rotationDegrees(90F));
			pose.mulPose(Vector3f.ZP.rotationDegrees(270F));
			pose.mulPose(Vector3f.XN.rotationDegrees(-10F));
			pose.translate(-0.17D, -0.275D, -0.425D);
		}

		else {

			if (this.getParentModel() instanceof WindWitchModel model) {
				model.translateAndRotate(model.getArm(false), pose);
			}

			pose.mulPose(Vector3f.XP.rotationDegrees(225F));
			pose.mulPose(Vector3f.YP.rotationDegrees(180F));
			pose.translate(0.0D, -0.2D, -0.55D);
		}

		this.render.renderItem(entity, WAND, ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, false, pose, buf, light);
		pose.popPose();
	}

	protected ResourceLocation getTex() {
		return TEX;
	}
}
