package sweetmagic.init.block.sm;

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
import sweetmagic.api.iblock.ISMCraftBlock;
import sweetmagic.init.TileInit;
import sweetmagic.init.block.base.BaseCookBlock;
import sweetmagic.init.tile.sm.TileAbstractSM;
import sweetmagic.init.tile.sm.TileBottle;
import sweetmagic.recipe.RecipeHelper;
import sweetmagic.recipe.RecipeUtil;
import sweetmagic.recipe.base.AbstractRecipe;
import sweetmagic.recipe.bottle.BottleRecipe;

public class Bottle extends BaseCookBlock implements ISMCraftBlock {

	private static final VoxelShape AABB = Block.box(4D, 0D, 4D, 12D, 11D, 12D);

	public Bottle(String name) {
		super(name);
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext col) {
		return AABB;
	}

	// ブロックでのアクション
	public void actionBlock (Level world, BlockPos pos, Player player, ItemStack stack) {
		if (world.isClientSide) { return; }

		BlockState state = world.getBlockState(pos);
		int cookState = this.getState(state);

		// 未作成状態なら
		if (cookState == 0) {
			this.recipeCraft(world, pos, player, stack);
		}

		// 作成完了なら
		else if (cookState == 2) {

			// クラフト後アイテムのドロップ
			TileBottle tile = (TileBottle) this.getTile(world, pos);
			this.spawnItemList(world, player.blockPosition(), tile.resultList);

			// 初期化
			this.setState(world, pos, 0);
			tile.clearInfo();
			tile.sendPKT();
		}
	}

	// 製粉レシピの取得
	public void recipeCraft (Level world, BlockPos pos, Player player, ItemStack stack) {

		// レシピを取得して見つからなければ終了
		List<ItemStack> stackList = RecipeHelper.getPlayerInv(player, stack);
		Optional<BottleRecipe> recipe = BottleRecipe.getRecipe(world, stackList);
		if (recipe.isEmpty()) { return; }

		// クラフト要求アイテムの消費とクラフト後のアイテム取得
		RecipeUtil recipeUtil = RecipeHelper.recipeAllCraft(stackList, recipe.get());

		// クラフトアイテムの情報をえんちちーへ送信
		TileBottle tile = (TileBottle) this.getTile(world, pos);
		tile.craftList = recipeUtil.getInputList();
		tile.resultList = recipeUtil.getResultList();
		tile.craftStart();
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileBottle(pos, state);
	}

	public BlockEntityType<? extends TileAbstractSM> getTileType () {
		return TileInit.bottle;
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		BlockEntityType<? extends TileAbstractSM> tileType = this.getTileType();
		return tileType != null ? this.createMailBoxTicker(world, type, tileType) : null;
	}

	@Override
	public void addBlockTip (List<Component> toolTip) {
		toolTip.add(this.getText(this.name).withStyle(GREEN));
	}

	public boolean notNullRecipe (Level world, List<ItemStack> stackList) {
		return !BottleRecipe.getRecipe(world, stackList).isEmpty();
	}

	public AbstractRecipe getRecipe (Level world, List<ItemStack> stackList) {
		return BottleRecipe.getRecipe(world, stackList).get();
	}
}
