package sweetmagic.init.render.entity.model;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import sweetmagic.init.entity.monster.boss.TwilightHora;

public class HoraModel<T extends TwilightHora> extends SMBaseModel<T> {

	public static final ModelLayerLocation LAYER = getLayer("hora");
	protected final ModelPart legJacketLeft;
	protected final ModelPart legJacketRight;
	protected final ModelPart cape;
	protected final ModelPart leftshoulder;
	protected final ModelPart rightshoulder;

	public HoraModel(ModelPart root) {
		super(root);
		this.legJacketLeft = root.getChild("legJacketLeft");
		this.legJacketRight = root.getChild("legJacketRight");
		this.cape = root.getChild("cape");
		this.rightshoulder = root.getChild("rightshoulder");
		this.leftshoulder = root.getChild("leftshoulder");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition part = mesh.getRoot();

		part.addOrReplaceChild("head", getCubeList(0, 0).addBox(-4F, -7F, -4F, 8F, 8F, 8F, getCube(-0.5F)), getPose(0F, 0F, 0F));

		part.addOrReplaceChild("body", getCubeList(16, 16).addBox(-3F, 0F, -2F, 8F, 12F, 4F, getCube(0F)), getPose(-1F, 0F, 0F));
		part.addOrReplaceChild("jacket", getCubeList(32, 0).addBox(-3F, 0F, -2F, 8F, 12F, 4F, getCube(0.35F)), getPose(-1F, 0.1F, 0F));
		part.addOrReplaceChild("leftshoulder", getCubeList(0, 57).addBox(-3F, 0F, -2F, 4F, 3F, 4F, getCube(0.35F)), getPose(-4.5F, 0.1F, 0F));
		part.addOrReplaceChild("rightshoulder", getCubeList(0, 57).addBox(-3F, 0F, -2F, 4F, 3F, 4F, getCube(0.35F)), getPose(5.5F, 0.1F, 0F));

		part.addOrReplaceChild("cape", getCubeList(16, 48).addBox(-3F, 0F, -2F, 8F, 12F, 4F, getCube(0.5F)), getPose(0F, 0.1F, 0F));

		part.addOrReplaceChild("armLeft", getCubeList(40, 32).addBox(-2F, -2F, -2F, 4F, 12F, 4F, getCube(-0.2F)), getPose(-5.75F, 1.8F, 0F));
		part.addOrReplaceChild("armRight", getCubeList(40, 16).addBox(-2F, -2F, -2F, 4F, 12F, 4F, getCube(-0.2F)), getPose(5.75F, 1.8F, 0F));
		part.addOrReplaceChild("armJacketLeft", getCubeList(40, 48).addBox(-2F, -2F, -2F, 4F, 12F, 4F, getCube(0F)), getPose(-5.75F, 1.95F, 0F));
		part.addOrReplaceChild("armJacketRight", getCubeList(40, 48).addBox(-2F, -2F, -2F, 4F, 12F, 4F, getCube(0F)), getPose(5.75F, 1.95F, 0F));

		part.addOrReplaceChild("legLeft", getCubeList(0, 16).addBox(-1F, -2F, -2F, 4F, 12F, 4F, getCube(-0.075F)), getPose(1F, 14F, 0F));
		part.addOrReplaceChild("legRight", getCubeList(0, 16).addBox(-1F, -2F, -2F, 4F, 12F, 4F, getCube(-0.075F)), getPose(-3F, 14F, 0F));
		part.addOrReplaceChild("legJacketLeft", getCubeList(0, 32).addBox(-1F, -2F, -2F, 4F, 12F, 4F, getCube(0F)), getPose(1F, 14F, 0F));
		part.addOrReplaceChild("legJacketRight", getCubeList(0, 32).addBox(-1F, -2F, -2F, 4F, 12F, 4F, getCube(0F)), getPose(-3F, 14F, 0F));

		part.addOrReplaceChild("skirt", getCubeList(16, 32).addBox(-3F, 0F, -2F, 8F, 4F, 4F, getCube(0F)), getPose(-1F, 12F, 0F));
		part.addOrReplaceChild("porch", getCubeList(0, 48).addBox(-3F, 0F, -2F, 6F, 4F, 1F, getCube(0.35F)), getPose(0F, 7.5F, 4F));
		return LayerDefinition.create(mesh, 64, 64);
	}

	public ModelPart root() {
		return this.root;
	}

	public void setupAnim(T entity, float swing, float swingAmount, float ageTick, float headYaw, float headPitch) {

		this.head.yRot = headYaw * ((float) Math.PI / 180F);
		this.head.xRot = headPitch * ((float) Math.PI / 180F);

		if (this.riding) {
			this.armRight.xRot = (-(float) Math.PI / 5F);
			this.armRight.yRot = 0F;
			this.armRight.zRot = 0F;
			this.armLeft.xRot = (-(float) Math.PI / 5F);
			this.armLeft.yRot = 0F;
			this.armLeft.zRot = 0F;
			this.legRight.xRot = -1.4137167F;
			this.legRight.yRot = ((float) Math.PI / 10F);
			this.legRight.zRot = 0.07853982F;
			this.legLeft.xRot = -1.4137167F;
			this.legLeft.yRot = (-(float) Math.PI / 10F);
			this.legLeft.zRot = -0.07853982F;
		}

		else {
			this.armRight.xRot = Mth.cos(swing * 0.6662F + (float) Math.PI) * 2F * swingAmount * 0.5F;
			this.armRight.yRot = 0F;
			this.armRight.zRot = 0F;
			this.armLeft.xRot = Mth.cos(swing * 0.6662F) * 2F * swingAmount * 0.5F;
			this.armLeft.yRot = 0F;
			this.armLeft.zRot = 0F;
			this.legRight.xRot = Mth.cos(swing * 0.6662F) * 1.4F * swingAmount * 0.5F;
			this.legRight.yRot = 0F;
			this.legRight.zRot = 0F;
			this.legLeft.xRot = Mth.cos(swing * 0.6662F + (float) Math.PI) * 1.4F * swingAmount * 0.5F;
			this.legLeft.yRot = 0F;
			this.legLeft.zRot = 0F;
		}

		if (entity.getSummon()) {
			this.armRight.z = 0F;
			this.armRight.x = -5F;
			this.armLeft.z = 0F;
			this.armLeft.x = 5F;
			this.armRight.xRot = Mth.cos(ageTick * 0.6662F) * 0.25F;
			this.armLeft.xRot = Mth.cos(ageTick * 0.6662F) * 0.25F;
			this.armRight.zRot = 2.3561945F;
			this.armLeft.zRot = -2.3561945F;
			this.armRight.yRot = 0F;
			this.armLeft.yRot = 0F;
		}
	}
}
