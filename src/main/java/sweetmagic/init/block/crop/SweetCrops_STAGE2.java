package sweetmagic.init.block.crop;

import java.util.Arrays;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShearsItem;
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
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.PlantType;
import sweetmagic.api.iblock.ISMCrop;
import sweetmagic.init.BlockInit;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.ItemInit;
import sweetmagic.init.block.base.BaseSMBlock;
import sweetmagic.init.item.sm.SMSickle;

public class SweetCrops_STAGE2 extends BushBlock implements ISMCrop, BonemealableBlock {

	private final String name;
	private final int data;
	private int chance;
	private static final VoxelShape[] CROP_VEXE = new VoxelShape[] {
			Block.box(3.2D, 6.4D, 3.2D, 12.8D, 16D, 12.8D),
			Block.box(1.6D, 3.2D, 1.6D, 14.4D, 16D, 14.4D),
			Block.box(0.8D, 1.6D, 0.8D, 15.2D, 16D, 15.2D)
	};

	public SweetCrops_STAGE2(String name, int data, int chance) {
		super(BaseSMBlock.setState(Material.PLANT, SoundType.GRASS, 0F, 8192F).noCollission().randomTicks());
		this.name = name;
		this.data = data;
		this.chance = chance;
		this.registerDefaultState(this.stateDefinition.any().setValue(this.getSMMaxAge(), 0));
		BlockInit.blockMap.put(new BlockInfo(this, null), this.name);
	}

	/**
	 * 0 = 栗
	 * 1 = ココナッツ
	 * 2 = バナナ
	 */

	// 作物の取得
	@Override
	public ItemLike getCrop() {
		switch (this.data) {
		case 1: return ItemInit.coconut;
		case 2: return ItemInit.banana;
		default: return ItemInit.chestnut;
		}
	}

	public ItemLike getSeed() {
		return null;
	}

	// 当たり判定
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext col) {
		return CROP_VEXE[this.getNowState(state)];
	}

	// 最大成長段階
	@Override
	public int getMaxBlockState() {
		return 2;
	}

	// 最大成長段階
	@Override
	public IntegerProperty getSMMaxAge() {
		return ISMCrop.AGE2;
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
		return 0;
	}

	// デフォルトステータス取得
	@Override
	public BlockState getDefault() {
		return this.defaultBlockState();
	}

	// ドロップ数
	@Override
	public int getDropValue(RandomSource rand, int fortune) {
		return Math.max(1, rand.nextInt(4) + 1);
	}

	public ItemStack getCloneItemStack(BlockGetter get, BlockPos pos, BlockState state) {
		return new ItemStack(this.getCrop());
	}

	// 成長処理チャンスを与えるかどうか
	public boolean isRandomlyTicking(BlockState state) {
		return !this.isMaxAge(state);
	}

	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {

		// 最大まで成長しているなら終了
		if (!this.isMaxAge(state) && ForgeHooks.onCropsGrowPre(level, pos, state, this.isGlowChange(rand))) {
			int nowAge = this.getNowState(state);
			BlockState glowState = state.setValue(this.getSMMaxAge(), Integer.valueOf(nowAge + 1));
			level.setBlock(pos, glowState, 2);
			level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(glowState));
			ForgeHooks.onCropsGrowPost(level, pos, state);
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
		return InteractionResult.sidedSuccess(world.isClientSide);
	}

	// 右クリック
	public void onRicghtClick(Level world, Player player, BlockState state, BlockPos pos, ItemStack stack) {

		RandomSource rand = world.random;
		ItemEntity drop = this.getDropItem(world, player, stack, this.getCrop(), this.getDropValue(rand, 0));
		world.addFreshEntity(drop);
		world.setBlock(pos, state.setValue(this.getSMMaxAge(), this.RCSetState()), 2); //作物の成長段階を2下げる
		this.playCropSound(world, rand, pos);
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> def) {
		def.add(this.getSMMaxAge());
	}

	@Override
	public boolean isValidBonemealTarget(BlockGetter get, BlockPos pos, BlockState state, boolean falg) {
		return !this.isMaxAge(state);
	}

	@Override
	public boolean isBonemealSuccess(Level level, RandomSource rand, BlockPos pos, BlockState state) {
		return true;
	}

	@Override
	public void performBonemeal(ServerLevel level, RandomSource rand, BlockPos pos, BlockState state) {
		this.growCrops(level, pos, state);
	}

	@Override
	public int getBonemealAgeIncrease(Level level) {
		return Mth.nextInt(level.random, 0, 1);
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder build) {

		ItemStack stack = build.getOptionalParameter(LootContextParams.TOOL);

		if (stack.getItem() instanceof ShearsItem) {
			return Arrays.<ItemStack> asList( new ItemStack(this));
		}

		return this.getDropList(state, build);
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader render, BlockPos pos) {
		BlockPos underpos = pos.above();
		return this.mayPlaceOn(render.getBlockState(underpos), render, underpos);
	}

	@Override
	protected boolean mayPlaceOn(BlockState state, BlockGetter get, BlockPos pos) {
		return state.is(BlockTags.LEAVES);
	}

	@Override
	public PlantType getPlantType(BlockGetter world, BlockPos pos) {
		return PlantType.CROP;
	}
}
