package sweetmagic.init.entity.projectile;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import sweetmagic.api.emagic.SMElement;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.api.iitem.info.WandInfo;
import sweetmagic.init.EntityInit;
import sweetmagic.init.PotionInit;

public class LightningRod extends AbstractMagicShot {

	private static final EntityDataAccessor<Boolean> LIGHNING = setEntityData(ISMMob.BOOLEAN);

	public LightningRod(EntityType<? extends AbstractMagicShot> entityType, Level world) {
		super(entityType, world);
	}

	public LightningRod(double x, double y, double z, Level world) {
		this(EntityInit.lightningRod, world);
		this.setPos(x, y, z);
	}

	public LightningRod(Level world, LivingEntity entity, WandInfo wandInfo) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.stack = wandInfo.getStack();
	}

	public LightningRod(Level world, LivingEntity entity) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.stack = ItemStack.EMPTY;
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.define(LIGHNING, false);
	}

	public boolean getLighning() {
		return this.get(LIGHNING);
	}

	public void tick() {

		int maxTime = this.getMaxLifeTime();
		if (!this.isClient() && this.tickCount + 200 > maxTime) {

			if (this.tickCount + 5 > maxTime) {
				this.set(LIGHNING, true);
			}

			if (this.tickCount >= maxTime) {
				this.rangeAttack((float) this.getDamage(), this.getRange());
				this.discard();
			}

			if (this.tickCount % 2 == 0 && this.getLevel() instanceof ServerLevel server) {
				BlockPos pos = this.blockPosition();
				float range = (float) this.getRange();
				for (int i = 0; i < 3; i++) {
					double x = pos.getX() + this.getRandFloat(range);
					double z = pos.getZ() + this.getRandFloat(range);
					server.sendParticles(ParticleTypes.END_ROD, x, this.yo + 0.25D, z, 0, 67F / 255F, 173F / 255F, 103F / 255F, 1F);
				}
			}
		}

		super.tick();
	}

	public void rangeAttack(float dame, double range) {
		List<LivingEntity> entityList = this.getEntityList(LivingEntity.class, e -> !(e instanceof ISMMob), range);
		if (entityList.isEmpty()) { return; }

		this.playSound(SoundEvents.LIGHTNING_BOLT_THUNDER, 0.5F, 1F);
		this.playSound(SoundEvents.LIGHTNING_BOLT_IMPACT, 0.5F, 1F);

		for (LivingEntity entity : entityList) {
			this.attackDamage(entity, dame, false);
			this.addPotion(entity, PotionInit.lightning_wind_vulnerable, 1200, 1);
		}
	}

	// 属性の取得
	public SMElement getElement() {
		return SMElement.LIGHTNING;
	}
}
