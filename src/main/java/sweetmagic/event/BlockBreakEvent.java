package sweetmagic.event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.event.level.BlockEvent.BreakEvent;
import net.minecraftforge.event.level.BlockEvent.EntityPlaceEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import sweetmagic.init.BlockInit;
import sweetmagic.init.ItemInit;
import sweetmagic.init.PotionInit;
import sweetmagic.init.block.magic.MagicianLectern;
import sweetmagic.init.tile.sm.TileAbstractMagicianLectern;
import sweetmagic.init.tile.sm.TileAbstractMagicianLectern.SummonType;

public class BlockBreakEvent {

	private static final List<Player> playerList = new ArrayList<>();
	private static final List<Player> playerPlaceList = new ArrayList<>();

	private static final List<Material> materialList = Arrays.<Material> asList(
		Material.STONE, Material.METAL
	);

	private static final List<Material> requiredList = Arrays.<Material> asList(
		Material.GLASS
	);

	// ブロックを破壊したときのイベント
	@SubscribeEvent
	public static void onBlockBreakEvent (BreakEvent event) {
		Player player = event.getPlayer();
		LevelAccessor world = event.getLevel();
		if (player == null || world.isClientSide()) { return; }

		BlockState state = event.getState();
		Block block = state.getBlock();

		if (!player.isCreative() && player.hasEffect(PotionInit.non_destructive) && block != Blocks.IRON_BARS && block != BlockInit.smspawner) {

			float time = block.defaultDestroyTime();
			Material mate = state.getMaterial();

			if ( ( time >= 1F && materialList.contains(mate) ) || requiredList.contains(mate)) {

				if (!player.level.isClientSide && !playerList.contains(player)) {
					player.sendSystemMessage( Component.translatable("tip.sweetmagic.non_destructive").withStyle(ChatFormatting.GREEN));
					playerList.add(player);
				}

				event.setCanceled(true);
				return;
			}
		}
		
		else if (player.getMainHandItem().is(ItemInit.startlight_wand)) {
			event.setCanceled(true);
			return;
		}

		if ( !(block instanceof MagicianLectern) ) { return; }

		BlockPos pos = event.getPos();
		TileAbstractMagicianLectern tile = (TileAbstractMagicianLectern) world.getBlockEntity(pos);
		if (tile.summonType.is(SummonType.START) || tile.summonType.is(SummonType.END)) { return; }

		event.setCanceled(true);
	}

	// ブロックを破壊したときのイベント
	@SubscribeEvent
	public static void onBlockPlaceEvent (EntityPlaceEvent event) {
		Entity entity = event.getEntity();
		if ( !(entity instanceof Player player) || player.isCreative() || !player.hasEffect(PotionInit.non_destructive)) { return; }

		Block block = event.getPlacedBlock().getBlock();
		if (block == Blocks.AIR || block instanceof EntityBlock || block.defaultDestroyTime() < 1F) { return; }

		if (!player.level.isClientSide && !playerPlaceList.contains(player)) {
			player.sendSystemMessage( Component.translatable("tip.sweetmagic.non_destructive_place").withStyle(ChatFormatting.GREEN));
			playerPlaceList.add(player);
		}

		event.setCanceled(true);
	}
}
