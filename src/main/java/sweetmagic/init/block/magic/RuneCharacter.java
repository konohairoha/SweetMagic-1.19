package sweetmagic.init.block.magic;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.block.base.BaseFaceBlock;

public class RuneCharacter extends BaseFaceBlock {

	public static final IntegerProperty SIZE = IntegerProperty.create("size", 0, 7);
	private static final VoxelShape AABB = Block.box(1D, 0D, 1D, 15D, 0.1D, 15D);

	public RuneCharacter(String name) {
		super(name, setState(Material.SAND, SoundType.SAND, 0.5F, 8192F));
		this.registerDefaultState(this.setState().setValue(SIZE, 0));
		BlockInfo.create(this, SweetMagicCore.smMagicTab, name);
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext col) {
		return AABB;
	}

	// 右クリック出来るか
	public boolean canRightClick (Player player, ItemStack stack) {
		return true;
	}

	// ブロックでのアクション
	public InteractionResult onUse(Level world, BlockPos pos, Player player, InteractionHand hand) {
		if (player == null || !player.getMainHandItem().isEmpty()) { return InteractionResult.PASS; }

		BlockState state = world.getBlockState(pos);
		int size = state.getValue(SIZE) + 1;
		if (size > 7) { size = 0; }

		world.setBlock(pos, state.setValue(SIZE, size), 3);
		this.playerSound(world, pos, SoundEvents.SAND_PLACE, 0.25F, world.random.nextFloat() * 0.1F + 1.2F);
		return super.onUse(world, pos, player, hand);
	}

	@Nullable
	public BlockState getStateForPlacement(BlockPlaceContext con) {
		return super.getStateForPlacement(con).setValue(SIZE, con.getLevel().random.nextInt(8));
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> build) {
		build.add(FACING, SIZE);
	}
}
