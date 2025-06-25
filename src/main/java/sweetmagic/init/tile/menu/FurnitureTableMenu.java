package sweetmagic.init.tile.menu;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.NetworkHooks;
import sweetmagic.init.MenuInit;
import sweetmagic.init.RecipeTypeInit;
import sweetmagic.init.tile.menu.container.BaseContainer.ContainerWoodChest;
import sweetmagic.init.tile.slot.SMSlot;
import sweetmagic.init.tile.sm.TileFurnitureTable;
import sweetmagic.util.ItemHelper;

public class FurnitureTableMenu extends BaseSMMenu {

	public final Slot inputSlot;
	public final Slot resultSlot;
	public final TileFurnitureTable tile;
	public List<Recipe<?>> recipeList = new ArrayList<>();
	private Runnable slotUpdateListener = () -> {};

	public FurnitureTableMenu(int windowId, Inventory pInv, FriendlyByteBuf data) {
		this(windowId, pInv, MenuInit.getTile(TileFurnitureTable::new, pInv, data));
	}

	public FurnitureTableMenu(int windowId, Inventory pInv, TileFurnitureTable tile) {
		super(MenuInit.furnitureTableMenu, windowId, pInv, tile);
		this.tile = tile;

		IItemHandler out = this.tile.getOut();
		SMSlot slot = new SMSlot(this.tile.getInput(), 0, 16, 33) {

			public void setChanged() {
				super.setChanged();
				FurnitureTableMenu.this.slotsChangedSide(this.container);
				FurnitureTableMenu.this.slotsChanged(this.container);
				FurnitureTableMenu.this.slotUpdateListener.run();
			}
		};

		for (int x = 0; x < 9; x++) {

			SMSlot slotSide = new SMSlot(this.tile.getInputSide(), x, 8 + x * 18, 8) {

				public void setChanged() {
					super.setChanged();
					FurnitureTableMenu.this.slotsChangedSide(this.container);
					FurnitureTableMenu.this.slotsChanged(this.container);
					FurnitureTableMenu.this.slotUpdateListener.run();
				}
			};

			this.addSlot(slotSide);
		}

		this.inputSlot = this.addSlot(slot);
		this.resultSlot = this.addSlot(new SMSlot(this.tile.getResult(), 0, 16, 69, s -> false));

		for (int y = 0; y < 3; y++)
			for (int x = 0; x < 9; x++)
				this.addSlot(new SMSlot(out, x + y * 9, 8 + x * 18, 113 + y * 18, s -> false));

		this.setPInv(pInv, 8, 171);
		this.setSlotSize(this.tile.getInvSize() + 9 + 2);
	}

	// アイテムが変わった時
	public void slotsChanged(Container container) {

		ItemStack stack = this.inputSlot.getItem();
		this.tile.chekcSlot();

		// 入力スロットのアイテムが空なら初期化
		if (stack.isEmpty()) {
			this.tile.selectId = 0;
			this.tile.isSelect = false;
			this.tile.outStack = ItemStack.EMPTY;
			this.recipeList = new ArrayList<>();
			this.tile.sendPKT();
			return;
		}

		// レシピ取得
		else if (!stack.is(this.tile.outStack.getItem())) {
			this.setupRecipeList(container, stack);
		}
	}

	// アイテムが変わった時
	public void slotsChangedSide(Container container) {
		this.tile.insertInput();
	}

	// レシピ取得
	private void setupRecipeList(Container container, ItemStack stack) {
		this.recipeList = new ArrayList<>();
		if (stack.isEmpty()) { return; }

		Container con = new SimpleContainer(1) {
			public ItemStack getItem(int i) { return stack; }
		};

		// レシピリストを取得
		Level world = this.tile.getLevel();
		this.recipeList.addAll(world.getRecipeManager().getRecipesFor(RecipeType.STONECUTTING, con, world));
		this.recipeList.addAll(world.getRecipeManager().getRecipesFor(RecipeTypeInit.FURNITURE, con, world));

		// レシピソート
		List<Item> itemList = new ArrayList<>();
		this.recipeList = this.recipeList.stream().filter(s -> this.itemFilter(itemList, s.getResultItem().getItem())).sorted((s1, s2) -> ItemHelper.sortItemStack(s1.getResultItem(), s2.getResultItem())).toList();
	}

	public boolean itemFilter(List<Item> list, Item item) {
		if(list.contains(item)) { return false; }
		list.add(item);
		return true;
	}

	public void registerUpdateListener(Runnable run) {
		this.slotUpdateListener = run;
	}

	@Override
	public boolean clickMenuButton(Player player, int id) {

		// クラフト開始
		if (id == 0) {
			if (this.tile.isSelect) {
				this.tile.craftStart();
				this.tile.clickButton();
			}

			return true;
		}

		else if (id == 1) {
			if (this.tile.isSelect && player instanceof ServerPlayer sePlayer) {
				NetworkHooks.openScreen(sePlayer, new ContainerWoodChest(this.tile), this.tile.getBlockPos());
				this.tile.clickButton();
			}
			return true;
		}

		Level world = this.tile.getLevel();

		// サーバー側でレシピ取得
		if (!world.isClientSide()) {
			this.slotsChanged(this.inputSlot.container);
		}

		// レシピリストより大きいIDの場合は終了
		if (id - 2 >= this.recipeList.size()) { return true; }

		// サーバー側で処理
		if (!world.isClientSide()) {

			// 既に選択しているレシピなら選択キャンセル
			if (this.tile.selectId == id) {
				this.tile.selectId = 0;
				this.tile.isSelect = false;
				this.tile.outStack = ItemStack.EMPTY;
				this.tile.inputStack = ItemStack.EMPTY;
			}

			// 未選択なら選択状態に
			else {
				this.tile.selectId = id;
				this.tile.isSelect = true;
				this.tile.outStack = this.recipeList.get(id - 2).getResultItem().copy();
				this.tile.inputStack = this.tile.getInputItem().copy();

				int count = this.tile.outStack.getCount();
				int value = this.tile.setCount / count;
				this.tile.setCount = Math.max(value * count, count);
			}

			// 作成状態解除とボタンクリック音を出す
			this.tile.isCraft = false;
			this.tile.clickButton();
			this.tile.sendPKT();
		}

		return true;
	}
}
