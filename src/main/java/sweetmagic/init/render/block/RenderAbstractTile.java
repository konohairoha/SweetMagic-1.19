package sweetmagic.init.render.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import sweetmagic.init.tile.sm.TileAbstractSM;
import sweetmagic.util.RenderUtil.RenderInfo;

public abstract class RenderAbstractTile<T extends TileAbstractSM> implements BlockEntityRenderer<T> {

	private static final float FLUID_OFFSET = 0.005F;
	protected final float pi = 180F / (float) Math.PI;
	protected final Font font;
	protected final ItemRenderer iRender;
	protected final BlockRenderDispatcher bRender;
	protected final EntityRenderDispatcher eRender;
	protected static final int NO_OVERLAY = OverlayTexture.NO_OVERLAY;

	public RenderAbstractTile(BlockEntityRendererProvider.Context con) {
		this.font = con.getFont();
		this.iRender = con.getItemRenderer();
		this.bRender = con.getBlockRenderDispatcher();
		this.eRender = con.getEntityRenderer();
	}

	public int getViewDistance() {
		return 48;
	}

	public void render(T tile, float parTick, PoseStack pose, MultiBufferSource buf, int light, int overlayLight) {
		if (tile.getLevel() == null || tile.isAir()) { return; }
		this.render(tile, parTick, new RenderInfo(this.iRender, light, overlayLight, pose, buf));
	}

	public void render(T tile, float parTick, RenderInfo info) { }

	public void renderLargeFluidCuboid(PoseStack pose, VertexConsumer ver, int light, int xd, float[] xBounds, int zd, float[] zBounds, float yMin, float yMax, TextureAtlasSprite still, int color) {
		if (yMin >= yMax) { return; }

		int yd = (int) (yMax - (int) yMin);

		if (yMax % 1d == 0) {
			yd--;
		}

		float[] yBounds = this.getBlockBounds(yd, yMin, yMax);
		Matrix4f matrix = pose.last().pose();
		Vector3f from = new Vector3f();
		Vector3f to = new Vector3f();
		int rot = false ? 180 : 0;
		for (int y = 0; y <= yd; y++) {
			for (int z = 0; z <= zd; z++) {
				for (int x = 0; x <= xd; x++) {

					from.set(xBounds[x], yBounds[y], zBounds[z]);
					to.set(xBounds[x + 1], yBounds[y + 1], zBounds[z + 1]);

					if (x == 0)
						this.putTexturedQuad(ver, matrix, still, from, to, Direction.WEST, color, light, rot, false);
					if (x == xd)
						this.putTexturedQuad(ver, matrix, still, from, to, Direction.EAST, color, light, rot, false);
					if (z == 0)
						this.putTexturedQuad(ver, matrix, still, from, to, Direction.NORTH, color, light, rot, false);
					if (z == zd)
						this.putTexturedQuad(ver, matrix, still, from, to, Direction.SOUTH, color, light, rot, false);
					if (y == yd)
						this.putTexturedQuad(ver, matrix, still, from, to, Direction.UP, color, light, rot, false);
					if (y == 0) {
						from.setY(from.y() + 0.001F);
						this.putTexturedQuad(ver, matrix, still, from, to, Direction.DOWN, color, light, rot, false);
					}
				}
			}
		}
	}

	public float[] getBlockBounds(int delta) {
		return getBlockBounds(delta, FLUID_OFFSET, delta + 1F - FLUID_OFFSET);
	}

	public float[] getBlockBounds(int delta, float start, float end) {

		float[] bounds = new float[2 + delta];
		bounds[0] = start;
		int offset = (int) start;

		for (int i = 1; i <= delta; i++) {
			bounds[i] = i + offset;
		}

		bounds[delta + 1] = end;
		return bounds;
	}

