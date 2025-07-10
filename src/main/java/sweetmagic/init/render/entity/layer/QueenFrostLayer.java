package sweetmagic.init.render.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit;
import sweetmagic.init.entity.monster.boss.QueenFrost;
import sweetmagic.init.render.entity.model.QuenModel;
import sweetmagic.util.RenderUtil;
import sweetmagic.util.RenderUtil.RenderColor;
import sweetmagic.util.RenderUtil.RenderInfo;

public class QueenFrostLayer <T extends QueenFrost, M extends EntityModel<T>> extends AbstractEntityLayer<T, M> {

	private ItemStack stack = ItemStack.EMPTY;
	private static final Block SQUARE_BLANK = BlockInit.magic_square_l_blank;
	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/entity/queenfrost.png");

	public QueenFrostLayer(RenderLayerParent<T, M> layer, EntityRendererProvider.Context con) {
		super(layer, con);
		this.setModel(new QuenModel<>(this.getModel(con, QuenModel.LAYER)));
	}

	public void render(PoseStack pose, MultiBufferSource buf, int light, T entity, float swing, float swingAmount, float parTick, float ageTick, float netHeadYaw, float headPitch) {

		int size = entity.getLectern() ? entity.getArmor() : entity.getHealthArmorProgress(8);
		float pi = 180F / (float) Math.PI;
		long gameTime = entity.tickCount;
		float rotY = (gameTime + parTick) / 90F;
		float scale = 1.15F;

		if (this.stack.isEmpty()) {
			this.stack = new ItemStack(Blocks.PACKED_ICE);
		}

		for (int i = 0; i < size; i++) {
			pose.pushPose();
			pose.translate(0F, 0.35F, 0F);
			pose.mulPose(Vector3f.YP.rotationDegrees(rotY * pi + (i * (360 / size)) + gameTime * 10.5F));
			pose.scale(scale, scale, scale);
			pose.translate(1.5F - (0.0055F * 1) , 0F, 0F);
			this.renderItemFix(entity, this.stack, pose, buf, light);
			pose.popPose();
		}

		float rgb = Math.min(entity.tickCount / 40F, 1F) * 0.5F;
		this.renderShadow(entity, pose, buf, swing, swingAmount, parTick, light, ageTick, netHeadYaw, headPitch, rgb, 0F, 1.15F);

		if(entity.isLaser()) {
			scale = 2F;
			pose.pushPose();
			pose.scale(scale, scale, scale);
			pose.translate(0D, 0.1D, -0.25D);
			float angle = (gameTime + parTick) / -20F * this.pi;
			pose.mulPose(Vector3f.XP.rotationDegrees(90F));
			pose.mulPose(Vector3f.YP.rotationDegrees(-angle));
			pose.translate(-0.5D, 0D, -0.5D);
			RenderInfo info = new RenderInfo(null, light, OverlayTexture.NO_OVERLAY, pose, buf);
			RenderUtil.renderBlock(info, new RenderColor(72F / 255F, 1F, 1F, info.light(), info.overlay()), SQUARE_BLANK);
			pose.popPose();
		}
	}

	protected ResourceLocation getTex() {
		return TEX;
	}
}
