package sweetmagic.event;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.event.entity.player.SleepingLocationCheckEvent;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.event.level.SleepFinishedTimeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import sweetmagic.api.event.SMUtilEvent;
import sweetmagic.init.DimentionInit;
import sweetmagic.init.block.sm.WoodBed;

public class SMPlayerEvent extends SMUtilEvent {

	// 睡眠完了時のイベント
	@SubscribeEvent
	public static void sleepEvent(SleepFinishedTimeEvent event) {
		if (!(event.getLevel() instanceof ServerLevel world) || world.dimension() != DimentionInit.SweetMagicWorld) { return; }

		ServerLevel sever = event.getLevel().getServer().getLevel(Level.OVERWORLD);
		int dayTime = 24000;
		long day = (sever.getDayTime() / dayTime) + 1;
		sever.setDayTime((day * dayTime));
	}

	@SubscribeEvent
	public static void onPlayerSetSpawn(PlayerSetSpawnEvent event) {
		if (SMPlayerEvent.isBed(event.getEntity(), event.getNewSpawn())) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onSleepTimeCheck(SleepingTimeCheckEvent event) {
		if (SMPlayerEvent.isBed(event.getEntity(), event.getSleepingLocation().get())) {
			event.setResult(Event.Result.ALLOW);
		}
	}

	@SubscribeEvent
	public static void onPlayerSleep(SleepingLocationCheckEvent event) {
		if (SMPlayerEvent.isBed(event.getEntity(), event.getSleepingLocation())) {
			event.setResult(Event.Result.ALLOW);
		}
	}

	public static boolean isBed(LivingEntity entity, BlockPos pos) {
		return entity.getLevel().getBlockState(pos).getBlock() instanceof WoodBed;
	}
}
