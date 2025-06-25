package sweetmagic.init.render.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;

import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.entity.monster.boss.BlitzWizardMaster;
import sweetmagic.init.render.entity.model.BlitzWizardModel;

public class BlitzWizarMasterLayer<T extends BlitzWizardMaster, M extends BlitzWizardModel<T>> extends AbstractEntityLayer<T, M> {

	private static final ResourceLocation MAGIC_BOOK = SweetMagicCore.getSRC("textures/entity/blitzbook.png");
	private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(MAGIC_BOOK);
	private final BookModel bookModel;

	public BlitzWizarMasterLayer(RenderLayerParent<T, M> layer, EntityRendererProvider.Context con) {
		super(layer, con);
		this.bookModel = new BookModel(con.bakeLayer(ModelLayers.BOOK));
	}

	public void render(PoseStack pose, MultiBufferSource buf, int light, T entity, float swing, float swingAmount, float parTick, float ageTick, float netHeadYaw, float headPitch) {
		this.renderArmWithItem(entity, pose, buf, light, parTick);
	}

	protected void renderArmWithItem(T entity, PoseStack pose, MultiBufferSource buf, int light, float parTick) {

		pose.pushPose();
		float bookValue = 0F;

		if (entity.getUpBook()) {
			pose.scale(3F, 3F, 3F);
			pose.translate(0D, -2D, 0D);
			pose.mulPose(Vector3f.XP.rotationDegrees(90F));
			pose.mulPose(Vector3f.YP.rotationDegrees(90F));
			bookValue = -200F;
		}

		else {
			M model = this.getParentModel();
			model.translateAndRotate(model.getArm(false), pose);
			pose.mulPose(Vector3f.XP.rotationDegrees(225F));
			pose.mulPose(Vector3f.YP.rotationDegrees(90F));
			pose.translate(-0.82D, -0.2D, -0.01D);
		}

		this.bookModel.setupAnim(0F, Mth.clamp(0F, 0F, 1F), Mth.clamp(0F, 0F, 1F), bookValue);
		VertexConsumer vert = buf.getBuffer(RENDER_TYPE).color(0F, 0F, 0F, 1F);
		this.bookModel.render(pose, vert, light, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1F);
		pose.popPose();
	}
}
