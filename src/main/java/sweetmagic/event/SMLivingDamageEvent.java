package sweetmagic.event;

import java.util.List;
import java.util.function.Predicate;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.api.iitem.IMagicBook;
import sweetmagic.api.iitem.IMagicItem;
import sweetmagic.api.iitem.IPorch;
import sweetmagic.api.iitem.IRobe;
import sweetmagic.api.iitem.IWand;
import sweetmagic.api.iitem.info.BookInfo;
import sweetmagic.api.iitem.info.MagicInfo;
import sweetmagic.api.iitem.info.PorchInfo;
import sweetmagic.api.iitem.info.RobeInfo;
import sweetmagic.init.ItemInit;
import sweetmagic.init.PotionInit;
import sweetmagic.init.TagInit;
import sweetmagic.init.entity.projectile.AbstractMagicShot;
import sweetmagic.init.entity.projectile.FireMagicShot;
import sweetmagic.util.PlayerHelper;
import sweetmagic.util.SMDamage;

public class SMLivingDamageEvent {

	private static boolean hasQuillpen = false;

	// ダメージを受けたときのイベント
	@SubscribeEvent
	public static void onHurt(LivingHurtEvent event) {
		float damage = event.getAmount();
		if (event.getEntity() == null || damage <= 0) { return; }

		LivingEntity target = event.getEntity();
		DamageSource src = event.getSource();
		Entity attackEntity = src.getEntity();
		final float oldDamage = damage;
		hasQuillpen = false;

		ItemStack chest = target.getItemBySlot(EquipmentSlot.CHEST);
		ItemStack leg = target.getItemBySlot(EquipmentSlot.LEGS);

		// 魔法ダメージなら
		if (src.isMagic() && src != SMDamage.poisonDamage) {

			// ローブを着ているなら
			if (!chest.isEmpty() && chest.getItem() instanceof IRobe) {
				damage = SMLivingDamageEvent.robeMagicDmageCut(target, damage, src, new RobeInfo(chest));
			}

			// ポーチを着ているなら
			if (!leg.isEmpty() && leg.getItem() instanceof IPorch) {
				damage = SMLivingDamageEvent.porchMagicDmageCut(target, damage, src, new PorchInfo(leg));
			}
		}

		// えんちちーによる攻撃なら
		if (attackEntity != null && attackEntity instanceof LivingEntity attacker) {

			// ポーションによるダメージ増減
			damage = SMLivingDamageEvent.potionDamageCut(target, attacker, src, damage);

			// プレイヤーなら魔術書のダメージ無効化を発動
			if (damage > 0F && target instanceof Player player) {
				damage = SMLivingDamageEvent.bookCutDamage(player, attacker, damage);
			}

			// ローブを着ているなら
			if (damage > 0F && !chest.isEmpty() && chest.getItem() instanceof IRobe) {
				damage = SMLivingDamageEvent.robeSMMobDmageCut(target, damage, attacker, new RobeInfo(chest));
			}

			// ポーチを着ているなら
			if (damage > 0F && !leg.isEmpty() && leg.getItem() instanceof IPorch) {
				damage = SMLivingDamageEvent.porchMobDmageCut(target, attacker, oldDamage, damage, src, new PorchInfo(leg));
			}

			ItemStack legAttack = attacker.getItemBySlot(EquipmentSlot.LEGS);
			if (!legAttack.isEmpty() && legAttack.getItem() instanceof IPorch porch) {

				// ポーションによるダメージ上昇
				damage = SMLivingDamageEvent.porchMobDmageUp(attacker, damage, src, new PorchInfo(legAttack));

				// 被ダメージが死亡した際
				if (damage >= target.getHealth() && src.getDirectEntity() instanceof AbstractMagicShot magic) {
					SMLivingDamageEvent.targetKill(target, attacker, magic, new PorchInfo(legAttack));
				}
			}

			// ポーチを着ているなら
			if (oldDamage > damage && !leg.isEmpty() && leg.getItem() instanceof IPorch) {
				SMLivingDamageEvent.porchAftereffect(attacker, oldDamage - damage, src, new PorchInfo(leg));
			}
		}

		if(damage > 0) {
			damage = SMLivingDamageEvent.barrierCut(target, leg, damage);
		}

		event.setAmount(damage);
	}

