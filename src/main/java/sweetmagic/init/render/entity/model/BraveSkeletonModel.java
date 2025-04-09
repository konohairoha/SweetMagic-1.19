package sweetmagic.init.render.entity.model;

import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.entity.monster.boss.BraveSkeleton;

public class BraveSkeletonModel<T extends BraveSkeleton> extends HumanoidModel<T> {

	public static final ModelLayerLocation LAYER = getLayer("brave_skeleton");

	public BraveSkeletonModel(ModelPart part) {
		super(part);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = HumanoidModel.createMesh(CubeDeformation.NONE, 0F);
		PartDefinition part = mesh.getRoot();
		part.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 16).addBox(-1F, -2F, -1F, 2F, 12F, 2F), PartPose.offset(-5F, 2F, 0F));
		part.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(40, 16).mirror().addBox(-1F, -2F, -1F, 2F, 12F, 2F), PartPose.offset(5F, 2F, 0F));
		part.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 16).addBox(-1F, 0F, -1F, 2F, 12F, 2F), PartPose.offset(-2F, 12F, 0F));
		part.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 16).mirror().addBox(-1F, 0F, -1F, 2F, 12F, 2F), PartPose.offset(2F, 12F, 0F));
		return LayerDefinition.create(mesh, 64, 32);
	}

	public void prepareMobModel(T entity, float par1, float par2, float par3) {
		this.rightArmPose = HumanoidModel.ArmPose.BOW_AND_ARROW;
		super.prepareMobModel(entity, par1, par2, par3);
	}

	public void setupAnim(T entity, float swing, float swingAmount, float ageTick, float headYaw, float headPitch) {
		super.setupAnim(entity, swing, swingAmount, ageTick, headYaw, headPitch);

		float f = Mth.sin(this.attackTime * (float) Math.PI);
		float f1 = Mth.sin((1F - (1F - this.attackTime) * (1F - this.attackTime)) * (float) Math.PI);
		this.rightArm.zRot = 0F;
		this.leftArm.zRot = 0F;
		this.rightArm.yRot = -(0.1F - f * 0.6F);
		this.leftArm.yRot = 0.1F - f * 0.6F;
		this.rightArm.xRot = (-(float) Math.PI / 2F);
		this.leftArm.xRot = (-(float) Math.PI / 2F);
		this.rightArm.xRot -= f * 1.2F - f1 * 0.4F;
		this.leftArm.xRot -= f * 1.2F - f1 * 0.4F;
		AnimationUtils.bobArms(this.rightArm, this.leftArm, ageTick);
	}

	public static ModelLayerLocation getLayer(String name) {
		return new ModelLayerLocation(SweetMagicCore.getSRC(name), "main");
	}
}
