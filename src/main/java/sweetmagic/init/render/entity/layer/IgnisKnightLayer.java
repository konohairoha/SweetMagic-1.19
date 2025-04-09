package sweetmagic.init.render.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit;
import sweetmagic.init.EnchantInit;
import sweetmagic.init.ItemInit;
import sweetmagic.init.entity.monster.boss.IgnisKnight;
import sweetmagic.init.render.entity.model.IgnisModel;
import sweetmagic.util.RenderUtil;
import sweetmagic.util.RenderUtil.RenderColor;

public class IgnisKnightLayer <T extends IgnisKnight, M extends IgnisModel<T>> extends AbstractEntityLayer<T, M> {

	private static final ItemStack ARMOR = new ItemStack(ItemInit.ignis_armor);
	private static final ItemStack HAMMER = new ItemStack(ItemInit.aether_hammer);
	private static final Block SQUARE_BLOCK = BlockInit.magic_square_l_blank;
	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/entity/ignis_knight.png");

	public IgnisKnightLayer(RenderLayerParent<T, M> layer, EntityRendererProvider.Context con) {
		super(layer, con);
		this.setModel(new IgnisModel<>(this.getModel(con, IgnisModel.LAYER)));
	}

	public void render(PoseStack pose, MultiBufferSource buf, int light, T entity, float swing, float swingAmount, float parTick, float ageTick, float headYaw, float headPitch) {
		this.renderShadow(entity, pose, buf, swing, swingAmount, parTick, light, ageTick, headYaw, headPitch, 0.5F, -0.65F, 1.15F);
		this.renderArmWithItem(entity, pose, buf, light, swing, swingAmount, parTick, ageTick, headYaw, headPitch);
	}

	protected void renderArmWithItem(T entity, PoseStack pose, MultiBufferSource buf, int light, float swing, float swingAmount, float parTick, float ageTick, float headYaw, float headPitch) {

		int attackType = entity.getAttackType();

		if (entity.isOnGround() && attackType == 2 && entity.isSwing()) {
			pose.pushPose();
			long gameTime = entity.level.getGameTime();
			pose.translate(0D, Math.sin((gameTime + parTick) / 15F) * 0.02D, 0D);
			pose.scale(10F, 10F, 10F);
			float angle = (gameTime + parTick) / 20.0F * (180F / (float) Math.PI);
			pose.mulPose(Vector3f.YP.rotationDegrees(angle));
			pose.translate(-0.5D, 0.14D, -0.5D);

			RenderColor color = new RenderColor(113F / 255F, 223F / 255F, 1F, light, OverlayTexture.NO_OVERLAY) ;
			RenderUtil.renderBlock(pose, buf, color, SQUARE_BLOCK);
			pose.popPose();
		}

		int size = entity.getArmor();

		if (size > 0) {

			float pi = 180F / (float) Math.PI;
			long gameTime = entity.getLevel().getGameTime();
			float rotY = (gameTime + parTick) / 90F;
			float scale = 1.15F;

			for (int i = 0; i < size; i++) {
				pose.pushPose();
				pose.translate(0.0F, 0.35F, 0.0F);
				pose.mulPose(Vector3f.YP.rotationDegrees(rotY * pi + (i * (360 / size)) + gameTime * 10.5F));
				pose.scale(scale, scale, scale);
				pose.translate(1.5F - (0.0055F * 1) , 0F, 0F);
				this.render.renderItem(entity, ARMOR, ItemTransforms.TransformType.FIXED, false, pose, buf, light);
				pose.popPose();
			}
		}

		this.getParentModel().translateAndRotate(this.getParentModel().getArm(true), pose);
		boolean hasTag = HAMMER.hasTag();

		if (attackType == 2 && !hasTag) {
			HAMMER.enchant(EnchantInit.aethercharm, 0);
		}

		else if (hasTag) {
			HAMMER.setTag(new CompoundTag());
		}

		pose.pushPose();
		pose.mulPose(Vector3f.XP.rotationDegrees(225.0F));
		pose.mulPose(Vector3f.YP.rotationDegrees(180.0F));
		pose.translate(0.0D, -0.2D, -0.5D);
		pose.mulPose(Vector3f.ZP.rotationDegrees(230.0F));
		pose.translate(0.1D, 0.2D, 0.05D);
		this.render.renderItem(entity, HAMMER, ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, false, pose, buf, light);
		pose.popPose();
	}

	protected ResourceLocation getTex() {
		return TEX;
	}
}
