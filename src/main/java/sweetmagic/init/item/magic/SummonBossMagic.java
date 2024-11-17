package sweetmagic.init.item.magic;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import sweetmagic.api.emagic.SMElement;
import sweetmagic.api.emagic.SMMagicType;
import sweetmagic.api.iitem.IPorch;
import sweetmagic.api.iitem.info.MagicInfo;
import sweetmagic.api.iitem.info.WandInfo;
import sweetmagic.init.ItemInit;
import sweetmagic.init.SoundInit;
import sweetmagic.init.entity.projectile.AbstractBossMagic;
import sweetmagic.init.entity.projectile.CherryRainMagic;
import sweetmagic.init.entity.projectile.FrostLaserMagic;
import sweetmagic.init.entity.projectile.HolyBusterMagic;
import sweetmagic.init.entity.projectile.IgnisBlastMagic;
import sweetmagic.init.entity.projectile.WindStormMagic;

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
	public List<MutableComponent> magicToolTip (List<MutableComponent> toolTip) {
		toolTip.add(this.getText(this.name));
		return toolTip;
	}

	@Override
	public boolean onItemAction(Level world, Player player, WandInfo wandInfo, MagicInfo magicInfo) {
		this.bossMagicAction(world, player, wandInfo);
		this.playSound(world, player.blockPosition(), SoundInit.HORAMAGIC, 0.1F, 1F);
		return true;
	}

	public boolean bossMagicAction (Level world, Player player, WandInfo wandInfo) {

		// 攻撃者の座標取得
		Vec3 src = new Vec3(player.getX(), player.getY(), player.getZ()).add(0, player.getEyeHeight(), 0);
		Vec3 look = player.getViewVector(1.0F);

		// 向き先に座標を設定
		Vec3 dest = src.add(look.x * 3, player.getY(), look.z * 3);
		BlockPos pos = new BlockPos(dest.x, player.getY(), dest.z);

		BlockPos p = new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ());
		AbstractBossMagic entity = this.getShot(world, player, wandInfo);
		entity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 1F, 0);
		entity.shoot(0D, 0D, 0D, 1.35F, 0F);
		entity.setPos(p.getX() + 0.5D, p.getY(), p.getZ() + 0.5D);
		entity.setHitDead(false);
		entity.setNotDamage(true);
		entity.setData(this.data);
		entity.setRotData(-player.getYRot());
		entity.acceEffect();

		// 召喚時間
		int summonTime = 400;
		ItemStack leg = player.getItemBySlot(EquipmentSlot.LEGS);

		if (!leg.isEmpty() && leg.getItem() instanceof IPorch porch) {
			int summonCount = porch.acceCount(leg, ItemInit.summon_book, 5);
			if (summonCount > 0) {
				summonTime *= 1F + summonCount * 0.2F;
			}
		}

		entity.setMaxLifeTime(summonTime);

		if (!world.isClientSide) {
			world.addFreshEntity(entity);
		}

		return true;
	}

	public AbstractBossMagic getShot (Level world, Player player, WandInfo wandInfo) {
		switch (this.data) {
		case 1:  return new HolyBusterMagic(world, player, wandInfo);
		case 2:  return new IgnisBlastMagic(world, player, wandInfo);
		case 3:  return new WindStormMagic(world, player, wandInfo);
		case 4:  return new CherryRainMagic(world, player, wandInfo);
		default: return new FrostLaserMagic(world, player, wandInfo);
		}
	}

	// ユニーク魔法かどうか
	public boolean isUniqueMagic () {
		return this.data <= 2;
	}

	public int getSummonTime () {
		switch (this.data) {
		default: return 400;
		}
	}
}
