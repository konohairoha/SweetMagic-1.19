package sweetmagic.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import sweetmagic.init.tile.sm.TileFurnitureTable;

public record FurnitureCraftPKT(int count, BlockPos pos) implements IPacket {

	@Override
	public void handle(NetworkEvent.Context con) {
		ServerPlayer player = con.getSender();
		if (player == null) { return; }

		TileFurnitureTable tile = (TileFurnitureTable) player.level.getBlockEntity(this.pos);
		tile.setCount = this.count;
		tile.sendPKT();
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeInt(this.count);
		buf.writeBlockPos(this.pos);
	}

	public static FurnitureCraftPKT decode(FriendlyByteBuf buf) {
		return new FurnitureCraftPKT(buf.readInt(), buf.readBlockPos());
	}
}
