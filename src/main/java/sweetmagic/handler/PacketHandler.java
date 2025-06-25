package sweetmagic.handler;

import java.util.Optional;
import java.util.function.Function;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import sweetmagic.SweetMagicCore;
import sweetmagic.packet.AddSPtoServerPKT;
import sweetmagic.packet.AetherLampLightPKT;
import sweetmagic.packet.BottlerPKT;
import sweetmagic.packet.CleroPKT;
import sweetmagic.packet.CompasPKT;
import sweetmagic.packet.CookStatusPKT;
import sweetmagic.packet.FurnitureCraftPKT;
import sweetmagic.packet.IPacket;
import sweetmagic.packet.KeyPressPKT;
import sweetmagic.packet.MouseSclorPKT;
import sweetmagic.packet.PhonePKT;
import sweetmagic.packet.StartLightWandPKT;
import sweetmagic.packet.SummonerWandPKT;
import sweetmagic.packet.WandLeftClickPKT;
import sweetmagic.packet.WoodChestLootPKT;

public class PacketHandler {

	private static int index;
	private static final String PRO_VER = Integer.toString(3);
	private static final SimpleChannel HANDLER = NetworkRegistry.ChannelBuilder.named(SweetMagicCore.getSRC("main_channel")).clientAcceptedVersions(PRO_VER::equals).serverAcceptedVersions(PRO_VER::equals).networkProtocolVersion(() -> PRO_VER).simpleChannel();

	public static void register() {
		registerClientToServer(KeyPressPKT.class, KeyPressPKT::decode);
		registerClientToServer(MouseSclorPKT.class, MouseSclorPKT::decode);
		registerClientToServer(WandLeftClickPKT.class, WandLeftClickPKT::decode);
		registerClientToServer(StartLightWandPKT.class, StartLightWandPKT::decode);
		registerClientToServer(CleroPKT.class, CleroPKT::decode);
		registerClientToServer(CompasPKT.class, CompasPKT::decode);
		registerClientToServer(PhonePKT.class, PhonePKT::decode);
		registerClientToServer(WoodChestLootPKT.class, WoodChestLootPKT::decode);
		registerClientToServer(FurnitureCraftPKT.class, FurnitureCraftPKT::decode);
		registerClientToServer(BottlerPKT.class, BottlerPKT::decode);
		registerClientToServer(AetherLampLightPKT.class, AetherLampLightPKT::decode);
		registerClientToServer(SummonerWandPKT.class, SummonerWandPKT::decode);
		registerClientToServer(AddSPtoServerPKT.class, AddSPtoServerPKT::decode);

		registerServerToClient(CookStatusPKT.class, CookStatusPKT::decode);
	}

	private static <MSG extends IPacket> void registerClientToServer(Class<MSG> type, Function<FriendlyByteBuf, MSG> decoder) {
		registerMessage(type, decoder, NetworkDirection.PLAY_TO_SERVER);
	}

	private static <MSG extends IPacket> void registerServerToClient(Class<MSG> type, Function<FriendlyByteBuf, MSG> decoder) {
		registerMessage(type, decoder, NetworkDirection.PLAY_TO_CLIENT);
	}

	private static <MSG extends IPacket> void registerMessage(Class<MSG> type, Function<FriendlyByteBuf, MSG> deco, NetworkDirection network) {
		HANDLER.registerMessage(index++, type, IPacket::encode, deco, IPacket::handle, Optional.of(network));
	}

	public static <MSG extends IPacket> void sendToServer(MSG msg) {
		HANDLER.sendToServer(msg);
	}

	public static <MSG extends IPacket> void sendTo(MSG msg, ServerPlayer player) {
		HANDLER.sendTo(msg, player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
	}
}
