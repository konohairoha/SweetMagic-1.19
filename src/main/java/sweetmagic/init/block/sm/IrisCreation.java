package sweetmagic.init.block.sm;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.TileInit;
import sweetmagic.init.block.base.BaseModelBlock;
import sweetmagic.init.tile.sm.TileAbstractSM;
import sweetmagic.init.tile.sm.TileIrisCreation;

public class IrisCreation extends BaseModelBlock implements EntityBlock {

	private static final VoxelShape AABB = Block.box(2D, 4D, 2D, 14D, 14D, 14D);
	private static final VoxelShape AABB_UNDER = Block.box(0D, 0D, 0D, 16D, 24D, 16D);
	public static final BooleanProperty UNDER = BooleanProperty.create("under");

	public IrisCreation(String name) {
		super(name, setState(Material.METAL,SoundType.METAL, 1.0F, 8192.0F));
		this.registerDefaultState(this.defaultBlockState().setValue(UNDER, Boolean.valueOf(false)));
		BlockInfo.create(this, SweetMagicCore.smMagicTab, name);
	}

	// 右クリック出来るか
	public boolean canRightClick (Player player, ItemStack stack) {
		return true;
	}

	// ブロックでのアクション
	public void actionBlock (Level world, BlockPos pos, Player player, ItemStack stack) {
		if (world.isClientSide) { return; }
		this.openGUI(world, pos, player, (TileIrisCreation) world.getBlockEntity(pos));
	}

	public void onRemove(Level world, BlockPos pos, BlockState state, TileAbstractSM tile) {
		if (!tile.isInfoEmpty()) {
			this.spawnItemList(world, pos, ((TileIrisCreation) tile).getDropList());
		}
	}

	@Nullable
	public BlockState getStateForPlacement(BlockPlaceContext con) {
		Block block = this.getBlock(con.getLevel(), con.getClickedPos().below());
		return this.defaultBlockState().setValue(UNDER, !(block instanceof CampfireBlock));
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> build) {
		build.add(UNDER);
	}

	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext cont) {
		Vec3 vec3 = state.getOffset(get, pos);
		return !state.getValue(UNDER) ? AABB_UNDER.move(vec3.x, vec3.y - 0.5D, vec3.z) : AABB;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileIrisCreation(pos, state);
	}

	public BlockEntityType<? extends TileAbstractSM> getTileType () {
		return TileInit.iris;
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		BlockEntityType<? extends TileAbstractSM> tileType = this.getTileType();
		return tileType != null ? this.createMailBoxTicker(world, type, tileType) : null;
	}

	@Override
	public void addBlockTip (List<Component> toolTip) {
		toolTip.add(this.getText(this.name).withStyle(GREEN));
		toolTip.add(this.getText(this.name + "_quick").withStyle(GOLD));
	}
}
