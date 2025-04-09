package sweetmagic.init.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;
import sweetmagic.SweetMagicCore;
import sweetmagic.handler.PacketHandler;
import sweetmagic.init.CapabilityInit;
import sweetmagic.init.SoundInit;
import sweetmagic.packet.CookStatusPKT;

public interface ICookingStatus extends INBTSerializable<CompoundTag> {

	// NBT用の変数
	public static final String EXP = "exp";				// 経験値
	public static final String LEVEL = "level";			// レベル
	public static final String HEALTH = "health";		// レベル
	public ResourceLocation ID = SweetMagicCore.getSRC("cap_shield");

	// 経験値付与
	default void addExp(int addExp) {
		this.levelUpCheck(this.getEntity().level, Math.max(1, addExp));
	}

	// レベルアップできるかどうか
	default void levelUpCheck(Level world, int addExp) {
		int level = this.getLevel();		// レベル
		int maxLevel = this.getMaxLevel();	// 最大レベル
		if (level >= maxLevel) { return; }

		int exp = this.getExpValue();	// 経験値
		int nextLevel = 1 + level;		// 次のレベル
		int keepExp = 0;				// レベルアップ後に持ち越し用の経験値と必要経験値の取得
		int nowNeedExp = this.needExp(nextLevel);

		// 要求経験値を超えた場合に次へ持ち越し
		if (addExp > nowNeedExp) {
			keepExp = addExp - nowNeedExp;
			this.setExpValue(exp + nowNeedExp);
		}

		else {
			this.setExpValue(exp + addExp);
		}

		// レベルアップに必要な経験値を満たしていないなら終了
		int needExp = this.needExp(nextLevel);
		if (needExp > 0) { return; }

		int upLevel = ++level;
		this.setLevel(upLevel);
		this.setExpValue(needExp);

		if (!world.isClientSide) {
			this.playSound(world, SoundInit.LEVELUP, 0.0625F, 1F);
		}

		// 余った分を再度レベルアップに回す
		if (keepExp > 0) {
			this.levelUpCheck(world, keepExp);
		}
	}

	default void playSound(Level world, SoundEvent sound, float vol, float pitch) {
		LivingEntity entity = this.getEntity();
		entity.getCommandSenderWorld().playSound(null, entity.blockPosition(), sound, SoundSource.PLAYERS, vol, pitch);
	}

	// 最大レベルの取得
	default int getMaxLevel() {
		return 20;
	}

	// 必要経験値を取得
	default int needExp(int nextLevel) {

		// 必要経験値量 - 取得済みの経験値
		int needExp = this.getNeedExp(nextLevel) - this.getExpValue();
		return (nextLevel - 1) >= this.getMaxLevel() ? 0 : needExp;
	}

	// 必要経験値を取得
	default int getNeedExp(int nextLevel) {
		int level = nextLevel - 1;				// 今のレベルを取得
		int baseExp = 100 * level;				// 基礎経験値
		float rateExp = 1 + (level - 1) * 0.1F;	// 経験値レート
		return (int) (baseExp * rateExp);
	}

	default CompoundTag writeNBT() {
		CompoundTag tags = new CompoundTag();
		tags.putInt(EXP, this.getExpValue());
		tags.putInt(LEVEL, this.getLevel());
		tags.putFloat(HEALTH, this.getHealth());
		return tags;
	}

	default void readNBT(CompoundTag tags) {
		this.setExpValue(tags.getInt(EXP));
		this.setLevel(Math.max(1, tags.getInt(LEVEL)));
		this.setHealth(tags.getFloat(HEALTH));
	}

	void setEntity(LivingEntity entity);

	LivingEntity getEntity();

	// 経験値の設定
	void setExpValue(int exp);

	// 経験値の取得
	int getExpValue();

	// レベルの取得
	void setLevel(int level);

	// レベルの取得
	int getLevel();

	void setHealth(float health);

	float getHealth();

	public static boolean hasValue(LivingEntity entity) {
		return entity.getCapability(CapabilityInit.COOK).resolve().isPresent();
	}

	public static ICookingStatus getState(LivingEntity entity) {
		return entity.getCapability(CapabilityInit.COOK).resolve().get();
	}

	public static void sendPKT(Player player) {
		player.getCapability(CapabilityInit.COOK).ifPresent(o -> {
			if (player instanceof ServerPlayer ser) {
				PacketHandler.sendTo(new CookStatusPKT(o.serializeNBT()), ser);
			}
		});
	}
}
