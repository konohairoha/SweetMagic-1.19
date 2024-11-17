package sweetmagic.init.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import sweetmagic.init.tile.sm.TileAetherPlanter;

public class RenderAetherPlanter extends RenderAbstractTile<TileAetherPlanter> {

	public RenderAetherPlanter(BlockEntityRendererProvider.Context con) {
		super(con);
	}

	@Override
	public void render(TileAetherPlanter tile, float parTick, PoseStack pose, MultiBufferSource buf, int light, int overlayLight) {
		if (tile.getLevel() == null || tile.isAir()) { return; }

		ItemStack stack = tile.stack;
		if (stack.isEmpty()) { return; }

		pose.pushPose();
		pose.translate(0.5D, 0.5D, 0.5D);
		pose.mulPose(Vector3f.YP.rotationDegrees(tile.getRot()));
		pose.translate(0D, 0.225D, -0.505D);
		pose.scale(0.5F, 0.5F, 0.5F);
		this.iRender.renderStatic(stack, ItemTransforms.TransformType.FIXED, light, overlayLight, pose, buf, 0);
		pose.popPose();
	}
}