	public TextureAtlasSprite getBlockSprite(ResourceLocation src) {
		return Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS).getSprite(src);
	}

	public int withBlockLight(int combinedLight, int blockLight) {
		return (combinedLight & 0xFFFF0000) | Math.max(blockLight << 4, combinedLight & 0xFFFF);
	}

	public void putTexturedQuad(VertexConsumer ver, Matrix4f mat, TextureAtlasSprite sprite, Vector3f from, Vector3f to, Direction face, int color, int light, int rot, boolean flowing) {

		float x1 = from.x(), y1 = from.y(), z1 = from.z();
		float x2 = to.x(), y2 = to.y(), z2 = to.z();
		float u1, u2, v1, v2;

		switch (face) {
		default -> { // DOWN
			u1 = x1;
			u2 = x2;
			v1 = z2;
			v2 = z1;
		}
		case UP -> {
			u1 = x1;
			u2 = x2;
			v1 = -z1;
			v2 = -z2;
		}
		case NORTH -> {
			u1 = -x1;
			u2 = -x2;
			v1 = y1;
			v2 = y2;
		}
		case SOUTH -> {
			u1 = x2;
			u2 = x1;
			v1 = y1;
			v2 = y2;
		}
		case WEST -> {
			u1 = z2;
			u2 = z1;
			v1 = y1;
			v2 = y2;
		}
		case EAST -> {
			u1 = -z1;
			u2 = -z2;
			v1 = y1;
			v2 = y2;
		}
		}

		if (rot == 0 || rot == 270) {
			float temp = v1;
			v1 = -v2;
			v2 = -temp;
		}

		if (rot >= 180) {
			float temp = u1;
			u1 = -u2;
			u2 = -temp;
		}

		boolean reverse = u1 > u2;
		u1 = this.boundUV(u1, reverse);
		u2 = this.boundUV(u2, !reverse);
		reverse = v1 > v2;
		v1 = this.boundUV(v1, reverse);
		v2 = this.boundUV(v2, !reverse);
		float minU, maxU, minV, maxV;
		double size = flowing ? 8 : 16;

		if ((rot % 180) == 90) {
			minU = sprite.getU(v1 * size);
			maxU = sprite.getU(v2 * size);
			minV = sprite.getV(u1 * size);
			maxV = sprite.getV(u2 * size);
		}

		else {
			minU = sprite.getU(u1 * size);
			maxU = sprite.getU(u2 * size);
			minV = sprite.getV(v1 * size);
			maxV = sprite.getV(v2 * size);
		}

		float u3, u4, v3, v4;
		switch (rot) {
		default -> {
			u1 = minU;
			v1 = maxV;
			u2 = minU;
			v2 = minV;
			u3 = maxU;
			v3 = minV;
			u4 = maxU;
			v4 = maxV;
		}
		case 90 -> {
			u1 = minU;
			v1 = minV;
			u2 = maxU;
			v2 = minV;
			u3 = maxU;
			v3 = maxV;
			u4 = minU;
			v4 = maxV;
		}
		case 180 -> {
			u1 = maxU;
			v1 = minV;
			u2 = maxU;
			v2 = maxV;
			u3 = minU;
			v3 = maxV;
			u4 = minU;
			v4 = minV;
		}
		case 270 -> {
			u1 = maxU;
			v1 = maxV;
			u2 = minU;
			v2 = maxV;
			u3 = minU;
			v3 = minV;
			u4 = maxU;
			v4 = minV;
		}
		}

		int light1 = light & 0xFFFF;
		int light2 = light >> 0x10 & 0xFFFF;
		int a = color >> 24 & 0xFF;
		int r = color >> 16 & 0xFF;
		int g = color >> 8 & 0xFF;
		int b = color & 0xFF;

		switch (face) {
		case DOWN -> {
			ver.vertex(mat, x1, y1, z2).color(r, g, b, a).uv(u1, v1).uv2(light1, light2).endVertex();
			ver.vertex(mat, x1, y1, z1).color(r, g, b, a).uv(u2, v2).uv2(light1, light2).endVertex();
			ver.vertex(mat, x2, y1, z1).color(r, g, b, a).uv(u3, v3).uv2(light1, light2).endVertex();
			ver.vertex(mat, x2, y1, z2).color(r, g, b, a).uv(u4, v4).uv2(light1, light2).endVertex();
		}
		case UP -> {
			ver.vertex(mat, x1, y2, z1).color(r, g, b, a).uv(u1, v1).uv2(light1, light2).endVertex();
			ver.vertex(mat, x1, y2, z2).color(r, g, b, a).uv(u2, v2).uv2(light1, light2).endVertex();
			ver.vertex(mat, x2, y2, z2).color(r, g, b, a).uv(u3, v3).uv2(light1, light2).endVertex();
			ver.vertex(mat, x2, y2, z1).color(r, g, b, a).uv(u4, v4).uv2(light1, light2).endVertex();
		}
		case NORTH -> {
			ver.vertex(mat, x1, y1, z1).color(r, g, b, a).uv(u1, v1).uv2(light1, light2).endVertex();
			ver.vertex(mat, x1, y2, z1).color(r, g, b, a).uv(u2, v2).uv2(light1, light2).endVertex();
			ver.vertex(mat, x2, y2, z1).color(r, g, b, a).uv(u3, v3).uv2(light1, light2).endVertex();
			ver.vertex(mat, x2, y1, z1).color(r, g, b, a).uv(u4, v4).uv2(light1, light2).endVertex();
		}
		case SOUTH -> {
			ver.vertex(mat, x2, y1, z2).color(r, g, b, a).uv(u1, v1).uv2(light1, light2).endVertex();
			ver.vertex(mat, x2, y2, z2).color(r, g, b, a).uv(u2, v2).uv2(light1, light2).endVertex();
			ver.vertex(mat, x1, y2, z2).color(r, g, b, a).uv(u3, v3).uv2(light1, light2).endVertex();
			ver.vertex(mat, x1, y1, z2).color(r, g, b, a).uv(u4, v4).uv2(light1, light2).endVertex();
		}
		case WEST -> {
			ver.vertex(mat, x1, y1, z2).color(r, g, b, a).uv(u1, v1).uv2(light1, light2).endVertex();
			ver.vertex(mat, x1, y2, z2).color(r, g, b, a).uv(u2, v2).uv2(light1, light2).endVertex();
			ver.vertex(mat, x1, y2, z1).color(r, g, b, a).uv(u3, v3).uv2(light1, light2).endVertex();
			ver.vertex(mat, x1, y1, z1).color(r, g, b, a).uv(u4, v4).uv2(light1, light2).endVertex();
		}
		case EAST -> {
			ver.vertex(mat, x2, y1, z1).color(r, g, b, a).uv(u1, v1).uv2(light1, light2).endVertex();
			ver.vertex(mat, x2, y2, z1).color(r, g, b, a).uv(u2, v2).uv2(light1, light2).endVertex();
			ver.vertex(mat, x2, y2, z2).color(r, g, b, a).uv(u3, v3).uv2(light1, light2).endVertex();
			ver.vertex(mat, x2, y1, z2).color(r, g, b, a).uv(u4, v4).uv2(light1, light2).endVertex();
		}
		}
	}

	public float boundUV(float value, boolean upper) {
		value = value % 1;
		if (value == 0) { return upper ? 1 : 0; }

		return value < 0 ? (value + 1) : value;
	}
}