	// ローブでの魔法ダメージカット
	public static float robeMagicDmageCut(LivingEntity target, float damage, DamageSource src, RobeInfo info) {

		IRobe robe = info.getRobe();
		ItemStack stack = info.getStack();

		if (src.isMagic() && !robe.isMFEmpty(stack)) {
			info.shrinkMF((int) damage * 25);
			damage *= robe.getMagicDamageCut();
		}

		return damage;
	}

	// ローブでのスイマジモブからのダメージカット
	public static float robeSMMobDmageCut(LivingEntity target, float damage, LivingEntity attackEntity, RobeInfo info) {

		IRobe robe = info.getRobe();
		ItemStack stack = info.getStack();

		// スイマジモブからのダメージカット
		if (attackEntity instanceof ISMMob && !robe.isMFEmpty(stack)) {
			info.shrinkMF((int) damage * 25);
			damage *= robe.getSMMobDamageCut();
		}

		return damage;
	}

	// ポーチによるダメージカット
	public static float porchMagicDmageCut(LivingEntity entity, float damage, DamageSource src, PorchInfo info) {
		Level world = entity.getLevel();
		IPorch porch = info.getPorch();
		ItemStack stack = info.getStack();
		if (porch.getStackList(stack).isEmpty()) { return damage; }

		if ((world.getDayTime() % 24000 >= 12000 || entity.hasEffect(MobEffects.NIGHT_VISION)) && porch.hasAcce(stack, ItemInit.veil_darkness)) {
			float dameRate = porch.acceCount(stack, ItemInit.veil_darkness, 5) * 0.1F;
			damage *= (1F - dameRate);
		}

		hasQuillpen = porch.hasAcce(stack, ItemInit.magician_quillpen);

		return damage;
	}

	// ポーチによるダメージカット
	public static float porchMobDmageCut(LivingEntity entity, LivingEntity attacker, float oldDamage, float damage, DamageSource src, PorchInfo info) {
		Level world = entity.getLevel();
		IPorch porch = info.getPorch();
		ItemStack stack = info.getStack();
		if (porch.getStackList(stack).isEmpty()) { return damage; }

		// 夜か暗視ならダメージ軽減
		if ((world.getDayTime() % 24000 >= 12000 || entity.hasEffect(MobEffects.NIGHT_VISION)) && porch.hasAcce(stack, ItemInit.veil_darkness)) {
			float dameRate = porch.acceCount(stack, ItemInit.veil_darkness, 5) * 0.1F;
			damage *= (1F - dameRate);
		}

		if (porch.hasAcce(stack, ItemInit.emelald_pias)) {
			float dameRate = porch.acceCount(stack, ItemInit.emelald_pias, 10) * 0.1F;
			damage *= (1F + dameRate);
		}

		if (porch.hasAcce(stack, ItemInit.poison_fang) && entity instanceof Player player) {

			ItemCooldowns cool = player.getCooldowns();
			if(!cool.isOnCooldown(ItemInit.poison_fang)) {
				double counterDamage = oldDamage * 0.25D;
				cool.addCooldown(ItemInit.poison_fang, (int) (counterDamage * 20));
				attacker.setHealth((float) Math.max(attacker.getHealth(), counterDamage));
			}
		}

		return damage;
	}

	// ポーチによるダメージ上昇
	public static float porchMobDmageUp(LivingEntity entity, float damage, DamageSource src, PorchInfo info) {
		Level world = entity.getLevel();
		IPorch porch = info.getPorch();
		ItemStack stack = info.getStack();
		if (porch.getStackList(stack).isEmpty()) { return damage; }

		// 夜か暗視ならダメージ軽減
		if ((world.getDayTime() % 24000 >= 12000 || entity.hasEffect(MobEffects.NIGHT_VISION)) && porch.hasAcce(stack, ItemInit.veil_darkness)) {
			float dameRate = porch.acceCount(stack, ItemInit.veil_darkness, 5) * 0.1F;
			damage *= (1F + dameRate);
		}

		return damage;
	}

