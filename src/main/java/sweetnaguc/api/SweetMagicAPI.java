package sweetmagic.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import sweetmagic.api.magiaflux.IMagiaFluxItemListPlugin;
import sweetmagic.api.magiaflux.MagiaFluxInfo;

public class SweetMagicAPI {

	// 保有MFアイテム情報リスト
	private static Map<Item, MagiaFluxInfo> mfList = new HashMap<>();

	// 保有MFアイテム情報リスト
	private static List<IMagiaFluxItemListPlugin> mfPluginList = new ArrayList<IMagiaFluxItemListPlugin>();

	public static Map<Item, MagiaFluxInfo> getMFMap () {
		return mfList;
	}

	public static List<IMagiaFluxItemListPlugin> getMFPluginList () {
		return mfPluginList;
	}

	public static boolean hasMF(ItemStack stack) {
		return SweetMagicAPI.getMFMap().containsKey(stack.getItem());
	}

	public static boolean hasMF(Item item) {
		return SweetMagicAPI.getMFMap().containsKey(item);
	}

	// 保有MFを取得
	public static int getMF(ItemStack stack) {
		if (!SweetMagicAPI.hasMF(stack)) { return 0; }

		MagiaFluxInfo info = SweetMagicAPI.getMFMap().get(stack.getItem());
		return info != null ? info.getMF() : -1;
	}
}
