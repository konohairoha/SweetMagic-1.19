package sweetmagic.init.tile.menu;

import java.util.Optional;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import sweetmagic.api.iitem.IMagicBook;
import sweetmagic.api.iitem.IPorch;
import sweetmagic.api.iitem.IRobe;
import sweetmagic.init.MenuInit;

public class SMBookMenu extends RecipeBookMenu<CraftingContainer> {

	private final CraftingContainer craftSlots = new CraftingContainer(this, 3, 3);
	private final ResultContainer resultSlots = new ResultContainer();
	private final ContainerLevelAccess access;
	private final ItemStack stack;
	private final Player player;

	public SMBookMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
		this(windowId, pInv, ContainerLevelAccess.create(pInv.player.getCommandSenderWorld(), pInv.player.blockPosition()));
	}

	public SMBookMenu(int windowId, Inventory level) {
		this(windowId, level, ContainerLevelAccess.NULL);
	}

	public SMBookMenu(int windowId, Inventory inv, ContainerLevelAccess con) {
		super(MenuInit.bookMenu, windowId);
		this.access = con;
		this.player = inv.player;
		this.stack = this.player.getMainHandItem();
		this.addSlot(new ResultSlot(inv.player, this.craftSlots, this.resultSlots, 0, 124, 65));

		for (int y = 0; y < 3; ++y)
			for (int x = 0; x < 3; ++x)
				this.addSlot(new Slot(this.craftSlots, x + y * 3, 42 + x * 18, 46 + y * 18));

		for (int y = 0; y < 3; ++y)
			for (int x = 0; x < 9; ++x)
				this.addSlot(new Slot(inv, x + y * 9 + 9, 8 + x * 18, 140 + y * 18));

		for (int x = 0; x < 9; ++x)
			this.addSlot(new Slot(inv, x, 8 + x * 18, 198));
	}

	protected static void slotChangedCraftingGrid(AbstractContainerMenu menu, Level world, Player player, CraftingContainer con, ResultContainer result) {
		if (world.isClientSide) { return; }

		ServerPlayer sPlayer = (ServerPlayer) player;
		ItemStack stack = ItemStack.EMPTY;
		Optional<CraftingRecipe> opti = world.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, con, world);

		if (opti.isPresent()) {
			CraftingRecipe recipe = opti.get();
			if (result.setRecipeUsed(world, sPlayer, recipe)) {
				stack = recipe.assemble(con);
			}
		}

		result.setItem(0, stack);
		menu.setRemoteSlot(0, stack);
		sPlayer.connection.send(new ClientboundContainerSetSlotPacket(menu.containerId, menu.incrementStateId(), 0, stack));
	}

	public void slotsChanged(Container container) {
		this.access.execute((par1, par2) -> slotChangedCraftingGrid(this, par1, this.player, this.craftSlots, this.resultSlots));
	}

	public void fillCraftSlotsStackedContents(StackedContents con) {
		this.craftSlots.fillStackedContents(con);
	}

	public void clearCraftingContent() {
		this.craftSlots.clearContent();
		this.resultSlots.clearContent();
	}

	public boolean recipeMatches(Recipe<? super CraftingContainer> recipe) {
		return recipe.matches(this.craftSlots, this.player.level);
	}

	public void removed(Player player) {
		super.removed(player);
		this.access.execute((par1, par2) -> this.clearContainer(player, this.craftSlots));
	}

	public boolean stillValid(Player player) {
		return true;
	}

	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack stack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot == null || !slot.hasItem()) { return stack; }

		ItemStack stack1 = slot.getItem();
		stack = stack1.copy();

		if (index == 0) {

			this.access.execute((par1, par2) -> stack1.getItem().onCraftedBy(stack1, par1, player));
			if (!this.moveItemStackTo(stack1, 10, 46, true)) { return ItemStack.EMPTY; }

			slot.onQuickCraft(stack1, stack);
		}

		else if (index >= 10 && index < 46) {

			if (!this.moveItemStackTo(stack1, 1, 10, false)) {

				if (index < 37) {

					if (!this.moveItemStackTo(stack1, 37, 46, false)) {
						return ItemStack.EMPTY;
					}
				}

				else if (!this.moveItemStackTo(stack1, 10, 37, false)) {
					return ItemStack.EMPTY;
				}
			}
		}

		else if (!this.moveItemStackTo(stack1, 10, 46, false)) {
			return ItemStack.EMPTY;
		}

		if (stack1.isEmpty()) {
			slot.set(ItemStack.EMPTY);
		}

		else {
			slot.setChanged();
		}

		if (stack1.getCount() == stack.getCount()) { return ItemStack.EMPTY; }

		slot.onTake(player, stack1);
		if (index == 0) {
			player.drop(stack1, false);
		}

		return stack;
	}

	public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
		return slot.container != this.resultSlots && super.canTakeItemForPickAll(stack, slot);
	}

	public int getResultSlotIndex() {
		return 0;
	}

	public int getGridWidth() {
		return this.craftSlots.getWidth();
	}

	public int getGridHeight() {
		return this.craftSlots.getHeight();
	}

	public int getSize() {
		return 10;
	}

	public RecipeBookType getRecipeBookType() {
		return RecipeBookType.CRAFTING;
	}

	public boolean shouldMoveToInventory(int index) {
		return index != this.getResultSlotIndex();
	}

	@Override
	public boolean clickMenuButton(Player player, int id) {
		switch (id) {
		case 0:
			ItemStack robe = player.getItemBySlot(EquipmentSlot.CHEST);
			((IRobe) robe.getItem()).openGui(player.level, player, robe);
			break;
		case 1:
			ItemStack porch = player.getItemBySlot(EquipmentSlot.LEGS);
			((IPorch) porch.getItem()).openGui(player.level, player, porch);
			break;
		case 2:
			ItemStack hand = player.getMainHandItem();
			if (hand.isEmpty() || !(hand.getItem() instanceof IMagicBook) ) { break; }
			((IMagicBook) this.stack.getItem()).openGui(player.level, player, this.stack);
			break;
		}

		return true;
	}
}
