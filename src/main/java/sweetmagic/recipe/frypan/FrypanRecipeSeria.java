package sweetmagic.recipe.frypan;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import sweetmagic.recipe.base.AbstractRecipeSerializer;

public class FrypanRecipeSeria extends AbstractRecipeSerializer implements RecipeSerializer<FrypanRecipe> {

	// jsonからレシピを生成する
	@Override
	public FrypanRecipe fromJson(ResourceLocation id, JsonObject json) {
		List<ItemStack> resultList = this.readResultList(json, "result");		// クラフト後のアイテムリスト取得
		List<Ingredient> ingredList = this.readIngredList(json, "ingredients");	// 要求アイテムリストの取得
		List<Integer> countList = this.readCountList(json, "ingredients");		// 要求アイテム個数の取得
		return new FrypanRecipe(id, resultList, ingredList, countList);
	}

	// サーバー側から受け取ったパケットから、レシピを復元する
	@Override
	public @Nullable FrypanRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {

		int resultSize = buf.readVarInt();
		List<ItemStack> resultList = new ArrayList<>();

		for (int k = 0; k < resultSize; k++) {
			resultList.add(buf.readItem());
		}

		// Ingredientの個数を読み込む
		int ingredSize = buf.readVarInt();
		List<Ingredient> ingredList = new ArrayList<>();

		// 読み取った個数の分だけ、繰り返し処理でIngredientを読み込む
		for (int i = 0; i < ingredSize; i++) {
			ingredList.add(Ingredient.fromNetwork(buf));
		}

		// Ingredientの個数を読み込む
		int countSize = buf.readVarInt();
		List<Integer> countList = new ArrayList<>();

		// 読み取った個数の分だけ、繰り返し処理でIntegerを読み込む
		for (int i = 0; i < countSize; i++) {
			countList.add(buf.readVarInt());
		}

		// 読み取った要素からレシピを作成
		return new FrypanRecipe(id, resultList, ingredList, countList);
	}

	// サーバー側からクライアント側に同期するために、レシピをパケットに乗せる
	@Override
	public void toNetwork(FriendlyByteBuf buf, FrypanRecipe recipe) {
		this.saveStackList(buf, recipe.getResultList());
		this.saveIngList(buf, recipe.getIngredList());
		this.saveIntList(buf, recipe.getCountList());
	}
}
