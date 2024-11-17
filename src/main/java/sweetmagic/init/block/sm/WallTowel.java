package sweetmagic.init.block.sm;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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

public class WallTowel extends BaseFaceBlock {

	private static final VoxelShape[] WALL = FaceAABB.create(0D, 1D, 15.999D, 16D, 14D, 16D);
	private static final BooleanProperty HIDE = BooleanProperty.create("hide");

	public WallTowel(String name) {
		super(name, setState(Material.WOOL, SoundType.WOOL, 0F, 8192F));
		this.registerDefaultState(this.setState().setValue(HIDE, false));
		BlockInfo.create(this, SweetMagicCore.smTab, name);
	}

	// 右クリック出来るか
	public boolean canRightClick (Player player, ItemStack stack) {
		return player != null;
	}

	// ブロックでのアクション
	public void actionBlock (Level world, BlockPos pos, Player player, ItemStack stack) {
		BlockState state = world.getBlockState(pos);
		world.setBlock(pos, state.cycle(HIDE), 3);
		this.playerSound(world, pos, SoundEvents.WOOL_BREAK, 0.25F, world.random.nextFloat() * 0.1F + 1.2F);
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext col) {
		return FaceAABB.getAABB(WALL, state);
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> build) {
		build.add(HIDE, FACING);
	}

	@Override
	public void addBlockTip (List<Component> toolTip) {
		toolTip.add(this.getText(this.name).withStyle(GREEN));
	}
}
