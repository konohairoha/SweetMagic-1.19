package sweetmagic.init.tile.sm;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import sweetmagic.init.TileInit;
import sweetmagic.init.item.sm.SMBook;
import sweetmagic.init.tile.menu.EnchantEduceMenu;
import sweetmagic.util.ItemHelper;

public class TileEnchantEduce extends TileSMMagic {

	public boolean isCraft = false;
	public int nowLevel = 1;
	public int craftTime = 0;
	public int maxMagiaFlux = 100000;
	private static final int MAX_CRAFT_TIME = 20;
	public ItemStack outStack = ItemStack.EMPTY;
	protected final StackHandler outputInv = new StackHandler(1);
	protected final StackHandler pageInv = new StackHandler(1, true);
	protected final StackHandler inputInv = new StackHandler(1, true);

	protected final StackHandler bookInv = new StackHandler(1, true) {

		@Override
		public int getSlotLimit(int slot) {
			return 1;
		}
	};

	public TileEnchantEduce(BlockPos pos, BlockState state) {
		this(TileInit.enchantEduce, pos, state);
	}

	public TileEnchantEduce(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.resolver = new InOutHandlerProvider(this.inputInv, this.outputInv);
	}

	// サーバー側処理
	public void serverTick(Level level, BlockPos pos, BlockState state) {
		super.serverTick(level, pos, state);
		if (this.tickTime % 10 != 0) { return; }

		if (this.tickTime >= 20) {
			this.tickTime = 0;
		}

		// 作成中
		if (this.isCraft) {

			if(this.craftTime++ >= MAX_CRAFT_TIME) {
				this.craftFinish();
			}

			this.sendPKT();
		}

		else if (this.getNowLevel() > 1 && this.getBookItem().isEmpty()) {
			this.nowLevel = 1;
			this.sendPKT();
		}
	}

	// 作成開始
	public void craftStart(int buttonId) {
		ItemStack magicBook = this.getBookItem();
		if (magicBook.isEmpty()) { return; }

		int needMF = this.getEnchantCost(buttonId, (SMBook) magicBook.getItem());
		if (this.getMF() < needMF) { return; }

		ItemStack book = this.getInputItem();
		if (book.isEmpty()) { return; }

		ItemStack page = this.getPageItem();
		if (page.isEmpty()) { return; }

		if (!this.getOutItem().isEmpty()) { return; }

		boolean isBook = book.is(Items.BOOK);

		// エンチャが取得できなければ終了
		ItemStack enchaBook = isBook ? new ItemStack(Items.ENCHANTED_BOOK) : book.copy();
		Enchantment encha = this.getEnchant(buttonId);
		if (encha == null) { return; }

		if (isBook) {
			CompoundTag compoundtag = book.getTag();
			if (compoundtag != null) {
				enchaBook.setTag(compoundtag.copy());
			}
		}

		// 必要なものを消費
		enchaBook.setCount(1);
		book.shrink(1);
		page.shrink(1);
		this.setMF(this.getMF() - needMF);

		int level = Math.min(encha.getMaxLevel(), this.getNowLevel());

		if (isBook) {
			this.addEnchantment(enchaBook, new EnchantmentInstance(encha, level));
		}

		else {
			enchaBook.enchant(encha, level);
		}

		this.outStack = enchaBook;
		this.isCraft = true;

		this.sendPKT();
		this.clickButton();
	}

	// クラフトの完成
	public void craftFinish() {
		ItemHelper.insertStack(this.getOutput(), this.outStack, false);
		this.playSound(this.getBlockPos(), SoundEvents.ENCHANTMENT_TABLE_USE, 0.25F, 1F);
		this.clearInfo();
	}

	// エンチャントの取得
	public Enchantment getEnchant(int buttonId) {
		List<Enchantment> enchaList = this.getEnchaList();
		if (buttonId >= enchaList.size() || enchaList.isEmpty()) { return null; }

		return enchaList.get(buttonId);
	}

