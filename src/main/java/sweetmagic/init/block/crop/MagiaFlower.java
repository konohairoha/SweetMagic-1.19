package sweetmagic.init.block.crop;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.ServerLevelData;
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

public class MagiaFlower extends BushBlock implements ISMCrop {

	private final int data;
	private int chance;
	private static final VoxelShape[] CROP_VEXE = new VoxelShape[] {
		Block.box(1.6D, 0D, 1.6D, 14.4D, 1D, 14.4D),
		Block.box(1.6D, 0D, 1.6D, 14.4D, 5D, 14.4D),
		Block.box(1.6D, 0D, 1.6D, 14.4D, 7D, 14.4D),
		Block.box(1.6D, 0D, 1.6D, 14.4D, 10D, 14.4D)
	};

	public MagiaFlower(String name, int data, int chance) {
		super(BaseSMBlock.setState(Material.PLANT, SoundType.GRASS, 0F, 8192F).noCollission().randomTicks());
		this.data = data;
		this.chance = chance;
		this.registerDefaultState(this.stateDefinition.any().setValue(this.getSMMaxAge(), 0));
		BlockInfo.create(this, null, name);
	}

	/**
	 * 0 = サニーフラワー
	 * 1 = ムーンブロッサム
	 * 2 = ドリズリィミオソチス
	 */

	// 作物の取得
	@Override
	public ItemLike getCrop() {
		switch (this.data) {
		case 1: return ItemInit.moonblossom_petal;
		case 2: return ItemInit.dm_flower;
		default: return ItemInit.sannyflower_petal;
		}
	}

	// 種の取得
	@Override
	public ItemLike getSeed() {
		switch (this.data) {
		case 1: return ItemInit.moonblossom_seed;
		case 2: return ItemInit.drizzly_mysotis_seed;
		default: return ItemInit.sannyflower_seed;
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
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext col) {
		return CROP_VEXE[this.getNowState(state)];
	}

	// 成長処理チャンスを与えるかどうか
	public boolean isRandomlyTicking(BlockState state) {
		return true;
	}

	public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource rand) {

		boolean isDayTime = world.getDayTime() % 24000 < 12000;		// 現在の時間の取得
		boolean isGlow = this.canGlow(world, isDayTime);			// 成長できるかのフラグ

		// 最大まで成長しているなら終了
		if (this.isMaxAge(state)) {

			// 夜なら成長段階を1つ戻す
			if (!isGlow) {
				this.glowDown(world, state, pos);
			}
			return;
		}

		int addChence = 0;

		if(this.data == 2 && world.getLevelData() instanceof ServerLevelData worldInfo) {
			addChence = worldInfo.isThundering() ? -1 : 0;
		}

		if (isGlow && ForgeHooks.onCropsGrowPre(world, pos, state, this.isGlowChange(rand, addChence))) {
			this.glowUp(world, state, pos);
		}
	}

	// 成長できるかどうかチェック
	public boolean canGlow(Level world, boolean isDayTime) {
		return ((isDayTime && this.data == 0) || (!isDayTime && this.data == 1)) || (world.isRaining() && this.data == 2);
	}

	// 成長度ダウン
	public void glowDown(Level world, BlockState state, BlockPos pos) {
		world.setBlock(pos, state.setValue(this.getSMMaxAge(), this.getMaxBlockState() - 1), 2);
	}

	// 成長度ダウン
	public void glowUp(Level world, BlockState state, BlockPos pos) {
		int nowAge = this.getNowState(state);
		BlockState glowState = state.setValue(this.getSMMaxAge(), nowAge + 1);
		world.setBlock(pos, glowState, 2);
		world.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(glowState));
		ForgeHooks.onCropsGrowPost(world, pos, state);
	}

	// 成長できるかどうか
	public boolean isGlowChange(RandomSource rand, int addChance) {
		return rand.nextInt(this.getGlowChange() + addChance) == 0;
	}

	// 右クリック
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {

		ItemStack stack = player.getItemInHand(hand);

		 if (stack.getItem() instanceof SMSickle sickle) {
			sickle.getPickPlant(world, player, pos, stack);
			return InteractionResult.SUCCESS;
		}

		// 最大成長していないなら終了
		if (!this.isMaxAge(state)) { return InteractionResult.SUCCESS; }

		this.onRicghtClick(world, player, state, pos, player.getItemInHand(hand));
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

	public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource rand) {
		if (rand.nextFloat() <= 0.95F) { return; }

		boolean isDayTime = world.getDayTime() % 24000 < 12000;

		if ((this.data == 0 && isDayTime) || (this.data == 1 && !isDayTime) || (world.isRaining() && this.data == 2)) {
			this.addParticlesAroundSelf(world, rand, pos, ParticleTypes.HAPPY_VILLAGER);
		}
	}

	protected void addParticlesAroundSelf(Level level, RandomSource rand, BlockPos pos, ParticleOptions par) {
		for (int i = 0; i < 4; ++i) {
			double d0 = rand.nextDouble() * 0.02D;
			double d1 = rand.nextDouble() * 0.02D;
			double d2 = rand.nextDouble() * 0.02D;
			level.addParticle(par, this.getRandomX(pos, rand, 0.5D), this.getRandomY(pos, rand), this.getRandomZ(pos, rand, 0.5D), d0, d1, d2);
		}
	}

	public double getRandomX(BlockPos pos, RandomSource rand, double scale) {
		return pos.getX() + ((rand.nextDouble() - 0.5D) * scale) + 0.5D;
	}

	public double getRandomY(BlockPos pos, RandomSource rand) {
		return pos.getY() + rand.nextDouble();
	}

	public double getRandomZ(BlockPos pos, RandomSource rand, double scale) {
		return pos.getZ() + ((rand.nextDouble() - 0.5D) * scale) + 0.5D;
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder build) {
		return this.getDropList(state, build);
	}

	@Override
	public PlantType getPlantType(BlockGetter world, BlockPos pos) {
		return PlantType.CROP;
	}
}
