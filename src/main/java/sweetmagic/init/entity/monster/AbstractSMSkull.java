package sweetmagic.init.entity.monster;

import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.level.Level;
import sweetmagic.api.ientity.ISMMob;

public abstract class AbstractSMSkull extends Skeleton implements ISMMob {

	public AbstractSMSkull(EntityType<? extends Skeleton> enType, Level world) {
		super(enType, world);
	}

	public void reassessWeaponGoal() { }

	public boolean isFreezeConverting() { return false; }

	public void refreshInfo() {
		this.reapplyPosition();
		this.refreshDimensions();
	}

	public SynchedEntityData getData() {
		return this.getEntityData();
	}
}
