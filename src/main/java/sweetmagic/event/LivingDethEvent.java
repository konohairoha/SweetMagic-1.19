package sweetmagic.event;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import sweetmagic.init.CapabilityInit;
import sweetmagic.init.EnchantInit;
import sweetmagic.init.ItemInit;
import sweetmagic.init.PotionInit;
import sweetmagic.init.capability.ICookingStatus;

public class LivingDethEvent {

	private static final Map<UUID, Inventory> playerKeepsMap = new HashMap<>();

	// 死亡時
	@SubscribeEvent
	public static void onLivingDeathEvent(LivingDeathEvent event) {
		LivingEntity entity = event.getEntity();
		if (entity == null) { return; }

		if (entity.hasEffect(PotionInit.resurrection)) {

			Level world = entity.level;
			entity.setHealth(entity.getMaxHealth() / 2F);
			entity.removeEffect(PotionInit.resurrection);
			world.playSound(null, entity.blockPosition(), SoundEvents.TOTEM_USE, SoundSource.VOICE, 0.5F, 1F);

			if (world instanceof ServerLevel sever) {

				RandomSource rand = world.random;

				for (int i = 0; i < 16; i++) {
					double d0 = rand.nextGaussian() * 0.015D;
					double d1 = rand.nextGaussian() * 0.02D;
					double d2 = rand.nextGaussian() * 0.015D;
					sever.sendParticles(ParticleTypes.HAPPY_VILLAGER, entity.getRandomX(1D), entity.getRandomY() + 0.25D, entity.getRandomZ(1D), 0, d0, d1, d2, 1F);
					sever.sendParticles(ParticleTypes.FIREWORK, entity.getRandomX(1D), entity.getRandomY() + 0.25D, entity.getRandomZ(1D), 0, d0, d1, d2, 1F);
				}
			}

			event.setCanceled(true);
			return;
		}

		if (entity instanceof Player player) {

			Inventory pInv = player.getInventory();
			Inventory keepInv= new Inventory(player);

			int count = -1;
			for (ItemStack stack : pInv.items) {

				// エーテルチャームが付いていなかったら次へ
				count++;
				if (stack.isEmpty() || EnchantmentHelper.getItemEnchantmentLevel(EnchantInit.aethercharm, stack) < 1) { continue; }

				keepInv.items.set(count, stack.copy());
				stack.shrink(stack.getCount());
			}

			count = -1;

			for (ItemStack stack : pInv.armor) {

				// エーテルチャームが付いていなかったら次へ
				count++;
				if (stack.isEmpty() || EnchantmentHelper.getItemEnchantmentLevel(EnchantInit.aethercharm, stack) < 1) { continue; }

				keepInv.armor.set(count, stack.copy());
				stack.shrink(stack.getCount());
			}

			count = -1;

			for (ItemStack stack : pInv.offhand) {

				// エーテルチャームが付いていなかったら次へ
				count++;
				if (stack.isEmpty() || EnchantmentHelper.getItemEnchantmentLevel(EnchantInit.aethercharm, stack) < 1) { continue; }

				keepInv.offhand.set(count, stack.copy());
				stack.shrink(stack.getCount());
			}

			playerKeepsMap.put(player.getUUID(), keepInv);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getEntity();
		if (player == null) { return; }

		Inventory keepInv = playerKeepsMap.remove(player.getUUID());
		if (keepInv  == null) { return; }

		Inventory pInv = player.getInventory();
		NonNullList<ItemStack> items = keepInv.items;
		NonNullList<ItemStack> armor = keepInv.armor;
		NonNullList<ItemStack> offhand = keepInv.offhand;

		for (int i = 0; i < items.size(); i++) {

			ItemStack stack = items.get(i);
			if (stack.isEmpty()) { continue; }

			NonNullList<ItemStack> pList = pInv.items;
			ItemStack pStack = pList.get(i);

			if (!pStack.isEmpty()) {

				Level world = player.level;
				if (world.isClientSide) { return; }

				BlockPos pos = player.blockPosition();
				world.addFreshEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack.copy()));
				continue;
			}

			pList.set(i, stack);
		}

		for (int i = 0; i < armor.size(); i++) {

			ItemStack stack = armor.get(i);
			if (stack.isEmpty()) { continue; }

			NonNullList<ItemStack> pList = pInv.armor;
			ItemStack pStack = pList.get(i);
			if (!pStack.isEmpty()) { continue; }

			pList.set(i, stack);
		}

