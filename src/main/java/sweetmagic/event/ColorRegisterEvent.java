package sweetmagic.event;

import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit;

@Mod.EventBusSubscriber(modid = SweetMagicCore.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ColorRegisterEvent {

	@SubscribeEvent
	public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {

		event.register((state, get, pos, tintIndex) -> {

			int color = 0XFFFFFF;
			Block block = get.getBlockState(pos.below()).getBlock();

			if (block == BlockInit.white_stone) {
				color = 0XFFEE00;
			}

			else if (block == BlockInit.white_stone_polished) {
				color = 0XFF0015;
			}

			else if (block == BlockInit.simplestonebrick) {
				color = 0X75FF66;
			}

			else if (block == BlockInit.simplestonebrick_cracked) {
				color = 0X008CFF;
			}

			return color;
		}, BlockInit.rune_character);
	}
}
