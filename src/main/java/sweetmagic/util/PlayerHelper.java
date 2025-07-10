package sweetmagic.util;

import java.util.List;

import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import sweetmagic.api.iitem.IPorch;
import sweetmagic.init.ItemInit;

public class PlayerHelper {

	public static void setPotion(LivingEntity target, MobEffect effect, int level, int time) {

		target.removeEffect(effect);
		ItemStack leg = target.getItemBySlot(EquipmentSlot.LEGS);

		if (!leg.isEmpty() && leg.getItem() instanceof IPorch porch) {
			int count = porch.acceCount(leg, ItemInit.pendulum_necklace, 4);
			boolean isBadEffect = effect.getCategory() == MobEffectCategory.HARMFUL;

			if (count > 0) {
				time *= !isBadEffect ? 1F + count * 0.25F : 1F - count * 0.125F;
			}
		}

		target.addEffect(new MobEffectInstance(effect, time, level, true, false));
	}

	public static void setPotion(LivingEntity target, MobEffect effect, int level, int time, boolean flag) {

		target.removeEffect(effect);
		ItemStack leg = target.getItemBySlot(EquipmentSlot.LEGS);

		if (!leg.isEmpty() && leg.getItem() instanceof IPorch porch) {
			int count = porch.acceCount(leg, ItemInit.pendulum_necklace, 4);
			boolean isBadEffect = effect.getCategory() == MobEffectCategory.HARMFUL;

			if (count > 0) {
				time *= !isBadEffect ? 1F + count * 0.25F : 1F - count * 0.125F;
			}
		}

		target.addEffect(new MobEffectInstance(effect, time, level));
	}

	public static void addExp(Player player, int amount) {
		player.totalExperience += amount;
		player.experienceLevel = getLevelForExp(player.totalExperience);
		player.totalExperience = Math.max(0, player.totalExperience);
		player.experienceProgress = -(float) (getExpForLevel(player.experienceLevel) - player.totalExperience) / (float) player.getXpNeededForNextLevel();
	}

	public static int getExpValue(Player player) {
		return (int) (getExpForLevel(player.experienceLevel) + player.experienceProgress * player.getXpNeededForNextLevel());
	}

	public static int getLevelForExp(int exp) {
		int level = 0;
		while (getExpForLevel(level) <= exp) { level++; }
		return Math.max(0, level - 1);
	}

	public static int getExpForLevel(int level) {
		if (level == 0) { return 0; }

		if (level > 0 && level < 17) {
			return level * level + 6 * level;
		}

		else if (level > 16 && level < 32) {
			return (int) (2.5F * level * level - 40.5F * level + 360);
		}

		return (int) (4.5F * level * level - 162.5F * level + 2220);
	}

	public static int getLevelForExperience(int exp) {
		int level = 0;
		while (getExpForLevel(level) <= exp) { level++; }
		return level - 1;
	}

	public static List<MobEffectInstance> getEffectList(LivingEntity entity, MobEffectCategory cate) {
		return entity.getActiveEffects().stream().filter(p -> p.getEffect().getCategory() == cate).toList();
	}

	public static boolean checkClearAdvanced(Player player, ResourceLocation... advancedArray) {
		return PlayerHelper.checkClearAdvanced(player, List.of(advancedArray));
	}

	public static boolean checkClearAdvanced(Player player, Iterable<ResourceLocation> advancedList) {
		if(player.getLevel().isClientSide() || !(player instanceof ServerPlayer sPlayer)) { return false; }
		for (ResourceLocation advanced : advancedList) {
			Advancement adv = sPlayer.getLevel().getServer().getAdvancements().getAdvancement(advanced);
			if(adv == null || !sPlayer.getAdvancements().getOrStartProgress(adv).isDone()) { return false; }
		}
		return true;
	}
}
