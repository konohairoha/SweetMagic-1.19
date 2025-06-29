package sweetmagic.init.tile.sm;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import sweetmagic.api.iitem.IWand;
import sweetmagic.api.iitem.info.WandInfo;
import sweetmagic.init.SoundInit;
import sweetmagic.init.TileInit;
import sweetmagic.init.block.sm.IrisCreation;
import sweetmagic.init.tile.inventory.SMInventory.SMWandInventory;
import sweetmagic.init.tile.menu.IrisCreationMenu;
import sweetmagic.recipe.iris.IrisRecipe;
import sweetmagic.util.ItemHelper;

public class TileIrisCreation extends TileAbstractSM {

	private int maxCraftTime = 16;
	public int craftTime = 0;
	public boolean isCraft = false;
	public ItemStack outStack = ItemStack.EMPTY;
	public List<ItemStack> craftList = new ArrayList<>();
	protected final StackHandler handInv = new StackHandler(1);
	protected final StackHandler inputInv = new StackHandler(this.getInvSize());
	protected final StackHandler outputInv = new StackHandler(1);

	public TileIrisCreation(BlockPos pos, BlockState state) {
		this(TileInit.iris, pos, state);
	}

	public TileIrisCreation(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.resolver = new InOutHandlerProvider(this.inputInv, this.outputInv);
	}

	// サーバー側処理
	public void serverTick(Level level, BlockPos pos, BlockState state) {
		super.serverTick(level, pos, state);
		if (this.tickTime % 10 != 0) { return; }

		if (this.tickTime >= 20) {
			this.tickTime = 0;

			if (this.isCraft && (this.craftTime + 18) >= this.maxCraftTime ) {
				this.playSound(pos, SoundInit.POT, 0.1F, 1F);
			}
		}

		// 作成中で
		if (this.isCraft) {

			// 一定時間が経てばクラフトの完成
			if (this.craftTime++ >= this.maxCraftTime) {
				this.craftFinish();
			}

			this.sendPKT();
		}

		// メインスロットに何もなければ終了
		if (this.isCraft || this.getHandItem().isEmpty()) { return; }

		// レシピが見つかれば作成開始
		if (this.checkRecipe()) {
			this.craftStart();
		}
	}

