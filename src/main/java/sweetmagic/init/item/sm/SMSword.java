package sweetmagic.init.item.sm;

import java.util.List;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.util.ISMTip;
import sweetmagic.init.ItemInit;
import sweetmagic.util.SMDamage;
import sweetmagic.util.WorldHelper;

public class SMSword extends SwordItem implements ISMTip {

	private final int data;

	public SMSword(String name, int data, int value) {
		super(Tiers.DIAMOND, 2 + data, -2.5F + data * 0.5F, SMItem.setItem(value, data != -1 ? SweetMagicCore.smMagicTab : null));
		this.data = data;
		ItemInit.itemMap.put(this, name);
	}

	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {

		// 向き先に座標を設定
		Vec3 look = attacker.getViewVector(1F);
		Vec3 dest = new Vec3(attacker.getX(), attacker.getY(), attacker.getZ()).add(0, attacker.getEyeHeight(), 0).add(look.x * 2, look.y * 2, look.z * 2);
		BlockPos pos = new BlockPos(dest.x, attacker.getY(), dest.z);
		List<LivingEntity> entityList = this.getEntityList(LivingEntity.class, attacker, e -> e.isAlive() && e instanceof Enemy, pos, 3D + this.data);

		DamageSource src = SMDamage.getMagicDamage(attacker, attacker);
		float baseDamae = (float) attacker.getAttributeValue(Attributes.ATTACK_DAMAGE);

		for (LivingEntity entity : entityList) {
			float dame = baseDamae + EnchantmentHelper.getDamageBonus(stack, entity.getMobType());
			entity.hurt(src, dame * 0.67F);
			entity.invulnerableTime = 0;
		}

		return super.hurtEnemy(stack, target, attacker);
	}

	public <T extends Entity> List<T> getEntityList(Class<T> enClass, Entity entity, Predicate<T> filter, BlockPos pos, double range) {
		return WorldHelper.getEntityList(entity, enClass, filter, this.getAABB(pos, range));
	}

	// 範囲の取得
	public AABB getAABB(BlockPos pos, double range) {
		return new AABB(pos.getX() - range, pos.getY() - range, pos.getZ() - range, pos.getX() + range, pos.getY() + range, pos.getZ() + range);
	}

	// ツールチップの表示
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> toolTip, TooltipFlag flag) {
		toolTip.add(this.getText("alternative_sword").withStyle(GREEN));
		toolTip.add(this.getText("attack_range", 3 + this.data).withStyle(GREEN));
	}
}
