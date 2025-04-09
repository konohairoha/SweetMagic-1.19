package sweetmagic.init.tile.slot;

import java.util.function.Predicate;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import sweetmagic.api.iitem.IFood;
import sweetmagic.api.iitem.IPorch;
import sweetmagic.api.iitem.info.FoodInfo;
import sweetmagic.init.ItemInit;
import sweetmagic.init.capability.ICookingStatus;

public class CookedItemSlot extends SMSlot {

	private int removeCount = 0;
	private final Player player;

	public CookedItemSlot(Player player, IItemHandler handler, int index, int x, int y, Predicate<ItemStack> pre) {
		super(handler, index, x, y, pre);
		this.player = player;
	}

	public ItemStack remove(int count) {
		if (this.hasItem()) {
			this.removeCount = Math.min(count, this.getItem().getCount());
		}

		return super.remove(count);
	}

	@Override
	public void onTake(Player player, ItemStack stack) {
		this.getExp(stack, stack.getCount());
		super.onTake(player, stack);
	}

	public void onQuickCraft(ItemStack stack, int count) {
		this.removeCount = count;
		this.getExp(stack, this.removeCount);
	}

	public void getExp(ItemStack stack, int count) {
		Item item = stack.getItem();
		if (!item.isEdible() || this.player == null) { return; }

		if (item instanceof IFood food) {
			IFood.getExpValue(this.player, new FoodInfo(stack), count);
			ICookingStatus.sendPKT(this.player);
		}

		ItemStack leg = this.player.getItemBySlot(EquipmentSlot.LEGS);
		if (leg.isEmpty() || !(leg.getItem() instanceof IPorch porch) || !porch.hasAcce(leg, ItemInit.mysterious_fork)) { return; }

		FoodProperties food = item.getFoodProperties();
		float amount = Math.max(food.getNutrition(), 1F) * Math.max(food.getSaturationModifier(), 0.5F) * count;
		int xp = (int) (Math.max(1, amount));
		Level world = this.player.level;

		if (!world.isClientSide) {
			int cookLevel = ICookingStatus.getState(this.player).getLevel();
			float rate = 1F + cookLevel * 0.05F;

			ExperienceOrb entity = new ExperienceOrb(world, this.player.getX(), this.player.getY(), this.player.getZ(), (int) (xp * rate));
			world.addFreshEntity(entity);
		}
	}
}