	// 取得可能なエンチャリストの取得
	public List<Enchantment> getEnchaList() {
		ItemStack stack = this.getInputItem().copy();
		List<Enchantment> enchaList = new ArrayList<>();
		if (stack.isEmpty()) { return enchaList; }

		// エンチャ本なら本と思わせておく
		boolean isEnchaBook = stack.is(Items.ENCHANTED_BOOK);
		boolean flag = stack.is(Items.BOOK) || isEnchaBook;
		ItemStack copy = isEnchaBook ? new ItemStack(Items.BOOK) : stack;

		// 既にエンチャントされているリストを取得
		List<Enchantment> stackEncha = this.stackEnchaList(stack);

		for (Enchantment encha : Registry.ENCHANTMENT) {
			if ((!encha.isTreasureOnly()) && encha.isDiscoverable() && (encha.canApplyAtEnchantingTable(copy) || (flag && encha.isAllowedOnBooks()))) {

				// エンチャが被っていたら次へ
				if (stackEncha.contains(encha)) { continue; }
				enchaList.add(encha);
			}
		}

		// 本なら修繕をリストに追加
		if (flag && !stackEncha.contains(Enchantments.MENDING)) {
			enchaList.add(Enchantments.MENDING);
		}

		return enchaList;
	}

	// 既にエンチャントされているリストを取得
	public List<Enchantment> stackEnchaList(ItemStack stack) {

		// 既にエンチャントされているリストを取得
		ListTag listTag = stack.getEnchantmentTags();
		List<Enchantment> enchaList = new ArrayList<>();

		if (!listTag.isEmpty()) {
			listTag.forEach(t -> enchaList.add(Registry.ENCHANTMENT.getOptional(this.getEnchantId(this.getTag(t))).get()));
		}

		return enchaList;
	}

	// エンチャントコストの取得
	public int getEnchantCost(int id, SMBook book) {
		List<Enchantment> enchaList = this.getEnchaList();
		if (enchaList.isEmpty() || id >= enchaList.size()) { return 0; }

		Enchantment encha = enchaList.get(id);
		if (encha.equals(Enchantments.MENDING)) { return this.getMaxMF(); }

		int maxLevel = encha.getMaxLevel();
		int rate = 6 - Math.min(maxLevel, 5);
		int addRate = maxLevel > 1 ? this.getNowLevel() : 1;
		return (11 - encha.getRarity().getWeight()) * 150 * rate * rate * addRate;
	}

	@Nullable
	public ResourceLocation getEnchantId(CompoundTag id) {
		return ResourceLocation.tryParse(id.getString("id"));
	}

	public void addEnchantment(ItemStack stack, EnchantmentInstance encha) {

		boolean flag = true;
		ListTag listtag = this.getEnchantments(stack);
		ResourceLocation src = EnchantmentHelper.getEnchantmentId(encha.enchantment);

		for (int i = 0; i < listtag.size(); ++i) {

			CompoundTag tag = listtag.getCompound(i);
			ResourceLocation src1 = EnchantmentHelper.getEnchantmentId(tag);

			if (src1 != null && src1.equals(src)) {

				if (EnchantmentHelper.getEnchantmentLevel(tag) < encha.level) {
					EnchantmentHelper.setEnchantmentLevel(tag, encha.level);
				}

				flag = false;
				break;
			}
		}

		if (flag) {
			listtag.add(EnchantmentHelper.storeEnchantment(src, encha.level));
		}

		stack.getOrCreateTag().put("StoredEnchantments", listtag);
	}

	public ListTag getEnchantments(ItemStack stack) {
		CompoundTag tags = stack.getTag();
		return tags != null ? tags.getList("StoredEnchantments", 10) : new ListTag();
	}

	public void clickLevelButton(int id) {
		ItemStack stack = this.getBookItem();
		if (stack.isEmpty()) { return; }

		int tier = this.getPageCount(stack);

		int minLevel = this.getMinLevel();
		int maxLevel = this.getMaxLevel(tier);

		if (id == 1 && this.getNowLevel() > minLevel) {
			this.nowLevel--;
		}

		else if (id == 0 && maxLevel >this.getNowLevel()) {
			this.nowLevel++;
		}

		else {
			return;
		}

		this.clickButton();
		this.sendPKT();
	}

