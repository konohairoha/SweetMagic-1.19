package sweetmagic.init.block.magic;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.ItemStackHandler;
import sweetmagic.init.TileInit;
import sweetmagic.init.block.base.BaseMFBlock;
import sweetmagic.init.tile.sm.TileAbstractSM;
import sweetmagic.init.tile.sm.TileMFChanger;
import sweetmagic.init.tile.sm.TileMFChangerAdvanced;
import sweetmagic.init.tile.sm.TileMFChangerMaster;

public class MFChager extends BaseMFBlock {

	private final int data;
	private static final VoxelShape AABB = Block.box(0D, 0D, 0D, 16D, 8D, 16D);

	public MFChager(String name, int data) {
		super(name);
		this.data = data;
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext con) {
		return AABB;
	}

	// 最大MFの取得
	public int getMaxMF() {
		switch (this.data) {
		case 1  : return 800000;
		case 2  : return 4000000;
		default:  return 30000;
		}
	}

	public int getTier() {
		return this.data + 1;
	}

	// ブロックでのアクション
	public boolean actionBlock(Level world, BlockPos pos, Player player, ItemStack stack) {
		if (world.isClientSide) { return false; }

		MenuProvider tile = null;

		switch (this.data) {
		case 0:
			tile = (TileMFChanger) world.getBlockEntity(pos);
			break;
		case 1:
			tile = (TileMFChangerAdvanced) world.getBlockEntity(pos);
			break;
		case 2:
			tile = (TileMFChangerMaster) world.getBlockEntity(pos);
			break;
		}

		this.openGUI(world, pos, player, tile);
		return true;
	}

	public boolean keepTileInfo() {
		return true;
	}

	public ItemStack inheritingNBT(ItemStack oldStack, ItemStack newStack) {

		// NBTを持っていたらNBTの移行
		if (oldStack.getOrCreateTag().contains("BlockEntityTag")) {

			CompoundTag tags = oldStack.getTagElement("BlockEntityTag");

			// 移行前のスロットサイズとスロットの取得
			int oldSlotSize = this.data == 0 ? 3 : 5;
			ItemStackHandler oldInputInv = new ItemStackHandler(oldSlotSize);
			oldInputInv.deserializeNBT(tags.getCompound("inputInv"));

			// 移行後のスロットサイズとスロットの取得
			int newSlotSize = this.data == 0 ? 5 : 10;
			ItemStackHandler newInputInv = new ItemStackHandler(newSlotSize);

			// 移行前のアイテムを移行後に移送
			for (int i = 0; i < oldSlotSize; i++) {
				newInputInv.setStackInSlot(i, oldInputInv.getStackInSlot(i));
			}

			// 古いNBTを除去して新しく登録
			tags.remove("inputInv");
			tags.put("inputInv", newInputInv.serializeNBT());

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
		case 1  : return new TileMFChangerAdvanced(pos, state);
		case 2  : return new TileMFChangerMaster(pos, state);
		default:  return new TileMFChanger(pos, state);
		}
	}

	public BlockEntityType<? extends TileAbstractSM> getTileType() {
		switch (this.data) {
		case 1: return TileInit.changerAdavance;
		case 2: return TileInit.changerMaster;
		default: return TileInit.changer;
		}
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return this.createMailBoxTicker(world, type, this.getTileType());
	}

	// RS信号で停止するかどうか
	public boolean isRSStop() {
		return true;
	}

	public void addTip(List<Component> toolTip, ItemStack stack, CompoundTag tags) {
		toolTip.add(this.getTipArray(this.getText("mfchanger"), GREEN));
	}
}
