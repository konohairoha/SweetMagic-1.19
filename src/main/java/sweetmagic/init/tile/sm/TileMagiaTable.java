package sweetmagic.init.tile.sm;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import sweetmagic.api.iitem.IMagicItem;
import sweetmagic.api.iitem.info.MagicInfo;
import sweetmagic.init.BlockInit;
import sweetmagic.init.ItemInit;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.SoundInit;
import sweetmagic.init.TileInit;
import sweetmagic.init.tile.menu.MagiaTableMenu;

public class TileMagiaTable extends TileSMMagic {

	public int time;
	public float flip;
	public float oFlip;
	public float flipT;
	public float flipA;
	public float open;
	public float oOpen;
	public boolean isCraft = false;
	public boolean canCraft = false;
	public int craftTime = 0;
	public int maxCraftTime = 10;
	public int maxMagiaFlux = 100000;
	public ItemStack copyMagic = ItemStack.EMPTY;
	protected final StackHandler inputInv = new StackHandler(1, true);
	protected final StackHandler outputInv = new StackHandler(1, true);
	protected final StackHandler subInv = new StackHandler(6);

	public TileMagiaTable(BlockPos pos, BlockState state) {
		super(TileInit.magiaTable, pos, state);
	}

	public TileMagiaTable(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	// サーバー側処理
	@Override
	public void serverTick(Level world, BlockPos pos, BlockState state) {
		super.serverTick(world, pos, state);
		if (this.tickTime % 10 != 0) { return; }

		ItemStack stack = this.getInputItem();

		if (!this.isCraft) {
			if(stack.isEmpty()) { return; }
			this.canCraft = this.craftStart(stack, true);
			this.sendInfo();
			return;
		}

		if (this.craftTime++ >= this.maxCraftTime) {
			this.craftFinish(stack);
		}

		else if(this.craftTime % 2 == 0 && this.craftTime <= this.maxCraftTime - 2) {
			this.playSound(pos, SoundInit.TURN_PAGE, 0.1F, 1F);
		}

		this.sendInfo();
	}

	public boolean craftStart(ItemStack stack, boolean isSum) {
		if(stack.isEmpty()) { return false; }

		Item item = stack.getItem();
		if(!(item instanceof IMagicItem magic) || magic.isUniqueMagic()) { return false; }
		if(!ItemHandlerHelper.insertItemStacked(this.getOutput(), stack.copy(), true).isEmpty()) { return false; }

		MagicInfo info = new MagicInfo(stack);
		if (this.getMF() < this.getRequestMF(info)) { return false; }

		List<ItemStack> requestList = this.getRequestList(info);
		List<ItemStack> stackList = new ArrayList<>();

		for (int i = 0; i < 5; i++) {
			ItemStack sub = this.getSubItem(i);
			if(sub.isEmpty()) { continue; }
			stackList.add(sub);
		}

		List<ItemStack> resultList = new ArrayList<>();
		List<Integer> slotIdList = new ArrayList<>();

		// 中分類（大分類に入っているアイテムごと）
		for (ItemStack ingStack : requestList) {

			boolean isChecked = false;

			// 小分類(インベントリアイテム)
			for (int i = 0; i < stackList.size(); i++) {

				if (slotIdList.contains(i)) { continue; }

				ItemStack s = stackList.get(i);

				// アイテムが一致して要求個数以上なら検索完了
				if (ingStack.is(s.getItem()) && s.getCount() >= ingStack.getCount()) {
					slotIdList.add(i);
					resultList.add(ingStack);
					isChecked = true;
					break;
				}
			}

			if (!isChecked) { return false; }
		}

		if(isSum) { return true; }

		this.isCraft = true;
		this.craftTime = 0;
		this.maxCraftTime = this.getMaxCraftTime(info);
		this.setMF(this.getMF() - this.getRequestMF(info));
		this.copyMagic = stack.copy();
		this.clickButton();
		this.sendPKT();

		for (ItemStack ingStack : resultList) {

			// 小分類(インベントリアイテム)
			for (int i = 0; i < stackList.size(); i++) {

				ItemStack s = stackList.get(i);

				// アイテムが一致して要求個数以上なら検索完了
				if (ingStack.is(s.getItem()) && s.getCount() >= ingStack.getCount()) {
					s.shrink(ingStack.getCount());
				}
			}
		}
		return true;
	}

	public void craftFinish(ItemStack stack) {
		if(!ItemHandlerHelper.insertItemStacked(this.getOutput(), this.copyMagic, true).isEmpty()) { return; }
		ItemHandlerHelper.insertItemStacked(this.getOutput(), this.copyMagic, false);
		this.isCraft = false;
		this.craftTime = 0;
		this.copyMagic = ItemStack.EMPTY;
		this.playSound(this.getBlockPos(), SoundInit.WRITE, 0.1F, 1F);
		this.sendPKT();
	}

	// クライアント側処理
	public void clientTick(Level world, BlockPos pos, BlockState state) {
		super.clientTick(world, pos, state);
		this.oOpen = this.open;
		this.open += this.isCraft ? 0.1F : -0.1F;
		this.open = Mth.clamp(this.open, 0F, 1F);
		++this.time;
		this.oFlip = this.flip;
		float f = (this.flipT - this.flip) * 0.4F;
		f = Mth.clamp(f, -0.2F, 0.2F);
		this.flipA += (f - this.flipA) * 0.9F;
		this.flip += this.flipA;

		if (this.isCraft && this.tickTime % 2 == 0) {
			this.spawnParticle(world, pos);

			if(this.tickTime % 20 == 0) {
				this.flip = -2F;
			}
		}
	}

	// パーティクルスポーン
	public void spawnParticle(Level world, BlockPos pos) {

		float posX = pos.getX() + 0.5F;
		float posY = pos.getY() + 1.25F;
		float posZ = pos.getZ() + 0.5F;
		ParticleOptions par = ParticleInit.CYCLE_LIGHT;

		for (int i = 0; i < 8; i++) {
			this.spawnParticleCycle(world, par, posX, posY, posZ, 0.67D, i * 32F);
		}

		par = ParticleInit.DIVINE;

		for (int i = 0; i < 2; i++) {
			world.addParticle(par, posX + this.getRandFloat(0.2F), posY, posZ + this.getRandFloat(0.2F), this.getRandFloat(0.075F), 0.1F, this.getRandFloat(0.075F));
		}
	}

	// パーティクルスポーンサイクル
	protected void spawnParticleCycle(Level world, ParticleOptions par, double x, double y, double z, double range, double angle) {
		Direction face = Direction.UP;
		world.addParticle(par, x, y, z, face.get3DDataValue(), range, angle + 6F - this.tickTime * 5);
	}

	public Item getMainRequestItem(MagicInfo info) {
		switch(info.getMagicItem().getElement()) {
		case FLAME: return ItemInit.fire_nasturtium_petal;
		case FROST: return ItemInit.unmeltable_ice;
		case CYCLON: return ItemInit.tiny_feather;
		case LIGHTNING: return ItemInit.electronic_orb;
		case EARTH: return ItemInit.sugarbell;
		case SHINE: return ItemInit.prizmium;
		case WATER: return ItemInit.dm_flower;
		case GRAVITY: return ItemInit.grav_powder;
		case BLAST: return ItemInit.magic_meal;
		case TOXIC: return ItemInit.poison_bottle;
		case TIME: return ItemInit.clero_petal;
		case ALL: return ItemInit.mf_small_bottle;
		default: return ItemInit.aether_crystal;
		}
	}

	public int getMainRequestSize(MagicInfo info) {
		switch(info.getMagicItem().getTier()) {
		case 0: return 3;
		case 1: return 8;
		case 2: return 12;
		case 3: return 24;
		default: return 32;
		}
	}

	public int getMaxCraftTime(MagicInfo info) {
		switch(info.getMagicItem().getTier()) {
		case 0: return 6;
		case 1: return 10;
		case 2: return 16;
		case 3: return 24;
		default: return 40;
		}
	}

	public List<ItemStack> getRequestList(MagicInfo info) {
		List<ItemStack> stackList = new ArrayList<>();
		stackList.add(new ItemStack(this.getMainRequestItem(info), this.getMainRequestSize(info)));

		switch(info.getMagicItem().getTier()) {
		case 0:
			stackList.add(new ItemStack(ItemInit.blank_magic));
			stackList.add(new ItemStack(ItemInit.blank_page));
			break;
		case 1:
			stackList.add(new ItemStack(ItemInit.blank_magic));
			stackList.add(new ItemStack(ItemInit.blank_page, 2));
			break;
		case 2:
			stackList.add(new ItemStack(ItemInit.blank_magic, 2));
			stackList.add(new ItemStack(ItemInit.blank_page, 3));
			break;
		case 3:
			stackList.add(new ItemStack(ItemInit.blank_magic, 3));
			stackList.add(new ItemStack(ItemInit.blank_page, 4));
			break;
		case 4:
			stackList.add(new ItemStack(ItemInit.blank_magic, 4));
			stackList.add(new ItemStack(ItemInit.blank_page, 5));
			stackList.add(new ItemStack(BlockInit.magiaflux_block));
			break;
		}

		return stackList;
	}

	public int getRequestMF(MagicInfo info) {
		switch(info.getMagicItem().getTier()) {
		case 0: return 2000;
		case 1: return 5000;
		case 2: return 25000;
		default: return 100000;
		}
	}

	// クラフト描画量を計算するためのメソッド
	public int getCraftProgress(int value) {
		return Math.min(value, (int) (value * (float) (this.craftTime) / (float) (this.maxCraftTime)));
	}

	// NBTの書き込み
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put("inputInv", this.inputInv.serializeNBT());
		tag.put("outputInv", this.outputInv.serializeNBT());
		tag.put("subInv", this.subInv.serializeNBT());
		tag.put("copyMagic", this.copyMagic.save(new CompoundTag()));
		tag.putInt("craftTime", this.craftTime);
		tag.putInt("maxCraftTime", this.maxCraftTime);
		tag.putBoolean("isCraft", this.isCraft);
		tag.putBoolean("canCraft", this.canCraft);
	}

