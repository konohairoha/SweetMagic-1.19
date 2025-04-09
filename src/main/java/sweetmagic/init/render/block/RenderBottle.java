package sweetmagic.init.render.block;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sweetmagic.init.tile.sm.TileBottle;
import sweetmagic.util.RenderUtil.RenderInfo;

@OnlyIn(Dist.CLIENT)
public class RenderBottle<T extends TileBottle> extends RenderAbstractTile<T> {

	private float space = 0.125F;

	public RenderBottle(BlockEntityRendererProvider.Context con) {
		super(con);
	}

	public void render(T tile, float parTick, RenderInfo info) {
		int cookData = tile.getCookData();
		if (cookData == 0) { return; }

		PoseStack pose = info.pose();
		pose.pushPose();
		pose.scale(0.5F, 0.5F, 0.5F);
		List<ItemStack> stackList = cookData == 1 ? tile.craftList : tile.resultList;

		int count = 0;
		float pos = 0;
		int gameTime = tile.getClientTime();
		double addZ = stackList.size() % 2 != 0 ? 0 : this.space / 2D;
		pose.translate(1D, Math.sin((gameTime + parTick) / 10.0F) * 0.1D + 0.55D, 1D - addZ);
		float angle = (gameTime + parTick) / 40F * this.pi;
		pose.mulPose(Vector3f.YP.rotationDegrees(angle));

		for (ItemStack stack : stackList) {
			pose.translate(0D, 0D, pos);
			info.itemRenderNo(stack);
			count++;
			pos = count == 1 ? this.space : count % 2 == 0 ? pos - this.space * (count - 1) + count * -this.space : Math.abs(pos) + this.space;
		}

		pose.popPose();
	}
}
