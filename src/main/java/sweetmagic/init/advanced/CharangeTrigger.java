package sweetmagic.init.advanced;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.AdvancedInit;
import sweetmagic.util.PlayerHelper;

public class CharangeTrigger extends SimpleCriterionTrigger<CharangeTrigger.Instance> {

	public final ResourceLocation name;

	public CharangeTrigger(String name) {
		this.name = SweetMagicCore.getSRC(name);
	}

	@Override
	public ResourceLocation getId() {
		return this.name;
	}

	@Override
	public Instance createInstance(JsonObject json, EntityPredicate.Composite player, DeserializationContext con) {
		return new CharangeTrigger.Instance(this.name, player);
	}

	public static CharangeTrigger create(String name) {
		return CriteriaTriggers.register(new CharangeTrigger(name));
	}

	public void trigger(ServerPlayer player) {
		List<ResourceLocation> srcList = new ArrayList<>();
		AdvancedInit.advancedMap.values().forEach(a -> srcList.add(a.getId()));
		this.trigger(player, i -> PlayerHelper.checkClearAdvanced(player, srcList));
	}

	public static class Instance extends AbstractCriterionTriggerInstance {

		public Instance(ResourceLocation name, EntityPredicate.Composite player) {
			super(name, player);
		}
	}
}
