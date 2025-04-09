package sweetmagic.init.block.sm;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import sweetmagic.api.iblock.ISMCookBlock;
import sweetmagic.init.BlockInit;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.SoundInit;
import sweetmagic.init.TileInit;
import sweetmagic.init.block.base.BaseFaceBlock;
import sweetmagic.init.tile.sm.TileAbstractSM;
import sweetmagic.init.tile.sm.TileFreezer;

public class Freezer extends BaseFaceBlock implements EntityBlock, ISMCookBlock {

	private Block botBlock;

	public Freezer(String name, int data) {
		super(name, setState(Material.METAL, SoundType.METAL, 1.0F, 8192.0F));
		this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
		this.botBlock = this;
		BlockInit.blockMap.put(new BlockInfo(this, null), name);
	}

	// 右クリック出来るか
	public boolean canRightClick(Player player, ItemStack stack) {
		return true;
	}

	// ブロックでのアクション
	public boolean actionBlock(Level world, BlockPos pos, Player player, ItemStack stack) {
		if (world.isClientSide) { return true; }
		TileFreezer tile = (TileFreezer) this.getTile(world, pos);
		tile.player = player;
		this.openGUI(world, pos, player, tile);
		this.playerSound(world, pos, SoundInit.FREEZER_OPEN, 0.2F, world.random.nextFloat() * 0.1F + 1.4F);
		return true;
	}

	@Override
	public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {

		BlockPos targetPos = pos.below();
		BlockState upState = world.getBlockState(targetPos);

		if (upState.getBlock() instanceof FreezerChest) {
			world.setBlock(targetPos, Blocks.AIR.defaultBlockState(), 35);
			world.levelEvent(player, 2001, targetPos, Block.getId(upState));
		}

		super.playerWillDestroy(world, pos, state, player);
	}

	public void onRemove(Level world, BlockPos pos, BlockState state, TileAbstractSM tile) {
		if (!tile.isInfoEmpty()) {
			this.spawnItemList(world, pos, ((TileFreezer) tile).getDropList());
		}
	}

	@Override
	public ItemStack getCloneItemStack(BlockGetter get, BlockPos pos, BlockState state) {
		return new ItemStack(this.botBlock);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileFreezer(pos, state);
	}

	public BlockEntityType<? extends TileAbstractSM> getTileType() {
		return TileInit.freezer;
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return this.createMailBoxTicker(world, type, this.getTileType());
	}

	// ドロップするかどうか
	protected boolean isDrop() {
		return false;
	}
}
