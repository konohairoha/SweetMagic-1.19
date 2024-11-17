package sweetmagic.init.render.entity.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import sweetmagic.SweetMagicCore;

public class AncientFairyModel<T extends LivingEntity> extends HumanoidModel<T> {

	public static final ModelLayerLocation LAYER = getLayer("ancientfairy");

	private final ModelPart leftWing;
	private final ModelPart rightWing;

	public AncientFairyModel(ModelPart root) {
		super(root);
		this.leftLeg.visible = false;
		this.hat.visible = false;
		this.rightWing = root.getChild("right_wing");
		this.leftWing = root.getChild("left_wing");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = HumanoidModel.createMesh(CubeDeformation.NONE, 0F);
		PartDefinition part = mesh.getRoot();
		part.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(32, 0).addBox(-1F, -1F, -2F, 6F, 10F, 4F), PartPose.offset(-1.9F, 12F, 0F));
		part.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(0, 32).addBox(-20F, 0F, 0F, 20F, 12F, 1F), PartPose.ZERO);
		part.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(0, 32).mirror().addBox(0F, 0F, 0F, 20F, 12F, 1F), PartPose.ZERO);
		return LayerDefinition.create(mesh, 64, 64);
	}

	protected Iterable<ModelPart> bodyParts() {
		return Iterables.concat(super.bodyParts(), ImmutableList.of(this.rightWing, this.leftWing));
	}

	public void setupAnim(T entity, float swing, float swingAmount, float ageTick, float headYaw, float headPitch) {

		super.setupAnim(entity, swing, swingAmount, ageTick, headYaw, headPitch);

		this.rightLeg.xRot += ((float) Math.PI / 5F);
		this.rightWing.z = 2F;
		this.leftWing.z = 2F;
		this.rightWing.y = 1F;
		this.leftWing.y = 1F;
		this.rightWing.yRot = 0.47123894F + Mth.cos(ageTick * 45.836624F * ((float) Math.PI / 360F)) * (float) Math.PI * 0.05F;
		this.leftWing.yRot = -this.rightWing.yRot;
		this.leftWing.zRot = -0.47123894F;
		this.leftWing.xRot = 0.47123894F;
		this.rightWing.xRot = 0.47123894F;
		this.rightWing.zRot = 0.47123894F;
	}

	public static ModelLayerLocation getLayer (String name) {
		return new ModelLayerLocation(SweetMagicCore.getSRC(name), "main");
	}
}