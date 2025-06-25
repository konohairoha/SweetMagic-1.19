package sweetmagic.init.render.entity.model;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AnimationState;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.AnimationInit;
import sweetmagic.init.entity.monster.boss.DemonsBelial;

public class DemonsBelialModel<T extends DemonsBelial> extends HierarchicalModel<T> {

	private static final Vector3f ANIMA_VEC = new Vector3f();
	public static final ModelLayerLocation LAYER = getLayer("demonsbelial");
	private final ModelPart root;
	protected final ModelPart bone;
	public final ModelPart body;
	public final ModelPart head;
	protected final ModelPart rightTendril;
	protected final ModelPart leftTendril;
	protected final ModelPart leftLeg;
	public final ModelPart leftArm;
	protected final ModelPart leftRibcage;
	public final ModelPart rightArm;
	protected final ModelPart rightLeg;
	protected final ModelPart rightRibcage;
	private final List<ModelPart> heartList;
	private final List<ModelPart> allList;

	public DemonsBelialModel(ModelPart part) {
		super(RenderType::entityCutoutNoCull);
		this.root = part;
		this.bone = part.getChild("bone");
		this.body = this.bone.getChild("body");
		this.head = this.body.getChild("head");
		this.rightLeg = this.bone.getChild("right_leg");
		this.leftLeg = this.bone.getChild("left_leg");
		this.rightArm = this.body.getChild("right_arm");
		this.leftArm = this.body.getChild("left_arm");
		this.rightTendril = this.head.getChild("right_tendril");
		this.leftTendril = this.head.getChild("left_tendril");
		this.rightRibcage = this.body.getChild("right_ribcage");
		this.leftRibcage = this.body.getChild("left_ribcage");
		this.heartList = ImmutableList.of(this.body);
		this.allList = ImmutableList.of(this.body, this.head, this.rightTendril, this.leftTendril, this.leftLeg, this.leftArm, this.leftRibcage, this.rightArm, this.rightLeg, this.rightRibcage);
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition part = mesh.getRoot();
		PartDefinition bone = part.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offset(0F, 24F, 0F));
		PartDefinition body = bone.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-9F, -13F, -4F, 18F, 21F, 11F), PartPose.offset(0F, -21F, 0F));
		body.addOrReplaceChild("right_ribcage", CubeListBuilder.create().texOffs(90, 11).addBox(-2F, -11F, -0.1F, 9F, 21F, 0F), PartPose.offset(-7F, -2F, -4F));
		body.addOrReplaceChild("left_ribcage", CubeListBuilder.create().texOffs(90, 11).mirror() .addBox(-7F, -11F, -0.1F, 9F, 21F, 0F).mirror(false), PartPose.offset(7F, -2F, -4F));
		PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 32).addBox(-8F, -16F, -5F, 16F, 16F, 10F), PartPose.offset(0F, -13F, 0F));
		head.addOrReplaceChild("right_tendril", CubeListBuilder.create().texOffs(52, 32).addBox(-16F, -13F, 0F, 16F, 16F, 0F), PartPose.offset(-4F, -16F, 0F));
		head.addOrReplaceChild("left_tendril", CubeListBuilder.create().texOffs(58, 0).addBox(0F, -13F, 0F, 16F, 16F, 0F), PartPose.offset(4F, -16F, 0F));
		body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(44, 50).addBox(-4F, 0F, -4F, 8F, 28F, 8F), PartPose.offset(-13F, -13F, 1F));
		body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(0, 58).addBox(-4F, 0F, -4F, 8F, 28F, 8F), PartPose.offset(13F, -13F, 1F));
		bone.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(76, 48).addBox(-3.1F, 0F, -3F, 6F, 13F, 6F), PartPose.offset(-5.9F, -13F, 0F));
		bone.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(76, 76).addBox(-2.9F, 0F, -3F, 6F, 13F, 6F), PartPose.offset(5.9F, -13F, 0F));
		return LayerDefinition.create(mesh, 128, 128);
	}

	public void setupAnim(T entity, float swing, float swingAmount, float ageTick, float headYaw, float headPitch) {
		this.root().getAllParts().forEach(ModelPart::resetPose);
		this.animateHeadLookTarget(headYaw, headPitch);
		this.animateIdlePose(ageTick);

		if(!entity.getNoMove()) {
			this.animateWalk(swing, swingAmount);
		}

		this.rightTendril.zRot = 0.375F;
		this.leftTendril.zRot = -0.375F;
		this.animate(entity.defalutAttackAnim, AnimationInit.BELIAL_ATTACK, ageTick);
		this.animate(entity.landingAnim, AnimationInit.BELIAL_LANDING, ageTick);
		this.animate(entity.laserAnim, AnimationInit.BELIAL_LASER, ageTick);
		this.animate(entity.swingAnim, AnimationInit.BELIAL_SWING, ageTick);
		this.animate(entity.meteorAnim, AnimationInit.BELIAL_METEOR, ageTick);
		this.animate(entity.downAnim, AnimationInit.BELIAL_DOWN, ageTick);

		if(!entity.isAlive()) {
//			this.allList.forEach(e -> e.visible = false);
		}
	}

	private void animateHeadLookTarget(float headYaw, float headPitch) {
		this.head.xRot = headPitch * ((float) Math.PI / 180F);
		this.head.yRot = headYaw * ((float) Math.PI / 180F);
	}

	private void animateIdlePose(float ageTick) {
		float f = ageTick * 0.1F;
		float f1 = Mth.cos(f);
		float f2 = Mth.sin(f);
		this.head.zRot += 0.06F * f1;
		this.head.xRot += 0.06F * f2;
		this.body.zRot += 0.025F * f2;
		this.body.xRot += 0.025F * f1;
	}

	private void animateWalk(float swing, float swingAmount) {
		float f = Math.min(0.5F, 3F * swingAmount);
		float f1 = swing * 0.8662F;
		float f2 = Mth.cos(f1);
		float f3 = Mth.sin(f1);
		float f4 = Math.min(0.35F, f);
		this.head.zRot += 0.3F * f3 * f;
		this.head.xRot += 1.2F * Mth.cos(f1 + ((float) Math.PI / 2F)) * f4;
		this.body.zRot = 0.1F * f3 * f;
		this.body.xRot = 1F * f2 * f4;
		this.leftLeg.xRot = 1F * f2 * f;
		this.rightLeg.xRot = 1F * Mth.cos(f1 + (float) Math.PI) * f;
		this.leftArm.xRot = -(0.8F * f2 * f);
		this.leftArm.zRot = 0F;
		this.rightArm.xRot = -(0.8F * f3 * f);
		this.rightArm.zRot = 0F;
		this.resetArmPoses();
	}

	private void resetArmPoses() {
		this.leftArm.yRot = 0F;
		this.leftArm.z = 1F;
		this.leftArm.x = 13F;
		this.leftArm.y = -13F;
		this.rightArm.yRot = 0F;
		this.rightArm.z = 1F;
		this.rightArm.x = -13F;
		this.rightArm.y = -13F;
	}

	public ModelPart root() {
		return this.root;
	}

	public List<ModelPart> getHeartPartList() {
		return this.heartList;
	}

	public List<ModelPart> getRootPartList() {
		return this.allList;
	}

	public static ModelLayerLocation getLayer(String name) {
		return new ModelLayerLocation(SweetMagicCore.getSRC(name), "main");
	}

	public void translateAndRotate(ModelPart arm, PoseStack pose) {

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

	public void animate(AnimationState state, AnimationDefinition def, float par1) {
		state.updateTime(par1, 1F);
		state.ifStarted(e -> KeyframeAnimations.animate(this, def, e.getAccumulatedTime(), 1F, ANIMA_VEC));
	}
}
