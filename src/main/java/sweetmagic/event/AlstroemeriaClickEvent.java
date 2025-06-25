package sweetmagic.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import sweetmagic.init.block.crop.Alstroemeria;
import sweetmagic.init.block.magic.PedalCreate;

public class AlstroemeriaClickEvent {

	@SubscribeEvent
	public static void rightClickBlock(RightClickBlock event) {
		ItemStack stack = event.getItemStack();
		if (stack.isEmpty()) { return; }

		Player player = event.getEntity();
		if (!player.isShiftKeyDown()) { return; }

		Level world = event.getLevel();
		BlockPos pos = event.getPos();
		BlockState state = world.getBlockState(pos);
		Block block = state.getBlock();

		if (block instanceof Alstroemeria als) {
			// 最大成長時にアルストロメリアクラフトの実行
			if (als.isMaxAge(state)) {

				if(!world.isClientSide()) {
					als.getRecipeAlstroemeria(world, pos, player, stack, true);
				}

				event.setCanceled(true);
			}
		}

		else if (block instanceof PedalCreate pedal) {

			if(!world.isClientSide()) {
				pedal.pedalCraft(world, pos, player, stack, true);
			}

			event.setCanceled(true);
		}
	}
}
