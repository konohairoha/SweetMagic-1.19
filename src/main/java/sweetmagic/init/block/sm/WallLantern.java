package sweetmagic.init.block.sm;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
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

public class WallLantern extends BaseFaceBlock {

	private final int data;
	public static final BooleanProperty ISON = BooleanProperty.create("ison");
	private static final VoxelShape[] AABB = FaceAABB.create(4D, 3D, 3D, 12D, 16D, 16D);

	public WallLantern(String name, int data) {
		super(name, setState(Material.METAL, SoundType.METAL, 0.5F, 8192F));
		this.registerDefaultState(this.setState().setValue(ISON, false));
		this.data = data;
		BlockInfo.create(this, SweetMagicCore.smTab, name);
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext col) {
		return FaceAABB.getAABB(AABB, state);
	}

	// ブロックでのアクション
	public InteractionResult onUse(Level world, BlockPos pos, Player player, InteractionHand hand) {

		if ( player.isShiftKeyDown() && this.data == 0) {
			world.setBlock(pos, world.getBlockState(pos).cycle(ISON), 3);
			this.playerSound(world, pos, SoundEvents.UI_BUTTON_CLICK, 0.25F, world.random.nextFloat() * 0.1F + 0.9F);
			return InteractionResult.sidedSuccess(world.isClientSide);
		}

		return InteractionResult.PASS;
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> build) {
		build.add(ISON, FACING);
	}

	@Override
	public void addBlockTip (List<Component> toolTip) {
		if (this.data == 1) { return; }
		toolTip.add(this.getText("shift_change").withStyle(GOLD));
	}
}
