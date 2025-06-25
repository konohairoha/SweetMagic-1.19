package sweetmagic.packet;

import java.util.UUID;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import sweetmagic.init.capability.icap.ICookingStatus;

public record AddSPtoServerPKT(UUID uuID, int sp) implements IPacket {

	@Override
	public void handle(NetworkEvent.Context con) {
		ServerPlayer player = con.getSender();
		if (player == null) { return; }

		for (Entity entity : player.getLevel().players()) {
			if(!(entity instanceof Player pl) || !pl.getUUID().equals(this.uuID)) { continue; }
			ICookingStatus cook = ICookingStatus.getState(pl);

			if(cook != null) {
				cook.addTradeSP(this.sp);
				ICookingStatus.sendPKT(pl);
			}

			return;
		}
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeUUID(this.uuID);
		buf.writeInt(this.sp);
	}


	public static AddSPtoServerPKT decode(FriendlyByteBuf buf) {
		return new AddSPtoServerPKT(buf.readUUID(), buf.readInt());
	}
}
