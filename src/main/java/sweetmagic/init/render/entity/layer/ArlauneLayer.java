package sweetmagic.init.render.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.ItemInit;
import sweetmagic.init.entity.monster.boss.Arlaune;
import sweetmagic.init.render.entity.model.ArlauneModel;

public class ArlauneLayer <T extends Arlaune, M extends ArlauneModel<T>> extends AbstractEntityLayer<T, M> {

	private static final ItemStack HAIRPIN = new ItemStack(ItemInit.cherry_ornate_hairpin);
	private static final ItemStack UMBRELLA = new ItemStack(ItemInit.japanese_umbrella);
	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/entity/arlaune.png");

	public ArlauneLayer(RenderLayerParent<T, M> layer, EntityRendererProvider.Context con) {
		super(layer, con);
		this.setModel(new ArlauneModel<>(this.getModel(con, ArlauneModel.LAYER)));
	}

	public void render(PoseStack pose, MultiBufferSource buf, int light, T entity, float swing, float swingAmount, float parTick, float ageTick, float netHeadYaw, float headPitch) {
		this.renderShadow(entity, pose, buf, swing, swingAmount, parTick, light, ageTick, netHeadYaw, headPitch, 0.5F, -1F, 1F);
		this.renderArmWithItem(entity, pose, buf, light);
	}

	protected void renderArmWithItem(T entity, PoseStack pose, MultiBufferSource buf, int light) {

		pose.pushPose();
		M model = this.getParentModel();
		model.translateAndRotate(model.head, pose);
		pose.mulPose(Vector3f.YP.rotationDegrees(180F));
		pose.mulPose(Vector3f.XP.rotationDegrees(180F));
		pose.translate(0D, -0.2D, -0.5D);
		pose.translate(0.3D, 0.4D, 0.45D);
		this.renderItem(entity, HAIRPIN, pose, buf, light);
		pose.popPose();

		pose.pushPose();
		model.translateAndRotate(model.getArm(true), pose);
		pose.mulPose(Vector3f.YP.rotationDegrees(0F));
		pose.mulPose(Vector3f.XP.rotationDegrees(120F));
		pose.translate(0D, -0.1D, -0.2D);

		if (entity.getMagic()) {
			pose.translate(0D, 0.2D, 0D);
		}

		pose.translate(0D, -0.4D, -0.2875D);
		this.renderItem(entity, UMBRELLA, pose, buf, light);
		pose.popPose();
	}

	protected ResourceLocation getTex() {
		return TEX;
	}
}
