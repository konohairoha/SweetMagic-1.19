package sweetmagic.init;

import javax.annotation.Nonnull;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import sweetmagic.init.capability.CookingStatusHandler;
import sweetmagic.init.capability.ICookingStatus;

public class CapabilityInit {

	public static final Capability<ICookingStatus> COOK = CapabilityManager.get(new CapabilityToken<>() {});

	@SubscribeEvent
	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.register(ICookingStatus.class);
	}

	public static void attachEntityCapability(AttachCapabilitiesEvent<Entity> event) {

		if (!(event.getObject() instanceof LivingEntity living)) { return; }

		event.addCapability(ICookingStatus.ID, new ICapabilitySerializable<CompoundTag>() {

			final LazyOptional<ICookingStatus> inst = LazyOptional.of(() -> {
				CookingStatusHandler i = new CookingStatusHandler();
				i.setEntity(living);
				return i;
			});

			@Nonnull
			@Override
			public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, Direction facing) {
				return COOK.orEmpty(capability, inst.cast());
			}

			@Override
			public CompoundTag serializeNBT() {
				return inst.orElseThrow(NullPointerException::new).serializeNBT();
			}

			@Override
			public void deserializeNBT(CompoundTag nbt) {
				inst.orElseThrow(NullPointerException::new).deserializeNBT(nbt);
			}
		});
	}
}
