package sweetmagic.init.tile.inventory;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class SMTrunkCaseInventory extends BaseSMInventory {

	public SMTrunkCaseInventory(ItemStack stack) {
		super(stack);
		this.setInv(new ItemStackHandler(104));

		if (!stack.getOrCreateTag().contains("BlockEntityTag")) {
			stack.getTag().put("BlockEntityTag", new CompoundTag());
		}

		this.readFromNBT(stack.getTagElement("BlockEntityTag"));
	}

	public CompoundTag getTag () {
		return this.getStack().getTagElement("BlockEntityTag");
	}

	@Override
	public void readFromNBT(CompoundTag nbt) {
		this.getInv().deserializeNBT(nbt);
	}

	@Override
	public void writeToNBT(CompoundTag tags) {
		tags.merge(this.getInv().serializeNBT());
	}
}
