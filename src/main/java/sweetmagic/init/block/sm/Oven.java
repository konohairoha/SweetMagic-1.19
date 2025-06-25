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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.iblock.ISMCraftBlock;
import sweetmagic.init.TileInit;
import sweetmagic.init.block.base.BaseCookBlock;
import sweetmagic.init.tile.sm.TileOven;
import sweetmagic.recipe.RecipeHelper;
import sweetmagic.recipe.RecipeHelper.RecipeUtil;
import sweetmagic.recipe.base.AbstractRecipe;
import sweetmagic.recipe.oven.OvenRecipe;
import sweetmagic.util.FaceAABB;

public class Oven extends BaseCookBlock implements ISMCraftBlock {

	private final int data;
	private static final VoxelShape[] AABB = FaceAABB.create(1D, 0D, 3D, 15D, 12.5D, 14D);

	public Oven(String name) {
		super(name);
		this.data = 0;
	}

	public Oven(String name, int data) {
		super(name, SweetMagicCore.smTab);
		this.data = data;
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext con) {
		switch (this.data) {
		case 2: return FaceAABB.getAABB(AABB, state);
		default: return Shapes.block();
		}
	}

	// ブロックでのアクション
	public boolean actionBlock(Level world, BlockPos pos, Player player, ItemStack stack) {
		if (world.isClientSide()) { return true; }

		BlockState state = world.getBlockState(pos);
		int cookState = this.getState(state);

		// 未作成状態なら
		if (cookState == 0) {
			this.recipeCraft(world, pos, player, stack);
		}

		// 作成完了なら
		else if (cookState == 2) {

			// クラフト後アイテムのドロップ
			TileOven tile = (TileOven) world.getBlockEntity(pos);
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
		List<ItemStack> stackList = RecipeHelper.getPlayerInv(player, stack);
		Optional<OvenRecipe> recipe = OvenRecipe.getRecipe(world, stackList);
		if (recipe.isEmpty()) { return; }

		// クラフト要求アイテムの消費とクラフト後のアイテム取得
		RecipeUtil recipeUtil = RecipeHelper.recipeAllCraft(stackList, recipe.get());

		// クラフトアイテムの情報をえんちちーへ送信
		TileOven tile = (TileOven) world.getBlockEntity(pos);
		tile.craftList = recipeUtil.getInputList();
		tile.resultList = recipeUtil.getResultList();
		tile.hasFork = this.hasFork(player);
		tile.amount = recipeUtil.getCount();
		tile.player = player;
		tile.craftStart();
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileOven(pos, state);
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return this.createMailBoxTicker(world, type, TileInit.oven);
	}

	public boolean notNullRecipe(Level world, List<ItemStack> stackList) {
		return !OvenRecipe.getRecipe(world, stackList).isEmpty();
	}

	public AbstractRecipe getRecipe(Level world, List<ItemStack> stackList) {
		return OvenRecipe.getRecipe(world, stackList).get();
	}

	@Override
	public void addBlockTip(List<Component> toolTip) {
		toolTip.add(this.getText("cook_block").withStyle(GREEN));

		if (this.data != 0) {
			toolTip.add(this.getText("oven_use").withStyle(GOLD));
		}
	}
}
