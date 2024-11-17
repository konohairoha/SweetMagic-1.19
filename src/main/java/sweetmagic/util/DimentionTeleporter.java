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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;
import sweetmagic.init.BlockInit;
import sweetmagic.init.block.magic.SturdustCrystal;
import sweetmagic.init.block.sm.MagiaPortal;

public record DimentionTeleporter (Block portal, Block flame, Direction.Axis face, boolean isZ) implements ITeleporter {

	@Override public boolean isVanilla() { return false; }

	@Override
	public boolean playTeleportSound(ServerPlayer player, ServerLevel sourceWorld, ServerLevel destWorld) {
		player.playSound(SoundEvents.PORTAL_TRAVEL, 1F, 1F);
		return false;
	}

    @Nullable
    @Override
    public PortalInfo getPortalInfo(Entity entity, ServerLevel world, Function<ServerLevel, PortalInfo> info) {
		if(!(entity instanceof ServerPlayer player) || ((ServerPlayer) entity).isOnPortalCooldown()) { return null; }

		player.setPortalCooldown();
		ChunkPos chunk = new ChunkPos(entity.blockPosition());
		int chunkX = chunk.x, chunkZ = chunk.z;

		for(int currentX = chunkX - 4; currentX < chunkX + 4; currentX++) for(int currentZ = chunkZ - 4; currentZ < chunkZ + 4; currentZ++) {

			MutableBlockPos mut = new MutableBlockPos();
			int baseX = chunkX * 16, baseZ = chunkZ * 16;

			for(int y = 0; y < 256; y++) {
				for(int x = -8; x < 16; x++) for(int z = -8; z < 16; z++) {

					mut.set(baseX + x, y, baseZ + z);
					BlockState targetState = world.getBlockState(mut);
					if(targetState.getBlock() != this.portal) { continue; }

					float addY = targetState.getValue(MagiaPortal.AXIS) == Direction.Axis.Y ? 1.5F : 0F;
					return new PortalInfo(new Vec3(mut.getX() + (entity.getX() % 1), mut.getY() - (this.isBlockPortal(world, mut.getX(), mut.getY() - 1, mut.getZ()) ? 1 : 0) + addY, mut.getZ() + (entity.getZ() % 1)), Vec3.ZERO, entity.getYRot(), entity.getXRot());
				}
			}
		}

		if(player.getRespawnPosition() != null && player.getRespawnDimension() == world.dimension()) {
			return new PortalInfo(new Vec3(player.getRespawnPosition().getX(), player.getRespawnPosition().getY(), player.getRespawnPosition().getZ()), Vec3.ZERO, player.getRespawnAngle(), player.getRespawnAngle());
		}

		BlockPos entityPos = entity.blockPosition();
		MutableBlockPos pos = new BlockPos(entityPos.getX(), world.getHeight() - 8, entityPos.getZ()).mutable();
		while(world.getBlockState(pos).isAir()) { pos.move(Direction.DOWN); }

		pos.move(Direction.UP);

		for (int addX = -3; addX < 3; addX++) {
			for (int addY = -1; addY < 5; addY++) {
				for (int addZ = -2; addZ < 3; addZ++) {

					BlockPos targetPos = this.isZ ? pos.offset(addX, addY, addZ) : pos.offset(addZ, addY, addX);
					BlockState state = world.getBlockState(targetPos);
					Block block = state.getBlock();
					if (block == BlockInit.sturdust_crystal) { continue; }

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

	public boolean isBlockPortal(ServerLevel var1, int var2, int var3, int var4) {
		return var1.getBlockState(new BlockPos(var2, var3, var4)).getBlock() == this.portal;
	}
}
