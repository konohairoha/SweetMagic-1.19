package sweetmagic.init.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;

public class CookingStatusHandler implements ICookingStatus {

	private LivingEntity host;
	private int exp;
	private int level;
	private float health;

	public void setEntity(LivingEntity entity) {
		this.host = entity;
	}

	public LivingEntity getEntity () {
		return this.host;
	}

	public void setExpValue(int exp) {
		this.exp = exp;
	}

	public int getExpValue() {
		return this.exp;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getLevel() {
		return this.level;
	}

	public void setHealth (float health) {
		this.health = health;
	}

	public float getHealth () {
		return this.health;
	}

	@Override
	public CompoundTag serializeNBT() {
		return this.writeNBT();
	}

	@Override
	public void deserializeNBT(CompoundTag tags) {
		this.readNBT(tags);
	}
}
