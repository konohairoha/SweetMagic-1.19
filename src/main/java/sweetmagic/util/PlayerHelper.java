package sweetmagic.util;

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

	public static void setPotion (LivingEntity target, MobEffect effect, int level, int time) {

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

	public static void setPotion (LivingEntity target, MobEffect effect, int level, int time, boolean flag) {

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

    public static void addExp (Player player, int amount) {
        final int exp = getExpValue(player) + amount;
        player.totalExperience = exp;
        player.experienceLevel = getLevelForExp(exp);
        final int expForLevel = getExpForLevel(player.experienceLevel);
        player.totalExperience = (int) ((float) (exp - expForLevel) / (float) player.getXpNeededForNextLevel());
    }

    public static int getExpValue (Player player) {
        return (int) (getExpForLevel(player.experienceLevel) + player.experienceProgress * player.getXpNeededForNextLevel());
    }

    public static int getLevelForExp (int exp) {

        int level = 0;

        while (getExpForLevel(level) <= exp) {
            level++;
        }

        return level - 1;
    }

    public static int getExpForLevel (int level) {
        if (level == 0) { return 0; }

        if (level > 0 && level < 17) {
            return level * level + 6 * level;
        }

        else if (level > 16 && level < 32) {
            return (int) (2.5 * level * level - 40.5 * level + 360);
        }

        return (int) (4.5 * level * level - 162.5 * level + 2220);
    }
}
