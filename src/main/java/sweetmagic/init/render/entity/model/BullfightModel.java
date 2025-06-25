package sweetmagic.init.render.entity.model;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.entity.monster.boss.BullFight;

public class BullfightModel extends HierarchicalModel<BullFight> {

	public static final ModelLayerLocation LAYER = getLayer("bullfight");
	private final ModelPart root;
	private final ModelPart head;
	private final ModelPart rightHindLeg;
	private final ModelPart leftHindLeg;
	private final ModelPart rightFrontLeg;
	private final ModelPart leftFrontLeg;
	private final ModelPart neck;

	public BullfightModel(ModelPart root) {
		this.root = root;
		this.neck = root.getChild("neck");
		this.head = this.neck.getChild("head");
		this.rightHindLeg = root.getChild("right_hind_leg");
		this.leftHindLeg = root.getChild("left_hind_leg");
		this.rightFrontLeg = root.getChild("right_front_leg");
		this.leftFrontLeg = root.getChild("left_front_leg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition part = mesh.getRoot();

		PartDefinition neckPart = part.addOrReplaceChild("neck", SMBaseModel.getCubeList(68, 73).addBox(-5F, -1F, -18F, 10F, 10F, 18F), PartPose.offset(0F, -7F, 5.5F));
		PartDefinition headPart = neckPart.addOrReplaceChild("head", SMBaseModel.getCubeList(0, 0).addBox(-6F, -20F, -2F, 12F, 16F, 8F), PartPose.offset(0F, 16F, -17F));
		headPart.addOrReplaceChild("right_horn", SMBaseModel.getCubeList(74, 55).addBox(2F, -6F, 4F, 2F, 14F, 4F), PartPose.offsetAndRotation(-10F, -14F, -8F, 1F, 0F, 0F));
		headPart.addOrReplaceChild("left_horn", SMBaseModel.getCubeList(74, 55).mirror().addBox(-2F, -6F, 4F, 2F, 14F, 4F), PartPose.offsetAndRotation(8F, -14F, -8F, 1F, 0F, 0F));
		part.addOrReplaceChild("body", SMBaseModel.getCubeList(0, 55).addBox(-7F, -10F, -7F, 14F, 16F, 20F).texOffs(0, 91).addBox(-6F, 6F, -5F, 12F, 19F, 16F), PartPose.offsetAndRotation(0F, 1F, 2F, ((float) Math.PI / 2F), 0F, 0F));
		part.addOrReplaceChild("right_hind_leg", SMBaseModel.getCubeList(96, 0).addBox(-1F, 15F, 1F, 6F, 22F, 6F),PartPose.offset(-8F, -13F, 18F));
		part.addOrReplaceChild("left_hind_leg", SMBaseModel.getCubeList(96, 0).mirror().addBox(-5F, 15F, 1F, 6F, 22F, 6F), PartPose.offset(8F, -13F, 18F));
		part.addOrReplaceChild("right_front_leg", SMBaseModel.getCubeList(64, 0).addBox(-3F, 15F, -1F, 6F, 22F, 6F), PartPose.offset(-8F, -13F, -5F));
		part.addOrReplaceChild("left_front_leg", SMBaseModel.getCubeList(64, 0).mirror().addBox(-3F, 15F, -1F,6F, 22F, 6F), PartPose.offset(8F, -13F, -5F));
		return LayerDefinition.create(mesh, 128, 128);
	}

	public ModelPart root() {
		return this.root;
	}

	public void setupAnim(BullFight entity, float swing, float swingAmount, float ageTick, float headYaw, float headPitch) {

		this.head.xRot = 0.35F;
		this.head.yRot = headYaw * ((float) Math.PI / 180F);

		float f = 0.4F * swingAmount;
		float rate = 1F;
		this.rightHindLeg.xRot = Mth.cos(swing * rate) * f;
		this.leftHindLeg.xRot = Mth.cos(swing * rate + (float) Math.PI) * f;
		this.rightFrontLeg.xRot = Mth.cos(swing * rate + (float) Math.PI) * f;
		this.leftFrontLeg.xRot = Mth.cos(swing * rate) * f;
	}

	public static ModelLayerLocation getLayer(String name) {
		return new ModelLayerLocation(SweetMagicCore.getSRC(name), "main");
	}
}
