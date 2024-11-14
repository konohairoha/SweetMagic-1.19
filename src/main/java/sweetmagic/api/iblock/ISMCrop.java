package sweetmagic.api.iblock;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootContext;
import sweetmagic.init.BlockInit;
import sweetmagic.init.block.sm.PottingSoil;

public interface ISMCrop {

	public static final IntegerProperty AGE1 = BlockStateProperties.AGE_1;
	public static final IntegerProperty AGE2 = BlockStateProperties.AGE_2;
	public static final IntegerProperty AGE3 = BlockStateProperties.AGE_3;
	public static final IntegerProperty AGE4 = BlockStateProperties.AGE_4;
	public static final IntegerProperty AGE5 = BlockStateProperties.AGE_5;
	public static final IntegerProperty AGE7 = BlockStateProperties.AGE_7;

	// 最大成長段階
	int getMaxBlockState();

	// 最大成長段階
	IntegerProperty getSMMaxAge();

	// 成長チャンスの設定
	void setGlowChance (int chance);

	// 成長チャンスの取得
	int getGlowChange();

	// 種の取得
	ItemLike getSeed();

	// 右クリック回収時のアイテム
	ItemLike getCrop();

	// デフォルトステータス取得
	BlockState getDefault ();

	// 右クリック回収時に戻る成長段階
	default int RCSetState () {
		return 0;
	}

	// ドロップ数
	default int getDropValue (RandomSource rand, int fortune) {
		return 1;
	}

	// ドロップアイテム取得
	default ItemStack getDropStack (RandomSource rand) {
		return new ItemStack(this.getCrop(), this.getDropValue(rand, 0));
	}

	// 右クリック時アイテムを取得
	default List<ItemStack> rightClickStack (Level world, BlockState state, BlockPos pos) {

		// アイテムの取得
		List<ItemStack> stackList = Arrays.<ItemStack> asList( this.getDropStack(world.random) );

		// 作物の成長段階を下げる
        world.setBlock(pos, state.setValue(this.getSMMaxAge(), this.RCSetState()), 2);
		return stackList;
	}

	// 右クリックアイテムの取得
	default ItemEntity getDropItem (Level world, Player player, ItemStack hand, ItemLike item, int amount) {
		return new ItemEntity(world, player.xo, player.yo, player.zo, new ItemStack(item, amount));
	}

	// 右クリック時の処理
	default void onRicghtClick (Level world, Player player, BlockState state, BlockPos pos, ItemStack stack) { }

	// ブロック破壊処理
	default boolean breakBlock(Level world, BlockPos pos) {
        return world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
    }

	default void playCropSound (Level world, RandomSource rand, BlockPos pos, float vol) {
		world.playSound(null, pos, SoundEvents.GRASS_PLACE, SoundSource.BLOCKS, vol, 0.8F + rand.nextFloat() * 0.4F);
	}

	// 作物回収時の音
	default void playCropSound (Level world, RandomSource rand, BlockPos pos) {
        this.playCropSound(world, rand, pos, 0.5F);
	}

	// 幸運での加算
	default int getFoutuneValue (Player player) {

		int value = 0;

		if (player.hasEffect(MobEffects.LUCK)) {
			value += player.getEffect(MobEffects.LUCK).getAmplifier();
		}

		return value;
	}

	// ドロップアイテムの取得
	default Item getDropItem () {
		return null;
	}

	default Block getBlock (LevelAccessor world, BlockPos pos) {
		return world.getBlockState(pos).getBlock();
	}

	// 現在の成長段階を取得
	default int getNowState (BlockState state) {
		return state.getValue(this.getSMMaxAge());
	}

	// 最大成長段階かどうか
	default boolean isMaxAge (BlockState state) {
		return this.getNowState(state) >= this.getMaxBlockState();
	}

	// 成長できるかどうか
	default boolean isGlowChange (RandomSource rand) {
		return rand.nextInt(this.getGlowChange()) == 0;
	}

	default void growCrops(Level world, BlockPos pos, BlockState state) {
		int age = Math.min(this.getNowState(state) + this.getBonemealAgeIncrease(world), this.getMaxBlockState());
		world.setBlock(pos, this.getStateForAge(age), 2);
	}

	default int getBonemealAgeIncrease(Level world) {
		return Mth.nextInt(world.random, 0, Math.min(2, this.getMaxBlockState() - 2));
	}

	default BlockState getStateForAge(int addAge) {
		return this.getDefault().setValue(this.getSMMaxAge(), Integer.valueOf(addAge));
	}

	default List<ItemStack> getDropList(BlockState state, LootContext.Builder build) {

		List<ItemStack> stackList = Lists.newArrayList();
		RandomSource rand = build.getLevel().random;

		// 最大成長しているなら
		if (this.isMaxAge(state)) {

			stackList.add(new ItemStack(this.getCrop(), rand.nextInt(this.getMaxBlockState()) + 1));

			if (this.getSeed() != this.getCrop()) {
				stackList.add(new ItemStack(this.getSeed(), rand.nextInt(2)));
			}
		}

		stackList.add(new ItemStack(this.getSeed()));
		return stackList;
	}

	default boolean isOnlyFarm() {
		return false;
	}

	default boolean checkPlace(BlockState state, BlockGetter get, BlockPos pos) {
		return this.isOnlyFarm() ? (state.is(Blocks.FARMLAND) || state.getBlock() instanceof PottingSoil || state.is(BlockInit.aether_planter) ) : ( state.is(BlockTags.DIRT) || state.is(Blocks.FARMLAND) );
	}
}
