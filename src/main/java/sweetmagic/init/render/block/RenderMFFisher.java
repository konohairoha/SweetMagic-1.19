package sweetmagic.init.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import sweetmagic.init.BlockInit;
import sweetmagic.init.tile.sm.TileMFFisher;
import sweetmagic.util.RenderUtil;
import sweetmagic.util.RenderUtil.RenderColor;
import sweetmagic.util.RenderUtil.RenderInfo;

public class RenderMFFisher<T extends TileMFFisher> extends RenderAbstractTile<T> {

	private static final ItemStack SQUARE = new ItemStack(BlockInit.magic_square);
	private static final Block SQUARE_BLOCK = BlockInit.magic_square_s;

	public RenderMFFisher(BlockEntityRendererProvider.Context con) {
		super(con);
	}

	public void render(T tile, float parTick, RenderInfo info) {
		int data = tile.getData();
		this.renderItem(tile, parTick, info, data);

		if (tile.isMFEmpty()) { return; }
		this.renderSquare(tile, parTick, info, data);
	}

	public void renderItem(T tile, float parTick, RenderInfo info, int data) {

		double addY = 0D;
		float scale = 0.5F;
		double shake = 0.1D;

		if (data == 3 || data == 4) {
			addY += 0.5D;
			scale = 0.375F;
			shake = 0D;
		}

		PoseStack pose = info.pose();
		pose.pushPose();
		pose.translate(0.5D, 0.5D + addY, 0.5D);
		int gameTime = tile.getClientTime();
		pose.translate(0D, Math.sin((gameTime + parTick) / 10F) * shake, 0D);
		pose.scale(scale, scale, scale);
		float angle = (gameTime + parTick) / 20F * this.pi;
		pose.mulPose(Vector3f.YP.rotationDegrees(angle));
		ItemStack stack = ItemStack.EMPTY;

		switch(data) {
		case 0:
			stack = TileMFFisher.FISHING_ROD;
			break;
		case 1:
			stack = TileMFFisher.MACHETE;
			break;
		case 2:
			stack = TileMFFisher.MILK_PACK;
			break;
		case 3:
			stack = TileMFFisher.AETHER_CRYSTAL;
			break;
		case 4:
			stack = TileMFFisher.DIVINE_CRYSTAL;
			break;
		case 5:
			stack = TileMFFisher.ALT_PICK;
			break;
		case 6:
			stack = TileMFFisher.EGG_BAG;
			break;
		}

		info.itemRender(stack);
		pose.popPose();
	}

	public void renderSquare(T tile, float parTick, RenderInfo info, int data) {

		PoseStack pose = info.pose();
		pose.pushPose();
		boolean isFurnace = data == 3 || data == 4;
		double addY = isFurnace ? -0.6D : 0D;

		pose.translate(0.5D, 1.65D + addY, 0.5D);
		int gameTime = tile.getClientTime();
		pose.translate(0D, Math.sin((gameTime + parTick) / 15F) * 0.02D, 0D);
		pose.scale(2F, 2F, 2F);
		float angle = (gameTime + parTick) / 20F * this.pi;
		pose.mulPose(Vector3f.YP.rotationDegrees(angle));

		if (isFurnace) {
			pose.translate(-0.25D, -0.25D, -0.25D);
			pose.scale(0.5F, 0.5F, 0.5F);
			int light = info.light();
			int overlayLight = info.overlay();
			RenderColor color = data == 3 ? new RenderColor(76F / 255F, 165F / 255F, 1F, light, overlayLight) : new RenderColor(102F / 255F, 206F / 255F, 1F, light, overlayLight);
			RenderUtil.renderBlock(info, color, SQUARE_BLOCK);
		}

		else {
			info.itemRender(SQUARE);
		}

		pose.popPose();
	}

	public int getViewDistance() {
		return 32;
	}
}
