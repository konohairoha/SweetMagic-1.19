package sweetmagic.init.block.sm;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.block.base.BaseModelBlock;

public class LeaveCarpet extends BaseModelBlock {

	private static final VoxelShape AABB = Block.box(0D, 0D, 0D, 16D, 1D, 16D);

	public LeaveCarpet (String name, int data) {
		super(name, setState(Material.WOOD, SoundType.GRASS, 0.05F, 8192F, data == 0 ? 7 : 0));
		BlockInfo.create(this, SweetMagicCore.smTab, name);
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext col) {
		return AABB;
	}
}
