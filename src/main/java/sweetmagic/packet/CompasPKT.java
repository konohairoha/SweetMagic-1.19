package sweetmagic.packet;

import com.mojang.datafixers.util.Pair;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.network.NetworkEvent;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.StructureInit;
import sweetmagic.util.WorldHelper;

public record CompasPKT(int selectId) implements IPacket {

	@Override
	public void handle(NetworkEvent.Context con) {
		ServerPlayer player = con.getSender();
		if (player == null) { return; }

		ItemStack stack = player.getMainHandItem();
		CompoundTag tags = stack.getOrCreateTag();
		tags.putInt("selectId", this.selectId);
		tags.putBoolean("foundStructure", false);
		tags.putBoolean("isSearch ", true);
		tags.putBoolean("notFound", false);

		ServerLevel server = player.getLevel();
		server.playSound(null, player.blockPosition(), SoundEvents.UI_BUTTON_CLICK, SoundSource.PLAYERS, 0.25F, 1F);

		if(this.selectId <= -1) {
			tags.putBoolean("isSearch", false);
			return;
		}

		Structure structure = WorldHelper.getStructureKey(server, SweetMagicCore.getSRC(StructureInit.strucMap.get(this.selectId).name()));
		HolderSet<Structure> hol = HolderSet.direct(WorldHelper.getStructure(server, structure));
		Pair<BlockPos, Holder<Structure>> pair = server.getChunkSource().getGenerator().findNearestMapStructure(server, hol, player.blockPosition(), 100, false);
		tags.putBoolean("isSearch", false);

		if (pair == null) {
			tags.putBoolean("notFound", true);
			return;
		}

		tags.putBoolean("foundStructure", true);
		tags.putInt("X", pair.getFirst().getX());
		tags.putInt("Z", pair.getFirst().getZ());
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeInt(this.selectId);
	}

	public static CompasPKT decode(FriendlyByteBuf buf) {
		return new CompasPKT(buf.readInt());
	}
}
