package sweetmagic.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;
import sweetmagic.init.ItemInit;
import sweetmagic.init.TagInit;
import sweetmagic.init.item.sm.SMBucket;
import sweetmagic.recipe.base.AbstractRecipe;

public class RecipeHelper {

	// プレイヤーのインベントリをリストで返す
	public static List<ItemStack> getPlayerInv(Player player, ItemStack stack) {

		List<ItemStack> stackList = new ArrayList<>();
		Inventory inv = player.getInventory();
		NonNullList<ItemStack> pInv = inv.items;
		int selectId = inv.selected;
		stackList.add(stack);

		for (int i = 0; i < pInv.size(); i++) {
			ItemStack s = pInv.get(i);
			if (i == selectId || pInv.get(i).isEmpty()) { continue; }
			stackList.add(s);
		}

		return stackList;
	}

	// クラフト処理
	public static RecipeUtil recipeAllCraft(List<ItemStack> invList, AbstractRecipe recipe) {

		// 投入と完成品リスト
		List<ItemStack> inputList = new ArrayList<ItemStack>();
		List<ItemStack> resultAllList = new ArrayList<ItemStack>();

		// 要求アイテムとリザルトリストの取得
		List<ItemStack> requestList = recipe.getRequestList();
		List<ItemStack> resultList = recipe.getResultList();

		if (recipe.isTagResult()) {

			Random rand = new Random();
			List<Ingredient> ingList = recipe.getResultIngList();
			resultList = new ArrayList<>();

			for (Ingredient ing : ingList) {
				ItemStack[] stackArray = ing.getItems();
				resultList.add(stackArray[stackArray.length > 1 ? rand.nextInt(stackArray.length) : 0]);
			}
		}

		int reHandAmount = requestList.get(0).getCount();										// レシピに登録されてるメインアイテムの要求数
		int shrinkAmount = getRequestAmount(reHandAmount, invList.get(0).copy().getCount());	// 手に持ってるアイテムの個数

		// 要求アイテムから最低消費個数を取得
		for (ItemStack request : requestList) {
			for (ItemStack stack : invList) {
				if (!request.is(stack.getItem())) { continue; }

				if (stack.is(ItemInit.alt_bucket_water) && stack.getItem() instanceof SMBucket bucket) {

					FluidStack fluid = bucket.getFluidStack(stack);
					if (!fluid.isEmpty() && fluid.getAmount() >= request.getCount()) {

						// インベントリのほうが消費個数が少ないなら消費個数を減らす
						int inputShrink = getRequestAmount(request.getCount(), fluid.getAmount());
						shrinkAmount = shrinkAmount > inputShrink ? inputShrink : shrinkAmount;
						break;
					}
					continue;
				}

				// アイテムが一致しないなら次へ
				else if (request.getCount() > stack.getCount()) { continue; }

				// インベントリのほうが消費個数が少ないなら消費個数を減らす
				int inputShrink = getRequestAmount(request.getCount(), stack.getCount());
				shrinkAmount = shrinkAmount > inputShrink ? inputShrink : shrinkAmount;
				break;
			}
		}

		// インベントリ内のアイテムを消費
		for (ItemStack request : requestList) {
			for (ItemStack stack : invList) {
				if (!request.is(stack.getItem())) { continue; }

				if (stack.is(ItemInit.alt_bucket_water) && stack.getItem() instanceof SMBucket bucket) {

					FluidStack fluid = bucket.getFluidStack(stack);
					if (!fluid.isEmpty() && fluid.getAmount() >= request.getCount()) {

						// 消費個数の取得
						int shrinkCount = shrinkAmount * request.getCount();

						// 使用するアイテムを投入リストへ追加
						ItemStack send = stack.copy();
						inputList.add(stack.copy());
						send.setTag(bucket.getTag(stack));
						send = bucket.shrinkWater(send, shrinkCount * 1000);
						resultAllList.add(send);

						// アイテム消費
						stack.shrink(1);
						break;
					}
					continue;
				}

				// アイテムが一致しないなら次へ
				else if (request.getCount() > stack.getCount()) { continue; }

				// 消費個数の取得
				int shrinkCount = shrinkAmount * request.getCount();

				// 使用するアイテムを投入リストへ追加
				ItemStack send = stack.copy();
				send.setCount(shrinkCount);
				inputList.add(send);

				// バケツを使っている場合、リザルトにバケツを追加
				if (isBucket(stack.getItem())) {
					resultAllList.add(new ItemStack(Items.BUCKET));
				}

				// アイテム消費
				stack.shrink(shrinkCount);
				break;
			}
		}

		// クラフト後アイテムの製作個数分リストへ入れる
		for (ItemStack stack : resultList) {

			// 最大クラフト個数の取得
			int amount = stack.getCount() * shrinkAmount;

			// 数が0になるまで繰り返し
			while (amount > 0) {

				// 最大個数を64に設定して最大クラフト個数から引いていく
				int input = Math.min(amount, 64);
				amount -= input;

				// 最大個数の数に設定してリザルトリストへ突っ込む
				ItemStack resultItems = stack.copy();
				resultItems.setCount(input);
				resultAllList.add(resultItems);
			}
		}

		return new RecipeUtil(inputList, resultAllList, shrinkAmount);
	}

