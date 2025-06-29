package sweetmagic.init.tile.sm;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import sweetmagic.init.ItemInit;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.SoundInit;
import sweetmagic.init.TileInit;
import sweetmagic.init.item.sm.SMBook;
import sweetmagic.init.tile.menu.MagiaRewriteMenu;
import sweetmagic.util.ItemHelper;

public class TileMagiaRewrite extends TileSMMagic {

	public boolean isCraft = false;
	public int nowLevel = 1;
	public int craftTime = 0;
	public int craftTick = 0;
	public int maxMagiaFlux = 1000000;
	private static final int MAX_CRAFT_TIME = 30;
	public ItemStack outStack = ItemStack.EMPTY;
	protected final StackHandler inputInv = new StackHandler(1, true);
	protected final StackHandler outInv = new StackHandler(1, true);
	protected final StackHandler bookInv = new StackHandler(1);

	public TileMagiaRewrite(BlockPos pos, BlockState state) {
		this(TileInit.magiaWrite, pos, state);
	}

	public TileMagiaRewrite(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.resolver = new InOutHandlerProvider(this.inputInv, this.outInv);
	}

	// サーバー側処理
	public void serverTick(Level world, BlockPos pos, BlockState state) {
		super.serverTick(world, pos, state);

		if (this.isCraft) {

			this.craftTick++;
			this.sendPKT();
			int value = 10;

			if (this.craftTime > 10 && this.craftTime <= 25) {
				value = 5;
			}

			if (this.tickTime % value == 0 && this.craftTime < 29) {
				this.playSound(pos, SoundInit.SWING, 0.15F, 1F);
			}
		}

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

	// クライアント側処理
	public void clientTick(Level world, BlockPos pos, BlockState state) {
		super.clientTick(world, pos, state);

		if (this.isCraft && this.tickTime % 6 == 0 && this.craftTime < 24) {
			this.spawnParticl();
		}

		if (this.isCraft && this.tickTime % 10 == 0 && this.craftTime > 3 && this.craftTime < 27) {

			ParticleOptions par = ParticleInit.CYCLE_RERITE;
			for (int i = 0; i < 2; i++) {
				this.spawnParticleCycle(world, par, pos.getX() + 0.5D, pos.getY() + 0.85D, pos.getZ() + 0.5D, Direction.UP, 0.7D, (i * 180), false);
				this.spawnParticleCycle(world, par, pos.getX() + 0.5D, pos.getY() + 0.55D, pos.getZ() + 0.5D, Direction.UP, 0.6D, (i * 180), true);
			}
		}
	}

	// 作成開始
	public void craftStart(int id) {
		ItemStack magicBook = this.getBookItem();
		if (magicBook.isEmpty()) { return; }

		ItemStack input = this.getInputItem();
		if (input.isEmpty() || !this.getOutItem().isEmpty()) { return; }

		// MFが足りなければ終了
		int needMF = this.getEnchantCost(id);
		if (this.getMF() < needMF || needMF <= 0) { return; }

		// 変更後のレベルを取得
		int changedLevel = this.getChanedLevel(id, input);
		if (changedLevel <= 0) { return; }

		// エンチャントレベルの設定
		int tier = this.getTier();
		int nowLevel = this.getNowEnchantLevel(id, input);
		this.setEnchant(id, input, changedLevel);

		this.isCraft = true;
		this.craftTick++;
		this.tickTime = 0;
		this.outStack = input.copy();

		// 必要なものを消費
		input.shrink(1);
		this.setMF(this.getMF() - needMF);

		if (tier == 3) {
			int levelUp = changedLevel - nowLevel;
			magicBook.shrink(levelUp);
		}

		this.sendPKT();
		this.clickButton();
	}

	// クラフトの完成
	public void craftFinish() {
		ItemHelper.insertStack(this.getOut(), this.outStack, false);
		this.playSound(this.getBlockPos(), SoundEvents.ENCHANTMENT_TABLE_USE, 1F, 1F);
		this.clearInfo();
	}

	// エンチャントの取得
	public Enchantment getEnchant(int id) {
		ItemStack stack = this.getInputItem();
		if (stack.isEmpty()) { return null; }

		// 取得可能なエンチャリストの取得
		List<Enchantment> enchaList = this.stackEnchaList(stack);
		if (enchaList.isEmpty() || id >= enchaList.size()) { return null; }

		return enchaList.get(id);
	}

	public int getEnchaLevel(int id) {
		ItemStack stack = this.getInputItem();
		ListTag listTags = stack.getEnchantmentTags();
		CompoundTag tags = listTags.getCompound(id);
		int level = EnchantmentHelper.getEnchantmentLevel(tags);
		return level;
	}

	public List<Enchantment> getEnchaList() {
		return this.stackEnchaList(this.getInputItem());
	}

	// 既にエンチャントされているリストを取得
	public List<Enchantment> stackEnchaList(ItemStack stack) {

		// 既にエンチャントされているリストを取得
		ListTag listTag = stack.getEnchantmentTags();
		List<Enchantment> stackEncha = new ArrayList<>();

		if (!listTag.isEmpty()) {
			listTag.forEach(t -> stackEncha.add(Registry.ENCHANTMENT.getOptional(this.getEnchantId(this.getTag(t))).get()));
		}

		return stackEncha;
	}

	// エンチャントコストの取得
	public int getEnchantCost(int id) {
		ItemStack stack = this.getInputItem();
		if (stack.isEmpty()) { return 0; }

		ListTag listTags = stack.getEnchantmentTags();
		if (listTags.isEmpty() || id >= listTags.size()) { return 0; }

		CompoundTag tags = listTags.getCompound(id);
		ResourceLocation enchaSRC = EnchantmentHelper.getEnchantmentId(tags);
		if (enchaSRC == null) { return 0; }

		List<Enchantment> enchaList = this.stackEnchaList(stack);
		if (enchaList.isEmpty()) { return 0; }

		Enchantment encha = enchaList.get(id);

		int level = EnchantmentHelper.getEnchantmentLevel(tags);
		if (level >= this.getMaxEnchantLevel()) { return 0; }

		int tier = this.getTier();
		int maxLevel = encha.getMaxLevel();
		int canAddLevel = this.getMaxRwiteLevel(maxLevel);
		if (maxLevel == 1 || level >= canAddLevel || ( tier >= 3 && level < maxLevel )) { return 0; }

		int rate = (6 - Math.min(maxLevel, 5));
		int addRate = Math.min(maxLevel, level + this.nowLevel) - level;

		if (tier >= 3) {

			ItemStack magicBook = this.getBookItem();
			int changedLevel = this.getChanedLevel(id, stack);
			int nowLevel = this.getNowEnchantLevel(id, stack);
			if (magicBook.getCount() < changedLevel - nowLevel) { return -1; }

			addRate = (Math.min(maxLevel * 2, level + this.nowLevel) - level) * 5;

			if (encha.getRarity().getWeight() == 1 || encha == Enchantments.BLOCK_FORTUNE) {
				return this.maxMagiaFlux;
			}
		}

		return (11 - encha.getRarity().getWeight() ) * 1500 * rate * rate * addRate;
	}

	public int getChanedLevel(int id) {
		return this.getChanedLevel(id, this.getInputItem());
	}

	public int getChanedLevel(int id, ItemStack stack) {
		Enchantment encha = this.getEnchant(id);
		if (encha == null) { return 0; }

		ListTag listTags = stack.getEnchantmentTags();
		CompoundTag tags = listTags.getCompound(id);
		int level = EnchantmentHelper.getEnchantmentLevel(tags);
		int maxLevel = encha.getMaxLevel();
		int addMaxLevel = this.getMaxRwiteLevel(maxLevel);

		if (addMaxLevel > maxLevel) {
			maxLevel = addMaxLevel;
		}

		int addLevel = Math.min(maxLevel, level + this.nowLevel) - level;
		return addLevel <= 0 ? 0 : level + addLevel;
	}

	public int getNowEnchantLevel(int id) {
		return this.getNowEnchantLevel(id, this.getInputItem());
	}

	public int getNowEnchantLevel(int id, ItemStack stack) {
		ListTag listTags = stack.getEnchantmentTags();
		CompoundTag tags = listTags.getCompound(id);
		return EnchantmentHelper.getEnchantmentLevel(tags);
	}

	// エンチャントレベルの設定
	public void setEnchant(int id, ItemStack stack, int level) {
		ListTag listTags = stack.getEnchantmentTags();
		CompoundTag tags = listTags.getCompound(id);
		EnchantmentHelper.setEnchantmentLevel(tags, level);
		stack.getTag().put("Enchantments", listTags);
	}

	@Nullable
	public ResourceLocation getEnchantId(CompoundTag id) {
		return ResourceLocation.tryParse(id.getString("id"));
	}

	public void clickLevelButton(int id) {
		ItemStack stack = this.getBookItem();
		if (stack.isEmpty()) { return; }

		int tier = this.getTier();
		int maxLevel = this.getAddMaxEnchantLevel(tier) - 1;

		if (id == 1 && this.getNowLevel() > 1) {
			this.nowLevel--;
		}

		else if (id == 0 && maxLevel > this.getNowLevel()) {
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
		this.craftTick = 0;
		this.outStack = ItemStack.EMPTY;
		this.sendPKT();
	}

	// 最大エンチャントレベル
	public int getMaxEnchantLevel() {
		return this.getMaxEnchantLevel(this.getTier());
	}

	// 最大書き換えレベル
	public int getMaxRwiteLevel(int maxLevel) {

		// tier1なら最大3まで
		int tier = this.getTier();
		if (tier == 1) { return Math.min(3, maxLevel); }

		// tier3なら最大が5以上なら10までそれ以外なら5
		if (tier == 3) {
			return maxLevel * 2;
		}

		// tier2なら最大レベルまでの引き上げ
		return Math.min(5, maxLevel);
	}

	// 最大エンチャントレベル
	public int getMaxEnchantLevel(int tier) {
		switch (tier) {
		case 1: return 3;
		case 2: return 5;
		case 3: return this.getWishMaxLevel();
		default: return 3;
		}
	}

	// 最大エンチャントレベル
	public int getAddMaxEnchantLevel() {
		return this.getAddMaxEnchantLevel(this.getTier());
	}

	// 最大エンチャントレベル
	public int getAddMaxEnchantLevel(int tier) {
		switch (tier) {
		case 1: return 3;
		case 2: return 5;
		case 3: return 6;
		default: return 3;
		}
	}

	// 魔術書のtierによってページ数を返す
	public int getTier() {

		ItemStack stack = this.getBookItem();
		Item item = stack.getItem();

		if (item instanceof SMBook book) {
			return Math.min(2, book.getTier());
		}

		else if (stack.is(ItemInit.wish_crystal)) {
			return 3;
		}

		return 0;
	}

	public int getMaxLevel(int maxLevel) {
		return !this.getBookItem().is(ItemInit.wish_crystal) ? maxLevel : this.getWishMaxLevel();
	}

	// 願いの結晶の個数で付与可能な最大レベルを取得
	public int getWishMaxLevel() {
		switch (this.getBookItem().getCount()) {
		case 0:  return 0;
		case 1:  return 6;
		case 2:  return 7;
		case 3:  return 8;
		case 4:  return 9;
		default: return 10;
		}
	}

	// 現在のレベルから必要な願いの結晶の個数を取得
	public int getWishShirinCount() {
		switch (this.nowLevel) {
		case 6:  return 1;
		case 7:  return 2;
		case 8:  return 3;
		case 9:  return 4;
		case 10: return 5;
		default: return 1;
		}
	}

	public int getNowLevel() {
		return this.nowLevel;
	}

	// 最大MFの取得
	@Override
	public int getMaxMF() {
		return this.maxMagiaFlux;
	}

	// 受信するMF量の取得
	@Override
	public int getReceiveMF() {
		return 20000;
	}

	// インベントリサイズの取得
	public int getInvSize() {
		return 1;
	}

	// NBTの書き込み
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put("bookInv", this.bookInv.serializeNBT());
		tag.put("inputInv", this.inputInv.serializeNBT());
		tag.put("outInv", this.outInv.serializeNBT());
		tag.putBoolean("isCraft", this.isCraft);
		tag.putInt("craftTime", this.craftTime);
		tag.putInt("craftTick", this.craftTick);
		tag.putInt("nowLevel", this.nowLevel);
		tag.put("outPutStack", this.outStack.save(new CompoundTag()));
	}

	// NBTの読み込み
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.bookInv.deserializeNBT(tag.getCompound("bookInv"));
		this.inputInv.deserializeNBT(tag.getCompound("inputInv"));
		this.outInv.deserializeNBT(tag.getCompound("outInv"));
		this.isCraft = tag.getBoolean("isCraft");
		this.craftTime = tag.getInt("craftTime");
		this.craftTick = tag.getInt("craftTick");
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
	public IItemHandler getOut() {
		return this.outInv;
	}

	// 素材スロットのアイテムを取得
	public ItemStack getOutItem() {
		return this.getOut().getStackInSlot(0);
	}

	// クラフト描画量を計算するためのメソッド
	public int getCraftProgress(int value) {
		return Math.min(value, (int) (value * (float) (this.craftTime) / (float) (MAX_CRAFT_TIME)));
	}

	public void spawnParticl() {

		BlockPos pos = this.getBlockPos();

		for (int i = 0; i < this.craftTime / 6; i++) {

			float randX = this.getRandFloat();
			float randY = this.getRandFloat();
			float randZ = this.getRandFloat();
			float x = pos.getX() + 0.5F + randX;
			float y = pos.getY() + 1.1F + randY;
			float z = pos.getZ() + 0.5F + randZ;
			float xSpeed = -randX * 0.075F;
			float ySpeed = -randY * 0.075F;
			float zSpeed = -randZ * 0.075F;
			this.addParticle(ParticleInit.NORMAL, x, y, z, xSpeed, ySpeed, zSpeed);
		}
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new MagiaRewriteMenu(windowId, inv, this);
	}

	// インベントリのアイテムを取得
	public List<ItemStack> getInvList() {
		List<ItemStack> stackList = new ArrayList<>();
		this.addStackList(stackList, this.getInputItem());
		this.addStackList(stackList, this.getOutItem());
		this.addStackList(stackList, this.getBookItem());
		return stackList;
	}

	// ブロック内の情報が空かどうか
	public boolean isInfoEmpty() {
		return this.getInvList().isEmpty() && this.isMFEmpty();
	}
}
