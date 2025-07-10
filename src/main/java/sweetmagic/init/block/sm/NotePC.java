package sweetmagic.init.block.sm;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.TileInit;
import sweetmagic.init.block.base.BaseFaceBlock;
import sweetmagic.init.tile.sm.TileNotePC;
import sweetmagic.util.FaceAABB;

public class NotePC extends BaseFaceBlock implements EntityBlock {

	private final Block block;
	public static final BooleanProperty CLOSE = BooleanProperty.create("close");
	private static final VoxelShape[] AABB = FaceAABB.create(1.5D, 0D, 1D, 14.5D, 9.75D, 15D);
	private static final VoxelShape[] CLO = FaceAABB.create(1.5D, 0D, 1D, 14.5D, 1.25D, 11D);

	public NotePC(String name, Block block) {
		super(name, setState(Material.METAL, SoundType.METAL, 0.5F, 8192F));
		this.registerDefaultState(this.setState().setValue(CLOSE, false));
		BlockInfo.create(this, SweetMagicCore.smTab, name);
		this.block = block;
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext con) {
		return state.getValue(CLOSE) ?  FaceAABB.getAABB(CLO, state) : FaceAABB.getAABB(AABB, state);
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> build) {
		build.add(FACING, CLOSE);
	}

	// 右クリック出来るか
	public boolean canRightClick(Player player, ItemStack stack) {
		return true;
	}

	// ブロックでのアクション
	public boolean actionBlock(Level world, BlockPos pos, Player player, ItemStack stack) {
		if (world.isClientSide()) { return true; }

		BlockState state = world.getBlockState(pos);
		boolean close = state.getValue(CLOSE);

		if(player.isShiftKeyDown()) {
			world.setBlock(pos, state.cycle(CLOSE), 3);
			this.playerSound(world, pos, SoundEvents.SHULKER_CLOSE, 0.25F, 1.15F);
		}

		else {

			TileNotePC tile = (TileNotePC) this.getTile(world, pos);
			Player owner = tile.getOwner();
			this.openGUI(world, pos, player, tile);

			if(close) {
				world.setBlock(pos, state.cycle(CLOSE), 3);
				this.playerSound(world, pos, SoundEvents.SHULKER_CLOSE, 0.25F, 1.15F);
			}
		}

		return true;
	}

	// tileの中身を保持するか
	public boolean isKeepTile() {
		return true;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileNotePC(pos, state);
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return this.createMailBoxTicker(world, type, TileInit.notePC);
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(world, pos, state, placer, stack);
		TileNotePC tile = (TileNotePC) this.getTile(world, pos);

		if(!world.isClientSide() && placer instanceof Player player) {
			tile.setOwner(player);
			tile.clearInfo();
		}
	}

	@Override
	public void addBlockTip(List<Component> toolTip) {

		boolean isShift = Screen.hasShiftDown();
		int size = isShift ? 7 : 1;

		for(int i = 0; i < size; i++)
			toolTip.add(this.getText("note_pc" + i).withStyle(i > 1 ? GOLD : GREEN));

		if(!isShift) {
			this.getShiftTip(toolTip);
			if (this.block == null) { return; }
			toolTip.add(this.getText("originatorblock", this.block.getName().getString()).withStyle(GOLD));
			toolTip.add(this.getText("note_pc_sell").withStyle(RED));
		}
	}

	// ドロップするかどうか
	protected boolean isDrop() {
		return false;
	}
}
