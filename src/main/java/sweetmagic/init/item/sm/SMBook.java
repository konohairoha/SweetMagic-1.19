package sweetmagic.init.item.sm;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.iitem.IMagicBook;
import sweetmagic.init.item.magic.SMMagicItem;

public class SMBook extends SMMagicItem implements IMagicBook {

	private final int data;

	public SMBook(String name, int data) {
		super(name, new Item.Properties().tab(SweetMagicCore.smMagicTab).stacksTo(1));
		this.data = data;
	}

	// 右クリック
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {

		// アイテムスタックを取得
		ItemStack stack = player.getItemInHand(hand);

		if (!world.isClientSide()) {
			this.openCraftGui(world, player, stack);
		}

		return InteractionResultHolder.consume(stack);
	}

	public int getTier() {
		return this.data + 1;
	}

	@Override
	public boolean hasCraftingRemainingItem(ItemStack stack) {
		return true;
	}

	@Override
	public ItemStack getCraftingRemainingItem(ItemStack stack) {
		return stack.copy();
	}

	// ツールチップの表示
	public void addTip(ItemStack stack, List<Component> toolTip) {
		toolTip.add(this.tierTip(this.getTier()));
		toolTip.add(this.getText("magic_book").withStyle(GREEN));
		toolTip.add(this.getText("magic_book_return").withStyle(GOLD));
	}

	// スロット数
	public int getSlotSize() {
		switch (this.data) {
		case 1:  return 5;
		case 2:  return 10;
		default: return 3;
		}
	}
}
