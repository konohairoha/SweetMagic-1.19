package sweetmagic.init.capability;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import sweetmagic.init.CapabilityInit;
import sweetmagic.init.capability.icap.IWorldData;

public class WorldDataHandler implements IWorldData {

	public final List<BlockPos> posList = new ArrayList<>();

	@Nullable
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction face) {
		return cap == CapabilityInit.TEB ? LazyOptional.of(() -> (T) this) : LazyOptional.empty();
	}

	public List<BlockPos> getPosList() {
		return this.posList;
	}

	@Override
	public CompoundTag serializeNBT() {
		return this.writeNBT();
	}

	@Override
	public void deserializeNBT(CompoundTag tags) {
		this.readNBT(tags);
	}
}
