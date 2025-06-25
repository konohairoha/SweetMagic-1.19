package sweetmagic.init.render.entity.layer;

import java.util.Arrays;
import java.util.List;

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
import sweetmagic.init.entity.monster.boss.WitchSandryon;
import sweetmagic.init.render.entity.model.WindWitchModel;
import sweetmagic.util.RenderUtil;
import sweetmagic.util.RenderUtil.RenderColor;

public class WitchSandryonLayer<T extends WitchSandryon, M extends WindWitchModel<T>> extends AbstractEntityLayer<T, M> {

	private static final Block SQUARE4 = BlockInit.magic_square_h;
	private static final Block SQUARE5 = BlockInit.magic_square_l;
	private static final Block SQUARE5_B = BlockInit.magic_square_l_blank;

	private static final List<ItemStack> TIER4 = Arrays.<ItemStack> asList(
		new ItemStack(ItemInit.deuscrystal_wand), new ItemStack(ItemInit.deuscrystal_wand_b), new ItemStack(ItemInit.deuscrystal_wand_g), new ItemStack(ItemInit.deuscrystal_wand_p), new ItemStack(ItemInit.deuscrystal_wand_r), new ItemStack(ItemInit.deuscrystal_wand_y)
	);

	private static final List<ItemStack> TIER5 = Arrays.<ItemStack> asList(
		new ItemStack(ItemInit.cosmic_sacred_wand), new ItemStack(ItemInit.cosmic_aquamarine_wand), new ItemStack(ItemInit.cosmic_blizzard_wand), new ItemStack(ItemInit.cosmic_flugel_wand), new ItemStack(ItemInit.cosmic_prominence_wand), new ItemStack(ItemInit.cosmic_gravity_wand)
	);

	private static final ItemStack WAND = new ItemStack(ItemInit.cosmic_magia_wand);
	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/entity/witchsandryon.png");

	public WitchSandryonLayer(RenderLayerParent<T, M> layer, EntityRendererProvider.Context con) {
		super(layer, con);
		this.setModel(new WindWitchModel<>(this.getModel(con, WindWitchModel.LAYER)));
	}

	public void render(PoseStack pose, MultiBufferSource buf, int light, T entity, float swing, float swingAmount, float parTick, float ageTick, float netHeadYaw, float headPitch) {

		if (entity.getWandCharge()) {
			this.renderCycleWand(entity, pose, buf, light, TIER4, false);
		}

		else if (entity.getInfiniteWand()) {
			this.renderCycleWand(entity, pose, buf, light, TIER5, true);
		}

		else {
			this.renderHandWand(entity, pose, buf, light);
		}

		this.renderShadow(entity, pose, buf, swing, swingAmount, parTick, light, ageTick, netHeadYaw, headPitch, 0.5F, 0F, 1.15F);
	}

	protected void renderHandWand(T entity, PoseStack pose, MultiBufferSource buf, int light) {

		pose.pushPose();
		ISMMob smMob = (ISMMob) entity;

		if (!smMob.isTarget()) {
			pose.mulPose(Vector3f.XP.rotationDegrees(90F));
			pose.mulPose(Vector3f.ZP.rotationDegrees(270F));
			pose.mulPose(Vector3f.XN.rotationDegrees(-10F));
			pose.translate(-0.17D, -0.275D, -0.425D);
		}

		else {
			this.getParentModel().translateAndRotate(this.getParentModel().getArm(false), pose);
			pose.mulPose(Vector3f.XP.rotationDegrees(225F));
			pose.mulPose(Vector3f.YP.rotationDegrees(180F));
			pose.translate(0D, -0.2D, -0.55D);
		}

		this.render.renderItem(entity, WAND, ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, false, pose, buf, light);
		pose.popPose();
	}

	protected void renderCycleWand(T entity, PoseStack pose, MultiBufferSource buf, int light, List<ItemStack> stackList, boolean isTier5) {

		int size = isTier5 ? stackList.size() : entity.getWandSize();
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
			this.render.renderItem(entity, stackList.get(i), ItemTransforms.TransformType.FIXED, false, pose, buf, light);
			pose.popPose();
		}

		size = isTier5 ? 4 : 3;

		pose.pushPose();
		pose.translate(0D, 1.5D, 0D);
		pose.scale(size, size, size);
		float angle = tickCount / 10F * this.pi;
		pose.mulPose(Vector3f.YP.rotationDegrees(angle));
		pose.translate(-0.5D, 0D, -0.5D);
		float rgb = (float) Math.sin(tickCount / 10F) * 40F;
		Block square = isTier5 ? SQUARE5 : SQUARE4;
		RenderUtil.renderBlock(pose, buf, new RenderColor((72F + rgb) / 255F, (200F + rgb) / 255F, (200F + rgb) / 255F, light, OverlayTexture.NO_OVERLAY), square);
		pose.popPose();

		if (isTier5) {
			pose.pushPose();
			pose.translate(0D, 1D, 0D);
			size = 3;
			pose.scale(size, size, size);
			pose.mulPose(Vector3f.YP.rotationDegrees(-angle * 1.5F));
			pose.translate(-0.5D, 0D, -0.5D);
			float rgb2 = (float) Math.sin(tickCount / 10F) * 20F;
			RenderUtil.renderBlock(pose, buf, new RenderColor((125F + rgb2) / 255F, (225F + rgb2) / 255F, (225F + rgb2) / 255F, light, OverlayTexture.NO_OVERLAY), SQUARE5_B);
			pose.popPose();
		}
	}

	protected ResourceLocation getTex() {
		return TEX;
	}
}
