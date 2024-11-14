package sweetmagic.api.iitem;

import java.util.Random;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import sweetmagic.init.capability.ICookingStatus;

public interface IFood {

	public static final String QUALITY = "qualityValue";	// 品質値のNBT名

	// 品質設定
	default void setQuality (Player player, ItemStack stack) {

		int cookLevel = ICookingStatus.getState(player).getLevel();
		int foodLevel = this.getFoodLevel();

		Random rand = new Random();
		float chance = rand.nextFloat();
		int difValue = cookLevel - foodLevel;
		int qualityValue = 0;

		if (difValue <= -3) {
			this.setQualityValue(stack, 0);
		}

		else if (difValue >= 3) {
			qualityValue = 2;

			if (chance >= 0.5F) {
				qualityValue = 3;
			}

			this.setQualityValue(stack, qualityValue);
		}

		switch (difValue) {
		case -2:
			this.setQualityValue(stack, rand.nextFloat() >= 0.75F ? 1 : 0);
			break;
		case -1:

			if (chance >= 0.9F) {
				qualityValue = 2;
			}

			else if (chance >= 0.5F) {
				qualityValue = 1;
			}

			this.setQualityValue(stack, qualityValue);
			break;
		case 0:

			if (chance >= 0.75F) {
				qualityValue = 2;
			}

			else if (chance >= 0.25F) {
				qualityValue = 1;
			}

			this.setQualityValue(stack, qualityValue);
			break;
		case 1:

			qualityValue = 1;

			if (chance >= 0.9F) {
				qualityValue = 3;
			}

			else if (chance >= 0.5F) {
				qualityValue = 2;
			}

			this.setQualityValue(stack, qualityValue);
			break;
		case 2:
			qualityValue = 1;

			if (chance >= 0.75F) {
				qualityValue = 3;
			}

			else if (chance >= 0.25F) {
				qualityValue = 2;
			}

			this.setQualityValue(stack, qualityValue);
			break;
		}
	}

	// 料理経験の取得
	default void getExpValue (Player player) {

		ICookingStatus cookStatus = ICookingStatus.getState(player);
		int cookLevel = cookStatus.getLevel();
		int foodLevel = this.getFoodLevel();
		int difValue = cookLevel - foodLevel;

		float expRate = 1F;
		int expValue = this.getExpValue();
		int expMinValue = this.getMinExpValue();

		if (difValue >= 0) {
			cookStatus.addExp(expMinValue);
		}

		else {
			cookStatus.addExp((int) Math.max(expValue * (expRate + difValue * 0.25F), expMinValue));
		}
	}

	// 食べ物回復量を取得
	default FoodProperties foodBuild(ItemStack stack, int healAmount, float saturation) {

		int qualityValue = this.getQualityValue(stack);

		switch (qualityValue) {
		case 1:
			healAmount *= 1.1F;
			saturation *= 1.1F;
			break;
		case 3:
			healAmount *= 1.25F;
			saturation *= 1.25F;
			break;
		}

		return new FoodProperties.Builder().nutrition(healAmount).saturationMod(saturation).alwaysEat().build();
	}

	// ポーション効果を付与
	default void addPotion (ItemStack stack) {
		int qualityValue = this.getQualityValue(stack);
		PotionInfo info = this.getPotionInfo();

		switch (qualityValue) {
		case 2:
			MobEffectInstance effect = new MobEffectInstance(info.getPotion(), info.getTime(), info.getLevel(), true, false);
	        PotionUtils.setPotion(stack, new Potion(effect));
			break;
		case 3:
			MobEffectInstance effect2 = new MobEffectInstance(info.getPotion(), (int) (info.getTime() * 1.5F), info.getLevel(), true, false);
	        PotionUtils.setPotion(stack, new Potion(effect2));
			break;
		}
	}

	// 品質値の取得
	default int getQualityValue (ItemStack stack) {
		if (!this.isQuality()) { return 0; }
		return this.getNBT(stack).getInt(QUALITY);
	}

	// 品質値の設定
	default void setQualityValue (ItemStack stack, int value) {
		if (!this.isQuality()) { return; }
		this.getNBT(stack).putInt(QUALITY, value);
	}

	// NBTの取得
	default CompoundTag getNBT (ItemStack stack) {

		CompoundTag tags = stack.getTag();

		// NBTがnullなら初期化
		if (tags == null) {
			tags = new CompoundTag();
			tags.putInt(QUALITY, 0);	// 品質値の初期化
		}

		if (!tags.contains(QUALITY)) {
			tags.putInt(QUALITY, 0);
		}

		return tags;
	}

	default int getExpValue () {
		switch (this.getFoodLevel()) {
		case 1:  return 1;
		case 2:  return 2;
		case 3:  return 4;
		case 4:  return 6;
		case 5:  return 9;
		case 6:  return 12;
		case 7:  return 15;
		case 8:  return 20;
		default: return 0;
		}
	}

	default int getMinExpValue () {
		return this.getFoodLevel();
	}

	// 品質の対象か
	boolean isQuality();

	// 料理難易度のレベル設定
	void setFoodLevel (int level);

	// 料理難易度のレベル取得
	int getFoodLevel();

	PotionInfo getPotionInfo();

	public record PotionInfo(MobEffect potion, int time, int level) {

		public MobEffect getPotion () {
			return this.potion;
		}

		public int getTime () {
			return this.time;
		}

		public int getLevel () {
			return this.level;
		}
	}
}
