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

public class FaceWood extends BaseFaceBlock {

	private final static VoxelShape BASE = Block.box(0.5D, 0D, 0.25D, 15.25D, 14.5D, 15.25D);
	private final static VoxelShape BUCKET = Block.box(3D, 0D, 3D, 13D, 10D, 13D);
	private final static VoxelShape BREAD = Block.box(3D, 0D, 3D, 13D, 10D, 13D);
	private static final VoxelShape[] BOOK = FaceAABB.create(3D, 0D, 2D, 11D, 10D, 14D);
	private static final VoxelShape[] BOOKS = FaceAABB.create(0.5D, 0D, 1D, 14.5D, 12D, 9D);
	private static final VoxelShape[] STICK = FaceAABB.create(6D, 5D, 5D, 10D, 16D, 16D);
	private final static VoxelShape[] STAND = FaceAABB.create(0D, 0D, 2D, 16D, 16D, 16D);

	private final int data;

	public FaceWood(String name, int data) {
		super(name, setState(Material.WOOD, SoundType.WOOD, 0.25F, 8192F));
		this.registerDefaultState(this.setState());
		this.data = data;
		BlockInfo.create(this, SweetMagicCore.smTab, name);
	}

	public FaceWood(String name, int data, boolean flag) {
		super(name, setState(Material.WOOD, SoundType.WOOD, 0.5F, 8192F));
		this.registerDefaultState(this.setState());
		this.data = data;
		BlockInfo.create(this, null, name);
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext col) {
		switch (this.data) {
		case 0: return BASE;
		case 1: return BUCKET;
		case 2: return BREAD;
		case 3: return FaceAABB.getAABB(BOOKS, state);
		case 4: return FaceAABB.getAABB(BOOK, state);
		case 5: return FaceAABB.getAABB(STICK, state);
		case 6: return FaceAABB.getAABB(STAND, state);
		default: return BASE;
		}
	}
}
