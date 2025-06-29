package sweetmagic.init.tile.slot;

import java.util.function.Predicate;

import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import sweetmagic.init.tile.sm.TileMFFurnace;

public class FurnaceSlot extends SMSlot {

	private final Player player;
	private final TileMFFurnace tile;

	public FurnaceSlot(TileMFFurnace tile, Player player, IItemHandler handler, int index, int x, int y, Predicate<ItemStack> pre) {
		super(handler, index, x, y, pre);
		this.tile = tile;
		this.player = player;
	}

	@Override
	public void onTake(Player player, ItemStack stack) {
		this.getExp(stack);
		super.onTake(player, stack);
	}

	protected void onQuickCraft(ItemStack stack, int count) {
		this.getExp(stack);
	}

	public void getExp(ItemStack stack) {
		if (stack.isEmpty() || this.tile.exp <= 0F) { return; }

		Level world = this.player.level;
		if (world.isClientSide()) { return; }

		ExperienceOrb entity = new ExperienceOrb(world, this.player.getX(), this.player.getY(), this.player.getZ(), (int) this.tile.exp);
		world.addFreshEntity(entity);
		this.tile.exp = 0F;
		this.tile.sendPKT();
	}
}
