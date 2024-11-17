package sweetmagic.init.block.crop;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.PlantType;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.iblock.ISMCraftBlock;
import sweetmagic.api.iblock.ISMCrop;
import sweetmagic.api.util.ISMTip;
import sweetmagic.init.BlockInit;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.ItemInit;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.SoundInit;
import sweetmagic.init.TileInit;
import sweetmagic.init.block.base.BaseSMBlock;
import sweetmagic.init.block.magic.PedalCreate;
import sweetmagic.init.item.magic.MFTime;
import sweetmagic.init.item.magic.MFWeather;
import sweetmagic.init.tile.sm.TileAbstractSM;
import sweetmagic.init.tile.sm.TileAlstroemeria;
import sweetmagic.recipe.RecipeHelper;
import sweetmagic.recipe.RecipeUtil;
import sweetmagic.recipe.alstrameria.AlstroemeriaRecipe;
import sweetmagic.recipe.base.AbstractRecipe;
import sweetmagic.util.SMUtil;

public class Alstroemeria extends BushBlock implements ISMCrop, EntityBlock, ISMCraftBlock, ISMTip {

	private final String name;

	private static final VoxelShape[] CROP_VEXE = new VoxelShape[] {
		Block.box(4D, 0D, 4D, 12D, 6D, 12D),
		Block.box(2D, 0D, 2D, 14D, 9.6D, 14D)
	};

	public Alstroemeria(String name) {
		super(BaseSMBlock.setState(Material.PLANT, SoundType.GRASS, 0F, 8192F).noCollission());
		this.registerDefaultState(this.stateDefinition.any().setValue(this.getSMMaxAge(), 0));
		BlockInfo.create(this, SweetMagicCore.smMagicTab, name);
		this.name = name;
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
	public void setGlowChance (int chance) { }

	// 成長チャンスの取得
	@Override
	public int getGlowChange() {
		return 0;
	}

	// 右クリック回収時に戻る成長段階
	@Override
	public int RCSetState () {
		return 1;
	}

	// 作物の取得
	@Override
	public ItemLike getCrop() {
		return BlockInit.twilight_alstroemeria;
	}

	// 種の取得
	@Override
	public ItemLike getSeed() {
		return BlockInit.twilight_alstroemeria;
	}

	// デフォルトステータス取得
	@Override
	public BlockState getDefault () {
		return this.defaultBlockState();
	}

	// ドロップ数
	@Override
	public int getDropValue (RandomSource rand, int fortune) {
		return 0;
	}

	public ItemStack getCloneItemStack(BlockGetter get, BlockPos pos, BlockState state) {
		return new ItemStack(this);
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext col) {
		return CROP_VEXE[this.getNowState(state)];
	}

	// 右クリック
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {

		ItemStack stack = player.getItemInHand(hand);
		boolean isTimeChange = false;

		if (player.isShiftKeyDown() && stack.isEmpty()) {
			isTimeChange = this.timeSet(world, player.getInventory().items, player);
		}

		// 時間操作時の音
		if (isTimeChange) {
			this.playerSound(world, pos, SoundInit.CHANGETIME, 0.0625F, 1F);
		}

		// 最大成長していないなら終了
		if (!this.isMaxAge(state)) {

			if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() == BlockInit.twilightlight) {
				this.setTwilightlight(world, pos, player, stack, blockItem.getBlock());
			}

			return InteractionResult.SUCCESS;
		}

		// アルストロメリアクラフト
		if ( !player.isShiftKeyDown() && !stack.isEmpty()) {
			this.getRecipeAlstroemeria(world, pos, player, stack, false);
		}

		return InteractionResult.sidedSuccess(world.isClientSide);
	}

