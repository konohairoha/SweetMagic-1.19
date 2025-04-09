package sweetmagic.recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.fluids.FluidStack;
import sweetmagic.init.ItemInit;
import sweetmagic.init.item.sm.SMBucket;

public class RecipeMathCheck {

	public static RecipeInfo checkRecipe(List<ItemStack> stackList, List<Ingredient> ingList, List<Integer> countList) {

		// 使用するアイテムのリスト
		List<ItemStack> requestStackList = new ArrayList<>();
		List<Integer> slotIdList = new ArrayList<>();

		for (int count = 0; count < ingList.size(); count++) {

			Ingredient ingred = ingList.get(count);
			int stackSize = countList.get(count);

			// 大分類に格納されたアイテムをリストで取得
			List<ItemStack> stackIngredList = new ArrayList<ItemStack>(Arrays.asList(ingred.getItems()));

			// 中分類を回すたびにレシピチェック成功フラグの初期化
			boolean successFlg = false;

			// 中分類（大分類に入っているアイテムごと）
			for (ItemStack ingStack : stackIngredList) {

				ingStack.setCount(stackSize);

				// 大分類を回したのが初回ならメインハンドのアイテムと比較
				if (count == 0) {
					ItemStack stack = stackList.get(0);

					// アイテムが一致して要求個数以上なら検索完了
					if (ingStack.is(stack.getItem()) && stack.getCount() >= ingStack.getCount()) {
						successFlg = true;
						requestStackList.add(ingStack);
						break;
					}
				}

				// 大分類を回した数が2回目以降ならインベントリ内をチェック
				else {

					// 小分類(インベントリアイテム)
					for (int i = 1; i < stackList.size(); i++) {

						if (slotIdList.contains(i)) { continue; }

						ItemStack stack = stackList.get(i);

						// アイテムが一致して要求個数以上なら検索完了
						if (ingStack.is(stack.getItem()) && stack.getCount() >= ingStack.getCount()) {
							successFlg = true;
							requestStackList.add(ingStack);
							slotIdList.add(i);
							break;
						}

						else if (ingStack.is(ItemInit.alt_bucket_water) && stack.getItem() instanceof SMBucket bucket) {

							FluidStack fluid = bucket.getFluidStack(stack);
							if (!fluid.isEmpty() && fluid.getAmount() >= ingStack.getCount()) {
								successFlg = true;
								requestStackList.add(ingStack);
								slotIdList.add(i);
								break;
							}
						}
					}
				}

				// アイテムが一致したなら次の大分類へ
				if (successFlg) { break; }
			}

			// 中分類を回し切っても見つからなかったら終了
			if (!successFlg) {
				requestStackList.isEmpty();
				return null;
			}
		}

		return new RecipeInfo(requestStackList);
	}

	public static RecipeInfo checkObMagiaRecipe(List<ItemStack> stackList, ItemStack pageStack, ItemStack baseStack, List<Ingredient> ingList, List<Integer> countList, Ingredient recipePageList, Ingredient recipeBaseList) {

		ItemStack req_page = ItemStack.EMPTY;

		// 大分類に格納されたアイテムをリストで取得
		List<ItemStack> pageIngredList = new ArrayList<ItemStack>(Arrays.asList(recipePageList.getItems()));
		List<Integer> slotIdList = new ArrayList<>();
		boolean isPageComplete = pageIngredList.isEmpty();

		for (ItemStack page : pageIngredList) {

			if (!pageStack.isEmpty() && pageStack.is(page.getItem()) && pageStack.getCount() >= page.getCount()) {
				isPageComplete = true;
				req_page = page;
				break;
			}
		}

		if (!isPageComplete) { return null; }

		ItemStack req_base = ItemStack.EMPTY;

		// 大分類に格納されたアイテムをリストで取得
		List<ItemStack> baseIngredList = new ArrayList<ItemStack>(Arrays.asList(recipeBaseList.getItems()));
		boolean isBaseComplete = baseIngredList.isEmpty();

		for (ItemStack base : baseIngredList) {
			if ( !baseStack.isEmpty() && baseStack.is(base.getItem()) && baseStack.getCount() >= base.getCount() ) {
				isBaseComplete = true;
				req_base = base;
				break;
			}
		}

		if (!isBaseComplete) { return null; }

		// 使用するアイテムのリスト
		List<ItemStack> requestStackList = new ArrayList<>();

		for (int count = 0; count < ingList.size(); count++) {

			Ingredient ingred = ingList.get(count);
			int stackSize = countList.get(count);

			// 大分類に格納されたアイテムをリストで取得
			List<ItemStack> stackIngredList = new ArrayList<ItemStack>(Arrays.asList(ingred.getItems()));

			// 中分類を回すたびにレシピチェック成功フラグの初期化
			boolean successFlg = false;

			// 中分類（大分類に入っているアイテムごと）
			for (ItemStack ingStack : stackIngredList) {

				ingStack.setCount(stackSize);

				// 大分類を回したのが初回ならメインハンドのアイテムと比較
				if (count == 0) {
					ItemStack stack = stackList.get(0);

					// アイテムが一致して要求個数以上なら検索完了
					if (ingStack.is(stack.getItem()) && stack.getCount() >= ingStack.getCount()) {
						successFlg = true;
						requestStackList.add(ingStack);
						break;
					}
				}

				// 大分類を回した数が2回目以降ならインベントリ内をチェック
				else {

					// 小分類(インベントリアイテム)
					for (int i = 1; i < stackList.size(); i++) {

						if (slotIdList.contains(i)) { continue; }

						ItemStack stack = stackList.get(i);

						// アイテムが一致して要求個数以上なら検索完了
						if (ingStack.is(stack.getItem()) && stack.getCount() >= ingStack.getCount()) {
							successFlg = true;
							requestStackList.add(ingStack);
							slotIdList.add(i);
							break;
						}
					}
				}

				// アイテムが一致したなら次の大分類へ
				if (successFlg) { break; }
			}

			// 中分類を回し切っても見つからなかったら終了
			if (!successFlg) {
				requestStackList.isEmpty();
				return null;
			}
		}

		return new RecipeInfo(requestStackList, req_page, req_base);
	}
}
