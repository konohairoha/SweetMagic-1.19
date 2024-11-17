package sweetmagic.init.capability;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullLazy;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.items.IItemHandler;

public abstract class BasicCapabilityResolver<CAPABILITY> implements ICapabilityResolver<CAPABILITY> {

	private final NonNullSupplier<CAPABILITY> supplier;
	private LazyOptional<CAPABILITY> capability;

	public static ICapabilityResolver<IItemHandler> getBasicItemHandlerResolver(NonNullSupplier<IItemHandler> sup) {
		return new BasicCapabilityResolver<>(sup) {
			@NotNull
			@Override
			public Capability<IItemHandler> getMatchingCapability() {
				return ForgeCapabilities.ITEM_HANDLER;
			}
		};
	}

	public static ICapabilityResolver<IItemHandler> getBasicItemHandlerResolver(IItemHandler hand) {
		return new BasicCapabilityResolver<>(hand) {
			@NotNull
			@Override
			public Capability<IItemHandler> getMatchingCapability() {
				return ForgeCapabilities.ITEM_HANDLER;
			}
		};
	}

	protected BasicCapabilityResolver(CAPABILITY con) {
		this.supplier = () -> con;
	}

	protected BasicCapabilityResolver(NonNullSupplier<CAPABILITY> sup) {
		this.supplier = sup instanceof NonNullLazy ? sup : NonNullLazy.of(sup);
	}

	@NotNull
	@Override
	public <T> LazyOptional<T> getCapabilityUnchecked(@NotNull Capability<T> cap, @Nullable Direction face) {
		if (this.capability == null || !this.capability.isPresent()) {
			this.capability = LazyOptional.of(this.supplier);
		}
		return this.capability.cast();
	}

	@Override
	public void invalidate(@NotNull Capability<?> cap, @Nullable Direction face) {
		this.invalidateAll();
	}

	@Override
	public void invalidateAll() {
		if (this.capability != null && this.capability.isPresent()) {
			this.capability.invalidate();
			this.capability = null;
		}
	}
}
