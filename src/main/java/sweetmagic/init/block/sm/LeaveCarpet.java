package sweetmagic.init.block.sm;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.iblock.ISMCrop;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.block.base.BaseModelBlock;

public class LeaveCarpet extends BaseModelBlock {

	private final int data;
	public static final IntegerProperty AGE_5 = ISMCrop.AGE5;
	private static final VoxelShape AABB = Block.box(0D, 0D, 0D, 16D, 1D, 16D);

	public LeaveCarpet(String name, int data) {
		super(name, setState(Material.WOOD, SoundType.GRASS, 0.05F, 8192F, data == 0 ? 7 : 0));
		this.registerDefaultState(this.stateDefinition.any().setValue(AGE_5, 0));
		this.data = data;
		BlockInfo.create(this, SweetMagicCore.smTab, name);
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> build) {
		super.createBlockStateDefinition(build);
		build.add(AGE_5);
	}

	@Nullable
	public BlockState getStateForPlacement(BlockPlaceContext con) {
		return this.data == 0 ? super.getStateForPlacement(con) : super.getStateForPlacement(con).setValue(AGE_5, con.getLevel().getRandom().nextInt(6));
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext con) {
		return AABB;
	}
}
