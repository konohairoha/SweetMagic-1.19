package sweetmagic.init.block.sm;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.util.EnumVertical;
import sweetmagic.init.BlockInit;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.block.base.BaseModelBlock;

public class Pole extends BaseModelBlock {

	private static final VoxelShape AABB = Block.box(5D, 0D, 5D, 11D, 16D, 11D);
	public static final BooleanProperty ISDROP = BooleanProperty.create("isdrop");
	public static final EnumProperty<EnumVertical> VERTICAL = EnumProperty.create("vertical", EnumVertical.class);

	public Pole(String name) {
		super(name, setState(Material.WOOD, SoundType.METAL, 0.5F, 8192F, 15));
		this.registerDefaultState(this.defaultBlockState().setValue(VERTICAL, EnumVertical.BOT).setValue(ISDROP, false));
		BlockInfo.create(this, SweetMagicCore.smTab, name);
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext col) {
		return AABB;
	}

	// 右クリック出来るか
	public boolean canRightClick(Player player, ItemStack stack) {
		return !stack.isEmpty();
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader reader, BlockPos pos) {

		boolean canPlace = false;

		for (int i = 0; i < 5; i++) {
			canPlace = reader.getBlockState(pos.above(i)).isAir();
			if(!canPlace) { break; }
		}

		return canPlace;
	}

	// ブロックでのアクション
	public boolean actionBlock(Level world, BlockPos pos, Player player, ItemStack stack) {
		if (!(stack.getItem() instanceof BlockItem blockItem)) { return false; }

		Block block = blockItem.getBlock();
		if (!(block instanceof Pole)) { return false; }

		if (world.isClientSide) { return true; }
		int addY = 0;

		for (int i = 0; i < 64; i++) {

			if (!world.getBlockState(pos.above(i + 1)).isAir()) { continue; }

			for (int y = 0; y < 5; y++) {
				if (!world.getBlockState(pos.above(i + y + 1)).isAir()) { return false; }
			}

			addY = i;
			break;
		}

		for (int k = 0; k < 5; k++) {

			int data = 0;
			boolean isDrop = false;

			switch (k) {
			case 0:
				data = 0;
				isDrop = true;
				break;
			case 3:
				data = 2;
				break;
			default:
				data = 1;
				break;
			}

			BlockPos p = pos.above(addY + k + 1);
			world.setBlockAndUpdate(p, BlockInit.pole.defaultBlockState().setValue(VERTICAL, EnumVertical.getLocalList().get(data)).setValue(ISDROP, isDrop));
			SoundType sound = this.getSoundType(block.defaultBlockState(), world, pos, player);
			this.playerSound(world, p, sound.getPlaceSound(), (sound.getVolume() + 1F) / 2F, sound.getPitch() * 0.8F);
		}

		if (!player.isCreative()) { stack.shrink(1); }
		return true;
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {

		for (int i = 0; i < 4; i++) {

			int data = 0;

			switch (i) {
			case 3:
				data = 2;
				break;
			default:
				data = 1;
				break;
			}

			world.setBlockAndUpdate(pos.above(i + 1), BlockInit.pole.defaultBlockState().setValue(VERTICAL, EnumVertical.getLocalList().get(data)));
		}
	}

	@Override
	@Deprecated
	public void onRemove(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {

		// ドロップするブロックが破壊されたらアイテムドロップ
		if (state.getValue(ISDROP) && newState.isAir()) {
			ItemEntity entity = new ItemEntity(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, new ItemStack(this));
			entity.setDefaultPickUpDelay();
			world.addFreshEntity(entity);
		}

		// ブロックの状態が変わった場合
		if (state.getBlock() != newState.getBlock() && !world.isClientSide) {

			BlockPos upPos = pos.above(1);
			BlockState upState = world.getBlockState(upPos);
			if (upState.getBlock() instanceof Pole pole) {
				this.breakBlock(world, upPos);
			}

			BlockPos downPos = pos.below(1);
			BlockState downState = world.getBlockState(downPos);
			if (downState.getBlock() instanceof Pole pole) {
				this.breakBlock(world, downPos);
			}
		}
	}

	@Nullable
	public BlockState getStateForPlacement(BlockPlaceContext con) {
		return this.setVertical(con.getLevel(), con.getClickedPos());
	}

	public BlockState updateShape(BlockState state, Direction face, BlockState state2, LevelAccessor world, BlockPos pos1, BlockPos pos2) {
		return this.setVertical(world, state, pos1);
	}

	public BlockState setVertical(LevelAccessor world, BlockPos pos) {
		return this.defaultBlockState().setValue(VERTICAL, EnumVertical.BOT).setValue(ISDROP, true);
	}

	public BlockState setVertical(LevelAccessor world, BlockState state, BlockPos pos) {
		boolean bot = this.getBlock(world, pos.below()) == this;
		boolean top = this.getBlock(world, pos.above()) == this;
		return state.setValue(VERTICAL, EnumVertical.getVertical(bot, top)).setValue(ISDROP, state.getValue(ISDROP));
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> build) {
		build.add(VERTICAL, ISDROP);
	}

	// ドロップするかどうか
	protected boolean isDrop() {
		return false;
	}
}
