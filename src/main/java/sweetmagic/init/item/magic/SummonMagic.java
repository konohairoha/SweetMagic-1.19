package sweetmagic.init.item.magic;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import sweetmagic.api.emagic.SMElement;
import sweetmagic.api.emagic.SMMagicType;
import sweetmagic.api.iitem.IPorch;
import sweetmagic.api.iitem.IWand;
import sweetmagic.api.iitem.info.MagicInfo;
import sweetmagic.api.iitem.info.WandInfo;
import sweetmagic.init.ItemInit;
import sweetmagic.init.PotionInit;
import sweetmagic.init.SoundInit;
import sweetmagic.init.entity.animal.AbstractSummonMob;
import sweetmagic.init.entity.animal.StellaWizard;
import sweetmagic.init.entity.animal.WitchAllay;
import sweetmagic.init.entity.animal.WitchCat;
import sweetmagic.init.entity.animal.WitchFox;
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
	public List<MutableComponent> magicToolTip(List<MutableComponent> toolTip) {
		toolTip.add(this.getText(this.name));

		switch (this.data) {
		case 6:
			toolTip.add(this.getText(this.name + 1));
			toolTip.add(this.getText(this.name + 2));
			toolTip.add(this.getText(this.name + 3));
			break;
		}

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

	public boolean summonAction(Level world, Player player, WandInfo wandInfo) {

		// 使用者の前方座標取得
		Vec3 src = new Vec3(player.getX(), player.getY(), player.getZ()).add(0, player.getEyeHeight(), 0);
		Vec3 look = player.getViewVector(1F);

		// 向き先に座標を設定
		Vec3 dest = src.add(look.x * 3, player.getY(), look.z * 3);
		BlockPos pos = new BlockPos(dest.x, player.getY() + 1, dest.z);
		AbstractSummonMob entity = this.getMob(world, player, wandInfo);
		entity.setPos(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
		entity.tame(player);
		entity.tamedState(wandInfo);

		if (this.data == 7) {
			entity.setRange(7.5F);
		}

		else if (this.data == 0) {

			Predicate<ItemStack> flag = s -> s.getItem() instanceof SummonMagic;
			List<ItemStack> magicList = IWand.getMagicList(IWand.getWandList(player), flag);
			List<Item> magicItemList = new ArrayList<>();
			magicList.forEach(s -> magicItemList.add(s.getItem()));

			if (magicItemList.contains(ItemInit.magic_summon_allay)) {
				entity.setAlay(true);
			}

			if (magicItemList.contains(ItemInit.magic_summon_golem)) {
				entity.setGolem(true);
				entity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(entity.getMaxHealth() * 1.25F);
			}

			if (magicItemList.contains(ItemInit.magic_summon_fox)) {
				entity.setFox(true);
			}
		}

		int summonTime = 2400;		// 召喚時間
		ItemStack leg = player.getItemBySlot(EquipmentSlot.LEGS);
		IPorch porch = IPorch.getPorch(leg);

		if (porch != null) {

			float addRate = 0F;

			int summonCount = porch.acceCount(leg, ItemInit.summon_book, 5);
			if (summonCount > 0) {
				addRate = summonCount * 0.2F;
			}

			if (porch.hasAcce(leg, ItemInit.twilight_hourglass)) {
				summonTime *= 1.25F;
				addRate += 0.25F;
			}

			int extensionCount = porch.acceCount(leg, ItemInit.extension_ring, 8);
			if (extensionCount > 0) {
				entity.setRange(entity.getRange() + extensionCount * 0.5F);
			}


			int ribbonCount = porch.acceCount(leg, ItemInit.wizard_ribbon, 5);
			if (ribbonCount > 0) {
				entity.setAttribute(Attributes.MAX_HEALTH, 1F + ribbonCount * 0.05F);
				entity.setHealth(entity.getMaxHealth());
				entity.setHealthArmor(entity.getMaxHealth() * ribbonCount * 0.1F);
			}

			summonTime *= (1F + addRate);
		}

		summonTime *= (1F + Math.min(5F, wandInfo.getLevel() * 0.2F));
		entity.setMaxLifeTime(summonTime);
		this.addPotion(entity, PotionInit.magic_array, 0, 100);

		if (!world.isClientSide()) {
			world.addFreshEntity(entity);
		}

		return true;
	}

	public AbstractSummonMob getMob(Level world, Player player, WandInfo wandInfo) {
		switch (this.data) {
		case 1:  return new WitchAllay(world);
		case 2:  return new WitchGolem(world);
		case 3:  return new WitchMaster(world);
		case 4:  return new WitchWindine(world);
		case 5:  return new WitchIfrit(world);
		case 6:  return new WitchFox(world);
		case 7:  return new WitchCat(world);
		case 8:  return new StellaWizard(world);
		default: return new WitchWolf(world);
		}
	}

	// ユニーク魔法かどうか
	public boolean isUniqueMagic() {
		return this.data == 8;
	}

	public int getSummonTime() {
		return 2400;
	}

	// ツールチップの表示
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> toolTip, TooltipFlag flag) {
		if(this.data != 8) { return; }
		toolTip.add(this.getText("summon_boss_get").withStyle(RED));
	}
}
