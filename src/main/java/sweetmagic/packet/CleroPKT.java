package sweetmagic.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

public record CleroPKT(String name) implements IPacket {

	@Override
	public void handle(NetworkEvent.Context con) {
		ServerPlayer player = con.getSender();
		if (player == null) { return; }

		ItemStack stack = player.getMainHandItem();
		stack.setHoverName(Component.literal(this.name));
		player.level.playSound(null, player.blockPosition(), SoundEvents.UI_BUTTON_CLICK, SoundSource.PLAYERS, 0.25F, 1F);
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeUtf(this.name);
	}

	public static CleroPKT decode(FriendlyByteBuf buf) {
		return new CleroPKT(buf.readUtf());
	}
}
