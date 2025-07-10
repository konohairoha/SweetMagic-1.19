package sweetmagic.init.item.magic;

import java.util.List;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.level.Level;
import sweetmagic.api.emagic.SMElement;
import sweetmagic.api.emagic.SMMagicType;
import sweetmagic.api.iitem.info.MagicInfo;
import sweetmagic.api.iitem.info.WandInfo;
import sweetmagic.init.PotionInit;
import sweetmagic.init.entity.projectile.AbstractMagicShot;
import sweetmagic.init.entity.projectile.BloodMagicShot;
import sweetmagic.init.entity.projectile.BubbleMagicShot;
import sweetmagic.init.entity.projectile.BulletMagicShot;
import sweetmagic.init.entity.projectile.CherryMagicShot;
import sweetmagic.init.entity.projectile.CycloneMagicShot;
import sweetmagic.init.entity.projectile.DigMagicShot;
import sweetmagic.init.entity.projectile.ExplosionMagicShot;
import sweetmagic.init.entity.projectile.FireMagicShot;
import sweetmagic.init.entity.projectile.FrostMagicShot;
import sweetmagic.init.entity.projectile.GravityMagicShot;
import sweetmagic.init.entity.projectile.LightMagicShot;
import sweetmagic.init.entity.projectile.NormalMagicShot;
import sweetmagic.init.entity.projectile.PoisonMagicShot;
import sweetmagic.init.entity.projectile.RockBlastMagicShot;

public class ShotMagic extends BaseMagicItem {

	public final int data;

	public ShotMagic(String name, SMElement ele, int tier, int coolTime, int useMF, int data) {
		super(name, SMMagicType.SHOT, ele, tier, coolTime, useMF, false);
		this.data = data;
	}

	public ShotMagic(String name, SMElement ele, int tier, int coolTime, int useMF, int data, String iconName) {
		super(name, SMMagicType.SHOT, ele, tier, coolTime, useMF, false, iconName);
		this.data = data;
	}

	/**
	 * 0 = 光魔法
	 * 1 = 炎魔法
	 * 2 = 炎魔法tire2
	 * 3 = 氷魔法
	 * 4 = 氷魔法tire2
	 * 5 = ディグ魔法
	 * 6 = 通常魔法
	 */

