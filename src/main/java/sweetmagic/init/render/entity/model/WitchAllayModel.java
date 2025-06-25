package sweetmagic.init.render.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;

import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.HumanoidArm;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.AnimationInit;
import sweetmagic.init.entity.animal.WitchAllay;

public class WitchAllayModel<T extends WitchAllay> extends HierarchicalModel<T> implements ArmedModel {

	private static final Vector3f ANIMA_VEC = new Vector3f();
	private final float pi = (float) Math.PI / 180F;
	public static final ModelLayerLocation LAYER = getLayer("witch_allay");
	private final ModelPart root;
	public final ModelPart head;
	private final ModelPart body;
	private final ModelPart right_arm;
	private final ModelPart right_wing;
	private final ModelPart left_wing;

	public WitchAllayModel(ModelPart part) {
		this.root = part.getChild("root");
		this.head = this.root.getChild("head");
		this.body = this.root.getChild("body");
		this.right_arm = this.body.getChild("right_arm");
		this.right_wing = this.body.getChild("right_wing");
		this.left_wing = this.body.getChild("left_wing");
	}

	public ModelPart root() {
		return this.root;
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition mesh = new MeshDefinition();
		PartDefinition part = mesh.getRoot();
		PartDefinition root = part.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0F, 23.5F, 0F));
		root.addOrReplaceChild("head", SMBaseModel.getCubeList(0, 0).addBox(-2.5F, -5F, -2.5F, 5F, 5F, 5F, SMBaseModel.getCube(0F)), PartPose.offset(0F, -3.99F, 0F));
		PartDefinition body = root.addOrReplaceChild("body", SMBaseModel.getCubeList(0, 10).addBox(-1.5F, 0F, -1F, 3F, 4F, 2F, SMBaseModel.getCube(0F)).texOffs(0, 16).addBox(-1.5F, 0F, -1F, 3F, 5F, 2F, SMBaseModel.getCube(-0.2F)), PartPose.offset(0F, -4F, 0F));
		body.addOrReplaceChild("right_arm", SMBaseModel.getCubeList(23, 0).addBox(-0.75F, -0.5F, -1F, 1F, 4F, 2F, SMBaseModel.getCube(-0.01F)), PartPose.offset(-1.75F, 0.5F, 0F));
		body.addOrReplaceChild("left_arm", SMBaseModel.getCubeList(23, 6).addBox(-0.25F, -0.5F, -1F, 1F, 4F, 2F, SMBaseModel.getCube(-0.01F)), PartPose.offset(1.75F, 0.5F, 0F));
		body.addOrReplaceChild("right_wing", SMBaseModel.getCubeList(16, 14).addBox(0F, 1F, 0F, 0F, 5F, 8F, SMBaseModel.getCube(0F)), PartPose.offset(-0.5F, 0F, 0.65F));
		body.addOrReplaceChild("left_wing", SMBaseModel.getCubeList(16, 14).addBox(0F, 1F, 0F, 0F, 5F, 8F, SMBaseModel.getCube(0F)), PartPose.offset(0.5F, 0F, 0.65F));
		return LayerDefinition.create(mesh, 32, 32);
	}

	public void setupAnim(T entity, float swing, float swingAmount, float ageTick, float headYaw, float headPitch) {

		this.root().getAllParts().forEach(ModelPart::resetPose);
		float f = ageTick * 20F * this.pi + swingAmount;
		float f1 = Mth.cos(f) * (float) Math.PI * 0.15F;
		float f2 = ageTick - (float) entity.tickCount;
		float f3 = ageTick * 9F * this.pi;
		float f4 = Math.min(swingAmount / 0.3F, 1F);
		float f5 = 1F - f4;

		if (entity.getDancing()) {
			float f7 = ageTick * 8F * this.pi + swingAmount;
			float f8 = Mth.cos(f7) * 16F * this.pi;
			float f9 = entity.getSpinningProgress(f2);
			float f10 = Mth.cos(f7) * 14F * this.pi;
			float f11 = Mth.cos(f7) * 30F * this.pi;
			this.root.yRot = entity.isSpinning() ? 12.566371F * f9 : this.root.yRot;
			this.root.zRot = f8 * (1F - f9);
			this.head.yRot = f11 * (1F - f9);
			this.head.zRot = f10 * (1F - f9);
		}

		else {
			this.head.xRot = headPitch * this.pi;
			this.head.yRot = headYaw * this.pi;
		}

		this.right_wing.xRot = 0.43633232F;
		this.right_wing.yRot = -0.61086524F + f1;
		this.left_wing.xRot = 0.43633232F;
		this.left_wing.yRot = 0.61086524F - f1;
		float f12 = f4 * 0.6981317F;
		this.body.xRot = f12;
		this.root.y += (float) Math.cos((double) f3) * 0.25F * f5;

		this.animate(entity.magicAttackAnim, AnimationInit.ALLAY_ATTACK, ageTick);
		this.animate(entity.winkAnim, AnimationInit.ALLAY_WINK, ageTick);
	}

	public void renderToBuffer(PoseStack pose, VertexConsumer ver, int par1, int par2, float par3, float par4, float par5, float par6) {
		this.root.render(pose, ver, par1, par2);
	}

	public void translateToBody(PoseStack pose) {
		this.root.translateAndRotate(pose);
		this.body.translateAndRotate(pose);
		pose.translate(0D, -0.09375D, 0.09375D);
		pose.scale(0.7F, 0.7F, 0.7F);
		pose.translate(0.0625D, -1D, -0.15D);
	}

	public void translateToHand(HumanoidArm arm, PoseStack pose) {
		this.root.translateAndRotate(pose);
		this.body.translateAndRotate(pose);
		pose.translate(0D, -0.09375D, 0.09375D);
		pose.mulPose(Vector3f.XP.rotation(this.right_arm.xRot + 0.43633232F));
		pose.scale(0.7F, 0.7F, 0.7F);
		pose.translate(0.0625D, 0D, 0D);
	}

	public void animate(AnimationState state, AnimationDefinition def, float par1) {
		state.updateTime(par1, 1F);
		state.ifStarted(e -> KeyframeAnimations.animate(this, def, e.getAccumulatedTime(), 1F, ANIMA_VEC));
	}

	public static ModelLayerLocation getLayer(String name) {
		return new ModelLayerLocation(SweetMagicCore.getSRC(name), "main");
	}
}
