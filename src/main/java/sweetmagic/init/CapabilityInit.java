package sweetmagic.init;

import javax.annotation.Nonnull;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import sweetmagic.init.capability.CookingStatusHandler;
import sweetmagic.init.capability.WorldDataHandler;
import sweetmagic.init.capability.icap.ICookingStatus;
import sweetmagic.init.capability.icap.IWorldData;

public class CapabilityInit {

	public static final Capability<ICookingStatus> COOK = CapabilityManager.get(new CapabilityToken<>() {});
	public static final Capability<IWorldData> TEB = CapabilityManager.get(new CapabilityToken<>() {});

	@SubscribeEvent
	public static void registerCapability(RegisterCapabilitiesEvent event) {
		event.register(ICookingStatus.class);
		event.register(IWorldData.class);
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
			public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction face) {
				return COOK.orEmpty(cap, this.inst.cast());
			}

			@Override
			public CompoundTag serializeNBT() {
				return this.inst.orElseThrow(NullPointerException::new).serializeNBT();
			}

			@Override
			public void deserializeNBT(CompoundTag tag) {
				this.inst.orElseThrow(NullPointerException::new).deserializeNBT(tag);
			}
		});
	}

	public static void attachEntityCapabilitys(AttachCapabilitiesEvent<Level> event) {

		event.addCapability(IWorldData.ID, new ICapabilitySerializable<CompoundTag>() {

			final LazyOptional<IWorldData> inst = LazyOptional.of(() -> new WorldDataHandler());

			@Nonnull
			@Override
			public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction face) {
				return TEB.orEmpty(cap, this.inst.cast());
			}

			@Override
			public CompoundTag serializeNBT() {
				return this.inst.orElseThrow(NullPointerException::new).serializeNBT();
			}

			@Override
			public void deserializeNBT(CompoundTag tag) {
				this.inst.orElseThrow(NullPointerException::new).deserializeNBT(tag);
			}
		});
	}
}
