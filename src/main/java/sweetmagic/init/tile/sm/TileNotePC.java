package sweetmagic.init.tile.sm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;
import sweetmagic.api.SweetMagicAPI;
import sweetmagic.api.iitem.IAcce;
import sweetmagic.api.iitem.IFood;
import sweetmagic.api.iitem.IMagicItem;
import sweetmagic.api.iitem.ITier;
import sweetmagic.api.iitem.info.AcceInfo;
import sweetmagic.init.ItemInit;
import sweetmagic.init.TagInit;
import sweetmagic.init.TileInit;
import sweetmagic.init.capability.icap.ICookingStatus;
import sweetmagic.init.item.blockitem.SMSeed;
import sweetmagic.init.item.sm.SMFood;
import sweetmagic.init.tile.menu.NotePCMenu;
import sweetmagic.util.ItemHelper;

public class TileNotePC extends TileAbstractSM {

	public long randDate = -1;
	private long nextTime = 0L;
	private long seed = 0L;
	private float rate = 0F;
	private Random dateRand = null;
	private UUID ownerUUID;
	private Player player;
	public final StackHandler inputInv = new StackHandler(1);
	public final StackHandler outInv = new StackHandler(this.getInvSize());
	private List<ItemStack> stackList = new ArrayList<>();
	public List<Integer> intList= new ArrayList<>();
	public int buyCount = 1;
	private int viewCount = 10;
	private static final String NOMAL_TRADE = "normal_trade";
	private static final String COUNT_TRADE = "count_trade";
	private static final String MF_TRADE = "mf_trade";

	public TileNotePC(BlockPos pos, BlockState state) {
		super(TileInit.notePC, pos, state);
	}

	// サーバー側処理
	@Override
	public void serverTick(Level world, BlockPos pos, BlockState state) {
		super.serverTick(world, pos, state);
		if (this.tickTime % 20 != 0) { return; }

		if(!this.stackList.isEmpty()) {

			List<ItemStack> stackList = new ArrayList<>();

			for(ItemStack stack : this.stackList) {
				if(stack.isEmpty()) { continue; }
				ItemStack out = ItemHelper.insertStack(this.getOut(), stack.copy(), false);
				this.addStackList(stackList, out);
			}

			this.stackList.clear();

			if (!stackList.isEmpty()) {
				this.stackList = stackList;
			}
		}

		this.clearInfo();
		this.setNextTime();
		Player player = this.getOwner();
		if(player == null || !this.isRSPower()) { return; }

		this.addButSale(player);
	}

	public List<List<TradeInfo>> getTrade(Player player) {
		List<List<TradeInfo>> list = new ArrayList<>();
		ICookingStatus cook = this.getCook(player);
		boolean isUpdate = cook.getRandDate() != this.randDate || cook.getSeedList().isEmpty();

		if(isUpdate) {
			cook.setRandDate(this.randDate);
		}

		this.dateRand = new Random(this.seed + this.randDate);
		cook.setRate(this.getRate());
		this.viewCount = 10 + cook.getTradeLevel() * 2;
		this.intList = Arrays.<Integer> asList(0, 1, 2, 3, 4, 5);
		Collections.shuffle(this.intList, this.dateRand);
		list.add(this.getSeedList(cook, isUpdate));
		list.add(this.getMagicList(cook, isUpdate));
		list.add(this.getFurnitureList(cook, isUpdate));
		list.add(this.getEnchantList(cook, isUpdate));
		list.add(this.getVanillaList(cook, isUpdate));
		list.add(this.getSeasoningList(cook, isUpdate));

		List<List<TradeInfo>> tradeList = new ArrayList<>();
		int tradeLevel = this.getCook(player).getTradeLevel() + 1;

		for(int i = 0; i < tradeLevel; i++)
			tradeList.add(list.get(this.intList.get(i)));

		return tradeList;
	}

	public Player getOwner() {
		if(this.player != null) { return this.player; }

		if (this.ownerUUID != null && this.getLevel() instanceof ServerLevel server && server.getEntity(this.ownerUUID) instanceof Player player) {
			this.player = player;
			return player;
		}

		return null;
	}

	public void setOwner(Player player) {
		this.player = player;
		this.ownerUUID = player.getUUID();
	}

