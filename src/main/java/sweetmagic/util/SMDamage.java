package sweetmagic.util;

import javax.annotation.Nullable;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.Entity;

public class SMDamage extends DamageSource {

	private final int data;
	private boolean isDebuffFlag = false;

	private Entity thower = null;
	private Entity magicShot = null;

	public static SMDamage magicDamage = new SMDamage("sm_magic", 0).setBypassesArmor();
	public static SMDamage flameDamage = new SMDamage("sm_flame", 0).setBypassesArmor();
	public static SMDamage flostDamage = new SMDamage("sm_flost", 0).setBypassesArmor();
	public static SMDamage exploDamage = new SMDamage("sm_explo", 0).setBypassesArmor();
	public static SMDamage poisonDamage = new SMDamage("sm_poison", 0).setBypassesArmor();
	public static SMDamage addDamage = new SMDamage("sm_add", 1).setBypassesArmor();

	public SMDamage(String name, int data) {
		super(name);
		this.data = data;
		this.setMagic();
	}

	// 魔法ダメージの取得
	public static DamageSource getMagicDamage(Entity magicShot, Entity thower) {
		return (new IndirectEntityDamageSource("sm_magic", magicShot, thower).bypassArmor().setMagic());
	}

	public static DamageSource getAddDamage (Entity magicShot, Entity thower) {
		SMDamage src = addDamage;
		src.thower = thower;
		src.magicShot = magicShot;
		return src.bypassArmor();
	}

	private SMDamage setBypassesArmor() {
		super.bypassArmor();
		return this;
	}

	public void setDebuffFlag (boolean isDebuffFlag) {
		this.isDebuffFlag = isDebuffFlag;
	}

	public boolean isDebuffFlag () {
		return this.isDebuffFlag;
	}

	@Nullable
	public Entity getDirectEntity() {
		return this.data == 0 ? this.getEntity() : this.magicShot;
	}

	@Nullable
	public Entity getEntity() {
		return this.data == 0 ? null : this.thower;
	}
}
