package sweetmagic.init.tile.sm;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import sweetmagic.api.iitem.IAcce;
import sweetmagic.api.iitem.info.AcceInfo;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.SoundInit;
import sweetmagic.init.TileInit;
import sweetmagic.init.tile.menu.AccessoryTableMenu;

public class TileAccessoryTable extends TileSMMagic {

	public int maxCraftTime = 10;
	public int craftTime = 0;
	public boolean isCraft = false;
	public ItemStack outStack = ItemStack.EMPTY;
	public int maxMagiaFlux = 100000;				// 最大MF量を設定

	protected final StackHandler inputInv = new StackHandler(this.getInvSize());
	protected final StackHandler acceInv = new StackHandler(this.getInvSize());
	protected final StackHandler starInv = new StackHandler(this.getInvSize());
	protected final StackHandler outputInv = new StackHandler(this.getInvSize());

	public TileAccessoryTable(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public TileAccessoryTable(BlockPos pos, BlockState state) {
		super(TileInit.accessoryProcessing, pos, state);
	}

	// サーバー側処理
	@Override
	public void serverTick(Level world, BlockPos pos, BlockState state) {
		super.serverTick(world, pos, state);

		if (this.isCraft && this.tickTime % 30 == 12 && (this.craftTime < this.maxCraftTime - 2)) {
			this.playSound(pos, SoundInit.IRON, 0.25F, 1F);
		}

		if (this.tickTime % 10 != 0) { return; }

		// 作成中
		if (this.isCraft) {

			// 一定時間が経てばクラフトの完成
			if (this.craftTime++ >= this.maxCraftTime) {
				this.craftFinish();
			}

			this.sendPKT();
		}
	}

	// クライアント側処理
	public void clientTick(Level world, BlockPos pos, BlockState state) {
		super.clientTick(world, pos, state);
		if (!this.isCraft || this.tickTime % 30 != 12 || (this.craftTime >= this.maxCraftTime - 3) ) { return; }

		this.craftParticle(world, pos, 0.45F, 0.0F);
		this.craftParticle(world, pos, 0.0F, 0.45F);
		this.craftParticle(world, pos, -0.45F, 0.0F);
		this.craftParticle(world, pos, 0.0F, -0.45F);

		float x = pos.getX() + 0.5F;
		float y = pos.getY() + 1.025F;
		float z = pos.getZ() + 0.5F;

		for (int i= 0; i < 8; i++) {
			float xSpeed = this.getRandFloat(0.1F);
			float ySpeed = this.getRandFloat(0.05F);
			float zSpeed = this.getRandFloat(0.1F);
			this.level.addParticle(ParticleInit.NORMAL.get(), x, y, z, xSpeed, ySpeed, zSpeed);
		}
	}

	public void craftParticle (Level world, BlockPos pos, float posX, float posZ) {

		float x = pos.getX() + 0.5F + posX;
		float y = pos.getY() + 1.025F;
		float z = pos.getZ() + 0.5F + posZ;
		float xSpeed = -posX * 0.08F;
		float ySpeed = 0F;
		float zSpeed = -posZ * 0.08F;

		this.level.addParticle(ParticleInit.TWILIGHTLIGHT.get(), x, y, z, xSpeed, ySpeed, zSpeed);
	}

	public boolean canCraft () {

		// メインスロットが空なら終了
		ItemStack input = this.getInputItem();
		if (input.isEmpty()) { return false; }

		// 装備品スロットが空なら終了
		ItemStack sub = this.getAcceItem();
		if (sub.isEmpty()) { return false; }

		// 星なる光スロットが空なら終了
		ItemStack star = this.getStarItem();
		if (star.isEmpty()) { return false; }

		// 出力スロットが空でないなら終了
		ItemStack out = this.getOutputItem();
		if (!out.isEmpty()) { return false; }

		// アイテムが一致していなかったら終了
		if (input.getItem() != sub.getItem()) { return false; }

		// アクセサリーが重複不可または強化不可なら終了
		AcceInfo inputInfo = new AcceInfo(input);
		IAcce acce = inputInfo.getAcce();
		int starCount = star.getCount();					// 星なる光の数を取得
		int stackCount = acce.getStackCount(inputInfo);	// アクセサリーのスタック数を取得

		// 星なる光の数が足りないまたはMFが足りないなら終了
		if (stackCount > starCount || ( stackCount * 10000 ) > this.getMF()) { return false; }
		return true;
	}

	// 作成開始
	public void craftStart () {
		ItemStack input = this.getInputItem();
		AcceInfo acceInfo = new AcceInfo(input);
		int stackCount = acceInfo.getAcce().getStackCount(acceInfo);
		acceInfo.getAcce().addStackCount(acceInfo);
		this.outStack = input.copy();
		input.shrink(1);
		this.getAcceItem().shrink(1);
		this.isCraft = true;
		this.craftTime = 0;
		this.tickTime = 0;
		this.maxCraftTime = stackCount * 4;
		this.setMF(this.getMF() - stackCount * 10000);
		this.getStarItem().shrink(stackCount);
		this.sendPKT();
		this.clickButton();
	}

	// クラフトの完成
	public void craftFinish () {
		ItemHandlerHelper.insertItemStacked(this.getOutput(), this.outStack.copy(), false);
		this.clearInfo();
		this.playSound(this.getBlockPos(), SoundEvents.ANVIL_USE, 0.25F, 1F);
	}

	// 初期化
	public void clearInfo () {
		this.craftTime = 0;
		this.maxCraftTime = 10;
		this.isCraft = false;
		this.outStack = ItemStack.EMPTY;
		this.sendPKT();
	}

	public String getTip () {
		String tip = "";

		// メインスロット、装備品スロットが空なら終了
		ItemStack input = this.getInputItem();
		ItemStack sub = this.getAcceItem();
		if (input.isEmpty() || sub.isEmpty()) { return "acce_noacce"; }

		// アイテムが一致していなかったら終了
		if (input.getItem() != sub.getItem()) { return "acce_noequal"; }

		ItemStack star = this.getStarItem();
		AcceInfo inputInfo = new AcceInfo(input);
		int starCount = star.getCount();									// 星なる光の数を取得
		int stackCount = inputInfo.getAcce().getStackCount(inputInfo) + 1;	// アクセサリーのスタック数を取得

		// 星なる光がないなら
		if (star.isEmpty() || stackCount > starCount) { return "acce_nostar"; }

		// 出力スロットが空でないなら終了
		ItemStack out = this.getOutputItem();
		if (!out.isEmpty()) { return "acce_noempty"; }

		if (( stackCount * 10000 ) > this.getMF()) { return "acce_nomf"; }

		return tip;
	}

	// インベントリサイズの取得
	@Override
	public int getInvSize () {
		return 1;
	}

	// 最大MFの取得
	@Override
	public int getMaxMF () {
		return this.maxMagiaFlux;
	}

	// 受信するMF量の取得
	public int getReceiveMF () {
		return 10000;
	}

	// 素材スロットの取得
	public IItemHandler getInput() {
		return this.inputInv;
	}

	// 素材スロットのアイテムを取得
	public  ItemStack getInputItem() {
		return this.getInput().getStackInSlot(0);
	}

	// 素材スロットの取得
	public IItemHandler getAcce() {
		return this.acceInv;
	}

	// 素材スロットのアイテムを取得
	public  ItemStack getAcceItem() {
		return this.getAcce().getStackInSlot(0);
	}

	// 星なる光スロットの取得
	public IItemHandler getStar() {
		return this.starInv;
	}

	// 星なる光スロットのアイテムを取得
	public  ItemStack getStarItem() {
		return this.getStar().getStackInSlot(0);
	}

	// 出力スロットの取得
	public IItemHandler getOutput() {
		return this.outputInv;
	}

	// 出力のアイテムを取得
	public  ItemStack getOutputItem() {
		return this.getOutput().getStackInSlot(0);
	}

	// NBTの書き込み
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put("inputInv", this.inputInv.serializeNBT());
		tag.put("acceInv", this.acceInv.serializeNBT());
		tag.put("starInv", this.starInv.serializeNBT());
		tag.put("outputInv", this.outputInv.serializeNBT());
		tag.putBoolean("isCraft", this.isCraft);
		tag.putInt("craftTime", this.craftTime);
		tag.putInt("maxCraftTime", this.maxCraftTime);
		tag.put("outPutStack", this.outStack.save(new CompoundTag()));
	}

