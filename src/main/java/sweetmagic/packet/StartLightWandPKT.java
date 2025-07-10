package sweetmagic.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import sweetmagic.init.item.magic.StartLightWand;

public record StartLightWandPKT(int data, BlockPos pos) implements IPacket {

	@Override
	public void handle(NetworkEvent.Context con) {
		ServerPlayer player = con.getSender();
		if (player == null) { return; }

		ItemStack stack = player.getMainHandItem();
		if (!(stack.getItem() instanceof StartLightWand wand)) { return; }

		ServerLevel world = player.getLevel();

		switch (this.data) {
		case 1:
			wand.registerBlock(world, world.getBlockState(this.pos), this.pos, stack);
			break;
		default:
			wand.setBlock(world, player, stack);
			break;
		}
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeInt(this.data);
		buf.writeBlockPos(this.pos);
	}

	public static StartLightWandPKT decode(FriendlyByteBuf buf) {
		return new StartLightWandPKT(buf.readInt(), buf.readBlockPos());
	}
}
