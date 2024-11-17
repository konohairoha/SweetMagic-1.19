package sweetmagic.init.block.sm;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import sweetmagic.init.TileInit;
import sweetmagic.init.tile.sm.TileAbstractSM;
import sweetmagic.init.tile.sm.TileTrashCan;
import sweetmagic.util.FaceAABB;

public class TrashCan extends WoodChest {

	private final static VoxelShape[] AABB = FaceAABB.create(2D, 0D, 10D, 14D, 9.5D, 16D);

	public TrashCan(String name, int data) {
		super(name, data);
	}

	// ブロックでのアクション
	public void actionBlock (Level world, BlockPos pos, Player player, ItemStack stack) {
		if (world.isClientSide) { return; }
		this.openGUI(world, pos, player, (TileTrashCan) this.getTile(world, pos));
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext col) {
		return FaceAABB.getAABB(AABB, state);
	}

	@Override
	public void addBlockTip (List<Component> toolTip) {
		super.addBlockTip(toolTip);
		toolTip.add(this.getText("trash_can").withStyle(GREEN));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileTrashCan(pos, state);
	}

	public BlockEntityType<? extends TileAbstractSM> getTileType () {
		return TileInit.trashCan;
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		BlockEntityType<? extends TileAbstractSM> tileType = this.getTileType();
		return tileType != null ? this.createMailBoxTicker(world, type, tileType) : null;
	}
}
