package sweetmagic.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.PlayerMainInvWrapper;

public class ItemHelper {

	public static final Comparator<ItemStack> ITEMSTACK_ASCENDING = (s1, s2) -> {
		if (s1.isEmpty() && s2.isEmpty()) { return 0; }
		if (s1.isEmpty()) { return 1; }
		if (s2.isEmpty()) { return -1; }

		if (s1.getItem() != s2.getItem()) {
			return s1.getCount() - s2.getCount();
		}

		// Different id
		return Item.getId(s1.getItem()) - Item.getId(s2.getItem());
	};

	public static boolean compactInventory(IItemHandlerModifiable inv) {

		List<ItemStack> stackList = new ArrayList<>();

		for (int i = 0; i < inv.getSlots(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.isEmpty()) { continue; }
			stackList.add(stack);
			inv.setStackInSlot(i, ItemStack.EMPTY);
		}

		// アイテムのソート
		stackList = stackList.stream().sorted((s1, s2) -> sortItemStack(s1, s2)).toList();
		stackList.forEach(s -> ItemHandlerHelper.insertItemStacked(inv, s, false));
		return stackList.isEmpty();
	}

	public static ItemStack insertStack(IItemHandler inv, ItemStack stack, boolean simulate) {
		return ItemHandlerHelper.insertItemStacked(inv, stack, simulate);
	}

	public static void inventoryInput(Player player, IItemHandlerModifiable inv) {

		List<ItemStack> stackList = new ArrayList<>();
		List<Item> itemList = new ArrayList<>();

		for (int i = 0; i < inv.getSlots(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.isEmpty()) { continue; }
			stackList.add(stack);
			itemList.add(stack.getItem());
		}

		List<ItemStack> pInveList = player.getInventory().items.stream().filter(s -> itemList.contains(s.getItem())).toList();

		for (ItemStack s : pInveList) {
			ItemStack st = ItemHandlerHelper.insertItemStacked(inv, s.copy(), false);
			s.setCount(st.getCount());
		}
	}

	public static void inventoryOutput(Player player, IItemHandlerModifiable inv) {

		List<ItemStack> pInveList = new ArrayList<>();
		List<ItemStack> stackList = new ArrayList<>();
		List<Item> itemList = new ArrayList<>();

		for (ItemStack stack : player.getInventory().items) {
			if (stack.isEmpty()) { continue; }
			pInveList.add(stack);
			itemList.add(stack.getItem());
		}

		for (int i = 0; i < inv.getSlots(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.isEmpty() || !itemList.contains(stack.getItem())) { continue; }
			stackList.add(stack);
		}

		IItemHandler pInv = new PlayerMainInvWrapper(player.getInventory());

		for (ItemStack s : stackList) {
			ItemStack st = ItemHandlerHelper.insertItemStacked(pInv, s.copy(), false);
			s.setCount(st.getCount());
		}
	}

	public static boolean compactSimpleInventory(IItemHandlerModifiable inv) {

		List<ItemStack> stackList = new ArrayList<>();

		for (int i = 0; i < inv.getSlots(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack.isEmpty()) { continue; }
			stackList.add(stack);
			inv.setStackInSlot(i, ItemStack.EMPTY);
		}

		stackList.forEach(s -> ItemHandlerHelper.insertItemStacked(inv, s, false));
		return stackList.isEmpty();
	}

	// アイテムソート
	public static int sortItemStack(ItemStack stack1, ItemStack stack2) {
		if (stack1.isEmpty() || stack2.isEmpty()) { return 0; }

		int stackId1 = Item.getId(stack1.getItem());
		int stackId2 = Item.getId(stack2.getItem());
		if (stackId1 == 0 || stackId2 == 0) { return 0; }
		if (stackId1 > stackId2) { return 1; }
		if (stackId1 < stackId2) { return -1; }

		return 0;
	}

	// アイテムソート
	public static int sortSlot(Slot slot1, Slot slot2, boolean notRreverse) {
		ItemStack stack1 = slot1.getItem();
		ItemStack stack2 = slot2.getItem();
		if (stack1.isEmpty() || stack2.isEmpty()) { return 0; }

		int stackId1 = Item.getId(stack1.getItem());
		int stackId2 = Item.getId(stack2.getItem());
		if (stackId1 == 0 || stackId2 == 0) { return 0; }
		if (stackId1 > stackId2) { return notRreverse ? 1 : -1; }
		if (stackId1 < stackId2) { return notRreverse ? -1 : 1; }

		return 0;
	}

	// アイテムソート
	public static int sortName(Slot slot1, Slot slot2, boolean notRreverse) {
		ItemStack stack1 = slot1.getItem();
		ItemStack stack2 = slot2.getItem();
		if (stack1.isEmpty() || stack2.isEmpty()) { return 0; }

		String stackName1 = stack1.getHoverName().getString();
		String stackName2 = stack2.getHoverName().getString();
		if (stackName1.equals(stackName2)) { return 0; }

		String[] nameArray = { stackName1, stackName2 };
		Arrays.sort(nameArray, String.CASE_INSENSITIVE_ORDER);

		String sortedName1 = nameArray[0];
		String sortedName2 = nameArray[1];

		if (sortedName2.equals(stackName1)) { return notRreverse ? 1 : -1; }
		if (sortedName1.equals(stackName1)) { return notRreverse ? -1 : 1; }

		return 0;
	}

	public static void compactItemListNoStacksize(List<ItemStack> stackList) {

		for (int x = 0; x < stackList.size(); x++) {

			ItemStack stack = stackList.get(x);
			if (stack.isEmpty()) { continue; }

			for (int z = x + 1; z < stackList.size(); z++) {
				ItemStack s1 = stackList.get(z);
				if (!(ItemHandlerHelper.canItemStacksStack(stack, s1))) { continue; }
				stack.grow(s1.getCount());
				stackList.set(z, ItemStack.EMPTY);
			}
		}

		stackList.removeIf(ItemStack::isEmpty);
		stackList.sort(ItemHelper.ITEMSTACK_ASCENDING);
	}

	public static int simulateFit(NonNullList<ItemStack> inv, ItemStack stack) {

		int stackSize = stack.getCount();

		for (ItemStack invStack : inv) {

			if (invStack.isEmpty()) { return 0; }

			if (!ItemHandlerHelper.canItemStacksStack(stack, invStack)) { continue; }

			int amountSlot = invStack.getMaxStackSize() - invStack.getCount();
			if (amountSlot <= 0) { continue; }

			if (stackSize <= amountSlot) { return 0; }

			stackSize -= amountSlot;
		}

		return stackSize;
	}
}
