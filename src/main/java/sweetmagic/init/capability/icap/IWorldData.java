package sweetmagic.init.capability.icap;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.CapabilityInit;
import sweetmagic.init.capability.WorldDataHandler;
import sweetmagic.init.tile.sm.TileMagiaLantern;

public interface IWorldData extends ICapabilityProvider, INBTSerializable<CompoundTag> {

	public ResourceLocation ID = SweetMagicCore.getSRC("cap_world");

//	static IWorldData getWorldData(Level world) {
//		return world.getCapability(CapabilityInit.TEB, null).orElse(null);
//	}

	default List<BlockPos> getPosList(Level world) {
//		WorldDataHandler data = (WorldDataHandler) IWorldData.getWorldData(world);
		return IWorldData.getData(world).posList;
	}

	public static void registerPos(Level world, BlockPos pos) {
		IWorldData.getData(world).posList.add(pos);
	}

	public static void removePos(Level world, BlockPos pos) {
		IWorldData.getData(world).posList.removeIf(p -> p.equals(pos));
	}

	public static boolean isPosInRange(Level world, BlockPos entityPos) {

		WorldDataHandler data = IWorldData.getData(world);
		double posX = entityPos.getX();
		double posZ = entityPos.getZ();

		for (BlockPos pos : data.getPosList()) {
			if (data.checkDistance(world, posX, posZ, pos) && data.checkHasMF(world, pos)) { return true; }
		}

		return false;
	}

	default boolean checkDistance (Level world, double posX, double posZ, BlockPos pos) {
		BlockEntity tile = world.getBlockEntity(pos);
		if (tile == null || !(tile instanceof TileMagiaLantern core)) { return false; }
		int range = core.getRange();
		return Math.abs(posX - pos.getX()) <= range && Math.abs(posZ - pos.getZ()) <= range;
	}

	default boolean checkHasMF (Level world, BlockPos pos) {
		BlockEntity tile = world.getBlockEntity(pos);
		if (tile == null || !(tile instanceof TileMagiaLantern core)) { return false; }
		return core.getMF() >= core.getShrinkMF() && !core.isRSPower();
	}

	List<BlockPos> getPosList();

	default CompoundTag writeNBT() {
		CompoundTag nbt = new CompoundTag();
		ListTag tagsList = new ListTag();

		for (BlockPos pos : this.getPosList()) {
			CompoundTag tags = new CompoundTag();
			tags.putInt("x", pos.getX());
			tags.putInt("y", pos.getY());
			tags.putInt("z", pos.getZ());
			tagsList.add(tags);
		}

		nbt.put("tagsList", tagsList);

		return nbt;
	}

	default void readNBT(CompoundTag nbt) {
		ListTag tagsList = nbt.getList("tagsList", 10);

		for (Tag tag : tagsList) {
			CompoundTag tags = (CompoundTag) tag;
			this.getPosList().add(new BlockPos(tags.getInt("x"), tags.getInt("y"), tags.getInt("z")));
		}
	}

	public static WorldDataHandler getData(Level world) {
		return (WorldDataHandler) world.getCapability(CapabilityInit.TEB).resolve().get();
	}
}
