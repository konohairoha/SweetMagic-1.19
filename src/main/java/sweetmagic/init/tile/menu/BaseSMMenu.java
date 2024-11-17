package sweetmagic.init.tile.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import sweetmagic.init.tile.sm.TileAbstractSM;

public abstract class BaseSMMenu extends AbstractContainerMenu {

	public final TileAbstractSM tile;
	public int slotSize = 0;
	public Player player;

	public BaseSMMenu(MenuType<?> type, int windowId, Inventory pInv, TileAbstractSM tile) {
		super(type, windowId);
		this.tile = tile;
		this.player = pInv.player;
	}

	public void setPInv (Inventory pInv, int tX, int tY) {
		this.setPInv(pInv, tX, tY, 0);
	}

	public void setPInv (Inventory pInv, int tX, int tY, int addY) {

		//Player Inventorye
		for (int y = 0; y < 3; y++)
			for (int x = 0; x < 9; x++)
				this.addSlot(new Slot(pInv, x + y * 9 + 9, tX + x * 18, tY + y * 18));

		//Player HotBar
		for (int x = 0; x < 9; x++)
			this.addSlot(new Slot(pInv, x, tX + x * 18, tY + 58 + addY));
	}

	public ItemStack quickMoveStack(Player player, int index) {

		ItemStack stack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);

		if (slot != null && slot.hasItem()) {

			ItemStack stack1 = slot.getItem();
			stack = stack1.copy();
			int slotCount = this.getSlotSize();

			if (index < slotCount) {
				if (!this.moveItemStackTo(stack1, slotCount, this.slots.size(), true)) {
					return ItemStack.EMPTY;
				}
			}

			else if (!this.moveItemStackTo(stack1, 0, slotCount, false)) {
				return ItemStack.EMPTY;
			}

			if (stack1.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			}

			else {
				slot.setChanged();
			}
		}

		return stack;
	}

	// ブロックエンティティの取得
	public TileAbstractSM getTile () {
		return this.tile;
	}

	// スロットサイズの取得
	public void setSlotSize(int slotSize) {
		this.slotSize = slotSize;
	}

	// スロットサイズの設定
	public int getSlotSize() {
		return this.slotSize;
	}

	@Override
	public boolean stillValid(Player player) {
		BlockPos pos = this.getTile().getBlockPos();
		return player.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64D;
	}
}
