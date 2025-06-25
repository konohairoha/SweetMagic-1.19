package sweetmagic.handler;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.ComposterBlock;
import sweetmagic.init.BlockInit;
import sweetmagic.init.ItemInit;

public class ComposterHandler {

	// コンポスターに登録
	public static void registerCompostables() {

		Object2FloatMap<ItemLike> comList = ComposterBlock.COMPOSTABLES;
		comList.put(ItemInit.sugarbell_seed, 0.3F);
		comList.put(ItemInit.glowflower_seed, 0.3F);
		comList.put(ItemInit.quartz_seed, 0.3F);
		comList.put(ItemInit.edamame, 0.3F);
		comList.put(ItemInit.raspberry, 0.3F);
		comList.put(ItemInit.sweetpotato, 0.3F);
		ItemInit.seedList.forEach(i -> comList.put(i, 0.3F));	// 種
		ItemInit.foodItemList.forEach(i -> comList.put(i, 0.4F));	// 調味料
		ItemInit.foodList.forEach(i -> comList.put(i, Math.min(1F, i.getFoodProperties().getNutrition() * 0.1F)));	// 食べ物
		BlockInit.saplingList.forEach(b -> comList.put(b, 0.3F));	// 苗木
	}
}
