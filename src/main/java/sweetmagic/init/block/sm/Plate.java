package sweetmagic.init.block.sm;

import java.util.Arrays;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.ItemHandlerHelper;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.iblock.IFoodExpBlock;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.block.base.BaseFaceBlock;
import sweetmagic.init.tile.sm.TilePlate;

public class Plate extends BaseFaceBlock implements EntityBlock, IFoodExpBlock {

	public final int data;
	private static final VoxelShape PLATE = Block.box(2D, 0D, 2D, 14D, 1.5D, 14D);
	private static final VoxelShape TRAY = Block.box(0D, 0D, 0D, 16D, 2D, 16D);

	public Plate(String name, int data) {
		super(name, setState(Material.STONE, SoundType.STONE, 0.5F, 8192F));
		this.data = data;
		this.registerDefaultState(this.setState());
		BlockInfo.create(this, SweetMagicCore.smTab, name);
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext col) {
		switch (this.data) {
		case 0: return PLATE;
		case 1: return TRAY;
		case 2: return TRAY;
		default: return Shapes.block();
		}
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return this.data == 0 ? RenderShape.ENTITYBLOCK_ANIMATED : super.getRenderShape(state);
	}

	// 右クリック出来るか
	public boolean canRightClick(Player player, ItemStack stack) {
		return true;
	}

	// ブロックでのアクション
	public boolean actionBlock(Level world, BlockPos pos, Player player, ItemStack stack) {
		if (world.isClientSide) { return true; }
		if (!(this.getTile(world, pos) instanceof TilePlate tile)) { return false; }

		ItemStack inputStack = tile.getInputItem(0);
		boolean isSneak = player.isShiftKeyDown();

		if (inputStack.isEmpty() || isSneak) {

			// 完成品を入れる
			if (!stack.isEmpty() && !isSneak) {
				ItemStack copy = stack.copy();
				ItemHandlerHelper.insertItemStacked(tile.getInput(), copy, false);
				stack.shrink(stack.getCount());
				this.playerSound(world, pos, SoundEvents.ITEM_PICKUP, 0.5F, 1F);
			}

			else {
				this.openGUI(world, pos, player, tile);
			}
		}

		else {
			this.spawnItemList(world, player.blockPosition(), Arrays.<ItemStack> asList(inputStack.copy()));
			inputStack.shrink(inputStack.getCount());
			this.playerSound(world, pos, SoundEvents.ITEM_PICKUP, 0.5F, 1F);
		}

		tile.sendPKT();
		return true;
	}

	// ドロップするかどうか
	protected boolean isDrop() {
		return false;
	}

	// tileの中身を保持するか
	public boolean isKeepTile() {
		return true;
	}

	@Override
	public void addBlockTip(List<Component> toolTip) {
		toolTip.add(this.getText("plate").withStyle(GREEN));

		switch (this.data) {
		case 0:
			toolTip.add(this.getText("plate_in").withStyle(GREEN));
			break;
		}
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TilePlate(pos, state);
	}

	public int getData() {
		return this.data;
	}

	public boolean isChanceUp() {
		return this.data == 0;
	}
}
