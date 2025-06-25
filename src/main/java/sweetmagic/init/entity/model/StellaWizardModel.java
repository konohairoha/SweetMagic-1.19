package sweetmagic.init.render.entity.model;

import com.mojang.math.Vector3f;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.LivingEntity;

public class StellaWizardModel<T extends LivingEntity> extends SMBaseModel<T> {

	public static final ModelLayerLocation LAYER = getLayer("stellawizard_master");
	private final ModelPart legJacketLeft;
	private final ModelPart legJacketRight;
	public int tickTime = 0;

	public StellaWizardModel(ModelPart root) {
		super(root);
		this.legJacketLeft = root.getChild("legJacketLeft");
		this.legJacketRight = root.getChild("legJacketRight");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition part = mesh.getRoot();

		part.addOrReplaceChild("head", getCubeList(0, 0).addBox(-4F, -7F, -4F, 8F, 8F, 8F, getCube(-0.5F)), getPose(0F, 0F, 0F));
		part.addOrReplaceChild("body", getCubeList(16, 16).addBox(-3F, 0F, -2F, 8F, 12F, 4F, getCube(0F)), getPose(-1F, 0F, 0F));
		part.addOrReplaceChild("jacket", getCubeList(32, 0).addBox(-3F, 0F, -2F, 8F, 12F, 4F, getCube(0.2F)), getPose(-1F, 0F, -0.15F));

		part.addOrReplaceChild("armLeft", getCubeList(40, 16).addBox(-2F, -2F, -2F, 4F, 12F, 4F, getCube(-0.2F)), getPose(-5.75F, 1.8F, 0F));
		part.addOrReplaceChild("armRight", getCubeList(40, 16).addBox(-2F, -2F, -2F, 4F, 12F, 4F, getCube(-0.2F)), getPose(5.75F, 1.8F, 0F));
		part.addOrReplaceChild("armJacketLeft", getCubeList(40, 32).addBox(-2F, -2F, -2F, 4F, 12F, 4F, getCube(0F)), getPose(-5.75F, 1.95F, 0F));
		part.addOrReplaceChild("armJacketRight", getCubeList(40, 32).addBox(-2F, -2F, -2F, 4F, 12F, 4F, getCube(0F)), getPose(5.75F, 1.95F, 0F));

		part.addOrReplaceChild("legLeft", getCubeList(0, 16).addBox(-1F, -2F, -2F, 4F, 12F, 4F, getCube(-0.075F)), getPose(1F, 13.95F, 0F));
		part.addOrReplaceChild("legRight", getCubeList(0, 16).addBox(-1F, -2F, -2F, 4F, 12F, 4F, getCube(-0.075F)), getPose(-3F, 13.95F, 0F));
		part.addOrReplaceChild("legJacketLeft", getCubeList(0, 32).addBox(-1F, -2F, -2F, 4F, 12F, 4F, getCube(0.1F)), getPose(1F, 14F, 0F));
		part.addOrReplaceChild("legJacketRight", getCubeList(0, 32).addBox(-1F, -2F, -2F, 4F, 12F, 4F, getCube(0.1F)), getPose(-3F, 14F, 0F));

		part.addOrReplaceChild("skirt", getCubeList(16, 32).addBox(-3F, 0F, -2F, 8F, 8F, 4F, getCube(0.15F)), getPose(-1F, 12F, -0.15F));
		part.addOrReplaceChild("skirt2", getCubeList(16, 42).addBox(-3F, 0F, -2F, 8F, 8F, 4F, getCube(0.2F)), getPose(-1F, 12F, -0.15F));
		return LayerDefinition.create(mesh, 64, 64);
	}

	public void swingLegs(Vector3f vec) {
		super.swingLegs(vec);
		Vector3f base = vec.copy();
		base.mul(LEG_SWING_AMOUNT);
		Vector3f mirror = base.copy();
		mirror.mul(-1F);

		this.legJacketRight.resetPose();
		this.legJacketLeft.resetPose();
		this.legJacketRight.offsetRotation(base);
		this.legJacketLeft.offsetRotation(mirror);
	}
}
