package sweetmagic.init.block.crop;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
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
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.ItemInit;
import sweetmagic.init.block.base.BaseSMBlock;
import sweetmagic.init.item.sm.SMSickle;

public class SweetCrops_STAGE5 extends BushBlock implements ISMCrop, BonemealableBlock {

	private final int data;
	private int chance;
	private final boolean isFarm;
	private static final VoxelShape[] CROP_VEXE = new VoxelShape[] {
		Block.box(1.6D, 0D, 1.6D, 14.4D, 1D, 14.4D),
		Block.box(1.6D, 0D, 1.6D, 14.4D, 5D, 14.4D),
		Block.box(1.6D, 0D, 1.6D, 14.4D, 7D, 14.4D),
		Block.box(1.6D, 0D, 1.6D, 14.4D, 10D, 14.4D),
		Block.box(1.6D, 0D, 1.6D, 14.4D, 10D, 14.4D),
		Block.box(1.6D, 0D, 1.6D, 14.4D, 10D, 14.4D)
	};

	public SweetCrops_STAGE5(String name, int data, int chance, boolean isFarm) {
		super(BaseSMBlock.setState(Material.PLANT, SoundType.GRASS, 0F, 8192F).noCollission().randomTicks());
		this.data = data;
		this.chance = chance;
		this.isFarm = isFarm;
		this.registerDefaultState(this.stateDefinition.any().setValue(this.getSMMaxAge(), 0));
		BlockInfo.create(this, null, name);
	}

	/**
	 * 0 = クオーツ
	 * 1 = ラズベリー
	 * 2 = 大豆
	 * 3 = 米
	 * 4 = 小豆
	 * 5 = ラピス
	 * 6 = レッドストーン
	 */

	// 作物の取得
	@Override
	public ItemLike getCrop() {
		switch (this.data) {
		case 0: return Items.QUARTZ;
		case 1: return ItemInit.raspberry;
		case 2: return ItemInit.soybean;
		case 3: return ItemInit.rice_plants;
		case 4: return ItemInit.azuki_seed;
		case 5: return Items.LAPIS_LAZULI;
		case 6: return Items.REDSTONE;
		default: return null;
		}
	}

	// 種の取得
	@Override
	public ItemLike getSeed () {
		switch (this.data) {
		case 0: return ItemInit.quartz_seed;
		case 1: return ItemInit.raspberry;
		case 2: return ItemInit.soybean;
		case 3: return ItemInit.rice_seed;
		case 4: return ItemInit.azuki_seed;
		case 5: return ItemInit.lapislazuli_seed;
		case 6: return ItemInit.redstone_seed;
		default: return null;
		}
	}

	// 最大成長段階
	@Override
	public int getMaxBlockState() {
		return 5;
	}

	// 最大成長段階
	@Override
	public IntegerProperty getSMMaxAge() {
		return ISMCrop.AGE5;
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
		return 2;
	}

	// デフォルトステータス取得
	@Override
	public BlockState getDefault () {
		return this.defaultBlockState();
	}

	// ドロップ数
	@Override
	public int getDropValue (RandomSource rand, int fortune) {
		return Math.max(1, rand.nextInt(3) + 1);
	}

	public ItemStack getCloneItemStack(BlockGetter get, BlockPos pos, BlockState state) {
		return new ItemStack(this.getCrop());
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext col) {
		return CROP_VEXE[this.getNowState(state)];
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

			if (stack.is(Items.BONE_MEAL)) {
				return InteractionResult.PASS;
			}

			if (this.data == 2 && this.getNowState(state) >= ( this.getMaxBlockState() - 1 ) ) {

			    RandomSource rand = world.random;
				ItemEntity drop = this.getDropItem(world, player, player.getItemInHand(hand), ItemInit.edamame, this.getDropValue(rand, 0));
		        world.addFreshEntity(drop);
		        world.setBlock(pos, state.setValue(this.getSMMaxAge(), this.RCSetState()), 2); //作物の成長段階を2下げる
				this.playCropSound(world, rand, pos);
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

	// 右クリック時アイテムを取得
	public List<ItemStack> rightClickStack (Level level, BlockState state, BlockPos pos) {

		// アイテムの取得
		List<ItemStack> stackList = new ArrayList<>();
		stackList.add(this.getDropStack(level.random));

        if (this.data == 2) {
        	stackList.add(new ItemStack(ItemInit.edamame, this.getDropValue(level.random, 0)));
        }

		// 作物の成長段階を下げる
        level.setBlock(pos, state.setValue(this.getSMMaxAge(), this.RCSetState()), 2);

		return stackList;
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
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
		return this.getDropList(state, builder);
	}

	@Override
	public List<ItemStack> getDropList(BlockState state, LootContext.Builder builder) {

		List<ItemStack> stackList = Lists.newArrayList();
		RandomSource rand = builder.getLevel().random;

		// 最大成長しているなら
		if (this.isMaxAge(state)) {

			stackList.add(new ItemStack(this.getCrop(), rand.nextInt(this.getMaxBlockState()) + 1));

			if (this.getSeed() != this.getCrop()) {
				stackList.add(new ItemStack(this.getSeed(), rand.nextInt(2)));
			}

			if (this.data == 2) {

				Player player = builder.getOptionalParameter(LootContextParams.LAST_DAMAGE_PLAYER);
				ItemStack stack = builder.getOptionalParameter(LootContextParams.TOOL);

				int level = this.getLuck(player, stack);

				if (level >= 0) {
					stackList.add(new ItemStack(ItemInit.edamame, rand.nextInt(this.getMaxBlockState()) + 1 + level));
				}
			}
		}

		else if (this.data == 2 && this.getNowState(state) == 4) {
			stackList.add(new ItemStack(ItemInit.edamame, rand.nextInt(this.getMaxBlockState()) + 1));
		}

		stackList.add(new ItemStack(this.getSeed()));
		return stackList;
	}

	public int getLuck (Player player, ItemStack stack) {

		int level = stack.isEmpty() ? 0 : stack.getEnchantmentLevel(Enchantments.BLOCK_FORTUNE);

		// 幸運上昇
		if (player != null && player.hasEffect(MobEffects.LUCK)) {
			level += player.getEffect(MobEffects.LUCK).getAmplifier();
		}

		return level;
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader render, BlockPos pos) {
		BlockPos underpos = pos.below();
		return this.mayPlaceOn(render.getBlockState(underpos), render, underpos);
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
	public PlantType getPlantType(BlockGetter world, BlockPos pos) {
		return PlantType.CROP;
	}
}
