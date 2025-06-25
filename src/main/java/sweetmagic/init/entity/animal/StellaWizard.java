package sweetmagic.init.entity.animal;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.api.util.ISMTip;
import sweetmagic.init.EntityInit;
import sweetmagic.init.ItemInit;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.PotionInit;
import sweetmagic.init.entity.projectile.AbstractMagicShot;
import sweetmagic.init.entity.projectile.CommetBulet;

public class StellaWizard extends AbstractWitch implements ISMTip {

	private static final ItemStack WAND = new ItemStack(ItemInit.magic_book_scarlet);
	private static final EntityDataAccessor<Float> CRYSTAL_HEALTH = ISMMob.setData(StellaWizard.class, ISMMob.FLOAT);
	private static final EntityDataAccessor<Float> MAX_CRYSTAL_HEALTH = ISMMob.setData(StellaWizard.class, ISMMob.FLOAT);
	private static final EntityDataAccessor<Boolean> CRYSTAL = ISMMob.setData(StellaWizard.class, ISMMob.BOOLEAN);
	private static final EntityDataAccessor<Boolean> DEMONS = ISMMob.setData(StellaWizard.class, ISMMob.BOOLEAN);

	public StellaWizard(Level world) {
		super(EntityInit.stellaWizard, world);
	}

	public StellaWizard(EntityType<? extends AbstractWitch> eType, Level world) {
		super(eType, world);
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.define(CRYSTAL, false);
		this.define(DEMONS, false);
		this.define(CRYSTAL_HEALTH, 0F);
		this.define(MAX_CRYSTAL_HEALTH, 0F);
	}

	public boolean getCrystal() {
		return this.get(CRYSTAL);
	}

	public void setCrystal(boolean crystal) {
		this.set(CRYSTAL, crystal);
	}

	public boolean getDemons() {
		return this.get(DEMONS);
	}

	public void setDemons(boolean demons) {
		this.set(DEMONS, demons);
	}

	public float getCrystalHealth() {
		return this.get(CRYSTAL_HEALTH);
	}

	public void setCrystalHealth(float crystal) {
		this.set(CRYSTAL_HEALTH, crystal);
	}

	public float getMaxCrystalHealth() {
		return this.get(MAX_CRYSTAL_HEALTH);
	}

	public void setMaxCrystalHealth(float crystal) {
		this.set(MAX_CRYSTAL_HEALTH, crystal);
	}

	public boolean canAttack() {
		return this.getCrystalHealth() > 0F;
	}

	public void addAdditionalSaveData(CompoundTag tags) {
		super.addAdditionalSaveData(tags);
		tags.putBoolean("isCrystal", this.getCrystal());
		tags.putBoolean("isDemons", this.getDemons());
		tags.putFloat("crystalHealth", this.getCrystalHealth());
		tags.putFloat("maxCrystalHealth", this.getMaxCrystalHealth());
	}

	public void readAdditionalSaveData(CompoundTag tags) {
		super.readAdditionalSaveData(tags);
		this.setCrystal(tags.getBoolean("isCrystal"));
		this.setDemons(tags.getBoolean("isDemons"));
		this.setCrystalHealth(tags.getFloat("crystalHealth"));
		this.setMaxCrystalHealth(tags.getFloat("maxCrystalHealth"));
	}

