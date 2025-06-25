package sweetmagic.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import sweetmagic.api.iitem.IWand;

public record MouseSclorPKT(boolean isNext) implements IPacket {

	@Override
	public void handle(NetworkEvent.Context con) {
		ServerPlayer player = con.getSender();
		if (player == null) { return; }

		Level world = player.getLevel();
		ItemStack stack = IWand.getWand(player);
		if(stack.isEmpty()) { return; }

		IWand wand = IWand.getWand(stack);

		// 次のスロットへ
		if (this.isNext) {
			wand.nextSlot(world, player, stack);
		}

		// 前のスロットへ
		else {
			wand.backSlot(world, player, stack);
		}
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeBoolean(this.isNext);
	}

	public static MouseSclorPKT decode(FriendlyByteBuf buf) {
		return new MouseSclorPKT(buf.readBoolean());
	}
}
