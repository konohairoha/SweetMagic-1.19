package sweetmagic.recipe.base;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.registries.ForgeRegistries;

public abstract class AbstractRecipeSerializer{

	public JsonArray getArray (JsonObject json, String name) {
		return GsonHelper.getAsJsonArray(json, name);
	}

	// アイテムをjsonから読み込み
	protected ItemStack readStack(JsonObject jo, String name) {

		JsonArray resultArray = this.getArray(jo, name);
		ItemStack stack = ItemStack.EMPTY;

		for (JsonElement json : resultArray) {
			ItemStack result = this.readJsonResult(json);
			if (result.isEmpty()) { continue; }

			stack = result;
			break;
		}

		return stack;
	}

	// クラフト時間の取得
	protected int readCraftTime(JsonObject jo, String name) {

		JsonArray resultArray = this.getArray(jo, name);
		int craftTime = 10;

		for (JsonElement json : resultArray) {
			craftTime = this.readCraftTimeJson(json);
			break;
		}

		return craftTime;
	}

	// クラフト後アイテムのリストをjsonから読み込み
	protected List<ItemStack> readResultList(JsonObject jo, String name) {
		JsonArray resultArray = this.getArray(jo, name);
		List<ItemStack> list = new ArrayList<>();
		resultArray.forEach(r -> list.add(this.readJsonResult(r)));
		return list;
	}

	// jsonからクラフト後アイテム取得
	protected ItemStack readJsonResult(JsonElement je) {

		if (!je.isJsonObject()) {
			throw new JsonSyntaxException("Must be a json object");
		}

		JsonObject json = je.getAsJsonObject();
		String itemId = GsonHelper.getAsString(json, "item");
		if (itemId.equals("")) { return ItemStack.EMPTY; }

		int count = GsonHelper.getAsInt(json, "count", 1);
		return new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemId)), count);
	}

	protected List<Ingredient> readIngredList(JsonObject jo, String name) {
		JsonArray resultArray = this.getArray(jo, name);
		List<Ingredient> list = new ArrayList<>();
		resultArray.forEach(r -> list.add(this.readIngred(r.getAsJsonObject())));
		return list;
	}

	protected Ingredient readIngred(JsonObject json) {

		// 個数の取得
		int count = GsonHelper.getAsInt(json, "count", 1);

		// アイテムとタグ両方なら登録方法がおかしいので終了
		if (json.has("item") && json.has("tag")) {
			throw new JsonParseException("An ingredient entry is either a tag or an item, not both");
		}

		// アイテムなら
		else if (json.has("item")) {
			Item item = ShapedRecipe.itemFromJson(json);
			return Ingredient.of(new ItemStack(item, count));
		}

		// タグなら
		else if (json.has("tag")) {
			return Ingredient.of(TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(GsonHelper.getAsString(json, "tag"))));
		}

		// アイテムとタグでなかったならエラー
		else {
			throw new JsonParseException("An ingredient entry needs either a tag or an item");
		}
	}

	protected List<Integer> readCountList(JsonObject jo, String name) {
		return this.readIntList(jo, name, "count", 1);
	}

	protected List<Integer> readIntList(JsonObject jo, String jName, String name, int value) {
		JsonArray resultArray = this.getArray(jo, jName);
		List<Integer> list = new ArrayList<>();
		resultArray.forEach(r -> list.add(GsonHelper.getAsInt(r.getAsJsonObject(), name, value)));
		return list;
	}

	protected List<Boolean> readNBTList(JsonObject jo, String name) {
		JsonArray resultArray = this.getArray(jo, name);
		List<Boolean> list = new ArrayList<>();
		resultArray.forEach(r -> list.add(GsonHelper.getAsBoolean(r.getAsJsonObject(), "nbt")));
		return list;
	}

	protected List<Float> readChanceList(JsonObject jo, String name) {
		JsonArray resultArray = this.getArray(jo, name);
		List<Float> list = new ArrayList<>();
		resultArray.forEach(r -> list.add(GsonHelper.getAsFloat(r.getAsJsonObject(), "chance", 1F)));
		return list;
	}

	// jsonからクラフト後アイテム取得
	protected int readCraftTimeJson(JsonElement je) {

		if (!je.isJsonObject()) {
			throw new JsonSyntaxException("Must be a json object");
		}

		return GsonHelper.getAsInt(je.getAsJsonObject(), "time", 1);
	}
}
