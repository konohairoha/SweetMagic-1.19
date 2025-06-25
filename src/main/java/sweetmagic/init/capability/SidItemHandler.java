package sweetmagic.init.capability;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import sweetmagic.init.capability.icap.ICapabilityResolver;
import sweetmagic.init.tile.slot.WrappedItemHandler;
import sweetmagic.init.tile.slot.WrappedItemHandler.WriteMode;

public abstract class SidItemHandler implements ICapabilityResolver<IItemHandler> {

	protected static final WriteMode IN = WrappedItemHandler.WriteMode.IN;
	protected static final WriteMode IN_OUT = WrappedItemHandler.WriteMode.IN_OUT;
	protected static final WriteMode OUT = WrappedItemHandler.WriteMode.OUT;
	protected abstract ICapabilityResolver<IItemHandler> getResolver(@Nullable Direction face);

	@NotNull
	@Override
	public Capability<IItemHandler> getMatchingCapability() {
		return ForgeCapabilities.ITEM_HANDLER;
	}

	@NotNull
	@Override
	public <T> LazyOptional<T> getCapabilityUnchecked(@NotNull Capability<T> cap, @Nullable Direction face) {
		return this.getResolver(face).getCapabilityUnchecked(cap, face);
	}

	@Override
	public void invalidate(@NotNull Capability<?> cap, @Nullable Direction face) {
		this.getResolver(face).invalidate(cap, face);
	}
}
