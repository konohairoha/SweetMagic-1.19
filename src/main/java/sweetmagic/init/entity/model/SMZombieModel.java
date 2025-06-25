package sweetmagic.init.render.entity.model;

import net.minecraft.client.model.AbstractZombieModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.monster.Monster;

public class SMZombieModel<T extends Monster> extends AbstractZombieModel<T> {

	public SMZombieModel(ModelPart part) {
		super(part);
	}

	public boolean isAggressive(T entity) {
		return entity.isAggressive();
	}
}
