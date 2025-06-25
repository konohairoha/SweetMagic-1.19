package sweetmagic.init.block.sm;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import sweetmagic.api.util.EnumHorizontal;
import sweetmagic.init.tile.sm.TileShowCase;
import sweetmagic.util.FaceAABB;

public class ShowCase extends Plate {

	private final static VoxelShape[] SHOWCASE = FaceAABB.create(0D, 0D, 2D, 16D, 16D, 16D);
	public static final EnumProperty<EnumHorizontal> HORIZONTAL = EnumProperty.create("horizontal", EnumHorizontal.class);

	public ShowCase(String name, int data) {
		super(name, 3, null);
		this.registerDefaultState(this.setState().setValue(HORIZONTAL, EnumHorizontal.NOR));
	}

	// ブロックでのアクション
	public boolean actionBlock(Level world, BlockPos pos, Player player, ItemStack stack) {
		if (world.isClientSide()) { return true; }
		this.openGUI(world, pos, player, this.getTile(world, pos));
		return true;
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> build) {
		build.add(FACING, HORIZONTAL);
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext con) {
		return FaceAABB.getAABB(SHOWCASE, state);
	}

	@Nullable
	public BlockState getStateForPlacement(BlockPlaceContext con) {
		Direction face = con.getPlayer().getDirection();
		return this.setVertical(con.getLevel(), con.getClickedPos(), face.getOpposite());
	}

	public BlockState updateShape(BlockState state, Direction face, BlockState state2, LevelAccessor world, BlockPos pos1, BlockPos pos2) {
		return this.setVertical(world, pos1, state.getValue(FACING));
	}

	public BlockState setVertical(LevelAccessor world, BlockPos pos, Direction face) {
		boolean right = this.getBlock(world, pos.relative(face.getCounterClockWise())) == this;
		boolean left = this.getBlock(world, pos.relative(face.getClockWise())) == this;
		return this.setState(face).setValue(HORIZONTAL, EnumHorizontal.getHorizontal(left, right));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileShowCase(pos, state);
	}

	@Override
	public void addBlockTip(List<Component> toolTip) {
		super.addBlockTip(toolTip);
		toolTip.add(this.getText("smhorizontal").withStyle(GOLD));
	}
}
