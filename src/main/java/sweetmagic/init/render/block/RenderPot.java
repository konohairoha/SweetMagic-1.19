package sweetmagic.init.render.block;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import sweetmagic.init.tile.sm.TilePot;
import sweetmagic.util.RenderUtil.RenderInfo;

public class RenderPot<T extends TilePot> extends RenderAbstractTile<T> {

	private static final float SPACE = 0.175F;

	public RenderPot(BlockEntityRendererProvider.Context con) {
		super(con);
	}

	public void render(T tile, float parTick, RenderInfo info) {
		if (tile.getCookData() != 2) { return; }

		PoseStack pose = info.pose();
		pose.pushPose();
		pose.scale(0.5F, 0.5F, 0.5F);
		List<ItemStack> stackList = tile.resultList;

		int count = 0;
		float pos = 0;
		double addZ = stackList.size() % 2 != 0 ? 0 : SPACE / 2D;
		pose.translate(1D, 0.5D, 1D - addZ);
		pose.mulPose(Vector3f.YP.rotationDegrees(tile.getRot()));

		for (ItemStack stack : stackList) {
			pose.translate(0D, 0D, pos);
			info.itemRenderNo(stack);
			count++;
			pos = count == 1 ? SPACE : count % 2 == 0 ? pos - SPACE * (count - 1) + count * -SPACE : Math.abs(pos) + SPACE;
		}

		pose.popPose();
	}
}
