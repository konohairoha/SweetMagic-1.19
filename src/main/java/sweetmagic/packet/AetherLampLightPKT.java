package sweetmagic.packet;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.network.NetworkEvent;
import sweetmagic.api.util.ISMTip;
import sweetmagic.init.tile.sm.TileAetherLamplight;

public record AetherLampLightPKT(int id, BlockPos pos, boolean isSound) implements IPacket, ISMTip {

	@Override
	public void handle(NetworkEvent.Context con) {
		ServerPlayer player = con.getSender();
		if (player == null) { return; }

		TileAetherLamplight tile = (TileAetherLamplight) player.level.getBlockEntity(this.pos);

		if (this.id > -1) {
			tile.selectId = this.id;
			List<Block> blockSetList = tile.getRangeBlockSet().stream().toList();
			tile.order = tile.blockOrder.getBlockOrder(blockSetList.get(this.id));
			tile.sendPKT();
		}

		if (this.isSound) {
			tile.clickButton();
		}
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeInt(this.id);
		buf.writeBlockPos(this.pos);
		buf.writeBoolean(this.isSound);
	}

	public static AetherLampLightPKT decode(FriendlyByteBuf buf) {
		return new AetherLampLightPKT(buf.readInt(), buf.readBlockPos(), buf.readBoolean());
	}
}
