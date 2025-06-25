package sweetmagic.init.render.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import sweetmagic.init.entity.monster.boss.IgnisKnight;

public class IgnisModel<T extends IgnisKnight> extends SMBaseModel<T> {

	public static final ModelLayerLocation LAYER = getLayer("sm_ignis");
	private final ModelPart body;
	private final ModelPart jacket;
	private final ModelPart legJacketLeft;
	private final ModelPart legJacketRight;
	private final ModelPart earLeft;
	private final ModelPart earRight;
	private final ModelPart tail;
	private final ModelPart skirt;
	private boolean isSwing = false;
	public int tickTime = 0;

	public IgnisModel(ModelPart root) {
		super(root);
		this.body = root.getChild("body");
		this.jacket = root.getChild("jacket");
		this.legJacketLeft = root.getChild("legJacketLeft");
		this.legJacketRight = root.getChild("legJacketRight");
		this.earLeft = root.getChild("earLeft");
		this.earRight = root.getChild("earRight");
		this.tail = root.getChild("tail");
		this.skirt = root.getChild("skirt");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition part = mesh.getRoot();

		part.addOrReplaceChild("head", getCubeList(0, 0).addBox(-4F, -7F, -4F, 8F, 8F, 8F, getCube(-0.5F)), getPose(0F, 0F, 0F));
		part.addOrReplaceChild("body", getCubeList(16, 16).addBox(-3F, 0F, -2F, 8F, 12F, 4F, getCube(0F)), getPose(-1F, 0F, 0F));
		part.addOrReplaceChild("jacket", getCubeList(32, 0).addBox(-3F, 0F, -2F, 8F, 12F, 4F, getCube(0.35F)), getPose(-1F, 0.3F, -0.15F));

		part.addOrReplaceChild("armLeft", getCubeList(40, 32).addBox(-2F, -2F, -2F, 4F, 12F, 4F, getCube(-0.2F)), getPose(-5.75F, 1.8F, 0F));
		part.addOrReplaceChild("armRight", getCubeList(40, 16).addBox(-2F, -2F, -2F, 4F, 12F, 4F, getCube(-0.2F)), getPose(5.75F, 1.8F, 0F));
		part.addOrReplaceChild("armJacketLeft", getCubeList(40, 48).addBox(-2F, -2F, -2F, 4F, 12F, 4F, getCube(0F)), getPose(-5.75F, 1.95F, 0F));
		part.addOrReplaceChild("armJacketRight", getCubeList(40, 48).addBox(-2F, -2F, -2F, 4F, 12F, 4F, getCube(0F)), getPose(5.75F, 1.95F, 0F));

		part.addOrReplaceChild("legLeft", getCubeList(0, 16).addBox(-1F, -2F, -2F, 4F, 12F, 4F, getCube(-0.075F)), getPose(1F, 14F, 0F));
		part.addOrReplaceChild("legRight", getCubeList(0, 16).addBox(-1F, -2F, -2F, 4F, 12F, 4F, getCube(-0.075F)), getPose(-3F, 14F, 0F));
		part.addOrReplaceChild("legJacketLeft", getCubeList(0, 32).addBox(-1F, -2F, -2F, 4F, 12F, 4F, getCube(0.1F)), getPose(1F, 14F, 0F));
		part.addOrReplaceChild("legJacketRight", getCubeList(0, 32).addBox(-1F, -2F, -2F, 4F, 12F, 4F, getCube(0.1F)), getPose(-3F, 14F, 0F));

		part.addOrReplaceChild("skirt", getCubeList(16, 45).addBox(-3F, 0F, -2F, 8F, 4F, 4F, getCube(0F)), getPose(-1F, 10F, -0.15F));
		part.addOrReplaceChild("earRight", getCubeList(0, 57).addBox(-2.5F, -9F, -1F, 3F, 5F, 1F, getCube(-0.2F)), getPose(0F, 0.5F, -1F));
		part.addOrReplaceChild("earLeft", getCubeList(8, 57).addBox(0.5F, -9F, -1F, 3F, 5F, 1F, getCube(-0.2F)), getPose(0F, 0F, -1F));
		part.addOrReplaceChild("tail", getCubeList(16, 32).addBox(-0.5F, 0F, 0F, 1, 12, 1, getCube(-0.2F)), getPose(0F, 15F, 8F));
		return LayerDefinition.create(mesh, 64, 64);
	}