	// NBTの読み込み
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.setInv(this.inputInv, tag, "inputInv");
		this.setInv(this.acceInv, tag, "acceInv");
		this.setInv(this.starInv, tag, "starInv");
		this.setInv(this.outputInv, tag, "outputInv");
		this.isCraft = tag.getBoolean("isCraft");
		this.craftTime = tag.getInt("craftTime");
		this.maxCraftTime = tag.getInt("maxCraftTime");
		this.outStack = ItemStack.of(tag.getCompound("outPutStack"));
	}

	public void setInv (StackHandler inv, CompoundTag tags, String name) {
		CompoundTag tag = tags.getCompound(name);
		if (tag == null) { return; }

		inv.deserializeNBT(tag);
	}

	// MFゲージの描画量を計算するためのメソッド
	public int getProgressScale(int value) {
		return Math.min(value, (int) (value * (float) (this.craftTime) / (float) (this.maxCraftTime)));
    }

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new AccessoryTableMenu(windowId, inv, this);
	}

	// インベントリのアイテムを取得
	public List<ItemStack> getInvList () {
		List<ItemStack> stackList = new ArrayList<>();
		this.addStackList(stackList, this.getInputItem());
		this.addStackList(stackList, this.getAcceItem());
		this.addStackList(stackList, this.getStarItem());
		this.addStackList(stackList, this.getOutputItem());
		return stackList;
	}
}
