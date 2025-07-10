package sweetmagic.plugin.jade;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.util.ISMTip;
import sweetmagic.init.entity.animal.AbstractSummonMob;

public enum JadeEntityProvider implements IEntityComponentProvider, ISMTip {

	INSTANCE;

	@Override
	public void appendTooltip(ITooltip tooltip, EntityAccessor acce, IPluginConfig config) {
		if (!(acce.getEntity() instanceof AbstractSummonMob mob)) { return; }
		int time = mob.getMaxLifeTime() - mob.tickCount;
		tooltip.add(Component.translatable("jade.sweetmagic.summontime", this.format(time / 20)).withStyle(GREEN));
	}

	@Override
	public ResourceLocation getUid() {
		return SweetMagicCore.getSRC("summon");
	}
}
