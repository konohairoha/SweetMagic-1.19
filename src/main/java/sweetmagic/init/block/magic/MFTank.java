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
import sweetmagic.init.tile.sm.TileMFTank;
import sweetmagic.init.tile.sm.TileMFTankAdavance;
import sweetmagic.init.tile.sm.TileMFTankCreative;
import sweetmagic.init.tile.sm.TileMFTankMaster;

public class MFTank extends BaseMFBlock {

	public final int data;

	public MFTank(String name, int data) {
		super(name);
		this.data = data;
	}

	// ブロックでのアクション
	public boolean actionBlock(Level world, BlockPos pos, Player player, ItemStack stack) {
		if (world.isClientSide()) { return true; }
		this.openGUI(world, pos, player, this.getTile(world, pos));
		return true;
	}

	// tierの取得
	public int getTier() {
		return this.data + 1;
	}

	// 最大MFの取得
	public int getMaxMF() {
		switch (this.data) {
		case 1: return 1000000;
		case 2: return 10000000;
		case 3: return Integer.MAX_VALUE;
		default: return 100000;
		}
	}

	public boolean keepTileInfo() {
		return true;
	}

	public ItemStack inheritingNBT(ItemStack oldStack, ItemStack newStack) {

		// NBTを持っていたらNBTの移行
		if (oldStack.getOrCreateTag().contains("BlockEntityTag")) {

			CompoundTag tags = oldStack.getTagElement("BlockEntityTag");

			// 移行前のスロットサイズとスロットの取得
			int oldInputSize = this.data == 0 ? 1 : 3;
			ItemStackHandler oldInputInv = new ItemStackHandler(oldInputSize);
			oldInputInv.deserializeNBT(tags.getCompound("inputInv"));

			// 移行後のスロットサイズとスロットの取得
			int newInputSize = this.data == 0 ? 3 : 5;
			ItemStackHandler newInputInv = new ItemStackHandler(newInputSize);

			// 移行前のアイテムを移行後に移送
			for (int i = 0; i < oldInputSize; i++) {
				newInputInv.setStackInSlot(i, oldInputInv.getStackInSlot(i));
			}

			// 古いNBTを除去して新しく登録
			tags.remove("inputInv");
			tags.put("inputInv", newInputInv.serializeNBT());

			// 移行前のスロットサイズとスロットの取得
			int oldOutSize = this.data == 0 ? 3 : 5;
			ItemStackHandler oldOutInv = new ItemStackHandler(oldOutSize);
			oldOutInv.deserializeNBT(tags.getCompound("outputInv"));

			// 移行後のスロットサイズとスロットの取得
			int newOutSize = this.data == 0 ? 5 : 10;
			ItemStackHandler newOutInv = new ItemStackHandler(newOutSize);

			// 移行前のアイテムを移行後に移送
			for (int i = 0; i < oldOutSize; i++) {
				newOutInv.setStackInSlot(i, oldOutInv.getStackInSlot(i));
			}

			// 古いNBTを除去して新しく登録
			tags.remove("outputInv");
			tags.put("outputInv", newOutInv.serializeNBT());

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
		case 1: return new TileMFTankAdavance(pos, state);
		case 2: return new TileMFTankMaster(pos, state);
		case 3: return new TileMFTankCreative(pos, state);
		default: return new TileMFTank(pos, state);
		}
	}

	public BlockEntityType<? extends TileAbstractSM> getTileType() {
		switch (this.data) {
		case 1: return TileInit.tankAdavance;
		case 2: return TileInit.tankMaster;
		case 3: return TileInit.tankCreative;
		default: return TileInit.tank;
		}
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return this.createMailBoxTicker(world, type, this.getTileType());
	}

	public void addTip(List<Component> toolTip, ItemStack stack, CompoundTag tags) {
		toolTip.add(this.getTipArray(this.getText("mftank"), GREEN));
		toolTip.add(this.getTipArray(this.getText("mftank_pili"), GOLD));
	}
}
