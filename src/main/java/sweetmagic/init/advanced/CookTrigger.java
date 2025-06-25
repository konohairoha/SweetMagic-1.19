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

public class CookTrigger extends SimpleCriterionTrigger<CookTrigger.Instance> {

	public final ResourceLocation name;

	public CookTrigger(String name) {
		this.name = SweetMagicCore.getSRC(name);
	}

	@Override
	public ResourceLocation getId() {
		return this.name;
	}

	@Override
	public Instance createInstance(JsonObject json, EntityPredicate.Composite player, DeserializationContext con) {
		return new CookTrigger.Instance(this.name, player);
	}

	public static CookTrigger create(String name) {
		return CriteriaTriggers.register(new CookTrigger(name));
	}

	public void trigger(ServerPlayer player, int needLevel, int cookLevel) {
		this.trigger(player, i -> cookLevel >= needLevel);
	}

	public static class Instance extends AbstractCriterionTriggerInstance {

		public Instance(ResourceLocation name, EntityPredicate.Composite player) {
			super(name, player);
		}
	}
}
