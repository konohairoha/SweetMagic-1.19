package sweetmagic.recipe.obmagia;

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

public class ObMagiaRecipeSeria extends AbstractRecipeSerializer implements RecipeSerializer<ObMagiaRecipe> {

	// jsonからレシピを生成する
	@Override
	public ObMagiaRecipe fromJson(ResourceLocation id, JsonObject json) {
		ItemStack result = this.readStack(json, "result");						// クラフト後のアイテム取得
		Ingredient page = this.readIngredList(json, "page").get(0);				// ページ取得
		Ingredient base = this.readIngredList(json, "base").get(0);				// ベースの取得
		int craftTime = this.readCraftTime(json, "crafttime");					// クラフト時間の取得
		List<Ingredient> ingredList = this.readIngredList(json, "ingredients");	// 要求アイテムリストの取得
		List<Integer> countList = this.readCountList(json, "ingredients");		// 要求アイテム個数の取得
		return new ObMagiaRecipe(id, result, page, base, ingredList, countList, craftTime);
	}

	// サーバー側から受け取ったパケットから、レシピを復元する
	@Override
	public @Nullable ObMagiaRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {


		ItemStack result = buffer.readItem();				// クラフト後アイテムの取得
		Ingredient page = Ingredient.fromNetwork(buffer);	// ページの取得
		Ingredient base = Ingredient.fromNetwork(buffer);	// ベースの取得

		// Ingredientの個数を読み込む
		int ingredSize = buffer.readVarInt();
		List<Ingredient> ingredList = new ArrayList<>();

		// 読み取った個数の分だけ、繰り返し処理でIngredientを読み込む
		for (int i = 0; i < ingredSize; i++) {
			ingredList.add(Ingredient.fromNetwork(buffer));
		}

		// Ingredientの個数を読み込む
		int countSize = buffer.readVarInt();
		List<Integer> countList = new ArrayList<>();

		// 読み取った個数の分だけ、繰り返し処理でIntegerを読み込む
		for (int i = 0; i < countSize; i++) {
			countList.add(buffer.readVarInt());
		}

		// クラフト時間の取得
		int craftTime = buffer.readVarInt();

		// 読み取った要素からレシピを作成
		return new ObMagiaRecipe(id, result, page, base, ingredList, countList, craftTime);
	}

	// サーバー側からクライアント側に同期するために、レシピをパケットに乗せる
	@Override
	public void toNetwork(FriendlyByteBuf buffer, ObMagiaRecipe recipe) {

		buffer.writeItem(recipe.getResultItem());	// クラフト後のアイテムの書き込み
		recipe.pageList.toNetwork(buffer);			// ページの書き込み
		recipe.baseList.toNetwork(buffer);			// ベースの書き込み

		List<Ingredient> ingredList = recipe.getIngredList();
		buffer.writeVarInt(ingredList.size());
		ingredList.forEach(t -> t.toNetwork(buffer));

		List<Integer> countList = recipe.getCountList();
		buffer.writeVarInt(countList.size());
		countList.forEach(i -> buffer.writeVarInt(i));

		buffer.writeVarInt(recipe.getCraftTime());	// クラフト時間の取得
	}
}
