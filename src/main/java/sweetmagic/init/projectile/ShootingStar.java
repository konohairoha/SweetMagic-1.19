package sweetmagic.init.entity.projectile;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import sweetmagic.api.emagic.SMElement;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.init.EntityInit;
import sweetmagic.init.ParticleInit;

public class ShootingStar extends AbstractMagicShot {

	private static final EntityDataAccessor<Integer> POSY = setEntityData(ISMMob.INT);
	private static final EntityDataAccessor<Boolean> CHARGE = setEntityData(ISMMob.BOOLEAN);

	public ShootingStar(EntityType<? extends AbstractMagicShot> entityType, Level world) {
		super(entityType, world);
		this.setMaxLifeTime(120);
	}

	public ShootingStar(double x, double y, double z, Level world) {
		this(EntityInit.shootingStar, world);
		this.setPos(x, y, z);
	}

	public ShootingStar(Level world, LivingEntity entity) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.stack = ItemStack.EMPTY;
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.define(POSY, 0);
		this.define(CHARGE, false);
	}

	public void tick() {
		this.checkOwner();
		if (this.getCharge()) { return; }
		super.tick();
	}

	// ブロック着弾
	protected void onHitBlock(BlockHitResult result) {
		this.rangeAttack(null, this.getRange());
		super.onHitBlock(result);
	}

	// えんちちーに当たった時の処理
	protected void entityHit(LivingEntity living) {
		this.rangeAttack(living, this.getRange() * 1.15F);
	}

	public void rangeAttack(LivingEntity living, double range) {

		this.playSound(SoundEvents.GENERIC_EXPLODE, 2F, 1F / (this.rand.nextFloat() * 0.2F + 0.9F));
		float damage = this.getDamage();
		List<LivingEntity> entityList = this.getEntityList(LivingEntity.class, this.isTarget(), range);
		entityList.forEach(e -> this.attackDamage(e, damage, false));
		this.discard();
		if (!(this.getLevel() instanceof ServerLevel sever)) { return; }

		sever.sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY() + 0.5D, this.getZ(), 2, 0D, 0D, 0D, 0D);

		BlockPos pos = this.blockPosition();
		float x = pos.getX() + this.getRandFloat(0.5F);
		float y = pos.getY() + this.getRandFloat(0.5F);
		float z = pos.getZ() + this.getRandFloat(0.5F);

		for (int i = 0; i < 16; i++) {
			sever.sendParticles(ParticleInit.TWILIGHTLIGHT, x, y, z, 4, 0F, 0F, 0F, 0.15F);
		}
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
	}

	public void readAdditionalSaveData(CompoundTag tags) {
		super.readAdditionalSaveData(tags);
		this.setCharge(tags.getBoolean("isCharge"));
	}

	// パーティクルスポーン
	protected void spawnParticle() {
		this.addParticle(ParticleInit.ORB, this.getX(), this.getY(), this.getZ(), 114F / 255F, 255F / 255F, 170F / 255F);
	}

	// 属性の取得
	public SMElement getElement() {
		return SMElement.SHINE;
	}
}
