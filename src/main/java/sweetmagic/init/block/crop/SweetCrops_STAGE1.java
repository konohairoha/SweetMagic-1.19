package sweetmagic.init.block.crop;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.ForgeHooks;
import sweetmagic.api.iblock.ISMCrop;
import sweetmagic.init.BlockInit;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.ItemInit;
import sweetmagic.init.block.base.BaseFaceBlock;
import sweetmagic.init.block.base.BaseSMBlock;
import sweetmagic.init.item.sm.SMSickle;

public class SweetCrops_STAGE1 extends BaseFaceBlock implements ISMCrop, BonemealableBlock {

	private final String name;
	private final int data;
	private int chance;

	public SweetCrops_STAGE1(String name, int data, int chance) {
		super(name, BaseSMBlock.setState(Material.WOOD, SoundType.WOOD, 1F, 8192F).randomTicks());
		this.name = name;
		this.data = data;
		this.chance = chance;
		this.registerDefaultState(this.setState().setValue(this.getSMMaxAge(), 0));
		BlockInit.blockMap.put(new BlockInfo(this, null), this.name);
	}

	/**
	 * 0 = メープル
	 */

	// 作物の取得
	@Override
	public ItemLike getCrop() {
		switch (this.data) {
		default: return ItemInit.maple_syrup;
		}
	}

	public ItemLike getSeed() {
		return null;
	}

	// 最大成長段階
	@Override
	public int getMaxBlockState() {
		return 1;
	}

	// 最大成長段階
	@Override
	public IntegerProperty getSMMaxAge() {
		return ISMCrop.AGE1;
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
		return Math.max(1, rand.nextInt(3) + 1);
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
			BlockState glowState = state.setValue(this.getSMMaxAge(), nowAge + 1).setValue(FACING, state.getValue(FACING));
			level.setBlock(pos, glowState, 2);
			level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(glowState));
			ForgeHooks.onCropsGrowPost(level, pos, state);
		}
	}

	public void growCrops(Level world, BlockPos pos, BlockState state) {
		int age = Math.min(this.getNowState(state) + this.getBonemealAgeIncrease(world), this.getMaxBlockState());
		world.setBlock(pos, this.getStateForAge(age).setValue(FACING, state.getValue(FACING)), 2);
	}

	// 右クリック
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {

		ItemStack stack = player.getItemInHand(hand);

		if (stack.getItem() instanceof SMSickle sickle) {
			sickle.getPickPlant(world, player, pos, stack);
			return InteractionResult.SUCCESS;
		}

		// 最大成長していないなら終了
		if (!this.isMaxAge(state)) { return InteractionResult.PASS; }

		this.onRicghtClick(world, player, state, pos, stack);
		return InteractionResult.sidedSuccess(world.isClientSide());
	}

	// 右クリック
	public void onRicghtClick(Level world, Player player, BlockState state, BlockPos pos, ItemStack stack) {
		RandomSource rand = world.getRandom();
		ItemEntity drop = this.getDropItem(world, player, stack, this.getCrop(), this.getDropValue(rand, 0));
		world.addFreshEntity(drop);
		world.setBlock(pos, state.setValue(this.getSMMaxAge(), this.RCSetState()).setValue(FACING, state.getValue(FACING)), 2); //作物の成長段階を2下げる
		world.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.5F, 0.8F + rand.nextFloat() * 0.4F);
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> bui) {
		bui.add(this.getSMMaxAge(), FACING);
	}

	@Override
	public boolean isValidBonemealTarget(BlockGetter get, BlockPos pos, BlockState state, boolean falg) {
		return false;
	}

	@Override
	public boolean isBonemealSuccess(Level world, RandomSource rand, BlockPos pos, BlockState state) {
		return false;
	}

	@Override
	public void performBonemeal(ServerLevel world, RandomSource rand, BlockPos pos, BlockState state) {
		this.growCrops(world, pos, state);
	}

	@Override
	public int getBonemealAgeIncrease(Level world) {
		return Mth.nextInt(world.getRandom(), 0, 1);
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder build) {
		List<ItemStack> stackList = this.getDropList(state, build);

		if (build.getOptionalParameter(LootContextParams.TOOL).getItem() instanceof AxeItem) {
			stackList.add(new ItemStack(BlockInit.maple_hole_log));
		}

		else if(!this.isMaxAge(state)) {
			stackList.add(new ItemStack(BlockInit.maple_log));
		}

		return stackList;
	}
}
