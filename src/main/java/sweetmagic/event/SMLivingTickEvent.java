package sweetmagic.event;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import sweetmagic.config.SMConfig;
import sweetmagic.init.ItemInit;
import sweetmagic.init.item.magic.SMHarness;

public class SMLivingTickEvent {

	@SubscribeEvent
	public static void onTickEvent(LivingTickEvent event) {
		LivingEntity entity = event.getEntity();
		if (!(entity instanceof Player player) || player.isCreative() || player.isSpectator()) { return; }

		ItemStack stack = player.getItemBySlot(EquipmentSlot.FEET);
		CompoundTag tags = player.getPersistentData();

		if ((!player.getAbilities().instabuild && (stack.isEmpty() || !isHarness(stack))) && tags.getBoolean("isHarness")) {
			player.getAbilities().mayfly = false;
			player.getAbilities().flying = false;
			player.onUpdateAbilities();
			tags.putBoolean("isHarness", false);
		}

		else if (isHarness(stack) && !tags.getBoolean("isHarness")) {
			player.getAbilities().mayfly = true;
			player.onUpdateAbilities();
			tags.putBoolean("isHarness", true);
		}
	}

	public static boolean isHarness (ItemStack stack) {
		if (!stack.is(ItemInit.angel_harness)) { return false; }
		return !((SMHarness) stack.getItem()).isMFEmpty(stack);
	}

	@SubscribeEvent
	public static void mobGriefingEvent(EntityMobGriefingEvent event) {
		if (SMConfig.canTackBlock.get() || !(event.getEntity() instanceof EnderMan)) { return; }
		event.setResult(Result.DENY);
	}
}
