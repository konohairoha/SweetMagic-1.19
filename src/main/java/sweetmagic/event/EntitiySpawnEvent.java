package sweetmagic.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import sweetmagic.config.SMConfig;
import sweetmagic.init.capability.icap.IWorldData;

public class EntitiySpawnEvent {

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onEntityCheckSpawn(LivingSpawnEvent.CheckSpawn event) {
		if (event.getResult() == Result.ALLOW || event.isSpawner() || event.isCanceled()) { return; }

		Mob mob = event.getEntity();

		// ファントムまたはドラウンド、行商人は死すべし
		if ((mob instanceof Phantom && !SMConfig.spawnPhantom.get()) ||
			(mob instanceof Drowned && !SMConfig.spawnDrowned.get()) ||
			(mob instanceof WanderingTrader && !SMConfig.spawnTrader.get())) {
			event.setResult(Result.DENY);
		}

		if(!(mob instanceof Enemy) && !(mob instanceof Bat)) { return; }

		Level world = mob.getLevel();
		BlockPos pos = new BlockPos(event.getX(), event.getY(), event.getZ());

		if (IWorldData.isPosInRange(world, pos)) {
			event.setResult(Result.DENY);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onEntityCheckSpecialSpawn(LivingSpawnEvent.SpecialSpawn event) {
		if (SMConfig.spawnTrader.get()) { return; }

		Mob mob = event.getEntity();
		if (!(mob instanceof WanderingTrader)) { return; }

		event.setResult(Result.DENY);
		mob.setHealth(0F);
	}
}
