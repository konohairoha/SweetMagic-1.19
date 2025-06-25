package sweetmagic.init.render.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import sweetmagic.init.BlockInit;
import sweetmagic.init.ItemInit;
import sweetmagic.init.entity.monster.boss.DemonsBelial;
import sweetmagic.init.render.entity.model.DemonsBelialModel;
import sweetmagic.util.RenderUtil;
import sweetmagic.util.RenderUtil.RenderColor;

public class DemonsBelialLayer<T extends DemonsBelial, M extends DemonsBelialModel<T>> extends AbstractEntityLayer<T, M> {

	private static final Block FLAME = BlockInit.belial_flame;
	private static final ItemStack STACK = new ItemStack(ItemInit.angel_wing_b);

	public DemonsBelialLayer(RenderLayerParent<T, M> layer, EntityRendererProvider.Context con) {
		super(layer, con);
		this.setModel(new DemonsBelialModel<>(this.getModel(con, DemonsBelialModel.LAYER)));
	}

	public void render(PoseStack pose, MultiBufferSource buf, int light, T entity, float swing, float swingAmount, float parTick, float ageTick, float headYaw, float headPitch) {
		if(!entity.isAlive()) { return; }
		for(int i = 0; i < 2; i++) {
			this.renderAngelWingBig(entity, pose, buf, light, i == 0);
			this.renderAngelWingSmall(entity, pose, buf, light, i == 0);
			this.renderFlame(entity, pose, buf, light, i == 0);
		}
	}

	public void renderAngelWingBig(T entity, PoseStack pose, MultiBufferSource buf, int light, boolean isReverse) {
		float scale = 2.5F;
		float reverseRate = isReverse ? -1F : 1F;
		pose.pushPose();
		M model = this.getParentModel();
		model.translateAndRotate(model.body, pose);
		pose.translate(-0.15F * reverseRate, -0.75F, 0.4F);
		pose.mulPose(Vector3f.YN.rotationDegrees((60F + 20F * Mth.sin(entity.tickCount * 0.1F)) * reverseRate));
		pose.scale(scale, -scale, scale);
		this.render.renderItem(entity, STACK, ItemTransforms.TransformType.FIXED, false, pose, buf, light);
		pose.popPose();
	}

	public void renderAngelWingSmall(T entity, PoseStack pose, MultiBufferSource buf, int light, boolean isReverse) {
		float scale = 1.85F;
		float reverseRate = isReverse ? -1F : 1F;
		pose.pushPose();
		M model = this.getParentModel();
		model.translateAndRotate(model.body, pose);
		pose.translate(-0.25F * reverseRate, -0.25F, 0.45F);
		pose.mulPose(Vector3f.YN.rotationDegrees((60F - 20F * Mth.sin(entity.tickCount * 0.1F)) * reverseRate));
		pose.mulPose(Vector3f.XP.rotationDegrees(-50F));
		pose.scale(scale, -scale, scale);
		this.render.renderItem(entity, STACK, ItemTransforms.TransformType.FIXED, false, pose, buf, light);
		pose.popPose();
	}

	public void renderFlame(T entity, PoseStack pose, MultiBufferSource buf, int light, boolean isLeft) {
		if(entity.getNoMove() || !entity.isHalfHealth(entity)) { return; }
		float scale = 0.55F;
		pose.pushPose();
		M model = this.getParentModel();
		model.translateAndRotate(isLeft ? model.leftArm : model.rightArm, pose);
		pose.scale(scale, -scale * 1.5F, scale);
		pose.translate(isLeft ? 1F : -2F, -1.5F, -0.35F);
		RenderUtil.renderBlock(pose, buf, new RenderColor(1, 1, 1, light, OverlayTexture.NO_OVERLAY), FLAME);
		pose.popPose();
	}
}
