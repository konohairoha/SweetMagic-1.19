package sweetmagic.init.render.entity.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.LivingEntity;
import sweetmagic.SweetMagicCore;

public abstract class SMBaseModel<T extends LivingEntity> extends EntityModel<T> {

	// pi/180の近似値
	protected final static float TORADIAN = 0.017453292F;

	// 手足の振りの角度
	protected static final float ARM_SWING_AMOUNT = 60F * TORADIAN;
	protected static final float LEG_SWING_AMOUNT = 65F * TORADIAN;

	// 手足の振りの速さ
	protected static final float LIMB_SWING_WEIGHT = 0.85F;
	private static final Vector3f ANIMA_VEC = new Vector3f();

	protected final ModelPart root;
	public final ModelPart head;
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

	public ModelPart root() {
		return this.root;
	}

	public ModelPart getHead() {
		return this.head;
	}

	public void animate(AnimationState state, AnimationDefinition def, float par1) {
		state.updateTime(par1, 1F);
		state.ifStarted(e -> this.animate(this, def, e.getAccumulatedTime(), 1F, ANIMA_VEC));
	}

	public static CubeListBuilder getCubeList(int x, int y) {
		return CubeListBuilder.create().texOffs(x, y);
	}

	public static CubeDeformation getCube(float rate) {
		return new CubeDeformation(rate);
	}

	public static PartPose getPose(float x, float y, float z) {
		return PartPose.offset(x, y, z);
	}

	public static ModelLayerLocation getLayer(String name) {
		return new ModelLayerLocation(SweetMagicCore.getSRC(name), "main");
	}

	public void animate(SMBaseModel<?> model, AnimationDefinition anime, long par1, float par2, Vector3f vec) {
		float f = this.getElapsedSeconds(anime, par1);

		for (Map.Entry<String, List<AnimationChannel>> entry : anime.boneAnimations().entrySet()) {
			Optional<ModelPart> opti = model.getAnyDescendantWithName(entry.getKey());
			List<AnimationChannel> list = entry.getValue();
			opti.ifPresent((par3) -> {
				list.forEach((par4) -> {
					Keyframe[] frame = par4.keyframes();
					int i = Math.max(0, Mth.binarySearch(0, frame.length, (par5) -> {
						return f <= frame[par5].timestamp();
					}) - 1);
					int j = Math.min(frame.length - 1, i + 1);
					Keyframe keyframe = frame[i];
					Keyframe keyframe1 = frame[j];
					float f1 = f - keyframe.timestamp();
					float f2 = Mth.clamp(f1 / (keyframe1.timestamp() - keyframe.timestamp()), 0F, 1F);
					keyframe1.interpolation().apply(vec, f2, frame, i, j, par2);
					par4.target().apply(par3, vec);
				});
			});
		}

	}

	public Optional<ModelPart> getAnyDescendantWithName(String par1) {
		return this.root().getAllParts().filter((par2) -> {
			return par2.hasChild(par1);
		}).findFirst().map((par2) -> {
			return par2.getChild(par1);
		});
	}

	private float getElapsedSeconds(AnimationDefinition anima, long par1) {
		float f = (float) par1 / 1000F;
		return anima.looping() ? f % anima.lengthInSeconds() : f;
	}
}
