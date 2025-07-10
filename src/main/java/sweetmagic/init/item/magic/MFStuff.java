package sweetmagic.init.item.magic;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.item.sm.SMItem;

public class MFStuff extends SMItem {

	public MFStuff(String name) {
		super(name, setItem(SweetMagicCore.smMagicTab));
	}

	// エンチャント表示をしない
	public boolean isFoil(ItemStack stack) {
		CompoundTag tag = stack.getTag();
		return tag != null && tag.contains("X");
	}

	@Override
	public InteractionResult useOn(UseOnContext con) {
		Player player = con.getPlayer();
		if (player.isShiftKeyDown()) { return InteractionResult.PASS; }

		ItemStack stack = con.getItemInHand();
		CompoundTag tag = stack.getTag();
		if (tag == null || !tag.contains("X")) { return InteractionResult.PASS; }

		if (!player.getLevel().isClientSide()) {
			player.sendSystemMessage(this.getText("posremo").withStyle(RED));
		}

		else {
			player.playSound(SoundEvents.ENDERMAN_TELEPORT, 1F, 1F);
		}

		tag.remove("tick");
		tag.remove("X");
		tag.remove("Y");
		tag.remove("Z");

		return InteractionResult.sidedSuccess(player.getLevel().isClientSide());
	}

	// ツールチップの表示
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> toolTip, TooltipFlag flag) {

		CompoundTag tag = stack.getTag();
		toolTip.add(this.getText(this.name).withStyle(RED));

		if (tag != null && tag.contains("X")) {

			int x = tag.getInt("X");
			int y = tag.getInt("Y");
			int z = tag.getInt("Z");
			String pos = ": " + x + ", " + y + ", " + z;
			toolTip.add(this.getTipArray(this.getText("regi_pos"), this.getLabel(pos, WHITE)).withStyle(GREEN));

			MutableComponent block = world.getBlockState(new BlockPos(x, y, z)).getBlock().getName().withStyle(WHITE);
			toolTip.add(this.getTipArray(this.getText("regi_block"), ": ", block).withStyle(GREEN));
		}
	}
}
