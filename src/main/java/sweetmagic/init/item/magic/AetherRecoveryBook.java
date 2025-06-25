package sweetmagic.init.item.magic;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import sweetmagic.api.iitem.IMFTool;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.item.sm.TreasureItem;

public class AetherRecoveryBook extends TreasureItem {

	public AetherRecoveryBook(String name, int tier) {
		super(name, tier, tier - 1);
	}

	// 右クリック
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {

		// インベントリからアイテムの取得
		ItemStack stack = player.getItemInHand(hand);
		Inventory pInv = player.getInventory();
		List<NonNullList<ItemStack>> playerStackList = ImmutableList.of(pInv.items, pInv.armor, pInv.offhand);
		List<ItemStack> stackList = new ArrayList<>();

		// MFを溜めれるアイテムだけリストに入れる
		for (List<ItemStack> stacks : playerStackList) {
			List<ItemStack> filterList = stacks.stream().filter(s -> !s.isEmpty() && s.getItem() instanceof IMFTool tool && !tool.isMaxMF(s)).toList();
			if (filterList.isEmpty()) { continue; }

			stackList.addAll(filterList);
		}

		if (stackList.isEmpty()) { return InteractionResultHolder.pass(stack); }

		// 回復するMF量を取得
		int mf = this.getHealMF();
		stackList.forEach(s -> ((IMFTool) s.getItem()).insetMF(s, mf));
		this.playSound(player, SoundEvents.AMETHYST_BLOCK_BREAK, 0.5F, 0.75F);

		if (world instanceof ServerLevel sever) {
			int count = 10 * this.tier;
			ParticleOptions par = ParticleInit.DIVINE;

			for (int i = 0; i < count; i++) {
				float x = (float) player.getX() + this.getRandFloat();
				float y = (float) player.getY() + 0.5F + this.getRandFloat(0.5F);
				float z = (float) player.getZ() + this.getRandFloat();
				sever.sendParticles(par, x, y, z, 0, this.getRandFloat(0.1F), this.rand.nextFloat() * 0.25F, this.getRandFloat(0.1F), 1F);
			}
		}

		if (!player.isCreative()) { stack.shrink(1); }
		return InteractionResultHolder.consume(stack);
	}

	public int getHealMF() {
		switch (this.data) {
		case 1:  return 8000;
		case 2:  return 32000;
		case 3:  return 128000;
		case 4:  return 512000;
		default: return 2000;
		}
	}

	// ツールチップの表示
	public void addTip(ItemStack stack, List<Component> toolTip) {
		toolTip.add(this.tierTip(this.tier));
		toolTip.add(this.getText("aether_recovery_book", this.format(this.getHealMF())).withStyle(GREEN));
	}
}
