package sweetmagic.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import sweetmagic.api.SweetMagicAPI;
import sweetmagic.api.util.ISMTip;
import sweetmagic.init.tile.sm.TileMFBottler;

public record BottlerPKT(int id, BlockPos pos) implements IPacket, ISMTip {

	@Override
	public void handle(NetworkEvent.Context con) {
		ServerPlayer player = con.getSender();
		if (player == null) { return; }

		TileMFBottler tile = (TileMFBottler) player.level.getBlockEntity(this.pos);

		if (this.id > -1) {

			int mf = tile.getMF();
			tile.selectId = this.id;
			ItemStack stack = tile.getStackList().get(this.id).copy();
			int stackMF = SweetMagicAPI.getMF(stack) * tile.setCount;

			if (mf < stackMF) {
				player.closeContainer();
				String needMF = this.format(stackMF);
				player.sendSystemMessage(this.getTipArray(this.getText("pedastal_nomf").withStyle(RED), ": ", this.getLabel(needMF, WHITE)));
				tile.clickButton();
				return;
			}

			tile.isSelect = true;
			tile.tickTime = -1;
			tile.outStack = stack;
			tile.outStack.setCount(tile.setCount);
			tile.sendPKT();
		}

		tile.clickButton();
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeInt(this.id);
		buf.writeBlockPos(this.pos);
	}

	public static BottlerPKT decode(FriendlyByteBuf buf) {
		return new BottlerPKT(buf.readInt(), buf.readBlockPos());
	}
}