	// ポーションによるダメージ増減
	public static float potionDamageCut(LivingEntity target, LivingEntity attacker, DamageSource src, float damage) {

		// 召喚保護なら攻撃を無効化
		if (target.hasEffect(PotionInit.magic_array)) { return 0; }

		// エーテルシールドなら攻撃を無効化
		if (target.hasEffect(PotionInit.aether_shield)) {

			int level = target.getEffect(PotionInit.aether_shield).getAmplifier() + 1;

			// レベル２以上ならダメージ受けたときにリキャスト回復
			if (level >= 2 && target instanceof Player player) {
				Predicate<ItemStack> flag = s -> !s.is(ItemInit.magic_aether_shield) && !s.is(ItemInit.magic_aether_shield2);
				List<ItemStack> magicList = IWand.getMagicList(IWand.getWandList(player), flag);

				for (ItemStack stack : magicList) {
					IMagicItem magic = new MagicInfo(stack).getMagicItem();
					int recast = magic.getRecastTime(stack);

					if (recast <= 0) { continue; }
					magic.setRecastTime(stack, Math.max(0, (int) ( recast - damage * 60 ) ));
				}
			}

			return 0;
		}

		// 攻撃予測回避なら攻撃を無効化
		if (target.hasEffect(PotionInit.future_vision)) {

			MobEffectInstance effect = target.getEffect(PotionInit.future_vision);
			int level = effect.getAmplifier() + 1;
			int time = effect.getDuration();
			target.removeEffect(PotionInit.future_vision);
			if (level < 2) { return 0; }

			level -= 1;
			if (level >= 2) {
				PlayerHelper.setPotion(target, PotionInit.future_vision, level, time);
			}

			return 0;
		}

		// 攻撃側が燃焼ならダメージ減少
		if (attacker != null && attacker.hasEffect(PotionInit.flame)) {
			damage *= 0.67F;
		}

		// ダメージカットならレベルの割合分ダメージ軽減
		if (target.hasEffect(PotionInit.damage_cut)) {
			int level = target.getEffect(PotionInit.damage_cut).getAmplifier();
			float cutRate = 1 - (0.1F + (level * 0.075F));
			damage = damage * cutRate;
		}

		// エーテルバリアーならレベルの割合分ダメージ軽減
		if (target.hasEffect(PotionInit.aether_barrier)) {

			int level = target.getEffect(PotionInit.aether_barrier).getAmplifier() + 1;
			float cutRate = 1.5F + (level * 0.1F);
			damage = damage / cutRate;

			if (attacker instanceof Warden) {
				damage *= 0.25F;
			}
		}

		// エーテルアーマーならレベル分のダメージ軽減
		if (target.hasEffect(PotionInit.aether_armor)) {

			MobEffectInstance effect = target.getEffect(PotionInit.aether_armor);
			int time = effect.getDuration();
			int level = effect.getAmplifier() + 1;

			if (attacker instanceof Warden) {
				damage = Math.max(0F, damage - 10F);
			}

			int maxValue = 2 + level * 2;
			time -= Math.min(maxValue, damage) * 20 / (hasQuillpen ? 4 : 1);
			damage = Math.max(0, damage - maxValue);
			target.addEffect(new MobEffectInstance(effect.getEffect(), time, level - 1, true, false));
		}

		// クイーンの保護
		if (target.hasEffect(PotionInit.queen_bless)) {
			damage *= 0.75F;
		}

		// ウィッチの保護
		if (target.hasEffect(PotionInit.witch_bless)) {

			MobEffectInstance effect = target.getEffect(PotionInit.witch_bless);
			int time = effect.getDuration();
			damage -= time;
			time -= damage * 40 / (hasQuillpen ? 4 : 1);
			target.addEffect(new MobEffectInstance(effect.getEffect(), time, 0, true, false));

			if (damage <= 0F) {
				return 0F;
			}
		}

		// 被弾側が毒ならダメージ増加
		if (target.hasEffect(PotionInit.deadly_poison) && damage > 0F) {
			int level = target.getEffect(PotionInit.deadly_poison).getAmplifier() + 1;
			float addDama = target.getType().is(TagInit.BOSS) && !(target instanceof Warden) ? level * 1F : level * 1.5F;
			damage += addDama;
		}

		// 魔法被ダメ増加
		if (target.hasEffect(PotionInit.magic_damage_receive) && src.getDirectEntity() instanceof AbstractMagicShot) {
			damage += target.getEffect(PotionInit.magic_damage_receive).getAmplifier() + 1;
		}

		return Math.max(0, damage);
	}

