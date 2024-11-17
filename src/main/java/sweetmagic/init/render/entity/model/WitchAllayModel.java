package sweetmagic.init.render.entity.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;

import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.entity.animal.WitchAllay;

public class WitchAllayModel<T extends WitchAllay> extends HierarchicalModel<T> implements ArmedModel {

	public static final ModelLayerLocation LAYER = getLayer("witch_allay");

	private final ModelPart root;
	private final ModelPart head;
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

		PartDefinition part1 = part.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0F, 23.5F, 0F));
		part1.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -5F, -2.5F, 5F, 5F, 5F, new CubeDeformation(0F)), PartPose.offset(0F, -3.99F, 0F));
		PartDefinition part2 = part1.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 10).addBox(-1.5F, 0F, -1F, 3F, 4F, 2F, new CubeDeformation(0F)).texOffs(0, 16).addBox(-1.5F, 0F, -1F, 3F, 5F, 2F, new CubeDeformation(-0.2F)), PartPose.offset(0F, -4F, 0F));
		part2.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(23, 0).addBox(-0.75F, -0.5F, -1F, 1F, 4F, 2F, new CubeDeformation(-0.01F)), PartPose.offset(-1.75F, 0.5F, 0F));
		part2.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(23, 6).addBox(-0.25F, -0.5F, -1F, 1F, 4F, 2F, new CubeDeformation(-0.01F)), PartPose.offset(1.75F, 0.5F, 0F));
		part2.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(16, 14).addBox(0F, 1F, 0F, 0F, 5F, 8F, new CubeDeformation(0F)), PartPose.offset(-0.5F, 0F, 0.65F));
		part2.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(16, 14).addBox(0F, 1F, 0F, 0F, 5F, 8F, new CubeDeformation(0F)), PartPose.offset(0.5F, 0F, 0.65F));
		return LayerDefinition.create(mesh, 32, 32);
	}

	public void setupAnim(T entity, float swing, float swingAmount, float ageTick, float headYaw, float headPitch) {

		this.root().getAllParts().forEach(ModelPart::resetPose);
		float f = ageTick * 20F * ((float) Math.PI / 180F) + swingAmount;
		float f1 = Mth.cos(f) * (float) Math.PI * 0.15F;
		float f2 = ageTick - (float) entity.tickCount;
		float f3 = ageTick * 9F * ((float) Math.PI / 180F);
		float f4 = Math.min(swingAmount / 0.3F, 1F);
		float f5 = 1F - f4;

		if (entity.isDancing()) {
			float f7 = ageTick * 8F * ((float) Math.PI / 180F) + swingAmount;
			float f8 = Mth.cos(f7) * 16F * ((float) Math.PI / 180F);
			float f9 = entity.getSpinningProgress(f2);
			float f10 = Mth.cos(f7) * 14F * ((float) Math.PI / 180F);
			float f11 = Mth.cos(f7) * 30F * ((float) Math.PI / 180F);
			this.root.yRot = entity.isSpinning() ? 12.566371F * f9 : this.root.yRot;
			this.root.zRot = f8 * (1F - f9);
			this.head.yRot = f11 * (1F - f9);
			this.head.zRot = f10 * (1F - f9);
		}

		else {
			this.head.xRot = headPitch * ((float) Math.PI / 180F);
			this.head.yRot = headYaw * ((float) Math.PI / 180F);
		}

		this.right_wing.xRot = 0.43633232F;
		this.right_wing.yRot = -0.61086524F + f1;
		this.left_wing.xRot = 0.43633232F;
		this.left_wing.yRot = 0.61086524F - f1;
		float f12 = f4 * 0.6981317F;
		this.body.xRot = f12;
		this.root.y += (float) Math.cos((double) f3) * 0.25F * f5;
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

	public static ModelLayerLocation getLayer (String name) {
		return new ModelLayerLocation(SweetMagicCore.getSRC(name), "main");
	}
}
