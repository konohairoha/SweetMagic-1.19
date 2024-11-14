package sweetmagic.api.iitem;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import sweetmagic.api.SweetMagicAPI;
import sweetmagic.api.iblock.ITileMF;

public interface IMFTool {

	// NBT用の変数
	public final String MF = "mf";

	// 最大MFを取得
	int getMaxMF (ItemStack stack);

	// 最大MFを設定
	void setMaxMF (int maxMF);

	// NBT初期化用
	default CompoundTag getNBT (ItemStack stack) {

		CompoundTag tags = stack.getTag();

		// MFを持っていないなら初期化
		if (tags == null) {
			tags = new CompoundTag();
			tags.putInt(MF, 0);
			stack.setTag(tags);
		}

		return tags;
	}

  	// MFを取得
	default int getMF (ItemStack stack) {
    	return this.getNBT(stack).getInt(MF);
  	}

  	// MFを設定
	default void setMF (ItemStack stack, int expValue) {
  		this.getNBT(stack).putInt(MF, Math.max(0, expValue));
  	}

	// MFが最大かどうか
	default boolean isMaxMF (ItemStack stack) {
		return this.getMF(stack) >= this.getMaxMF(stack);
	}

	// ゲージ計算取得用
	default int getMFProgressScaled(ItemStack stack, int value) {
		return Math.min(value, (int) (value * this.getMF(stack) / this.getMaxMF(stack)));
	}

	// MFブロックからMFを入れるときの処理
	default void insertMF (ItemStack stack, ITileMF tile) {

		int mf = this.getMF(stack);
		int useMF = tile.getShrinkMF() > tile.getMF() ? tile.getMF() : tile.getShrinkMF();
		int sumMF = mf + useMF;

		// 合計MFが最大値より少ない場合
		if (sumMF <= this.getMaxMF(stack)) {
			this.setMF(stack, sumMF);
			tile.setMF(tile.getMF() - useMF);
		}

		// 合計MFが最大値を超える場合
		else {

			int insertMF = this.getMaxMF(stack) - mf;
			this.setMF(stack, mf + insertMF);
			tile.setMF(tile.getMF() - insertMF);
		}

		tile.sentClient();
	}

	default int insetMF (ItemStack stack, int insertMF) {

		int mf = this.getMF(stack);
		int sumMF = insertMF + mf;

		// 合計MFが最大値より少ない場合
		if (sumMF <= this.getMaxMF(stack)) {
			this.setMF(stack, sumMF);
			return insertMF;
		}

		// 合計MFが最大値を超える場合
		else {
			int setMF = this.getMaxMF(stack) - mf;
			this.setMF(stack, mf + setMF);
			return setMF;
		}
	}

	// MFが空かどうか
	default boolean isMFEmpty (ItemStack stack) {
		return this.getMF(stack) <= 0;
	}

	default void playSound (Level world, Player player, SoundEvent sound, float vol, float pitch) {
		player.getCommandSenderWorld().playSound(null, player.blockPosition(), sound, SoundSource.PLAYERS, vol, pitch);
	}

	// 吸い込むアイテムのMF量
	default int getItemMF (ItemStack stack) {
		return SweetMagicAPI.getMF(stack) * stack.getCount();
	}

	// エンチャントレベル取得
	default int getEnchaLevel (ItemStack stacck, Enchantment encha) {
		return EnchantmentHelper.getItemEnchantmentLevel(encha, stacck);
	}

    // APIアイテムかどうかの判定
	default boolean hasMF (ItemStack stack) {
    	return SweetMagicAPI.hasMF(stack);
    }
}
