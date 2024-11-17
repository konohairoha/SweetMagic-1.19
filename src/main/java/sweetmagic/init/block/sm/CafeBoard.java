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
import sweetmagic.init.block.base.BaseFaceBlock;
import sweetmagic.util.FaceAABB;

public class CafeBoard extends BaseFaceBlock {

	private final static VoxelShape BASE = Block.box(0.25D, 0D, 0.25D, 0.75D, 0.65D, 0.75D);
	private static final VoxelShape[] AABB = FaceAABB.create(2D, 0D, 1D, 14D, 13.5D, 15D);
	private static final VoxelShape[] WALL = FaceAABB.create(1D, 1D, 15D, 15D, 15D, 16D);
	private final int data;

	public CafeBoard(String name, int data) {
		super(name, setState(Material.WOOD, SoundType.WOOD, 0.1F, 8192F));
		this.registerDefaultState(this.setState());
		this.data = data;
		BlockInfo.create(this, SweetMagicCore.smTab, name);
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext col) {
		switch (this.data) {
		case 0: return FaceAABB.getAABB(AABB, state);
		case 1: return FaceAABB.getAABB(WALL, state);
		default: return BASE;
		}
	}
}
