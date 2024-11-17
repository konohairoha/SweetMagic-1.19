package sweetmagic.init.render.block;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sweetmagic.init.tile.sm.TileFrypan;

@OnlyIn(Dist.CLIENT)
public class RenderFrypan extends RenderAbstractTile<TileFrypan> {

	private float space = 0.175F;

	public RenderFrypan(BlockEntityRendererProvider.Context con) {
		super(con);
	}

	@Override
	public void render(TileFrypan tile, float parTick, PoseStack pose, MultiBufferSource buf, int light, int overlayLight) {
		if (tile.getLevel() == null || tile.isAir()) { return; }

		// 未料理徐状態なら終了
		int cookData = tile.getCookData();
		if (cookData == 0) { return; }

		pose.pushPose();
		pose.scale(0.5F, 0.5F, 0.5F);
		List<ItemStack> stackList = cookData == 1 ? tile.craftList : tile.resultList;

        int count = 0;
        float pos = 0;
		double addZ = stackList.size() % 2 != 0 ? 0 : space / 2D;
		pose.translate(1D, 0.5D, 1D);
		pose.mulPose(Vector3f.YP.rotationDegrees(tile.getRot()));
		pose.translate(0D, 0D, 0.25D -addZ);

        for (ItemStack stack : stackList) {
    		pose.translate(0D, 0D, pos);
			this.iRender.renderStatic(stack, ItemTransforms.TransformType.FIXED, light, OverlayTexture.NO_OVERLAY, pose, buf, 0);
			count++;
			pos = count == 1 ? space : count % 2 == 0 ? pos - space * (count - 1) + count * -space : Math.abs(pos) + space;
        }

		pose.popPose();
	}
}
