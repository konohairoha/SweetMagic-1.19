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

public class GrantTrigger extends SimpleCriterionTrigger<GrantTrigger.Instance> {

	public final ResourceLocation name;

	public GrantTrigger(String name) {
		this.name = SweetMagicCore.getSRC(name);
	}

	@Override
	public ResourceLocation getId() {
		return this.name;
	}

	@Override
	public Instance createInstance(JsonObject json, EntityPredicate.Composite player, DeserializationContext con) {
		return new GrantTrigger.Instance(this.name, player);
	}

	public static GrantTrigger create(String name) {
		return CriteriaTriggers.register(new GrantTrigger(name));
	}

	public void trigger(ServerPlayer player) {
		this.trigger(player, i -> true);
	}

	public static class Instance extends AbstractCriterionTriggerInstance {

		public Instance(ResourceLocation name, EntityPredicate.Composite player) {
			super(name, player);
		}
	}
}
