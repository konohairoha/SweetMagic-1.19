package sweetmagic.init.block.sm;

import java.util.Arrays;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.iblock.IFoodExpBlock;
import sweetmagic.init.BlockInit;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.block.base.BaseFaceBlock;

public class RangeFood extends BaseFaceBlock implements IFoodExpBlock {

	private final int data;

	public RangeFood(String name, int data) {
		super(name, setState(Material.WOOD, SoundType.METAL, 0.25F, 8192F, data == 0 ? 0 : 10));
		this.data = data;
		BlockInfo.create(this, data == 1 ? null : SweetMagicCore.smTab, name);
	}

	// 右クリック出来るか
	public boolean canRightClick(Player player, ItemStack stack) {
		return player != null;
	}

	// ブロックでのアクション
	public boolean actionBlock(Level world, BlockPos pos, Player player, ItemStack stack) {

		Block block = null;
		switch (this.data) {
		case 0:
			block = BlockInit.range_food_light;
			break;
		case 1:
			block = BlockInit.range_food;
			break;
		}

		world.setBlock(pos, block.defaultBlockState().setValue(FACING, world.getBlockState(pos).getValue(FACING)), 3);
		this.playerSound(world, pos, SoundEvents.UI_BUTTON_CLICK, 0.25F, world.getRandom().nextFloat() * 0.1F + 1.2F);
		return true;
	}

	@Override
	public void addBlockTip(List<Component> toolTip) {
		toolTip.add(this.getText("right_change").withStyle(GREEN));
	}

	public ItemStack getCloneItemStack(BlockGetter get, BlockPos pos, BlockState state) {
		return new ItemStack(BlockInit.range_food);
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder build) {
		return Arrays.<ItemStack> asList(new ItemStack(BlockInit.range_food));
	}
}