	public void clearInfo() {
		this.isCraft = false;
		this.craftTime = 0;
		this.outStack = ItemStack.EMPTY;
		this.sendPKT();
	}

	// エンチャント最低レベルの取得
	public int getMinLevel() {
		return 1;
	}

	// エンチャント最高レベルの取得
	public int getMaxLevel(int tier) {
		return tier + 1;
	}

	public int getNowLevel() {
		return this.nowLevel;
	}

	// 魔術書のtierによってページ数を返す
	public int getPageCount(ItemStack magicBook) {
		SMBook smBook = (SMBook) magicBook.getItem();
		return smBook.getTier();
	}

	// 最大MFの取得
	@Override
	public int getMaxMF() {
		return this.maxMagiaFlux;
	}

	// 受信するMF量の取得
	@Override
	public int getReceiveMF() {
		return 10000;
	}

	// インベントリサイズの取得
	public int getInvSize() {
		return 5;
	}

	// NBTの書き込み
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put("bookInv", this.bookInv.serializeNBT());
		tag.put("pageInv", this.pageInv.serializeNBT());
		tag.put("inputInv", this.inputInv.serializeNBT());
		tag.put("outputInv", this.outputInv.serializeNBT());
		tag.putBoolean("isCraft", this.isCraft);
		tag.putInt("craftTime", this.craftTime);
		tag.putInt("nowLevel", this.nowLevel);
		tag.put("outPutStack", this.outStack.save(new CompoundTag()));
	}

	// NBTの読み込み
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.bookInv.deserializeNBT(tag.getCompound("bookInv"));
		this.pageInv.deserializeNBT(tag.getCompound("pageInv"));
		this.inputInv.deserializeNBT(tag.getCompound("inputInv"));
		this.outputInv.deserializeNBT(tag.getCompound("outputInv"));
		this.isCraft = tag.getBoolean("isCraft");
		this.craftTime = tag.getInt("craftTime");
		this.nowLevel = tag.getInt("nowLevel");
		this.outStack = ItemStack.of(tag.getCompound("outPutStack"));
	}

	// 素材スロットの取得
	public IItemHandler getBook() {
		return this.bookInv;
	}

	// 素材スロットのアイテムを取得
	public ItemStack getBookItem() {
		return this.getBook().getStackInSlot(0);
	}

	// 素材スロットの取得
	public IItemHandler getInput() {
		return this.inputInv;
	}

	// 素材スロットのアイテムを取得
	public ItemStack getInputItem() {
		return this.getInput().getStackInSlot(0);
	}

	// 素材スロットの取得
	public IItemHandler getPage() {
		return this.pageInv;
	}

	// 素材スロットのアイテムを取得
	public ItemStack getPageItem() {
		return this.getPage().getStackInSlot(0);
	}

	// 素材スロットの取得
	public IItemHandler getOutput() {
		return this.outputInv;
	}

	// 素材スロットのアイテムを取得
	public ItemStack getOutItem() {
		return this.getOutput().getStackInSlot(0);
	}

	// クラフト描画量を計算するためのメソッド
	public int getCraftProgress(int value) {
		return this.getProgress(value, this.craftTime, MAX_CRAFT_TIME);
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new EnchantEduceMenu(windowId, inv, this);
	}

	// インベントリのアイテムを取得
	public List<ItemStack> getInvList() {
		List<ItemStack> stackList = new ArrayList<>();
		this.addStackList(stackList, this.getInputItem());
		this.addStackList(stackList, this.getOutItem());
		this.addStackList(stackList, this.getPageItem());
		this.addStackList(stackList, this.getBookItem());
		return stackList;
	}

	// ブロック内の情報が空かどうか
	public boolean isInfoEmpty() {
		return this.getInvList().isEmpty() && this.isMFEmpty();
	}
}
