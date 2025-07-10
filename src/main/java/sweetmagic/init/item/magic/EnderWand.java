package sweetmagic.init.item.magic;

import java.util.Arrays;
import java.util.List;

import org.antlr.v4.runtime.misc.NotNull;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import sweetmagic.api.iitem.IMFTool;
import sweetmagic.init.EnchantInit;
import sweetmagic.init.PotionInit;
import sweetmagic.init.entity.projectile.AbstractMagicShot;
import sweetmagic.init.entity.projectile.EnderBall;

public class EnderWand extends SMTierItem implements IMFTool {

	private int maxMF = 20000;
	public static final List<EnchantmentCategory> ENCHACATELIST = Arrays.<EnchantmentCategory> asList(
			EnchantInit.ISMFTOOL, EnchantInit.ISALL, EnchantInit.ISWAND_HARNESS
		);

	public EnderWand(String name) {
		super(name, 1);
	}

	// 右クリック
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		player.startUsingItem(hand);
		return InteractionResultHolder.consume(player.getItemInHand(hand));
	}

	public void releaseUsing(ItemStack stack, Level world, LivingEntity entity, int chargeTime) {
		this.magicAction(world, (Player) entity, stack, Math.min(40, this.getUseDuration(stack) - chargeTime));
	}

	public void magicAction(Level world, Player player, ItemStack stack, float chargeTime) {
		int mf = this.getMF(stack);
		int useMF = this.getNeedMF(stack);
		if ((mf < useMF && !player.isCreative()) || player.hasEffect(PotionInit.non_destructive) || world.isClientSide()) { return; }

		float shotSpeed = 1F;
		shotSpeed += chargeTime >= 10 ? 3F * (chargeTime / 60F) : 0F;
		AbstractMagicShot entity = new EnderBall(world, player);
		entity.setAddDamage(-1.99F);
		entity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, shotSpeed, 0);
		world.addFreshEntity(entity);
		this.playSound(player.getLevel(), player, SoundEvents.BLAZE_SHOOT, 0.5F, 0.67F);

		if(!player.isCreative()) {
			this.setMF(stack, mf - useMF);
		}
	}

	public int getNeedMF(ItemStack stack) {
		int useMF = 400;
		int costDown = Math.min(99, this.getEnchantLevel(EnchantInit.mfCostDown, stack) * 10);
		return useMF *= (100 - costDown) / 100F;
	}

	// ツールチップの表示
	public void addTip(ItemStack stack, List<Component> toolTip) {
		toolTip.add(this.getText(this.name).withStyle(GREEN));
		toolTip.add(this.getText(this.name + "_entity").withStyle(GREEN));
		toolTip.add(this.getText(this.name + "_food").withStyle(GREEN));
		toolTip.add(this.getText(this.name + "_charge").withStyle(GREEN));
		toolTip.add(this.getTipArray(this.getText("mf"), ": ", this.getLabel(this.format(this.getMF(stack)), WHITE)).withStyle(GREEN));
	}

	@Override
	public int getMaxMF(ItemStack stack) {
		int addMaxMF = (this.getEnchantLevel(EnchantInit.maxMFUP, stack) * 10) * (this.maxMF / 100);
		return this.maxMF + addMaxMF;
	}

	@Override
	public void setMaxMF(int maxMF) {
		this.maxMF = maxMF;
	}

	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment encha) {
		return ENCHACATELIST.contains(encha.category) || (encha != Enchantments.UNBREAKING && encha.category.canEnchant(stack.getItem()));
	}

	public int getUseDuration(ItemStack stack) {
		return 72000;
	}

	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.BOW;
	}

	// エンチャレベル取得
	public int getEnchantLevel(Enchantment enchant, ItemStack stack) {
		return Math.min(EnchantmentHelper.getItemEnchantmentLevel(enchant, stack), 10);
	}

	@Override
	public int getBarColor(@NotNull ItemStack stack) {
		return this.getMF(stack) >= this.getMaxMF(stack) ? 0X30FF89 : 0X00C3FF;
	}

	@Override
	public boolean isBarVisible(@NotNull ItemStack stack) {
		return this.getMF(stack) != 0;
	}

	@Override
	public int getBarWidth(@NotNull ItemStack stack) {
		return Math.min(13, Math.round(13F * (float) this.getMF(stack) / (float) this.getMaxMF(stack)));
	}
}
