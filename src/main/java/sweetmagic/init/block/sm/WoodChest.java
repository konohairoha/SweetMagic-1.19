package sweetmagic.init.block.sm;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.iblock.IFoodExpBlock;
import sweetmagic.api.iblock.IWaterBlock;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.ItemInit;
import sweetmagic.init.block.base.BaseFaceBlock;
import sweetmagic.init.tile.menu.container.ContainerWoodChest;
import sweetmagic.init.tile.sm.TileAbstractSM;
import sweetmagic.init.tile.sm.TileWoodChest;
import sweetmagic.util.FaceAABB;

public class WoodChest extends BaseFaceBlock implements EntityBlock, IWaterBlock, IFoodExpBlock {

	public final int data;
	private final static VoxelShape[] AABB = FaceAABB.create(0D, 0D, 8D, 16D, 16D, 16D);
	private final static VoxelShape[] WOOD_CHEST = FaceAABB.create(0D, 0D, 2D, 16D, 16D, 16D);
	private final static VoxelShape[] POST = FaceAABB.create(4D, 0D, 7D, 12D, 19.5D, 14D);
	private final static VoxelShape[] CABINET = FaceAABB.create(0D, 0D, 6D, 16D, 16D, 16D);

	public WoodChest(String name, int data) {
		super(name, setState(Material.WOOD, SoundType.WOOD, 0.5F, 8192F));
		this.data = data;
		this.registerDefaultState(this.setState().setValue(WATERLOGGED, false));
		BlockInfo.create(this, SweetMagicCore.smTab, name);
	}

	public WoodChest(String name, BlockBehaviour.Properties props) {
		super(name, props);
		this.data = 5;
	}

	public WoodChest(String name, BlockBehaviour.Properties props, int data) {
		super(name, props);
		this.data = data;
	}

	// 右クリック出来るか
	public boolean canRightClick(Player player, ItemStack stack) {
		return true;
	}

	// ブロックでのアクション
	public boolean actionBlock(Level world, BlockPos pos, Player player, ItemStack stack) {
		if (world.isClientSide()) { return true; }

		TileWoodChest tile = (TileWoodChest) this.getTile(world, pos);

		if (player.isCreative() && stack.is(ItemInit.creative_wand)) {
			this.openGUI(world, pos, player, new ContainerWoodChest(pos));
			return true;
		}

		if (tile.lootTable != null) {
			if (player.isSpectator()) { return false; }
			tile.setLootInv(player);
		}

		this.openGUI(world, pos, player, tile);

		SoundEvent sound = null;
		switch (this.data) {
		case 0:
		case 5:
		case 7:
			sound = SoundEvents.PISTON_CONTRACT;
			break;
		case 1:
			sound = SoundEvents.BARREL_OPEN;
			break;
		case 2:
			sound = SoundEvents.IRON_DOOR_OPEN;
			break;
		case 6:
		case 9:
		case 10:
		case 11:
			sound = SoundEvents.WOODEN_TRAPDOOR_OPEN;
			break;
		case 8:
			sound = SoundEvents.LEVER_CLICK;
			break;
		}

		this.playerSound(world, pos, sound, 0.5F, world.getRandom().nextFloat() * 0.1F + 0.9F);
		return true;
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext con) {
		switch (this.data) {
		case 0: return FaceAABB.getAABB(AABB, state);
		case 4: return FaceAABB.getAABB(WOOD_CHEST, state);
		case 9: return FaceAABB.getAABB(POST, state);
		case 10: return FaceAABB.getAABB(POST, state);
		case 11: return FaceAABB.getAABB(CABINET, state);
		default: return Shapes.block();
		}
	}

	// ドロップするかどうか
	protected boolean isDrop() {
		return false;
	}

	// tileの中身を保持するか
	public boolean isKeepTile() {
		return true;
	}

	public ItemStack getDropStack(TileAbstractSM tile) {
		return tile.getDropStack(new ItemStack(this));
	}

	@Override
	public void addBlockTip(List<Component> toolTip) {
		toolTip.add(this.getText("sm_chest").withStyle(GREEN));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileWoodChest(pos, state);
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> build) {
		build.add(FACING, WATERLOGGED);
	}

	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	public BlockState updateShape(BlockState state, Direction face, BlockState newState, LevelAccessor world, BlockPos pos1, BlockPos pos2) {
		this.setWater(world, state, pos1);
		return super.updateShape(state, face, newState, world, pos1, pos2);
	}

	public boolean isPathfindable(BlockState state, BlockGetter get, BlockPos pos, PathComputationType type) {
		return type == PathComputationType.WATER ? get.getFluidState(pos).is(FluidTags.WATER) : false;
	}

	public boolean isChanceUp() {
		return this.data == 4 || this.data == 7;
	}
}
