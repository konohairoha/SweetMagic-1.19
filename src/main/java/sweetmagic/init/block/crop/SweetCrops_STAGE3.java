package sweetmagic.init.block.crop;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.PlantType;
import sweetmagic.api.iblock.ISMCrop;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.ItemInit;
import sweetmagic.init.block.base.BaseSMBlock;
import sweetmagic.init.item.sm.SMSickle;

public class SweetCrops_STAGE3 extends BushBlock implements ISMCrop, BonemealableBlock {

	private int chance;
	private final int data;
	private final boolean isFarm;
	private static final VoxelShape[] CROP_VEXE = new VoxelShape[] {
		Block.box(1.6D, 0D, 1.6D, 14.4D, 1D, 14.4D),
		Block.box(1.6D, 0D, 1.6D, 14.4D, 5D, 14.4D),
		Block.box(1.6D, 0D, 1.6D, 14.4D, 7D, 14.4D),
		Block.box(1.6D, 0D, 1.6D, 14.4D, 10D, 14.4D)
	};

	public SweetCrops_STAGE3(String name, int data, int chance, boolean isFarm) {
		super(BaseSMBlock.setState(Material.PLANT, SoundType.GRASS, 0F, 8192F, data == 5 ? 15 : 0).noCollission().randomTicks());
		this.data = data;
		this.chance = chance;
		this.isFarm = isFarm;
		this.registerDefaultState(this.stateDefinition.any().setValue(this.getSMMaxAge(), 0));
		BlockInfo.create(this, null, name);
	}

	/**
	 * 0 = シュガーベル
	 * 1 = ファイアナスタチウム
	 * 2 = ドリィズリミオソチス
	 * 3 = クレロデンドルム
	 * 4 = コットン
	 * 5 = グロウフラワー
	 * 6 = サツマイモ
	 * 7 = イチゴ
	 * 8 = キャベツ
	 * 9 = レタス
	 * 10 = ほうれん草
	 * 11 = ミント
	 * 12 = ピーマン
	 * 13 = 大根
	 */

	// 作物の取得
	@Override
	public ItemLike getCrop() {
		switch (this.data) {
		case 1: return ItemInit.fire_nasturtium_petal;
		case 2: return ItemInit.dm_flower;
		case 3: return ItemInit.clero_petal;
		case 4: return ItemInit.cotton;
		case 5: return Items.GLOWSTONE_DUST;
		case 6: return ItemInit.sweetpotato;
		case 7: return ItemInit.strawberry;
		case 8: return ItemInit.cabbage;
		case 9: return ItemInit.lettuce;
		case 10: return ItemInit.spinach;
		case 11: return ItemInit.paper_mint;
		case 12: return ItemInit.greenpepper;
		case 13: return ItemInit.j_radish;
		default: return ItemInit.sugarbell;
		}
	}

	public ItemLike getSeed() {
		switch (this.data) {
		case 1: return ItemInit.fire_nasturtium_seed;
		case 2: return ItemInit.drizzly_mysotis_seed;
		case 3: return ItemInit.clerodendrum_seed;
		case 4: return ItemInit.cotton_seed;
		case 5: return ItemInit.glowflower_seed;
		case 6: return ItemInit.sweetpotato;
		case 7: return ItemInit.strawberry;
		case 8: return ItemInit.cabbage_seed;
		case 9: return ItemInit.lettuce_seed;
		case 10: return ItemInit.spinach_seed;
		case 11: return ItemInit.paper_mint_seed;
		case 12: return ItemInit.greenpepper_seed;
		case 13: return ItemInit.j_radish_seed;
		default: return ItemInit.sugarbell_seed;
		}
	}

	// 最大成長段階
	@Override
	public int getMaxBlockState() {
		return 3;
	}

	// 最大成長段階
	@Override
	public IntegerProperty getSMMaxAge() {
		return ISMCrop.AGE3;
	}

	// 成長チャンスの設定
	@Override
	public void setGlowChance(int chance) {
		this.chance = chance;
	}

	// 成長チャンスの取得
	@Override
	public int getGlowChange() {
		return this.chance;
	}

	// 右クリック回収時に戻る成長段階
	@Override
	public int RCSetState() {
		return 1;
	}

	// デフォルトステータス取得
	@Override
	public BlockState getDefault() {
		return this.defaultBlockState();
	}

	// ドロップ数
	@Override
	public int getDropValue(RandomSource rand, int fortune) {
		return Math.max(1, rand.nextInt(3) + 1);
	}

	public ItemStack getCloneItemStack(BlockGetter get, BlockPos pos, BlockState state) {
		return new ItemStack(this.getCrop());
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext con) {
		return CROP_VEXE[this.getNowState(state)];
	}

	// 成長処理チャンスを与えるかどうか
	public boolean isRandomlyTicking(BlockState state) {
		return !this.isMaxAge(state);
	}

	public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource rand) {

		// 最大まで成長しているなら終了
		if (!this.isMaxAge(state) && ForgeHooks.onCropsGrowPre(world, pos, state, this.isGlowChange(rand))) {
			BlockState glowState = state.setValue(this.getSMMaxAge(), this.getNowState(state) + 1);
			world.setBlock(pos, glowState, 2);
			world.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(glowState));
			ForgeHooks.onCropsGrowPost(world, pos, state);
		}
	}

	// 右クリック
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {

		ItemStack stack = player.getItemInHand(hand);

		if (stack.getItem() instanceof SMSickle sickle) {
			sickle.getPickPlant(world, player, pos, stack);
			return InteractionResult.SUCCESS;
		}

		// 最大成長していないなら終了
		if (!this.isMaxAge(state)) {
			return stack.is(Items.BONE_MEAL) ? InteractionResult.PASS : InteractionResult.SUCCESS;
		}

		this.onRicghtClick(world, player, state, pos, stack);
		return InteractionResult.sidedSuccess(world.isClientSide());
	}

	// 右クリック
	public void onRicghtClick(Level world, Player player, BlockState state, BlockPos pos, ItemStack stack) {
		RandomSource rand = world.getRandom();
		ItemEntity drop = this.getDropItem(world, player, stack, this.getCrop(), this.getDropValue(rand, 0));
		world.addFreshEntity(drop);
		world.setBlock(pos, state.setValue(this.getSMMaxAge(), this.RCSetState()), 2); //作物の成長段階を2下げる
		this.playCropSound(world, rand, pos);
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> build) {
		build.add(this.getSMMaxAge());
	}

	@Override
	public boolean isValidBonemealTarget(BlockGetter get, BlockPos pos, BlockState state, boolean falg) {
		return !this.isMaxAge(state);
	}

	@Override
	public boolean isBonemealSuccess(Level world, RandomSource rand, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void performBonemeal(ServerLevel world, RandomSource rand, BlockPos pos, BlockState state) {
		this.growCrops(world, pos, state);
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder build) {
		return this.getDropList(state, build);
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
		BlockPos underPos = pos.below();
		return this.mayPlaceOn(world.getBlockState(underPos), world, underPos);
	}

	@Override
	protected boolean mayPlaceOn(BlockState state, BlockGetter get, BlockPos pos) {
		return this.checkPlace(state, get, pos);
	}

	// 農地限定かどうか
	@Override
	public boolean isOnlyFarm() {
		return this.isFarm;
	}

	@Override
	public PlantType getPlantType(BlockGetter get, BlockPos pos) {
		return PlantType.CROP;
	}
}
