package sweetmagic.init.entity.projectile;

import java.util.List;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import sweetmagic.api.emagic.SMElement;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.api.iitem.info.WandInfo;
import sweetmagic.init.EntityInit;
import sweetmagic.init.PotionInit;

public class TridentThunder extends AbstractMagicShot {

	private static final EntityDataAccessor<Boolean> LIGHNING = setEntityData(ISMMob.BOOLEAN);
	private static final EntityDataAccessor<Boolean> CHARGE = setEntityData(ISMMob.BOOLEAN);

	public TridentThunder(EntityType<? extends AbstractMagicShot> entityType, Level world) {
		super(entityType, world);
	}

	public TridentThunder(double x, double y, double z, Level world) {
		this(EntityInit.tridentThunder, world);
		this.setPos(x, y, z);
	}

	public TridentThunder(Level world, LivingEntity entity, WandInfo wandInfo) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.stack = wandInfo.getStack();
	}

	public TridentThunder(Level world, LivingEntity entity) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.stack = ItemStack.EMPTY;
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.define(LIGHNING, false);
		this.define(CHARGE, false);
	}

	public boolean getLighning() {
		return this.get(LIGHNING);
	}

	public void setCharge(boolean isCharge) {
		this.set(CHARGE, isCharge);
	}

	public boolean getCharge() {
		return this.get(CHARGE);
	}

	public void tick() {
		if (this.getCharge()) { return; }
		super.tick();
	}
	// ブロック着弾
	protected void onHitBlock(BlockHitResult result) {

		if (!this.getLighning()) {
			this.set(LIGHNING, true);
			this.setMaxLifeTime(10);
			this.rangeAttack((float) this.getDamage() * 0.5F, this.getRange());
			this.playSound(SoundEvents.LIGHTNING_BOLT_THUNDER, 1F, 1F);
			this.playSound(SoundEvents.LIGHTNING_BOLT_IMPACT, 1F, 1F);
			this.playSound(SoundEvents.TRIDENT_HIT_GROUND, 1F, 1F);
		}
	}

	public void rangeAttack(float dame, double range) {
		List<LivingEntity> entityList = this.getEntityList(LivingEntity.class, e -> !(e instanceof ISMMob), range);
		if (entityList.isEmpty()) { return; }

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
