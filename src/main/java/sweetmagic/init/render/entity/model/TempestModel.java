package sweetmagic.init.render.entity.model;

import java.util.Arrays;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.monster.Monster;

public class TempestModel<T extends Monster> extends EntityModel<T> {

	// モデルの登録のために、他と被らない名前でResourceLocationを登録しておく
	public static final ModelLayerLocation LAYER = SMBaseModel.getLayer("blaze_tempest");
	private final ModelPart root;
	private final ModelPart[] upperBodyParts;
	private final ModelPart head;

	public TempestModel(ModelPart root) {
		this.root = root;
		this.head = root.getChild("head");
		this.upperBodyParts = new ModelPart[12];
		Arrays.setAll(this.upperBodyParts, (s) -> { return root.getChild(getPartName(s)); });
	}

	private static String getPartName(int par1) {
		return "part" + par1;
	}

	public static LayerDefinition createBodyLayer() {

		MeshDefinition mesh = new MeshDefinition();
		PartDefinition part = mesh.getRoot();
		float f = 0F;

		part.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4F, -4F, -4F, 8F, 8F, 8F), PartPose.ZERO);
		CubeListBuilder cube = CubeListBuilder.create().texOffs(0, 16).addBox(0F, 0F, 0F, 2F, 8F, 2F);

		for (int i = 0; i < 6; i++) {
			float f1 = Mth.cos(f) * 18F;
			float f2 = -2F + Mth.cos((float) (i * 2) * 0.25F);
			float f3 = Mth.sin(f) * 18F;
			part.addOrReplaceChild(getPartName(i), cube, PartPose.offset(f1, f2, f3));
			++f;
		}

		f = ((float) Math.PI / 4F);

		for (int i = 6; i < 12; i++) {
			float f4 = Mth.cos(f) * 7F;
			float f6 = 2F + Mth.cos((float) (i * 2) * 0.25F);
			float f8 = Mth.sin(f) * 7F;
			part.addOrReplaceChild(getPartName(i), cube, PartPose.offset(f4, f6, f8));
			++f;
		}

		return LayerDefinition.create(mesh, 64, 32);
	}

	public ModelPart root() {
		return this.root;
	}

	public void setupAnim(T entity, float swing, float swingAmount, float ageTick, float headYaw, float headPitch) {

		float f = ageTick * (float) Math.PI * -0.1F;

		for (int i = 0; i < 6; i++) {
			this.upperBodyParts[i].y = 0F + Mth.cos(((float) (i * 2) + ageTick) * 0.25F);
			this.upperBodyParts[i].x = Mth.cos(f) * 11F;
			this.upperBodyParts[i].z = Mth.sin(f) * 11F;
			++f;
		}

		f = ((float) Math.PI / 4F) + ageTick * (float) Math.PI * 0.1F;

		for (int i = 6; i < 12; i++) {
			this.upperBodyParts[i].y = 13F + Mth.cos(((float) (i * 2) + ageTick) * 0.25F);
			this.upperBodyParts[i].x = Mth.cos(f) * 7F;
			this.upperBodyParts[i].z = Mth.sin(f) * 7F;
			++f;
		}

		this.head.yRot = headYaw * ((float) Math.PI / 180F);
		this.head.xRot = headPitch * ((float) Math.PI / 180F);
	}

	@Override
	public void renderToBuffer(PoseStack pose, VertexConsumer ver, int light, int overlay, float red, float green, float blue, float alpha) {
		this.root.render(pose, ver, light, overlay, red, green, blue, alpha);
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
}
