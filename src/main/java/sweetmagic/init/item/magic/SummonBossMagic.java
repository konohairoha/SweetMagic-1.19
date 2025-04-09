package sweetmagic.init.item.magic;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import sweetmagic.api.emagic.SMElement;
import sweetmagic.api.emagic.SMMagicType;
import sweetmagic.api.iitem.IPorch;
import sweetmagic.api.iitem.info.MagicInfo;
import sweetmagic.api.iitem.info.WandInfo;
import sweetmagic.init.ItemInit;
import sweetmagic.init.PotionInit;
import sweetmagic.init.SoundInit;
import sweetmagic.init.entity.projectile.AbstractBossMagic;
import sweetmagic.init.entity.projectile.CherryRainMagic;
import sweetmagic.init.entity.projectile.FrostLaserMagic;
import sweetmagic.init.entity.projectile.HolyBusterMagic;
import sweetmagic.init.entity.projectile.IgnisBlastMagic;
import sweetmagic.init.entity.projectile.InfinitWandMagic;
import sweetmagic.init.entity.projectile.WindStormMagic;
import sweetmagic.util.PlayerHelper;

public class SummonBossMagic extends BaseMagicItem {

	public final int data;

	public SummonBossMagic(String name, SMElement ele, int tier, int coolTime, int useMF, int data) {
		super(name, SMMagicType.SUMMON, ele, tier, coolTime, useMF, false);
		this.data = data;
	}

	public SummonBossMagic(String name, SMElement ele, int tier, int coolTime, int useMF, int data, String iconName) {
		super(name, SMMagicType.SUMMON, ele, tier, coolTime, useMF, false, iconName);
		this.data = data;
	}

	// ツールチップ
	public List<MutableComponent> magicToolTip(List<MutableComponent> toolTip) {

		if (this.data <= 4) {
			toolTip.add(this.getText(this.name));
		}

		else {

			switch (this.data) {
			case 5:
				toolTip.add(this.getText(this.name));
				toolTip.add(this.getText(this.name + "_buff"));
				break;
			case 6:
				toolTip.add(this.getText("magic_frostlaser"));
				toolTip.add(this.getText(this.name));
				toolTip.add(this.getText(this.name + "_buff"));
				break;
			case 7:
				toolTip.add(this.getText("magic_holybuster"));
				toolTip.add(this.getText(this.name));
				toolTip.add(this.getText(this.name + "_buff"));
				break;
			case 8:
				toolTip.add(this.getText("magic_ignisblast"));
				toolTip.add(this.getText(this.name));
				toolTip.add(this.getText(this.name + "_buff"));
				break;
			case 9:
				toolTip.add(this.getText("magic_windstorm"));
				toolTip.add(this.getText(this.name));
				toolTip.add(this.getText(this.name + "_buff"));
				break;
			case 10:
				toolTip.add(this.getText("magic_cherryrain"));
				toolTip.add(this.getText(this.name));
				toolTip.add(this.getText(this.name + "_buff"));
				break;
			}
		}

		return toolTip;
	}

	@Override
	public boolean onItemAction(Level world, Player player, WandInfo wandInfo, MagicInfo magicInfo) {
		this.bossMagicAction(world, player, wandInfo);
		this.playSound(world, player.blockPosition(), SoundInit.HORAMAGIC, 0.1F, 1F);
		return true;
	}

	public boolean bossMagicAction(Level world, Player player, WandInfo wandInfo) {

		// 攻撃者の座標取得
		Vec3 src = new Vec3(player.getX(), player.getY(), player.getZ()).add(0, player.getEyeHeight(), 0);
		Vec3 look = player.getViewVector(1F);

		// 向き先に座標を設定
		Vec3 dest = src.add(look.x * 3, player.getY(), look.z * 3);
		BlockPos pos = new BlockPos(dest.x, player.getY() + 1, dest.z);
		AbstractBossMagic entity = this.getShot(world, player, wandInfo);
		entity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 1F, 0);
		entity.shoot(0D, 0D, 0D, 1.35F, 0F);
		entity.setPos(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
		entity.setHitDead(false);
		entity.setNotDamage(true);
		entity.setData(this.data);
		entity.setRotData(-player.getYRot());
		entity.acceEffect();

		// 召喚時間
		int summonTime = this.getSummonTime();
		ItemStack leg = player.getItemBySlot(EquipmentSlot.LEGS);

		if (!leg.isEmpty() && leg.getItem() instanceof IPorch porch) {

			float addRate = 0F;

			int summonCount = porch.acceCount(leg, ItemInit.summon_book, 5);
			if (summonCount > 0) {
				addRate = summonCount * 0.2F;
			}

			if (porch.hasAcce(leg, ItemInit.twilight_hourglass)) {
				summonTime *= 1.25F;
				addRate += 0.25F;
			}

			summonTime *= (1F + addRate);
		}

		entity.setMaxLifeTime(summonTime);

		if (this.data >= 5) {
			entity.setData(1);
			this.addBuff(player, summonTime);
		}

		if (!world.isClientSide) {
			world.addFreshEntity(entity);
		}

		return true;
	}

	public AbstractBossMagic getShot(Level world, Player player, WandInfo wandInfo) {
		switch (this.data) {
		case 1:  return new HolyBusterMagic(world, player, wandInfo);
		case 2:  return new IgnisBlastMagic(world, player, wandInfo);
		case 3:  return new WindStormMagic(world, player, wandInfo);
		case 4:  return new CherryRainMagic(world, player, wandInfo);
		case 5:  return new InfinitWandMagic(world, player, wandInfo);
		case 6:  return new FrostLaserMagic(world, player, wandInfo);
		case 7:  return new HolyBusterMagic(world, player, wandInfo);
		case 8:  return new IgnisBlastMagic(world, player, wandInfo);
		case 9:  return new WindStormMagic(world, player, wandInfo);
		case 10: return new CherryRainMagic(world, player, wandInfo);
		default: return new FrostLaserMagic(world, player, wandInfo);
		}
	}

	public void addBuff(Player player, int time) {
		MobEffect buff = PotionInit.sandryon_bless;

		switch (this.data) {
		case 6:
			buff = PotionInit.queen_bless;
			break;
		case 7:
			buff = PotionInit.holy_bless;
			break;
		case 8:
			buff = PotionInit.knight_bless;
			break;
		case 9:
			buff = PotionInit.witch_bless;
			break;
		case 10:
			buff = PotionInit.arlaune_bless;
			break;
		}

		PlayerHelper.setPotion(player, buff, 0, time);
	}

	// ユニーク魔法かどうか
	public boolean isUniqueMagic() {
		return true;
	}

	public boolean isEqualMagic(ItemStack stack, MagicInfo info) {
		return stack.is(info.getItem()) || (this.data >= 6 && stack.is(this.getUniqueItem()));
	}

	public Item getUniqueItem() {
		switch (this.data) {
		case 7:  return ItemInit.magic_holybuster;
		case 8:  return ItemInit.magic_ignisblast;
		case 9:  return ItemInit.magic_windstorm;
		case 10: return ItemInit.magic_cherryrain;
		default: return ItemInit.magic_frostlaser;
		}
	}

	public int getSummonTime() {
		switch (this.data) {
		case 5:
		case 6:
		case 7:
		case 8:
		case 9:
		case 10:
			return 500;
		default: return 400;
		}
	}
}
