package sweetmagic.recipe.furniture;

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

public class FurnitureRecipeSeria extends AbstractRecipeSerializer implements RecipeSerializer<FurnitureRecipe> {

	// jsonからレシピを生成する
	@Override
	public FurnitureRecipe fromJson(ResourceLocation id, JsonObject json) {
		List<ItemStack> resultList = this.readResultList(json, "result");		// クラフト後のアイテムリスト取得
		List<Ingredient> ingredList = this.readIngredList(json, "ingredients");	// 要求アイテムリストの取得
		return new FurnitureRecipe(id, resultList, ingredList);
	}

	// サーバー側から受け取ったパケットから、レシピを復元する
	@Override
	public @Nullable FurnitureRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {

		int resultSize = buffer.readVarInt();
		List<ItemStack> resultList = new ArrayList<>();

		for (int k = 0; k < resultSize; k++) {
			resultList.add(buffer.readItem());
		}

		// Ingredientの個数を読み込む
		int ingredSize = buffer.readVarInt();
		List<Ingredient> ingredList = new ArrayList<>();

		// 読み取った個数の分だけ、繰り返し処理でIngredientを読み込む
		for (int i = 0; i < ingredSize; i++) {
			ingredList.add(Ingredient.fromNetwork(buffer));
		}

		// 読み取った要素からレシピを作成
		return new FurnitureRecipe(id, resultList, ingredList);
	}

	// サーバー側からクライアント側に同期するために、レシピをパケットに乗せる
	@Override
	public void toNetwork(FriendlyByteBuf buffer, FurnitureRecipe recipe) {

		List<ItemStack> resultList = recipe.getResultList();
		buffer.writeVarInt(resultList.size());
		resultList.forEach(s -> buffer.writeItem(s));

		List<Ingredient> ingredList = recipe.getIngredList();
		buffer.writeVarInt(ingredList.size());
		ingredList.forEach(t -> t.toNetwork(buffer));
	}
}
