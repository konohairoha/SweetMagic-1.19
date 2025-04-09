package sweetmagic.init.block.sm;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.ItemHandlerHelper;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.TileInit;
import sweetmagic.init.block.base.BaseFaceBlock;
import sweetmagic.init.tile.sm.TileWandPedastal;
import sweetmagic.util.FaceAABB;

public class WandPedastal extends BaseFaceBlock implements EntityBlock {

	public final int data;
	private static final VoxelShape AABB = Block.box(1.6D, 0D, 1.6D, 14.4D, 9.6D, 14.4D);
	private static final VoxelShape STAND = Block.box(2D, 0D, 2D, 14D, 16D, 14D);
	private static final VoxelShape[] WALL = FaceAABB.create(1D, 1D, 15D, 15D, 15D, 16D);
	private static final VoxelShape[] SHOP = FaceAABB.create(7D, 0D, 0D, 9D, 16D, 16D);

	public WandPedastal(String name, int data) {
		super(name, setState(Material.STONE, SoundType.STONE, 0.5F, 8192F).noCollission());
		this.data = data;
		this.registerDefaultState(this.setState());
		BlockInfo.create(this, SweetMagicCore.smTab, name);
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext con) {
		switch (this.data) {
		case 0: return AABB;
		case 1: return FaceAABB.getAABB(WALL, state);
		case 2: return FaceAABB.getAABB(SHOP, state);
		case 3: return FaceAABB.getAABB(WALL, state);
		case 4: return STAND;
		default: return Shapes.block();
		}
	}

	// 右クリック出来るか
	public boolean canRightClick(Player player, ItemStack stack) {
		return true;
	}

	// ブロックでのアクション
	public boolean actionBlock(Level world, BlockPos pos, Player player, ItemStack stack) {
		if (world.isClientSide) { return true; }
		if (!(this.getTile(world, pos) instanceof TileWandPedastal tile) ) { return false; }

		ItemStack inputStack = tile.getInputItem(0);

		// 完成品を入れる
		if (inputStack.isEmpty()) {
			if (!stack.isEmpty()) {
				ItemStack copy = stack.copy();
				copy.setCount(1);
				ItemHandlerHelper.insertItemStacked(tile.getInput(), copy, false);
				stack.shrink(1);
			}
		}

		else {
			this.spawnItemList(world, player.blockPosition(), Arrays.<ItemStack> asList(inputStack.copy()));
			inputStack.shrink(64);
		}

		this.playerSound(world, pos, SoundEvents.ITEM_PICKUP, 1F, 1F);
		tile.sendPKT();
		return true;
	}

	// tileの中身を保持するか
	public boolean isKeepTile() {
		return true;
	}

	@Override
	public void addBlockTip(List<Component> toolTip) {
		toolTip.add(this.getText("wand_pedastal").withStyle(GREEN));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileWandPedastal(pos, state);
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return this.createMailBoxTicker(world, type, TileInit.wandPedastal);
	}

	// ドロップするかどうか
	protected boolean isDrop() {
		return false;
	}
}
