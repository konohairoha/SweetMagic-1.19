package sweetmagic.init.render.entity.model;

import com.mojang.math.Vector3f;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.monster.Monster;
import sweetmagic.api.ientity.IWitch;

public class WindWitchModel<T extends Monster & IWitch> extends SMBaseModel<T> {

	public static final ModelLayerLocation LAYER = getLayer("sm_wind_witch");
	protected final ModelPart legJacketLeft;
	protected final ModelPart legJacketRight;
	protected final ModelPart cape;
	protected final ModelPart leftshoulder;
	protected final ModelPart rightshoulder;

	public WindWitchModel(ModelPart root) {
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

	public void swingLegs(Vector3f vec3f) {
		super.swingLegs(vec3f);

		Vector3f base = vec3f.copy();
		base.mul(LEG_SWING_AMOUNT);
		Vector3f mirror = base.copy();
		mirror.mul(-1F);
		this.legJacketLeft.resetPose();
		this.legJacketRight.resetPose();
		this.legJacketLeft.offsetRotation(base);
		this.legJacketRight.offsetRotation(mirror);
	}

	public void swingBody(Vector3f vec) {
		this.cape.xScale = 1.75F;
		this.cape.x = -1.75F;
		this.cape.y = -1F;
		this.cape.zScale = 0.85F;
		this.cape.yScale = 1.5F;

		this.leftshoulder.xScale = 0.775F;
		this.leftshoulder.yScale = 0.855F;
		this.leftshoulder.y = 0.05F;
		this.leftshoulder.x = -5.375F;

		this.rightshoulder.xScale = 0.775F;
		this.rightshoulder.yScale = 0.855F;
		this.rightshoulder.y = 0.05F;
		this.rightshoulder.x = 6.925F;
	}

	public void setupAnim(T entity, float swing, float swingAmount, float ageTick, float headYaw, float headPitch) {
		super.setupAnim(entity, swing, swingAmount, ageTick, headYaw, headPitch);

		if (entity.isCharge()) {
			this.armLeft.zRot = 1F;
			this.armLeft.x = -5.25F;
			this.armLeft.y = 2.5F;
			this.armJacketLeft.zRot = 1F;
			this.armJacketLeft.x = -5.25F;
			this.armJacketLeft.y = 2.5F;

			this.armRight.zRot = -1F;
			this.armRight.x = 5.25F;
			this.armRight.y = 2.5F;
			this.armJacketRight.zRot = -1F;
			this.armJacketRight.x = 5.25F;
			this.armJacketRight.y = 2.5F;
		}
	}
}
