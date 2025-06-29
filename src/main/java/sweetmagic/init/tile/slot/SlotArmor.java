package sweetmagic.init.tile.slot;

import org.antlr.v4.runtime.misc.NotNull;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class SlotArmor extends Slot {

	private final Entity entity;
	private final EquipmentSlot slot;

	public SlotArmor(Entity entity, EquipmentSlot type, Inventory inv, int index, int x, int y) {
		super(inv, index, x, y);
		this.entity = entity;
		this.slot = type;
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}

	@Override
	public boolean mayPlace(@NotNull ItemStack stack) {
		return stack.canEquip(this.slot, this.entity);
	}

	public boolean mayPickup(Player player) {
		ItemStack stack = this.getItem();
		return !stack.isEmpty() && !player.isCreative() && EnchantmentHelper.hasBindingCurse(stack) ? false : super.mayPickup(player);
	}
}
