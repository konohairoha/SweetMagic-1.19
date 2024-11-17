package sweetmagic.init.render.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.LivingEntity;
import sweetmagic.SweetMagicCore;

public class PorchModel extends HumanoidModel<LivingEntity> {

	// モデルの登録のために、他と被らない名前でResourceLocationを登録しておく
	public static final ModelLayerLocation LAYER = new ModelLayerLocation(SweetMagicCore.getSRC("sm_porch"), "main");

	public PorchModel(ModelPart root) {
		super(root);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition part = mesh.getRoot();

		part.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4F, -7F, -4F, 8F, 8F, 8F, new CubeDeformation(-0.5F)), PartPose.offset(0F, 0F, 0F));

		PartDefinition bodyPart = part.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-4F, 0F, -2F, 8F, 12F, 4F, new CubeDeformation(0.25F)), PartPose.offset(-1F, 0F, 0F));
		part.addOrReplaceChild("hat", CubeListBuilder.create().texOffs(64, 32).addBox(-3F, 0F, -2F, 8F, 12F, 4F, new CubeDeformation(0.35F)), PartPose.offset(-1F, 0.3F, -0.15F));

		part.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(40, 16).addBox(-1F, -2F, -2F, 4F, 12F, 4F, new CubeDeformation(0.1F)), PartPose.offset(-5F, 1.8F, 0F));
		part.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 16).addBox(-3F, -2F, -2F, 4F, 12F, 4F, new CubeDeformation(0.1F)), PartPose.offset(5F, 1.8F, 0F));

		part.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 32).addBox(-1F, -2F, -2F, 4F, 12F, 4F, new CubeDeformation(-0.075F)), PartPose.offset(1F, 14F, 0F));
		part.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 32).addBox(-1F, -2F, -2F, 4F, 12F, 4F, new CubeDeformation(-0.075F)), PartPose.offset(-3F, 14F, 0F));

		bodyPart.addOrReplaceChild("porch", CubeListBuilder.create().texOffs(50, 0).addBox(-4F, 2F, -2F, 6F, 4F, 1F, new CubeDeformation(0F)), PartPose.offset(0F, 7.5F, 4F));

		return LayerDefinition.create(mesh, 64, 32);
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
