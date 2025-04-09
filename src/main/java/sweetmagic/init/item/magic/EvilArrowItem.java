package sweetmagic.init.item.magic;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import sweetmagic.init.entity.projectile.AbstractMagicShot;
import sweetmagic.init.entity.projectile.EvilArrow;

public class EvilArrowItem extends SMMagicItem {

	public EvilArrowItem(String name) {
		super(name);
	}

	// 右クリック
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {

		// アイテムスタックを取得
		ItemStack stack = player.getItemInHand(hand);

		if (!world.isClientSide) {
			AbstractMagicShot entity = new EvilArrow(world, player);
			entity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 2F, 0);
			entity.setAddDamage(8F);
			world.addFreshEntity(entity);
			this.playSound(player, SoundEvents.BLAZE_SHOOT, 0.25F, 0.67F);
			stack.shrink(1);
		}

		return InteractionResultHolder.consume(stack);
	}

	// ツールチップの表示
	public void addTip(ItemStack stack, List<Component> toolTip) {
		toolTip.add(this.getText(this.name).withStyle(GREEN));
	}
}
