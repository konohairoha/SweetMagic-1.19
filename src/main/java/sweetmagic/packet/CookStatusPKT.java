package sweetmagic.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import sweetmagic.init.CapabilityInit;

public record CookStatusPKT(CompoundTag tags) implements IPacket {

	@Override
	public void handle(NetworkEvent.Context con) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null) { return; }
		player.getCapability(CapabilityInit.COOK).ifPresent(c -> c.deserializeNBT(this.tags));
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeNbt(this.tags);
	}

	public static CookStatusPKT decode(FriendlyByteBuf buf) {
		return new CookStatusPKT(buf.readNbt());
	}
}