	// ツールチップ
	public List<MutableComponent> magicToolTip(List<MutableComponent> toolTip) {

		switch(this.data) {
		case 4:
			toolTip.add(this.getText(this.name));
			toolTip.add(this.getText(this.name + "_damage"));
			break;
		case 19:
			toolTip.add(this.getText("magic_blood_read"));
			toolTip.add(this.getText("magic_health", this.formatPar(50F, WHITE)));
			toolTip.add(this.getText("blood_heart"));
			break;
		case 20:
			toolTip.add(this.getText("magic_blood_read"));
			toolTip.add(this.getText("magic_health", this.formatPar(75F, WHITE)));
			toolTip.add(this.getText("magic_food0"));
			toolTip.add(this.getText("magic_food1", this.formatPar(50F, WHITE)));
			toolTip.add(this.getText("blood_heart"));
			break;
		case 18:
			toolTip.add(this.getText("magic_bubleprison"));
			toolTip.add(this.getText(this.name));
			break;
		case 21:
			toolTip.add(this.getText(this.name));
			break;
		case 23:
			toolTip.add(this.getText("magic_illuminate", 1));
			toolTip.add(this.getText(this.name));
			break;
		case 24:
			toolTip.add(this.getText("magic_illuminate", 3));
			toolTip.add(this.getText("magic_holy_light"));
			break;
		case 25:
			toolTip.add(this.getText("magic_storm"));
			toolTip.add(this.getText(this.name));
			toolTip.add(this.getText("magic_tempest_storm", this.getEffectTip("bleeding").getString(), 1));
			break;
		case 28:
			toolTip.add(this.getText("magic_bubleprison"));
			toolTip.add(this.getText("magic_scumefang"));
			break;
		case 29:
		case 30:
		case 31:
			toolTip.add(this.getText("magic_rockblast", (this.data - 28) + "-" + (this.data - 26)));
			break;
		case 32:
			toolTip.add(this.getText("magic_blood_read"));
			toolTip.add(this.getText("magic_health", this.formatPar(100F, WHITE)));
			toolTip.add(this.getText("magic_food0"));
			toolTip.add(this.getText("magic_food1", this.formatPar(75F, WHITE)));
			toolTip.add(this.getText("magic_add"));
			break;
		case 34:
		case 35:
			toolTip.add(this.getText(this.name));
			toolTip.add(this.getText(this.name + "_vulnerable"));
			break;
		case 36:
		case 37:
		case 38:
			toolTip.add(this.getText("magic_cherry"));
			toolTip.add(this.getText("magic_cherry_vulnerable", this.getEnchaTip(Math.max(1, this.data - 36)).getString()));
			break;
		case 39:
			toolTip.add(this.getText("magic_illuminate", 5));
			toolTip.add(this.getText(this.name, this.getEffectTip("flame").getString(), this.getMCTip("glowing").getString()));
			break;
		case 40:
			toolTip.add(this.getText("magic_meteor"));
			toolTip.add(this.getText("magic_meteor_vulnerable"));
			break;
		case 41:
			toolTip.add(this.getText("magic_frostrain"));
			toolTip.add(this.getText("magic_frostrain_vulnerable"));
			break;
		case 42:
			String bleed = this.getEffectTip("bleeding").getString();
			toolTip.add(this.getText("magic_storm"));
			toolTip.add(this.getText("magic_gale", this.getEnchaTip(2).getString()));
			toolTip.add(this.getText("magic_tempest_storm", bleed, 2));
			toolTip.add(this.getText("magic_tempest_storm_damage", bleed));
			break;
		case 43:
			toolTip.add(this.getText("magic_magia_destroy"));
			toolTip.add(this.getText(this.name));
			break;
		case 45:
			toolTip.add(this.getText(this.name, this.getEffectTip("deadly_poison").getString(), this.getEffectTip("magic_damage_receive").getString()));
			toolTip.add(this.getText(this.name + "_dame", this.getEffectTip("reflash_effect").getString()));
			break;
		case 46:
			toolTip.add(this.getText("magic_range_dig"));
			break;
		case 47:
			toolTip.add(this.getText("magic_rockblast", 5 + "-" + 8));
			break;
		case 48:
			toolTip.add(this.getText("magic_cherry"));
			toolTip.add(this.getText("magic_cherry_vulnerable", this.getEnchaTip(3).getString()));
			break;
		case 49:
			toolTip.add(this.getText("magic_advance"));
			toolTip.add(this.getText(this.name));
			break;
		case 50:
			toolTip.add(this.getText("magic_bubleprison"));
			toolTip.add(this.getText("magic_scumefang"));
			toolTip.add(this.getText(this.name));
			break;
		case 51:
			toolTip.add(this.getText("magic_blood_read"));
			toolTip.add(this.getText("magic_health", this.formatPar(150F, WHITE)));
			toolTip.add(this.getText("magic_food0"));
			toolTip.add(this.getText("magic_food1", this.formatPar(100F, WHITE)));
			toolTip.add(this.getText("magic_add"));
			toolTip.add(this.getText("magic_buff"));
			break;
		default:
			toolTip.add(this.getText(this.name));
			break;
		}

		return toolTip;
	}

