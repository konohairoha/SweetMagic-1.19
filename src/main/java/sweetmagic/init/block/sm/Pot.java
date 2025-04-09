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
import net.minecraft.world.phys.shapes.VoxelShape;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.iblock.ISMCraftBlock;
import sweetmagic.init.TileInit;
import sweetmagic.init.block.base.BaseCookBlock;
import sweetmagic.init.tile.sm.TileAbstractSM;
import sweetmagic.init.tile.sm.TilePot;
import sweetmagic.recipe.RecipeHelper;
import sweetmagic.recipe.RecipeHelper.RecipeUtil;
import sweetmagic.recipe.base.AbstractRecipe;
import sweetmagic.recipe.pot.PotRecipe;
import sweetmagic.util.FaceAABB;

public class Pot extends BaseCookBlock implements ISMCraftBlock {

	private final int data;
	private static final VoxelShape[] AABB = FaceAABB.create(2.56D, 0D, 2.56D, 13.44D, 6.08D, 13.44D);

	public Pot(String name) {
		super(name);
		this.data = 0;
	}

	public Pot(String name, int data) {
		super(name, SweetMagicCore.smTab);
		this.data = data;
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext con) {
		return FaceAABB.getAABB(AABB, state);
	}

	// ブロックでのアクション
	public boolean actionBlock(Level world, BlockPos pos, Player player, ItemStack stack) {
		if (world.isClientSide) { return true; }
		BlockState state = world.getBlockState(pos);
		int cookState = this.getState(state);

		// 未作成状態なら
		if (cookState == 0 && this.isUnderStove(world, pos)) {
			this.recipeCraft(world, pos, player, stack);
		}

		// 作成完了なら
		else if (cookState == 2) {

			// クラフト後アイテムのドロップ
			TilePot tile = (TilePot) world.getBlockEntity(pos);
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
		Optional<PotRecipe> recipe = PotRecipe.getRecipe(world, stackList);
		if (recipe.isEmpty()) { return; }

		// クラフト要求アイテムの消費とクラフト後のアイテム取得
		RecipeUtil recipeUtil = RecipeHelper.recipeAllCraft(stackList, recipe.get());

		// クラフトアイテムの情報をえんちちーへ送信
		TilePot tile = (TilePot) this.getTile(world, pos);
		tile.craftList = recipeUtil.getInputList();
		tile.resultList = recipeUtil.getResultList();
		tile.hasFork = this.hasFork(player);
		tile.amount = recipeUtil.getCount();
		tile.player = player;
		tile.craftStart();
	}

	public void onRemove(Level world, BlockPos pos, BlockState state, TileAbstractSM tile) {
		if (this.getState(state) != 0 && this.getBlock(world, pos.below()) instanceof Stove stove) {
			stove.setState(world, pos.below(), 0);
		}

		super.onRemove(world, pos, state, tile);
	}

	// ステータスの変更
	@Override
	public void setState(Level world, BlockPos pos, int data) {
		super.setState(world, pos, data);

		if (this.getBlock(world, pos.below()) instanceof Stove stove) {
			stove.setState(world, pos.below(), data);
		}
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TilePot(pos, state);
	}

	public BlockEntityType<? extends TileAbstractSM> getTileType() {
		return TileInit.pot;
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
			toolTip.add(this.getText("pot_use").withStyle(GOLD));
		}
	}

	public boolean notNullRecipe(Level world, List<ItemStack> stackList) {
		return !PotRecipe.getRecipe(world, stackList).isEmpty();
	}

	public AbstractRecipe getRecipe(Level world, List<ItemStack> stackList) {
		return PotRecipe.getRecipe(world, stackList).get();
	}
}
