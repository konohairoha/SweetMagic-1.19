package sweetmagic.plugin.jade;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class SMJadePlugin implements IWailaPlugin {

	@Override
	public void registerClient(IWailaClientRegistration registr) {
		registr.registerBlockComponent(JadeDataProvider.INSTANCE, Block.class);
		registr.registerEntityComponent(JadeEntityProvider.INSTANCE, Entity.class);
	}
}
