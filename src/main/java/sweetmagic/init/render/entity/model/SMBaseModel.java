package sweetmagic.init.render.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import sweetmagic.SweetMagicCore;

public abstract class SMBaseModel<T extends Mob> extends EntityModel<T> {

	// pi/180の近似値
	protected final static float TORADIAN = 0.017453292F;

	// 手足の振りの角度
	protected static final float ARM_SWING_AMOUNT = 60F * TORADIAN;
	protected static final float LEG_SWING_AMOUNT = 65F * TORADIAN;

	// 手足の振りの速さ
	protected static final float LIMB_SWING_WEIGHT = 0.85F;

	protected final ModelPart root;
	protected final ModelPart head;
	protected final ModelPart armLeft;
	protected final ModelPart armRight;
	protected final ModelPart armJacketLeft;
	protected final ModelPart armJacketRight;
	protected final ModelPart legLeft;
	protected final ModelPart legRight;

	// まとめて描画するためにrootを取得
	// そのほか、アニメーションしたい部位に応じてModelPartを取得しておく
	public SMBaseModel(ModelPart root) {
		this.root = root;
		this.head = root.getChild("head");
		this.armLeft = root.getChild("armLeft");
		this.armRight = root.getChild("armRight");
		this.armJacketLeft = root.getChild("armJacketLeft");
		this.armJacketRight = root.getChild("armJacketRight");
		this.legLeft = root.getChild("legLeft");
		this.legRight = root.getChild("legRight");
	}

	// モデルの動きをここで設定する
	@Override
	public void setupAnim(T entity, float swing, float swingAmount, float ageTick, float headYaw, float headPitch) {
		this.animHead(headYaw, headPitch);
		Vector3f vec = new Vector3f(Mth.cos(swing * 0.662F * LIMB_SWING_WEIGHT / entity.getScale()) * swingAmount, 0F, 0F);
		this.swingArms(vec);
		this.swingLegs(vec);
		this.swingBody(vec);
	}

	public void animHead(float headYaw, float headPitch) {
		Vector3f vec = new Vector3f(headPitch * TORADIAN, headYaw * TORADIAN, 0F);
		this.head.resetPose();
		this.head.offsetRotation(vec);
		this.head.setRotation(headPitch * TORADIAN, headYaw * TORADIAN, 0F);
	}

	public void swingArms(Vector3f vec) {
		Vector3f base = vec.copy();
		base.mul(ARM_SWING_AMOUNT);
		Vector3f mirror = base.copy();
		mirror.mul(-1F);
		this.armLeft.resetPose();
		this.armRight.resetPose();
		this.armLeft.offsetRotation(base);
		this.armRight.offsetRotation(mirror);
		this.armJacketLeft.resetPose();
		this.armJacketRight.resetPose();
		this.armJacketLeft.offsetRotation(base);
		this.armJacketRight.offsetRotation(mirror);
	}

	public void swingLegs(Vector3f vec3f) {
		Vector3f base = vec3f.copy();
		base.mul(LEG_SWING_AMOUNT);
		Vector3f mirror = base.copy();
		mirror.mul(-1F);
		this.legRight.resetPose();
		this.legLeft.resetPose();
		this.legRight.offsetRotation(base);
		this.legLeft.offsetRotation(mirror);
	}

	public void swingBody(Vector3f vec) { }

	@Override
	public void renderToBuffer(PoseStack pose, VertexConsumer ver, int light, int overlay, float red, float green, float blue, float alpha) {
		this.root.render(pose, ver, light, overlay, red, green, blue, alpha);
	}

	public ModelPart getArm(boolean isLeft) {
		return isLeft ? this.armRight : this.armLeft ;
	}

	public void translateAndRotate(ModelPart arm, PoseStack pose) {
		pose.translate((double) (arm.x / 16F), (double) (arm.y / 16F), (double) (arm.z / 16F));
		if (arm.zRot != 0F) {
			pose.mulPose(Vector3f.ZP.rotation(arm.zRot));
		}

		if (arm.yRot != 0F) {
			pose.mulPose(Vector3f.YP.rotation(arm.yRot));
		}

		if (arm.xRot != 0F) {
			pose.mulPose(Vector3f.XP.rotation(arm.xRot));
		}

		if (arm.xScale != 1F || arm.yScale != 1F || arm.zScale != 1F) {
			pose.scale(arm.xScale, arm.yScale, arm.zScale);
		}
	}

	public ModelPart getHead() {
		return this.head;
	}

	public static ModelLayerLocation getLayer(String name) {
		return new ModelLayerLocation(SweetMagicCore.getSRC(name), "main");
	}
}
