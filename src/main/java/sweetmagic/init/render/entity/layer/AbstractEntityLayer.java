package sweetmagic.init.render.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import sweetmagic.init.entity.monster.boss.AbstractSMBoss;
import sweetmagic.util.RenderUtil;
import sweetmagic.util.RenderUtil.RenderColor;

public abstract class AbstractEntityLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {

	protected EntityModel<T> model;
	protected final ItemInHandRenderer render;
	protected final float pi = 180F / (float) Math.PI;

	public AbstractEntityLayer(RenderLayerParent<T, M> layer, EntityRendererProvider.Context con) {
		super(layer);
		this.render = con.getItemInHandRenderer();
	}

	protected void renderHead(T entity, ModelPart head, PoseStack pose, MultiBufferSource buf, int light, float scale, float x, float y, float z) {
		ItemStack stack = entity.getItemBySlot(EquipmentSlot.HEAD);
		if (stack.isEmpty() || !(stack.getItem() instanceof BlockItem bItem)) { return; }

		pose.pushPose();
		head.translateAndRotate(pose);
		pose.mulPose(Vector3f.XP.rotationDegrees(180F));
		pose.mulPose(Vector3f.YP.rotationDegrees(180F));
		pose.translate(x, y, z);
		pose.scale(scale, scale, scale);
		RenderUtil.renderBlock(pose, buf, new RenderColor(1, 1, 1, light, OverlayTexture.NO_OVERLAY), bItem.getBlock());
		pose.popPose();
	}

	public void renderShadow(AbstractSMBoss entity, PoseStack pose, MultiBufferSource buf, float swing, float swingAmount, float parTick, int light, float ageTick, float headYaw, float headPitch, float rgb, float addY, float scale) {
		if (!entity.isMagic()) { return; }

		pose.pushPose();
		pose.scale(scale, scale, scale);
		EntityModel<T> model = this.getModel();
		model.prepareMobModel((T) entity, swing, swingAmount, parTick);
		this.getParentModel().copyPropertiesTo(model);
		VertexConsumer ver = buf.getBuffer(RenderType.entityCutoutNoCull(this.getTex()));
		model.setupAnim((T) entity, swing, swingAmount, ageTick, headYaw, headPitch);
		model.renderToBuffer(pose, ver, light, OverlayTexture.NO_OVERLAY, rgb, rgb, rgb, 1F);
		pose.popPose();
	}

	protected float xOffset(float size) {
		return size * 0.01F;
	}

	protected ResourceLocation getTex() {
		return null;
	}

	protected EntityModel<T> getModel() {
		return this.model;
	}

	protected void setModel(EntityModel<T> model) {
		this.model = model;
	}

	protected ModelPart getModel(EntityRendererProvider.Context con, ModelLayerLocation layer) {
		return con.getModelSet().bakeLayer(layer);
	}
}
