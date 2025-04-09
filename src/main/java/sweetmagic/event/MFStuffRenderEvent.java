package sweetmagic.event;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.ItemInit;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.item.sm.SMItem;

@Mod.EventBusSubscriber(modid = SweetMagicCore.MODID, value = Dist.CLIENT)
public class MFStuffRenderEvent {

	private static final List<Direction> ALL_FACE = Arrays.<Direction> asList(Direction.UP, Direction.NORTH, Direction.EAST);

	@SubscribeEvent
	public static void renderLevelLastEvent(RenderLevelStageEvent event) {
		if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) { return; }

		Minecraft mc = Minecraft.getInstance();
		Player player = mc.player;
		if (player == null) { return; }

		ItemStack stack = player.getMainHandItem();
		if (stack.isEmpty() || !stack.is(ItemInit.mf_stuff)) { return; }

		CompoundTag tags = stack.getTag();
		if (tags == null || !tags.contains("X") || player.tickCount % 30 != tags.getInt("tick")) { return; }

		ClientLevel world = mc.level;
		BlockPos pos = new BlockPos(tags.getInt("X"), tags.getInt("Y"), tags.getInt("Z"));
		BlockState state = world.getBlockState(pos);
		VoxelShape aabb = state.getBlock().getOcclusionShape(state, world, pos);
		double range = aabb.min(Axis.X);
		range = range > 1D - aabb.max(Axis.X) ? 1D - aabb.max(Axis.X) : range;
		range = range > aabb.min(Axis.Z) ? aabb.min(Axis.Z) : range;
		range = range > 1D - aabb.max(Axis.Z) ? 1D - aabb.max(Axis.Z) : range;
		range = 0.85D - range;
		ParticleOptions par = ParticleInit.CYCLE_ORB_Y;

		for (int i = 0; i < 16; i++) {
			for (Direction face : ALL_FACE) {
				spawnParticleCycle(world, par, pos.getX() + 0.5D, pos.getY() + 0.55D, pos.getZ() + 0.5D, face, range, (i * 22.5D));
			}
		}
	}

	// パーティクルスポーンサイクル
	protected static void spawnParticleCycle (ClientLevel world, ParticleOptions par, double x, double y, double z, Direction face, double range, double angle) {
		world.addParticle(par, x, y, z, face.get3DDataValue(), range, angle + SMItem.SPEED);
	}
}
