package sweetmagic.init.render.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.entity.monster.SkullFrostRoyalGuard;

public class RoyalGuardLayer <T extends AbstractSkeleton, M extends SkeletonModel<T>> extends AbstractEntityLayer<T, M> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/entity/skullflost_guard.png");
	private static final ItemStack SHIELD = new ItemStack(Items.SHIELD);

	public RoyalGuardLayer(RenderLayerParent<T, M> layer, EntityRendererProvider.Context con) {
		super(layer, con);
		this.setModel(new SkeletonModel<>(this.getModel(con, ModelLayers.SKELETON)));
	}

	public void render(PoseStack pose, MultiBufferSource buf, int light, T entity, float swing, float swingAmount, float parTick, float ageTick, float netHeadYaw, float headPitch) {
		pose.pushPose();
		this.renderArmWithItem(entity, pose, buf, light, swing, swingAmount, parTick, ageTick, netHeadYaw, headPitch);
		pose.popPose();
	}

	protected void renderArmWithItem(T entity, PoseStack pose, MultiBufferSource buf, int light, float swing, float swingAmount, float parTick, float ageTick, float netHeadYaw, float headPitch) {

		pose.pushPose();
		EntityModel<T> model = this.getModel();
		this.getParentModel().translateToHand(HumanoidArm.LEFT, pose);
		pose.mulPose(Vector3f.XP.rotationDegrees(-90F));
		pose.mulPose(Vector3f.YP.rotationDegrees(180F));
		pose.mulPose(Vector3f.ZP.rotationDegrees(180F));
		pose.translate(0.1D, 0.1D, -1.75D);
		this.render.renderItem(entity, SHIELD, ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND, false, pose, buf, light);
		pose.popPose();
		if (!((SkullFrostRoyalGuard)entity).getGuard()) { return; }

		pose.scale(1.05F, 1.05F, 1.05F);
		float f = (float) entity.tickCount + parTick;
		model.prepareMobModel(entity, swing, swingAmount, parTick);
		this.getParentModel().copyPropertiesTo(model);
		VertexConsumer ver = buf.getBuffer(RenderType.energySwirl(this.getTex(), this.xOffset(f) % 1F, f * 0.01F % 1F));
		model.setupAnim(entity, swing, swingAmount, ageTick, netHeadYaw, headPitch);
		model.renderToBuffer(pose, ver, light, OverlayTexture.NO_OVERLAY, 0.25F, 0.25F, 0.25F, 1F);
	}

	protected ResourceLocation getTex() {
		return TEX;
	}
}
