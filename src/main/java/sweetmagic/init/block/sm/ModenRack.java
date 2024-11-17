package sweetmagic.init.block.sm;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.block.base.BaseFaceBlock;
import sweetmagic.init.tile.sm.TilCeilingShelf;
import sweetmagic.init.tile.sm.TileBottleRack;
import sweetmagic.init.tile.sm.TileModenRack;
import sweetmagic.init.tile.sm.TileWallPartition;
import sweetmagic.init.tile.sm.TileWallRack;
import sweetmagic.init.tile.sm.TileWallShelf;
import sweetmagic.util.FaceAABB;

public class ModenRack extends BaseFaceBlock implements EntityBlock {

	public final int data;
	private static final VoxelShape[] WALL_RACK = FaceAABB.create(0D, 15D, 4D, 16D, 16D, 16D);
	private static final VoxelShape[] WALL_SHELF = FaceAABB.create(0D, 0D, 8D, 16D, 16D, 16D);
	private static final VoxelShape[] WALL_PATITION = FaceAABB.create(0D, 0D, 14.5D, 16D, 16D, 16D);
	private static final VoxelShape[] BOTTLE_RACK = FaceAABB.create(0D, 0D, 10D, 16D, 16D, 16D);

	public ModenRack(String name, int data, boolean isIron) {
		super(name, setState(Material.WOOD, isIron ? SoundType.METAL : SoundType.WOOD, 0.5F, 8192F));
		this.data = data;
		this.registerDefaultState(this.setState());
		BlockInfo.create(this, SweetMagicCore.smTab, name);
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext col) {
		switch (this.data) {
		case 1: return FaceAABB.getAABB(WALL_RACK, state);
		case 2: return FaceAABB.getAABB(WALL_SHELF, state);
		case 3: return FaceAABB.getAABB(WALL_PATITION, state);
		case 4: return FaceAABB.getAABB(BOTTLE_RACK, state);
		default: return Shapes.block();
		}
	}

	// 右クリック出来るか
	public boolean canRightClick (Player player, ItemStack stack) {
		return true;
	}

	// ブロックでのアクション
	public void actionBlock (Level world, BlockPos pos, Player player, ItemStack stack) {
		if (world.isClientSide) { return; }

		MenuProvider tile = null;

		switch (this.data) {
		case 0:
			tile = (TileModenRack) this.getTile(world, pos);
			break;
		case 1:
			tile = (TileWallRack) this.getTile(world, pos);
			break;
		case 2:
			tile = (TileWallShelf) this.getTile(world, pos);
			break;
		case 3:
			tile = (TileWallPartition) this.getTile(world, pos);
			break;
		case 4:
			tile = (TileBottleRack) this.getTile(world, pos);
			break;
		case 5:
			tile = (TilCeilingShelf) this.getTile(world, pos);
			break;
		default:
			tile = (TileWallShelf) this.getTile(world, pos);
			break;
		}

		this.openGUI(world, pos, player, tile);
	}

	// tileの中身を保持するか
	public boolean isKeepTile () {
		return true;
	}

	@Override
	public void addBlockTip (List<Component> toolTip) {
		toolTip.add(this.getText("sm_chest").withStyle(GREEN));

		if (this.data == 5) {
			toolTip.add(this.getText(this.name).withStyle(GREEN));
		}
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		switch(this.data) {
		case 0:  return new TileModenRack(pos, state);
		case 1:  return new TileWallRack(pos, state);
		case 2:  return new TileWallShelf(pos, state);
		case 3:  return new TileWallPartition(pos, state);
		case 4:  return new TileBottleRack(pos, state);
		case 5:  return new TilCeilingShelf(pos, state);
		default: return new TileModenRack(pos, state);
		}
	}

	// ドロップするかどうか
	protected boolean isDrop () {
		return false;
	}
}