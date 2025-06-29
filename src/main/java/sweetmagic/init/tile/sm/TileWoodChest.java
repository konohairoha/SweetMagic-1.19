package sweetmagic.init.tile.sm;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.IItemHandler;
import sweetmagic.init.TileInit;
import sweetmagic.init.block.sm.WoodChest;
import sweetmagic.init.tile.menu.WoodChestMenu;

public class TileWoodChest extends TileAbstractSM {

	public int count = 4;
	public float chance = 0.825F;
	public ResourceLocation lootTable = null;
	public final StackHandler inputInv = new StackHandler(this.getInvSize(), true);

	public TileWoodChest(BlockPos pos, BlockState state) {
		this(TileInit.woodChest, pos, state);
	}

	public TileWoodChest(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.resolver = new SingleHandlerProvider(this.inputInv, IN_OUT);
	}

	// NBTの書き込み
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.merge(this.inputInv.serializeNBT());

		if (this.lootTable != null) {
			tag.putString("LootTable", this.lootTable.toString());
			tag.putInt("count", this.count);
			tag.putFloat("chance", this.chance);
		}
	}

	// NBTの読み込み
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.inputInv.deserializeNBT(tag);

		if (tag.contains("LootTable")) {
			this.lootTable = new ResourceLocation(tag.getString("LootTable"));
			this.count = tag.getInt("count");
			this.chance = tag.getFloat("chance");
		}
	}

	public void setLootInv(@Nullable Player player) {
		if (this.lootTable == null) { return; }

		RandomSource rand = this.getLevel().getRandom();
		LootContext.Builder builder = (new LootContext.Builder((ServerLevel)this.getLevel())).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(this.worldPosition)).withOptionalRandomSeed(rand.nextLong());
 		List<ItemStack> stackList = new ArrayList<>();

 		for (int i = 0; i < this.count; i++ ) {
	 		stackList.addAll(this.getLevel().getServer().getLootTables().get(this.lootTable).getRandomItems(builder.create(LootContextParamSets.CHEST)));
 		}

		IItemHandler handler = this.getItemHandler(this, Direction.UP);

		for (int i = 0; i < this.getInvSize(); i++) {
			if (rand.nextFloat() < this.chance) { continue; }

			for (int s = 0; s < stackList.size(); s++) {

				ItemStack stack = stackList.get(s);
				if (stack.isEmpty()) { continue; }

				ItemStack result = handler.insertItem(i, stack.copy(), true);
				if (!result.isEmpty()) { continue; }

				handler.insertItem(i, stack.copy(), false);
				stackList.remove(s);
				break;
			}
		}

		this.lootTable = null;
		this.sendPKT();
	}

	// インベントリサイズの取得
	@Override
	public int getInvSize() {
		return 104;
	}

	// スロットの取得
	public IItemHandler getInput() {
		return this.inputInv;
	}

	// スロットのアイテムを取得
	public ItemStack getInputItem(int i) {
		return this.getInput().getStackInSlot(i);
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new WoodChestMenu(windowId, inv, this);
	}

	// ブロック内の情報が空かどうか
	public boolean isInfoEmpty() {
		return this.getInputList().isEmpty() && this.lootTable == null;
	}

	public List<ItemStack> getInputList() {
		List<ItemStack> stackList = new ArrayList<>();

		for (int i = 0; i < this.getInvSize(); i++) {
			this.addStackList(stackList, this.getInputItem(i));
		}

		return stackList;
	}

	public List<ItemStack> getInvList() {
		List<ItemStack> stackList = new ArrayList<>();

		for (int i = 0; i < this.getInvSize(); i++) {
			stackList.add(this.getInputItem(i));
		}

		return stackList;
	}

	public void invTrash(boolean isRS) {

		boolean isTrash = isRS;

		for (int i = 0; i < this.getInvSize(); i++) {

			ItemStack stack = this.getInputItem(i);
			if (stack.isEmpty()) { continue; }

			stack.shrink(stack.getCount());
			isTrash = true;
		}

		if (isTrash) {
			this.playSound(this.getBlockPos(), SoundEvents.SAND_BREAK, 0.0625F, 1F);
		}

		else {
			this.clickButton();
		}
	}

	public int getData() {
		return ((WoodChest) this.getBlock(this.getBlockPos())).data;
	}
}