	// クライアント側処理
	public void clientTick(Level world, BlockPos pos, BlockState state) {
		super.clientTick(world, pos, state);
		if (this.tickTime % 3 != 0 || !this.isCraft) { return; }

		this.tickTime = 0;
		boolean under = state.getValue(IrisCreation.UNDER);
		double addY = under ? 0.8D : 0.1D;

		for (int i = 0; i < this.rand.nextInt(4) + 1; i++) {
			double x = (double) pos.getX() + 0.5D + (this.rand.nextDouble() * 0.4D - 0.2D);
			double y = (double) pos.getY() + addY;
			double z = (double) pos.getZ() + 0.5D + (this.rand.nextDouble() * 0.4D - 0.2D);
			world.addParticle(ParticleTypes.BUBBLE_POP, x, y, z, 0D, 0D, 0D);
		}

		if (under && this.rand.nextFloat() >= 0.75F) {

			double x = (double) pos.getX() + 0.5D + (this.rand.nextDouble() * 0.4D - 0.2D);
			double y = pos.getY() + 0.9D;
			double z = (double) pos.getZ() + 0.5D + (this.rand.nextDouble() * 0.4D - 0.2D);
			double speedY = this.rand.nextDouble() * 0.015D;

			world.addAlwaysVisibleParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, true, x, y, z, 0D, 0.02D + speedY, 0D);
		}
	}

	// 素材の取得
	public List<ItemStack> getStackList() {

		List<ItemStack> stackList = new ArrayList<>();
		stackList.add(this.getHandItem());

		for (int i = 0; i < this.getInvSize(); i++) {

			ItemStack stack = this.getInputItem(i);
			if (stack.isEmpty()) { continue; }

			stackList.add(stack);
		}

		return stackList;
	}

	// レシピチェック
	public boolean checkRecipe() {
		return !IrisRecipe.getRecipe(this.getLevel(), this.getStackList()).isEmpty();
	}

	// 作成開始
	public void craftStart() {

		// レシピを取得して見つからなければ終了
		List<ItemStack> stackList = this.getStackList();
		IrisRecipe recipe = IrisRecipe.getRecipe(this.getLevel(), stackList).get();

		// レシピから完成品を取得
		ItemStack resultStack = recipe.getResultItem().copy();
		CompoundTag tags = this.getHandItem().getTag();

		if (tags != null) {

			Item outItem = resultStack.getItem();

			// 元の杖の魔法リスト
			List<ItemStack> invStackList = new ArrayList<>();

			// スロットの数を増やす
			if (outItem instanceof IWand wand) {
				tags.putInt(IWand.SLOTCOUNT, wand.getSlot());
				invStackList = wand.getMagicList(stackList.get(0));
			}

			// 出力アイテムにNBTを設定
			resultStack.setTag(tags);

			if (outItem instanceof IWand wand) {

				// インベントリを取得してスロット数を設定
				SMWandInventory inv = new SMWandInventory(new WandInfo(resultStack));
				inv.inv = new ItemStackHandler(wand.getSlot());

				// インベントリに元の杖の魔法リストのアイテムを突っ込む
				for (int i = 0; i < invStackList.size(); i++) {
					inv.insertItem(i, invStackList.get(i), false);
				}

				// NBTの保存
				inv.writeBack();
			}
		}

		if (!ItemHelper.insertStack(this.getOutput(), resultStack, true).isEmpty()) { return; }

		// クラフトで使うアイテムを入れておく
		this.craftList = new ArrayList<ItemStack>(recipe.getRequestList());
		this.outStack = resultStack;

		// 要求アイテムの消費
		for (ItemStack request: recipe.getRequestList()) {
			for (ItemStack stack : stackList) {
				if (request.is(stack.getItem())) {
					stack.shrink(request.getCount());
					break;
				}
			}
		}

		this.isCraft = true;
		this.maxCraftTime = this.getBlock(this.getBlockPos().below()) instanceof CampfireBlock ? 4 : 16;
		this.playSound(this.getBlockPos(), SoundInit.POT, 0.1F, 1F);
	}

	// クラフトの完成
	public void craftFinish() {
		ItemHelper.insertStack(this.getOutput(), this.outStack, false);
		this.clearInfo();
	}

	// 初期化
	public void clearInfo() {
		this.craftTime = 0;
		this.isCraft = false;
		this.outStack = ItemStack.EMPTY;
		this.craftList.clear();
	}

	// ブロック内の情報が空かどうか
	public boolean isInfoEmpty() {
		return this.getDropList().isEmpty();
	}

	// ドロップリストを取得
	public List<ItemStack> getDropList() {

		List<ItemStack> stackList = new ArrayList<>();
		this.addStackList(stackList, this.getHandItem());

		for (int i = 0; i < this.getInvSize(); i++) {
			this.addStackList(stackList, this.getInputItem(i));
		}

		this.addStackList(stackList, this.getOutputItem());
		stackList.addAll(this.craftList);

		return stackList;
	}

	// NBTの書き込み
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put("handInv", this.handInv.serializeNBT());
		tag.put("inputInv", this.inputInv.serializeNBT());
		tag.put("outputInv", this.outputInv.serializeNBT());
		tag.putBoolean("isCraft", this.isCraft);
		tag.putInt("craftTime", this.craftTime);
		tag.putInt("maxCraftTime", this.maxCraftTime);
		this.saveStackList(tag, this.craftList, "craftList");
		tag.put("outPutStack", this.outStack.save(new CompoundTag()));
	}

	// NBTの読み込み
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.handInv.deserializeNBT(tag.getCompound("handInv"));
		this.inputInv.deserializeNBT(tag.getCompound("inputInv"));
		this.outputInv.deserializeNBT(tag.getCompound("outputInv"));
		this.isCraft = tag.getBoolean("isCraft");
		this.craftTime = tag.getInt("craftTime");
		this.maxCraftTime = tag.getInt("maxCraftTime");
		this.craftList = this.loadAllStack(tag, "craftList");
		this.outStack = ItemStack.of(tag.getCompound("outPutStack"));
	}

	// インベントリサイズの取得
	@Override
	public int getInvSize() {
		return 8;
	}

	// メインスロットの取得
	public IItemHandler getHand() {
		return this.handInv;
	}

	// メインスロットのアイテムを取得
	public ItemStack getHandItem() {
		return this.getHand().getStackInSlot(0);
	}

	// 素材スロットの取得
	public IItemHandler getInput() {
		return this.inputInv;
	}

	// 素材スロットのアイテムを取得
	public ItemStack getInputItem(int i) {
		return this.getInput().getStackInSlot(i);
	}

	// 出力スロットの取得
	public IItemHandler getOutput() {
		return this.outputInv;
	}

	// 出力のアイテムを取得
	public ItemStack getOutputItem() {
		return this.getOutput().getStackInSlot(0);
	}

	// 描画量を計算するためのメソッド
	public int getProgress(int value) {
		return Math.min(value, (int) (value * (float) (this.craftTime) / (float) (this.maxCraftTime)));
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new IrisCreationMenu(windowId, inv, this);
	}

	// RS信号で動作を停止するかどうか
	public boolean isRSStop() {
		return true;
	}
}
