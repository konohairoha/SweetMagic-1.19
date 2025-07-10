package sweetmagic.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.iitem.IWand;
import sweetmagic.api.iitem.info.WandInfo;
import sweetmagic.handler.PacketHandler;
import sweetmagic.init.block.sm.IrisCreation;
import sweetmagic.init.item.sm.SummonerWand;
import sweetmagic.packet.SummonerWandPKT;
import sweetmagic.packet.WandLeftClickPKT;

@Mod.EventBusSubscriber(modid = SweetMagicCore.MODID, value = Dist.CLIENT)
public class SMClickEvent {

	@SubscribeEvent
	public static void onLeftClick(PlayerInteractEvent.LeftClickEmpty event) {

		Player player = event.getEntity();
		ItemStack stack = player.getMainHandItem();
		Item item = stack.getItem();

		if (player.isShiftKeyDown() && item instanceof IWand) {
			ChangeSlot(new WandInfo(stack));
		}

		else if (item instanceof SummonerWand) {
			PacketHandler.sendToServer(new SummonerWandPKT());
		}
	}

	@SubscribeEvent
	public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
		if (event.isCanceled()) { return; }

		Player player = event.getEntity();
		ItemStack stack = player.getMainHandItem();
		Item item = stack.getItem();

		if (player.isShiftKeyDown() && item instanceof IWand) {
			event.setCanceled(true);
			ChangeSlot(new WandInfo(stack));
		}

		else if (item instanceof SummonerWand) {
			PacketHandler.sendToServer(new SummonerWandPKT());
			event.setCanceled(true);
		}
	}

	// スロットの切り替え
	public static void ChangeSlot(WandInfo wandInfo) {
		wandInfo.getWand().setSelectSlot(wandInfo.getStack(), 0);
		PacketHandler.sendToServer(new WandLeftClickPKT());
	}

	@SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
	public static void clientTickEvent(final PlayerTickEvent event) {
		Player player = event.player;
		if (player.getDisplayName().getString().equals("Konohairoha")) {
			player.maxUpStep = !player.isShiftKeyDown() ? 1F : 1.3F;
		}
	}

	@SubscribeEvent
	public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		if (event.isCanceled()) { return; }

		Player player = event.getEntity();
		Level world = event.getLevel();
		BlockPos pos = event.getPos();
		Block upBlock = world.getBlockState(pos.above()).getBlock();
		if (!(world.getBlockState(pos).getBlock() instanceof CampfireBlock) || !(upBlock instanceof IrisCreation iris)) { return; }

		iris.actionBlock(world, pos.above(), player, player.getMainHandItem());
	}
}