	// ダメージ処理
	public boolean hurt(DamageSource src, float amount) {
		Entity attacker = src.getEntity();

		if(this.getCrystal()) {
			if(!(attacker instanceof Player)) { return false; }

			int count = 16;
			this.setCrystalHealth(this.getCrystalHealth() - amount);

			if(this.getCrystalHealth() <= 0F) {
				count = 64;
				this.setCrystal(false);
				this.playSound(SoundEvents.AMETHYST_CLUSTER_BREAK, 2F, 1F);
				this.addPotion(this, PotionInit.reflash_effect, 99999, 0);
				this.addPotion(this, PotionInit.aether_armor, 99999, 100);
				this.addPotion(this, PotionInit.aether_barrier, 99999, 100);
				this.addPotion(this, PotionInit.damage_cut, 99999, 100);
				this.addPotion(this, PotionInit.regeneration, 99999, 100);
				this.addPotion(this, PotionInit.resurrection, 99999, 100);
				this.addPotion(this, PotionInit.resistance_blow, 99999, 100);

			}

			else {
				this.playSound(SoundEvents.AMETHYST_BLOCK_HIT, 2F, 1F);
			}

			if (this.getLevel() instanceof ServerLevel sever) {
				ParticleOptions par = ParticleInit.DIVINE;
				BlockPos pos = this.blockPosition();

				for (int i = 0; i < count; i++) {
					float x = (float) pos.getX() + 0.5F + this.getRand(1.5F);
					float y = (float) pos.getY() + 0.5F + this.getRand(0.5F);
					float z = (float) pos.getZ() + 0.5F + this.getRand(1.5F);
					sever.sendParticles(par, x, y, z, 1, this.getRand(0.15F), this.rand.nextFloat() * 0.5F, this.getRand(0.15F), 1F);
				}
			}

			amount = 0F;

			if(!this.isClient()) {
				List<Player> playerList = this.getEntityList(Player.class, 48D);
				playerList.forEach(p -> p.sendSystemMessage(this.getText("stella_wizard_summon").withStyle(GREEN)));
			}

			return true;
		}

		return super.hurt(src, amount);
	}

	public InteractionResult mobClick(InteractionResult result, Player player, ItemStack stack) {
		if(this.getCrystal()) { return InteractionResult.PASS; }
		return super.mobClick(result, player, stack);
	}

	public void tick() {
		if(this.getCrystal()) { return; }
		super.tick();
	}

	protected void customServerAiStep() {
		if(this.getCrystal()) { return; }
		super.customServerAiStep();
	}

	public void magicAttack(LivingEntity target, boolean isWarden) {

		List<CommetBulet> commetList = this.getEntityList(CommetBulet.class, e -> e.getOwner() == this && e.getCharge(), 64D);

		if(!commetList.isEmpty()) {
			commetList.get(0).setCharge(false);
			this.recastTime = recastTime - 4;
		}

		else {

			float damage = 20F;
			float dameRate = isWarden ? 2F : 1F;
			this.recastTime = this.getRecastTime();
			List<LivingEntity> entityList = this.getEntityList(LivingEntity.class, e -> e instanceof Enemy, 48D);
			int size = Math.max(12, entityList.size());

			for (int i = 0; i < size; i++) {
				LivingEntity entity = entityList.get(this.rand.nextInt(entityList.size()));
				AbstractMagicShot magic = this.getMagicShot(entity, isWarden);
				double x = entity.getX() - this.getX();
				double y = entity.getY(0.3333333333333333D) - this.getY();
				double z = entity.getZ() - this.getZ();
				double xz = Math.sqrt(x * x + z * z);
				magic.shoot(x, y - xz * 0.065D, z, 2F, 0F);
				magic.setAddDamage((magic.getAddDamage() + damage) * dameRate);
				magic.setPos(this.getX() + this.getRand(0.5F), this.getY() + 1D + this.getRand(0.5F), this.getZ() + this.getRand(0.5F));
				this.getLevel().addFreshEntity(magic);
			}
		}
	}

	public AbstractMagicShot getMagicShot(LivingEntity target, boolean isWarden) {
		CommetBulet magic = new CommetBulet(this.getLevel(), this);
		magic.setWandLevel(this.getWandLevel());
		magic.setTarget(target);
		magic.setCharge(true);
		magic.setDeltaMovement(new Vec3(0D, 0D, 0D));
		return magic;
	}

	public ItemStack getStack() {
		return WAND;
	}

	public int getRecastTime() {
		return 200;
	}

	public int getWandLevel() {
		return 20;
	}

	@Override
	public float getHealValue() {
		return this.getMaxHealth() * 0.67F;
	}
}
