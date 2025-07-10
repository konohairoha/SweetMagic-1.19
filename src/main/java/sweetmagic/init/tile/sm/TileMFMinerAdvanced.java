package sweetmagic.init.tile.sm;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;
import sweetmagic.init.TagInit;
import sweetmagic.init.TileInit;
import sweetmagic.init.tile.menu.MFMinerAdvancedMenu;
import sweetmagic.util.ItemHelper;

public class TileMFMinerAdvanced extends TileSMMagic {

	public int craftTime = 0;
	public int maxMagiaFlux = 100000;
	public static final int MAX_CRAFT_TIME = 10;
	public boolean isCraft = false;
	public ItemStack outStack = ItemStack.EMPTY;
	protected final StackHandler inputInv = new StackHandler(10);
	protected final StackHandler outInv = new StackHandler(this.getInvSize());

	public TileMFMinerAdvanced(BlockPos pos, BlockState state) {
		this(TileInit.mfMinerAdvanced, pos, state);
	}

	public TileMFMinerAdvanced(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.resolver = new InOutHandlerProvider(this.inputInv, this.outInv);
	}

	// サーバー側処理
	@Override
	public void serverTick(Level world, BlockPos pos, BlockState state) {
		super.serverTick(world, pos, state);
		if (this.tickTime % 10 != 0 || this.isRSPower()) { return; }

		if (!this.isCraft) {
			this.craftStart();
		}

		else if (this.craftTime++ >= MAX_CRAFT_TIME) {
			this.craftEnd();
		}

		this.sendInfo();
	}

	public void craftStart() {
		ItemHelper.compactSimpleInventory(this.inputInv);
		ItemStack stack = this.getInputItem(0);
		if (stack.isEmpty() || !stack.is(Tags.Items.COBBLESTONE) || stack.getCount() < 64) { return; }

		int mf = this.getMF();
		if (mf < this.getNeedMF()) { return; }

		List<ItemStack> oresList = this.getList(ForgeRegistries.ITEMS.tags().getTag(Tags.Items.ORES).stream());
		ItemStack oreStack = oresList.get(this.rand.nextInt(oresList.size()));
		if (oreStack.isEmpty()) { return; }

		oreStack = oreStack.is(TagInit.COSMIC_ORE) ? new ItemStack(Blocks.IRON_ORE) : oreStack;

		if (oreStack.is(Tags.Items.ORES_NETHERITE_SCRAP) && this.rand.nextFloat() > 0.001F) {
			oreStack = new ItemStack(Blocks.GOLD_ORE);
		}

		if (!ItemHelper.insertStack(this.getOut(), oreStack.copy(), true).isEmpty()) { return; }

		this.isCraft = true;
		this.outStack = oreStack;
		stack.shrink(64);
		this.setMF(mf - this.getNeedMF());
		this.sendInfo();
	}

	public void craftEnd() {
		ItemHelper.insertStack(this.getOut(), this.outStack.copy(), false);
		this.outStack = ItemStack.EMPTY;

		ItemHelper.compactSimpleInventory(this.inputInv);
		ItemStack stack = this.getInputItem(0);
		this.isCraft = !stack.isEmpty() && stack.is(Tags.Items.COBBLESTONE) && stack.getCount() >= 64;
		this.craftTime = this.isCraft ? MAX_CRAFT_TIME - 4 : 0;
		this.isCraft = false;

		this.playSound(this.getBlockPos(), SoundEvents.AMETHYST_BLOCK_BREAK, 0.5F, 1F);
		this.sendInfo();
	}

	public List<ItemStack> getList(Stream<Item> stream) {
		return stream.map(Item::getDefaultInstance).collect(Collectors.toList());
	}

	// インベントリサイズの取得
	@Override
	public int getInvSize() {
		return 27;
	}

	// 最大MFの取得
	@Override
	public int getMaxMF() {
		return this.maxMagiaFlux;
	}

	// 受信するMF量の取得
	@Override
	public int getReceiveMF() {
		return 5000;
	}

	// 必要MF
	public int getNeedMF() {
		return 1000;
	}

	// 入力スロットの取得
	public IItemHandler getInput() {
		return this.inputInv;
	}

	// 入力スロットのアイテムを取得
	public ItemStack getInputItem(int i) {
		return this.getInput().getStackInSlot(i);
	}

	// 出力スロットの取得
	public IItemHandler getOut() {
		return this.outInv;
	}

	// 出力スロットのアイテムを取得
	public ItemStack getOutItem(int i) {
		return this.getOut().getStackInSlot(i);
	}

	// NBTの書き込み
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put("inputInv", this.inputInv.serializeNBT());
		tag.put("outInv", this.outInv.serializeNBT());
		tag.putInt("craftTime", this.craftTime);
		tag.putBoolean("isCraft", this.isCraft);
		tag.put("outStack", this.outStack.save(new CompoundTag()));
	}

	// NBTの読み込み
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.inputInv.deserializeNBT(tag.getCompound("inputInv"));
		this.outInv.deserializeNBT(tag.getCompound("outInv"));
		this.craftTime = tag.getInt("craftTime");
		this.isCraft = tag.getBoolean("isCraft");
		this.outStack = ItemStack.of(tag.getCompound("outStack"));
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new MFMinerAdvancedMenu(windowId, inv, this);
	}

	// RS信号で動作を停止するかどうか
	public boolean isRSStop() {
		return true;
	}

	// クラフト描画量を計算するためのメソッド
	public int getCraftProgress(int value) {
		return this.getProgress(value, this.craftTime, MAX_CRAFT_TIME);
	}

	// インベントリのアイテムを取得
	public List<ItemStack> getInvList() {
		List<ItemStack> stackList = new ArrayList<>();

		for (int i = 0; i < this.getInvSize(); i++) {
			this.addStackList(stackList, this.getOutItem(i));
		}

		for (int i = 0; i < 9; i++) {
			this.addStackList(stackList, this.getInputItem(i));
		}

		return stackList;
	}

	// ブロック内の情報が空かどうか
	public boolean isInfoEmpty() {
		return this.getInvList().isEmpty() && this.isMFEmpty();
	}
}
