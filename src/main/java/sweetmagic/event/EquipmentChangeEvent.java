package sweetmagic.event;

import com.google.common.collect.ImmutableMultimap;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import sweetmagic.api.iitem.ISMArmor;
import sweetmagic.init.capability.ICookingStatus;

public class EquipmentChangeEvent {

	@SubscribeEvent
	public static void changeEvent(LivingEquipmentChangeEvent event) {
		if (!(event.getEntity() instanceof Player player)) { return; }

		boolean isFrom = EquipmentChangeEvent.isSMArmor(event.getFrom());
		ItemStack stack = event.getTo();
		boolean isTo = EquipmentChangeEvent.isSMArmor(stack);

		if (isFrom && !isTo) {
			ImmutableMultimap.Builder<Attribute, AttributeModifier> map = ImmutableMultimap.builder();
			map.put(Attributes.MAX_HEALTH, new AttributeModifier("SM healthUP", -5F, AttributeModifier.Operation.ADDITION));
			player.getAttributes().addTransientAttributeModifiers(map.build());
			player.setHealth(player.getHealth());
		}

		else if (!isFrom && isTo) {
			ImmutableMultimap.Builder<Attribute, AttributeModifier> map = ImmutableMultimap.builder();
			map.put(Attributes.MAX_HEALTH, new AttributeModifier("SM healthUP", 5F, AttributeModifier.Operation.ADDITION));
			player.getAttributes().addTransientAttributeModifiers(map.build());
			player.setHealth(player.getHealth());

			float health = ICookingStatus.getState(player).getHealth();

			if (health > player.getHealth()) {
				player.setHealth(health);
			}
		}
	}

	public static boolean isSMArmor(ItemStack stack) {
		return stack.getItem() instanceof ISMArmor armor && armor.getTier() >= 2;
	}
}
