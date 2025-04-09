package sweetmagic.init.render.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit;
import sweetmagic.init.ItemInit;
import sweetmagic.init.entity.monster.boss.HolyAngel;
import sweetmagic.init.render.entity.model.HolyModel;
import sweetmagic.util.RenderUtil;
import sweetmagic.util.RenderUtil.RenderColor;

public class HolyAngelLayer <T extends HolyAngel, M extends EntityModel<T>> extends AbstractEntityLayer<T, M> {

	private static final Block RING = BlockInit.kogen;
	private static final Block GLASS = BlockInit.yellow_glass;
	private static final ItemStack STACK = new ItemStack(ItemInit.angel_wing);
	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/entity/holyshining.png");

	public HolyAngelLayer(RenderLayerParent<T, M> layer, EntityRendererProvider.Context con) {
		super(layer, con);
		this.setModel(new HolyModel<>(con.getModelSet().bakeLayer(HolyModel.LAYER)));
	}

	public void render(PoseStack pose, MultiBufferSource buf, int light, T entity, float swing, float swingAmount, float parTick, float ageTick, float netHeadYaw, float headPitch) {
		this.renderAngelWingBig(entity, pose, buf, light, false);
		this.renderAngelWingBig(entity, pose, buf, light, true);
		this.renderAngelWingSmall(entity, pose, buf, light, false);
		this.renderAngelWingSmall(entity, pose, buf, light, true);
		this.renderAngelRing(entity, pose, buf, light);
		this.renderORU(entity, pose, buf, parTick, light);
		this.renderShadow(entity, pose, buf, swing, swingAmount, parTick, light, ageTick, netHeadYaw, headPitch, 0.5F, 0F, 1.15F);
	}

	public void renderAngelWingBig(T entity, PoseStack pose, MultiBufferSource buf, int light, boolean isReverse) {
		float scale = 1.35F;
		float reverseRate = (isReverse ? -1F : 1F);
		pose.pushPose();
		pose.translate(-0.15F * reverseRate, -0.15F, 0.15F);
		pose.mulPose(Vector3f.YN.rotationDegrees( (60F + 20F * Mth.sin(entity.tickCount * 0.1F)) * reverseRate ));
		pose.scale(scale, -scale, scale);
		this.render.renderItem(entity, STACK, ItemTransforms.TransformType.FIXED, false, pose, buf, light);
		pose.popPose();
	}

	public void renderAngelWingSmall(T entity, PoseStack pose, MultiBufferSource buf, int light, boolean isReverse) {
		float scale = 1.15F;
		float reverseRate = (isReverse ? -1F : 1F);
		pose.pushPose();
		pose.translate(-0.25F * reverseRate, 0.35F, 0.175F);
		pose.mulPose(Vector3f.YN.rotationDegrees( (60F - 20F * Mth.sin(entity.tickCount * 0.1F)) * reverseRate ));
		pose.mulPose(Vector3f.XP.rotationDegrees(-50F));
		pose.scale(scale, -scale, scale);
		this.render.renderItem(entity, STACK, ItemTransforms.TransformType.FIXED, false, pose, buf, light);
		pose.popPose();
	}

	public void renderAngelRing(T entity, PoseStack pose, MultiBufferSource buf, int light) {
		float scale = 1.15F;
		pose.pushPose();
		pose.translate(-0.575F, -0.65F, -0.55F);
		pose.scale(scale, scale, scale);
		RenderUtil.renderBlock(pose, buf, new RenderColor(1, 1, 1, light, OverlayTexture.NO_OVERLAY), RING);
		pose.popPose();
	}

	public void renderORU(T entity, PoseStack pose, MultiBufferSource buf, float parTick, int light) {

		int oruSize = entity.getORU();
		float scale = 0.25F;
		float pi = 180F / (float) Math.PI;
		float rotY = (entity.tickCount + parTick) / 15F;
		float addY = 0F;
		float rot = rotY * pi;

		for (int i = 0; i < oruSize; i++) {

			boolean isReverse = i % 2 == 0;

			if (isReverse) {
				addY -= 0.3F;
				rot *= -1F;
			}

			pose.pushPose();
			pose.translate(0F, 1.35F + addY, 0F);
			pose.mulPose(Vector3f.YP.rotationDegrees(rot + (isReverse ? 180F : 0F) + (i / 2) * 30F ));
			pose.scale(scale, scale, scale);
			pose.translate(4F, 0F, 0F);
			RenderUtil.renderBlock(pose, buf, new RenderColor(1, 1, 1, light, OverlayTexture.NO_OVERLAY), GLASS);
			pose.popPose();
		}
	}

	protected ResourceLocation getTex() {
		return TEX;
	}
}
