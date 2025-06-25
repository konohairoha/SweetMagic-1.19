package sweetmagic.init.render.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import sweetmagic.init.potion.SMEffect;

public class FrostEffectRender <T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {

	public FrostEffectRender(RenderLayerParent<T, M> render) {
		super(render);
	}

	@Override
	public void render(PoseStack pose, MultiBufferSource buffer, int light, T entity, float limbSwing, float swingAmount, float parTick, float ageTick, float headYaw, float headPitch) {
		AttributeInstance speed = entity.getAttribute(Attributes.MOVEMENT_SPEED);
		if (speed == null || speed.getModifier(SMEffect.MODIFIER_UUID) == null) { return; }

		RandomSource rand = entity.getLevel().getRandom();
		rand.setSeed(entity.getId() * 3121L);
		BlockRenderDispatcher render = Minecraft.getInstance().getBlockRenderer();
		BlockState state = Blocks.FROSTED_ICE.defaultBlockState();

		for (int i = 0; i < 6; i++) {
			pose.pushPose();
			float dx = (rand.nextFloat() * (entity.getBbWidth() * 3.5F) - entity.getBbWidth()) * 0.25F;
			float dy = Math.max(1.5F - (rand.nextFloat()) * (entity.getBbHeight() * 0.5F), -0.1F);
			float dz = (rand.nextFloat() * (entity.getBbWidth() * 3.5F) - entity.getBbWidth()) * 0.25F;
			pose.translate(dx, dy, dz);
			pose.scale(0.5F, 0.5F, 0.5F);
			pose.mulPose(Vector3f.XP.rotationDegrees(rand.nextFloat() * 360F));
			pose.mulPose(Vector3f.YP.rotationDegrees(rand.nextFloat() * 360F));
			pose.mulPose(Vector3f.ZP.rotationDegrees(rand.nextFloat() * 360F));
			pose.translate(-0.5F, -0.5F, -0.5F);

			render.renderSingleBlock(state, pose, buffer, light, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, RenderType.translucentMovingBlock());
			pose.popPose();
		}
	}
}
