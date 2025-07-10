package sweetmagic.init.item.sm;

import java.util.List;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import sweetmagic.init.SoundInit;
import sweetmagic.init.entity.animal.AbstractSummonMob;
import sweetmagic.init.item.magic.SMMagicItem;

public class SummonerWand extends SMMagicItem {

	private static final String MODE = "mode";

	public SummonerWand(String name) {
		super(name);
	}

	// 右クリック
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {

		ItemStack stack = player.getItemInHand(hand);
		List<AbstractSummonMob> entityList = this.getEntityList(AbstractSummonMob.class, player, e -> e.isAlive() && e.getOwnerUUID().equals(player.getUUID()), 8096D);

		for (AbstractSummonMob entity : entityList) {
			entity.teleportToOwner(player);
			entity.setTarget(null);
		}

		this.playSound(player, SoundInit.MAGIC_CRAFT, 0.5F, 1.2F);
		return InteractionResultHolder.consume(stack);
	}

	public void leftClick(Level world, Player player, ItemStack stack) {
		CompoundTag tags = stack.getOrCreateTag();
		int tickCount = tags.getInt("tickCount");
		if(tickCount == player.tickCount) { return; }

		List<AbstractSummonMob> entityList = this.getEntityList(AbstractSummonMob.class, player, e -> e.isAlive() && e.getOwnerUUID().equals(player.getUUID()), 64D);
		entityList.forEach(e -> e.setShitMob(tags.getBoolean(MODE)));
		tags.putBoolean(MODE, !tags.getBoolean(MODE));
		tags.putInt("tickCount", player.tickCount);
		player.sendSystemMessage(this.getText(this.name + "_" + tags.getBoolean(MODE)).withStyle(GREEN));
		this.playSound(player, SoundEvents.AMETHYST_BLOCK_BREAK, 0.5F, 1.15F);
	}

	// ツールチップの表示
	@Override
	public void addTip(ItemStack stack, List<Component> toolTip) {
		toolTip.add(this.getText(this.name).withStyle(GOLD));
		toolTip.add(this.getText(this.name + "_left").withStyle(GOLD));
	}
}
