package sweetmagic.init.capability.icap;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import sweetmagic.SweetMagicCore;
import sweetmagic.handler.PacketHandler;
import sweetmagic.init.AdvancedInit;
import sweetmagic.init.CapabilityInit;
import sweetmagic.init.SoundInit;
import sweetmagic.init.tile.sm.TileNotePC.TradeInfo;
import sweetmagic.packet.CookStatusPKT;

public interface ICookingStatus extends INBTSerializable<CompoundTag> {

	// NBT用の変数
	public static final String EXP = "exp";			// 経験値
	public static final String LEVEL = "level";		// レベル
	public static final String HEALTH = "health";	// 体力
	public static final String TR_LEVEL = "trade_level";	// 購買レベル
	public static final String TR_EXP = "trade_exp";		// 購買経験値
	public static final String TR_SP = "trade_sp";			// 購買金額
	public static final String RATE = "rate";				// レート
	public static final String RAND = "longRand";			// 乱数
	public static final String DATE = "randDate";			// 更新日付
	public ResourceLocation ID = SweetMagicCore.getSRC("cap_shield");

	// 経験値付与
	default void addExp(int addExp) {
		this.levelUpCheck(this.getEntity().getLevel(), Math.max(1, addExp));
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

		if (!world.isClientSide()) {
			this.playSound(world, SoundInit.LEVELUP, 0.0625F, 1F);
		}

		// 進捗確認
		this.checkAdavanced(this.getEntity(), this.getLevel());

		// 余った分を再度レベルアップに回す
		if (keepExp > 0) {
			this.levelUpCheck(world, keepExp);
		}
	}

