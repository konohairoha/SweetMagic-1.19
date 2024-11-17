package sweetmagic.init.render.entity.model;

import com.mojang.math.Vector3f;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.monster.Monster;

public class SMHolyModel<T extends Monster> extends SMBaseModel<T> {

	// モデルの登録のために、他と被らない名前でResourceLocationを登録しておく
	public static final ModelLayerLocation LAYER = getLayer("sm_queen");

	private final ModelPart legJacketLeft;
	private final ModelPart legJacketRight;

	// まとめて描画するためにrootを取得
	// そのほか、アニメーションしたい部位に応じてModelPartを取得しておく
	public SMHolyModel(ModelPart root) {
		super(root);
		this.legJacketLeft = root.getChild("legJacketLeft");
		this.legJacketRight = root.getChild("legJacketRight");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition part = mesh.getRoot();

		part.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4F, -7F, -4F, 8F, 8F, 8F, new CubeDeformation(-0.5F)), PartPose.offset(0F, 0F, 0F));

		part.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 16).addBox(-3F, 0F, -2F, 8F, 12F, 4F, new CubeDeformation(0F)), PartPose.offset(-1F, 0F, 0F));
		part.addOrReplaceChild("jacket", CubeListBuilder.create().texOffs(32, 0).addBox(-3F, 0F, -2F, 8F, 12F, 4F, new CubeDeformation(0.35F)), PartPose.offset(-1F, 0.3F, -0.15F));

		part.addOrReplaceChild("armLeft", CubeListBuilder.create().texOffs(40, 32).addBox(-2F, -2F, -2F, 4F, 12F, 4F, new CubeDeformation(-0.2F)), PartPose.offset(-5.75F, 1.8F, 0F));
		part.addOrReplaceChild("armRight", CubeListBuilder.create().texOffs(40, 16).addBox(-2F, -2F, -2F, 4F, 12F, 4F, new CubeDeformation(-0.2F)), PartPose.offset(5.75F, 1.8F, 0F));

		part.addOrReplaceChild("armJacketLeft", CubeListBuilder.create().texOffs(40, 48).addBox(-2F, -2F, -2F, 4F, 12F, 4F, new CubeDeformation(0F)), PartPose.offset(-5.75F, 1.95F, 0F));
		part.addOrReplaceChild("armJacketRight", CubeListBuilder.create().texOffs(40, 48).addBox(-2F, -2F, -2F, 4F, 12F, 4F, new CubeDeformation(0F)), PartPose.offset(5.75F, 1.95F, 0F));

		part.addOrReplaceChild("legLeft", CubeListBuilder.create().texOffs(0, 32).addBox(-1F, -2F, -2F, 4F, 12F, 4F, new CubeDeformation(-0.075F)), PartPose.offset(1F, 13.95F, 0F));
		part.addOrReplaceChild("legRight", CubeListBuilder.create().texOffs(0, 16).addBox(-1F, -2F, -2F, 4F, 12F, 4F, new CubeDeformation(-0.075F)), PartPose.offset(-3F, 13.95F, 0F));

		part.addOrReplaceChild("legJacketLeft", CubeListBuilder.create().texOffs(24, 48).addBox(-1F, -2F, -2F, 4F, 12F, 4F, new CubeDeformation(0.1F)), PartPose.offset(1F, 14F, 0F));
		part.addOrReplaceChild("legJacketRight", CubeListBuilder.create().texOffs(24, 48).addBox(-1F, -2F, -2F, 4F, 12F, 4F, new CubeDeformation(0.1F)), PartPose.offset(-3F, 14F, 0F));

		part.addOrReplaceChild("skirt", CubeListBuilder.create().texOffs(16, 32).addBox(-3F, 0F, -2F, 8F, 6F, 4F, new CubeDeformation(0.35F)), PartPose.offset(-1F, 13F, -0.15F));

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
