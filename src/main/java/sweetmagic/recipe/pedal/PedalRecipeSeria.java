package sweetmagic.recipe.pedal;

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

public class PedalRecipeSeria extends AbstractRecipeSerializer implements RecipeSerializer<PedalRecipe> {

	// jsonからレシピを生成する
	@Override
	public PedalRecipe fromJson(ResourceLocation id, JsonObject json) {
		List<ItemStack> resultList = this.readResultList(json, "result");		// クラフト後のアイテムリスト取得
		List<Ingredient> ingredList = this.readIngredList(json, "ingredients");	// 要求アイテムリストの取得
		List<Integer> countList = this.readCountList(json, "ingredients");		// 要求アイテム個数の取得
		List<Integer> mfList = this.readIntList(json, "result", "mf", 0);		// 要求MF数の取得
		List<Boolean> nbtList = this.readNBTList(json, "result");				// NBT保持の取得
		return new PedalRecipe(id, resultList, ingredList, countList, mfList, nbtList);
	}

	// サーバー側から受け取ったパケットから、レシピを復元する
	@Override
	public @Nullable PedalRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {

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

		// Ingredientの個数を読み込む
		int countSize = buffer.readVarInt();
		List<Integer> countList = new ArrayList<>();

		// 読み取った個数の分だけ、繰り返し処理でIntegerを読み込む
		for (int i = 0; i < countSize; i++) {
			countList.add(buffer.readVarInt());
		}

		// Ingredientの個数を読み込む
		int mfSize = buffer.readVarInt();
		List<Integer> mfList = new ArrayList<>();

		// 読み取った個数の分だけ、繰り返し処理でIntegerを読み込む
		for (int i = 0; i < mfSize; i++) {
			mfList.add(buffer.readVarInt());
		}

		// Ingredientの個数を読み込む
		int nbtSize = buffer.readVarInt();
		List<Boolean> nbtList = new ArrayList<>();

		// 読み取った個数の分だけ、繰り返し処理でIntegerを読み込む
		for (int i = 0; i < nbtSize; i++) {
			nbtList.add(buffer.readBoolean());
		}

		// 読み取った要素からレシピを作成
		return new PedalRecipe(id, resultList, ingredList, countList, mfList, nbtList);
	}

	// サーバー側からクライアント側に同期するために、レシピをパケットに乗せる
	@Override
	public void toNetwork(FriendlyByteBuf buffer, PedalRecipe recipe) {

		List<ItemStack> resultList = recipe.getResultList();
		buffer.writeVarInt(resultList.size());
		resultList.forEach(s -> buffer.writeItem(s));

		List<Ingredient> ingredList = recipe.getIngredList();
		buffer.writeVarInt(ingredList.size());
		ingredList.forEach(t -> t.toNetwork(buffer));

		List<Integer> countList = recipe.getCountList();
		buffer.writeVarInt(countList.size());
		countList.forEach(i -> buffer.writeVarInt(i));

		List<Integer> mfList = recipe.getMFList();
		buffer.writeVarInt(mfList.size());
		mfList.forEach(i -> buffer.writeVarInt(i));

		List<Boolean> nbtList = recipe.getNBTList();
		buffer.writeVarInt(nbtList.size());
		nbtList.forEach(b -> buffer.writeBoolean(b));
	}
}
