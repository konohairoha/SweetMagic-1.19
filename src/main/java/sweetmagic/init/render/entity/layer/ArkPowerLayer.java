package sweetmagic.init.render.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Spider;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.PotionInit;

public class ArkPowerLayer <T extends Spider, M extends SpiderModel<T>> extends AbstractEntityLayer<T, M> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/entity/creeper_armor2.png");

	public ArkPowerLayer(RenderLayerParent<T, M> layer, EntityRendererProvider.Context con) {
		super(layer, con);
		this.setModel(new SpiderModel<>(con.getModelSet().bakeLayer(ModelLayers.SPIDER)));
	}

	public void render(PoseStack pose, MultiBufferSource buf, int light, T entity, float swing, float swingAmount, float parTick, float ageTick, float netHeadYaw, float headPitch) {
		if (!entity.hasEffect(PotionInit.leader_flag)) { return; }

		pose.pushPose();
		pose.scale(1.075F, 1.075F, 1.075F);
		pose.translate(0F, -0.05F, 0F);
		float f = (float) entity.tickCount + parTick;
		EntityModel<T> eModel = this.getModel();
		eModel.prepareMobModel(entity, swing, swingAmount, parTick);
		this.getParentModel().copyPropertiesTo(eModel);
		VertexConsumer ver = buf.getBuffer(RenderType.energySwirl(this.getTex(), this.xOffset(f) % 1F, f * 0.025F % 1F));
		eModel.setupAnim(entity, swing, swingAmount, ageTick, netHeadYaw, headPitch);
		eModel.renderToBuffer(pose, ver, light, OverlayTexture.NO_OVERLAY, 0.65F, 0.65F, 0.65F, 1F);
		pose.popPose();
	}

	protected ResourceLocation getTex() {
		return TEX;
	}
}
