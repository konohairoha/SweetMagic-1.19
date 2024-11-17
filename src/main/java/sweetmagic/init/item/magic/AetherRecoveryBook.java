package sweetmagic.init.item.magic;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableList;

import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import sweetmagic.api.iitem.IMFTool;
import sweetmagic.init.item.sm.TreasureItem;

public class AetherRecoveryBook extends TreasureItem {

	public AetherRecoveryBook (String name, int tier) {
		super(name, tier, tier - 1);
	}

	// 右クリック
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {

		// アイテムスタックを取得
		ItemStack stack = player.getItemInHand(hand);

		// インベントリからアイテムの取得
		Inventory pInv = player.getInventory();
		List<NonNullList<ItemStack>> playerStackList = ImmutableList.of(pInv.items, pInv.armor, pInv.offhand);
		List<ItemStack> stackList = new ArrayList<>();

		// MFを溜めれるアイテムだけリストに入れる
		for (List<ItemStack> stacks : playerStackList) {
			List<ItemStack> filterList = stacks.stream().filter(s -> !s.isEmpty() && s.getItem() instanceof IMFTool tool && !tool.isMaxMF(s)).toList();
			if (filterList.isEmpty()) { continue; }

			stackList.addAll(filterList);
		}

		if (stackList.isEmpty()) { return InteractionResultHolder.pass(stack); }

		// 回復するMF量を取得
		int mf = this.getHealMF();
		stackList.forEach(s -> ((IMFTool) s.getItem()).insetMF(s, mf));

		if (!player.isCreative()) { stack.shrink(1); }
		return InteractionResultHolder.consume(stack);
	}

	public int getHealMF () {
		switch (this.data) {
		case 1:  return 8000;
		case 2:  return 32000;
		default: return 2000;
		}
	}

	// ツールチップの表示
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> toolTip, TooltipFlag flag) {
		toolTip.add(this.tierTip(this.tier));
		toolTip.add(this.getText("aether_recovery_book", String.format("%,d", this.getHealMF())).withStyle(GREEN));
	}
}
