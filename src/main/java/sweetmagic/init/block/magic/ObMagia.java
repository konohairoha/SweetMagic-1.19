package sweetmagic.init.block.magic;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.TileInit;
import sweetmagic.init.block.base.BaseFaceBlock;
import sweetmagic.init.block.base.BaseSMBlock;
import sweetmagic.init.tile.sm.TileAbstractSM;
import sweetmagic.init.tile.sm.TileObMagia;
import sweetmagic.init.tile.sm.TileStove;

public class ObMagia extends BaseFaceBlock implements EntityBlock {

	private final boolean isTop;
	private static final VoxelShape BOT = Block.box(0D, 0D, 0D, 16D, 16D, 16D);
	private static final VoxelShape TOP = Block.box(0D, 0D, 0D, 16D, 4D, 16D);

	public ObMagia(String name, boolean isTop) {
		super(name, setState(Material.PISTON, SoundType.METAL, 1F, 8192F));
		this.isTop = isTop;
		this.registerDefaultState(this.setState());
		BlockInfo.create(this, isTop ? null : SweetMagicCore.smMagicTab, name);
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext col) {
		return this.isTop ? TOP : BOT;
	}

	// 右クリック出来るか
	public boolean canRightClick(Player player, ItemStack stack) {
		return true;
	}

	// ブロックでのアクション
	public boolean actionBlock(Level world, BlockPos pos, Player player, ItemStack stack) {
		if (world.isClientSide()) { return true; }

		pos = this.isTop ? pos.below() : pos;
		TileAbstractSM tile = this.getTile(world, pos);

		if (tile != null) {
			this.openGUI(world, pos, player, tile);
		}

		else if (this.getBlock(world, pos) instanceof BaseSMBlock sm) {
			sm.actionBlock(world, pos, player, stack);
		}
		return true;
	}

	// tileの中身を保持するか
	public boolean isKeepTile() {
		return !this.isTop;
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
		return world.isEmptyBlock(pos.above());
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		world.setBlockAndUpdate(pos.above(), BlockInit.obmagia_top.defaultBlockState().setValue(FACING, placer.getDirection()));
	}

	@Override
	public ItemStack getCloneItemStack(BlockGetter get, BlockPos pos, BlockState state) {
		return new ItemStack(BlockInit.obmagia);
	}

	@Override
	public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {

		BlockPos targetPos = this.isTop ? pos.below() : pos.above();
		BlockState upState = world.getBlockState(targetPos);

		if (upState.getBlock() instanceof ObMagia) {
			world.setBlock(targetPos, Blocks.AIR.defaultBlockState(), 35);
			world.levelEvent(player, 2001, targetPos, Block.getId(upState));
		}

		super.playerWillDestroy(world, pos, state, player);
	}

	// ドロップするかどうか
	protected boolean isDrop() {
		return false;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return this.isTop ? new TileStove(pos, state) : new TileObMagia(pos, state);
	}

	public BlockEntityType<? extends TileAbstractSM> getTileType () {
		return this.isTop ? null : TileInit.obmagia;
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return this.isTop ? null : this.createMailBoxTicker(level, type, TileInit.obmagia);
	}

	@Override
	public void addBlockTip(List<Component> toolTip) {
		if (!this.isTop) {
			toolTip.add(this.tierTip(1));
			toolTip.add(this.getText(this.name).withStyle(GREEN));
		}
	}
}