	// 時間、天気操作
	public boolean timeSet (Level world, NonNullList<ItemStack> pInv, Player player) {

		Boolean tmFlg = false;

		Object[] objSun = SMUtil.getStackFromPInv(pInv, ItemInit.sannyflower_petal, (byte) 1),
				objMoon = SMUtil.getStackFromPInv(pInv, ItemInit.moonblossom_petal, (byte) 1),
				objDM = SMUtil.getStackFromPInv(pInv, ItemInit.dm_flower, (byte) 1),
				obFire = SMUtil.getStackFromPInv(pInv, ItemInit.fire_nasturtium_petal, (byte) 1
		);

		// ドリズリィ・ミオソチスのお花を持っている状態だったら1日雨にする
		if (objDM != null) {
			MFWeather item = (MFWeather) SMUtil.getItem(objDM[1]);
			item.changeWeather(world);
			SMUtil.getStack(objDM[1]).shrink(1);
			tmFlg = true;
		}

		// ファイアーナスタチウムを持っていたら晴れにする
		else if (obFire != null) {
			MFWeather item = (MFWeather) SMUtil.getItem(obFire[1]);
			item.changeWeather(world);
			SMUtil.getStack(obFire[1]).shrink(1);
			tmFlg = true;
		}

		// 時間操作のお花の処理
		else if (objSun != null) {

			// 夕方設定
			if (objMoon != null) {

				this.setTime(world, 10400);
				SMUtil.getStack(objSun[1]).shrink(1);
				SMUtil.getStack(objMoon[1]).shrink(1);
			}

			// 朝に設定
			else {
				ItemStack stack = SMUtil.getStack(objSun[1]);
				this.setTime(world, this.getMFTime(stack.getItem()).getTime());
				stack.shrink(1);
			}

			tmFlg = true;
		}

		// 夜に設定
		else if (objMoon != null) {
			ItemStack stack = SMUtil.getStack(objMoon[1]);
			this.setTime(world, this.getMFTime(stack.getItem()).getTime());
			stack.shrink(1);
			tmFlg = true;
		}

		return tmFlg;
	}

	// 変更する天気と時間を取得
	public TimeWeatherType getTimeWeather (Level world, NonNullList<ItemStack> pInv, Player player) {

		TimeWeatherType timeWeather = null;

		Object[] objSun = SMUtil.getStackFromPInv(pInv, ItemInit.sannyflower_petal, (byte) 1),
				objMoon = SMUtil.getStackFromPInv(pInv, ItemInit.moonblossom_petal, (byte) 1),
				objDM = SMUtil.getStackFromPInv(pInv, ItemInit.dm_flower, (byte) 1),
				obFire = SMUtil.getStackFromPInv(pInv, ItemInit.fire_nasturtium_petal, (byte) 1
		);

		// ドリズリィ・ミオソチスのお花を持っている状態だったら1日雨にする
		if (objDM != null) {
			return TimeWeatherType.RAIN;
		}

		// ファイアーナスタチウムを持っていたら晴れにする
		else if (obFire != null) {
			return TimeWeatherType.SUN;
		}

		// 時間操作のお花の処理
		else if (objSun != null) {
			return objMoon != null ? TimeWeatherType.TWILIGHT : TimeWeatherType.DAYTIME;
		}

		// 夜に設定
		else if (objMoon != null) {
			return TimeWeatherType.NIGHT;
		}

		return timeWeather;
	}

	// 時間設定
	public void setTime (Level world, int time) {
		if ( !( world instanceof ServerLevel sever ) ) { return; }
		int dayTime = 24000;
        long day = (sever.getDayTime() / dayTime) + 1;
		sever.setDayTime(time + (day * dayTime));
	}

	// 時間操作アイテムを取得
	public MFTime getMFTime(Item item) {
		return (MFTime) item;
	}

	public void setTwilightlight (Level world, BlockPos pos, Player player, ItemStack stack, Block block) {
		if (!world.getBlockState(pos.above()).isAir()) { return; }

		world.setBlock(pos.above(), block.defaultBlockState(), 3);
		world.setBlock(pos, world.getBlockState(pos).setValue(this.getSMMaxAge(), 1), 3);

        SoundType sound = this.getSoundType(block.defaultBlockState(), world, pos.above(), player);
        this.playerSound(world, pos.above(), sound.getPlaceSound(),(sound.getVolume() + 1F) / 2F, sound.getPitch() * 0.8F);
        this.bloomAlstoemeria(world, pos.below());

        if (!player.isCreative()) { stack.shrink(1); }
	}


