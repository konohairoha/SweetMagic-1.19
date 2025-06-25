package sweetmagic.init.block.sm;

import java.util.Arrays;
import java.util.List;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.block.base.BaseSMBlock;

public class SMPressurePlate extends PressurePlateBlock {

	public SMPressurePlate(String name, int data) {
		super(data == 1 ? Sensitivity.MOBS : Sensitivity.EVERYTHING, BaseSMBlock.setState(data == 1 ? Material.STONE : Material.WOOD, data == 1 ? SoundType.STONE : SoundType.WOOD, 0.5F, 8192F));
		BlockInfo.create(this, SweetMagicCore.smTab, name);
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder build) {
		return Arrays.<ItemStack> asList(new ItemStack(this));
	}
}
