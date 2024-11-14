package sweetmagic.event;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import sweetmagic.api.iitem.IHarness;
import sweetmagic.init.EnchantInit;
import sweetmagic.init.PotionInit;
import sweetmagic.util.PlayerHelper;

public class PotionEvent {

	// ポーションのイベント
	@SubscribeEvent
	public static void potionAddEvent (MobEffectEvent.Added event) {

		LivingEntity entity = event.getEntity();
		MobEffectInstance instance = event.getEffectInstance();
		if (!entity.hasEffect(PotionInit.debuff_extension) || instance.equals(PotionInit.debuff_extension) || !instance.getEffect().getCategory().equals(MobEffectCategory.HARMFUL)) { return; }

		int level = entity.getEffect(PotionInit.debuff_extension).getAmplifier() + 1;
		float rate = 1F + level * 0.25F;
		MobEffectInstance newInstance = new MobEffectInstance(instance.getEffect(), (int) (instance.getDuration() * rate), instance.getAmplifier());
		instance.update(newInstance);
	}

	// 回復のイベント
	@SubscribeEvent
	public static void healEvent (LivingHealEvent event) {

		// 回復増加が付いていないなら終了
		LivingEntity entity = event.getEntity();
		if (entity == null || !entity.hasEffect(PotionInit.increased_recovery)) { return; }

		// 回復量が0以下なら終了
		float amount = event.getAmount();
		if(amount <= 0F) { return; }

		// 回復増加レベル×25%アップ
		int level = entity.getEffect(PotionInit.increased_recovery).getAmplifier() + 1;
		event.setAmount(amount * (1 + (level * 0.25F) ) );
	}

	// ノックバックのイベント
	@SubscribeEvent
	public static void knockBackEvent (LivingKnockBackEvent event) {
		LivingEntity entity = event.getEntity();
		if (!entity.hasEffect(PotionInit.resistance_blow) && !hasHarness(entity)) { return; }

		int level = entity.getEffect(PotionInit.resistance_blow).getAmplifier() + 1;
		float strength = Math.max(0, event.getStrength() - level * 0.4F);
		event.setStrength(strength);

		if (strength <= 0) {
			event.setRatioX(0D);
			event.setRatioZ(0D);
			event.setCanceled(true);
		}
	}

	public static boolean hasHarness (LivingEntity entity) {
		ItemStack feet = entity.getItemBySlot(EquipmentSlot.FEET);
		if ( !(feet.getItem() instanceof IHarness harness)) { return false; }

		int mf = harness.getMF(feet);
		int useMF = 300;
		int costDown = Math.min(99, harness.getEnchantLevel(EnchantInit.mfCostDown, feet) * 7);

		if (costDown > 0) {
			useMF *= (100 - costDown) / 100F;
		}

		if (mf < useMF) { return false; }

		harness.setMF(feet, mf - useMF);
		PlayerHelper.setPotion(entity, PotionInit.resistance_blow, harness.getTier(), 600);
		return true;
	}

	// ノックバックのイベント
	@SubscribeEvent
	public static void teleportEvent (EntityTeleportEvent event) {
		if ( !(event.getEntity() instanceof LivingEntity living) || !living.hasEffect(PotionInit.gravity)) { return; }
		event.setCanceled(true);
	}
}
