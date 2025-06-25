package sweetmagic.init.render.entity.model;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.ientity.IGolem;

public class WitchGolemModel<T extends LivingEntity & IGolem> extends HierarchicalModel<T> {

	public static final ModelLayerLocation LAYER = getLayer("witchgolem");
	private final ModelPart root;
	private final ModelPart head;
	private final ModelPart rightArm;
	private final ModelPart leftArm;
	private final ModelPart rightLeg;
	private final ModelPart leftLeg;

	public WitchGolemModel(ModelPart part) {
		this.root = part;
		this.head = part.getChild("head");
		this.rightArm = part.getChild("right_arm");
		this.leftArm = part.getChild("left_arm");
		this.rightLeg = part.getChild("right_leg");
		this.leftLeg = part.getChild("left_leg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition part = mesh.getRoot();
		part.addOrReplaceChild("head", SMBaseModel.getCubeList(0, 0).addBox(-4F, -12F, -5.5F, 8F, 10F, 8F).texOffs(24, 0).addBox(-1F, -5F, -7.5F, 2F, 4F, 2F), PartPose.offset(0F, -7F, -2F));
		part.addOrReplaceChild("body", SMBaseModel.getCubeList(0, 40).addBox(-9F, -2F, -6F, 18F, 12F, 11F).texOffs(0, 70).addBox(-4.5F, 10F, -3F, 9F, 5F, 6F, new CubeDeformation(0.5F)), PartPose.offset(0F, -7F, 0F));
		part.addOrReplaceChild("right_arm", SMBaseModel.getCubeList(60, 21).addBox(-13F, -2.5F, -3F, 4F, 30F, 6F), PartPose.offset(0F, -7F, 0F));
		part.addOrReplaceChild("left_arm", SMBaseModel.getCubeList(60, 58).addBox(9F, -2.5F, -3F, 4F, 30F, 6F), PartPose.offset(0F, -7F, 0F));
		part.addOrReplaceChild("right_leg", SMBaseModel.getCubeList(37, 0).addBox(-3.5F, -3F, -3F, 6F, 16F, 5F), PartPose.offset(-4F, 11F, 0F));
		part.addOrReplaceChild("left_leg", SMBaseModel.getCubeList(60, 0).mirror().addBox(-3.5F, -3F, -3F, 6F, 16F, 5F), PartPose.offset(5F, 11F, 0F));
		return LayerDefinition.create(mesh, 128, 128);
	}

	public ModelPart root() {
		return this.root;
	}

	public void setupAnim(T entity, float swing, float swingAmount, float ageTick, float headYaw, float headPitch) {
		this.head.yRot = entity.getShit() ? 0F : headYaw * ((float) Math.PI / 180F);
		this.head.xRot = entity.getShit() ? -5.5F : headPitch * ((float) Math.PI / 180F);
		this.rightLeg.xRot = -1.5F * Mth.triangleWave(swing, 13F) * swingAmount;
		this.leftLeg.xRot = 1.5F * Mth.triangleWave(swing, 13F) * swingAmount;
		this.rightLeg.yRot = 0F;
		this.leftLeg.yRot = 0F;
	}

	public void prepareMobModel(T entity, float par1, float par2, float par3) {

		int i = entity.getAttackTick();

		if (i > 0) {
			this.rightArm.xRot = -2F + 1.5F * Mth.triangleWave((float) i - par3, 10F);
			this.leftArm.xRot = -2F + 1.5F * Mth.triangleWave((float) i - par3, 10F);
		}

		else {
			this.rightArm.xRot = (-0.2F + 1.5F * Mth.triangleWave(par1, 13F)) * par2;
			this.leftArm.xRot = (-0.2F - 1.5F * Mth.triangleWave(par1, 13F)) * par2;
		}
	}

	public ModelPart getFlowerHoldingArm() {
		return this.rightArm;
	}

	public static ModelLayerLocation getLayer(String name) {
		return new ModelLayerLocation(SweetMagicCore.getSRC(name), "main");
	}
}