	// プレイヤーなら魔術書のダメージ無効化を発動
	public static float bookCutDamage(Player player, LivingEntity attacker, float damage) {
		List<ItemStack> stackBookList = IMagicBook.getBookList(player);
		if (stackBookList.isEmpty()) { return damage; }

		float addChance = 0F;

		// 攻撃回避率上昇なら確率追加
		if (player.hasEffect(PotionInit.attack_disable)) {
			addChance += 10F + player.getEffect(PotionInit.attack_disable).getAmplifier() * 5F;
		}

		BookInfo info = new BookInfo(stackBookList.get(0));
		IMagicBook book = info.getBook();
		return book.checkChance(book.getRecastPage(info) + addChance, player.getLevel()) ? 0F : damage;
	}

	public static float barrierCut(LivingEntity target, ItemStack leg, float damage) {
		if(!target.hasEffect(PotionInit.aether_barrier_origin)) { return damage; }

		MobEffectInstance effect = target.getEffect(PotionInit.aether_barrier_origin);
		int time = effect.getDuration();
		int level = effect.getAmplifier();
		float cutTime = damage * 20;

		if (!leg.isEmpty() && leg.getItem() instanceof IPorch porch && porch.hasAcce(leg, ItemInit.magician_quillpen)) {
			cutTime *= 0.25F;
		}

		target.removeEffect(PotionInit.aether_barrier_origin);
		target.addEffect(new MobEffectInstance(PotionInit.aether_barrier_origin, time - (int) cutTime, level, true, false));
		return damage / (level + 1);
	}

	// 攻撃対象を倒した場合
	public static void targetKill(LivingEntity target, LivingEntity attacker, AbstractMagicShot shot, PorchInfo info) {

		ItemStack stack = info.getStack();
		IPorch porch = info.getPorch();

		// 血吸の指輪の分最大体力の割合回復
		int bloodCount = porch.acceCount(stack, ItemInit.blood_sucking_ring, 5);

		if (bloodCount > 0) {
			attacker.heal(attacker.getMaxHealth() * bloodCount * 0.1F);
		}

		// 戦士の腕輪の数だけバフ時間延長
		int warriorCount = porch.acceCount(stack, ItemInit.warrior_bracelet, 5);

		if (warriorCount > 0) {
			int time = 2400 * warriorCount;
			attacker.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, time, 2, true, false));
			attacker.addEffect(new MobEffectInstance(PotionInit.drop_increase, time, 0, true, false));
		}

		// 炎魔法で倒したらリキャスト回復
		if (porch.hasAcce(stack, ItemInit.ignis_soul) && shot instanceof FireMagicShot) {

			List<ItemStack> magicList = IWand.getMagicList(IWand.getWandList((Player) attacker), e -> true);

			for (ItemStack s : magicList) {
				IMagicItem magic = new MagicInfo(s).getMagicItem();
				int recast = magic.getRecastTime(s);
				if (recast <= 0) { continue; }

				magic.setRecastTime(s, Math.max(0, (int) ( recast - magic.getMaxRecastTime() * 0.1F ) ));
			}
		}
	}

	// ポーチによるダメージ上昇
	public static void porchAftereffect(LivingEntity entity, float damage, DamageSource src, PorchInfo info) {
		if (info.getPorch().hasAcce(info.getStack(), ItemInit.angel_flugel)) {
			entity.heal(damage * 0.025F);
		}
	}

	public static void attackDisabled(LivingEntity target, PorchInfo info) {
		if (!info.getPorch().hasAcce(info.getStack(), ItemInit.angel_flugel)) { return; }

		PlayerHelper.setPotion(target, PotionInit.regeneration, 0, 200);
		PlayerHelper.setPotion(target, MobEffects.DAMAGE_BOOST, 3, 400);
		PlayerHelper.setPotion(target, PotionInit.mfcostdown, 2, 400);
	}
}
