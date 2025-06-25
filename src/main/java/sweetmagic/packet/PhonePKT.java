package sweetmagic.packet;

import java.util.UUID;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

public record PhonePKT(UUID id, int selectId, boolean isSelect) implements IPacket {

	@Override
	public void handle(NetworkEvent.Context con) {
		ServerPlayer player = con.getSender();
		if (player == null) { return; }

		CompoundTag tags = player.getMainHandItem().getOrCreateTag();

		if(this.selectId == -1) {
			tags.putUUID("userId", this.id);
			tags.putBoolean("isSelect", this.isSelect);
		}

		else {
			tags.putInt("selectId", this.selectId);
		}
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeUUID(this.id);
		buf.writeInt(this.selectId);
		buf.writeBoolean(this.isSelect);
	}

	public static PhonePKT decode(FriendlyByteBuf buf) {
		return new PhonePKT(buf.readUUID(), buf.readInt(), buf.readBoolean());
	}
}
