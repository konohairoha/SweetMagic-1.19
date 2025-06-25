package sweetmagic.init.render.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import sweetmagic.init.BlockInit;
import sweetmagic.init.ItemInit;
import sweetmagic.init.entity.animal.StellaWizard;
import sweetmagic.init.render.entity.model.StellaWizardModel;
import sweetmagic.util.RenderUtil;
import sweetmagic.util.RenderUtil.RenderColor;

public class StellaLayer<T extends StellaWizard, M extends StellaWizardModel<T>> extends AbstractEntityLayer<T, M> {

	private static final ItemStack RIBBON = new ItemStack(ItemInit.wizard_ribbon);
	private static final Block CRYSTAL = BlockInit.spawn_stone_m;

	public StellaLayer(RenderLayerParent<T, M> layer, EntityRendererProvider.Context con) {
		super(layer, con);
	}

	public void render(PoseStack pose, MultiBufferSource buf, int light, T entity, float swing, float swingAmount, float parTick, float ageTick, float netHeadYaw, float headPitch) {

		if(entity.getCrystal()) {
			this.renderCrystal(entity, pose, buf, light);
		}

		else {
			this.renderArmWithItem(entity, pose, buf, light);
		}

		this.renderHead(entity, this.getParentModel().getHead(), pose, buf, light, 0.67F, -0.33F, -0.1F, -0.33F);
	}

	protected void renderArmWithItem(T entity, PoseStack pose, MultiBufferSource buf, int light) {

		pose.pushPose();
		M model = this.getParentModel();
		model.translateAndRotate(model.head, pose);
		pose.mulPose(Vector3f.YP.rotationDegrees(180F));
		pose.mulPose(Vector3f.XP.rotationDegrees(180F));
		float scale = 0.45F;
		pose.translate(0D, 0.45D, -0.2D);
		pose.scale(scale, scale, scale);
		this.render.renderItem(entity, RIBBON, ItemTransforms.TransformType.FIXED, false, pose, buf, light);
		pose.popPose();

		pose.pushPose();
		boolean flag = true;
		ItemStack wand = entity.getStack();

		if (entity.getShit()) {
			flag = false;
			pose.mulPose(Vector3f.ZP.rotationDegrees(180F));
			pose.translate(0D, -0.75D, 0.075D);
		}

		if(flag) {
			model.translateAndRotate(model.getArm(false), pose);
			pose.mulPose(Vector3f.XP.rotationDegrees(225F));
			pose.mulPose(Vector3f.YP.rotationDegrees(180F));
			pose.translate(0D, -0.2D, -0.5D);
		}

		this.render.renderItem(entity, wand, ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, false, pose, buf, light);
		pose.popPose();
	}

	protected void renderCrystal(T entity, PoseStack pose, MultiBufferSource buf, int light) {
		float scale = 0.75F + (entity.getCrystalHealth() / entity.getMaxCrystalHealth()) * 1.5F;
		pose.pushPose();
		pose.scale(scale, scale * 1.5F, scale);
		pose.translate(-0.5F, 0.5F, 0.5F);
		pose.mulPose(Vector3f.XP.rotationDegrees(180F));
		RenderUtil.renderBlock(pose, buf, new RenderColor(1, 1, 1, light, OverlayTexture.NO_OVERLAY), CRYSTAL);
		pose.popPose();
	}
}
