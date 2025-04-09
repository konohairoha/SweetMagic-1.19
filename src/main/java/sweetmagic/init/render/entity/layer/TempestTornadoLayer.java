package sweetmagic.init.render.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit;
import sweetmagic.init.entity.monster.BlazeTempestTornado;
import sweetmagic.init.render.entity.model.TempestModel;
import sweetmagic.util.RenderUtil;
import sweetmagic.util.RenderUtil.RenderColor;

public class TempestTornadoLayer <T extends BlazeTempestTornado, M extends EntityModel<T>> extends AbstractEntityLayer<T, M> {

	private static final ItemStack CANDLE = new ItemStack(BlockInit.candle);
	private static final ItemStack CANDLE_EX = new ItemStack(BlockInit.candle_ex);
	private static final Block TORNADO = BlockInit.tornado;
	private static final ResourceLocation TEX = SweetMagicCore.getSRC("textures/entity/mob_armor.png");

	public TempestTornadoLayer(RenderLayerParent<T, M> layer, EntityRendererProvider.Context con) {
		super(layer, con);
		this.setModel(new TempestModel<>(this.getModel(con, TempestModel.LAYER)));
	}

	public void render(PoseStack pose, MultiBufferSource buf, int light, T entity, float swing, float swingAmount, float parTick, float ageTick, float netHeadYaw, float headPitch) {

		float scale = 1F;
		int tickCount = entity.tickCount;
		float rotY = (tickCount + parTick) * 100F / (float) Math.PI;

		pose.pushPose();
		pose.mulPose(Vector3f.YP.rotationDegrees(rotY));
		pose.translate(-0.5F, 1.05F, -0.5F);
		pose.scale(scale, -scale, scale);

		RenderColor color = new RenderColor(0F, 0F, 0F, light, OverlayTexture.NO_OVERLAY);
		RenderUtil.renderBlock(pose, buf, color, TORNADO);
		pose.popPose();

		int count = entity.getCandole();
		int size = 4;
		float pi = 180F / (float) Math.PI;
		rotY = (tickCount + parTick) / 10F;
		scale = 0.75F;

		for (int i = 0; i < size; i++) {
			pose.pushPose();
			pose.translate(0F, -0.35F + Mth.cos(((float)(i * 2) + ageTick) * 0.1F) * 0.1F, 0F);
			pose.mulPose(Vector3f.YP.rotationDegrees(-rotY * pi + (i * (360 / size))));
			pose.scale(scale, -scale, scale);
			pose.translate(0.7F - (0.0055F * 1) , 0F, 0F);

			ItemStack stack = count > i ? CANDLE : CANDLE_EX;
			this.render.renderItem(entity, stack, ItemTransforms.TransformType.FIXED, false, pose, buf, light);
			pose.popPose();
		}
	}

	protected ResourceLocation getTex() {
		return TEX;
	}
}
