package sweetmagic.init.entity.monster;

import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.init.EntityInit;
import sweetmagic.init.entity.monster.boss.WindWitchMaster;

public class WitchCrystal extends AbstractSMMob {

	private UUID ownerID;
	public WitchCrystal(Level world) {
		super(EntityInit.witchCrystal, world);
	}

	public WitchCrystal(EntityType<? extends WitchCrystal> enType, Level world) {
		super(enType, world);
		this.xpReward = 1;
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Monster.createMonsterAttributes()
				.add(Attributes.MAX_HEALTH, 40D)
				.add(Attributes.MOVEMENT_SPEED, 0D)
				.add(Attributes.ATTACK_DAMAGE, 0D)
				.add(Attributes.ARMOR, 6D)
				.add(Attributes.FOLLOW_RANGE, 0D);
	}

	// ダメージ処理
	public boolean hurt(DamageSource src, float amount) {

		Entity attacker = src.getEntity();
		if ( (attacker != null && attacker instanceof ISMMob) || !this.isSMDamage(src) ) {
			this.playSound(SoundEvents.BLAZE_HURT, 2F, 0.85F);
			return false;
		}

		if (amount > 4 && this.level instanceof ServerLevel server) {

			BlockPos pos = this.blockPosition().above();

			for (int i = 0; i < 8; i++) {
				double x = pos.getX() + this.rand.nextDouble();
				double y = pos.getY() + this.rand.nextDouble() * 0.4D + 0.2D;
				double z = pos.getZ() + this.rand.nextDouble();
				server.sendParticles(ParticleTypes.ANGRY_VILLAGER, x, y, z, 5, 0F, 0F, 0F, 0.25F);
			}
		}

		return super.hurt(src, Math.min(20F, amount));
	}

	public void addAdditionalSaveData(CompoundTag tags) {
		super.addAdditionalSaveData(tags);
		if (this.getOwnerID() != null) {
			tags.putUUID("ownerID", this.getOwnerID());
		}
	}

	public void readAdditionalSaveData(CompoundTag tags) {
		super.readAdditionalSaveData(tags);
		if (tags.contains("ownerID")) {
			this.setOwnerID(tags.getUUID("ownerID"));
		}
	}

	public void tick() {
		super.tick();
		this.setDeltaMovement(new Vec3(0, 0, 0));
	}

	protected void tickDeath() {

		if (this.deathTime == 0) {

			if (this.getOwnerID() != null && this.level instanceof ServerLevel server) {
				WindWitchMaster entity = (WindWitchMaster) server.getEntity(this.getOwnerID());
				if (entity != null && entity.isAlive()) {
					entity.setArmor(entity.getArmor() - 1);
				}
			}

			this.playSound(SoundEvents.GLASS_BREAK, 3F, 1.1F);
			this.playSound(SoundEvents.GENERIC_EXPLODE, 1.5F, 1F / (this.rand.nextFloat() * 0.2F + 0.7F));

			if (this.level instanceof ServerLevel sever) {
				BlockPos pos = this.blockPosition().above();
				sever.sendParticles(ParticleTypes.EXPLOSION, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 2, 0D, 0D, 0D, 0D);
			}
		}

		super.tickDeath();
	}

	public UUID getOwnerID () {
		return this.ownerID;
	}

	public void setOwnerID (LivingEntity entity) {
		this.ownerID = entity.getUUID();
	}

	public void setOwnerID (UUID id) {
		this.ownerID = id;
	}

	public float getLightLevelDependentMagicValue() {
		return 1F;
	}
}
