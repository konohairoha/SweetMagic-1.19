package sweetmagic.init.render.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.model.EntityModel;
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
import sweetmagic.init.entity.monster.boss.ElshariaCurious;
import sweetmagic.init.render.entity.model.HolyModel;
import sweetmagic.util.RenderUtil;
import sweetmagic.util.RenderUtil.RenderColor;

public class ElshariaCuriousLayer<T extends ElshariaCurious, M extends EntityModel<T>> extends AbstractEntityLayer<T, M> {

	private static final Block RING = BlockInit.kogen;
	private static final ItemStack STACK = new ItemStack(ItemInit.angel_wing_old);

	public ElshariaCuriousLayer(RenderLayerParent<T, M> layer, EntityRendererProvider.Context con) {
		super(layer, con);
		this.setModel(new HolyModel<>(this.getModel(con, HolyModel.LAYER)));
	}

	public void render(PoseStack pose, MultiBufferSource buf, int light, T entity, float swing, float swingAmount, float parTick, float ageTick, float netHeadYaw, float headPitch) {
		this.renderAngelWingBig(entity, pose, buf, light, false);
		this.renderAngelWingBig(entity, pose, buf, light, true);
		this.renderAngelRing(entity, pose, buf, light);
		this.renderShadow(entity, pose, buf, swing, swingAmount, parTick, light, ageTick, netHeadYaw, headPitch, 0.5F, 0F, 1.15F);
	}

	public void renderAngelWingBig(T entity, PoseStack pose, MultiBufferSource buf, int light, boolean isReverse) {
		float scale = entity.getCharge() ? 2.5F : 1.35F;
		float reverseRate = isReverse ? -1F : 1F;
		pose.pushPose();
		pose.translate(-0.15F * reverseRate, -0.15F, 0.15F);
		pose.mulPose(Vector3f.YN.rotationDegrees((60F + 20F * Mth.sin(entity.tickCount * 0.1F)) * reverseRate));
		pose.scale(scale, -scale, scale);
		this.render.renderItem(entity, STACK, ItemTransforms.TransformType.FIXED, false, pose, buf, light);
		pose.popPose();
	}

	public void renderAngelRing(T entity, PoseStack pose, MultiBufferSource buf, int light) {
		float scale = entity.getCharge() ? 2F : 1.15F;
		pose.pushPose();
		pose.scale(scale, scale, scale);
		pose.translate(-0.5F, -0.5F, -0.5F);
		RenderUtil.renderBlock(pose, buf, new RenderColor(1, 1, 1, light, OverlayTexture.NO_OVERLAY), RING);
		pose.popPose();
	}
}
