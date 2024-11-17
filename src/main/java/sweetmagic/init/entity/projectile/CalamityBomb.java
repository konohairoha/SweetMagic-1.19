package sweetmagic.init.entity.projectile;

import java.util.List;
import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import sweetmagic.api.emagic.SMElement;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.api.iitem.info.WandInfo;
import sweetmagic.init.EntityInit;
import sweetmagic.init.PotionInit;
import sweetmagic.util.SMDamage;

public class CalamityBomb extends AbstractMagicShot {

	private static final EntityDataAccessor<Integer> COUNT = SynchedEntityData.defineId(CalamityBomb.class, EntityDataSerializers.INT);

	public CalamityBomb(EntityType<? extends CalamityBomb> entityType, Level world) {
		super(entityType, world);
	}

	public CalamityBomb(double x, double y, double z, Level world) {
		this(EntityInit.calamityBomb, world);
		this.setPos(x, y, z);
	}

	public CalamityBomb(Level world, LivingEntity entity, WandInfo wandInfo) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.setWandInfo(wandInfo);
		this.setCount(2);
		this.setMaxLifeTime(120);
	}

	public CalamityBomb(Level world, LivingEntity entity, ItemStack stack) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.stack = stack;
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(COUNT, 0);
	}

	public void setCount(int count) {
		this.entityData.set(COUNT, count);
	}

	public int getCount() {
		return this.entityData.get(COUNT);
	}

	public void tick() {
		super.tick();

		if (!this.inGround) {
			Vec3 vec = this.getDeltaMovement();
			float down = (float) (this.getCount() == 2 ? 0.075F : 0.015F);
			this.setDeltaMovement(new Vec3(vec.x, vec.y - down, vec.z));
		}
	}

	// ブロック着弾
	protected void onHitBlock(BlockHitResult result) {

		if (this.level.isClientSide) {
			this.setHitDead(false);
		}

		this.setLifeTime(0);

		if (this.getCount() > 0) {
			this.setCount(this.getCount() - 1);
			Vec3 look = this.getDeltaMovement();
			Vec3 dest = this.getCount() == 1 ? new Vec3(look.x * 0.25F, 0.1F, look.z * 0.25F) : new Vec3(look.x, 0.1F, look.z);
			this.setDeltaMovement(dest);
			this.playSound(SoundEvents.GENERIC_EXPLODE, 2F, 1F);

			if (this.level instanceof ServerLevel sever) {
				BlockPos pos = this.blockPosition().above();
				float x = (float) (pos.getX() + 0.5F);
				float y = (float) (pos.getY() - 0.5F);
				float z = (float) (pos.getZ() + 0.5F);
				sever.sendParticles(ParticleTypes.EXPLOSION, x, y, z, 4, 0F, 0F, 0F, 0.15F);
			}

			float rate = 0.33F + (1 - this.getCount()) * 0.34F;
			this.createExplo(this.getDamage() * rate, (float) (this.getRange() * rate));
		}

		else {

			if (this.level instanceof ServerLevel sever) {
				BlockPos pos = this.blockPosition().above();
				float x = (float) (pos.getX() + 0.5F);
				float y = (float) (pos.getY() - 0.5F);
				float z = (float) (pos.getZ() + 0.5F);
				sever.sendParticles(ParticleTypes.EXPLOSION_EMITTER, x, y, z, 4, 0F, 0F, 0F, 0.15F);
			}

			this.createExplo(this.getDamage(), (float) (this.getRange() * 2F));
			this.playSound(SoundEvents.GENERIC_EXPLODE, 3F, 1F);
			this.discard();
		}
	}

	public void createExplo (float explo, float range) {

		double effectRange = range * range;
		List<LivingEntity> entityList = this.getEntityList(LivingEntity.class, e -> !(e instanceof ISMMob), explo);
		if (entityList.isEmpty()) { return; }

		for (LivingEntity entity : entityList ) {

			float dame = explo;
			double distance = 2 - entity.distanceTo(this) / dame;
			boolean isWarden = entity instanceof Warden;
			dame *= distance * (isWarden ? 25F : 1F);
			entity.hurt(SMDamage.MAGIC, dame);
			entity.invulnerableTime = 0;

			float pi = ((float) Math.PI / 180F);
			entity.knockback(1D, (double) Mth.sin(this.getYRot() * pi), (double) (-Mth.cos(this.getYRot() * pi)));
			if(!( this.distanceTo(entity) <= effectRange )) { continue; }

			this.addPotion(entity, PotionInit.gravity, 100, 0);
		}
	}

	// パーティクルスポーン
	protected void spawnParticle() {

		Random rand = this.rand;
		Vec3 vec = this.getDeltaMovement();
		float x = (float) (-vec.x / 80F);
		float y = (float) (-vec.y / 80F);
		float z = (float) (-vec.z / 80F);

		for (int i = 0; i < 1; i++) {
			float f1 = (float) (this.getX() - 0.5F + rand.nextFloat() + vec.x * i / 4.0F);
			float f2 = (float) (this.getY() + 0.25F + rand.nextFloat() * 0.5 + vec.y * i / 4.0D);
			float f3 = (float) (this.getZ() - 0.5F + rand.nextFloat() + vec.z * i / 4.0D);
			this.level.addParticle(ParticleTypes.CLOUD, f1, f2, f3, x, y, z);
		}
	}

	// 属性の取得
	public SMElement getElement() {
		return SMElement.BLAST;
	}
}
