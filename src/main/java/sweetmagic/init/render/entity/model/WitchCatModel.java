package sweetmagic.init.render.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.ModelUtils;
import net.minecraft.client.model.OcelotModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.entity.animal.WitchCat;

public class WitchCatModel<T extends WitchCat> extends OcelotModel<T> {

	private float downAmount;
	private float downAmountTail;
	private float relaxStateOneAmount;
	public static final ModelLayerLocation LAYER = getLayer("witchcat");

	public WitchCatModel(ModelPart part) {
		super(part);
	}

	public static LayerDefinition createBodyLayer() {
		CubeDeformation cube = new CubeDeformation(0.001F);
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition part = mesh.getRoot();
		part.addOrReplaceChild("head", CubeListBuilder.create().addBox("main", -2.5F, -2F, -3F, 5F, 4F, 5F, cube).addBox("nose", -1.5F, -0.001F, -4F, 3, 2, 2, cube, 0, 24).addBox("ear1", -2F, -3F, 0F, 1, 1, 2, cube, 0, 10).addBox("ear2", 1F, -3F, 0F, 1, 1, 2, cube, 6, 10), PartPose.offset(0F, 15F, -9F));
		part.addOrReplaceChild("body", SMBaseModel.getCubeList(20, 0).addBox(-2F, 3F, -8F, 4F, 16F, 6F, cube), PartPose.offsetAndRotation(0F, 12F, -10F, ((float)Math.PI / 2F), 0F, 0F));
		part.addOrReplaceChild("tail1", SMBaseModel.getCubeList(0, 15).addBox(-0.5F, 0F, 0F, 1F, 8F, 1F, cube), PartPose.offsetAndRotation(0F, 15F, 8F, 0.9F, 0F, 0F));
		part.addOrReplaceChild("tail2", SMBaseModel.getCubeList(4, 15).addBox(-0.5F, 0F, 0F, 1F, 8F, 1F, cube), PartPose.offset(0F, 20F, 14F));
		CubeListBuilder build = SMBaseModel.getCubeList(8, 13).addBox(-1F, 0F, 1F, 2F, 6F, 2F, cube);
		part.addOrReplaceChild("left_hind_leg", build, PartPose.offset(1.1F, 18F, 5F));
		part.addOrReplaceChild("right_hind_leg", build, PartPose.offset(-1.1F, 18F, 5F));
		CubeListBuilder build1 = SMBaseModel.getCubeList(40, 0).addBox(-1F, 0F, 0F, 2F, 10F, 2F, cube);
		part.addOrReplaceChild("left_front_leg", build1, PartPose.offset(1.2F, 14.1F, -5F));
		part.addOrReplaceChild("right_front_leg", build1, PartPose.offset(-1.2F, 14.1F, -5F));
		return LayerDefinition.create(mesh, 64, 32);
	}

	public void prepareMobModel(T entity, float par1, float par2, float par3) {

		this.downAmount = 0F;
		this.downAmountTail = 0F;
		this.relaxStateOneAmount = 0F;

		if (this.downAmount <= 0F) {
			this.head.xRot = 0F;
			this.head.zRot = 0F;
			this.leftFrontLeg.xRot = 0F;
			this.leftFrontLeg.zRot = 0F;
			this.rightFrontLeg.xRot = 0F;
			this.rightFrontLeg.zRot = 0F;
			this.rightFrontLeg.x = -1.2F;
			this.leftHindLeg.xRot = 0F;
			this.rightHindLeg.xRot = 0F;
			this.rightHindLeg.zRot = 0F;
			this.rightHindLeg.x = -1.1F;
			this.rightHindLeg.y = 18F;
		}

		this.body.y = 12F;
		this.body.z = -10F;
		this.head.y = 15F;
		this.head.z = -9F;
		this.tail1.y = 15F;
		this.tail1.z = 8F;
		this.tail2.y = 20F;
		this.tail2.z = 14F;
		this.leftFrontLeg.y = 14.1F;
		this.leftFrontLeg.z = -5F;
		this.rightFrontLeg.y = 14.1F;
		this.rightFrontLeg.z = -5F;
		this.leftHindLeg.y = 18F;
		this.leftHindLeg.z = 5F;
		this.rightHindLeg.y = 18F;
		this.rightHindLeg.z = 5F;
		this.tail1.xRot = 0.9F;

		if (entity.isCrouching()) {
			++this.body.y;
			this.head.y += 2F;
			++this.tail1.y;
			this.tail2.y += -4F;
			this.tail2.z += 2F;
			this.tail1.xRot = ((float) Math.PI / 2F);
			this.tail2.xRot = ((float) Math.PI / 2F);
			this.state = 0;
		}

		else if (entity.isSprinting()) {
			this.tail2.y = this.tail1.y;
			this.tail2.z += 2F;
			this.tail1.xRot = ((float) Math.PI / 2F);
			this.tail2.xRot = ((float) Math.PI / 2F);
			this.state = 2;
		}

		else {
			this.state = 1;
		}

		float ageSin = Mth.sin(entity.tickCount * 0.0875F) * 0.1F;
		float ageCos = Mth.cos(entity.tickCount * 0.0875F) * 0.1F;
		this.body.xRot = ((float) Math.PI / 4F);
		this.body.y += -5.25F;
		this.body.z += 5F;
		this.head.y += -3.3F;
		++this.head.z;

		if (entity.getShit()) {
			this.tail1.y += 8F;
			this.tail1.z = 5F;
			this.tail1.xRot = 0.5F;
			this.tail2.y += 9.5F;
			this.tail2.z = 8.75F;
			this.tail2.xRot = 0.1F;
		}

		else {
			this.tail1.y += 8F;
			this.tail1.z += -2F;
			this.tail2.y += 2F;
			this.tail2.z += -0.8F;
			this.tail1.xRot = 1.7278761F;
			this.tail2.xRot = 2.670354F;
		}

		this.leftFrontLeg.xRot = ageSin;
		this.leftFrontLeg.y = 16.1F;
		this.leftFrontLeg.z = -7F;
		this.rightFrontLeg.xRot = -ageSin;
		this.rightFrontLeg.y = 16.1F;
		this.rightFrontLeg.z = -7F;
		this.leftHindLeg.xRot = -ageCos;
		this.leftHindLeg.y = 24.5F;
		this.leftHindLeg.z = 1F;
		this.rightHindLeg.xRot = ageCos;
		this.rightHindLeg.y = 24.5F;
		this.rightHindLeg.z = 1F;
		this.state = 3;
	}

