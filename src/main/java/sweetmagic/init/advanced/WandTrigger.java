package sweetmagic.init.advanced;

import com.google.gson.JsonObject;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import sweetmagic.SweetMagicCore;

public class WandTrigger extends SimpleCriterionTrigger<WandTrigger.Instance> {

	public final ResourceLocation name;

	public WandTrigger(String name) {
		this.name = SweetMagicCore.getSRC(name);
	}

	@Override
	public ResourceLocation getId() {
		return this.name;
	}

	@Override
	public Instance createInstance(JsonObject json, EntityPredicate.Composite player, DeserializationContext con) {
		return new WandTrigger.Instance(this.name, player);
	}

	public static WandTrigger create(String name) {
		return CriteriaTriggers.register(new WandTrigger(name));
	}

	public void trigger(ServerPlayer player, int needLevel, int wandLevel) {
		this.trigger(player, i -> wandLevel >= needLevel);
	}

	public static class Instance extends AbstractCriterionTriggerInstance {

		public Instance(ResourceLocation name, EntityPredicate.Composite player) {
			super(name, player);
		}
	}
}
