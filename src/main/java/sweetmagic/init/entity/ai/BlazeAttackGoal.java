package sweetmagic.init.entity.ai;

import java.util.EnumSet;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import sweetmagic.init.entity.monster.BlazeTempest;
import sweetmagic.init.entity.projectile.AbstractMagicShot;
import sweetmagic.init.entity.projectile.CycloneMagicShot;

public class BlazeAttackGoal extends Goal {

	public final Mob blaze;
	public int attackStep;
	public int attackTime;
	public int lastSeen;
	public final int data;
	public boolean isHard;

	public BlazeAttackGoal(Mob blaze, boolean isHard, int data) {
		this.blaze = blaze;
		this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
		this.data = data;
		this.isHard = isHard;
	}

	public boolean canUse() {
		LivingEntity entity = this.blaze.getTarget();
		return entity != null && entity.isAlive() && this.blaze.canAttack(entity);
	}

	public void start() {
		this.attackStep = 0;
	}

	public void stop() {
		this.lastSeen = 0;
	}

	public boolean requiresUpdateEveryTick() {
		return true;
	}

	public void tick() {
		--this.attackTime;
		LivingEntity target = this.blaze.getTarget();
		if (target == null) { return; }

		boolean flag = this.blaze.getSensing().hasLineOfSight(target);
		this.lastSeen = flag ? 0 : ++this.lastSeen;
		double d0 = this.blaze.distanceToSqr(target);

		if (d0 < 4.0D) {

			if (!flag) { return; }

			if (this.attackTime <= 0) {
				this.attackTime = 20;
				this.blaze.doHurtTarget(target);
			}

			this.blaze.getMoveControl().setWantedPosition(target.getX(), target.getY(), target.getZ(), 1.0D);
		}

		else if (d0 < this.getFollowDistance() * this.getFollowDistance() && flag) {

			Level world = this.blaze.level;
			double x = target.getX() - this.blaze.getX();
			double y = target.getY(0.3333333333333333D) - this.blaze.getY();
			double z = target.getZ() - this.blaze.getZ();
			double xz = Math.sqrt(x * x + z * z);

			if (this.attackTime <= 0) {

				++this.attackStep;
				if (this.attackStep == 1) {
					this.attackTime = 40;
				}

				else if (this.attackStep <= 5) {
					this.attackTime = 6;
				}

				else {
					this.attackTime = 140;
					this.attackStep = 0;
				}

				if (this.attackStep > 1) {

					if (!this.blaze.isSilent()) {
						world.levelEvent((Player) null, 1018, this.blaze.blockPosition(), 0);
					}

					boolean isWarden = target instanceof Warden;
					float shake = isWarden ? 6F : 48F;
					int range = isWarden ? 30 : 16;

					float dama = isWarden ? 8F : 1.5F;
					float speed = isWarden ? 2.25F : 1F;
					int attackCount = isWarden ? 8 : 3;

					if (this.data == 0) {
						dama = isWarden ? 5F : 0.5F;
						speed = isWarden ? 2.25F : 1.25F;
						attackCount = isWarden ? 5 : 1 + (int) (Math.min(3F, 3F * ( (BlazeTempest) this.blaze).getDateRate(world, 0.25F)));
					}

					// ウォーデン以外でハードなら威力を上昇
					if (!isWarden && this.isHard) {
						dama += this.data == 0 ? 0.75F : 1F;
					}

					for (int i = 0; i < attackCount; ++i) {
						AbstractMagicShot entity = new CycloneMagicShot(world, this.blaze);
						entity.shoot(x, y - xz * (double) 0.065F, z, speed, shake);
						entity.setAddDamage(entity.getAddDamage() + dama);
						entity.setRange(1D);
						entity.setMaxLifeTime(range);
						this.blaze.playSound(SoundEvents.BLAZE_SHOOT, 0.5F, 0.67F);
						world.addFreshEntity(entity);
					}
				}
			}

			this.blaze.getLookControl().setLookAt(target, 10.0F, 10.0F);
		}

		else if (this.lastSeen < 5) {
			this.blaze.getMoveControl().setWantedPosition(target.getX(), target.getY(), target.getZ(), 1.0D);
		}

		super.tick();
	}

	public double getFollowDistance() {
		return this.blaze.getAttributeValue(Attributes.FOLLOW_RANGE);
	}
}
