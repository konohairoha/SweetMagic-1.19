package sweetmagic.init.block.sm;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
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
import sweetmagic.init.tile.sm.TileBottleRack;
import sweetmagic.init.tile.sm.TileCeilingShelf;
import sweetmagic.init.tile.sm.TileFruitCrate;
import sweetmagic.init.tile.sm.TileModenRack;
import sweetmagic.init.tile.sm.TileWallPartition;
import sweetmagic.init.tile.sm.TileWallRack;
import sweetmagic.init.tile.sm.TileWallShelf;
import sweetmagic.init.tile.sm.TileWoodenToolBox;
import sweetmagic.util.FaceAABB;

public class ModenRack extends BaseFaceBlock implements EntityBlock {

	public final int data;
	private static final VoxelShape[] WALL_RACK = FaceAABB.create(0D, 15D, 4D, 16D, 16D, 16D);
	private static final VoxelShape[] WALL_SHELF = FaceAABB.create(0D, 0D, 8D, 16D, 16D, 16D);
	private static final VoxelShape[] WALL_PATITION = FaceAABB.create(0D, 0D, 14.5D, 16D, 16D, 16D);
	private static final VoxelShape[] BOTTLE_RACK = FaceAABB.create(0D, 0D, 10D, 16D, 16D, 16D);
	private static final VoxelShape[] FRUIT_CREATE = FaceAABB.create(1D, 0D, -3.25D, 15D, 13.5D, 15D);
	private static final VoxelShape[] FRUIT_CREATE_BOX = FaceAABB.create(2D, 0D, 1D, 14D, 6.5D, 15D);
	private static final VoxelShape[] BOX = FaceAABB.create(0D, 0D, 8D, 16D, 16D, 16D);

	public ModenRack(String name, int data, boolean isIron) {
		super(name, setState(Material.WOOD, isIron ? SoundType.METAL : SoundType.WOOD, 0.5F, 8192F));
		this.data = data;
		this.registerDefaultState(this.setState());
		BlockInfo.create(this, SweetMagicCore.smTab, name);
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext con) {
		switch (this.data) {
		case 1: return FaceAABB.getAABB(WALL_RACK, state);
		case 2: return FaceAABB.getAABB(WALL_SHELF, state);
		case 3: return FaceAABB.getAABB(WALL_PATITION, state);
		case 4: return FaceAABB.getAABB(BOTTLE_RACK, state);
		case 6: return FaceAABB.getAABB(FRUIT_CREATE, state);
		case 7: return FaceAABB.getAABB(FRUIT_CREATE_BOX, state);
		case 9: return FaceAABB.getAABB(BOX, state);
		default: return Shapes.block();
		}
	}

	// 右クリック出来るか
	public boolean canRightClick(Player player, ItemStack stack) {
		return true;
	}

	// ブロックでのアクション
	public boolean actionBlock(Level world, BlockPos pos, Player player, ItemStack stack) {
		if (world.isClientSide()) { return true; }
		this.openGUI(world, pos, player, this.getTile(world, pos));
		return true;
	}

	// tileの中身を保持するか
	public boolean isKeepTile() {
		return true;
	}

	@Override
	public void addBlockTip(List<Component> toolTip) {
		toolTip.add(this.getText("sm_chest").withStyle(GREEN));

		if (this.data == 5) {
			toolTip.add(this.getText(this.name).withStyle(GREEN));
		}
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		switch(this.data) {
		case 1:  return new TileWallRack(pos, state);
		case 2:  return new TileWallShelf(pos, state);
		case 3:  return new TileWallPartition(pos, state);
		case 4:  return new TileBottleRack(pos, state);
		case 5:  return new TileCeilingShelf(pos, state);
		case 6:  return new TileFruitCrate(pos, state);
		case 7:  return new TileFruitCrate(pos, state);
		case 8:  return new TileWoodenToolBox(pos, state);
		case 9:  return new TileWallRack(pos, state);
		default: return new TileModenRack(pos, state);
		}
	}

	// ドロップするかどうか
	protected boolean isDrop() {
		return false;
	}
}