	@Override
	public boolean onItemAction(Level world, Player player, WandInfo wandInfo, MagicInfo magicInfo) {

		AbstractMagicShot entity = null;
		RandomSource rand = world.getRandom();
		float level = wandInfo.getLevel();

		// ( レベル × 0.2 ) + 最小((レベル - 1) × 0.175, 5) + 最小( 最大(5 × (1 - (レベル - 1) × 0.02), 0), 4)
		float power = this.getPower(wandInfo);
		float shotSpeed = 2F + level * 0.05F;
		FoodData foodData = player.getFoodData();
		boolean hasBlood = this.hasBloodSuckingRing(player);

		switch(this.data) {
		case 0:
			entity = new LightMagicShot(world, player, wandInfo);
			entity.setAddDamage(entity.getAddDamage() + power * 0.3F);
			break;
		case 1:
			entity = new FireMagicShot(world, player, wandInfo);
			entity.setAddDamage(entity.getAddDamage() + power);
			break;
		case 2:
			entity = new FireMagicShot(world, player, wandInfo);
			entity.setAddDamage(entity.getAddDamage() + power * 1.35F);
			entity.setRange(2D + (level * 0.1D));
			entity.setData(1);
			break;
		case 3:
			entity = new FrostMagicShot(world, player, wandInfo);
			entity.setAddDamage(entity.getAddDamage() + power);
			break;
		case 4:
			entity = new FrostMagicShot(world, player, wandInfo);
			entity.setAddDamage(entity.getAddDamage() + power * 1.75F);
			entity.setHitDead(false);
			entity.setData(1);
			break;
		case 5:
			entity = new CycloneMagicShot(world, player, wandInfo);
			entity.setAddDamage(entity.getAddDamage() + power);
			break;
		case 6:
			entity = new CycloneMagicShot(world, player, wandInfo);
			entity.setAddDamage(entity.getAddDamage() + power * 1.25F);
			entity.setRange(9D);
			entity.setData(1);
			break;
		case 7:
			entity = new ExplosionMagicShot(world, player, wandInfo);
			entity.setAddDamage(entity.getAddDamage() + power);
			entity.setRange(3D);
			break;
		case 8:
			entity = new ExplosionMagicShot(world, player, wandInfo);
			entity.setAddDamage(entity.getAddDamage() + power * 1.25F);
			entity.setRange(6D);
			entity.setData(1);
			break;
		case 9:
			entity = new GravityMagicShot(world, player, wandInfo);
			entity.setAddDamage(entity.getAddDamage() + power);
			break;
		case 10:
			entity = new GravityMagicShot(world, player, wandInfo);
			entity.setAddDamage(entity.getAddDamage() + power * 1.5F);
			entity.setRange(5D);
			entity.setData(1);
			break;
		case 11:
			entity = new PoisonMagicShot(world, player, wandInfo);
			entity.setAddDamage(entity.getAddDamage() + power);
			break;
		case 12:
			entity = new PoisonMagicShot(world, player, wandInfo);
			entity.setAddDamage(entity.getAddDamage() + power * 1.5F);
			entity.setRange(7.5D);
			entity.setData(1);
			break;
		case 13:
			entity = new DigMagicShot(world, player, wandInfo);
			entity.setAddDamage(entity.getAddDamage() + power * 0.33F);
			break;
		case 14:
			entity = new DigMagicShot(world, player, wandInfo);
			entity.setAddDamage(entity.getAddDamage() + power * 0.67F);
			entity.setData(1);
			break;
		case 15:
			entity = new NormalMagicShot(world, player, wandInfo);
			entity.setAddDamage(entity.getAddDamage() + power * 0.65F);
			break;
		case 16:
			entity = new BulletMagicShot(world, player, wandInfo);
			entity.setAddDamage(entity.getAddDamage() + power * 0.95F);
			entity.setRange(6D);
			entity.setMaxLifeTime(70);
			break;
		case 17:
			entity = new BubbleMagicShot(world, player, wandInfo);
			entity.setAddDamage(entity.getAddDamage() + power);
			break;
		case 18:
			entity = new BubbleMagicShot(world, player, wandInfo);
			entity.setAddDamage(entity.getAddDamage() + power * 1.75F);
			entity.setData(1);
			break;
		case 19:
			entity = new BloodMagicShot(world, player, wandInfo);
			float healthRate = player.getHealth() >= player.getMaxHealth() * 0.5F ? 1.5F : 1F;
			entity.setAddDamage(entity.getAddDamage() + power * healthRate);

			if (!player.isCreative() && player.getHealth() > 1F && !hasBlood) {
				player.setHealth(player.getHealth() - 1F);
			}
			break;
		case 20:
			entity = new BloodMagicShot(world, player, wandInfo);
			float healthRate2 = player.getHealth() >= player.getMaxHealth() * 0.5F ? 1.75F : 1F;
			float foodRate2 = foodData.getFoodLevel() >= 10 ? 1.5F : 1F;

			entity.setAddDamage(entity.getAddDamage() + power * healthRate2);
			entity.setData(1);
			entity.setRange(5D * foodRate2);

			if (!player.isCreative() && player.getHealth() > 1F && !hasBlood) {
				player.setHealth(player.getHealth() - 1F);
				foodData.setFoodLevel(foodData.getFoodLevel() - 1);
			}
			break;
		case 21:
			entity = new BulletMagicShot(world, player, wandInfo);
			entity.setAddDamage(entity.getAddDamage() + power * 1.25F);
			entity.setRange(12D);
			entity.setMaxLifeTime(80);
			entity.setBlockPenetration(true);
			break;
		case 22:
			entity = new ExplosionMagicShot(world, player, wandInfo);
			entity.setAddDamage(entity.getAddDamage() + power * 1.5F);
			entity.setRange(9D);
			entity.setData(2);
			break;
		case 23:
			entity = new LightMagicShot(world, player, wandInfo);
			entity.setAddDamage(entity.getAddDamage() + power * 1.25F);
			entity.setRange(4D);
			entity.setData(1);
			break;
		case 24:
			entity = new LightMagicShot(world, player, wandInfo);
			entity.setAddDamage(entity.getAddDamage() + power * 1.5F);
			entity.setRange(9D);
			entity.setData(2);
			break;
		case 25:
			entity = new CycloneMagicShot(world, player, wandInfo);
			entity.setAddDamage(entity.getAddDamage() + power * 1.375F);
			entity.setRange(14D);
			entity.setData(2);
			break;
		case 26:
			entity = new GravityMagicShot(world, player, wandInfo);
			entity.setAddDamage(entity.getAddDamage() + power * 1.75F);
			entity.setRange(10D);
			entity.setData(2);
			entity.setHitDead(false);
			break;
		case 27:
			entity = new PoisonMagicShot(world, player, wandInfo);
			entity.setAddDamage(entity.getAddDamage() + power * 1.75F);
			entity.setRange(12D);
			entity.setData(2);
			break;
		case 28:
			entity = new BubbleMagicShot(world, player, wandInfo);
			entity.setAddDamage(entity.getAddDamage() + power * 2.25F);
			entity.setData(2);
			entity.setAddAttack(entity.getAddAttack() + 2);
			break;
		case 29:
			entity = new RockBlastMagicShot(world, player, wandInfo);
			entity.setAddDamage(entity.getAddDamage() + power * 1.25F);
			entity.setData(0);
			entity.setAddAttack(entity.getAddAttack() + rand.nextInt(3) + 1);
			break;
		case 30:
			entity = new RockBlastMagicShot(world, player, wandInfo);
			entity.setAddDamage(entity.getAddDamage() + power * 1.65F);
			entity.setData(1);
			entity.setAddAttack(entity.getAddAttack() + rand.nextInt(3) + 2);
			break;
		case 31:
			entity = new RockBlastMagicShot(world, player, wandInfo);
			entity.setAddDamage(entity.getAddDamage() + power * 2F);
			entity.setData(2);
			entity.setAddAttack(entity.getAddAttack() + rand.nextInt(3) + 3);
			break;
		case 32:
			entity = new BloodMagicShot(world, player, wandInfo);
			float healthRate3 = player.getHealth() >= player.getMaxHealth() * 0.5F ? 2F : 1F;
			float foodRate3 = foodData.getFoodLevel() >= 10 ? 1.75F : 1F;
			entity.setAddDamage(entity.getAddDamage() + power * healthRate3);
			entity.setData(2);
			entity.setRange(8F * foodRate3);

			if (!player.isCreative() && player.getHealth() > 1F && !hasBlood) {
				player.setHealth(player.getHealth() - 1F);
				foodData.setFoodLevel(foodData.getFoodLevel() - 1);
			}

			break;
		case 33:
			entity = new DigMagicShot(world, player, wandInfo);
			entity.setAddDamage(entity.getAddDamage() + power);
			entity.setData(2);
			entity.setBlockPenetration(true);
			entity.setMaxBreak(2);
			break;
		case 34:
			entity = new FireMagicShot(world, player, wandInfo);
			entity.setAddDamage(entity.getAddDamage() + power * 1.75F);
			entity.setRange(7.5D);
			entity.setData(2);
			break;
		case 35:
			entity = new FrostMagicShot(world, player, wandInfo);
			entity.setAddDamage(entity.getAddDamage() + power * 2F);
			entity.setRange(8.25D);
			entity.setData(2);
			break;
		case 36:
			entity = new CherryMagicShot(world, player, wandInfo);
			entity.setAddDamage(entity.getAddDamage() + power * 1F);
			entity.setRange(1D);
			entity.setData(0);
			break;
		case 37:
			entity = new CherryMagicShot(world, player, wandInfo);
			entity.setAddDamage(entity.getAddDamage() + power * 1.33F);
			entity.setRange(4D);
			entity.setData(1);
			break;
		case 38:
			entity = new CherryMagicShot(world, player, wandInfo);
			entity.setAddDamage(entity.getAddDamage() + power * 1.67F);
			entity.setRange(10D);
			entity.setData(2);
			break;
		case 39:
			entity = new LightMagicShot(world, player, wandInfo);
			entity.setAddDamage(entity.getAddDamage() + power * 2.25F);
			entity.setRange(12D);
			entity.setData(3);
			break;
		case 40:
			entity = new FireMagicShot(world, player, wandInfo);
			entity.setAddDamage(entity.getAddDamage() + power * 2F);
			entity.setRange(12D);
			entity.setData(3);
			break;
		case 41:
			entity = new FrostMagicShot(world, player, wandInfo);
			entity.setAddDamage(entity.getAddDamage() + power * 2F);
			entity.setRange(12D);
			entity.setData(3);
			break;
		case 42:
			entity = new CycloneMagicShot(world, player, wandInfo);
			entity.setAddDamage(entity.getAddDamage() + power * 1.75F);
			entity.setRange(18.5D);
			entity.setData(3);
			break;
		case 43:
			entity = new ExplosionMagicShot(world, player, wandInfo);
			entity.setAddDamage(entity.getAddDamage() + power * 2F);
			entity.setRange(14D);
			entity.setData(3);
			entity.setAddAttack(3);
			break;
		case 44:
			entity = new GravityMagicShot(world, player, wandInfo);
			entity.setAddDamage(entity.getAddDamage() + power * 2.25F);
			entity.setRange(15D);
			entity.setData(3);
			entity.setHitDead(false);
			break;
		case 45:
			entity = new PoisonMagicShot(world, player, wandInfo);
			entity.setAddDamage(entity.getAddDamage() + power * 2F);
			entity.setRange(15D);
			entity.setData(3);
			break;
		case 46:
			entity = new DigMagicShot(world, player, wandInfo);
			entity.setAddDamage(entity.getAddDamage() + power * 1.5F);
			entity.setData(3);
			entity.setBlockPenetration(true);
			entity.setMaxBreak(7);
			break;
		case 47:
			entity = new RockBlastMagicShot(world, player, wandInfo);
			entity.setAddDamage(entity.getAddDamage() + power * 2F);
			entity.setData(3);
			entity.setAddAttack(entity.getAddAttack() + rand.nextInt(4) + 5);
			break;
		case 48:
			entity = new CherryMagicShot(world, player, wandInfo);
			entity.setAddDamage(entity.getAddDamage() + power * 2F);
			entity.setRange(15D);
			entity.setData(3);
			break;
		case 49:
			entity = new BulletMagicShot(world, player, wandInfo);
			entity.setAddDamage(entity.getAddDamage() + power * 1.67F);
			entity.setRange(20D);
			entity.setMaxLifeTime(100);
			entity.setData(3);
			entity.setBlockPenetration(true);
			entity.setHitDead(false);
			break;
		case 50:
			entity = new BubbleMagicShot(world, player, wandInfo);
			entity.setAddDamage(entity.getAddDamage() + power * 2.67F);
			entity.setData(3);
			entity.setAddAttack(entity.getAddAttack() + 3);
			break;
		case 51:
			entity = new BloodMagicShot(world, player, wandInfo);
			float healthRate4 = player.getHealth() >= player.getMaxHealth() * 0.5F ? 2.5F : 1F;
			float foodRate4 = foodData.getFoodLevel() >= 10 ? 2F : 1F;
			entity.setAddDamage(entity.getAddDamage() + power * healthRate4);
			entity.setData(3);
			entity.setRange(12F * foodRate4);

			if (!player.isCreative() && player.getHealth() > 1F && !hasBlood) {
				player.setHealth(player.getHealth() - 1F);
				foodData.setFoodLevel(foodData.getFoodLevel() - 1);
			}

			this.addPotion(player, PotionInit.blood_curse, 0, 600);
			break;
		}

		entity.acceEffect();
		entity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, shotSpeed, 0);
		world.addFreshEntity(entity);
		wandInfo.getWand().shotSound(player);
		this.acceEffect(player, this.getPower(wandInfo));

		return true;
	}
}
