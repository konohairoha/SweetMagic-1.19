package sweetmagic.init.entity.projectile;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import sweetmagic.api.emagic.SMElement;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.api.iitem.info.WandInfo;
import sweetmagic.init.EntityInit;
import sweetmagic.init.PotionInit;

public class EnderBall extends AbstractMagicShot {

	public boolean isTeleport = true;

	public EnderBall(EntityType<? extends EnderBall> entityType, Level world) {
		super(entityType, world);
	}

	public EnderBall(double x, double y, double z, Level world) {
		this(EntityInit.enderBall, world);
		this.setPos(x, y, z);
	}

	public EnderBall(Level world, LivingEntity entity, WandInfo wandInfo) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.stack = wandInfo.getStack();
	}

	public EnderBall(Level world, LivingEntity entity) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.stack = ItemStack.EMPTY;
	}

	protected void onHitBlock(BlockHitResult result) {
		if (this.getOwner() instanceof Player player) {

			BlockPos pos = result.getBlockPos().relative(result.getDirection());

			if (this.checkBlock(pos)) {
				this.teleportPos(player, player.blockPosition(), pos);
			}
		}

		super.onHitBlock(result);
	}

	// えんちちーに当たった時の処理
	protected void entityHit(LivingEntity target) {

		// 敵モブでの処理
		if(target instanceof ISMMob) {

			if (target instanceof ISMMob || this.isClient() || !this.isTeleport) { return; }

			Random rand = this.rand;

			for (int i = 0; i < 16; ++i) {

				double d3 = this.getX() + (rand.nextDouble() - 0.5D) * 16D;
				double d4 = this.getY() + 0.5D;
				double d5 = this.getZ() + (rand.nextDouble() - 0.5D) * 16D;
				if (target.isPassenger()) {
					target.stopRiding();
				}

				Vec3 vec3 = this.position();
				this.getLevel().gameEvent(GameEvent.TELEPORT, vec3, GameEvent.Context.of(target));
				EntityTeleportEvent.ChorusFruit event = ForgeEventFactory.onChorusFruitTeleport(target, d3, d4, d5);
				if (event.isCanceled()) { return; }

				if (target.randomTeleport(event.getTargetX(), event.getTargetY(), event.getTargetZ(), true)) {
					this.playSound(SoundEvents.CHORUS_FRUIT_TELEPORT, 1F, 1F);
					break;
				}
			}
		}

		// プレイヤーでの処理
		else {
			Entity owner = this.getOwner();
			if(!(target instanceof LivingEntity) || owner == null) { return; }

			BlockPos targetPos = target.blockPosition();
			BlockPos ownerPos = owner.blockPosition();

			this.teleportPos(target, targetPos, ownerPos);
			this.teleportPos(owner, ownerPos, targetPos);
		}
	}

	public void teleportPos(Entity entity, BlockPos basePos, BlockPos targetPos) {
		entity.teleportTo(targetPos.getX(), targetPos.getY(), targetPos.getZ());

		if(this.getLevel().isEmptyBlock(targetPos.below()) && entity instanceof LivingEntity living) {
			this.addPotion(living, PotionInit.resistance_blow, 70, 0);
		}

		this.playSound(SoundEvents.CHORUS_FRUIT_TELEPORT, 1F, 1F);

		if(entity instanceof Player player && !player.isCreative()) {
			player.causeFoodExhaustion((float) Math.sqrt(basePos.distSqr(targetPos) * 0.02F));
		}
	}

	public boolean checkBlock(BlockPos pos) {
		Level world = this.getLevel();
		if(this.checkAABB(world, pos)) { return true; }
		return this.checkAABB(world, pos.below()) || this.checkAABB(world, pos.above());
	}

	public boolean checkAABB(Level world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);
		BlockState underState = world.getBlockState(pos.below());
		VoxelShape aabb = state.getBlock().getBlockSupportShape(state, world, pos);
		VoxelShape underAABB = underState.getBlock().getBlockSupportShape(underState, world, pos.below());
		return aabb.equals(Shapes.empty()) && underAABB.equals(Shapes.empty());
	}

	// パーティクルスポーン
	protected void spawnParticle() {

		Vec3 vec = this.getDeltaMovement();
		float x = (float) (-vec.x / 80F);
		float y = (float) (-vec.y / 80F);
		float z = (float) (-vec.z / 80F);
		Random rand = this.rand;

		for (int i = 0; i < 6; i++) {
			float f1 = (float) (this.getX() - 0.5F + rand.nextFloat() + vec.x * i / 4F);
			float f2 = (float) (this.getY() - 0.25F + rand.nextFloat() * 0.5 + vec.y * i / 4F);
			float f3 = (float) (this.getZ() - 0.5F + rand.nextFloat() + vec.z * i / 4F);
			this.addParticle(ParticleTypes.PORTAL, f1, f2, f3, x, y, z);
		}
	}

	// 属性の取得
	public SMElement getElement() {
		return SMElement.TIME;
	}
}