	public void clearInfo() {
		if(this.dateRand != null && this.randDate == this.getDate()) { return; }

		Player player = this.player;
		if(player == null) { return; }

		this.randDate = this.getDate();
		this.seed = this.seed != 0L ? this.seed : this.getLevel().getServer().getLevel(Level.OVERWORLD).getSeed();
		this.sendInfo();
	}

	public long getDate() {
		ServerLevel sever = this.getLevel().getServer().getLevel(Level.OVERWORLD);
		int dayTime = 24000;
		return sever.getDayTime() / dayTime;
	}

	public Random getRand(ICookingStatus cook) {
		ServerLevel sever = this.getLevel().getServer().getLevel(Level.OVERWORLD);
		cook.setLongRand(sever.getSeed() + this.randDate);
		return new Random(cook.getLongRand());
	}

	public float getRate() {
		this.dateRand.nextFloat();
		return 0.75F + this.dateRand.nextFloat() * 0.75F;
	}

	public List<TradeInfo> getSeedList(ICookingStatus cook, boolean isUpdate) {
		if(!isUpdate) { return cook.getSeedList(); }
		List<TradeInfo> list = this.setSeedList();
		cook.setSeedList(list);
		return list;
	}

	public List<TradeInfo> setSeedList() {
		List<TradeInfo> list = new ArrayList<>();

		for (Item item : this.getTagList(TagInit.SEED)) {
			if (item instanceof ITier ti && ti.getTier() > 0) {
				list.add(TradeInfo.create(item, 750 + 500 * ti.getTier() * ti.getTier(), this.dateRand));
			}

			else {
				list.add(TradeInfo.create(item, 75, this.dateRand));
			}
		}

		return this.getTradeList(list);
	}

	public List<TradeInfo> getMagicList(ICookingStatus cook, boolean isUpdate) {
		if(!isUpdate) { return cook.getMagicList(); }
		List<TradeInfo> list = this.setMagicList();
		cook.setMagicList(list);
		return list;
	}

	public List<TradeInfo> setMagicList() {
		List<TradeInfo> list = new ArrayList<>();
		this.getTagList(TagInit.MAGIC).forEach(s -> list.add(TradeInfo.create(s, 1.5F, this.dateRand)));
		return this.getTradeList(list);
	}

	public List<TradeInfo> getFurnitureList(ICookingStatus cook, boolean isUpdate) {
		if(!isUpdate) { return cook.getFurnitureList(); }
		List<TradeInfo> list = this.setFurnitureList();
		cook.setFurnitureList(list);
		return list;
	}

	public List<TradeInfo> setFurnitureList() {
		List<TradeInfo> list = new ArrayList<>();
		this.getTagList(TagInit.FURNITURE).forEach(s -> list.add(TradeInfo.create(s, 4, 25, this.dateRand)));
		return this.getTradeList(list);
	}

	public List<TradeInfo> getEnchantList(ICookingStatus cook, boolean isUpdate) {
		if(!isUpdate) { return cook.getEnchantList(); }
		List<TradeInfo> list = this.setEnchantList();
		cook.setEnchantList(list);
		return list;
	}

	public List<TradeInfo> setEnchantList() {
		ItemStack stack = new ItemStack(Items.ENCHANTED_BOOK);
		List<TradeInfo> seedList = new ArrayList<>();
		List<Enchantment> allEncha = Registry.ENCHANTMENT.stream().toList();

		for(int i = 0; i < this.getViewCount(); i++) {

			int cost = 0;
			ItemStack copy = stack.copy();
			List<Enchantment> enchaList = new ArrayList<>();
			List<EnchantmentInstance> enchaInsList = new ArrayList<>();

			for (int k = 0; k < this.dateRand.nextInt(3) + 1; k++) {
				Enchantment encha = allEncha.get(this.dateRand.nextInt(allEncha.size()));
				if(enchaList.contains(encha)) { continue; }

				int level = Math.min(encha.getMaxLevel(), this.dateRand.nextInt(encha.getMaxLevel() + 1) + 1);
				cost += (11 - encha.getRarity().getWeight()) * level * 200;

				if(encha.getMaxLevel() == 1) {
					cost += (11 - encha.getRarity().getWeight()) * (encha.isCurse() ? -100 : 3000);
				}

				enchaList.add(encha);
				enchaInsList.add(new EnchantmentInstance(encha, level));
			}

			ListTag listtag = new ListTag();
			enchaInsList.forEach(e -> listtag.add(EnchantmentHelper.storeEnchantment(EnchantmentHelper.getEnchantmentId(e.enchantment), e.level)));
			copy.getOrCreateTag().put("StoredEnchantments", listtag);
			seedList.add(new TradeInfo(copy, cost + this.dateRand.nextInt(Math.max(1, cost / 4))));
		}

		return seedList;
	}

