package sweetmagic.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.AdvancementEvent.AdvancementProgressEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import sweetmagic.init.AdvancedInit;
import sweetmagic.init.advanced.GrantTrigger;

public class AdvancedEvent {

	@SubscribeEvent
	public static void addAdvanced(AdvancementProgressEvent event) {
		if(!event.getProgressType().equals(AdvancementProgressEvent.ProgressType.GRANT)) { return; }

		if(!event.getAdvancementProgress().isDone()) { return; }
		GrantTrigger grant = AdvancedInit.advancedMap.get(event.getAdvancement().getId());
		if(grant != null && event.getEntity() instanceof ServerPlayer sPlayer) {
			grant.trigger(sPlayer);
			AdvancedInit.all_chalange.trigger(sPlayer);
		}
	}
}
