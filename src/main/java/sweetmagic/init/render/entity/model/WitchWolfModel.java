package sweetmagic.init.render.entity.model;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.model.ColorableAgeableListModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.entity.animal.WitchWolf;

@OnlyIn(Dist.CLIENT)
public class WitchWolfModel<T extends LivingEntity> extends ColorableAgeableListModel<T> {

	public static final ModelLayerLocation LAYER = getLayer("witchwolf");

	private final ModelPart head;
	private final ModelPart realHead;
	private final ModelPart body;
	private final ModelPart rightHindLeg;
	private final ModelPart leftHindLeg;
	private final ModelPart rightFrontLeg;
	private final ModelPart leftFrontLeg;
	private final ModelPart tail;
	private final ModelPart realTail;
	private final ModelPart upperBody;

	public WitchWolfModel(ModelPart par) {
		this.head = par.getChild("head");
		this.realHead = this.head.getChild("real_head");
		this.body = par.getChild("body");
		this.upperBody = par.getChild("upper_body");
		this.rightHindLeg = par.getChild("right_hind_leg");
		this.leftHindLeg = par.getChild("left_hind_leg");
		this.rightFrontLeg = par.getChild("right_front_leg");
		this.leftFrontLeg = par.getChild("left_front_leg");
		this.tail = par.getChild("tail");
		this.realTail = this.tail.getChild("real_tail");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition par = mesh.getRoot();

		PartDefinition par1 = par.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(-1F, 13.5F, -7F));
		par1.addOrReplaceChild("real_head", CubeListBuilder.create().texOffs(0, 0).addBox(-2F, -3F, -2F, 6F, 6F, 4F).texOffs(16, 14).addBox(-2F, -5F, 0F, 2F, 2F, 1F).texOffs(16, 14).addBox(2F, -5F, 0F, 2F, 2F, 1F).texOffs(0, 10).addBox(-0.5F, -0.001F, -5F, 3F, 3F, 4F), PartPose.ZERO);
		par.addOrReplaceChild("body", CubeListBuilder.create().texOffs(18, 14).addBox(-3F, -2F, -3F, 6F, 9F, 6F), PartPose.offsetAndRotation(0F, 14F, 2F, ((float) Math.PI / 2F), 0F, 0F));
		par.addOrReplaceChild("upper_body", CubeListBuilder.create().texOffs(21, 0).addBox(-3F, -3F, -3F, 8F, 6F, 7F), PartPose.offsetAndRotation(-1F, 14F, -3F, ((float) Math.PI / 2F), 0F, 0F));
		CubeListBuilder cube = CubeListBuilder.create().texOffs(0, 18).addBox(0F, 0F, -1F, 2F, 8F, 2F);
		par.addOrReplaceChild("right_hind_leg", cube, PartPose.offset(-2.5F, 16F, 7F));
		par.addOrReplaceChild("left_hind_leg", cube, PartPose.offset(0.5F, 16F, 7F));
		par.addOrReplaceChild("right_front_leg", cube, PartPose.offset(-2.5F, 16F, -4F));
		par.addOrReplaceChild("left_front_leg", cube, PartPose.offset(0.5F, 16F, -4F));
		PartDefinition par2 = par.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offsetAndRotation(-1F, 12F, 8F, ((float) Math.PI / 5F), 0F, 0F));
		par2.addOrReplaceChild("real_tail", CubeListBuilder.create().texOffs(9, 18).addBox(0F, 0F, -1F, 2F, 8F, 2F), PartPose.ZERO);
		return LayerDefinition.create(mesh, 64, 32);
	}

	protected Iterable<ModelPart> headParts() {
		return ImmutableList.of(this.head);
	}

	protected Iterable<ModelPart> bodyParts() {
		return ImmutableList.of(this.body, this.rightHindLeg, this.leftHindLeg, this.rightFrontLeg, this.leftFrontLeg, this.tail, this.upperBody);
	}

	public void prepareMobModel(T entity, float par1, float par2, float par3) {

		this.tail.yRot = Mth.cos(par1 * 0.6662F) * 1.4F * par2;

		if (entity instanceof WitchWolf wolf && wolf.isInSittingPose()) {
			this.upperBody.setPos(-1F, 16F, -3F);
			this.upperBody.xRot = 1.2566371F;
			this.upperBody.yRot = 0F;
			this.body.setPos(0F, 18F, 0F);
			this.body.xRot = ((float) Math.PI / 4F);
			this.tail.setPos(-1F, 21F, 6F);
			this.rightHindLeg.setPos(-2.5F, 22.7F, 2F);
			this.rightHindLeg.xRot = ((float) Math.PI * 1.5F);
			this.leftHindLeg.setPos(0.5F, 22.7F, 2F);
			this.leftHindLeg.xRot = ((float) Math.PI * 1.5F);
			this.rightFrontLeg.xRot = 5.811947F;
			this.rightFrontLeg.setPos(-2.49F, 17F, -4F);
			this.leftFrontLeg.xRot = 5.811947F;
			this.leftFrontLeg.setPos(0.51F, 17F, -4F);
		}

		else {
			this.body.setPos(0F, 14F, 2F);
			this.body.xRot = ((float) Math.PI / 2F);
			this.upperBody.setPos(-1F, 14F, -3F);
			this.upperBody.xRot = this.body.xRot;
			this.tail.setPos(-1F, 12F, 8F);
			this.rightHindLeg.setPos(-2.5F, 16F, 7F);
			this.leftHindLeg.setPos(0.5F, 16F, 7F);
			this.rightFrontLeg.setPos(-2.5F, 16F, -4F);
			this.leftFrontLeg.setPos(0.5F, 16F, -4F);
			this.rightHindLeg.xRot = Mth.cos(par1 * 0.6662F) * 1.4F * par2;
			this.leftHindLeg.xRot = Mth.cos(par1 * 0.6662F + (float) Math.PI) * 1.4F * par2;
			this.rightFrontLeg.xRot = Mth.cos(par1 * 0.6662F + (float) Math.PI) * 1.4F * par2;
			this.leftFrontLeg.xRot = Mth.cos(par1 * 0.6662F) * 1.4F * par2;
		}

		if (entity instanceof WitchWolf wolf) {
			this.realHead.zRot = wolf.getHeadRollAngle(par3) + wolf.getBodyRollAngle(par3, 0F);
			this.upperBody.zRot = wolf.getBodyRollAngle(par3, -0.08F);
			this.body.zRot = wolf.getBodyRollAngle(par3, -0.16F);
			this.realTail.zRot = wolf.getBodyRollAngle(par3, -0.2F);
		}
	}

	public void setupAnim(T entity, float swing, float swingAmount, float ageTick, float headYaw, float headPitch) {
		this.head.xRot = headPitch * ((float) Math.PI / 180F);
		this.head.yRot = headYaw * ((float) Math.PI / 180F);
		this.tail.xRot = 0.75F + Mth.cos(ageTick * 0.01F) * 0.3F;
	}

	public static ModelLayerLocation getLayer (String name) {
		return new ModelLayerLocation(SweetMagicCore.getSRC(name), "main");
	}
}
