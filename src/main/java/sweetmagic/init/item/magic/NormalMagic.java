package sweetmagic.init.item.magic;

import java.util.List;

import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import sweetmagic.api.emagic.SMElement;
import sweetmagic.api.emagic.SMMagicType;
import sweetmagic.api.iitem.info.MagicInfo;
import sweetmagic.api.iitem.info.WandInfo;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.PotionInit;
import sweetmagic.init.SoundInit;

public class NormalMagic extends BaseMagicItem {

	public final int data;

	public NormalMagic(String name, SMElement ele, int tier, int coolTime, int useMF, int data) {
		super(name, SMMagicType.NORMAL, ele, tier, coolTime, useMF, false);
		this.data = data;
	}

	public NormalMagic(String name, SMElement ele, int tier, int coolTime, int useMF, int data, String iconName) {
		super(name, SMMagicType.NORMAL, ele, tier, coolTime, useMF, false, iconName);
		this.data = data;
	}

	/**
	 *  0 = 即時回復魔法
	 *  1 = 即時大回復魔法
	 *  2 = エーテルアーマー
	 *  3 = エーテルバリア
	 *  4 = エフェクトリムーバー
	 *  5 = リフレッシュエフェクト
	 */

	// ツールチップ
	public List<MutableComponent> magicToolTip (List<MutableComponent> toolTip) {

		switch(this.data) {
		case 0:
		case 1:
			toolTip.add(this.getText("magic_quickheal.1", String.format("%.0f%%", 25F * (this.data + 1))));
			toolTip.add(this.getText("magic_quickheal.2"));
			break;
		case 2:
			toolTip.add(this.getText(this.name));
			toolTip.add(this.getText(this.name + "_caution"));
			break;
		case 10:
			toolTip.add(this.getText("magic_quickheal.1", String.format("%.0f%%", 100F)));
			break;
		case 11:
			toolTip.add(this.getText("magic_reflasheffect"));
			toolTip.add(this.getText(this.name));
			break;
		case 12:
			toolTip.add(this.getText(this.name));
			toolTip.add(this.getText(this.name + "_regene"));
			break;
		case 14:
			toolTip.add(this.getText(this.name));
			toolTip.add(this.getText("magic_aether_shield_wand"));
			break;
		case 15:
			toolTip.add(this.getText(this.name));
			toolTip.add(this.getText(this.name + "_recast"));
			toolTip.add(this.getText("magic_aether_shield_wand"));
			break;
		default :
			toolTip.add(this.getText(this.name));
			break;
		}

		return toolTip;
	}

	@Override
	public boolean onItemAction(Level world, Player player, WandInfo wandInfo, MagicInfo magicInfo) {

		// 杖の取得
		ItemStack stack = wandInfo.getStack();
		this.acceEffect(player, this.getPower(wandInfo));

		switch(this.data) {
		case 0: return this.roundHealAction(world, player, stack, wandInfo);
		case 1: return this.roundHealAction(world, player, stack, wandInfo);
		case 2: return this.armorAction(world, player, stack, wandInfo);
		case 3: return this.barrierAction(world, player, stack, wandInfo);
		case 4: return this.reflashEffectAction(world, player, stack, wandInfo);
		case 5: return this.reflashEffectAction(world, player, stack, wandInfo);
		case 6: return this.potionAction(world, player, stack, wandInfo);
		case 7: return this.potionAction(world, player, stack, wandInfo);
		case 8: return this.potionAction(world, player, stack, wandInfo);
		case 9: return this.potionAction(world, player, stack, wandInfo);
		case 10: return this.roundHealAction(world, player, stack, wandInfo);
		case 11: return this.reflashEffectAction(world, player, stack, wandInfo);
		case 12: return this.barrierAction(world, player, stack, wandInfo);
		case 13: return this.invisibleAction(world, player, stack, wandInfo);
		case 14: return this.aetherShiledAction(world, player, stack, wandInfo);
		case 15: return this.aetherShiledAction(world, player, stack, wandInfo);
		}

		return true;
	}

	// 防御魔法
	public boolean armorAction (Level world, Player player, ItemStack stack, WandInfo wandInfo) {

		int level = (wandInfo.getLevel() - 1) / 10;
		int time = (int) this.getHealValue(player, 2400 + wandInfo.getLevel() * 120);

		this.addPotion(player, PotionInit.aether_armor, level, time);
		this.playSound(world, player, SoundEvents.ENCHANTMENT_TABLE_USE, 0.5F, 1.175F);

		return true;
	}

	// バリア魔法
	public boolean barrierAction (Level world, Player player, ItemStack stack, WandInfo wandInfo) {

		int level = wandInfo.getLevel();
		int time = (int) this.getHealValue(player, this.effectTime(wandInfo));

		if (this.data == 12) {
			this.addPotion(player, PotionInit.regeneration, 0, time);
			time *= 1.25F;
		}

		this.addPotion(player, PotionInit.aether_barrier, level, time);
		this.playSound(world, player, SoundEvents.ENCHANTMENT_TABLE_USE, 0.5F, 1.175F);

		return true;
	}