	// モデルの動きをここで設定する
	@Override
	public void setupAnim(T entity, float swing, float swingAmount, float ageTick, float headYaw, float headPitch) {
		this.animHead(entity, ageTick, headYaw, headPitch);
		Vector3f vec = new Vector3f(Mth.cos(swing * 0.662F * LIMB_SWING_WEIGHT / entity.getScale()) * swingAmount, 0F, 0F);
		this.swingArms(entity, vec);
		this.swingLegs(vec);
		this.skirt.xScale = 1.00F;
		this.skirt.y = 12F;
		this.skirt.z = 0.025F;
	}

	public void animHead(T entity, float ageTick, float headYaw, float headPitch) {

		Vector3f vec = new Vector3f(headPitch * TORADIAN, headYaw * TORADIAN, 0F);
		this.head.resetPose();
		this.head.offsetRotation(vec);
		this.head.setRotation(headPitch * TORADIAN, headYaw * TORADIAN, 0F);

		// 耳の設定
		this.earLeft.copyFrom(this.head);
		this.earLeft.x -= 0.5F;
		this.earLeft.y -= 1F;
		this.earRight.copyFrom(this.head);
		this.earRight.x -= 0.5F;
		this.earRight.y -= 1F;

		// 耳の挙動設定
		float headAngleX = this.head.xRot * 1F;
		float ageCos1 = Mth.cos(ageTick * 0.11F) * 0.045F;
		this.earRight.xRot = -ageCos1 + headAngleX;
		this.earLeft.xRot = ageCos1 + headAngleX;

		// 尻尾の設定
		this.tail.xRot = 0.9F;
		this.tail.x = 0F;
		this.tail.y = 10F;
		this.tail.z = 0F;
		this.tail.xScale = 1.25F;
		this.tail.yScale = 0.75F;
		this.tail.zScale = 1.25F;

		// 尻尾の挙動設定
		float ageCos2 = Mth.cos(ageTick * 0.06875F);
		this.tail.zRot = -ageCos2 * 0.35F;
	}

	public void swingArms(T entity, Vector3f vec3f) {
		Vector3f base = vec3f.copy();
		base.mul(ARM_SWING_AMOUNT);
		Vector3f mirror = base.copy();
		mirror.mul(-1F);
		this.armLeft.resetPose();
		this.armRight.resetPose();
		this.armLeft.offsetRotation(base);
		this.armRight.offsetRotation(mirror);
		this.body.yRot = 0F;

		int attackType = entity.getAttackType();

		switch(attackType) {
		case 0:
			this.hammerAttack(entity);
			break;
		case 1:
			this.hammerGroundwork(entity);
			break;
		case 2:
			this.hammerBlast(entity);
			break;
		}

		this.jacket.copyFrom(this.body);
		this.armJacketLeft.copyFrom(this.armLeft);
		this.armJacketRight.copyFrom(this.armRight);
	}

	public void hammerAttack(T entity) {

		// 振りかざしているかつ時間経過がない場合
		if (!this.isSwing) {
			this.isSwing = entity.getSwing() && this.tickTime <= 0;
		}

		this.tickTime = this.isSwing ? this.tickTime + 1 : this.tickTime - 1;	// 振りかざしているなら時間経過を加算、そうでないなら減算
		int maxtick = 30;														// 最大時間の設定
		this.tickTime = Math.max(Math.min(this.tickTime, maxtick), 0);			// 最大最小時間の設定
		float tickRate = this.tickTime / (float) maxtick;						// 最大時間までの割合を算出

		// 最大時間に達していたら元の位置へ戻し始める
		if (this.tickTime >= maxtick) {
			this.isSwing = false;
		}

		this.armLeft.x = -5.5F + (3.5F * tickRate);
		this.armLeft.y = 2.25F;
		this.armLeft.z = 2.25F - (5F * tickRate);
		this.armLeft.xRot = -1F + (0.5F * tickRate);
		this.armLeft.zRot = 0.5F - (2F * tickRate);
		this.armRight.x = 4F;
		this.armRight.z = -2F + (5.5F * tickRate);
		this.armRight.y = 2.5F;
		this.armRight.xRot = -0.5F - (1.35F * tickRate);
		this.armRight.yRot = 0F - (0.5F * tickRate);
		this.armRight.zRot = 0.85F + (0.5F * tickRate);
		this.body.yRot = -(0.5F * tickRate);
	}

