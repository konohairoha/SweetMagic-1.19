package sweetmagic.init.item.sm;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerBlock;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.block.sm.SMSapling;
import sweetmagic.worldgen.tree.gen.PrismTreeGen;

public class MagicMeal extends SMItem {

	public MagicMeal (String name) {
		super(name, SweetMagicCore.smMagicTab);
	}

	public InteractionResult useOn(UseOnContext con) {

		Level world = con.getLevel();
		RandomSource rand = world.random;
		BlockPos pos = con.getClickedPos();
		Block block = world.getBlockState(pos).getBlock();
		Player player = con.getPlayer();
		ItemStack stack = player.getMainHandItem();

		if (!world.isClientSide && block instanceof FlowerBlock flower) {

			int chance = rand.nextInt(7) + 4;
			if(!player.isCreative()) { stack.shrink(1); }
			world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(flower, chance)));
			this.playSound(player,  SoundEvents.FIREWORK_ROCKET_BLAST_FAR, 0.5F, 1F / (rand.nextFloat() * 0.4F + 1.2F) + 1 * 0.5F);
		}

		else if (block instanceof SMSapling sap) {
			PrismTreeGen tree = new PrismTreeGen(sap.getLog().defaultBlockState(), sap.getLeaves(), 0);
			tree.generate(world, rand, pos);
			if(!player.isCreative()) { stack.shrink(1); }
			this.playSound(player,  SoundEvents.FIREWORK_ROCKET_BLAST_FAR, 0.5F, 1F / (rand.nextFloat() * 0.4F + 1.2F) + 1 * 0.5F);
		}

		return InteractionResult.sidedSuccess(world.isClientSide);
	}

	// ツールチップの表示
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> toolTip, TooltipFlag flag) {
		toolTip.add(this.getText(this.name + "_flower").withStyle(GREEN));
		toolTip.add(this.getText(this.name + "_sapling").withStyle(GREEN));
	}
}