	// リフレッシュエフェクト
	public boolean reflashEffectAction (Level world, Player player, ItemStack stack, WandInfo wandInfo) {

		int time = this.data == 4 ? 2 : (int) this.getHealValue(player, this.effectTime(wandInfo));
		double range = 7.5D * ( 1D + this.getExtensionRingCount(player) * 0.25D);
		boolean isTier3 = this.data == 11;

		if (isTier3) {
			range *= 1.25D;
		}

		double x = player.getX();
		double y = player.getY();
		double z = player.getZ();

		if (world instanceof ServerLevel server) {
			for (int i = 0; i < 20; i++) {
				this.spawnParticleCycle(server, ParticleInit.CYCLE_REFLASH.get(), x, y + 0.25D, z, Direction.UP, range, (i * 18), false);
			}
		}


		List<LivingEntity> entityList = this.getEntityList(LivingEntity.class, player, e -> e.isAlive() && !(e instanceof Enemy), range);

		for (LivingEntity entity : entityList) {

			this.addPotion(entity, PotionInit.reflash_effect, 0, time);

			if (isTier3) {
				this.addPotion(entity, PotionInit.resurrection, 0, time);
			}

			double pX = entity.getX();
			double pY = entity.getY();
			double pZ = entity.getZ();

			for (int i = 0; i < 3; i++) {
				this.spawnParticleRing(world, ParticleInit.REFLASH.get(), 1D, pX, pY, pZ, 0.5D + i * 0.5D, 0.025D, 1D);
			}
		}

		this.playSound(world, player, SoundEvents.ENCHANTMENT_TABLE_USE, 0.5F, 1.175F);
		return true;
	}

	// バニラバフ
	public boolean potionAction (Level world, Player player, ItemStack stack, WandInfo wandInfo) {

		int time = (int) this.getHealValue(player, this.effectTime(wandInfo));
		MobEffect effect = null;

		switch (this.data) {
		case 6:
			effect = MobEffects.WATER_BREATHING;
			break;
		case 7:
			effect = MobEffects.NIGHT_VISION;
			break;
		case 8:
			effect = MobEffects.MOVEMENT_SPEED;
			break;
		case 9:
			effect = MobEffects.DAMAGE_BOOST;
			break;
		}

		this.addPotion(player, effect, 1, time);
		this.playSound(world, player, SoundEvents.BREWING_STAND_BREW, 0.5F, 1.175F);
		return true;
	}

	// 回復魔法
	public boolean roundHealAction (Level world, Player player, ItemStack stack, WandInfo wandInfo) {

		double x = player.getX();
		double y = player.getY();
		double z = player.getZ();
		double range = 2.5D * this.getTier() * ( 1D + this.getExtensionRingCount(player) * 0.25D);
		List<LivingEntity> entityList = this.getEntityList(LivingEntity.class, player, e -> e.isAlive() && !(e instanceof Enemy), range);

		if (world instanceof ServerLevel server) {
			for (int i = 0; i < 20; i++) {
				this.spawnParticleCycle(server, ParticleInit.CYCLE_HEAL.get(), x, y + 0.25D, z, Direction.UP, range, (i * 18), false);
			}
		}

		for (LivingEntity entity : entityList) {

			entity.heal(entity.getMaxHealth());

			// デバフ解除
			entity.removeEffect(MobEffects.HUNGER);
			entity.removeEffect(MobEffects.WEAKNESS);

			if (entity instanceof Player) {
				this.playSound(world, entity, SoundInit.HEAL, 0.0625F, 1F);
			}

			double pX = entity.getX();
			double pY = entity.getY();
			double pZ = entity.getZ();

			for (int i = 0; i < 3; i++) {
				this.spawnParticleRing(world, ParticleTypes.HAPPY_VILLAGER, 1D, pX, pY, pZ, 0.75D + i * 0.5D, 1D, 1D);
			}
		}

		return true;
	}

	// 透明化
	public boolean invisibleAction (Level world, Player player, ItemStack stack, WandInfo wandInfo) {
		int time = (int) this.getHealValue(player, this.effectTime(wandInfo));
		this.addPotion(player, MobEffects.INVISIBILITY, 0, time);
		this.playSound(world, player, SoundEvents.ENCHANTMENT_TABLE_USE, 0.5F, 1.175F);

		List<Mob> entityList = this.getEntityList(Mob.class, player, e -> e.isAlive() && e.getTarget() != null && e.getTarget().getUUID() == player.getUUID(), 64D);
		entityList.forEach(e -> e.setTarget(null));
		return true;
	}

	// エーテルシールド
	public boolean aetherShiledAction (Level world, Player player, ItemStack stack, WandInfo wandInfo) {
		int time = (int) this.getHealValue(player, this.data == 14 ? 300 : 500);
		this.addPotion(player, PotionInit.aether_shield, this.data - 14, time);
		this.playSound(world, player, SoundEvents.ENCHANTMENT_TABLE_USE, 0.5F, 1.175F);

		if (!wandInfo.getWand().isCreativeWand()) {
			player.getCooldowns().addCooldown(stack.getItem(), 200);
		}

		List<Mob> entityList = this.getEntityList(Mob.class, player, e -> e.isAlive() && e instanceof Enemy, 64D);
		entityList.forEach(e -> e.setTarget(player));
		return true;
	}
}
