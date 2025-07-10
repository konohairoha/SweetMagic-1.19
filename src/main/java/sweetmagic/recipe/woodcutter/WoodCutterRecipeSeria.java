package sweetmagic.recipe.woodcutter;

import java.util.List;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import sweetmagic.recipe.base.AbstractRecipeSerializer;

public class WoodCutterRecipeSeria extends AbstractRecipeSerializer implements RecipeSerializer<WoodCutterRecipe> {

	// jsonからレシピを生成する
	@Override
	public WoodCutterRecipe fromJson(ResourceLocation id, JsonObject json) {
		List<ItemStack> resultList = this.readResultList(json, "result");		// クラフト後のアイテムリスト取得
		List<Ingredient> ingredList = this.readIngredList(json, "ingredients");	// 要求アイテムリストの取得
		List<Integer> countList = this.readCountList(json, "ingredients");		// 要求アイテム個数の取得
		List<Integer> minList = this.readIntList(json, "result", "min", 0);		// 要求最低値の取得
		List<Integer> maxList = this.readIntList(json, "result", "max", 0);		// 要求最高値の取得
//		List<Integer> mfList = this.readIntList(json, "result", "mf", 0);		// 要求MF数の取得
		int mf = this.readIntValue(json, 200, "request", "mf");					// 要求MF数の取得
		return new WoodCutterRecipe(id, resultList, ingredList, countList, minList, maxList, mf);
	}

	// サーバー側から受け取ったパケットから、レシピを復元する
	@Override
	public @Nullable WoodCutterRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
		List<ItemStack> resultList = this.loadStackList(buf);
		List<Ingredient> ingredList = this.loadIngList(buf);
		List<Integer> countList = this.loadIntList(buf);
		List<Integer> minList = this.loadIntList(buf);
		List<Integer> maxList = this.loadIntList(buf);
		int mf = buf.readVarInt();
		return new WoodCutterRecipe(id, resultList, ingredList, countList, minList, maxList, mf);
	}

	// サーバー側からクライアント側に同期するために、レシピをパケットに乗せる
	@Override
	public void toNetwork(FriendlyByteBuf buf, WoodCutterRecipe recipe) {
		this.saveStackList(buf, recipe.getResultList());
		this.saveIngList(buf, recipe.getIngredList());
		this.saveIntList(buf, recipe.getCountList());
		this.saveIntList(buf, recipe.getMinList());
		this.saveIntList(buf, recipe.getMaxList());
		buf.writeVarInt(recipe.getMF());
	}
}