	// 単体クラフト
	public static RecipeUtil recipeSingleCraft(List<ItemStack> stackList, AbstractRecipe recipe) {

		// 投入と完成品リスト
		List<ItemStack> inputList = new ArrayList<ItemStack>();

		// 要求アイテムとリザルトリストの取得
		List<ItemStack> requestList = recipe.getRequestList();
		List<ItemStack> resultList = recipe.getResultList();

		for (ItemStack request: requestList) {
			for (int i = 0; i < stackList.size(); i++) {

				ItemStack stack = stackList.get(i);
				if (!request.is(stack.getItem())) { continue; }

				if (stack.is(ItemInit.alt_bucket_water) && stack.getItem() instanceof SMBucket bucket) {

					FluidStack fluid = bucket.getFluidStack(stack);
					if (!fluid.isEmpty() && fluid.getAmount() >= request.getCount()) {

						// 使用するアイテムを投入リストへ追加
						ItemStack send = stack.copy();
						inputList.add(stack.copy());
						send.setTag(bucket.getTag(stack));
						send = bucket.shrinkWater(send, request.getCount() * 1000);
						resultList.add(send);

						// アイテム消費
						stack.shrink(1);
						break;
					}
					continue;
				}

				// アイテムが一致しないなら次へ
				else if (request.getCount() > stack.getCount()) { continue; }

				// tier2以上の魔術書なら
				if (stack.is(TagInit.MAGIC_BOOK_COSMIC) && i != 0) { break; }

				int shrinkCount = request.getCount();

				// 使用するアイテムを投入リストへ追加
				ItemStack send = stack.copy();
				send.setCount(shrinkCount);
				inputList.add(send);

				// バケツを使っている場合、リザルトにバケツを追加
				if (isBucket(stack.getItem())) {
					resultList.add(new ItemStack(Items.BUCKET));
				}

				stack.shrink(shrinkCount);
				break;
			}
		}

		return new RecipeUtil(inputList, resultList, 1);
	}

	// 表示用クラフト
	public static RecipeUtil recipePreview(List<ItemStack> stackList, AbstractRecipe recipe) {

		// 投入と完成品リスト
		List<ItemStack> inputList = new ArrayList<ItemStack>();

		// 要求アイテムとリザルトリストの取得
		List<ItemStack> requestList = recipe.getRequestList();
		List<ItemStack> resultList = recipe.getResultList();

		for (ItemStack request: requestList) {
			for (ItemStack stack : stackList) {
				if (!request.is(stack.getItem())) { continue; }

				if (stack.is(ItemInit.alt_bucket_water) && stack.getItem() instanceof SMBucket bucket) {

					FluidStack fluid = bucket.getFluidStack(stack);
					if (!fluid.isEmpty() && fluid.getAmount() >= request.getCount()) {
						inputList.add(stack.copy());
						resultList.add(stack.copy());
						break;
					}
				}

				// アイテムが一致しないなら次へ
				else if (request.getCount() > stack.getCount()) { continue; }

				int shrinkCount = request.getCount();

				// 使用するアイテムを投入リストへ追加
				ItemStack send = stack.copy();
				send.setCount(shrinkCount);
				inputList.add(send);

				// バケツを使っている場合、リザルトにバケツを追加
				if (isBucket(stack.getItem())) {
					resultList.add(new ItemStack(Items.BUCKET));
				}

				// tier2以上の魔術書なら
				else if (stack.is(TagInit.MAGIC_BOOK_COSMIC)) {
					resultList.add(stack.copy());
				}

				break;
			}
		}

		return new RecipeUtil(inputList, resultList, 1);
	}

	// レシピの消費数計算
	public static int getRequestAmount(int recipeAmount, int invAmount) {
		return invAmount / recipeAmount;
	}

	// バケツチェック
	public static boolean isBucket(Item item) {
		return item == Items.WATER_BUCKET || item == Items.MILK_BUCKET;
	}

	public record RecipeUtil(List<ItemStack> inputList, List<ItemStack> resultList, int count) {
		public ItemStack getHand() { return this.getInputList().get(0); }	// ハンドの取得
		public List<ItemStack> getInputList() { return this.inputList; }	// 投入リストの取得
		public List<ItemStack> getResultList() { return this.resultList; }	// 出力リストの取得
		public int getCount() { return this.count; }						// レシピ個数
	}
}
