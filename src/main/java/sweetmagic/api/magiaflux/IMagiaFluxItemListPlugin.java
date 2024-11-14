package sweetmagic.api.magiaflux;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public interface IMagiaFluxItemListPlugin {

	void setMF(MagiaFluxInfo info);		// 保有MFを設定
	void addPluginList ();				// プラグインリストへの追加

	default void setMF (MagiaFluxInfo info, ItemLike item, int mf) {
		info.setMF(new MagiaFluxInfo(new ItemStack(item), mf));
	}
}
