package sweetmagic.init.render.entity.layer;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import sweetmagic.api.iitem.IWand;

public class WandRenderLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {

	public WandRenderLayer(RenderLayerParent<T, M> render) {
		super(render);
	}

	@Override
	public void render(PoseStack pose, MultiBufferSource buffer, int light, T entity, float limbSwing, float swingAmount, float parTick, float ageTick, float headYaw, float headPitch) {
		if (!(entity instanceof Player player)) { return; }

		// メインハンドが杖なら終了
		ItemStack mainStack = player.getMainHandItem();
		if (!mainStack.isEmpty() && mainStack.getItem() instanceof IWand) { return; }

		List<ItemStack> stackList = player.getInventory().items.stream().filter(s -> s.getItem() instanceof IWand).toList();
		if (stackList.isEmpty()) { return; }

		ItemStack stack = stackList.get(0);
		pose.pushPose();

		if (this.getParentModel() instanceof HumanoidModel model) {
			if (model.body.zRot != 0F) {
				pose.mulPose(Vector3f.ZP.rotation(model.body.zRot));
			}

			if (model.body.yRot != 0F) {
				pose.mulPose(Vector3f.YP.rotation(model.body.yRot));
			}

			if (model.body.xRot != 0F) {
				pose.mulPose(Vector3f.XP.rotation(model.body.xRot));
			}
		}

		IWand.getWand(stack).renderWand(pose, buffer, player, parTick);
		ItemRenderer render = Minecraft.getInstance().getItemRenderer();
		render.renderStatic(stack, ItemTransforms.TransformType.FIXED, light, OverlayTexture.NO_OVERLAY, pose, buffer, 0);
		pose.popPose();
	}
}
