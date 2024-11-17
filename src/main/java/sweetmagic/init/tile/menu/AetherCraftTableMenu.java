package sweetmagic.init.tile.menu;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import sweetmagic.init.MenuInit;
import sweetmagic.init.tile.menu.container.SMCraftingContainer;
import sweetmagic.init.tile.slot.ChangeSlot;
import sweetmagic.init.tile.sm.TileAbstractSM.StackHandler;
import sweetmagic.init.tile.sm.TileAetherCraftTable;
import sweetmagic.util.ItemHelper;

public class AetherCraftTableMenu extends BaseSMMenu {

	public final TileAetherCraftTable tile;
	public int maxSlots = 0;
	private final ContainerLevelAccess access;
	private final SMCraftingContainer craftSlots;
	private final ResultContainer resultSlots = new ResultContainer();
	private int offSet = 0;
	public float scrollOffset = 0F;

    public AetherCraftTableMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
        this(windowId, pInv, (TileAetherCraftTable) MenuInit.getTile(pInv, data));
    }

	public AetherCraftTableMenu(int windowId, Inventory pInv, TileAetherCraftTable tile) {
		super(MenuInit.aetherCraftTableMenu, windowId, pInv, tile);
		this.tile = tile;
		this.access = ContainerLevelAccess.create(tile.getLevel(), tile.getBlockPos());

		this.addSlotInv();

        this.craftSlots = new SMCraftingContainer(this, (StackHandler) tile.getInput());
		this.addSlot(new ResultSlot(this.player, this.craftSlots, this.resultSlots, 0, 133, 120));

		for (int y = 0; y < 3; ++y) {
			for (int x = 0; x < 3; ++x) {
				this.addSlot(new Slot(this.craftSlots, x + y * 3, 45 + x * 18, 102 + y * 18));
			}
		}

		this.setPInv(pInv, 8, 159);
		this.setSlotSize(this.maxSlots + 10);
		this.access.execute((par1, par2) -> this.slotChangedCraftingGrid(this, par1, this.player, this.craftSlots, this.resultSlots) );
	}

	// インベントリの初期設定
	public void addSlotInv () {
		BlockPos targetPos = this.getTargetPos();
		if (targetPos == null) { return; }

        int count = 0;
        List<IItemHandler> handlerList = this.getHandlerList(targetPos, true);
        for (IItemHandler handler : handlerList) {

			for (int i = 0; i < handler.getSlots(); i++) {
				int pY = count >= 45 ? -1000 : 8 + count / 9 * 18;
				this.addSlot(new ChangeSlot(handler, i, 8 + count % 9 * 18, pY, s -> true, this));
				count++;
			}
        }

        this.updateSlotPos(0);
	}

	// ターゲット座標の取得
	public BlockPos getTargetPos () {

		Iterable<BlockPos> posArray = this.tile.getRangePos(this.tile.getBlockPos(), 16);
		BlockPos targetPos = null;

		for (BlockPos pos : posArray) {
			Block block = this.tile.getBlock(pos);
			if (!this.tile.isReader(block)) { continue; }

			targetPos = pos;
			break;
		}

		return targetPos;
	}

	// インベントリリストの取得
	public List<IItemHandler> getHandlerList (BlockPos targetPos, boolean addSlot) {

		List<BlockEntity>  tileList = new ArrayList<>();

		// ターゲット座標に隣接されたインベントリを取得
        for (Direction face : Direction.values()) {

            BlockPos pos = targetPos.relative(face);
            BlockEntity tile = this.tile.getLevel().getBlockEntity(pos);
            if (tile == null || tile instanceof TileAetherCraftTable) { continue; }

            if (tile.getCapability(ForgeCapabilities.ITEM_HANDLER, face).filter(IItemHandlerModifiable.class::isInstance).isPresent()) {
            	tileList.add(tile);
            }
        }

        // ブロックえんちちーが見つからなかったら終了
        List<IItemHandler> handlerList = new ArrayList<>();
        if (tileList.isEmpty()) { return handlerList; }

		int chestCount = 0;

		for (BlockEntity tile : tileList) {
			tile.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(i -> {
				int size = i.getSlots();

				if (addSlot) {
					this.maxSlots += size;
				}
				handlerList.add(i);
			});

			chestCount += 1;
			if (chestCount >= this.tile.getMaxViewChest()) { break; }
		}

		return handlerList;
	}

    // スロット位置の設定
    public void updateSlotPos(int offsetY) {
    	this.updateSlotPos(offsetY, this.tile.getSortType());
    }

    // スロット位置の設定
    public void updateSlotPos(int offsetY, int id) {

		this.offSet = offsetY;
		int maxY = this.maxSlots / 9 /*+ 1*/;
		if (this.maxSlots % 9 != 0) { maxY += 1; }

		List<Slot> slotList = this.sortSlot(id);

		for (int y = 0; y < maxY; y++) {
			for (int x = 0; x < 9; x++) {

				if (x + y * 9 >= this.maxSlots) { return; }

				int tX = x + (y - offsetY) * 9;
				int pX = 8 + x * 18;
				int pY = (tX >= 45 || tX < 0) ? -1000 : 8 + (y - offsetY) * 18;
				ChangeSlot slot = (ChangeSlot) slotList.get(x + y * 9);
				slot.setActive(!(tX >= 45 || tX < 0));
				this.setSlotPos(slot, pX, pY);
			}
		}
    }

	// スロット順番ソート
	public List<Slot> sortSlot (int id) {
		switch (id) {
		case 1:  return this.sortSlotName();
		case 2:  return this.sortSlotChest();
		default: return this.sortSlotId();
		}
	}

	// アイテムIDソート
	public List<Slot> sortSlotId () {

		List<Slot> slotList = new ArrayList<>();
		List<Slot> slotStackList = new ArrayList<>();
		List<Slot> slotEmptyList = new ArrayList<>();

		for (int i = 0; i < this.maxSlots; i++) {

			Slot slot = this.slots.get(i);

			if (slot.getItem().isEmpty()) {
				slotEmptyList.add(slot);
			}

			else {
				slotStackList.add(slot);
			}
		}

		slotList.addAll(slotStackList);
		slotList.addAll(slotEmptyList);
		return slotList.stream().sorted( (s1, s2) -> ItemHelper.sortSlot(s1, s2, this.tile.getAscending()) ).toList();
	}

	// アイテム名ソート
	public List<Slot> sortSlotName () {

		List<Slot> slotList = new ArrayList<>();
		List<Slot> slotStackList = new ArrayList<>();
		List<Slot> slotEmptyList = new ArrayList<>();

		for (int i = 0; i < this.maxSlots; i++) {

			Slot slot = this.slots.get(i);

			if (slot.getItem().isEmpty()) {
				slotEmptyList.add(slot);
			}

			else {
				slotStackList.add(slot);
			}
		}

		slotList.addAll(slotStackList);
		slotList.addAll(slotEmptyList);
		return slotList.stream().sorted( (s1, s2) -> ItemHelper.sortName(s1, s2, this.tile.getAscending()) ).toList();
	}

	// スロットの取得
	public List<Slot> sortSlotChest () {

		List<Slot> slotList = new ArrayList<>();

		for (int i = 0; i < this.maxSlots; i++) {
			slotList.add(this.slots.get(i));
		}

		if (!this.tile.getAscending()) {
			Collections.reverse(slotList);
		}

		return slotList;
	}

    // インベントリの座標を設定
    public void setSlotPos(Slot slot, int x, int y) {
        this.setSlotPos(slot, "f_40220_", x);
        this.setSlotPos(slot, "f_40221_", y);
    }

    // インベントリの座標を設定
    public void setSlotPos(Slot slot, String fieldName, int newValue) {
        try {
            Field field = ObfuscationReflectionHelper.findField(Slot.class, fieldName);
            field.setAccessible(true);
            field.set(slot, newValue);
        }

        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

	// アイテムが変わった時
	public void slotsChangInv(Container con) {
		if (this.player.level.isClientSide) {
	        this.updateSlotPos(this.offSet);
		}
	}

	// ボタンクリック
	@Override
	public boolean clickMenuButton(Player player, int id) {
		this.tile.clickButton();

		switch(id) {
		case 0:
		case 1:
		case 2:
			this.tile.setSortType(id);
	        this.updateSlotPos(0);
	        this.tile.sendPKT();
	        this.scrollOffset = 0;
			break;
		case 3:
			this.tile.setAscending(!this.tile.getAscending());
	        this.updateSlotPos(0);
	        this.tile.sendPKT();
			break;
		case 4:
			this.compactInventory();
			break;
		}

		return true;
	}

	// 各インベントリソート
	public void compactInventory () {
		if (this.tile.getLevel().isClientSide) { return; }

		// 座標の取得
		BlockPos targetPos = this.getTargetPos();
		if (targetPos == null) { return; }

		// インベントリの取得
        List<IItemHandler> handlerList = this.getHandlerList(targetPos, false).stream().filter(s -> s instanceof IItemHandlerModifiable).toList();
        if (handlerList.isEmpty()) { return; }

		// アイテムをソート
        for (int i = handlerList.size() - 2; i >= 0; i--) {
        	this.compactInventoryHandler(handlerList.get(i), handlerList.get(i + 1));
        }

		// インベントリのソート
        handlerList.forEach(h -> ItemHelper.compactInventory((IItemHandlerModifiable) h));
	}

	// アイテムをソート
	public int compactInventoryHandler (IItemHandler hand1, IItemHandler hand2) {

		// 回収元のインベントリ分回す
		for (int k = 0; k < hand2.getSlots(); k++) {

    		ItemStack input = hand2.getStackInSlot(k);
    		if (input.isEmpty()) { continue; }

    		// 投入先のインベントリ分回してアイテムを入れる
    		for (int i = 0; i < hand1.getSlots(); i++) {
				ItemStack inStack = hand1.insertItem(i, input.copy(), false);
				input.setCount(inStack.getCount());
    		}
		}

		return 0;
	}

	protected void slotChangedCraftingGrid(AbstractContainerMenu menu, Level world, Player player, CraftingContainer con, ResultContainer result) {
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

	public void slotsChanged(Container con) {
		this.access.execute((par1, par2) -> this.slotChangedCraftingGrid(this, par1, this.player, this.craftSlots, this.resultSlots));
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

	public ItemStack quickMoveStack(Player player, int index) {

		ItemStack stack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot == null || !slot.hasItem()) { return stack; }

		ItemStack stack1 = slot.getItem();
		stack = stack1.copy();
		int maxSlots = this.maxSlots;

		if (index + maxSlots == 0) {

			this.access.execute((par1, par2) -> stack1.getItem().onCraftedBy(stack1, par1, player));

			if (!this.moveItemStackTo(stack1, 10, 46, true)) {
				return ItemStack.EMPTY;
			}

			slot.onQuickCraft(stack1, stack);
		}

		int slotCount = this.getSlotSize();

		if (index < slotCount) {
			if (!this.moveItemStackTo(stack1, slotCount, this.slots.size(), true)) {
				return ItemStack.EMPTY;
			}
		}

		else if (!this.moveItemStackTo(stack1, 0, slotCount - 10, false)) {
			return ItemStack.EMPTY;
		}

		if (stack1.isEmpty()) {
			slot.set(ItemStack.EMPTY);
		}

		else {
			slot.setChanged();
		}

		if (stack1.getCount() == stack.getCount()) {
			return ItemStack.EMPTY;
		}

		slot.onTake(player, stack1);
		if (index == 0) {
			player.drop(stack1, false);
		}

		return stack;
	}

	@Override
	public void clicked(int slotId, int dragType, ClickType clickType, Player player) {

		if (slotId == -999 || this.slots.size() >= slotId + 36 || slotId < 0) {

			ItemStack stack = this.getCarried();

			if (slotId >= 0 && slotId < this.maxSlots) {

				ItemStack slotStack = this.slots.get(slotId).getItem();
				if(!stack.isEmpty() && !slotStack.isEmpty()) {

					if (!this.insertItem(stack)) {
						super.clicked(slotId, dragType, clickType, player);
					}
					return;
				}
			}

			super.clicked(slotId, dragType, clickType, player);
			return;
		}

		super.clicked(slotId, dragType, clickType, player);
	}

	public boolean insertItem (ItemStack input) {

		List<Slot> slotList = this.sortSlot(this.tile.getSortType());

		for (Slot slot : slotList) {
			ItemStack inStack = slot.safeInsert(input.copy());
			input.setCount(inStack.getCount());
			if (inStack.isEmpty()) { break; }
		}

        return true;
	}
}
