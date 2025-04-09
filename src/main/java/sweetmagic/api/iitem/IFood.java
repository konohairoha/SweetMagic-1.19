package sweetmagic.api.iitem;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import sweetmagic.api.iblock.IFoodExpBlock;
import sweetmagic.api.iitem.info.FoodInfo;
import sweetmagic.config.SMConfig;
import sweetmagic.init.ItemInit;
import sweetmagic.init.PotionInit;
import sweetmagic.init.capability.ICookingStatus;
import sweetmagic.util.PlayerHelper;
import sweetmagic.util.WorldHelper;

public interface IFood {

	public static final String QUALITY = "qualityValue";	// 品質値のNBT名

	// 品質設定
	default int setQuality(BlockPos pos, Player player, ItemStack stack, float baseChance) {
		if (!this.isQuality() || !SMConfig.foodQuality.get()) { return 0; }

		int cookLevel = ICookingStatus.getState(player).getLevel();
		int foodLevel = this.getFoodLevel();

		Random rand = new Random();
		float chance = rand.nextFloat() + baseChance;
		int difValue = cookLevel - foodLevel;
		int qualityValue = 0;

		int count = 0;
		Level world = player.level;
		List<IFoodExpBlock> blockList = new ArrayList<>();
		Iterable<BlockPos> posList = WorldHelper.getRangePos(pos, 16);
		ItemStack leg = player.getItemBySlot(EquipmentSlot.LEGS);

		for (BlockPos p :posList) {
			if (world.getBlockState(p).getBlock() instanceof IFoodExpBlock fb && fb.isChanceUp() && blockList.contains(fb)) {
				if (count++ >= 3) { break; }
				blockList.add(fb);
			}
		}

		chance += count * 0.05F;

		if (!leg.isEmpty() && leg.getItem() instanceof IPorch porch && porch.hasAcce(leg, ItemInit.mysterious_fork)) {
			chance += 0.1F;
		}

		// 品質レベルと料理レベルの差が-3以上ある
		if (difValue <= -3) {
			qualityValue = 0;
		}

		// 品質レベルと料理レベルの差が3以上ある
		else if (difValue >= 3) {
			float difChance = (1F - 0.05F * (difValue - 3));
			qualityValue = chance >= difChance ? 4 : 3;
		}

		switch (difValue) {
		case -2:
			qualityValue = chance >= 0.75F ? 1 : 0;
			break;
		case -1:

			if (chance >= 0.9F) {
				qualityValue = 2;
			}

			else if (chance >= 0.5F) {
				qualityValue = 1;
			}
			break;
		case 0:

			if (chance >= 0.75F) {
				qualityValue = 2;
			}

			else if (chance >= 0.25F) {
				qualityValue = 1;
			}
			break;
		case 1:

			qualityValue = 1;

			if (chance >= 0.9F) {
				qualityValue = 3;
			}

			else if (chance >= 0.5F) {
				qualityValue = 2;
			}
			break;
		case 2:
			qualityValue = 1;

			if (chance >= 0.75F) {
				qualityValue = 3;
			}

			else if (chance >= 0.25F) {
				qualityValue = 2;
			}
			break;
		}

		if (player.getDisplayName().getString().equals("Konohairoha") && stack.is(ItemInit.omelet_rice)) {
			qualityValue = 4;
		}

		return qualityValue;
	}

	// 料理経験の取得
	public static void getExpValue(Player player, FoodInfo info, int count) {
		if (!SMConfig.foodQuality.get()) { return; }

		IFood food = info.getFood();
		ICookingStatus cookStatus = ICookingStatus.getState(player);
		int cookLevel = cookStatus.getLevel();	// プレイヤー料理レベル
		int foodLevel = food.getFoodLevel();	// 料理難易度
		int difValue = cookLevel - foodLevel;	// プレイヤー料理レベル - 料理難易度

		float expRate = 1F * count;
		int expValue = food.getExpValue();
		int expMinValue = food.getMinExpValue();

		ItemStack leg = player.getItemBySlot(EquipmentSlot.LEGS);
		if (!leg.isEmpty() && leg.getItem() instanceof IPorch portch && portch.hasAcce(leg, ItemInit.mysterious_fork)) {
			expValue *= 1.25;
			expMinValue *= 1.1;
		}

		if (difValue > 0 && cookLevel <= 10) {
			cookStatus.addExp((int) Math.max(expValue * Math.min(expRate - difValue * 2, 0.1F), Math.max(1, expMinValue * 0.5F) * count));
		}

		else {
			cookStatus.addExp((int) Math.max(expValue * (expRate + difValue), Math.max(1, expMinValue * 0.75F) * count));
		}
	}

