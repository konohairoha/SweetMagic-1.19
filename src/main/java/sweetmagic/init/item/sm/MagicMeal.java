package sweetmagic.init.item.sm;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraftforge.event.ForgeEventFactory;
import sweetmagic.init.block.sm.SMSapling;
import sweetmagic.init.item.magic.SMTierItem;
import sweetmagic.worldgen.tree.gen.PrismTreeGen;

public class MagicMeal extends SMTierItem {

	public MagicMeal(String name) {
		super(name, 1);
	}

	public InteractionResult useOn(UseOnContext con) {

		Level world = con.getLevel();
		RandomSource rand = world.getRandom();
		BlockPos pos = con.getClickedPos();
		Block block = world.getBlockState(pos).getBlock();
		Player player = con.getPlayer();
		ItemStack stack = player.getMainHandItem();

		if (!world.isClientSide() && block instanceof FlowerBlock flower) {
			int chance = rand.nextInt(7) + 4;
			if(!player.isCreative()) { stack.shrink(1); }
			this.spawnItem(world, pos, new ItemStack(flower, chance));
			this.playSound(player, SoundEvents.FIREWORK_ROCKET_BLAST_FAR, 0.5F, 1F / (rand.nextFloat() * 0.4F + 1.2F) + 1 * 0.5F);
		}

		else if (block instanceof SMSapling sap) {
			PrismTreeGen tree = new PrismTreeGen(sap.getLog().defaultBlockState(), sap.getLeaves(), 0);
			tree.generate(world, rand, pos);
			if(!player.isCreative()) { stack.shrink(1); }
			this.playSound(player, SoundEvents.FIREWORK_ROCKET_BLAST_FAR, 0.5F, 1F / (rand.nextFloat() * 0.4F + 1.2F) + 1 * 0.5F);
		}

		return InteractionResult.sidedSuccess(world.isClientSide());
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {

		if(entity instanceof Witch witch && entity.getLevel() instanceof ServerLevel server) {

			Villager villager = witch.convertTo(EntityType.VILLAGER, false);
			villager.finalizeSpawn(server, server.getCurrentDifficultyAt(villager.blockPosition()), MobSpawnType.CONVERSION, (SpawnGroupData) null, (CompoundTag) null);
			villager.refreshBrain(server);
			villager.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
			if (!server.isClientSide()) {
				server.levelEvent((Player) null, 1027, witch.blockPosition(), 0);
			}

			if(!player.isCreative()) { stack.shrink(1); }
			ForgeEventFactory.onLivingConvert(witch, villager);
			this.playSound(player, SoundEvents.FIREWORK_ROCKET_BLAST_FAR, 0.5F, 1F / (server.getRandom().nextFloat() * 0.4F + 1.2F) + 1 * 0.5F);
		}

		return super.interactLivingEntity(stack, player, entity, hand);
	}

	// ツールチップの表示
	@Override
	public void addTip(ItemStack stack, List<Component> toolTip) {
		toolTip.add(this.getText(this.name + "_flower").withStyle(GREEN));
		toolTip.add(this.getText(this.name + "_sapling").withStyle(GREEN));
	}
}
