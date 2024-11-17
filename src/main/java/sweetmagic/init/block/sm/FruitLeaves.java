package sweetmagic.init.block.sm;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.ForgeHooks;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.iblock.ISMCrop;
import sweetmagic.init.BlockInit;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.ItemInit;
import sweetmagic.init.block.base.BaseSMBlock;
import sweetmagic.init.item.sm.SMSickle;

public class FruitLeaves extends BushBlock implements ISMCrop, BonemealableBlock {

	private final String name;
	private final int data;
	private int chance;
	public static final BooleanProperty NOGLOW = BooleanProperty.create("noglow");
	private final static BlockBehaviour.StatePredicate never = (a, b, c) -> false;
	private final static BlockBehaviour.StateArgumentPredicate<EntityType<?>> checkMob = (s, g, p, e) -> e == EntityType.OCELOT || e == EntityType.PARROT;

	public FruitLeaves(String name, int data, int chance) {
		super(BaseSMBlock.setState(Material.PLANT, SoundType.GRASS, 0F, 8192F, data == 5 ? 15 : 0).randomTicks().isValidSpawn(checkMob).isSuffocating(never).isViewBlocking(never));
		this.name = name;
		this.data = data;
		this.chance = chance;
		this.registerDefaultState(this.stateDefinition.any().setValue(this.getSMMaxAge(), 0).setValue(NOGLOW, false));
		BlockInit.blockMap.put(new BlockInfo(this, SweetMagicCore.smTab), this.name);
	}

	/**
	 * 0 = れもん
	 * 1 = みかん
	 * 2 = りんご
	 * 3 = もも
	 */

	// 作物の取得
	@Override
	public ItemLike getCrop() {
		switch (this.data) {
		case 0: return ItemInit.lemon;
		case 1: return ItemInit.orange;
		case 2: return ItemInit.estor_apple;
		case 3: return ItemInit.peach;
		default: return ItemInit.lemon;
		}
	}

	public ItemLike getSeed () {
		switch (this.data) {
		case 0: return BlockInit.lemon_sapling;
		case 1: return BlockInit.orange_sapling;
		case 2: return BlockInit.estor_sapling;
		case 3: return BlockInit.peach_sapling;
		default: return BlockInit.lemon_sapling;
		}
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
	public void setGlowChance (int chance) {
		this.chance = chance;
	}

	// 成長チャンスの取得
	@Override
	public int getGlowChange() {
		return this.chance;
	}

	// 右クリック回収時に戻る成長段階
	@Override
	public int RCSetState () {
		return 0;
	}

	// デフォルトステータス取得
	@Override
	public BlockState getDefault () {
		return this.defaultBlockState();
	}

	// ドロップ数
	@Override
	public int getDropValue (RandomSource rand, int fortune) {
		return Math.max(1, rand.nextInt(2) + 1);
	}

	public ItemStack getCloneItemStack(BlockGetter get, BlockPos pos, BlockState state) {
		return new ItemStack(this.getCrop());
	}

	// 成長処理チャンスを与えるかどうか
	public boolean isRandomlyTicking(BlockState state) {
		return !this.isMaxAge(state);
	}

	public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource rand) {

		// 最大まで成長しているなら終了
		if (!this.isMaxAge(state) && !state.getValue(NOGLOW) && ForgeHooks.onCropsGrowPre(world, pos, state, this.isGlowChange(rand))) {

			int nowAge = this.getNowState(state);
			BlockState glowState = state.setValue(this.getSMMaxAge(), Integer.valueOf(nowAge + 1));
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

			if (stack.is(Items.BONE_MEAL)) {
				return InteractionResult.PASS;
			}

			return InteractionResult.SUCCESS;
		}

		this.onRicghtClick(world, player, state, pos, stack);
		return InteractionResult.sidedSuccess(world.isClientSide);
	}

	// 右クリック
	public void onRicghtClick (Level world, Player player, BlockState state, BlockPos pos, ItemStack stack) {
	    RandomSource rand = world.random;
		ItemEntity drop = this.getDropItem(world, player, stack, this.getCrop(), this.getDropValue(rand, 0));
		world.addFreshEntity(drop);
        world.setBlock(pos, state.setValue(this.getSMMaxAge(), this.RCSetState()), 2); //作物の成長段階を2下げる
		this.playCropSound(world, rand, pos);
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> bui) {
		bui.add(this.getSMMaxAge(), NOGLOW);
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
	public int getBonemealAgeIncrease(Level world) {
		return Mth.nextInt(world.random, 0, 1);
	}

	// シルクタッチでのドロップ
	public ItemStack getSilkDrop() {
		return new ItemStack(this);
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder build) {

		ServerLevel world = build.getLevel();
		RandomSource rand = world.random;
		List<ItemStack> stackList = new ArrayList<ItemStack>();
		ItemStack stack = build.getOptionalParameter(LootContextParams.TOOL);

		// シルクタッチの場合はそのままドロップ
		if (!stack.isEmpty() && ( stack.getEnchantmentLevel(Enchantments.SILK_TOUCH) > 0 || stack.getItem() instanceof ShearsItem)) {
			stackList.add(this.getSilkDrop());
			return stackList;
		}

		int level = !stack.isEmpty() ? stack.getEnchantmentLevel(Enchantments.BLOCK_FORTUNE) : 0;
		float chance = 0.1F + (0.1F * (float) level);

		if (chance >= rand.nextFloat()) {
			stackList.add(new ItemStack(this.getSeed()));
		}

		stackList.addAll(this.getDropList(state, build));
		return stackList;
	}

	@Override
	public List<ItemStack> getDropList(BlockState state, LootContext.Builder build) {

		List<ItemStack> stackList = Lists.newArrayList();
		RandomSource rand = build.getLevel().random;

		// 最大成長しているなら
		if (this.isMaxAge(state)) {
			stackList.add(new ItemStack(this.getCrop(), rand.nextInt(this.getMaxBlockState()) + 1));
		}

		return stackList;
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader render, BlockPos pos) {
		BlockPos underpos = pos.below();
		return this.mayPlaceOn(render.getBlockState(underpos), render, underpos);
	}

	@Override
	protected boolean mayPlaceOn(BlockState state, BlockGetter get, BlockPos pos) {
		return true;
	}
}