	// 食べ物回復量を取得
	default FoodProperties foodBuild(ItemStack stack, int healAmount, float saturation) {

		int qualityValue = this.getQualityValue(stack);

		switch (qualityValue) {
		case 1:
		case 2:
			healAmount *= 1.1F;
			saturation *= 1.1F;
			break;
		case 3:
			healAmount *= 1.25F;
			saturation *= 1.25F;
			break;
		case 4:
			healAmount *= 1.5F;
			saturation *= 1.5F;
			break;
		}

		return new FoodProperties.Builder().nutrition(healAmount).saturationMod(saturation).alwaysEat().build();
	}

	// ポーション効果を付与
	default void addPotion(LivingEntity entity, ItemStack stack, int level) {
		PotionInfo info = this.getPotionInfo();

		switch (level) {
		case 2:
			PlayerHelper.setPotion(entity, info.potion(), info.level(), info.time());
			break;
		case 3:
			PlayerHelper.setPotion(entity, info.potion(), info.level(), (int) (info.time() * 1.5F));
			break;
		}
	}

	// 品質値の取得
	default int getQualityValue(ItemStack stack) {
		if (!this.isQuality()) { return 0; }
		return this.getNBT(stack).getInt(QUALITY);
	}

	// 品質値の設定
	default void setQualityValue(ItemStack stack, int value) {
		if (!this.isQuality()) { return; }
		this.getNBT(stack).putInt(QUALITY, value);
	}

	// NBTの取得
	default CompoundTag getNBT(ItemStack stack) {

		CompoundTag tags = stack.getTag();

		// NBTがnullなら初期化
		if (tags == null) {
			tags = new CompoundTag();
			stack.setTag(tags);
			tags.putInt(QUALITY, 0);	// 品質値の初期化
		}

		else if (!tags.contains(QUALITY)) {
			tags.putInt(QUALITY, 0);
		}

		return tags;
	}

	default int getExpValue() {
		switch (this.getFoodLevel()) {
		case 2:  return 2;
		case 3:  return 6;
		case 4:  return 13;
		case 5:  return 20;
		case 6:  return 28;
		case 7:  return 36;
		case 8:  return 50;
		default: return 1;
		}
	}

	default int getMinExpValue() {
		return this.getFoodLevel();
	}

	// 品質の対象か
	boolean isQuality();

	// 料理難易度のレベル設定
	void setFoodLevel(int level);

	// 料理難易度のレベル取得
	int getFoodLevel();

	FoodType getFoodType();

	default PotionInfo getPotionInfo() {
		switch(this.getFoodType()) {
		case Fermentation:	return new PotionInfo(PotionInit.reflash_effect, 0, 600);
		case Baked: 		return new PotionInfo(PotionInit.mfcostdown, 0, 1200);
		case Simmered:		return new PotionInfo(MobEffects.DAMAGE_RESISTANCE, 0, 1200);
		case Stir:			return new PotionInfo(PotionInit.damage_cut, 0, 1200);
		case Fried:			return new PotionInfo(PotionInit.recast_reduction, 0, 600);
		case Rice:			return new PotionInfo(MobEffects.ABSORPTION, 2, 1200);
		case Fish:			return new PotionInfo(MobEffects.CONDUIT_POWER, 0, 600);
		case Bread:			return new PotionInfo(MobEffects.DIG_SPEED, 0, 1200);
		case Japanese:		return new PotionInfo(PotionInit.aether_armor, 0, 1200);
		case Western:		return new PotionInfo(PotionInit.aether_barrier, 0, 1200);
		case Cookie:		return new PotionInfo(PotionInit.resistance_blow, 1, 1200);
		case Cake:			return new PotionInfo(PotionInit.mfcostdown, 0, 1200);
		case Chilling:		return new PotionInfo(PotionInit.increased_recovery, 0, 1200);
		case Salad:			return new PotionInfo(PotionInit.regeneration, 0, 200);
		default:			return new PotionInfo(PotionInit.increased_experience, 0, 1200);
		}
	}

	public enum FoodType {
		Fermentation,	// 発光
		Baked,			// 焼き物
		Simmered,		// 煮物
		Stir,			// 炒め物
		Fried,			// 揚げ物
		Rice,			// 米
		Fish,			// 魚
		Bread,			// パン
		Japanese,		// 和菓子
		Western,		// 洋菓子
		Cookie,			// クッキー
		Cake,			// ケーキ
		Chilling,		// 冷蔵
		Salad,			// サラダ
		Drink			// 飲み物
	}

	public record PotionInfo(MobEffect potion, int level, int time) { }

	// 杖の取得
	public static IFood getFood(ItemStack stack) {
		return (IFood) stack.getItem();
	}
}
