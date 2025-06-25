package sweetmagic.api.event;

import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;

import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public class SMUtilEvent {

	public final static ChatFormatting RED = ChatFormatting.RED;
	public final static ChatFormatting BLUE = ChatFormatting.BLUE;
	public final static ChatFormatting GREEN = ChatFormatting.GREEN;
	public final static ChatFormatting GOLD = ChatFormatting.GOLD;
	public final static ChatFormatting WHITE = ChatFormatting.WHITE;
	public final static ChatFormatting GRAY = ChatFormatting.GRAY;

	// 複数の文字列を連結して1つの文字列にする
	public static MutableComponent getTipArray(Object... objArray) {
		MutableComponent com = null;
		for (Object obj : objArray) {

			if (obj instanceof Component comp) {
				com = com == null ? (MutableComponent) obj : com.append(comp);
			}

			// Stringならそのまま設定
			else if (obj instanceof String str) {
				MutableComponent tip = getLabel(str);
				com = com == null ? tip : com.append(tip);
			}

			// 色を設定するなら
			else if (obj instanceof ChatFormatting chat && com != null) {
				com.withStyle(chat);
			}

			else {
				MutableComponent tip = getLabel(obj);
				com = com == null ? tip : com.append(tip);
			}
		}

		return com;
	}

	// シフト押したときのツールチップ
	public static void getShiftTip(List<Component> tooltip) {
		tooltip.add(getText("shift").withStyle(RED));
	}

	public static MutableComponent enumString(String name) {
		return getText(name.toLowerCase());
	}

	// 翻訳に変換
	public static MutableComponent getTip(String tip) {
		return Component.translatable(tip);
	}

	// ツールチップに変換
	public static MutableComponent getText(String name) {
		return getTip("tip.sweetmagic." + name);
	}

	// ツールチップに変換
	public static MutableComponent getText(String name, String text) {
		return Component.translatable("tip.sweetmagic." + name, text);
	}

	public static MutableComponent empty() {
		return getLabel(" ");
	}

	public static MutableComponent getLabel(Object tip, ChatFormatting color) {
		return getLabel(tip).withStyle(color);
	}

	public static MutableComponent getLabel(Object tip) {
		return Component.literal("" + tip);
	}

	// Stringをスイマジの翻訳用文字列に変換
	public static MutableComponent getEnchaTip(int level) {
		return getTip("enchantment.level." + level);
	}

	// レンダーの開始
	public static void renderStart(ResourceLocation tex) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, tex);
		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
	}

	public static void drawTextured(Matrix4f mat, int x, int y, int texX, int texY, int wid, int hei) {
		drawTextured(mat, x, y, texX, texY, wid, hei, 256F);
	}

	public static void drawTextured(Matrix4f mat, int x, int y, int texX, int texY, int wid, int hei, float size) {

		Tesselator tes = Tesselator.getInstance();
		BufferBuilder buf = tes.getBuilder();
		buf.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

		float f = 1F / size;
		float f1 = 1F / size;
		buf.vertex(mat, x + 0, y + hei, -90F).uv((texX + 0) * f, (texY + hei) * f1).endVertex();
		buf.vertex(mat, x + wid, y + hei, -90F).uv((texX + wid) * f, (texY + hei) * f1).endVertex();
		buf.vertex(mat, x + wid, y + 0, -90F).uv((texX + wid) * f, (texY + 0) * f1).endVertex();
		buf.vertex(mat, x + 0, y + 0, -90F).uv((texX + 0) * f, (texY + 0) * f1).endVertex();
		tes.end();
	}
}