	// 進捗チェック
	default void checkAdavanced(LivingEntity entity, int level) {
		if(entity instanceof ServerPlayer sPlayer) {
			AdvancedInit.biginerCook.trigger(sPlayer, 3, level);
			AdvancedInit.intermediateCook.trigger(sPlayer, 7, level);
			AdvancedInit.advancedCook.trigger(sPlayer, 10, level);
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
		tags.putInt(TR_LEVEL, this.getTradeLevel());
		tags.putFloat(TR_EXP, this.getTradeExp());
		tags.putInt(TR_SP, this.getTradeSP());
		tags.putFloat(RATE, this.getRate());
		tags.putLong(RAND, this.getLongRand());
		tags.putLong(DATE, this.getRandDate());
		this.saveTradeList(tags, this.getSeedList(), "seedList");
		this.saveTradeList(tags, this.getFurnitureList(), "furnitureList");
		this.saveTradeList(tags, this.getMagicList(), "magicList");
		this.saveTradeList(tags, this.getEnchantList(), "enchantList");
		this.saveTradeList(tags, this.getSeasoningList(), "seasoningList");
		this.saveTradeList(tags, this.getVanillaList(), "vanillaList");
		return tags;
	}

	default void readNBT(CompoundTag tags) {
		this.setExpValue(tags.getInt(EXP));
		this.setLevel(Math.max(1, tags.getInt(LEVEL)));
		this.setHealth(tags.getFloat(HEALTH));
		this.setTradeLevel(Math.max(1, tags.getInt(TR_LEVEL)));
		this.setTradeExp(tags.getFloat(TR_EXP));
		this.setTradeSP(tags.getInt(TR_SP));
		this.setRate(tags.getFloat(RATE));
		this.setLongRand(tags.getLong(RAND));
		this.setRandDate(tags.getLong(DATE));
		this.setSeedList(this.loadAllTrade(tags, "seedList"));
		this.setFurnitureList(this.loadAllTrade(tags, "furnitureList"));
		this.setMagicList(this.loadAllTrade(tags, "magicList"));
		this.setEnchantList(this.loadAllTrade(tags, "enchantList"));
		this.setSeasoningList(this.loadAllTrade(tags, "seasoningList"));
		this.setVanillaList(this.loadAllTrade(tags, "vanillaList"));
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

	int getTradeLevel();

	void setTradeLevel(int level);

	float getTradeExp();

	void setTradeExp(float exp);

	int getTradeSP();

	void setTradeSP(int sp);

	float getRate();

	void setRate(float rate);

	long getLongRand();

	void setLongRand(long longRand);

	long getRandDate();

	void setRandDate(long randDate);

	List<TradeInfo> getSeedList();

	void setSeedList(List<TradeInfo> list);

	List<TradeInfo> getMagicList();

	void setMagicList(List<TradeInfo> list);

	List<TradeInfo> getFurnitureList();

	void setFurnitureList(List<TradeInfo> list);

	List<TradeInfo> getEnchantList();

	void setEnchantList(List<TradeInfo> list);

	List<TradeInfo> getVanillaList();

	void setVanillaList(List<TradeInfo> list);

	List<TradeInfo> getSeasoningList();

	void setSeasoningList(List<TradeInfo> list);

	default int getTradeIntExp() {
		return (int) this.getTradeExp();
	}

	default void addTradeSP(int sp) {
		this.setTradeSP(this.getTradeSP() + sp);
	}

	default void addTradeExp(Level world, int price, boolean isBuy) {
		float rate = isBuy ? 0.25F : 0.05F;
		this.tradeLevelUpCheck(world, price * rate);
	}

	// レベルアップできるかどうか
	default void tradeLevelUpCheck(Level world, float addExp) {
		int level = this.getTradeLevel();	// レベル
		int maxLevel = this.maxTradeLevel();// 最大レベル
		if (level >= maxLevel) { return; }

		float exp = this.getTradeExp();	// 経験値
		int nextLevel = 1 + level;		// 次のレベル
		float keepExp = 0F;				// レベルアップ後に持ち越し用の経験値と必要経験値の取得
		float nowNeedExp = this.needTradeExp(nextLevel) - exp;

		// 要求経験値を超えた場合に次へ持ち越し
		if (addExp > nowNeedExp) {
			keepExp = addExp - nowNeedExp;
			this.setTradeExp(exp + nowNeedExp);
		}

		else {
			this.setTradeExp(exp + addExp);
		}

		// レベルアップに必要な経験値を満たしていないなら終了
		int needExp = this.needTradeExp(nextLevel) - this.getTradeIntExp();
		if (needExp > 0) { return; }

		int upLevel = ++level;
		this.setTradeLevel(upLevel);
		this.setTradeExp(needExp);

		if (!world.isClientSide()) {
			this.playSound(world, SoundEvents.PLAYER_LEVELUP, 0.0625F, 1.15F);
		}

		// 余った分を再度レベルアップに回す
		if (keepExp > 0) {
			this.tradeLevelUpCheck(world, keepExp);
		}
	}

	// 必要経験値を取得
	default int needExp() {
		int level = this.getTradeLevel();
		int needExp = this.needTradeExp(level + 1) - this.getTradeIntExp();
		return level >= this.maxTradeLevel() ? 0 : needExp;
	}

	// 必要経験値を取得
	default int needTradeExp(int nextLevel) {
		switch(nextLevel) {
		case 1: return 0;
		case 2: return 1000;
		case 3: return 3000;
		case 4: return 15000;
		case 5: return 25000;
		default: return 1000;
		}
	}

	default int maxTradeLevel() {
		return 5;
	}

	default int getExpProgress(int value) {
		return this.getExpProgress(value, 0);
	}

	default int getExpProgress(int value, int addValue) {
		int nextEXP = this.needTradeExp(this.getTradeLevel() + 1);
		float nowEXP = this.getTradeExp() + addValue;
		return Math.min(value, (int) (value * (nowEXP / (float) nextEXP)));
	}

	public static boolean hasValue(LivingEntity entity) {
		return entity.getCapability(CapabilityInit.COOK).resolve().isPresent();
	}

	public static ICookingStatus getState(LivingEntity entity) {
		LazyOptional<ICookingStatus> opt = entity.getCapability(CapabilityInit.COOK);
		return opt != null ? opt.resolve().get() : null;
	}

	public static void sendPKT(Player player) {
		if (!(player instanceof ServerPlayer ser)) { return; }
		player.getCapability(CapabilityInit.COOK).ifPresent(o -> PacketHandler.sendTo(new CookStatusPKT(o.serializeNBT()), ser));
	}

	// List<Float>をnbt保存
	default CompoundTag saveTradeList(CompoundTag nbt, List<TradeInfo> tradeList, String name) {

		// NULLチェックとListの個数を確認
		if (tradeList != null && !tradeList.isEmpty()) {

			// リストの分だけ回してNBTに保存
			ListTag tagsList = new ListTag();
			for (TradeInfo trade : tradeList) {

				// nbtリストにnbtを入れる
				CompoundTag tags = new CompoundTag();
				tags.put("stack", trade.stack().save(new CompoundTag()));
				tags.putInt("price", trade.price());
				tagsList.add(tags);
			}

			// NBTに保存
			nbt.put(name, tagsList);
		}

		return nbt;
	}

	// nbtを呼び出してList<Float>に突っ込む
	default List<TradeInfo> loadAllTrade(CompoundTag nbt, String name) {
		List<TradeInfo> tradeList = new ArrayList<>();
		nbt.getList(name, 10).forEach(t -> tradeList.add(new TradeInfo(ItemStack.of(this.getTag(t).getCompound("stack")), this.getTag(t).getInt("price"))));
		return tradeList;
	}

	default CompoundTag getTag(Tag tag) {
		return (CompoundTag) tag;
	}
}
