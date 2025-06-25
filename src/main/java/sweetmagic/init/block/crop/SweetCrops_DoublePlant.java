package sweetmagic.init.block.crop;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ForgeHooks;
import sweetmagic.init.BlockInit;
import sweetmagic.init.ItemInit;
import sweetmagic.init.block.sm.PottingSoil;

public class SweetCrops_DoublePlant extends SweetCrops_STAGE4 {

	private static final VoxelShape[] CROP_VEXE = new VoxelShape[] {
		Block.box(1.6D, 0D, 1.6D, 14.4D, 4D, 14.4D),
		Block.box(1.6D, 0D, 1.6D, 14.4D, 8D, 14.4D),
		Block.box(1.6D, 0D, 1.6D, 14.4D, 16D, 14.4D),
		Block.box(1.6D, 0D, 1.6D, 14.4D, 16D, 14.4D),
		Block.box(1.6D, 0D, 1.6D, 14.4D, 16D, 14.4D)
	};

	public SweetCrops_DoublePlant(String name, int data, int chance) {
		super(name, data, chance, true);
	}

	/**
	 * 0 = ステッキースタッフ
	 * 1 = 玉ねぎ
	 * 2 = オリーブ
	 */

	// 作物の取得
	@Override
	public ItemLike getCrop() {
		switch (this.data) {
		case 1: return ItemInit.eggplant;
		case 2: return ItemInit.tomato_seed;
		default: return ItemInit.corn;
		}
	}

	// 種の取得
	@Override
	public ItemLike getSeed() {
		switch (this.data) {
		case 1: return ItemInit.eggplant_seed;
		case 2: return ItemInit.tomato_seed;
		default: return ItemInit.corn_seed;
		}
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext con) {
		return CROP_VEXE[this.getNowState(state)];
	}

	// ランダム成長時
	public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource rand) {

		// 最大まで成長しているなら終了
		if (!this.isMaxAge(state) && ForgeHooks.onCropsGrowPre(world, pos, state, this.isGlowChange(rand))) {

			int nowAge = this.getNowState(state);
			BlockState glowState = state.setValue(this.getSMMaxAge(), nowAge + 1);
			world.setBlock(pos, glowState, 2);
			world.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(glowState));
			ForgeHooks.onCropsGrowPost(world, pos, state);

			if (nowAge + 1 >= 2 && world.isEmptyBlock(pos.above()) && this.checkPlace(world.getBlockState(pos.below()), world, pos.below())) {
				world.setBlock(pos.above(), this.defaultBlockState().setValue(this.getSMMaxAge(), 1), 2);
			}
		}
	}

	// 骨粉成長時
	public void growCrops(Level world, BlockPos pos, BlockState state) {
		int age = Math.min(this.getNowState(state) + this.getBonemealAgeIncrease(world), this.getMaxBlockState());
		world.setBlock(pos, this.getStateForAge(age), 2);

		if (age >= 2 && world.isEmptyBlock(pos.above()) && this.checkPlace(world.getBlockState(pos.below()), world, pos.below())) {
			world.setBlock(pos.above(), this.defaultBlockState().setValue(this.getSMMaxAge(), 1), 2);
		}
	}

	// 右クリック
	public void onRicghtClick(Level world, Player player, BlockState state, BlockPos pos, ItemStack stack) {

		// アイテムドロップ
		this.dropItem(world, state, pos, player, stack);

		// 上または下のブロックを取得
		BlockPos targetPot = this.isFarm(world.getBlockState(pos.below())) ? pos.above() : pos.below();
		BlockState targetState = world.getBlockState(targetPot);

		// 二段作物ならアイテムドロップ
		if (targetState.getBlock() instanceof SweetCrops_DoublePlant crop) {
			this.dropItem(world, targetState, targetPot, player, stack);
		}
	}

	// アイテムドロップ
	public void dropItem(Level world, BlockState state, BlockPos pos, Player player, ItemStack stack) {
		RandomSource rand = world.getRandom();
		ItemEntity drop = this.getDropItem(world, player, stack, this.getCrop(), this.getDropValue(rand, 0));
		world.addFreshEntity(drop);
		world.setBlock(pos, state.setValue(this.getSMMaxAge(), this.RCSetState()), 2); //作物の成長段階を2下げる
		this.playCropSound(world, rand, pos);
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
		BlockPos underPos1 = pos.below();
		BlockState underState1 = world.getBlockState(underPos1);
		BlockPos underPos2 = pos.below(2);
		BlockState underState2 = world.getBlockState(underPos2);
		return this.mayPlaceOn(world.getBlockState(underPos1), world, underPos1) || (world.isEmptyBlock(pos.above()) && underState1.getBlock() == this && this.mayPlaceOn(underState2, world, underPos2));
	}

	// 下が農地か、(下が作物かつ二段下が農地で成長段階が２以上なら)
	@Override
	public boolean checkPlace(BlockState state, BlockGetter get, BlockPos pos) {
		return super.checkPlace(state, get, pos) || ( state.getBlock() == this && super.checkPlace(state, get, pos.below()) && state.getValue(this.getSMMaxAge()) >= 2 );
	}

	public boolean isFarm (BlockState state) {
		return state.is(Blocks.FARMLAND) || state.is(BlockTags.DIRT) || state.getBlock() instanceof PottingSoil || state.is(BlockInit.aether_planter);
	}
}
