package sweetmagic.init.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.init.EntityInit;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.entity.monster.boss.Arlaune;
import sweetmagic.init.entity.projectile.AbstractMagicShot;

public class CherryPlant extends AbstractOwnerMob {

	private static final EntityDataAccessor<Integer> STAGE = ISMMob.setData(CherryPlant.class, INT);

	public CherryPlant(Level world) {
		super(EntityInit.cherryPlant, world);
	}

	public CherryPlant(EntityType<? extends AbstractSMMob> enType, Level world) {
		super(enType, world);
		this.xpReward = 1;
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Monster.createMonsterAttributes()
				.add(Attributes.MAX_HEALTH, 50D)
				.add(Attributes.MOVEMENT_SPEED, 0D)
				.add(Attributes.ATTACK_DAMAGE, 0D)
				.add(Attributes.ARMOR, 6D)
				.add(Attributes.KNOCKBACK_RESISTANCE, 40D)
				.add(Attributes.FOLLOW_RANGE, 0D);
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(STAGE, 0);
	}

	public int getStage () {
		return this.entityData.get(STAGE);
	}

	public void setStage (int stage) {
		this.entityData.set(STAGE, stage);
	}

	public void addAdditionalSaveData(CompoundTag tags) {
		super.addAdditionalSaveData(tags);
		tags.putInt("cherry", this.getStage());
	}

	public void readAdditionalSaveData(CompoundTag tags) {
		super.readAdditionalSaveData(tags);
		this.setStage(tags.getInt("cherry"));
	}

	// ダメージ処理
	public boolean hurt(DamageSource src, float amount) {

		Entity attacker = src.getEntity();
		if ( (attacker != null && attacker instanceof ISMMob) || !this.isSMDamage(src) ) {
			this.playSound(SoundEvents.BLAZE_HURT, 1F, 0.85F);
			return false;
		}

		if (amount > 4 && this.level instanceof ServerLevel server) {

			for (int i = 0; i < 8; i++) {
				double x = this.getX() + this.rand.nextDouble();
				double y = this.getY() + this.rand.nextDouble() * 0.4D + 0.2D;
				double z = this.getZ() + this.rand.nextDouble();
				server.sendParticles(ParticleInit.CHERRY_BLOSSOMS_LARGE, x, y, z, 2, 0F, 0F, 0F, 0.1F);
			}
		}

		Entity attackEntity = src.getDirectEntity();
		if ( attackEntity != null && attackEntity instanceof AbstractMagicShot magic && magic.getRange() > 0D) {
			amount *= 0.5F;
		}

		return super.hurt(src, Math.min(10F, amount));
	}

	public void tick() {
		super.tick();

		// 移動の無効化
		this.setDeltaMovement(new Vec3(0, -4D, 0));

		if (this.tickCount % 300 == 0) {

			if (this.getStage() <= 2) {

				this.setStage(this.getStage() + 1);
				this.heal(10F);
				this.playSound(SoundEvents.GRASS_PLACE, 1F, 0.8F + rand.nextFloat() * 0.4F);

				if (this.level instanceof ServerLevel server) {
					BlockPos pos = this.blockPosition().above();

					for (int i = 0; i < 8; i++) {
						double x = pos.getX() + this.rand.nextDouble() - 0.5D;
						double y = pos.getY() + this.rand.nextDouble() * 0.4D + 0.2D;
						double z = pos.getZ() + this.rand.nextDouble() - 0.5D;
						server.sendParticles(ParticleTypes.HAPPY_VILLAGER, x, y, z, 3, 0F, 0F, 0F, 0.075F);
					}
				}

				if (this.getStage() >= 3 && this.getOwnerID() != null) {
					Arlaune entity = (Arlaune) this.getEntity();
					if (entity != null && entity.isAlive()) {
						entity.setPlant(entity.getPlant() + 1);
					}
				}
			}

			else {
				this.setHealth(0F);
			}
		}

		if (this.tickCount % 30 == 0 && this.getOwnerID() != null) {
			Arlaune entity = (Arlaune) this.getEntity();
			if (entity == null || !entity.isAlive()) {
				this.setHealth(0F);
			}
		}
	}

	protected void tickDeath() {

		if (this.deathTime == 0 && this.level instanceof ServerLevel server) {
			BlockPos pos = this.blockPosition().above();
			ParticleOptions par = ParticleInit.CHERRY_BLOSSOMS_LARGE;

			for (int i = 0; i < 64; i++) {
				double x = pos.getX() + this.rand.nextDouble() - 0D;
				double y = pos.getY() + this.rand.nextDouble() * 0.4D + 0.2D;
				double z = pos.getZ() + this.rand.nextDouble() - 0D;
				server.sendParticles(par, x, y, z, 2, 0F, 0F, 0F, 0.075F);
			}
		}

		super.tickDeath();
	}
}
