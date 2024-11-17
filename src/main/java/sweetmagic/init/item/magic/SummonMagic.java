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
import sweetmagic.init.entity.animal.AbstractSummonMob;
import sweetmagic.init.entity.animal.WitchAllay;
import sweetmagic.init.entity.animal.WitchGolem;
import sweetmagic.init.entity.animal.WitchIfrit;
import sweetmagic.init.entity.animal.WitchMaster;
import sweetmagic.init.entity.animal.WitchWindine;
import sweetmagic.init.entity.animal.WitchWolf;

public class SummonMagic extends BaseMagicItem {

	public final int data;

	public SummonMagic(String name, SMElement ele, int tier, int coolTime, int useMF, int data) {
		super(name, SMMagicType.SUMMON, ele, tier, coolTime, useMF, false);
		this.data = data;
	}

	public SummonMagic(String name, SMElement ele, int tier, int coolTime, int useMF, int data, String iconName) {
		super(name, SMMagicType.SUMMON, ele, tier, coolTime, useMF, false, iconName);
		this.data = data;
	}

	// ツールチップ
	public List<MutableComponent> magicToolTip (List<MutableComponent> toolTip) {
		toolTip.add(this.getText(this.name));
		toolTip.add(this.getText("summon_status"));
		toolTip.add(this.getText("summon_keep"));
		return toolTip;
	}

	@Override
	public boolean onItemAction(Level world, Player player, WandInfo wandInfo, MagicInfo magicInfo) {
		this.summonAction(world, player, wandInfo);
		this.playSound(world, player.blockPosition(), SoundInit.HORAMAGIC, 0.1F, 1F);
		return true;
	}

	public boolean summonAction (Level world, Player player, WandInfo wandInfo) {

		// 攻撃者の座標取得
		Vec3 src = new Vec3(player.getX(), player.getY(), player.getZ()).add(0, player.getEyeHeight(), 0);
		Vec3 look = player.getViewVector(1.0F);

		// 向き先に座標を設定
		Vec3 dest = src.add(look.x * 3, player.getY(), look.z * 3);
		BlockPos pos = new BlockPos(dest.x, player.getY(), dest.z);

		BlockPos p = new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ());
		AbstractSummonMob entity = this.getMob(world, player, wandInfo);
		entity.setPos(p.getX() + 0.5D, p.getY(), p.getZ() + 0.5D);
		entity.tame(player);
		entity.tamedState(wandInfo);

		int summonTime = 2400;		// 召喚時間
		ItemStack leg = player.getItemBySlot(EquipmentSlot.LEGS);
		IPorch porch = IPorch.getPorch(leg);

		if (porch != null) {
			int summonCount = porch.acceCount(leg, ItemInit.summon_book, 5);
			if (summonCount > 0) {
				summonTime *= 1F + summonCount * 0.2F;
			}

			int extensionCount = porch.acceCount(leg, ItemInit.extension_ring, 8);
			if (extensionCount > 0) {
				entity.setRange(extensionCount * 0.5F);
			}
		}

		summonTime *= 1F + Math.min(5F, wandInfo.getLevel() * 0.2F);
		entity.setMaxLifeTime(summonTime);

		if (!world.isClientSide) {
			world.addFreshEntity(entity);
		}

		return true;
	}

	public AbstractSummonMob getMob (Level world, Player player, WandInfo wandInfo) {
		switch (this.data) {
		case 1:  return new WitchAllay(world);
		case 2:  return new WitchGolem(world);
		case 3:  return new WitchMaster(world);
		case 4:  return new WitchWindine(world);
		case 5:  return new WitchIfrit(world);
		default: return new WitchWolf(world);
		}
	}

	// ユニーク魔法かどうか
	public boolean isUniqueMagic () {
		return false;
	}

	public int getSummonTime () {
		switch (this.data) {
		default: return 2400;
		}
	}
}
