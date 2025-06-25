package sweetmagic.init.render.entity.model;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.model.AgeableListModel;
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
import sweetmagic.init.entity.animal.WitchFox;

public class WitchFoxModel<T extends WitchFox> extends AgeableListModel<T> {

	public static final ModelLayerLocation LAYER = getLayer("witchfox");
	public final ModelPart head;
	private final ModelPart body;
	private final ModelPart rightHindLeg;
	private final ModelPart leftHindLeg;
	private final ModelPart rightFrontLeg;
	private final ModelPart leftFrontLeg;
	private final ModelPart tail;

	public WitchFoxModel(ModelPart part) {
		super(true, 8F, 3.35F);
		this.head = part.getChild("head");
		this.body = part.getChild("body");
		this.rightHindLeg = part.getChild("right_hind_leg");
		this.leftHindLeg = part.getChild("left_hind_leg");
		this.rightFrontLeg = part.getChild("right_front_leg");
		this.leftFrontLeg = part.getChild("left_front_leg");
		this.tail = this.body.getChild("tail");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition par = mesh.getRoot();
		PartDefinition par1 = par.addOrReplaceChild("head", SMBaseModel.getCubeList(1, 5).addBox(-3F, -2F, -5F, 8F, 6F, 6F), PartPose.offset(-1F, 16.5F, -3F));
		par1.addOrReplaceChild("right_ear", SMBaseModel.getCubeList(8, 1).addBox(-3F, -4F, -4F, 2F, 2F, 1F), PartPose.ZERO);
		par1.addOrReplaceChild("left_ear", SMBaseModel.getCubeList(15, 1).addBox(3F, -4F, -4F, 2F, 2F, 1F), PartPose.ZERO);
		par1.addOrReplaceChild("nose", SMBaseModel.getCubeList(6, 18).addBox(-1F, 2.01F, -8F, 4F, 2F, 3F), PartPose.ZERO);
		PartDefinition par2 = par.addOrReplaceChild("body", SMBaseModel.getCubeList(24, 15).addBox(-3F, 3.999F, -3.5F, 6F, 11F, 6F), PartPose.offsetAndRotation(0F, 16F, -6F, ((float) Math.PI / 2F), 0F, 0F));
		CubeDeformation cube = new CubeDeformation(0.001F);
		CubeListBuilder build = SMBaseModel.getCubeList(4, 24).addBox(2F, 0.5F, -1F, 2F, 6F, 2F, cube);
		CubeListBuilder build1 = SMBaseModel.getCubeList(13, 24).addBox(2F, 0.5F, -1F, 2F, 6F, 2F, cube);
		par.addOrReplaceChild("right_hind_leg", build1, PartPose.offset(-5F, 17.5F, 7F));
		par.addOrReplaceChild("left_hind_leg", build, PartPose.offset(-1F, 17.5F, 7F));
		par.addOrReplaceChild("right_front_leg", build1, PartPose.offset(-5F, 17.5F, 0F));
		par.addOrReplaceChild("left_front_leg", build, PartPose.offset(-1F, 17.5F, 0F));
		par2.addOrReplaceChild("tail", SMBaseModel.getCubeList(30, 0).addBox(2F, 0F, -1F, 4F, 9F, 5F), PartPose.offsetAndRotation(-4F, 15F, -1F, -0.05235988F, 0F, 0F));
		return LayerDefinition.create(mesh, 48, 32);
	}

	public void prepareMobModel(T entity, float par1, float par2, float par3) {
		this.body.xRot = ((float) Math.PI / 2F);
		this.tail.xRot = -0.05235988F;
		this.rightHindLeg.xRot = Mth.cos(par1 * 0.6662F) * 1.4F * par2;
		this.leftHindLeg.xRot = Mth.cos(par1 * 0.6662F + (float) Math.PI) * 1.4F * par2;
		this.rightFrontLeg.xRot = Mth.cos(par1 * 0.6662F + (float) Math.PI) * 1.4F * par2;
		this.leftFrontLeg.xRot = Mth.cos(par1 * 0.6662F) * 1.4F * par2;
		this.head.setPos(-1F, 16.5F, -3F);
		this.head.yRot = 0F;
		this.head.zRot = entity.getHeadRollAngle(par3);
		this.rightHindLeg.visible = true;
		this.leftHindLeg.visible = true;
		this.rightFrontLeg.visible = true;
		this.leftFrontLeg.visible = true;
		this.body.setPos(0F, 16F, -6F);
		this.body.zRot = 0F;
		this.rightHindLeg.setPos(-5F, 17.5F, 7F);
		this.leftHindLeg.setPos(-1F, 17.5F, 7F);
		this.tail.zRot = -0.01F;
		this.tail.yRot = -0.01F;
		this.tail.setPos(-4F, 15F, -2F);

		if (entity.isCrouching()) {
			this.body.xRot = 1.6755161F;
			float f = entity.getCrouchAmount(par3);
			this.body.setPos(0F, 16F + entity.getCrouchAmount(par3), -6F);
			this.head.setPos(-1F, 16.5F + f, -3F);
			this.head.yRot = 0F;
		}

		else if (entity.getShit()) {
			this.body.xRot = ((float) Math.PI / 6F);
			this.body.setPos(0F, 9F, -3F);
			this.tail.zRot = 0F;
			this.tail.xRot = 1.15F + Mth.sin(entity.tickCount * 0.0875F) * 0.15F;
			this.tail.setPos(-4F, 17F, 0F);
			this.head.setPos(-1F, 10F, -0.25F);
			this.head.xRot = 0F;
			this.head.yRot = 0F;

			if (this.young) {
				this.head.setPos(-1F, 13F, -3.75F);
			}

			this.rightHindLeg.xRot = -1.3089969F;
			this.rightHindLeg.setPos(-5F, 21.5F, 6.75F);
			this.leftHindLeg.xRot = -1.3089969F;
			this.leftHindLeg.setPos(-1F, 21.5F, 6.75F);
			this.rightFrontLeg.xRot = -0.2617994F;
			this.leftFrontLeg.xRot = -0.2617994F;
		}
	}

	protected Iterable<ModelPart> headParts() {
		return ImmutableList.of(this.head);
	}

	protected Iterable<ModelPart> bodyParts() {
		return ImmutableList.of(this.body, this.rightHindLeg, this.leftHindLeg, this.rightFrontLeg, this.leftFrontLeg);
	}

	public void setupAnim(T entity, float swing, float swingAmount, float ageTick, float headYaw, float headPitch) {

		if (!entity.isCrouching()) {
			this.head.xRot = 0F;
			this.head.yRot = 0F;
		}

		if (entity.isCrouching()) {
			float f = Mth.cos(ageTick) * 0.01F;
			this.body.yRot = f;
			this.rightHindLeg.zRot = f;
			this.leftHindLeg.zRot = f;
			this.rightFrontLeg.zRot = f / 2F;
			this.leftFrontLeg.zRot = f / 2F;
		}
	}

	public static ModelLayerLocation getLayer(String name) {
		return new ModelLayerLocation(SweetMagicCore.getSRC(name), "main");
	}
}
