package sweetmagic.init.render.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.item.ItemStack;
import sweetmagic.init.ItemInit;
import sweetmagic.init.entity.monster.boss.WhiteButler;
import sweetmagic.init.render.entity.model.WhiteButlerModel;

public class WhiteButlerLayer<T extends WhiteButler, M extends WhiteButlerModel<T>> extends AbstractEntityLayer<T, M> {

	private static final ItemStack KNIFE = new ItemStack(ItemInit.alt_sword);
	private static final ItemStack SICKLE = new ItemStack(ItemInit.alt_sickle);
	private static final ItemStack RIFLE = new ItemStack(ItemInit.cosmic_rifle);

	public WhiteButlerLayer(RenderLayerParent<T, M> layer, EntityRendererProvider.Context con) {
		super(layer, con);
		this.model = new WhiteButlerModel<>(this.getModel(con, WhiteButlerModel.LAYER));
	}

	public void render(PoseStack pose, MultiBufferSource buf, int light, T entity, float swing, float swingAmount, float parTick, float ageTick, float netHeadYaw, float headPitch) {
		this.renderItem(entity, pose, buf, light);
	}

	protected void renderItem(T entity, PoseStack pose, MultiBufferSource buf, int light) {

		M model = this.getParentModel();

		if (entity.getKnife()) {
			pose.pushPose();
			model.translateAndRotate(model.getArm(false), pose);
			pose.mulPose(Vector3f.XP.rotationDegrees(-90F));
			pose.mulPose(Vector3f.YP.rotationDegrees(180F));
			pose.translate(0D, 0.15D, -0.7D);
			this.render.renderItem(entity, KNIFE, ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, false, pose, buf, light);
			pose.popPose();
		}

		if (entity.getSickle()) {
			pose.pushPose();
			pose.mulPose(Vector3f.XP.rotationDegrees(90F));
			pose.mulPose(Vector3f.ZP.rotationDegrees(270F));
			pose.mulPose(Vector3f.XN.rotationDegrees(-10F));
			pose.translate(-0.15D, -0.3D, -0.825D);
			pose.scale(1F, 1.25F, 1.25F);
			this.render.renderItem(entity, SICKLE, ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, false, pose, buf, light);
			pose.popPose();
		}

		if (entity.getRifle()) {
			pose.pushPose();
			model.translateAndRotate(model.getArm(true), pose);
			pose.mulPose(Vector3f.XP.rotationDegrees(180F));
			pose.mulPose(Vector3f.YP.rotationDegrees(190F));
			pose.translate(0.225D, 0.08D, -0.65D);
			pose.scale(1.25F, 1.25F, 1.5F);
			this.render.renderItem(entity, RIFLE, ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, false, pose, buf, light);
			pose.popPose();
		}
	}
}
