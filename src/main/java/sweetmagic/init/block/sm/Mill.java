package sweetmagic.init.block.sm;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.ItemHandlerHelper;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.iblock.ISMCookBlock;
import sweetmagic.api.iblock.ISMCraftBlock;
import sweetmagic.init.TileInit;
import sweetmagic.init.block.base.BaseCookBlock;
import sweetmagic.init.tile.sm.TileAbstractSM;
import sweetmagic.init.tile.sm.TileMill;
import sweetmagic.recipe.base.AbstractRecipe;
import sweetmagic.recipe.mill.MillRecipe;

public class Mill extends BaseCookBlock implements ISMCraftBlock, ISMCookBlock {

	private final int data;
	private static final VoxelShape AABB = Block.box(0D, 0D, 0D, 16D, 14D, 16D);
	private static final VoxelShape MIXER = Block.box(4.5D, 0D, 4.5D, 11.5D, 15.5D, 11.5D);

	public Mill(String name) {
		super(name);
		this.data = 0;
	}

	public Mill(String name, int data) {
		super(name, SweetMagicCore.smTab);
		this.data = data;
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext con) {
		switch (this.data) {
		case 1:  return MIXER;
		default: return AABB;
		}
	}

	// ブロックでのアクション
	public boolean actionBlock(Level world, BlockPos pos, Player player, ItemStack stack) {
		if (world.isClientSide) { return true; }

		BlockState state = world.getBlockState(pos);
		int cookState = this.getState(state);

		if (cookState == 0) {
			this.getRecipeMill(world, pos, player, stack);
		}

		else if (cookState == 2) {
			TileMill tile = (TileMill) this.getTile(world, pos);
			this.spawnItemList(world, player.blockPosition(), tile.getOutPutList());
			this.setState(world, pos, 0);
			tile.outPutClear();
		}
		return true;
	}

	// 製粉レシピの取得
	public void getRecipeMill(Level world, BlockPos pos, Player player, ItemStack stack) {
		List<ItemStack> stackList = Arrays.<ItemStack> asList(stack);
		Optional<MillRecipe> recipe = MillRecipe.getRecipe(world, stackList);
		if (recipe.isEmpty()) { return; }

		// 要求アイテムリスト
		List<ItemStack> requestList = recipe.get().getRequestList();

		// メインアイテムを取得
		ItemStack hasndStack = stackList.get(0);
		int requestAmount = requestList.get(0).getCount();
		int amount = hasndStack.getCount() / requestAmount;
		int shrinkAmount = requestAmount * amount;
		ItemStack copy = stack.copy();
		copy.setCount(shrinkAmount);
		stack.shrink(shrinkAmount);

		TileMill tile = (TileMill) this.getTile(world, pos);
		ItemHandlerHelper.insertItemStacked(tile.getHand(), copy, false);
		tile.craftStart();
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileMill(pos, state);
	}

	public BlockEntityType<? extends TileAbstractSM> getTileType() {
		return TileInit.mill;
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return this.createMailBoxTicker(world, type, this.getTileType());
	}

	@Override
	public void addBlockTip(List<Component> toolTip) {
		toolTip.add(this.getText("mill").withStyle(GREEN));

		if (this.data != 0) {
			toolTip.add(this.getText("mill_use").withStyle(GOLD));
		}
	}

	public boolean notNullRecipe(Level world, List<ItemStack> stackList) {
		return !MillRecipe.getRecipe(world, stackList).isEmpty();
	}

	public AbstractRecipe getRecipe(Level world, List<ItemStack> stackList) {
		return MillRecipe.getRecipe(world, stackList).get();
	}

	public boolean isView() {
		return false;
	}
}
