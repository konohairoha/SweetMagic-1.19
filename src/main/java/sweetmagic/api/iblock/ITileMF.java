package sweetmagic.api.iblock;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import sweetmagic.api.SweetMagicAPI;

public interface ITileMF {

	// ブロックえんちちーの取得
	BlockEntity getTile();

	// 座標リストの取得
	Set<BlockPos> getPosList();

	// 座標リストの追加
	void addPosList(BlockPos pos);

	// 座標の取得
	BlockPos getTilePos();

	// worldの取得
	Level getTileWorld();

	// 経過時間の取得
	int getTickTime();

	// 経過時間の設定
	void setTickTime(int tickTime);

	// 受信側かどうかの取得
	boolean getReceive();

	// MFの取得
	int getMF();

	// MFの設定
	void setMF(int mf);

	// 最大MFの取得
	int getMaxMF();

	// MFが空かどうか
	default boolean isMFEmpty() {
		return this.getMF() <= 0;
	}

	// MFが最大かどうか
	default boolean isMaxMF() {
		return this.getMF() >= this.getMaxMF();
	}

	// 受信するMF量の取得
	default int getReceiveMF() {
		return 5000;
	}

	// 消費MF量の取得
	default int getShrinkMF() {
		return 2000;
	}

	// 消費MF以上にMFがあるか
	default boolean hasNeedMF() {
		return this.getMF() >= this.getShrinkMF();
	}

	// MFゲージの描画量を計算するためのメソッド
	default int getMFProgressScaled(int value) {
		return this.isMFEmpty() ? 0 : Math.min(value, (int) (value * (float) (this.getMF()) / (float) (this.getMaxMF())));
	}

	// MFを他のブロックに移し替えるときの処理
	default int outputMF(int mf) {

		// 要求MF量が現在持っているMFよりも多い場合
		if (mf > this.getMF()) {
			mf = this.getMF();
		}

		this.setMF(this.getMF() - mf);
		return mf;
	}

	// アイテムを返す量を計算
	default int outPutItemMF(ItemStack stack) {

		// 残りのMFが入る量の計算
		int mf = this.getMaxMF() - this.getMF();
		int count = 0;

		// 入れる予定のアイテムが残りMFより少なかったらcountにスタック合計MF - 残りMF / アイテム一つのMF
		if ((mf < this.getItemMF(stack.getItem()) * stack.getCount())) {
			count = (this.getItemMF(stack.getItem()) * stack.getCount() - mf) / this.getItemMF(stack.getItem());
		}

		return count;
	}

	// MFを持ったアイテムか
	default boolean isMFAPIItem(ItemStack stack) {
		return SweetMagicAPI.hasMF(stack);
	}

	// アイテムが保持するMF取得
	default int getItemMF(Item item) {
		return SweetMagicAPI.getMF(new ItemStack(item));
	}

	// 送受信処理
	default void sendRecivehandler() {

		Level world = this.getTileWorld();

		for (BlockPos tilePos : this.getPosList()) {

			if (tilePos.getX() == this.getTilePos().getX() && tilePos.getY() == this.getTilePos().getY() && tilePos.getZ() == this.getTilePos().getZ()) { continue; }

			// MF関連のブロックえんちちーでない場合
			BlockEntity tile = world.getBlockEntity(tilePos);
			if (!(tile instanceof ITileMF)) { continue; }

			ITileMF mfBase = (ITileMF) tile;

			// 受け取り側ならMFブロックからMFを入れるときの処理
			if (this.getReceive()) {
				this.insertMF(this , mfBase, this.getTickTime());
			}
		}
	}

	// MFブロックからMFを入れるときの処理
	default void insertMF(ITileMF reci, ITileMF tran, int tickTime) {

		int reciMF = reci.getMF();
		int maxMF = reci.getMaxMF() <= 0 ? 100000 : reci.getMaxMF();
		int tranMF = tran.getMF();
		int receiveMF = Math.min(reci.getReceiveMF(), reci.getMaxMF() - reci.getMF());

		// 最大MFを超えている時、MF最大時のインサート処理
		if (reciMF >= maxMF) {
			this.maxMFInsert(tran);
			return;
		}

		// MFが空なら終了
		else if (tranMF <= 0) {
			return;
		}

		// 送る側のMFが一定値より少ないなら
		if (receiveMF > tranMF && !tran.isCreative()) {
			reci.setMF(reci.getMF() + tranMF);
			reci.sentClient();

			tran.setMF(0);
			tran.sentClient();
		}

		// 一定値より多いなら
		else {

			reci.setMF(reciMF + receiveMF);
			reci.sentClient();

			// クリエ機能がなければ
			if (!tran.isCreative()) {
				tran.setMF(tranMF - receiveMF);
				tran.sentClient();
			}
		}

		// MF受信時のインサート処理
		reci.recipedMFInsert();
	}

	// MF最大時のインサート処理
	default void maxMFInsert(ITileMF tran) {}

	// MF受信時のインサート処理
	default void recipedMFInsert() {}


	// List<BlockPos>をnbt保存
	default CompoundTag savePosList(CompoundTag nbt, Set<BlockPos> posList, String name) {
		if (posList == null || posList.isEmpty()) { return nbt; }

		// nbtのリストを作成
		ListTag tagsList = new ListTag();

		// リストの分だけ回してNBTに保存
		for (BlockPos pos : posList) {

			if (pos == null) { continue; }

			// 座標をXYZごとに保存
			CompoundTag tags = new CompoundTag();
			tags.putInt("X", pos.getX());
			tags.putInt("Y", pos.getY());
			tags.putInt("Z", pos.getZ());

			// nbtリストにnbtを入れる
			tagsList.add(tags);
		}

		// NBTに保存
		nbt.put(name, tagsList);
		return nbt;
	}

	// nbtを呼び出してList<BlockPos>に突っ込む
	default Set<BlockPos> loadAllPos(CompoundTag nbt, String name) {

		// nbtを受け取りnbtリストを作成
		ListTag tagsList = nbt.getList(name, 10);
		Set<BlockPos> list = new HashSet<BlockPos>();

		// nbtリスト分だけ回す
		for (int i = 0; i < tagsList.size(); ++i) {
			CompoundTag tags = tagsList.getCompound(i);
			list.add(new BlockPos(tags.getInt("X"), tags.getInt("Y"), tags.getInt("Z")));
		}

		return list;
	}

	// 距離のチェック
	default boolean checkDistance(BlockPos pos) {
		double dis = 15D;
		double pX = Math.abs(this.getTilePos().getX() - pos.getX());
		double pY = Math.abs(this.getTilePos().getY() - pos.getY());
		double pZ = Math.abs(this.getTilePos().getZ() - pos.getZ());
		return pX <= dis && pY <= dis && pZ <= dis;
	}

	// 燃焼時間を返す
	default int getItemMF(ItemStack stack) {
		return !SweetMagicAPI.hasMF(stack) ? 0 : SweetMagicAPI.getMF(stack) * stack.getCount();
	}

	// クリエイティブ機能
	default boolean isCreative() {
		return false;
	}

	// クライアント側へ送信
	default void sentClient() {
		this.markDirty();
	}

	default void markDirty() {
		Level world = this.getTileWorld();
		if (world == null) { return; }

		BlockPos pos = this.getTilePos();
		BlockState state = world.getBlockState(pos);

		if (world.hasChunkAt(pos)) {
			world.getChunkAt(pos).setUnsaved(true);
		}

		world.sendBlockUpdated(this.getTilePos(), state, state, Block.UPDATE_CLIENTS);
	}
}
