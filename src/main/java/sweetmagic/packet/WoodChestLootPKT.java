package sweetmagic.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import sweetmagic.init.LootInit;
import sweetmagic.init.tile.sm.TileWoodChest;

public record WoodChestLootPKT(int count, float chance, int selectID, BlockPos pos) implements IPacket {

	@Override
	public void handle(NetworkEvent.Context con) {
		ServerPlayer player = con.getSender();
		if (player == null) { return; }

		TileWoodChest tile = (TileWoodChest) player.getLevel().getBlockEntity(this.pos);
		tile.count = this.count;
		tile.chance = this.chance;
		tile.lootTable = LootInit.lootList.get(this.selectID);
		tile.sendPKT();
		tile.clickButton();
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeInt(this.count);
		buf.writeFloat(this.chance);
		buf.writeInt(this.selectID);
		buf.writeBlockPos(this.pos);
	}

	public static WoodChestLootPKT decode(FriendlyByteBuf buf) {
		return new WoodChestLootPKT(buf.readInt(), buf.readFloat(), buf.readInt(), buf.readBlockPos());
	}
}
