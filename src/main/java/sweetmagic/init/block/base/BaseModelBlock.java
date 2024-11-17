package sweetmagic.init.block.base;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import sweetmagic.init.BlockInit.BlockInfo;

public class BaseModelBlock extends BaseSMBlock {

	public BaseModelBlock(String name, BlockBehaviour.Properties pro) {
		super(name, pro.noOcclusion());
	}

	public BaseModelBlock(String name) {
		super(name, setState(Material.WOOD).sound(SoundType.WOOD).strength(0.5F, 8192F).noOcclusion());
		BlockInfo.create(this, null, name);
	}

	public BaseModelBlock(String name, CreativeModeTab tab) {
		super(name, setState(Material.WOOD).sound(SoundType.WOOD).strength(0.5F, 8192F).noOcclusion());
		BlockInfo.create(this, tab, name);
	}

	public BaseModelBlock(String name, BlockBehaviour.Properties pro, CreativeModeTab tab) {
		super(name, pro.strength(0.5F, 8192F).noOcclusion());
		BlockInfo.create(this, tab, name);
	}

	public BaseModelBlock(String name, BlockBehaviour.Properties pro, CreativeModeTab tab, boolean flag) {
		super(name, pro.noOcclusion());
		BlockInfo.create(this, tab, name);
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

	// ブロック破壊
	public void breakBlock (LevelAccessor world, BlockPos pos) {
		world.destroyBlock(pos, false);
		world.removeBlock(pos, false);
	}
}
