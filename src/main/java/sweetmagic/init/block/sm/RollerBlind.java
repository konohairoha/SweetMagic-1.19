package sweetmagic.init.block.sm;

import java.util.List;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.block.base.BaseFaceBlock;
import sweetmagic.util.FaceAABB;

public class RollerBlind extends BaseFaceBlock {

	public static final BooleanProperty CLOSE = BooleanProperty.create("close");
	public static final IntegerProperty TYPE = IntegerProperty.create("type", 0, 3);
	private static final VoxelShape[] AABB = FaceAABB.create(0D, 0D, 14.5D, 16D, 16D, 15.5D);

	public RollerBlind(String name) {
		super(name, setState(Material.WOOL, SoundType.WOOL, 0.5F, 8192F).isSuffocating((a, b, c) -> false).isViewBlocking((a, b, c) -> false));
		this.registerDefaultState(this.setState().setValue(TYPE, 0).setValue(CLOSE, false));
		BlockInfo.create(this, SweetMagicCore.smTab, name);
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext con) {
		return FaceAABB.getAABB(AABB, state);
	}

	@Nonnull
	@Override
	public VoxelShape getCollisionShape(BlockState state, @Nonnull BlockGetter world, @Nonnull BlockPos pos, CollisionContext col) {

		if (state.getValue(CLOSE) && col instanceof EntityCollisionContext con && con.getEntity() instanceof Player) {
			return Shapes.empty();
		}

		return super.getCollisionShape(state, world, pos, col);
	}

	// 右クリックしない
	public boolean canRightClick(Player player, ItemStack stack) {
		return true;
	}

	// ブロックでのアクション
	public boolean actionBlock(Level world, BlockPos pos, Player player, ItemStack stack) {

		if (!stack.isEmpty() && stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() == this) {

			BlockState state = world.getBlockState(pos);
			Block block = state.getBlock();

			for (int i = 1; i < 11; i++) {

				BlockPos targetPos = pos.below(i);
				BlockState targetState = world.getBlockState(targetPos);
				Block targetBlock = targetState.getBlock();
				if (targetBlock == this) { continue; }
				if (!targetState.isAir()) { break; }

				world.setBlock(targetPos, state, 3);
				this.blockSound(world, block, targetPos, player);
				if (!player.isCreative()) { stack.shrink(1); }
				return true;
			}
		}

		world.setBlock(pos, world.getBlockState(pos).cycle(CLOSE), 2);
		this.playerSound(world, pos, SoundEvents.WOOL_BREAK, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
		return true;
	}

	@Nullable
	public BlockState getStateForPlacement(BlockPlaceContext con) {

		Level world = con.getLevel();
		BlockPos pos = con.getClickedPos();
		BlockState state = super.getStateForPlacement(con);
		BlockState upState = world.getBlockState(pos.above());
		BlockState downState = world.getBlockState(pos.below());

		if (upState.hasProperty(CLOSE)) {
			state = state.setValue(CLOSE, upState.getValue(CLOSE));
		}

		else if (downState.hasProperty(CLOSE)) {
			state = state.setValue(CLOSE, downState.getValue(CLOSE));
		}

		return this.setVertical(state, world, pos);
	}

	public BlockState updateShape(BlockState state, Direction face, BlockState state2, LevelAccessor world, BlockPos pos1, BlockPos pos2) {

		if (state2.getBlock() == this && state2.hasProperty(CLOSE)) {
			state = state.setValue(CLOSE, state2.getValue(CLOSE));
		}

		return this.setVertical(super.updateShape(state, face, state2, world, pos1, pos2) ,world, pos1);
	}

	public BlockState setVertical(BlockState state, LevelAccessor world, BlockPos pos) {

		BlockState upState = world.getBlockState(pos.above());
		BlockState downState = world.getBlockState(pos.below());
		if (upState.getBlock() != this && downState.getBlock() != this) {
			return state.setValue(TYPE, 0);
		}

		if (upState.getBlock() != this) {
			return state.setValue(TYPE, 1);
		}

		else {
			return state.setValue(TYPE, downState.getBlock() == this ? 2 : 3);
		}
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> build) {
		build.add(TYPE, CLOSE, FACING);
	}

	@Override
	public void addBlockTip(List<Component> toolTip) {
		toolTip.add(this.getText("is_vertical").withStyle(GOLD));
		toolTip.add(this.getText("roller_blind").withStyle(GREEN));
	}
}
