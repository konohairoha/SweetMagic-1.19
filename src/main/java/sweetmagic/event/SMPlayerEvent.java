package sweetmagic.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.level.SleepFinishedTimeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import sweetmagic.init.DimentionInit;

public class SMPlayerEvent {

	// 睡眠完了時のイベント
	@SubscribeEvent
	public static void sleepEvent (SleepFinishedTimeEvent event) {
		if ( !(event.getLevel() instanceof ServerLevel world) || world.dimension() != DimentionInit.SweetMagicWorld) { return; }

		ServerLevel sever = event.getLevel().getServer().getLevel(Level.OVERWORLD);
		int dayTime = 24000;
		long day = (sever.getDayTime() / dayTime) + 1;
		sever.setDayTime((day * dayTime));
	}
}