	public void hammerGroundwork(T entity) {

		// 振りかざしているかつ時間経過がない場合
		if (!this.isSwing) {
			this.isSwing = entity.getSwing() && this.tickTime <= 0;
		}

		this.tickTime = this.isSwing ? this.tickTime + 1 : this.tickTime - 1;	// 振りかざしているなら時間経過を加算、そうでないなら減算
		int maxtick = 20;														// 最大時間の設定
		this.tickTime = Math.max(Math.min(this.tickTime, maxtick), 0);			// 最大最小時間の設定
		float tickRate = this.tickTime / (float) maxtick;						// 最大時間までの割合を算出

		// 最大時間に達していたら元の位置へ戻し始める
		if (this.tickTime >= maxtick) {
			this.isSwing = false;
		}

		this.armLeft.x = -5F + (1F * tickRate);
		this.armLeft.y = 1.5F + (2F * tickRate);
		this.armLeft.z = 2F - (3F * tickRate);
		this.armLeft.xRot = -2.25F + (1.75F * tickRate);
		this.armLeft.yRot = 0F - (0.5F * tickRate);
		this.armLeft.zRot = -0.25F;
		this.armRight.x = 4F;
		this.armRight.y = 2.5F - (0.5F * tickRate);
		this.armRight.z = -2F;
		this.armRight.xRot = -0.75F - (0.5F * tickRate);
		this.armRight.yRot = 0.5F - (2F * tickRate);
		this.armRight.zRot = 1.5F;
		this.body.yRot = 0.5F - (0.5F * tickRate);
	}

	public void hammerBlast(T entity) {

		// 振りかざしているかつ時間経過がない場合
		if (!this.isSwing) {
			this.isSwing = entity.getSwing() && this.tickTime <= 0;
		}

		this.tickTime = this.isSwing ? this.tickTime + 1 : this.tickTime - 1;	// 振りかざしているなら時間経過を加算、そうでないなら減算
		int maxtick = 15;														// 最大時間の設定
		this.tickTime = Math.max(Math.min(this.tickTime, maxtick), 0);			// 最大最小時間の設定
		float tickRate = this.tickTime / (float) maxtick;						// 最大時間までの割合を算出

		// 最大時間に達していたら元の位置へ戻し始める
		if (this.tickTime >= maxtick && !entity.getBlast()) {
			this.isSwing = false;
		}

		this.armLeft.x = -4.5F - (1F * tickRate);
		this.armLeft.y = -2.5F + (7F * tickRate);
		this.armLeft.z = -1.5F - (1F * tickRate);
		this.armLeft.xRot = -1.35F + (0.5F * tickRate);
		this.armLeft.yRot = -1.4F + (2.25F * tickRate);
		this.armLeft.zRot = -1.25F;
		this.armRight.x = 5.5F - (1.5F * tickRate);
		this.armRight.y = -1.25F + (4.75F * tickRate);
		this.armRight.z = -2F;
		this.armRight.xRot = -1F - (0.5F * tickRate);
		this.armRight.yRot = 1F - (2.5F * tickRate);
		this.armRight.zRot = 1.75F;
	}

	public void swingLegs(Vector3f vec) {
		super.swingLegs(vec);
		this.legJacketRight.copyFrom(this.legRight);
		this.legJacketRight.xScale = 0.925F;
		this.legJacketRight.x = -2.9375F;
		this.legJacketLeft.copyFrom(this.legLeft);
		this.legJacketLeft.xScale = 0.925F;
		this.legJacketLeft.x = 1.075F;
	}

	public void hammerBlastItem(PoseStack pose) {
		int maxtick = 15;									// 最大時間の設定
		float tickRate = this.tickTime / (float) maxtick;	// 最大時間までの割合を算出
		pose.mulPose(Vector3f.XP.rotationDegrees(135F));
		pose.mulPose(Vector3f.YP.rotationDegrees(0F - (37F * tickRate)));
		pose.mulPose(Vector3f.ZP.rotationDegrees(270F));
		pose.translate(-0.25D, 0.1D, 0D);
	}
}
