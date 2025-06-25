package sweetmagic.api.util;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public interface ISMTip {

	public final static ChatFormatting RED = ChatFormatting.RED;
	public final static ChatFormatting GREEN = ChatFormatting.GREEN;
	public final static ChatFormatting GOLD = ChatFormatting.GOLD;
	public final static ChatFormatting WHITE = ChatFormatting.WHITE;
	public final static ChatFormatting BLACK = ChatFormatting.BLACK;

	// 複数の文字列を連結して1つの文字列にする
	default MutableComponent getTipArray(Object... objArray) {
		MutableComponent com = null;
		for (Object obj : objArray) {

			if (obj instanceof Component co) {
				com = com == null ? (MutableComponent) obj : com.append(co);
			}

			// Stringならそのまま設定
			else if (obj instanceof String st) {
				MutableComponent tip = this.getLabel(st);
				com = com == null ? tip : com.append(tip);
			}

			// 色を設定するなら
			else if (obj instanceof ChatFormatting chat && com != null) {
				com.withStyle(chat);
			}

			else {
				MutableComponent tip = this.getLabel("" + obj);
				com = com == null ? tip : com.append(tip);
			}
		}

		return com;
	}

	// Stringをそのまま表示
	default MutableComponent getLabel(Object tip, ChatFormatting color) {
		return this.getLabel(tip).withStyle(color);
	}

	// Stringをそのまま表示
	default MutableComponent getLabel(Object tip) {
		return Component.literal("" + tip);
	}

	// Stringを翻訳して変換
	default MutableComponent getTip(String tip) {
		return Component.translatable(tip);
	}

	// Stringをスイマジの翻訳用文字列に変換
	default MutableComponent getText(String tip) {
		return this.getTip("tip.sweetmagic." + tip);
	}

	// Stringをスイマジの翻訳用文字列に変換
	default MutableComponent getText(String tip, Object... text) {
		return Component.translatable("tip.sweetmagic." + tip, text);
	}

	// Stringをスイマジの翻訳用文字列に変換
	default MutableComponent getEffectTip(String tip) {
		return this.getTip("effect.sweetmagic." + tip);
	}

	// Stringをスイマジの翻訳用文字列に変換
	default MutableComponent getEntityTip(String tip) {
		return this.getTip("entity.sweetmagic." + tip);
	}

	// Stringをマイクラの翻訳用文字列に変換
	default MutableComponent getMCTip(String tip) {
		return this.getTip("effect.minecraft." + tip);
	}

	// Stringをスイマジの翻訳用文字列に変換
	default MutableComponent getEnchaTip(int level) {
		return this.getTip("enchantment.level." + level);
	}

	// シフト押したときのツールチップ
	default void getShiftTip(List<Component> tooltip) {
		tooltip.add(this.getText("shift").withStyle(RED));
	}

	default MutableComponent empty() {
		return Component.literal(" ");
	}

	// tierのチップ取得
	default MutableComponent tierTip(int tier) {
		return this.getTipArray(this.getText("tier"), ": ", this.getLabel(tier, WHITE), GREEN);
	}

	default String format(int num) {
		return String.format("%,d", num);
	}

	default String format(float num) {
		return String.format("%.1f", num);
	}

	default MutableComponent formatPar(float num) {
		return this.getLabel(String.format("%.1f%%", num));
	}

	default MutableComponent formatPar(float num, ChatFormatting color) {
		return this.getLabel(String.format("%.1f%%", num), color);
	}
}
