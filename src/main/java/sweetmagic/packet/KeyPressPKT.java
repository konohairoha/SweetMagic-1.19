package sweetmagic.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import sweetmagic.api.iitem.IMagicBook;
import sweetmagic.api.iitem.IPorch;
import sweetmagic.api.iitem.IRobe;
import sweetmagic.api.iitem.IWand;
import sweetmagic.init.ItemInit;
import sweetmagic.init.item.sm.SMAxe;
import sweetmagic.key.SMKeybind;

public record KeyPressPKT(SMKeybind key) implements IPacket {

	@Override
	public void handle(NetworkEvent.Context con) {
		ServerPlayer player = con.getSender();
		if (player == null) { return; }

		Level level = player.level;
		ItemStack stack = player.getMainHandItem();
		Item item = stack.getItem();
		ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
		ItemStack leg = player.getItemBySlot(EquipmentSlot.LEGS);

		switch  (this.key) {
		case OPEN:

			if (item instanceof IWand wand) {
				wand.openGui(level, player, stack);
			}

			else if (!chest.isEmpty() && chest.getItem() instanceof IRobe robe) {
				robe.openGui(level, player, chest);
			}

			break;
		case NEXT:

			if (player.isShiftKeyDown()) {

				if (!leg.isEmpty() && leg.getItem() instanceof IPorch porch) {

					// シフト押しつつアブソープペンダントなら機能停止
					if (porch.hasAcce(leg, ItemInit.earth_ruby_ring)) {
						porch.acceInvalidate(player, leg, ItemInit.earth_ruby_ring);
						return;
					}
				}
			}

			if (item instanceof IWand wand) {
				wand.nextSlot(level, player, stack);
			}

			break;
		case BACK:

			if (item instanceof IMagicBook book) {
				book.openGui(level, player, stack);
			}

			else if (item instanceof IWand wand) {
				wand.backSlot(level, player, stack);
			}
			break;
		case POUCH:

			// シフト押しつつアブソープペンダントなら機能停止
			if (!leg.isEmpty() && leg.getItem() instanceof IPorch porch) {
				if (player.isShiftKeyDown() && porch.hasAcce(leg, ItemInit.gravity_pendant)) {
					porch.acceInvalidate(player, leg, ItemInit.gravity_pendant);
					break;
				}

				porch.openGui(level, player, leg);
			}

			break;
		case SPECIAL:
			if (item instanceof SMAxe axe) {
				axe.cancelAction(player);
			}
			break;
		}
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeEnum(key);
	}

	public static KeyPressPKT decode(FriendlyByteBuf buf) {
		return new KeyPressPKT(buf.readEnum(SMKeybind.class));
	}
}