	public void setupAnim(T entity, float swing, float swingAmount, float ageTick, float headYaw, float headPitch) {

		this.head.xRot = headPitch * ((float) Math.PI / 180F);
		this.head.yRot = headYaw * ((float) Math.PI / 180F);

		if (this.state != 3) {
			this.body.xRot = ((float) Math.PI / 2F);

			if (this.state == 2) {
				this.leftHindLeg.xRot = Mth.cos(swing * 0.6662F) * swingAmount;
				this.rightHindLeg.xRot = Mth.cos(swing * 0.6662F + 0.3F) * swingAmount;
				this.leftFrontLeg.xRot = Mth.cos(swing * 0.6662F + (float) Math.PI + 0.3F) * swingAmount;
				this.rightFrontLeg.xRot = Mth.cos(swing * 0.6662F + (float) Math.PI) * swingAmount;
				this.tail2.xRot = 1.7278761F + ((float) Math.PI / 10F) * Mth.cos(swing) * swingAmount;
			}

			else {
				this.leftHindLeg.xRot = Mth.cos(swing * 0.6662F) * swingAmount;
				this.rightHindLeg.xRot = Mth.cos(swing * 0.6662F + (float) Math.PI) * swingAmount;
				this.leftFrontLeg.xRot = Mth.cos(swing * 0.6662F + (float) Math.PI) * swingAmount;
				this.rightFrontLeg.xRot = Mth.cos(swing * 0.6662F) * swingAmount;

				if (this.state == 1) {
					this.tail2.xRot = 1.7278761F + ((float) Math.PI / 4F) * Mth.cos(swing) * swingAmount;
				}

				else {
					this.tail2.xRot = 1.7278761F + 0.47123894F * Mth.cos(swing) * swingAmount;
				}
			}
		}

		if (this.downAmount > 0F) {
			this.head.zRot = ModelUtils.rotlerpRad(this.head.zRot, -1.2707963F, this.downAmount);
			this.head.yRot = ModelUtils.rotlerpRad(this.head.yRot, 1.2707963F, this.downAmount);
			this.leftFrontLeg.xRot = -1.2707963F;
			this.rightFrontLeg.xRot = -0.47079635F;
			this.rightFrontLeg.zRot = -0.2F;
			this.rightFrontLeg.x = -0.2F;
			this.leftHindLeg.xRot = -0.4F;
			this.rightHindLeg.xRot = 0.5F;
			this.rightHindLeg.zRot = -0.5F;
			this.rightHindLeg.x = -0.3F;
			this.rightHindLeg.y = 20F;
			this.tail1.xRot = ModelUtils.rotlerpRad(this.tail1.xRot, 0.8F, this.downAmountTail);
			this.tail2.xRot = ModelUtils.rotlerpRad(this.tail2.xRot, -0.4F, this.downAmountTail);
		}

		if (this.relaxStateOneAmount > 0F) {
			this.head.xRot = ModelUtils.rotlerpRad(this.head.xRot, -0.58177644F, this.relaxStateOneAmount);
		}
	}

	public void translateToHand(HumanoidArm arm, PoseStack pose) {
		this.head.translateAndRotate(pose);
		pose.translate(0D, -0.09375D, 0.09375D);
		pose.scale(0.7F, 0.7F, 0.7F);
		pose.translate(0.0625D, 0D, 0D);
	}

	public static ModelLayerLocation getLayer(String name) {
		return new ModelLayerLocation(SweetMagicCore.getSRC(name), "main");
	}
}
