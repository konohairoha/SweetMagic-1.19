package sweetmagic.init.block.magic;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import sweetmagic.init.TileInit;
import sweetmagic.init.block.base.BaseMFBlock;
import sweetmagic.init.tile.sm.TileAbstractSM;
import sweetmagic.init.tile.sm.TileMFTable;
import sweetmagic.init.tile.sm.TileMFTableAdvanced;
import sweetmagic.init.tile.sm.TileMFTableMaster;

public class MFTable extends BaseMFBlock {

	private final int data;

	public MFTable(String name, int data) {
		super(name);
		this.data = data;
	}

	// ブロックでのアクション
	public boolean actionBlock(Level world, BlockPos pos, Player player, ItemStack stack) {
		if (world.isClientSide()) { return false; }
		this.openGUI(world, pos, player, this.getTile(world, pos));
		return true;
	}

	// 最大MFの取得
	public int getMaxMF() {
		switch (this.data) {
		case 1  : return 400000;
		case 2  : return 8000000;
		default:  return 20000;
		}
	}

	public int getTier() {
		return this.data + 1;
	}

	public boolean keepTileInfo() {
		return true;
	}

	public ItemStack inheritingNBT(ItemStack oldStack, ItemStack newStack) {

		// NBTを持っていたらNBTの移行
		if (oldStack.getOrCreateTag().contains("BlockEntityTag")) {

			CompoundTag tags = oldStack.getTagElement("BlockEntityTag");

			// 移行前のスロットサイズとスロットの取得
			int oldSlotSize = this.data == 0 ? 1 : 4;
			ItemStackHandler oldInputInv = new ItemStackHandler(oldSlotSize);
			oldInputInv.deserializeNBT(tags.getCompound("wandInv"));

			// 移行後のスロットサイズとスロットの取得
			int newSlotSize = this.data == 0 ? 4 : 6;
			ItemStackHandler newInputInv = new ItemStackHandler(newSlotSize);

			// 移行前のアイテムを移行後に移送
			for (int i = 0; i < oldSlotSize; i++) {
				newInputInv.setStackInSlot(i, oldInputInv.getStackInSlot(i));
			}

			// 古いNBTを除去して新しく登録
			tags.remove("wandInv");
			tags.put("wandInv", newInputInv.serializeNBT());

			CompoundTag newTags = new CompoundTag();
			newTags.put("BlockEntityTag", tags);
			newTags.putInt("mf", oldStack.getTag().getInt("mf"));
			newStack.setTag(newTags);
		}

		return newStack;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		switch (this.data) {
		case 1:  return new TileMFTableAdvanced(pos, state);
		case 2:  return new TileMFTableMaster(pos, state);
		default: return new TileMFTable(pos, state);
		}
	}

	public BlockEntityType<? extends TileAbstractSM> getTileType() {
		switch (this.data) {
		case 1: return TileInit.tableAdavance;
		case 2: return TileInit.tableMaster;
		default: return TileInit.table;
		}
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return this.createMailBoxTicker(world, type, this.getTileType());
	}

	public void addTip (List<Component> toolTip, ItemStack stack, CompoundTag tags) {
		toolTip.add(this.getTipArray(this.getText("mftable"), GREEN));
	}
}
