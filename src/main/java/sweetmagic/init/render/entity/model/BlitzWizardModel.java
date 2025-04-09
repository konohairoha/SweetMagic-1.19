package sweetmagic.init.render.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import sweetmagic.init.entity.monster.boss.BlitzWizardMaster;

public class BlitzWizardModel<T extends BlitzWizardMaster> extends SMBaseModel<T> {

	// モデルの登録のために、他と被らない名前でResourceLocationを登録しておく
	public static final ModelLayerLocation LAYER = getLayer("blitz_wizard");

	// まとめて描画するためにrootを取得
	// そのほか、アニメーションしたい部位に応じてModelPartを取得しておく
	public BlitzWizardModel(ModelPart root) {
		super(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition part = mesh.getRoot();

		part.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4F, -7F, -4F, 8F, 8F, 8F, new CubeDeformation(-0.5F)), PartPose.offset(0F, 0F, 0F));
		part.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-3F, 0F, -2F, 8F, 12F, 4F, new CubeDeformation(0F)), PartPose.offset(-1F, 0F, 0F));
		part.addOrReplaceChild("jacket", CubeListBuilder.create().texOffs(40, 0).addBox(-3F, 0F, -2F, 8F, 15F, 4F, new CubeDeformation(0.35F)), PartPose.offset(-1F, 0.1F, 0F));

		part.addOrReplaceChild("armLeft", CubeListBuilder.create().texOffs(40, 32).addBox(-2F, -2F, -2F, 4F, 12F, 4F, new CubeDeformation(-0.2F)), PartPose.offset(-5.75F, 1.8F, 0F));
		part.addOrReplaceChild("armRight", CubeListBuilder.create().texOffs(40, 32).addBox(-2F, -2F, -2F, 4F, 12F, 4F, new CubeDeformation(-0.2F)), PartPose.offset(5.75F, 1.8F, 0F));
		part.addOrReplaceChild("armJacketLeft", CubeListBuilder.create().texOffs(40, 48).addBox(-2F, -2F, -2F, 4F, 12F, 4F, new CubeDeformation(0F)), PartPose.offset(-5.75F, 1.95F, 0F));
		part.addOrReplaceChild("armJacketRight", CubeListBuilder.create().texOffs(40, 48).addBox(-2F, -2F, -2F, 4F, 12F, 4F, new CubeDeformation(0F)), PartPose.offset(5.75F, 1.95F, 0F));

		part.addOrReplaceChild("legLeft", CubeListBuilder.create().texOffs(0, 16).addBox(-1F, -2F, -2F, 4F, 12F, 4F, new CubeDeformation(-0.075F)), PartPose.offset(1F, 14F, 0F));
		part.addOrReplaceChild("legRight", CubeListBuilder.create().texOffs(0, 16).addBox(-1F, -2F, -2F, 4F, 12F, 4F, new CubeDeformation(-0.075F)), PartPose.offset(-3F, 14F, 0F));

		part.addOrReplaceChild("body2", CubeListBuilder.create().texOffs(0, 39).addBox(-8F, 0F, -2F, 16F, 4F, 4F, new CubeDeformation(0.4F)), PartPose.offset(0F, 0F, 0F));

		return LayerDefinition.create(mesh, 64, 64);
	}

	public void setupAnim(T entity, float swing, float swingAmount, float ageTick, float headYaw, float headPitch) {
		super.setupAnim(entity, swing, swingAmount, ageTick, headYaw, headPitch);

		if (!entity.getUpBook()) {
			this.armLeft.xRot = this.armLeft.xRot + -0.25F;
			this.armJacketLeft.xRot = this.armJacketLeft.xRot + -0.25F;
			this.armLeft.y = this.armLeft.y + 0.25F;
			this.armJacketLeft.y = this.armJacketLeft.y + 0.25F;
		}
	}

	public void swingArms(Vector3f vec) {
		Vector3f base = vec.copy();
		base.mul(ARM_SWING_AMOUNT);
		Vector3f mirror = base.copy();
		mirror.mul(-1F);
		this.armLeft.resetPose();
		this.armRight.resetPose();
		this.armLeft.offsetRotation(base);
		this.armRight.offsetRotation(mirror);
		this.armJacketLeft.resetPose();
		this.armJacketRight.resetPose();
		this.armJacketLeft.offsetRotation(base);
		this.armJacketRight.offsetRotation(mirror);
	}

	public ModelPart getArm(boolean isLeft) {
		return isLeft ? this.armRight : this.armLeft ;
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
