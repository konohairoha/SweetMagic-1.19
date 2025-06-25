package sweetmagic.init.render.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.init.BlockInit;
import sweetmagic.init.ItemInit;
import sweetmagic.init.entity.monster.boss.WindWitchMaster;
import sweetmagic.init.render.entity.model.WindWitchModel;
import sweetmagic.util.RenderUtil;
import sweetmagic.util.RenderUtil.RenderColor;

public class WitchMasterWandLayer<T extends WindWitchMaster, M extends WindWitchModel<T>> extends AbstractEntityLayer<T, M> {

	private static final ItemStack WAND = new ItemStack(ItemInit.deuscrystal_wand_g);
	private static final Block SQUARE4 = BlockInit.magic_square_h;
	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/entity/windwitch_master.png");

	public WitchMasterWandLayer(RenderLayerParent<T, M> layer, EntityRendererProvider.Context con) {
		super(layer, con);
		this.setModel(new WindWitchModel<>(this.getModel(con, WindWitchModel.LAYER)));
	}

	public void render(PoseStack pose, MultiBufferSource buf, int light, T entity, float swing, float swingAmount, float parTick, float ageTick, float netHeadYaw, float headPitch) {

		if(entity.isCharge()) {
			this.renderCycleWand(entity, pose, buf, light, WAND);
		}

		else {
			this.renderArmWithItem(entity, pose, buf, light);
		}

		this.renderShadow(entity, pose, buf, swing, swingAmount, parTick, light, ageTick, netHeadYaw, headPitch, 0.5F, -0.65F, 1.15F);
	}

	protected void renderArmWithItem(T entity, PoseStack pose, MultiBufferSource buf, int light) {

		pose.pushPose();
		ISMMob smMob = (ISMMob) entity;

		if (!smMob.isTarget()) {
			pose.mulPose(Vector3f.XP.rotationDegrees(90F));
			pose.mulPose(Vector3f.ZP.rotationDegrees(270F));
			pose.mulPose(Vector3f.XN.rotationDegrees(-10F));
			pose.translate(-0.17D, -0.275D, -0.425D);
		}

		else {
			M model = this.getParentModel();
			model.translateAndRotate(model.getArm(false), pose);
			pose.mulPose(Vector3f.XP.rotationDegrees(225F));
			pose.mulPose(Vector3f.YP.rotationDegrees(180F));
			pose.translate(0D, -0.2D, -0.55D);
		}

		this.render.renderItem(entity, WAND, ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, false, pose, buf, light);
		pose.popPose();
	}


	protected void renderCycleWand(T entity, PoseStack pose, MultiBufferSource buf, int light,ItemStack stack) {

		int size = 6;
		float pi = 180F / (float) Math.PI;
		int tickCount = entity.tickCount;
		float rotY = tickCount / 90F;
		float scale = 1.25F;

		for (int i = 0; i < size; i++) {
			pose.pushPose();
			pose.translate(0F, 0.65F, 0F);
			pose.mulPose(Vector3f.ZP.rotationDegrees(180F));
			pose.mulPose(Vector3f.YP.rotationDegrees(rotY * pi + (i * (360 / size)) + tickCount * 7.5F));
			pose.scale(scale, scale + 0.25F, scale);
			pose.translate(-1.325F - (0.0055F * 1) , 0F, 0F);
			pose.mulPose(Vector3f.ZP.rotationDegrees(-45F));
			this.render.renderItem(entity, stack, ItemTransforms.TransformType.FIXED, false, pose, buf, light);
			pose.popPose();
		}

		size = 3;

		pose.pushPose();
		pose.translate(0D, 1.5D, 0D);
		pose.scale(size, size, size);
		float angle = tickCount / 10F * this.pi;
		pose.mulPose(Vector3f.YP.rotationDegrees(angle));
		pose.translate(-0.5D, 0D, -0.5D);
		float rgb = (float) Math.sin(tickCount / 10F) * 40F;
		Block square = SQUARE4;
		RenderUtil.renderBlock(pose, buf, new RenderColor((72F + rgb) / 255F, (200F + rgb) / 255F, (200F + rgb) / 255F, light, OverlayTexture.NO_OVERLAY), square);
		pose.popPose();
	}

	protected ResourceLocation getTex() {
		return TEX;
	}
}