	public List<TradeInfo> getVanillaList(ICookingStatus cook, boolean isUpdate) {
		if(!isUpdate) { return cook.getVanillaList(); }
		List<TradeInfo> list = this.setVanillaList();
		cook.setVanillaList(list);
		return list;
	}

	public List<TradeInfo> setVanillaList() {
		List<TradeInfo> list = new ArrayList<>();
		this.getTagList(TagInit.VANILLA).forEach(s -> list.add(TradeInfo.create(s, 1F, this.dateRand)));
		return this.getTradeList(list);
	}

	public List<TradeInfo> getSeasoningList(ICookingStatus cook, boolean isUpdate) {
		if(!isUpdate) { return cook.getSeasoningList(); }
		List<TradeInfo> list = this.setSeasoningList();
		cook.setSeasoningList(list);
		return list;
	}

	public List<TradeInfo> setSeasoningList() {
		List<TradeInfo> list = new ArrayList<>();
		this.getTagList(TagInit.SEASONING).forEach(s -> list.add(TradeInfo.create(s, 100, this.dateRand)));
		return this.getTradeList(list);
	}

	public List<TradeInfo> getTradeList(List<TradeInfo> list) {
		List<TradeInfo> tradeList = new ArrayList<>();
		int size = Math.min(this.getViewCount(), list.size());

		for(int i = 0; i < size; i++) {
			while(true) {
				TradeInfo info = list.get(this.dateRand.nextInt(list.size()));
				if(!tradeList.contains(info)) {
					tradeList.add(info);
					break;
				}
			}
		}

		return tradeList;
	}

	public void setTradeList(List<TradeInfo> list, String tradeType, Item item, int value, Random rand) {
		switch(tradeType) {

		}
	}

	public int getValue(ICookingStatus cook, ItemStack stack) {
		return TileNotePC.getGlobalValue(cook.getRate(), stack);
	}

	public static int getGlobalValue(float rate, ItemStack stack) {

		int value = stack.getCount();
		Item item = stack.getItem();

		List<Item> seedStackList = TileNotePC.getGlobalTagList(TagInit.SEEDS).stream().toList();
		List<Item> saplingStackList = TileNotePC.getGlobalTagList(TagInit.SAPLINGS).stream().toList();

		// 苗木
		if (saplingStackList.contains(item)) {
			return (int) (25 * value * rate);
		}

		// 種
		else if (seedStackList.contains(item) || item instanceof SMSeed) {
			return (int) (40 * value * rate);
		}

		// スイマジの食べ物
		 if (item instanceof SMFood food) {
			 FoodProperties pro = food.getFoodProperties(stack, null);
			int amount = (int) (pro.getNutrition() * pro.getSaturationModifier() * 100F);
			float saturation = Math.max(pro.getSaturationModifier() * pro.getSaturationModifier(), 0.75F);
			int mfValue = (int) (amount * (saturation + 0.1));
			float quality = 1F + food.getQualityValue(stack) * 0.25F;
			return (int) (mfValue * value * rate * quality);
		}

		// 食べ物
		else if (item instanceof IFood || ItemInit.foodList.contains(item) || item.isEdible()) {

			if (ItemInit.foodList.contains(item)) {
				return (int) (30 * value * rate);
			}

			 FoodProperties pro = item.getFoodProperties();
			int amount = (int) (pro.getNutrition() * pro.getSaturationModifier() * 10F);
			int mfValue = (int) (amount * 0.5F);
			return (int) (mfValue * value * rate);
		}

		// エメラルド
		else if (item == Items.EMERALD) {
			return (int) (1000 * value * rate);
		}

		else if (SweetMagicAPI.hasMF(stack)) {
			int sp = (int) (Math.min(10000, Math.max(1, SweetMagicAPI.getMF(stack) / 15)) * value * rate);

			if(sp > 100000) {
				sp *= 0.33F;
			}

			else if(sp > 10000) {
				sp *= 0.67F;
			}

			else if(sp > 1000) {
				sp *= 0.75F;
			}
			return sp;
		}

		else if (item instanceof IAcce acce) {
			int tier = acce.getTier() - 1;
			return (int) ((1000 + 10000 * tier * tier) * value * rate * acce.getStackCount(new AcceInfo(stack)));
		}

		else if (item instanceof ITier tier) {
			return (int) (10 + 20 * tier.getTier() * tier.getTier() * value * rate);
		}

		return Math.max(1, (int) (value * rate));
	}

