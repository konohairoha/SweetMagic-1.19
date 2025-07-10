package sweetmagic.packet;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkEvent.Context;

public interface IPacket {

	void handle(NetworkEvent.Context con);

	void encode(FriendlyByteBuf buf);

	static <P extends IPacket> void handle(final P msg, Supplier<NetworkEvent.Context> ctx) {
		Context con = ctx.get();
		con.enqueueWork(() -> msg.handle(con));
		con.setPacketHandled(true);
	}
}
