package sweetmagic.init.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import sweetmagic.init.PotionInit;

public class BubleExplosionGoal extends SMBaseGoal {

	private final Mob entity;
	private final Entity attacker;
	private final Entity magic;
	private final float damage;
	private boolean isFinish = false;

	public BubleExplosionGoal(Mob entity, Entity attacker, Entity magic, float damage, int time) {
		this.entity = entity;
		this.attacker = attacker;
		this.magic = magic;
		this.damage = damage;
		this.tickTime = time;
	}

	// 時間切れになるまで続ける
	public boolean canUse() {
		return this.entity.hasEffect(PotionInit.bubble) && this.tickTime >= 0 && !this.isFinish;
	}

	public boolean canContinueToUse() {
		boolean isUse = this.canUse();

		if(!isUse) {
			this.isFinish = true;
		}

		return isUse;
	}

	// 常時処理
	public void tick() {
		if (this.tickTime-- % 20 != 0 && this.tickTime != 0) { return; }
		this.stop();
	}

	// 終了時処理
	public void stop() {

		Level world = this.entity.level;

		if (world instanceof ServerLevel sever) {
			BlockPos pos = this.entity.blockPosition();
			sever.sendParticles(ParticleTypes.EXPLOSION_EMITTER, pos.getX(), pos.getY() + 2D, pos.getZ(), 2, 0D, 0D, 0D, 0D);
		}

		this.attackDamage(this.entity, this.attacker, this.magic, this.damage * 0.1F);
		this.entity.playSound(SoundEvents.GENERIC_EXPLODE, 2F, 1F / (world.random.nextFloat() * 0.2F + 0.9F));
	}

	public void clearInfo(int tickTime) {
		this.tickTime = tickTime;
		this.isFinish = false;
	}
}
