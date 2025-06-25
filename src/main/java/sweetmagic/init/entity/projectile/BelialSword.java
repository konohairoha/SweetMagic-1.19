package sweetmagic.init.entity.projectile;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import sweetmagic.api.emagic.SMElement;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.init.EntityInit;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.entity.monster.boss.DemonsBelial;

public class BelialSword extends AbstractMagicShot {

	private float health = 256F;
	private float maxHealth = 256F;
	private static final EntityDataAccessor<Boolean> CHARGE = setEntityData(ISMMob.BOOLEAN);

	public BelialSword(EntityType<? extends AbstractMagicShot> entityType, Level world) {
		super(entityType, world);
	}

	public BelialSword(double x, double y, double z, Level world) {
		this(EntityInit.belialSword, world);
		this.setPos(x, y, z);
	}

	public BelialSword(Level world, LivingEntity entity) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.stack = ItemStack.EMPTY;
		this.maxHealth = this.health = entity.getHealth() * 0.25F;
		this.setRange(7.5F);
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.define(CHARGE, false);
	}

	public void setCharge(boolean isCharge) {
		this.set(CHARGE, isCharge);
	}

	public boolean getCharge() {
		return this.get(CHARGE);
	}

	public void addAdditionalSaveData(CompoundTag tags) {
		super.addAdditionalSaveData(tags);
		tags.putBoolean("isCharge", this.getCharge());
		tags.putFloat("health", this.health);
		tags.putFloat("maxHealth", this.maxHealth);
	}

	public void readAdditionalSaveData(CompoundTag tags) {
		super.readAdditionalSaveData(tags);
		this.setCharge(tags.getBoolean("isCharge"));
		this.health = tags.getFloat("health");
		this.maxHealth = tags.getFloat("maxHealth");
	}

	public void tick() {
		this.checkOwner();
		if (this.getCharge()) { return; }
		super.tick();
	}

	protected void tickDespawn() {}

	// ブロック着弾
	protected void onHitBlock(BlockHitResult result) {
		this.rangeAttack(null, this.getRange());
		super.onHitBlock(result);
	}

	public void rangeAttack(LivingEntity living, double range) {

		float damage = this.getDamage();
		List<LivingEntity> entityList = this.getEntityList(LivingEntity.class, this.isTarget(), range);
		entityList.forEach(e -> this.attackDamage(e, damage, false));

		if (!(this.getLevel() instanceof ServerLevel sever)) { return; }

		double effectRange = range * range;
		Iterable<BlockPos> pList = this.getPosRangeList(this.blockPosition(), range);

		for (BlockPos pos : pList) {
			if(!this.checkDistance(pos, effectRange)) { continue; }

			double x = pos.getX() + this.rand.nextDouble() * 1.5D - 0.75D;
			double y = pos.getY() + this.rand.nextDouble() * 0.25D + 0.25D;
			double z = pos.getZ() + this.rand.nextDouble() * 1.5D - 0.75D;
			float xSpeed = this.getRandFloat(0.5F);
			float ySpeed = 0.25F + rand.nextFloat() * 0.25F;
			float zSpeed = this.getRandFloat(0.5F);
			sever.sendParticles(ParticleInit.BELIAL_FLAME, x, y, z, 0, xSpeed, ySpeed, zSpeed, 0.25F);
		}
	}

	public boolean isPickable() {
		return true;
	}

	public boolean hurt(DamageSource src, float amount) {
		if (this.isClient()) { return true; }

		this.health -= Math.min(30F, amount);

		if(this.health <= 0F && this.maxHealth > 0F) {
			this.playSound(SoundEvents.ITEM_BREAK, 2F, 1F);

			if(this.getOwner() instanceof DemonsBelial entity) {
				this.teleportParticle(ParticleInit.BELIAL_FLAME, this.blockPosition(), entity.blockPosition(), 16);
				this.teleportParticle(ParticleInit.CYCLONE, this.blockPosition(), entity.blockPosition(), 1);
				entity.setHealth(entity.getHealth() - this.maxHealth * 1.5F);
				this.maxHealth = 0F;
			}

			this.discard();
		}

		else {
			this.playSound(SoundEvents.ARMOR_STAND_HIT, 2F, 1F);
		}

		return true;
	}

	public void teleportParticle(ParticleOptions par, BlockPos beforePos, BlockPos afterPos, int value) {
		if (!(this.getLevel() instanceof ServerLevel sever)) { return; }

		float pX = afterPos.getX() - beforePos.getX();
		float pY = afterPos.getY() - beforePos.getY();
		float pZ = afterPos.getZ() - beforePos.getZ();
		int count = Math.abs((int) (pX + pZ));

		for (int i = 0; i < count; i++) {
			for (int k = 0; k < value; k++) {

				float randX = this.getRandFloat(1.5F);
				float randY = this.getRandFloat(1.5F);
				float randZ = this.getRandFloat(1.5F);
				float ax = beforePos.getX() + 0.5F + randX+ pX * (i / (float) count);
				float ay = beforePos.getY() + 1.25F + randY + pY * (i / (float) count);
				float az = beforePos.getZ() + 0.5F + randZ + pZ * (i / (float) count);

				sever.sendParticles(par, ax, ay, az, 0, 0F, 0F, 0F, 1F);
			}
		}
	}

	// 属性の取得
	public SMElement getElement() {
		return SMElement.TOXIC;
	}
}
