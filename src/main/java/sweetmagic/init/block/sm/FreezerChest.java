package sweetmagic.init.block.sm;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.block.base.BaseFaceBlock;
import sweetmagic.init.tile.sm.TileFreezerChest;

public class FreezerChest extends BaseFaceBlock implements EntityBlock {

	private Block botBlock;

	public FreezerChest(String name, Block botBlock) {
		super(name, setState(Material.METAL, SoundType.METAL, 1F, 8192F));
		this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
		this.botBlock = botBlock;
		BlockInit.blockMap.put(new BlockInfo(this, SweetMagicCore.smFoodTab), name);
	}

	public FreezerChest(String name, Block botBlock, int data) {
		super(name, setState(Material.METAL, SoundType.METAL, 1F, 8192F));
		this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
		this.botBlock = botBlock;
		BlockInit.blockMap.put(new BlockInfo(this, SweetMagicCore.smTab), name);
	}

	// 右クリック出来るか
	public boolean canRightClick(Player player, ItemStack stack) {
		return true;
	}

	// ブロックでのアクション
	public boolean actionBlock(Level world, BlockPos pos, Player player, ItemStack stack) {
		if (world.isClientSide()) { return true; }
		this.openGUI(world, pos, player, this.getTile(world, pos));
		this.playerSound(world, pos, SoundEvents.BARREL_OPEN, 0.5F, world.getRandom().nextFloat() * 0.1F + 1.4F);
		return true;
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		world.setBlockAndUpdate(pos.above(), this.botBlock.defaultBlockState().setValue(FACING, world.getBlockState(pos).getValue(FACING)));
	}

	@Override
	public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {

		BlockPos targetPos = pos.above();
		BlockState upState = world.getBlockState(targetPos);

		if (upState.getBlock() instanceof Freezer) {
			world.setBlock(targetPos, Blocks.AIR.defaultBlockState(), 35);
			world.levelEvent(player, 2001, targetPos, Block.getId(upState));
		}

		super.playerWillDestroy(world, pos, state, player);
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
		return world.getBlockState(pos.above()).isAir();
	}

	// tileの中身を保持するか
	public boolean isKeepTile() {
		return true;
	}

	@Override
	public ItemStack getCloneItemStack(BlockGetter get, BlockPos pos, BlockState state) {
		return new ItemStack(this.botBlock);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileFreezerChest(pos, state);
	}

	// ドロップするかどうか
	protected boolean isDrop() {
		return false;
	}
}
