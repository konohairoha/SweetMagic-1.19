package sweetmagic.util;

import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;
import sweetmagic.init.BlockInit;
import sweetmagic.init.block.magic.SturdustCrystal;
import sweetmagic.init.block.sm.MagiaPortal;

public record DimentionTeleporter (Block portal, Block flame, Direction.Axis face, boolean isZ) implements ITeleporter {

	private static final Direction[] FACE_ARRAY = { Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST };

	@Override public boolean isVanilla() { return false; }

	@Override
	public boolean playTeleportSound(ServerPlayer player, ServerLevel sourceWorld, ServerLevel destWorld) {
		destWorld.playSound(player, player.blockPosition(), SoundEvents.PORTAL_TRAVEL, SoundSource.BLOCKS, 0.25F, 1F);
		return false;
	}

	@Nullable
	@Override
	public PortalInfo getPortalInfo(Entity entity, ServerLevel world, Function<ServerLevel, PortalInfo> info) {
		if(!(entity instanceof LivingEntity living)) { return null; }

		living.setPortalCooldown();
		ChunkPos chunk = new ChunkPos(entity.blockPosition());
		int chunkX = chunk.x, chunkZ = chunk.z;

		for(int currentX = chunkX - 2; currentX < chunkX + 2; currentX++) for(int currentZ = chunkZ - 2; currentZ < chunkZ + 2; currentZ++) {

			MutableBlockPos mut = new MutableBlockPos();
			int baseX = currentX * 16, baseZ = currentZ * 16;

			for(int y = 0; y < 256; y++) {
				for(int x = 0; x < 16; x++) for(int z = 0; z < 16; z++) {
					MutableBlockPos mut2 = mut.set(baseX + x, y, baseZ + z);
					if(world.isEmptyBlock(mut2) || !world.getBlockState(mut2).is(this.portal) || !this.isCenter(world, mut2)) { continue; }

					BlockState state = world.getBlockState(mut2);
					float addY = state.hasProperty(MagiaPortal.AXIS) && state.getValue(MagiaPortal.AXIS) == Direction.Axis.Y ? 1.5F : 0F;
					return new PortalInfo(new Vec3(mut2.getX() + (entity.getX() % 1), mut2.getY() - (this.isBlockPortal(world, mut2.getX(), mut2.getY() - 1, mut2.getZ()) ? 1 : 0) + addY, mut2.getZ() + (entity.getZ() % 1)), Vec3.ZERO, entity.getYRot(), entity.getXRot());
				}
			}
		}

		if((entity instanceof ServerPlayer player)) {
			BlockPos resPawnpos = player.getRespawnPosition();
			if(resPawnpos != null && player.getRespawnDimension() == world.dimension()) {
				return new PortalInfo(new Vec3(resPawnpos.getX(), resPawnpos.getY(), resPawnpos.getZ()), Vec3.ZERO, player.getRespawnAngle(), player.getRespawnAngle());
			}
		}

		BlockPos entityPos = entity.blockPosition();
		MutableBlockPos pos = new BlockPos(entityPos.getX(), world.getHeight() - 8, entityPos.getZ()).mutable();
		while(world.isEmptyBlock(pos)) { pos.move(Direction.DOWN); }

		pos.move(Direction.UP);

		for (int addX = -3; addX < 3; addX++) {
			for (int addY = -1; addY < 5; addY++) {
				for (int addZ = -2; addZ < 3; addZ++) {

					BlockPos targetPos = this.isZ ? pos.offset(addX, addY, addZ) : pos.offset(addZ, addY, addX);
					if (world.getBlockState(targetPos).is(BlockInit.sturdust_crystal)) { continue; }

					world.destroyBlock(pos, false);
					world.removeBlock(pos, false);
				}
			}
		}

		Map<BlockPos, BlockState> posMap = this.isZ ? SturdustCrystal.getZPosMap(pos) : SturdustCrystal.getXPosMap(pos);
		posMap.forEach((key, val) -> world.setBlock(key, val, 3));
		pos = pos.move(Direction.WEST);

		if (this.face == Direction.Axis.Y) {
			pos.above(2);
		}

		return new PortalInfo(new Vec3(pos.getX(), pos.getY(), pos.getZ()), Vec3.ZERO, entity.getYRot(), entity.getXRot());
	}

	public boolean isBlockPortal(ServerLevel world, int x, int y, int z) {
		return world.getBlockState(new BlockPos(x, y, z)).getBlock() == this.portal;
	}

	public boolean isCenter(Level world, MutableBlockPos pos) {
		return (world.getBlockState(pos.relative(FACE_ARRAY[0])).is(this.portal) && world.getBlockState(pos.relative(FACE_ARRAY[1])).is(this.portal)) ||
				(world.getBlockState(pos.relative(FACE_ARRAY[2])).is(this.portal) && world.getBlockState(pos.relative(FACE_ARRAY[3])).is(this.portal));
	}
}
