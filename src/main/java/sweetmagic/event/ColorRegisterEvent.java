package sweetmagic.event;

import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.iblock.ISMCrop;
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

		event.register((state, get, pos, tintIndex) -> {
			int red, green, blue;

			int fade = 0;
			int value = state.getValue(ISMCrop.AGE5);

			switch(value) {
			case 0:
				fade = pos.getX() * 64 + pos.getY() * 64 + pos.getZ() * 64;
				break;
			default:
				fade = (value - 1) * 64;
				break;
			}

			if ((fade & 256) != 0) {
				fade = 255 - (fade & 255);
			}

			fade &= 255;
			float spring = (255 - fade) / 255F;
			float fall = fade / 255F;

			red = (int) (spring * 106 + fall * 251);
			green = (int) (spring * 156 + fall * 108);
			blue = (int) (spring * 23 + fall * 27);

			return 0xFF000000 | red << 16 | green << 8 | blue;
		}, BlockInit.maple_leaves);
	}
}
