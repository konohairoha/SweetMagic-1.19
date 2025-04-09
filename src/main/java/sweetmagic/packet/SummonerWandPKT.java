package sweetmagic.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import sweetmagic.init.item.sm.SummonerWand;

public class SummonerWandPKT implements IPacket {

	@Override
	public void handle(NetworkEvent.Context con) {
		ServerPlayer player = con.getSender();
		if (player == null) { return; }

		ItemStack stack = player.getMainHandItem();
		SummonerWand wand = (SummonerWand) stack.getItem();
		wand.leftClick(player.getLevel(), player, stack);
	}

	@Override
	public void encode(FriendlyByteBuf buf) { }

	public static SummonerWandPKT decode(FriendlyByteBuf buf) {
		return new SummonerWandPKT();
	}
}
