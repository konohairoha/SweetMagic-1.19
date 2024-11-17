package sweetmagic.init.capability;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullLazy;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import sweetmagic.init.tile.slot.WrappedItemHandler;
import sweetmagic.init.tile.slot.WrappedItemHandler.WriteMode;

public interface ICapabilityResolver<CAPABILITY> extends ICapabilityProvider {

	@NotNull
	Capability<CAPABILITY> getMatchingCapability();

	@NotNull
	<T> LazyOptional<T> getCapabilityUnchecked(@NotNull Capability<T> capability, @Nullable Direction side);

	@NotNull
	@Override
	default  <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side) {
		if (capability == getMatchingCapability()) {
			return getCapabilityUnchecked(capability, side);
		}
		return LazyOptional.empty();
	}

	void invalidate(@NotNull Capability<?> capability, @Nullable Direction side);

	void invalidateAll();

	default NonNullLazy<IItemHandler> getHandler (IItemHandlerModifiable compose, WriteMode mode) {
		return () -> new WrappedItemHandler(compose, mode);
	}

	default NonNullLazy<IItemHandler> getHandlerArray (IItemHandlerModifiable... compose) {
		return () -> new CombinedInvWrapper(compose);
	}

	default ICapabilityResolver<IItemHandler> getBasicResolver(NonNullSupplier<IItemHandler> sup) {
		return BasicCapabilityResolver.getBasicItemHandlerResolver(sup);
	}
}
