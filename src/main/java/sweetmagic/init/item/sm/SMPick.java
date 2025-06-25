package sweetmagic.init.item.sm;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
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
		super(data == 2 ? Tiers.NETHERITE :Tiers.DIAMOND, 2, -2.8F + data, SMItem.setItem(value, data == -1 ? null : SweetMagicCore.smMagicTab));
		this.name = name;
		this.data = data;
		ItemInit.itemMap.put(this, name);
	}

	@Override
	public boolean mineBlock(ItemStack stack, Level world, BlockState state, BlockPos pos, LivingEntity entity) {

		if (!entity.isShiftKeyDown()) {
			this.rangeBreake(stack, world, pos, entity, Item::getPlayerPOVHitResult);
		}

		return super.mineBlock(stack, world, state, pos, entity);
	}

	public boolean isAllBlock() {
		return this.data >= 1;
	}

	// ツールチップの表示
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> toolTip, TooltipFlag flag) {
		toolTip.add(this.getText(this.name).withStyle(GREEN));
	}

	public int getRange() {
		switch (this.data) {
		case 0: return 1;
		case 1: return 1;
		case 2: return 1;
		default: return 0;
		}
	}

	public boolean isDepth() {
		return this.data >= 2;
	}

	// アイテム修理
	public boolean isValidRepairItem(ItemStack stack, ItemStack ingot) {
		return ingot.is(ItemInit.alternative_ingot);
	}
}