		for (int i = 0; i < offhand.size(); i++) {

			ItemStack stack = offhand.get(i);
			if (stack.isEmpty()) { continue; }

			NonNullList<ItemStack> pList = pInv.offhand;
			ItemStack pStack = pList.get(i);
			if (!pStack.isEmpty()) { continue; }

			pList.set(i, stack);
		}
	}

	@SubscribeEvent
	public static void onPlayerLogout(PlayerLoggedOutEvent event) {
		Player player = event.getEntity();
		if (player == null) { return; }

		ICookingStatus.getState(player).setHealth(player.getHealth());
		Inventory keepInv = playerKeepsMap.remove(player.getUUID());
		if (keepInv  == null) { return; }

		keepInv.dropAll();
	}

	@SubscribeEvent
	public static void cloneEvent(PlayerEvent.Clone event) {
		Player original = event.getOriginal();
		original.reviveCaps();
		original.getCapability(CapabilityInit.COOK).ifPresent(old -> {
			CompoundTag bags = old.serializeNBT();
			event.getEntity().getCapability(CapabilityInit.COOK).ifPresent(c -> c.deserializeNBT(bags));
		});
	}

	@SubscribeEvent
	public static void loginEvent(PlayerEvent.PlayerLoggedInEvent event) {
		Player player = event.getEntity();
		if (player.isCreative() || player.isSpectator()) { return; }

		ItemStack stack = player.getItemBySlot(EquipmentSlot.FEET);
		CompoundTag tags = player.getPersistentData();

		if (SMLivingTickEvent.isHarness(stack)) {
			player.getAbilities().mayfly = true;

			if (!player.isOnGround()) {
				player.getAbilities().flying = true;
			}

			player.onUpdateAbilities();
			tags.putBoolean("isHarness", true);
		}
	}

	@SubscribeEvent
	public static void onEvent(LivingDropsEvent event) {

		LivingEntity entity = event.getEntity();
		Collection<ItemEntity> itemList = event.getDrops();
		Level world = entity.level;
		RandomSource rand = world.random;
		double x = entity.xo;
		double y = entity.yo;
		double z = entity.zo;

		//ウィッチが不思議なページを落とす
		if (entity instanceof Witch && rand.nextBoolean()) {
			itemList.add(getItem(world, x, y, z, ItemInit.mysterious_page, rand.nextInt(2) + 1));
		}

		// ゾンビなら
		else if (entity instanceof Zombie && rand.nextFloat() <= 0.15F) {
			itemList.add(getItem(world, x, y, z, ItemInit.egg_bag, rand.nextInt(2) + 1));
		}

		// クリーパーなら
		else if (entity instanceof Creeper) {
			itemList.add(getItem(world, x, y, z, ItemInit.magic_meal, rand.nextInt(2) + 1));
		}

		// ニワトリなら
		else if (entity instanceof Chicken) {
			itemList.add(getItem(world, x, y, z, Items.FEATHER, rand.nextInt(3) + 1));
		}

		if (entity.hasEffect(PotionInit.darkness_fog)) {
			itemList.clear();

			if (entity.hasEffect(PotionInit.leader_flag) && entity.getEffect(PotionInit.leader_flag).getAmplifier() + 1 >= 2) {
				itemList.add(getItem(world, x, y, z, ItemInit.evil_arrow, 3));
			}
		}
	}

	// EntityItemで返す
	public static ItemEntity getItem(Level world, double x, double y, double z,Item item, int amount) {
		return new ItemEntity(world, x, y, z, new ItemStack(item, amount));
	}

	@SubscribeEvent
	public static void dropEvent(LivingDropsEvent event) {
		Entity entity = event.getSource().getDirectEntity();
		if ( !(entity instanceof LivingEntity living) || !living.hasEffect(PotionInit.drop_increase)) { return; }

		RandomSource rand = entity.level.random;
		Collection<ItemEntity> entityList = event.getDrops();
		int level = living.getEffect(PotionInit.drop_increase).getAmplifier() + 1;

		for (ItemEntity item : entityList) {
			for (int i = 0; i < level; i++) {
				if (!rand.nextBoolean()) { continue; }

				ItemStack stack = item.getItem();
				if (stack.getCount() >= 64) { continue; }

				stack.setCount(stack.getCount() + 1);
			}
		}
	}

	@SubscribeEvent
	public static void dropExpEvent(LivingExperienceDropEvent event) {
		if (event.getEntity().hasEffect(PotionInit.darkness_fog)) {
			event.setCanceled(true);
		}
	}
}
