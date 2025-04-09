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
import net.minecraft.util.Mth;
import sweetmagic.init.entity.monster.boss.QueenFrost;

public class QuenModel<T extends QueenFrost> extends SMBaseModel<T> {

	// モデルの登録のために、他と被らない名前でResourceLocationを登録しておく
	public static final ModelLayerLocation LAYER = getLayer("quen");
	private final ModelPart cap;

	// まとめて描画するためにrootを取得
	// そのほか、アニメーションしたい部位に応じてModelPartを取得しておく
	public QuenModel(ModelPart root) {
		super(root);
		this.cap = root.getChild("cap");
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

		part.addOrReplaceChild("legLeft", CubeListBuilder.create().texOffs(0, 32).addBox(-1F, -2F, -2F, 4F, 12F, 4F, new CubeDeformation(-0.075F)), PartPose.offset(1F, 14F, 0F));
		part.addOrReplaceChild("legRight", CubeListBuilder.create().texOffs(0, 16).addBox(-1F, -2F, -2F, 4F, 12F, 4F, new CubeDeformation(-0.075F)), PartPose.offset(-3F, 14F, 0F));
		part.addOrReplaceChild("skirt", CubeListBuilder.create().texOffs(16, 32).addBox(-3F, 0F, -2F, 8F, 4F, 4F, new CubeDeformation(0.35F)), PartPose.offset(-1F, 13F, -0.15F));
		part.addOrReplaceChild("cap", CubeListBuilder.create().texOffs(0, 48).addBox(-4F, -13.5F, -4F, 8F, 8F, 8F, new CubeDeformation(-0.3F)), PartPose.offset(0F, 0F, 0F));
		return LayerDefinition.create(mesh, 64, 64);
	}

	// モデルの動きをここで設定する
	@Override
	public void setupAnim(T entity, float swing, float swingAmount, float ageTick, float headYaw, float headPitch) {
		this.animHead(entity, headYaw, headPitch);
		Vector3f vec = new Vector3f(Mth.cos(swing * 0.662F * LIMB_SWING_WEIGHT / entity.getScale()) * swingAmount, 0F, 0F);
		this.swingArms(vec);
		this.swingLegs(vec);
	}

	public void animHead(T entity, float headYaw, float headPitch) {
		if (entity.isMagic()) { return; }

		Vector3f vec = new Vector3f(headPitch * TORADIAN, headYaw * TORADIAN, 0F);
		super.animHead(headYaw, headPitch);
		this.cap.resetPose();
		this.cap.offsetRotation(vec);
		this.cap.setRotation(headPitch * TORADIAN, headYaw * TORADIAN, 0F);
	}
}
