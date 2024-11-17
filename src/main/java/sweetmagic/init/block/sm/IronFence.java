package sweetmagic.init.block.sm;

import java.util.Arrays;
import java.util.List;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.block.base.BaseSMBlock;

public class IronFence extends IronBarsBlock {

	public IronFence(String name, int data) {
		super(BaseSMBlock.setState(getMaterial(data)).sound(getSound(data)).strength(0.5F, 8192F));
		this.registerDefaultState(this.stateDefinition.any().setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false).setValue(WEST, false).setValue(WATERLOGGED, false));
		BlockInfo.create(this, SweetMagicCore.smTab, name);
	}

	public static Material getMaterial(int data) {
		switch (data) {
		case 1:  return Material.WOOD;
		case 2:  return Material.STONE;
		default: return Material.METAL;
		}
	}

	public static SoundType getSound(int data) {
		switch (data) {
		case 1:  return SoundType.WOOD;
		case 2:  return SoundType.STONE;
		default: return SoundType.METAL;
		}
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder build) {
		return Arrays.<ItemStack> asList(new ItemStack(this));
	}
}
