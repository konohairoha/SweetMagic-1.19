package sweetmagic.init.render.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public class SMRobeModel<T extends LivingEntity> extends HumanoidModel<T> {

	public static final ModelLayerLocation LAYER = SMBaseModel.getLayer("sm_robe");
	protected static final float LIMB_SWING_WEIGHT = 0.85F;
	protected final static float TORADIAN = 0.017453292F;
	protected static final float ARM_SWING_AMOUNT = 60F * TORADIAN;
	protected static final float LEG_SWING_AMOUNT = 65F * TORADIAN;
	public final ModelPart jacket;
	public final ModelPart jacketLeftArm;
	public final ModelPart jacketRightArm;

	public SMRobeModel(ModelPart root) {
		super(root);
		this.jacket = this.body.getChild("jacket");
		this.jacketLeftArm = this.leftArm.getChild("jacket_left_arm");
		this.jacketRightArm = this.rightArm.getChild("jacket_right_arm");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition part = mesh.getRoot();

		part.addOrReplaceChild("head", SMBaseModel.getCubeList(0, 0).addBox(-4F, -7F, -4F, 8F, 8F, 8F, SMBaseModel.getCube(-0.5F)), PartPose.offset(0F, 0F, 0F));
		PartDefinition body = part.addOrReplaceChild("body", SMBaseModel.getCubeList(16, 16).addBox(-4F, 0F, -2F, 8F, 12F, 4F, SMBaseModel.getCube(0.25F)), PartPose.offset(-1F, 0F, 0F));
		body.addOrReplaceChild("jacket", SMBaseModel.getCubeList(16, 0).addBox(-4F, 0F, -2F, 8F, 12F, 4F, SMBaseModel.getCube(0.45F)), PartPose.offset(0F, 0F, 0F));
		part.addOrReplaceChild("hat", SMBaseModel.getCubeList(64, 32).addBox(-3F, 0F, -2F, 8F, 12F, 4F, SMBaseModel.getCube(0.35F)), PartPose.offset(-1F, 0.3F, -0.15F));

		PartDefinition left_arm = part.addOrReplaceChild("left_arm", SMBaseModel.getCubeList(40, 16).addBox(-1F, -2F, -2F, 4F, 12F, 4F, SMBaseModel.getCube(0.3F)), PartPose.offset(-5F, 1.8F, 0F));
		left_arm.addOrReplaceChild("jacket_left_arm", SMBaseModel.getCubeList(40, 0).addBox(-1F, -2F, -2F, 4F, 12F, 4F, SMBaseModel.getCube(0.45F)), PartPose.offset(0F, 0F, 0F));
		PartDefinition right_arm = part.addOrReplaceChild("right_arm", SMBaseModel.getCubeList(40, 16).addBox(-3F, -2F, -2F, 4F, 12F, 4F, SMBaseModel.getCube(0.3F)), PartPose.offset(5F, 1.8F, 0F));
		right_arm.addOrReplaceChild("jacket_right_arm", SMBaseModel.getCubeList(40, 0).addBox(-3F, -2F, -2F, 4F, 12F, 4F, SMBaseModel.getCube(0.45F)), PartPose.offset(0F, 0F, 0F));
		part.addOrReplaceChild("left_leg", SMBaseModel.getCubeList(0, 0).addBox(-1F, -2F, -2F, 4F, 12F, 4F, SMBaseModel.getCube(0.3F)), PartPose.offset(1F, 14F, 0F));
		part.addOrReplaceChild("right_leg", SMBaseModel.getCubeList(0, 16).addBox(-1F, -2F, -2F, 4F, 12F, 4F, SMBaseModel.getCube(0.3F)), PartPose.offset(1F, 14F, 0F));
		part.addOrReplaceChild("headdress", SMBaseModel.getCubeList(16, 16).addBox(-3F, 0F, 0F, 10F, 3F, 1F, SMBaseModel.getCube(0F)), PartPose.offset(-2F, 10F, -3F));
		return LayerDefinition.create(mesh, 64, 32);
	}

	@Override
	public void renderToBuffer(PoseStack pose, VertexConsumer ver, int light, int overlay, float red, float green, float blue, float alpha) {
		this.head.visible = false;
		this.hat.visible = false;
		this.body.visible = true;
		this.rightLeg.visible = true;
		this.rightLeg.x = -3.1F;
		this.rightLeg.y = 14F;
		this.leftLeg.visible = true;
		this.leftLeg.x = 1.1F;
		this.leftLeg.y = 14F;
		super.renderToBuffer(pose, ver, light, overlay, red, green, blue, alpha);
	}

	public void armorView(T entity, PoseStack pose, float swing, float swingAmount) {

		this.body.x = 0F;
		Vector3f vec = new Vector3f(Mth.cos(swing * 0.662F * LIMB_SWING_WEIGHT / entity.getScale()) * swingAmount, 0F, 0F);

		Vector3f base = vec.copy();
		base.mul(ARM_SWING_AMOUNT);
		Vector3f mirror = base.copy();
		mirror.mul(-1F);
		this.leftArm.resetPose();
		this.rightArm.resetPose();
		this.leftArm.offsetRotation(base);
		this.rightArm.offsetRotation(mirror);

		base = vec.copy();
		base.mul(LEG_SWING_AMOUNT);
		mirror = base.copy();
		mirror.mul(-1F);
		this.rightLeg.resetPose();
		this.leftLeg.resetPose();
		this.rightLeg.offsetRotation(base);
		this.leftLeg.offsetRotation(mirror);

		float scale = 1F / 1.1F;
		this.rightArm.xScale = scale;
		this.rightArm.yScale = scale;
		this.rightArm.zScale = scale;
		this.leftArm.xScale = scale;
		this.leftArm.yScale = scale;
		this.leftArm.zScale = scale;
		this.rightArm.x = 6.75F;
		this.leftArm.x = -6.5F;
	}

	public void translateAndRotate(ModelPart arm, PoseStack pose) {
		pose.translate((double) (arm.x / 16F), (double) (arm.y / 16F), (double) (arm.z / 16F));
		if (arm.zRot != 0F) {
			pose.mulPose(Vector3f.ZP.rotation(arm.zRot));
		}

		if (arm.yRot != 0F) {
			pose.mulPose(Vector3f.YP.rotation(arm.yRot));
		}

		if (arm.xRot != 0F) {
			pose.mulPose(Vector3f.XP.rotation(arm.xRot));
		}

		if (arm.xScale != 1F || arm.yScale != 1F || arm.zScale != 1F) {
			pose.scale(arm.xScale, arm.yScale, arm.zScale);
		}
	}
}
