package sweetmagic.init.item.magic;

import java.util.List;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import sweetmagic.api.emagic.SMElement;
import sweetmagic.api.emagic.SMMagicType;
import sweetmagic.api.iitem.IWand;
import sweetmagic.api.iitem.info.MagicInfo;
import sweetmagic.api.iitem.info.WandInfo;

public class RankUpMagic extends BaseMagicItem {

	private final int data;
	private int needEXP = 0;

	public RankUpMagic(String name, SMElement ele, int tier, int coolTime, int useMF, int data) {
		super(name, SMMagicType.NORMAL, ele, tier, coolTime, useMF, true);
		this.data = data;
	}

	public RankUpMagic(String name, SMElement ele, int tier, int coolTime, int useMF, int data, String iconName) {
		super(name, SMMagicType.NORMAL, ele, tier, coolTime, useMF, true, iconName);
		this.data = data;
	}

	/**
	 * 0 = 範囲回復魔法
	 */

	// ツールチップ
	public List<MutableComponent> magicToolTip(List<MutableComponent> toolTip) {
		toolTip.add(this.getText("magic_aether_force", String.format("%,d", this.addExp())));
		return toolTip;
	}

	@Override
	public boolean onItemAction(Level world, Player player, WandInfo wandInfo, MagicInfo magicInfo) {

		// 杖の取得
		ItemStack stack = wandInfo.getStack();
		boolean flag = true;

		switch (this.data) {
		case 5:
			// 範囲回復魔法
			flag = this.ruckUpMagic(world, player, stack, wandInfo);
			break;
		}

		return flag;
	}

	// 回復魔法
	public boolean ruckUpMagic(Level world, Player player, ItemStack stack, WandInfo wandInfo) {

		// 杖の取得
		IWand wand = wandInfo.getWand();
		int level = wandInfo.getLevel();

		if (wand.isNotElement()) {
			level -= 3;
		}

		this.needEXP = wand.needExp(wand.getMaxLevel(), level + 1, stack);
		return true;
	}

	// 追加経験値
	public int addExp() {
		switch(this.data) {
		case 1: return 1000;
		case 2: return 3000;
		case 3: return 8000;
		case 4: return 40000;
		case 5: return this.needEXP;
		default: return 200;
		}
	}

	@Override
	public boolean canItemMagic(Level world, Player player, WandInfo info) {
		IWand wand = info.getWand();
		int level = info.getLevel();
		return !wand.isCreativeWand() && level < wand.getMaxLevel();
	}
}
