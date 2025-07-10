package sweetmagic.recipe.juice_maker;

import java.util.List;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import sweetmagic.recipe.base.AbstractRecipeSerializer;

public class JuiceMakerRecipeSeria extends AbstractRecipeSerializer implements RecipeSerializer<JuiceMakerRecipe> {

	// jsonからレシピを生成する
	@Override
	public JuiceMakerRecipe fromJson(ResourceLocation id, JsonObject json) {
		List<ItemStack> resultList = this.readResultList(json, "result");		// クラフト後のアイテムリスト取得
		List<Ingredient> ingredList = this.readIngredList(json, "ingredients");	// 要求アイテムリストの取得
		List<Integer> countList = this.readCountList(json, "ingredients");		// 要求アイテム個数の取得
		return new JuiceMakerRecipe(id, resultList, ingredList, countList);
	}

	// サーバー側から受け取ったパケットから、レシピを復元する
	@Override
	public @Nullable JuiceMakerRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
		List<ItemStack> resultList = this.loadStackList(buf);
		List<Ingredient> ingredList = this.loadIngList(buf);
		List<Integer> countList = this.loadIntList(buf);
		return new JuiceMakerRecipe(id, resultList, ingredList, countList);
	}

	// サーバー側からクライアント側に同期するために、レシピをパケットに乗せる
	@Override
	public void toNetwork(FriendlyByteBuf buf, JuiceMakerRecipe recipe) {
		this.saveStackList(buf, recipe.getResultList());
		this.saveIngList(buf, recipe.getIngredList());
		this.saveIntList(buf, recipe.getCountList());
	}
}
