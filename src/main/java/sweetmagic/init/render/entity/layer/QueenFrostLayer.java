package sweetmagic.init.render.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.entity.monster.boss.QueenFrost;
import sweetmagic.init.render.entity.model.QuenModel;

public class QueenFrostLayer <T extends QueenFrost, M extends EntityModel<T>> extends AbstractEntityLayer<T, M> {

	private ItemStack stack = ItemStack.EMPTY;
	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/entity/queenfrost.png");

	public QueenFrostLayer(RenderLayerParent<T, M> layer, EntityRendererProvider.Context con) {
		super(layer, con);
		this.setModel(new QuenModel<>(con.getModelSet().bakeLayer(QuenModel.LAYER)));
	}

	public void render(PoseStack pose, MultiBufferSource buf, int light, QueenFrost entity, float swing, float swingAmount, float parTick, float ageTick, float netHeadYaw, float headPitch) {

		int size = entity.getArmor();
		float pi = 180F / (float) Math.PI;
		long gameTime = entity.getLevel().getGameTime();
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
			this.render.renderItem(entity, this.stack, ItemTransforms.TransformType.FIXED, false, pose, buf, light);
			pose.popPose();
		}

		float rgb = Math.min(entity.tickCount / 40F, 1F) * 0.5F;
		this.renderShadow(entity, pose, buf, swing, swingAmount, parTick, light, ageTick, netHeadYaw, headPitch, rgb, 0F, 1.15F);
	}

	protected ResourceLocation getTex() {
		return TEX;
	}
}
