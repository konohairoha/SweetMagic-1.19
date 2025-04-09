package sweetmagic.api.magiaflux;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import sweetmagic.api.SweetMagicAPI;

public class MagiaFluxInfo {

	public ItemStack mItem;
	public int magiaflux;

	// 初期化用
	public MagiaFluxInfo() {
		this.mItem = null;
		this.magiaflux = 0;
	}

	// 削除用
	public MagiaFluxInfo(ItemStack item) {
		this.mItem = item;
		this.magiaflux = 0;
	}

	// 追加用
	public MagiaFluxInfo(ItemStack item, int mf) {
		this.mItem = item;
		this.magiaflux = mf;
	}

	public ItemStack getItem() {
		return this.mItem;
	}

	public int getMF() {
		return this.magiaflux;
	}

	// アイテムにMFを定義
	public void setMF(MagiaFluxInfo info) {
		ItemStack stack = info.getItem();
		Item item = stack.getItem();
		if(stack.isEmpty()) { return; }

		// すでにそのアイテムが定義されてたら異常
		if (SweetMagicAPI.getMFMap().containsKey(item)) {
			throw new IllegalArgumentException("The item has already been defined.item:" + info.getItem() + ", mf:" + info.getMF());
		}

		// 対象のMFが1を下回ったら異常
		if(info.getMF() < 1) {
			throw new IllegalArgumentException("The target Magia Flux is an invalid value.item:" + info.getItem() + ", mf:" + info.getMF());
		}

		// チェック終了後、リストに入れる
		SweetMagicAPI.getMFMap().put(item, info);
	}
}
