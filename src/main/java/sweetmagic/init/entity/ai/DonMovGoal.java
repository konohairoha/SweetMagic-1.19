package sweetmagic.init.entity.ai;

import java.util.Set;
import java.util.UUID;

import com.google.common.collect.ImmutableMultimap;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;

public class DonMovGoal extends Goal {

	public static final UUID UUID_donmov  = UUID.fromString("6fd1ce57-8e37-504d-f859-6262b644ef19");
	public static final AttributeModifier modifierDonmove = new AttributeModifier(UUID_donmov, "SM speedDown", -5D, AttributeModifier.Operation.ADDITION);

	public int tickTime = 20;
	private final Mob entity;
	private Set<WrappedGoal> goal;

	public DonMovGoal(Mob entity, Set<WrappedGoal> goal) {
		this.entity = entity;
		this.goal = goal;
	}

	// 時間切れになるまで続ける
	public boolean canUse() {
		return this.tickTime > 0;
	}

	// 中断できるか
	public boolean isInterruptable() {
		return false;
	}

	// 開始時処理
	public void start() {

		AttributeMap attriMap = this.entity.getAttributes();
		ImmutableMultimap.Builder<Attribute, AttributeModifier> map = this.getMap();

		if (attriMap.hasModifier(Attributes.MOVEMENT_SPEED, UUID_donmov)) {
			attriMap.removeAttributeModifiers(map.build());
		}

		attriMap.addTransientAttributeModifiers(map.build());
	}

	// 終了時処理
	public void stop() {
    	this.tickTime = 0;
		AttributeMap attriMap = this.entity.getAttributes();
		ImmutableMultimap.Builder<Attribute, AttributeModifier> map = this.getMap();
		attriMap.removeAttributeModifiers(map.build());
		this.entity.goalSelector.getAvailableGoals().addAll(this.goal);
	}

	// 常時処理
	public void tick() {
        --this.tickTime;
	}

	// 移動速度情報取得
	public ImmutableMultimap.Builder<Attribute, AttributeModifier> getMap () {
		ImmutableMultimap.Builder<Attribute, AttributeModifier> map = ImmutableMultimap.builder();
		map.put(Attributes.MOVEMENT_SPEED, modifierDonmove);
		return map;
	}
}
