package sweetmagic.init.capability;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import sweetmagic.init.capability.icap.ICookingStatus;
import sweetmagic.init.tile.sm.TileNotePC.TradeInfo;

public class CookingStatusHandler implements ICookingStatus {

	private LivingEntity host;
	private int exp;
	private int level;
	private float health;
	private int tradeLevel;
	private float tradeExp;
	private int tradeSP;
	private float rate;
	private long longRand;
	private long randDate;
	private List<TradeInfo> seedList = new ArrayList<>();
	private List<TradeInfo> magicList = new ArrayList<>();
	private List<TradeInfo> furnitureList = new ArrayList<>();
	private List<TradeInfo> enchantList = new ArrayList<>();
	private List<TradeInfo> vanillaList = new ArrayList<>();
	private List<TradeInfo> seasoningList = new ArrayList<>();

	public void setEntity(LivingEntity entity) {
		this.host = entity;
	}

	public LivingEntity getEntity() {
		return this.host;
	}

	public void setExpValue(int exp) {
		this.exp = exp;
	}

	public int getExpValue() {
		return this.exp;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getLevel() {
		return Math.max(1, this.level);
	}

	public void setHealth(float health) {
		this.health = health;
	}

	public float getHealth() {
		return this.health;
	}

	public int getTradeLevel() {
		return this.tradeLevel;
	}

	public void setTradeLevel(int level) {
		this.tradeLevel = level;
	}

	public float getTradeExp() {
		return this.tradeExp;
	}

	public void setTradeExp(float exp) {
		this.tradeExp = exp;
	}

	public int getTradeSP() {
		return this.tradeSP;
	}

	public void setTradeSP(int sp) {
		this.tradeSP = sp;
	}

	public float getRate() {
		return this.rate;
	}

	public void setRate(float rate) {
		this.rate = rate;
	}

	public long getLongRand() {
		return this.longRand;
	}

	public void setLongRand(long longRand) {
		this.longRand = longRand;
	}

	public long getRandDate() {
		return this.randDate;
	}

	public void setRandDate(long randDate) {
		this.randDate = randDate;
	}

	@Override
	public CompoundTag serializeNBT() {
		return this.writeNBT();
	}

	@Override
	public void deserializeNBT(CompoundTag tags) {
		this.readNBT(tags);
	}

	@Override
	public List<TradeInfo> getSeedList() {
		return this.seedList;
	}

	@Override
	public void setSeedList(List<TradeInfo> list) {
		this.seedList = list;

	}

	@Override
	public List<TradeInfo> getMagicList() {
		return this.magicList;
	}

	@Override
	public void setMagicList(List<TradeInfo> list) {
		this.magicList = list;
	}

	@Override
	public List<TradeInfo> getFurnitureList() {
		return this.furnitureList;
	}

	@Override
	public void setFurnitureList(List<TradeInfo> list) {
		this.furnitureList = list;
	}

	@Override
	public List<TradeInfo> getEnchantList() {
		return this.enchantList;
	}

	@Override
	public void setEnchantList(List<TradeInfo> list) {
		this.enchantList = list;
	}

	@Override
	public List<TradeInfo> getVanillaList() {
		return this.vanillaList;
	}

	@Override
	public void setVanillaList(List<TradeInfo> list) {
		this.vanillaList = list;
	}

	@Override
	public List<TradeInfo> getSeasoningList() {
		return this.seasoningList;
	}

	@Override
	public void setSeasoningList(List<TradeInfo> list) {
		this.seasoningList = list;
	}
}
