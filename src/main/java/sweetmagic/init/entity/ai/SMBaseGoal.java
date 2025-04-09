package sweetmagic.init.entity.ai;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import sweetmagic.util.SMDamage;

public abstract class SMBaseGoal extends Goal {

	public int tickTime;

	// 時間切れになるまで続ける
	public boolean canUse() {
		return this.tickTime >= 0;
	}

	// 中断できるか
	public boolean isInterruptable() {
		return false;
	}

	public void attackDamage (Mob target, Entity attacker, Entity magic, float damage) {

		target.invulnerableTime = 0;

		// エンダーマン以外ならターゲットに攻撃
		if (!(target instanceof EnderMan) && !(target instanceof Witch)) {
			target.hurt(SMDamage.getMagicDamage(magic, attacker), target instanceof Warden ? damage * 4F : damage);
		}

		// エンダーマンの場合
		else {

			if (attacker instanceof Player payer) {
				target.hurt(DamageSource.playerAttack(payer), damage);
			}
		}

		target.invulnerableTime = 0;
	}

	public boolean hasGoal (Mob entity, Goal goal) {
		return !entity.goalSelector.getAvailableGoals().stream().filter(g -> g.getGoal() == this).toList().isEmpty();
	}
}
