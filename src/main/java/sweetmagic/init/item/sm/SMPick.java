package sweetmagic.init.item.sm;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.iitem.IRangeTool;
import sweetmagic.api.util.ISMTip;
import sweetmagic.init.ItemInit;

public class SMPick extends PickaxeItem implements ISMTip, IRangeTool {

	private final String name;
	private final int data;

	public SMPick(String name, int data, int value) {
		super(Tiers.DIAMOND, 2, -2.8F, SMItem.setItem(value, data == 1 ? null : SweetMagicCore.smMagicTab));
		this.name = name;
		this.data = data;
		ItemInit.itemMap.put(this, name);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		return InteractionResultHolder.consume(player.getItemInHand(hand));
	}

	@Override
	public boolean mineBlock(ItemStack stack, Level world, BlockState state, BlockPos pos, LivingEntity living) {

		if (!living.isShiftKeyDown()) {
			this.rangeBreake(stack, world, pos, living, Item::getPlayerPOVHitResult);
		}

		return super.mineBlock(stack, world, state, pos, living);
	}

	public boolean isAllBlock () {
		return this.data == 2;
	}

	// ツールチップの表示
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> toolTip, TooltipFlag flag) {
		toolTip.add(this.getText(this.name).withStyle(GREEN));
	}

	public int getRange() {
		switch (this.data) {
		case 0: return 1;
		case 2: return 1;
		default: return 0;
		}
	}

	// アイテム修理
	public boolean isValidRepairItem(ItemStack stack, ItemStack ingot) {
		return ingot.is(ItemInit.alt_ingot);
	}
}
