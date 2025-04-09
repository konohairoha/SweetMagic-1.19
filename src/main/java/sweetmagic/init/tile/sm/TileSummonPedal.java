package sweetmagic.init.tile.sm;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import sweetmagic.api.util.ISMTip;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.TileInit;
import sweetmagic.init.block.sm.BossFigurine;
import sweetmagic.init.entity.monster.boss.AbstractSMBoss;
import sweetmagic.init.item.sm.SMItem;

public class TileSummonPedal extends TileAbstractSM implements ISMTip {

	private int summonTick = 0;
	private boolean isWarning = false;
	private static final int MAX_SUMMONTICK = 20;
	private static final AABB FULL = Shapes.block().bounds();
	private static final List<Direction> ALL_FACE = Arrays.<Direction> asList(Direction.UP, Direction.NORTH, Direction.EAST);

	public TileSummonPedal(BlockPos pos, BlockState state) {
		super(TileInit.summonPedal, pos, state);
	}

	public TileSummonPedal(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	// サーバー側処理
	public void serverTick(Level world, BlockPos pos, BlockState state) {
		super.serverTick(world, pos, state);
		if (this.tickTime % 10 != 0) { return; }

		pos = pos.above();
		Block block = this.getBlock(pos);
		if (block instanceof BossFigurine) {

			boolean isBad = false;
			Iterable<BlockPos> posList = this.getRangePosUnder(pos, 5);

			// リスト分まわす
			for (BlockPos p : posList) {
				if(world.getBlockState(p).isAir()) { continue; }

				AABB aabb = this.getState(p).getCollisionShape(world, p).bounds();
				if(!FULL.equals(aabb)) { continue; }

				isBad = true;
				ParticleOptions par = ParticleInit.CYCLE_ORB;
				if(this.tickTime % 30 != 0 || !(world instanceof ServerLevel server)) { continue; }

				double range = aabb.min(Axis.X);
				range = range > 1D - aabb.max(Axis.X) ? 1D - aabb.max(Axis.X) : range;
				range = range > aabb.min(Axis.Z) ? aabb.min(Axis.Z) : range;
				range = range > 1D - aabb.max(Axis.Z) ? 1D - aabb.max(Axis.Z) : range;
				range = 0.85D - range;

				for (int i = 0; i < 16; i++) {
					for (Direction face : ALL_FACE) {
						this.spawnParticleCycle(server, p,  par, face, range, (i * 22.5D));
					}
				}
			}

			if (isBad) {
				this.sendPlayerMSG(world);
				return;
			}

			if (this.summonTick++ >= MAX_SUMMONTICK) {
				this.summonBoss((TileBossFigurine) this.getTile(pos), world, pos);
				this.isWarning = false;
			}

			else {
				this.spawnParticle(world, pos);
			}
		}

		else {
			this.summonTick = 0;
			this.isWarning = false;
		}
	}

	public void sendPlayerMSG(Level world) {
		if(this.isWarning) { return; }
		List<Player> playerList = this.getEntityList(Player.class, e -> e.isAlive() && !e.isSpectator(), 24D);
		playerList.forEach(p -> p.sendSystemMessage(this.getText("noempty_block", "" + 5).withStyle(RED)));
		this.isWarning = true;
	}

	public void summonBoss(TileBossFigurine tile, Level world, BlockPos pos) {
		AbstractSMBoss entity = (AbstractSMBoss) tile.getEntity();
		entity.setPos(pos.getX() + 2.5D, pos.getY() + 2D, pos.getZ() + 0.5D);
		entity.startInfo();
		world.addFreshEntity(entity);
		entity.spawnAnim();
		world.destroyBlock(pos, false);
		world.removeBlock(pos, false);
		entity.startInfo();
		this.summonTick = 0;
	}

	public void spawnParticle(Level world, BlockPos pos) {
		if (!(world instanceof ServerLevel server)) { return; }

		ParticleOptions par = ParticleInit.NORMAL;

		for (int i = 0; i < this.summonTick; i++) {
			float randX = this.getRand(this.rand, 4);
			float randY = this.getRand(this.rand, 4);
			float randZ = this.getRand(this.rand, 4);
			float x = pos.getX() + 0.5F + randX;
			float y = pos.getY() + 0.75F + randY;
			float z = pos.getZ() + 0.5F + randZ;
			float xSpeed = -randX * 0.115F;
			float ySpeed = -randY * 0.115F;
			float zSpeed = -randZ * 0.115F;
			server.sendParticles(par, x, y, z, 0, xSpeed, ySpeed, zSpeed, 1F);
		}
	}

	public void spawnParticleCycle(ServerLevel server, BlockPos pos, ParticleOptions par, Direction face, double range, double angle) {
		float x = pos.getX() + 0.5F;
		float y = pos.getY() + 0.5F;
		float z = pos.getZ() + 0.5F;
		server.sendParticles(par, x, y, z, 0, face.get3DDataValue(), range, angle + SMItem.SPEED, 1F);
	}

	protected int getRand(Random rand, int range) {
		return rand.nextInt(range) - rand.nextInt(range);
	}
}
