package sweetmagic.init.item.sm;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import sweetmagic.init.entity.block.Cushion;

public class SMCushion extends SMItem {

	public SMCushion(String name) {
		super(name);
	}

	public InteractionResult useOn(UseOnContext con) {
		Level world = con.getLevel();
		Vec3 vec = con.getClickLocation();
		Player player = con.getPlayer();
		Cushion cushion = new Cushion(world, new ItemStack(this));
		cushion.setYRot(player.isShiftKeyDown() ? 0F : player.getYRot());
		cushion.setPos(vec.x, vec.y + 0.75D, vec.z);
		world.addFreshEntity(cushion);
		if(!player.isCreative()) { player.getMainHandItem().shrink(1); }
		return InteractionResult.sidedSuccess(world.isClientSide());
	}

	// ツールチップの表示
	public void addTip(ItemStack stack, List<Component> toolTip) {
		toolTip.add(this.getText("cushion").withStyle(GREEN));
		toolTip.add(this.getText("cushion_recovery").withStyle(GREEN));
	}
}
