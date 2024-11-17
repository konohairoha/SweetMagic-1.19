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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.TileInit;
import sweetmagic.init.block.base.BaseFaceBlock;
import sweetmagic.init.tile.sm.TileAbstractSM;
import sweetmagic.init.tile.sm.TileStove;
import sweetmagic.init.tile.sm.TileTransferGate;
import sweetmagic.util.FaceAABB;

public class TransferGate extends BaseFaceBlock implements EntityBlock {

	private final int data;
	private int tickTime = 25;
	private final static VoxelShape[] AABB = FaceAABB.create(-6D, 0D, -4D, 22D, 2D, 20D);
	private final static VoxelShape[] TOP = FaceAABB.create(-14D, -14D, 7D, 30D, 30D, 9D);

	public TransferGate(String name, int data) {
		super(name, setState(Material.METAL, SoundType.METAL, -1F, 8192F));
		this.data = data;
		BlockInfo.create(this, data == 0 ? SweetMagicCore.smMagicTab : null, name);
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext col) {
		return this.data == 0 ? FaceAABB.getAABB(AABB, state) : FaceAABB.getAABB(TOP, state);
	}

	// ドロップするかどうか
	protected boolean isDrop () {
		return false;
	}

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

	@Override
	public void addBlockTip (List<Component> toolTip) {
		toolTip.add(this.getText("dungen_only").withStyle(GREEN));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return this.data == 1 ? new TileTransferGate(pos, state) : new TileStove(pos, state);
	}

	public BlockEntityType<? extends TileAbstractSM> getTileType () {
		return this.data == 1 ? TileInit.transferGate : null;
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		BlockEntityType<? extends TileAbstractSM> tileType = this.getTileType();
		return tileType != null ? this.createMailBoxTicker(world, type, tileType) : null;
	}

	public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
		if (this.data == 0 || world.isClientSide || this.tickTime++ % 30 != 0 || !(entity instanceof LivingEntity)) { return; }

		Direction face = state.getValue(FACING);
		for (int i = 1; i < 512; i++) {
			BlockPos newPos = pos;

			switch(face) {
			case NORTH:
				newPos = pos.south(i);
				break;
			case SOUTH:
				newPos = pos.north(i);
				break;
			case WEST:
				newPos = pos.east(i);
				break;
			case EAST:
				newPos = pos.west(i);
				break;
			}

			BlockState targetState = world.getBlockState(newPos);
			if (!targetState.is(BlockInit.transfer_gate_top) || targetState.getValue(FACING) != face.getClockWise().getClockWise()) { continue; }

			switch(face) {
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
	public VoxelShape getCollisionShape(BlockState state, @Nonnull BlockGetter world, @Nonnull BlockPos pos, CollisionContext col) {
		return Shapes.empty();
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		world.setBlock(pos.above(), BlockInit.transfer_gate_top.defaultBlockState().setValue(FACING, state.getValue(FACING)), 3);
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
