package sweetmagic.init.item.sm;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import sweetmagic.init.tile.menu.container.BaseContainer.ContainerPhone;

public class Phone extends SMItem {

	public Phone(String name) {
		super(name);
	}

	// 右クリック
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {

		// アイテムスタックを取得
		ItemStack stack = player.getItemInHand(hand);

		if (!world.isClientSide()) {
			NetworkHooks.openScreen((ServerPlayer) player, new ContainerPhone(stack));
		}

		return InteractionResultHolder.consume(stack);
	}

	// ツールチップの表示
	@Override
	public void addTip(ItemStack stack, List<Component> toolTip) {
		toolTip.add(this.getText("phone").withStyle(GOLD));
		toolTip.add(this.getText("phone_multi").withStyle(GOLD));
	}
}
