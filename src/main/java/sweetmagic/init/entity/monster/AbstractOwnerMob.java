package sweetmagic.init.entity.monster;

import java.util.UUID;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public abstract class AbstractOwnerMob extends AbstractSMMob {

	private UUID ownerID;
	private LivingEntity owner;

	public AbstractOwnerMob(EntityType<? extends AbstractSMMob> enType, Level world) {
		super(enType, world);
	}

	public UUID getOwnerID() {
		return this.ownerID;
	}

	public void setOwnerID(LivingEntity entity) {
		this.ownerID = entity.getUUID();
	}

	public void setOwnerID(UUID id) {
		this.ownerID = id;
	}

	public LivingEntity getEntity() {

		LivingEntity entity = this.owner;

		if (entity == null && this.level instanceof ServerLevel server) {
			entity = (LivingEntity) server.getEntity(this.getOwnerID());
		}

		return entity;
	}

	public boolean is(LivingEntity entity) {
		return this.getOwnerID().equals(entity.getUUID());
	}

	public void addAdditionalSaveData(CompoundTag tags) {
		super.addAdditionalSaveData(tags);
		this.saveOwnerTag(tags);
	}

	public void saveOwnerTag(CompoundTag tags) {
		if (this.getOwnerID() != null) {
			tags.putUUID("ownerID", this.getOwnerID());
		}
	}

	public void readAdditionalSaveData(CompoundTag tags) {
		super.readAdditionalSaveData(tags);
		this.writeOwnerTag(tags);
	}

	public void writeOwnerTag(CompoundTag tags) {
		if (tags.contains("ownerID")) {
			this.setOwnerID(tags.getUUID("ownerID"));
		}
	}
}
