package sweetmagic.init.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;

public class ExplosionAttackGoal extends SMBaseGoal {

	private final Mob entity;
	private final Entity attacker;
	private final Entity magic;
	private final float damage;
	private int coolTime = 10;
	private boolean isFinish = false;

	public ExplosionAttackGoal(Mob entity, Entity attacker, Entity magic, float damage, int time) {
		this.entity = entity;
		this.attacker = attacker;
		this.magic = magic;
		this.damage = damage;
		this.tickTime = time;
	}

	public boolean canContinueToUse() {
		return this.canUse() && !this.isFinish;
	}

	// 常時処理
	public void tick() {
		if (this.coolTime-- >= 0 || this.tickTime-- % 5 != 0) { return; }
		if (this.tickTime <= 10) { return; }
		this.stop();
	}

	// 終了時処理
	public void stop() {

		Level world = this.entity.level;
		this.entity.invulnerableTime = 0;
		boolean isFinish = this.tickTime <= 0;
		float damage = isFinish ? this.damage : this.damage * 0.25F;

		if (world instanceof ServerLevel sever) {
			BlockPos pos = this.entity.blockPosition();
			sever.sendParticles(ParticleTypes.EXPLOSION_EMITTER, pos.getX(), pos.getY() + 2D, pos.getZ(), 2, 0D, 0D, 0D, 0D);
		}

		this.attackDamage(this.entity, this.attacker, this.magic, damage);
		float pitch = isFinish ? 1.15F : 0.75F;
		this.entity.playSound(SoundEvents.GENERIC_EXPLODE, 2F, 1F / (world.random.nextFloat() * 0.2F + pitch));

		if (isFinish && this.hasGoal(entity, this)) {
			this.isFinish = true;
//			try {
//				this.entity.goalSelector.removeGoal(this);
//			}
//			catch (Throwable e) { }
		}
	}

	public void clearInfo(int tickTime) {
		this.tickTime = tickTime;
		this.isFinish = false;
	}
}
