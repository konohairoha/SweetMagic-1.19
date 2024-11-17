package sweetmagic.init.block.sm;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.block.base.BaseFaceBlock;
import sweetmagic.util.FaceAABB;

public class CandleStick extends BaseFaceBlock {

	public static final BooleanProperty ISUNDER = BooleanProperty.create("isunder");
	private static final VoxelShape[] STICK = FaceAABB.create(6D, 5D, 5D, 10D, 16D, 16D);
	private static final VoxelShape[] UNDER = FaceAABB.create(6D, 0D, 5D, 10D, 11D, 16D);

	public CandleStick(String name) {
		super(name, setState(Material.WOOD, SoundType.WOOD, 0.25F, 8192F));
		this.registerDefaultState(this.setState().setValue(ISUNDER, false));
		BlockInfo.create(this, SweetMagicCore.smTab, name);
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext col) {
		return FaceAABB.getAABB(state.getValue(ISUNDER) ? UNDER : STICK, state);
	}

	@NotNull
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext con) {
		BlockState state = super.getStateForPlacement(con);
		boolean flag = con.getClickLocation().y - (double) con.getClickedPos().getY() > 0.5D;
		return state.setValue(ISUNDER, !flag);
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> build) {
		build.add(ISUNDER, FACING);
	}

	@Override
	public void addBlockTip (List<Component> toolTip) {
		toolTip.add(this.getText(this.name).withStyle(GOLD));
	}
}
