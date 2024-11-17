package sweetmagic.init.render.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.BlazeModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.PotionInit;
import sweetmagic.init.entity.monster.BlazeTempest;

public class TempestLayer <T extends BlazeTempest, M extends BlazeModel<T>> extends AbstractEntityLayer<T, M> {

	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/entity/mob_armor.png");

	public TempestLayer(RenderLayerParent<T, M> layer, EntityRendererProvider.Context con) {
		super(layer, con);
		this.setModel(new BlazeModel<>(con.getModelSet().bakeLayer(ModelLayers.BLAZE)));
	}

	public void render(PoseStack pose, MultiBufferSource buf, int light, T entity, float swing, float swingAmount, float parTick, float ageTick, float netHeadYaw, float headPitch) {
		if (!entity.hasEffect(PotionInit.leader_flag)) { return; }

		pose.scale(1.15F, 1.15F, 1.15F);
		float f = (float) entity.tickCount + parTick;
		EntityModel<T> eModel = this.getModel();
		eModel.prepareMobModel(entity, swing, swingAmount, parTick);
		this.getParentModel().copyPropertiesTo(eModel);
		VertexConsumer ver = buf.getBuffer(RenderType.energySwirl(this.getTex(), this.xOffset(f) % 1F, f * 0.025F % 1F));
		eModel.setupAnim(entity, swing, swingAmount, ageTick, netHeadYaw, headPitch);
		eModel.renderToBuffer(pose, ver, light, OverlayTexture.NO_OVERLAY, 0.65F, 0.65F, 0.65F, 1F);
	}

	protected ResourceLocation getTex() {
		return TEX;
	}
}
