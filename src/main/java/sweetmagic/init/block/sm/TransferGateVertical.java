package sweetmagic.init.block.sm;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ForgeHooks;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.TileInit;
import sweetmagic.init.block.base.BaseModelBlock;
import sweetmagic.init.tile.sm.TileAbstractSM;
import sweetmagic.init.tile.sm.TileStove;
import sweetmagic.init.tile.sm.TileTransferGateVertical;

public class TransferGateVertical extends BaseModelBlock implements EntityBlock {

	private final int data;
	private int tickTime = 25;
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	private final static VoxelShape AABB = Block.box(-4D, 0D, -4D, 20D, 16D, 20D);
	public static final BooleanProperty BREAK = BooleanProperty.create("break");

	public TransferGateVertical(String name, int data) {
		super(name, setState(Material.METAL, SoundType.METAL, -1F, 8192F));
		this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.UP).setValue(BREAK, false));
		this.data = data;
		BlockInfo.create(this, data == 0 ? SweetMagicCore.smMagicTab : null, name);
	}

	public float getDestroyProgress(BlockState state, Player player, BlockGetter world, BlockPos pos) {
		if (!state.getValue(BREAK)) { return 0F; }

		int i = ForgeHooks.isCorrectToolForDrops(state, player) ? 30 : 100;
		return player.getDigSpeed(state, pos) / 1 / (float) i;
	}

	@Override
	protected void createBlockStateDefinition(@NotNull StateDefinition.Builder<Block, BlockState> build) {
		build.add(FACING, BREAK);
	}

	// 右クリック出来るか
	public boolean canRightClick(Player player, ItemStack stack) {
		return true;
	}

	// ブロックでのアクション
	public boolean actionBlock(Level world, BlockPos pos, Player player, ItemStack stack) {
		if (world.isClientSide) { return true; }

		boolean isBreak = world.getBlockState(pos).getValue(BREAK);
		if (!isBreak) { return false; }

		pos = this.data == 1 ? pos : pos.above();
		this.openGUI(world, pos, player, (TileTransferGateVertical) this.getTile(world, pos));
		return true;
	}

	@NotNull
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext con) {
		Player player = con.getPlayer();
		Direction face = con.getClickedFace().getOpposite();
		return face == Direction.DOWN || !player.isCreative() || !player.isShiftKeyDown() ? this.defaultBlockState().setValue(FACING, Direction.DOWN).setValue(BREAK, true) : this.defaultBlockState();
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext con) {
		return AABB;
	}

	// ドロップするかどうか
	protected boolean isDrop() {
		return false;
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public void addBlockTip(List<Component> toolTip) {
		toolTip.add(this.getText(this.name).withStyle(GREEN));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return this.data == 1 ? new TileTransferGateVertical(pos, state) : new TileStove(pos, state);
	}

	public BlockEntityType<? extends TileAbstractSM> getTileType() {
		return this.data == 1 ? TileInit.transferGateVertical : null;
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return this.createMailBoxTicker(world, type, this.getTileType());
	}

	public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
		if (world.isClientSide || this.tickTime++ % 30 != 0 || !(entity instanceof LivingEntity living)) { return; }

		if (state.getValue(BREAK) && this.data == 0) {

			TileTransferGateVertical tile = (TileTransferGateVertical) this.getTile(world, pos.above());
			if (tile.doTereport(living)) {
				this.tickTime = 25;
			}
			return;
		}

		if (this.data == 0) { return; }

		Direction face = state.getValue(FACING);
		for (int i = 1; i < 512; i++) {
			BlockPos newPos = pos;

			switch(face) {
			case UP:
				newPos = pos.above(i);
				break;
			case DOWN:
				newPos = pos.below(i);
				break;
			}

			BlockState targetState = world.getBlockState(newPos);
			if (!targetState.is(BlockInit.transfer_gate_vertical_top) || targetState.getValue(FACING) == face) { continue; }

			DirectionProperty pro = HorizontalDirectionalBlock.FACING;
			BlockState underState = world.getBlockState(newPos.below(2));
			Direction face2 = underState.hasProperty(pro) ? underState.getValue(pro) : Direction.NORTH;

			switch(face2) {
			case NORTH:
				newPos = newPos.south(2);
				break;
			case SOUTH:
				newPos = newPos.north(2);
				break;
			case WEST:
				newPos = newPos.east(2);
				break;
			case EAST:
				newPos = newPos.west(2);
				break;
			}

			entity.teleportTo(newPos.getX() + 0.5D, newPos.getY() + 0.5D, newPos.getZ() + 0.5D);
			this.playerSound(world, newPos, SoundEvents.ENDERMAN_TELEPORT, 1F, 1F);
			this.tickTime = 25;
			break;
		}
	}

	@Nonnull
	@Override
	public VoxelShape getCollisionShape(BlockState state, @Nonnull BlockGetter world, @Nonnull BlockPos pos, CollisionContext con) {
		return Shapes.empty();
	}

	@Override
	public ItemStack getCloneItemStack(BlockGetter get, BlockPos pos, BlockState state) {
		return new ItemStack(BlockInit.transfer_gate_vertical);
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		world.setBlock(pos.above(), BlockInit.transfer_gate_vertical_top.defaultBlockState().setValue(FACING, state.getValue(FACING)).setValue(BREAK, state.getValue(BREAK)), 3);
	}

	public void onRemove(Level world, BlockPos pos, BlockState state, TileAbstractSM tile) {
		if (this.data == 1) { return; }
		this.spawnItem(world, pos, tile.getDropStack(new ItemStack(this)));
	}

	@Override
	@Deprecated
	public void onRemove(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {

		// ブロックの状態が変わった場合
		if (state.getBlock() != newState.getBlock() && !world.isClientSide) {
			BlockPos targetPos = this.data == 0 ? pos.above() : pos.below();
			this.breakBlock(world, targetPos);
		}

		super.onRemove(state, world, pos, newState, isMoving);
	}
}
