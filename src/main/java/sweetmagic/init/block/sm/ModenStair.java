package sweetmagic.init.block.sm;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.block.base.BaseFaceBlock;

public class ModenStair extends BaseFaceBlock {

	private static final VoxelShape BOT = Block.box(0D, 0D, 0D, 16D, 8D, 16D);
	private static final VoxelShape AABB_NORTH = Shapes.or(Block.box(0D, 8D, 8D, 16D, 16D, 16D), BOT);
	private static final VoxelShape AABB_SOUTH = Shapes.or(BOT, Block.box(0D, 8D, 0D, 16D, 16D, 8D));
	private static final VoxelShape AABB_WEST = Shapes.or(BOT, Block.box(8D, 8D, 0D, 16D, 16D, 16D));
	private static final VoxelShape AABB_EAST = Shapes.or(BOT, Block.box(0D, 8D, 0D, 8D, 16D, 16D));

	public ModenStair(String name) {
		super(name, setState(Material.WOOD, SoundType.WOOD, 0.5F, 8192F));
		BlockInfo.create(this, SweetMagicCore.smTab, name);
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext con) {
		switch(state.getValue(FACING)) {
		case WEST: return AABB_WEST;
		case EAST: return AABB_EAST;
		case SOUTH: return AABB_SOUTH;
		default: return AABB_NORTH;
		}
	}
}
