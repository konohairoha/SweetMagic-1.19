package sweetmagic.plugin.jade;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.util.ISMTip;
import sweetmagic.init.entity.animal.AbstractSummonMob;

public class JadeEntityProvider implements IEntityComponentProvider, IServerDataProvider<Entity>, ISMTip {

	public static final ResourceLocation PROVIDER = SweetMagicCore.getSRC("summon");
	static final JadeEntityProvider INSTANCE = new JadeEntityProvider();

	@Override
	public void appendTooltip(ITooltip tooltip, EntityAccessor acce, IPluginConfig config) {
		if (!acce.getServerData().contains("SummonTime")) { return; }
		int time = acce.getServerData().getInt("SummonTime");
		tooltip.add(Component.translatable("jade.sweetmagic.summontime", this.format(time / 20)));
	}

	@Override
	public void appendServerData(CompoundTag tag, ServerPlayer player, Level world, Entity entity, boolean showDetails) {
		if(!(entity instanceof AbstractSummonMob mob)) { return; }
		int time = mob.getMaxLifeTime() - mob.tickCount;
		tag.putInt("SummonTime", time);
	}

	@Override
	public ResourceLocation getUid() {
		return PROVIDER;
	}
}
