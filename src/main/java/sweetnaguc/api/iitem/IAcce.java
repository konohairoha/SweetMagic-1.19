package sweetmagic.api.iitem;

import java.util.List;
import java.util.function.Predicate;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import sweetmagic.api.emagic.SMAcceType;
import sweetmagic.api.emagic.SMDropType;
import sweetmagic.api.iitem.info.AcceInfo;
import sweetmagic.api.iitem.info.PorchInfo;
import sweetmagic.api.util.ISMTip;
import sweetmagic.util.PlayerHelper;

public interface IAcce extends ISMTip {

	public static final String STACKCOUNT = "stackCount";			// 選択中のスロット

	// 効果が発動できるか
	default boolean canUseEffect (Level world, Player player, AcceInfo info) {
		return !player.getCooldowns().isOnCooldown(info.getItem());
	}

	// 常に発動したいならここで
	default void onUpdate (Level world, Player player, AcceInfo info) { }

	// 重複系で常に発動したいならここで
	default void onMultiUpdate (Level world, Player player, AcceInfo info, PorchInfo pInfo) { }

	// ツールチップ
	void magicToolTip (List<MutableComponent> toolTip, ItemStack stack);

	default void debuffRemovalTip(List<MutableComponent> toolTip) { }

	default void dropMobTip(List<MutableComponent> toolTip) { }

	default MutableComponent getHowGetTip() {
		return this.getText(this.getDropType().getName());
	}

	// アクセサリタイプの取得
	SMAcceType getAcceType ();

	// アクセサリタイプの設定
	void setAcceType(SMAcceType type);

	// アクセサリタイプの取得
	SMDropType getDropType ();

	// アクセサリタイプの設定
	void setDropType(SMDropType type);

	int getTier();

	// 常時発動できるかどうか
	default boolean isUpdateType(Level world, Player player, AcceInfo info) {
		return this.getAcceType().is(SMAcceType.UPDATE);
	}

	// ポーション付与
	default void addPotion (LivingEntity target, MobEffect effect, int level, int time) {
		PlayerHelper.setPotion(target, effect, level, time);
	}

	// ポーション付与
	default void addPotion (LivingEntity target, MobEffect effect, int level, int time, boolean flag) {
		PlayerHelper.setPotion(target, effect, level, time, flag);
	}

	default <T extends Entity> List<T> getEntityList(Class<T> enClass, Entity entity, double range) {
		return entity.level.getEntitiesOfClass(enClass, this.getAABB(entity, range));
	}

	default <T extends Entity> List<T> getEntityList(Class<T> enClass, Entity entity, Predicate<T> filter, double range) {
		return entity.level.getEntitiesOfClass(enClass, this.getAABB(entity, range)).stream().filter(filter).toList();
	}

	// 範囲の取得
	default AABB getAABB (Entity entity, double range) {
		return entity.getBoundingBox().inflate(range, range, range);
	}

	// 範囲の取得
	default AABB getAABB (Entity entity, double x, double  y, double  z) {
		return entity.getBoundingBox().inflate(x, y, z);
	}

	default void playSound(Level world, LivingEntity entity, SoundEvent sound, float vol, float pitch) {
		world.playSound(null, entity.blockPosition(), sound, SoundSource.PLAYERS, vol, pitch);
	}

	// 重複できるか
	default boolean isDuplication () {
		return false;
	}

	// onOff切り替えするか
	default boolean isSwitch () {
		return false;
	}

	// スタック数の取得
	default int getStackCount (AcceInfo info) {
		return !this.isDuplication() ? 1 : info.getNBT().getInt(STACKCOUNT);
	}

	// 最大スタック数の取得
	default int getMaxStackCount () {
		return 1;
	}

	// スタック数の増加が可能か
	default boolean canAddStackCount (AcceInfo info) {
		return this.getStackCount(info) < this.getMaxStackCount();
	}

	// スタック数の増加
	default void addStackCount (AcceInfo info) {
		if (!this.canAddStackCount(info)) { return; }

		CompoundTag tags = this.getNBT(info.getStack());
		tags.putInt(STACKCOUNT, tags.getInt(STACKCOUNT) + 1);
	}

	// nbt初期化用
	default CompoundTag getNBT (ItemStack stack) {

		CompoundTag tags = stack.getTag();

		// NBTがnullなら初期化
		if (tags == null) {
			tags = new CompoundTag();
			tags.putInt(STACKCOUNT, 1);		// スタック数の初期化
			stack.setTag(tags);
		}

		if (!tags.contains(STACKCOUNT)) {
			tags.putInt(STACKCOUNT, 1);
		}

		return tags;
	}

	// 杖の取得
	public static IAcce getAcce (ItemStack stack) {
		return (IAcce) stack.getItem();
	}
}