	public void addButCount(int id) {
		switch(id) {
		case 0:
			this.buyCount = Math.min(640, this.buyCount + 1);
			break;
		case 1:
			int addValue = this.buyCount == 1 ? 9 : 10;
			this.buyCount = Math.min(640, this.buyCount + addValue);
			break;
		case 2:
			int addValue2 = this.buyCount == 1 ? 63 : 64;
			this.buyCount = Math.min(640, this.buyCount + addValue2);
			break;
		case 3:
			this.buyCount = Math.max(1, this.buyCount - 1);
			break;
		case 4:
			this.buyCount = Math.max(1, this.buyCount - 10);
			break;
		case 5:
			this.buyCount = Math.max(1, this.buyCount - 64);
			break;
		}

		this.sendInfo();
	}

	public void addButSale(Player player) {
		ItemStack stack = this.getInputItem();
		if (stack.isEmpty()) { return; }

		this.dateRand = new Random(this.seed + this.randDate);
		this.rate = this.getRate();
		ICookingStatus cook = this.getCook(player);
		cook.setRate(this.rate);
		int value = this.getValue(cook, stack);
		cook.addTradeSP(value);

		stack.shrink(stack.getCount());
		cook.addTradeExp(player.getLevel(), value, false);
		ICookingStatus.sendPKT(this.player);
	}

	public void addButBuy(Player player, int id) {
		if(!this.stackList.isEmpty()) { return; }

		ICookingStatus cook = this.getCook(player);
		int viewCount = 10 + cook.getTradeLevel() * 2;
		int tabId = id / viewCount;
		int selectId = id % viewCount;

		List<List<TradeInfo>> tradeList = this.getTrade(player);
		TradeInfo info = tradeList.get(tabId).get(selectId);
		int buyCount = this.buyCount;
		int shrinkSP = info.price() * buyCount;
		ItemStack stack = info.stack();
		if(cook.getTradeSP() < shrinkSP) { return; }

		while(buyCount > 0) {
			int count = Math.min(64, buyCount);
			buyCount -= count;
			ItemStack copy = stack.copy();
			copy.setCount(count * copy.getCount());
			ItemStack out = ItemHelper.insertStack(this.getOut(), copy, false);
			this.addStackList(this.stackList, out);
		}

		cook.addTradeExp(player.getLevel(), shrinkSP, true);
		cook.addTradeSP(-shrinkSP);
		ICookingStatus.sendPKT(this.player);
	}

	public long getNextTime() {
		return this.nextTime;
	}

	public void setNextTime() {
		this.nextTime = this.getNextDate();
		this.sendInfo();
	}

	public long getNextDate() {
		ServerLevel sever = this.getLevel().getServer().getLevel(Level.OVERWORLD);
		int dayTime = 24000;
		long date= sever.getDayTime() / dayTime;
		long nextDate= (date + 1) * dayTime;
		return nextDate - sever.getDayTime();
	}

	public int getViewCount() {
		return this.viewCount;
	}

	public ICookingStatus getCook(Player player) {
		return ICookingStatus.getState(player);
	}

	public ITag<Item> getTagList(TagKey<Item> tag) {
		return TileNotePC.getGlobalTagList(tag);
	}

	public static ITag<Item> getGlobalTagList(TagKey<Item> tag) {
		return ForgeRegistries.ITEMS.tags().getTag(tag);
	}

	// インベントリサイズの取得
	@Override
	public int getInvSize() {
		return 10;
	}

