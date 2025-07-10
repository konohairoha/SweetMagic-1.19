package sweetmagic.recipe.obmagia;

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
		int craftTime = this.readIntValue(json, 10, "crafttime", "time");		// クラフト時間の取得
		List<Ingredient> ingredList = this.readIngredList(json, "ingredients");	// 要求アイテムリストの取得
		List<Integer> countList = this.readCountList(json, "ingredients");		// 要求アイテム個数の取得
		return new ObMagiaRecipe(id, result, page, base, ingredList, countList, craftTime);
	}

	// サーバー側から受け取ったパケットから、レシピを復元する
	@Override
	public @Nullable ObMagiaRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
		ItemStack result = buf.readItem();				// クラフト後アイテムの取得
		Ingredient page = Ingredient.fromNetwork(buf);	// ページの取得
		Ingredient base = Ingredient.fromNetwork(buf);	// ベースの取得
		List<Ingredient> ingredList = this.loadIngList(buf);
		List<Integer> countList = this.loadIntList(buf);
		int craftTime = buf.readVarInt();
		return new ObMagiaRecipe(id, result, page, base, ingredList, countList, craftTime);
	}

	// サーバー側からクライアント側に同期するために、レシピをパケットに乗せる
	@Override
	public void toNetwork(FriendlyByteBuf buf, ObMagiaRecipe recipe) {
		buf.writeItem(recipe.getResultItem());	// クラフト後のアイテムの書き込み
		recipe.pageList.toNetwork(buf);			// ページの書き込み
		recipe.baseList.toNetwork(buf);			// ベースの書き込み
		this.saveIngList(buf, recipe.getIngredList());
		this.saveIntList(buf, recipe.getCountList());
		buf.writeVarInt(recipe.getCraftTime());	// クラフト時間の取得
	}
}
