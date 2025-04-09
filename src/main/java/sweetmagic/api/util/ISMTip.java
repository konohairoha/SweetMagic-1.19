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

	// 複数の文字列を連結して1つの文字列にする
	default MutableComponent getTipArray(Object... objArray) {
		MutableComponent com = null;
		for (Object obj : objArray) {

			if (obj instanceof Component) {

				if (com == null) {
					com = (MutableComponent) obj;
				}

				else {
					com.append((Component) obj);
				}
			}

			// Stringならそのまま設定
			else if (obj instanceof String) {
				MutableComponent tip = this.getLabel((String) obj);

				if (com == null) {
					com = tip;
				}

				else {
					com.append(tip);
				}
			}

			// 色を設定するなら
			else if (obj instanceof ChatFormatting) {
				if (com != null) {
					com.withStyle((ChatFormatting) obj);
				}
			}
		}

		return com;
	}

	// Stringをそのまま表示
	default MutableComponent getLabel(String tip) {
		return Component.literal(tip);
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
	default MutableComponent getText(String tip, String... text) {
		Object[] array = text;
		return Component.translatable("tip.sweetmagic." + tip, array);
	}

	// Stringをスイマジの翻訳用文字列に変換
	default MutableComponent getEffectText(String tip) {
		return this.getTip("effect.sweetmagic." + tip);
	}

	// Stringをスイマジの翻訳用文字列に変換
	default MutableComponent getEntityText(String tip) {
		return this.getTip("entity.sweetmagic." + tip);
	}

	// Stringをマイクラの翻訳用文字列に変換
	default MutableComponent getMCText(String tip) {
		return this.getTip("effect.minecraft." + tip);
	}

	// Stringをスイマジの翻訳用文字列に変換
	default MutableComponent getEnchaText(int level) {
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
		return this.getTipArray(this.getText("tier"), ": ", this.getTip("" + tier).withStyle(WHITE), GREEN);
	}
}
