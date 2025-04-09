package sweetmagic.init.block.sm;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.iblock.ISMCraftBlock;
import sweetmagic.init.TileInit;
import sweetmagic.init.block.base.BaseCookBlock;
import sweetmagic.init.tile.sm.TileAbstractSM;
import sweetmagic.init.tile.sm.TileFrypan;
import sweetmagic.recipe.RecipeHelper;
import sweetmagic.recipe.RecipeHelper.RecipeUtil;
import sweetmagic.recipe.base.AbstractRecipe;
import sweetmagic.recipe.frypan.FrypanRecipe;
import sweetmagic.util.FaceAABB;

public class Frypan extends BaseCookBlock implements ISMCraftBlock {

	private final int data;
	private static final VoxelShape[] AABB = FaceAABB.create(3.75D, 0D, 4.75D, 12.25D, 2D, 15.25D);

	public Frypan(String name) {
		super(name);
		this.data = 0;
	}

	public Frypan(String name, int data) {
		super(name, SweetMagicCore.smTab);
		this.data = data;
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext col) {
		return FaceAABB.getAABB(AABB, state);
	}

	// ブロックでのアクション
	public boolean actionBlock(Level world, BlockPos pos, Player player, ItemStack stack) {
		if (world.isClientSide) { return true; }

		BlockState state = world.getBlockState(pos);
		int cookState = this.getState(state);

		// 未作成状態かつ下がコンロなら
		if (cookState == 0 && this.isUnderStove(world, pos) && !stack.isEmpty()) {
			this.recipeCraft(world, pos, player, stack);
		}

		// 作成完了なら
		else if (cookState == 2) {

			// クラフト後アイテムのドロップ
			TileFrypan tile = (TileFrypan) this.getTile(world, pos);
			this.spawnXp(player, tile.resultList, tile.hasFork);
			this.spawnItemList(world, player.blockPosition(), tile.resultList);
			tile.player = player;
			tile.getExpValue();

			// 初期化
			this.setState(world, pos, 0);
			tile.clearInfo();
			tile.sendPKT();
		}
		return true;
	}

	// 製粉レシピの取得
	public void recipeCraft(Level world, BlockPos pos, Player player, ItemStack stack) {

		// レシピを取得して見つからなければ終了
		List<ItemStack> stackList = RecipeHelper.getPlayerInv(player, stack);
		Optional<FrypanRecipe> recipe = FrypanRecipe.getRecipe(world, stackList);
		if (recipe.isEmpty()) {
			this.cookedFurnaceRecipe(world, pos, player, stack);
			return;
		}

		// クラフト要求アイテムの消費とクラフト後のアイテム取得
		RecipeUtil recipeUtil = RecipeHelper.recipeAllCraft(stackList, recipe.get());

		// クラフトアイテムの情報をえんちちーへ送信
		TileFrypan tile = (TileFrypan) this.getTile(world, pos);
		tile.craftList = recipeUtil.getInputList();
		tile.resultList = recipeUtil.getResultList();
		tile.hasFork = this.hasFork(player);
		tile.amount = recipeUtil.getCount();
		tile.player = player;
		tile.craftStart();
	}

	public void cookedFurnaceRecipe(Level world, BlockPos pos, Player player, ItemStack stack) {
		Optional<SmeltingRecipe> recipe = world.getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(stack), world);
		if (recipe.isEmpty()) { return; }

		// アイテムが空か食べ物でないなら終了
		ItemStack result = recipe.get().getResultItem().copy();
		if (result.isEmpty() || !result.getItem().isEdible()) { return; }

		// 今持っている個数に精錬後アイテムを設定
		result.setCount(stack.getCount());

		// クラフトアイテムの情報をえんちちーへ送信
		TileFrypan tile = (TileFrypan) this.getTile(world, pos);
		tile.craftList.add(stack.copy());
		tile.resultList.add(result);
		tile.craftStart();

		// アイテムの消費
		stack.shrink(stack.getCount());
	}

	public void onRemove(Level world, BlockPos pos, BlockState state, TileAbstractSM tile) {
		if (this.getState(state) != 0 && this.getBlock(world, pos.below()) instanceof Stove stove) {
			stove.setState(world, pos.below(), 0);
		}

		super.onRemove(world, pos, state, tile);
	}

	// ステータスの変更
	public void setState(Level world, BlockPos pos, int data) {
		super.setState(world, pos, data);

		if (this.getBlock(world, pos.below()) instanceof Stove stove) {
			stove.setState(world, pos.below(), data);
		}
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileFrypan(pos, state);
	}

	public BlockEntityType<? extends TileAbstractSM> getTileType() {
		return TileInit.frypan;
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return this.createMailBoxTicker(world, type, this.getTileType());
	}

	@Override
	public void addBlockTip(List<Component> toolTip) {
		super.addBlockTip(toolTip);
		toolTip.add(this.getText("under_stove").withStyle(GREEN));

		if (this.data != 0) {
			toolTip.add(this.getText("frypan_use").withStyle(GOLD));
		}
	}

	public boolean notNullRecipe(Level world, List<ItemStack> stackList) {
		return !FrypanRecipe.getRecipe(world, stackList).isEmpty();
	}

	public AbstractRecipe getRecipe(Level world, List<ItemStack> stackList) {
		return FrypanRecipe.getRecipe(world, stackList).get();
	}
}
