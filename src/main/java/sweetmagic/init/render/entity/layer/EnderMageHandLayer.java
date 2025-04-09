package sweetmagic.init.render.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.model.EndermanModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import sweetmagic.init.EntityInit;
import sweetmagic.init.ItemInit;

public class EnderMageHandLayer <T extends Monster, M extends EndermanModel<T>> extends AbstractEntityLayer<T, M> {

	private static final ItemStack WAND = new ItemStack(ItemInit.wood_wand_g);
	private static final ItemStack ISWORD = new ItemStack(Items.IRON_SWORD);
	private static final ItemStack SSWONRD = new ItemStack(Items.WOODEN_SWORD);

	public EnderMageHandLayer(RenderLayerParent<T, M> layer, EntityRendererProvider.Context con) {
		super(layer, con);
		this.setModel(new EndermanModel<>(con.getModelSet().bakeLayer(ModelLayers.ENDERMAN)));
	}

	public void render(PoseStack pose, MultiBufferSource buf, int light, T entity, float swing, float swingAmount, float parTick, float ageTick, float netHeadYaw, float headPitch) {
		this.renderArmWithItem(entity, pose, buf, light, swing, swingAmount, parTick, ageTick, netHeadYaw, headPitch);
		this.renderHead(entity, this.getParentModel().getHead(), pose, buf, light, 0.67F, -0.33F, 0F, -0.33F);
	}

	protected void renderArmWithItem(T entity, PoseStack pose, MultiBufferSource buf, int light, float swing, float swingAmount, float parTick, float ageTick, float netHeadYaw, float headPitch) {
		pose.pushPose();
		this.getParentModel().translateToHand(HumanoidArm.RIGHT, pose);
		pose.mulPose(Vector3f.XP.rotationDegrees(-90F));
		pose.mulPose(Vector3f.YP.rotationDegrees(180F));
		pose.translate(0D, 0.15D, -1.65D);
		this.render.renderItem(entity, this.getStack(entity.getType()), ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, false, pose, buf, light);
		pose.popPose();
	}

	public ItemStack getStack(EntityType<?> type) {

		if (type == EntityInit.enderMage) {
			return WAND;
		}

		else if (type == EntityInit.enderShadow) {
			return ISWORD;
		}

		else if (type == EntityInit.enderShadowMirage) {
			return SSWONRD;
		}

		return ItemStack.EMPTY;
	}
}
