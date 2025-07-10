package sweetmagic.recipe.base;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.registries.ForgeRegistries;

public abstract class AbstractRecipeSerializer {

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
	protected int readIntValue(JsonObject jo, int minValue, String name, String type) {

		JsonArray resultArray = this.getArray(jo, name);
		int craftTime = minValue;

		for (JsonElement json : resultArray) {
			craftTime = this.readIntValueJson(json, type);
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
	protected int readIntValueJson(JsonElement je, String type) {

		if (!je.isJsonObject()) {
			throw new JsonSyntaxException("Must be a json object");
		}

		return GsonHelper.getAsInt(je.getAsJsonObject(), type, 1);
	}

	public List<ItemStack> loadStackList(FriendlyByteBuf buf) {
		int size = buf.readVarInt();
		List<ItemStack> list = new ArrayList<>();
		for (int k = 0; k < size; k++) { list.add(buf.readItem()); 	}
		return list;
	}

	public List<Ingredient> loadIngList(FriendlyByteBuf buf) {
		int size = buf.readVarInt();
		List<Ingredient> list = new ArrayList<>();
		for (int i = 0; i < size; i++) { list.add(Ingredient.fromNetwork(buf)); }
		return list;
	}

	public List<Integer> loadIntList(FriendlyByteBuf buf) {
		int size = buf.readVarInt();
		List<Integer> list = new ArrayList<>();
		for (int i = 0; i < size; i++) { list.add(buf.readVarInt()); }
		return list;
	}

	public List<Float> loadFltList(FriendlyByteBuf buf) {
		int size = buf.readVarInt();
		List<Float> list = new ArrayList<>();
		for (int i = 0; i < size; i++) { list.add(buf.readFloat()); }
		return list;
	}

	public List<Boolean> loadBlnList(FriendlyByteBuf buf) {
		int size = buf.readVarInt();
		List<Boolean> list = new ArrayList<>();
		for (int i = 0; i < size; i++) { list.add(buf.readBoolean()); }
		return list;
	}

	public void saveStackList(FriendlyByteBuf buf, List<ItemStack> list) {
		buf.writeVarInt(list.size());
		list.forEach(s -> buf.writeItem(s));
	}

	public void saveIngList(FriendlyByteBuf buf, List<Ingredient> list) {
		buf.writeVarInt(list.size());
		list.forEach(t -> t.toNetwork(buf));
	}

	public void saveIntList(FriendlyByteBuf buf, List<Integer> list) {
		buf.writeVarInt(list.size());
		list.forEach(i -> buf.writeVarInt(i));
	}

	public void saveFltList(FriendlyByteBuf buf, List<Float> list) {
		buf.writeVarInt(list.size());
		list.forEach(f -> buf.writeFloat(f));
	}

	public void saveBlnList(FriendlyByteBuf buf, List<Boolean> list) {
		buf.writeVarInt(list.size());
		list.forEach(b -> buf.writeBoolean(b));
	}
}