	// NBTの読み込み
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.inputInv.deserializeNBT(tag.getCompound("inputInv"));
		this.outputInv.deserializeNBT(tag.getCompound("outputInv"));
		this.subInv.deserializeNBT(tag.getCompound("subInv"));
		this.copyMagic = ItemStack.of(tag.getCompound("copyMagic"));
		this.craftTime = tag.getInt("craftTime");
		this.maxCraftTime = tag.getInt("maxCraftTime");
		this.isCraft = tag.getBoolean("isCraft");
		this.canCraft = tag.getBoolean("canCraft");
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
		return 6;
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
	public IItemHandler getOutput() {
		return this.outputInv;
	}

	// 素材スロットのアイテムを取得
	public ItemStack getOutItem() {
		return this.getOutput().getStackInSlot(0);
	}

	// 素材スロットの取得
	public IItemHandler getSub() {
		return this.subInv;
	}

	// 素材スロットのアイテムを取得
	public ItemStack getSubItem(int i) {
		return this.getSub().getStackInSlot(i);
	}

	// インベントリのアイテムを取得
	public List<ItemStack> getInvList() {
		List<ItemStack> stackList = new ArrayList<>();
		this.addStackList(stackList, this.getInputItem());
		this.addStackList(stackList, this.getOutItem());

		for(int i = 0; i < this.getInvSize(); i++)
			this.addStackList(stackList, this.getSubItem(i));
		return stackList;
	}

	// ブロック内の情報が空かどうか
	public boolean isInfoEmpty() {
		return this.getInvList().isEmpty() && this.isMFEmpty();
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new MagiaTableMenu(windowId, inv, this);
	}
}
