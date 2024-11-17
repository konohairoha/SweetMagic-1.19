package sweetmagic.init.block.sm;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.ItemInit;
import sweetmagic.init.block.base.BaseSMBlock;

public class SMDoor extends DoorBlock {

	private final int data;

	public SMDoor (String name, int data) {
		super(BaseSMBlock.setState(Material.WOOD, SoundType.WOOD, 0.35F, 8192F));
		this.data = data;
		BlockInfo.create(this, null, name);
	}

	public ItemLike getItem () {
		switch (this.data) {
		case 1: return  ItemInit.pane4_door_i;
		case 2: return ItemInit.elegant_door_i;
		case 3: return ItemInit.arch_door_i;
		case 4: return ItemInit.arch_plant_door_i;
		case 5: return ItemInit.simple_door_i;
		case 6: return ItemInit.simple_net_door_i;
		case 7: return ItemInit.frosted_glass_moden_door_t_i;
		case 8: return ItemInit.frosted_glass_moden_door_b_i;
		case 9: return ItemInit.frosted_glass_moden_door_d_i;
		case 10: return ItemInit.large_frosted_glass_moden_door_t_i;
		case 11: return ItemInit.large_frosted_glass_moden_door_b_i;
		case 12: return ItemInit.large_frosted_glass_moden_door_d_i;
		default : return ItemInit.pane2_door_i;
		}
	}

	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	public VoxelShape getVisualShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext con) {
		return Shapes.empty();
	}

	@Override
	public float getShadeBrightness(BlockState state, BlockGetter get, BlockPos pos) {
		return 1F;
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter get, BlockPos pos) {
		return true;
	}

	@Override
	public ItemStack getCloneItemStack(BlockGetter get, BlockPos pos, BlockState state) {
		return new ItemStack(this.getItem());
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder build) {
		List<ItemStack> stackList = new ArrayList<>();

		if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
			stackList.add(new ItemStack(this.getItem()));
		}

		return stackList;
	}
}
