package sweetmagic.init.item.sm;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.iitem.IFood;
import sweetmagic.init.ItemInit;
import sweetmagic.init.PotionInit;
import sweetmagic.util.PlayerHelper;

public class SMFood  extends SMItem implements IFood {

	private final int data;
	private final boolean isDrink;
//	private final int healAmount;
//	private final float saturation;
	private final boolean isQuality;
	private int foodLevel;

	public SMFood(String name, int healAmount, float saturation, int data, boolean isDrink) {
		super(name, SMItem.setItem(SweetMagicCore.smFoodTab).food(foodBuild(healAmount, saturation)));
		this.data = data;
		this.isDrink = isDrink;
//		this.healAmount = healAmount;
//		this.saturation = saturation;
		ItemInit.foodList.add(this);

		this.isQuality = false;
		this.foodLevel = 0;
	}

	public SMFood(String name, int healAmount, float saturation, int data) {
		super(name, SMItem.setItem(SweetMagicCore.smFoodTab).food(foodBuild(healAmount, saturation)));
		this.data = data;
		this.isDrink = false;
//		this.healAmount = healAmount;
//		this.saturation = saturation;
		ItemInit.foodList.add(this);

		this.isQuality = true;
		this.foodLevel = 3;
	}

//	public FoodProperties getFoodProperties(ItemStack stack, @Nullable LivingEntity entity) {
//		return foodBuild(this.healAmount * 10, this.saturation * 10);
//	}

	/**
	 * 0 = 通常
	 * 1 = 全バフ解除
	 * 2 = 火消し
	 * 3 = 体力回復(ハート1)
	 * 4 = 火炎耐性
	 * 5 = MF消費ダウン
	 * 6 = 酔い
	 * 7 = 体力回復(最大20%)
	 * 8 = 周囲体力回復(ハート2)
	 * 9 = 杖経験値増加
	 * 10 = 回復量増加
	 */

	// 食べた際にポーション効果を付加
	protected void onFoodEaten(Level world, Player player, ItemStack stack) {
		switch (this.data) {
		case 1:
			player.curePotionEffects(new ItemStack(Items.MILK_BUCKET));
			break;
		case 2:
			player.clearFire();
			break;
		case 3:
			player.heal(2F);
			break;
		case 4:
			this.addPotion(player, MobEffects.FIRE_RESISTANCE, 1800, 0);
			break;
		case 5:
			this.addPotion(player, PotionInit.mfcostdown, 2400, 0);
			break;
		case 6:
			this.addPotion(player, MobEffects.CONFUSION, 100, 0);
			break;
		case 7:
			player.heal(player.getMaxHealth() * 0.2F);
			break;
		case 8:

			RandomSource rand = world.random;
			List<LivingEntity> entityList = this.getEntityList(LivingEntity.class, player, e -> e.isAlive() && ( !(e instanceof Enemy) || e instanceof Zombie), 5D);

			for (LivingEntity entity : entityList) {

				ParticleOptions par = ParticleTypes.HAPPY_VILLAGER;

				if (entity instanceof Enemy) {
					entity.addEffect(new MobEffectInstance(MobEffects.HEAL, 1, 1, true, false));
					par = ParticleTypes.TOTEM_OF_UNDYING;
				}

				else {
					entity.heal(4F);
				}

				if (world instanceof ServerLevel sever) {
					for (int i = 0; i < 4; i++) {
						float x = (float) (entity.getX() + rand.nextFloat() * 1.5F - 0.5F);
						float y = (float) (entity.getY() + rand.nextFloat() - rand.nextFloat() + 1F);
						float z = (float) (entity.getZ() + rand.nextFloat() * 1.5F - 0.75F);
						sever.sendParticles(par, x, y, z, 4, 0F, 0F, 0F, 0.1F);
					}
				}
			}

			break;
		case 9:
			this.addPotion(player, PotionInit.increased_experience, 2400, 0);
			break;
		case 10:
			this.addPotion(player, PotionInit.increased_recovery, 900, 0);
			break;
		}
	}

	public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entity) {

		if (!world.isClientSide && entity instanceof Player player) {
			this.onFoodEaten(world, player, stack);
		}

		return super.finishUsingItem(stack, world, entity);
	}

	public void addPotion(LivingEntity entity, MobEffect potion, int time, int level) {
		PlayerHelper.setPotion(entity, potion, level, time);
	}

	public int getUseDuration(ItemStack stack) {
		return 32;
	}

	public UseAnim getUseAnimation(ItemStack stack) {
		return this.isDrink ? UseAnim.DRINK : UseAnim.EAT;
	}

	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		return ItemUtils.startUsingInstantly(level, player, hand);
	}

	public static FoodProperties foodBuild(int healAmount, float saturation) {
		return (new FoodProperties.Builder()).nutrition(healAmount).saturationMod(saturation).alwaysEat().build();
	}

	// ツールチップの表示
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> toolTip, TooltipFlag flag) {

		if (this.data == 0) { return; }

		String text = "";

		switch (this.data) {
		case 1:
			text = "food_buffclear";
			break;
		case 2:
			text = "food_fireclear";
			break;
		case 3:
			text = "food_heal";
			break;
		case 7:
			text = "food_heal2";
			break;
		case 8:
			text = "food_heal3";
			break;
		}

		if (this.data == 4 || this.data == 5 || this.data == 9 || this.data == 10) {

			int time = 90;
			MutableComponent effect = null;;

			switch (this.data) {
			case 4:
				time = 90;
				effect = this.getMCText("fire_resistance");
				break;
			case 5:
				time = 120;
				effect = this.getEffectText("mfcostdown");
				break;
			case 9:
				time = 120;
				effect = this.getEffectText("increased_experience");
				break;
			case 10:
				time = 45;
				effect = this.getEffectText("increased_recovery");
				break;
			}

			toolTip.add(this.getTipArray(this.getText("food_effect"), effect, " (" + time + "sec)").withStyle(GREEN));
		}

		else {
			toolTip.add(this.getText(text).withStyle(GREEN));
		}
	}

	// 品質の対象か
	public boolean isQuality() {
		return this.isQuality;
	}

	// 料理難易度のレベル設定
	public void setFoodLevel (int level) {
		this.foodLevel = level;
	}

	// 料理難易度のレベル取得
	public int getFoodLevel() {
		return this.foodLevel;
	}

	public PotionInfo getPotionInfo() {
		return new PotionInfo(PotionInit.increased_recovery, 900, 0);
	}
}