	// スロットの取得
	public IItemHandler getInput() {
		return this.inputInv;
	}

	// スロットのアイテムを取得
	public ItemStack getInputItem() {
		return this.getInput().getStackInSlot(0);
	}

	// スロットの取得
	public IItemHandler getOut() {
		return this.outInv;
	}

	// スロットのアイテムを取得
	public ItemStack getOutItem(int i) {
		return this.getOut().getStackInSlot(i);
	}

	// NBTの書き込み
	@Override
	protected void saveAdditional(CompoundTag tags) {
		super.saveAdditional(tags);
		tags.put("inputInv", this.inputInv.serializeNBT());
		tags.put("outInv", this.outInv.serializeNBT());
		tags.putInt("buyCount", this.buyCount);
		tags.putLong("nextTime", this.getNextTime());
		tags.putLong("seed", this.seed);
		tags.putLong("randDate", this.randDate);
		this.saveStackList(tags, this.stackList, "stackList");
		if (this.ownerUUID != null) {
			tags.putUUID("Owner", this.ownerUUID);
		}
	}

	// NBTの読み込み
	@Override
	public void load(CompoundTag tags) {
		super.load(tags);
		this.inputInv.deserializeNBT(tags.getCompound("inputInv"));
		this.outInv.deserializeNBT(tags.getCompound("outInv"));
		this.buyCount = tags.getInt("buyCount");
		this.nextTime = tags.getLong("nextTime");
		this.seed = tags.getLong("seed");
		this.randDate = tags.getLong("randDate");
		this.stackList = this.loadAllStack(tags, "stackList");
		if (tags.hasUUID("Owner")) {
			this.ownerUUID = tags.getUUID("Owner");
		}
	}

	// List<Float>をnbt保存
	public CompoundTag saveTradeList(CompoundTag nbt, List<TradeInfo> tradeList, String name) {

		// NULLチェックとListの個数を確認
		if (tradeList != null && !tradeList.isEmpty()) {

			// リストの分だけ回してNBTに保存
			ListTag tagsList = new ListTag();
			for (TradeInfo trade : tradeList) {

				// nbtリストにnbtを入れる
				CompoundTag tags = new CompoundTag();
				tags.put("stack", trade.stack.save(new CompoundTag()));
				tags.putInt("price", trade.price);
				tagsList.add(tags);
			}

			// NBTに保存
			nbt.put(name, tagsList);
		}

		return nbt;
	}

	// nbtを呼び出してList<Float>に突っ込む
	public List<TradeInfo> loadAllTrade(CompoundTag nbt, String name) {
		List<TradeInfo> tradeList = new ArrayList<>();
		nbt.getList(name, 10).forEach(t -> tradeList.add(new TradeInfo(ItemStack.of(this.getTag(t).getCompound("stack")), this.getTag(t).getInt("price"))));
		return tradeList;
	}

	// ブロック内の情報が空かどうか
	public boolean isInfoEmpty() {
		return this.getInputItem().isEmpty();
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new NotePCMenu(windowId, inv, this);
	}

	public static record TradeInfo(ItemStack stack, int price) {

		// 通常の売値設定
		public static TradeInfo create(Item item, int price) {
			return new TradeInfo(new ItemStack(item), price);
		}

		// 通常の売値設定
		public static TradeInfo create(Item item, int price, Random rand) {
			return new TradeInfo(new ItemStack(item), price + rand.nextInt(price / 4));
		}

		// 一定個数での売値設定
		public static TradeInfo create(Item item, int value, int price, Random rand) {
			return new TradeInfo(new ItemStack(item, value), price + rand.nextInt(price / 4));
		}

		// MFでの売値設定
		public static TradeInfo create(Item item, float rate, Random rand) {
			ItemStack stack = new ItemStack(item);
			int mf = SweetMagicAPI.getMF(stack);

			if(mf <= 0 && item instanceof ITier ti) {
				int addRate = item instanceof IMagicItem ? Math.max(2, ti.getTier()) : 1;
				mf = 100 + 150 * ti.getTier() * ti.getTier() * addRate;
			}

			return new TradeInfo(stack, (int) (mf * rate * (1.25F - rand.nextFloat() * 0.25F)));
		}
	}
}