	// アルストロメリアレシピの取得
	public boolean getRecipeAlstroemeria (Level world, BlockPos pos, Player player, ItemStack stack, boolean isAllCraft) {

		// レシピを取得して見つからなければ終了
		List<ItemStack> stackList = RecipeHelper.getPlayerInv(player, stack);
		Optional<AlstroemeriaRecipe> recipe = AlstroemeriaRecipe.getRecipe(world, stackList);
		if (recipe.isEmpty()) { return true; }

		if (!world.isClientSide) {

			// クラフト要求アイテムの消費とクラフト後のアイテム取得
			RecipeUtil recipeUtil = isAllCraft ?
					RecipeHelper.recipeAllCraft(stackList, recipe.get()) :
					RecipeHelper.recipeSingleCraft(stackList, recipe.get());

			// レシピから完成品を取得
			List<ItemStack> resultList = recipeUtil.getResultList();
			ItemStack inputStack = recipeUtil.getInputList().get(0);

			if (inputStack.getItem() instanceof BlockItem bItem && bItem.getBlock() instanceof PedalCreate) {
				CompoundTag tags = inputStack.getTag();
				resultList.get(0).setTag(tags);
			}

			resultList.forEach(s -> world.addFreshEntity(new ItemEntity(world, player.xo, player.yo, player.zo, s.copy())));
			this.playerSound(world, pos, SoundEvents.FIREWORK_ROCKET_BLAST_FAR, 0.5F, 1F / (world.random.nextFloat() * 0.4F + 1.2F) + 1 * 0.5F);
		}

		else {
			for (int i = 0; i < 4; i ++) {
				world.addParticle(ParticleTypes.LAVA, pos.getX() + 0.5D, pos.getY() + 0.75D, pos.getZ() + 0.5D, 0D, 0D, 0D);
			}
		}
		return true;

	}

	public void playerSound (Level world, BlockPos pos, SoundEvent sound, float vol, float pitch) {
		world.playSound(null, pos, sound, SoundSource.BLOCKS, vol, pitch);
	}

	public void bloomAlstoemeria (Level world, BlockPos pos) {
		if ( !(world instanceof ServerLevel sever) ) { return; }

		float posX = pos.getX() + 0.5F;
		float posY = pos.getY() + 1.1F;
		float posZ = pos.getZ() + 0.5F;
		RandomSource rand = world.random;

		for (int k = 0; k < 6; k++) {
			float f1 = (float) posX - 0.625F + rand.nextFloat() * 1.25F;
			float f2 = (float) posY + 0.5F + rand.nextFloat() * 0.5F;
			float f3 = (float) posZ - 0.625F + rand.nextFloat() * 1.25F;

			sever.sendParticles(ParticleInit.TWILIGHTLIGHT.get(), f1, f2, f3, 2, 0F, 0F, 0F, 0.01F);
		}
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> def) {
		def.add(this.getSMMaxAge());
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileAlstroemeria(pos, state);
	}

	@Nullable
	protected static <T extends BlockEntity> BlockEntityTicker<T> createMailBoxTicker(Level level, BlockEntityType<T> bEntityType, BlockEntityType<? extends TileAbstractSM> grill) {
		return createTickerHelper(bEntityType, grill, level.isClientSide() ? TileAbstractSM::clientTick : TileAbstractSM::serverTick);
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createMailBoxTicker(level, type, TileInit.alst);
	}

	@Nullable
	protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper( BlockEntityType<A> type1, BlockEntityType<E> type2, BlockEntityTicker<? super E> ticker) {
		return type2 == type1 ? (BlockEntityTicker<A>) ticker : null;
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
		return Arrays.<ItemStack> asList(new ItemStack(this.getCrop()));
	}

	@Override
	public PlantType getPlantType(BlockGetter world, BlockPos pos) {
		return PlantType.CROP;
	}

	public boolean notNullRecipe (Level world, List<ItemStack> stackList) {
		return !AlstroemeriaRecipe.getRecipe(world, stackList).isEmpty();
	}

	public AbstractRecipe getRecipe (Level world, List<ItemStack> stackList) {
		return AlstroemeriaRecipe.getRecipe(world, stackList).get();
	}

	public boolean canShiftCraft () {
		return true;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter get, List<Component> toolTip, TooltipFlag flag) {
		toolTip.add(this.getTipArray(this.getText(this.name + "_open"), GOLD));
		toolTip.add(this.getTipArray(this.getText(this.name), GREEN));
	}

	public static enum TimeWeatherType {
		SUN,
		RAIN,
		DAYTIME,
		TWILIGHT,
		NIGHT
	}
}
