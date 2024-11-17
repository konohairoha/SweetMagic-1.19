package sweetmagic.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import sweetmagic.api.iitem.info.WandInfo;
import sweetmagic.init.SoundInit;

public class WandLeftClickPKT implements IPacket {

	@Override
	public void handle(NetworkEvent.Context con) {
		ServerPlayer player = con.getSender();
		if (player == null) { return; }

		WandInfo info = new WandInfo(player.getMainHandItem());
		info.getWand().setSelectSlot(info.getStack(), 0);
		info.getWand().playSound(player.level, player, SoundInit.NEXT, 0.15F, 1F);
	}

	@Override
	public void encode(FriendlyByteBuf buf) { }

	public static WandLeftClickPKT decode(FriendlyByteBuf buf) {
		return new WandLeftClickPKT();
	}
}
