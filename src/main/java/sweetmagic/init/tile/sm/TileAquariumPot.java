package sweetmagic.init.tile.sm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.DropperBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraftforge.items.IItemHandler;
import sweetmagic.api.SweetMagicAPI;
import sweetmagic.api.iblock.ISMCrop;
import sweetmagic.init.TileInit;
import sweetmagic.init.block.magic.AquariumPot;
import sweetmagic.init.tile.menu.AquariumPotMenu;

public class TileAquariumPot extends TileSMMagic {

	public int maxMagiaFlux = 500000;				// 最大MF量を設定
	public boolean isReceive = false;				// 受け取る側かどうか
	private int stackCount = 1;						// スタック数
	public static final int MAX_STACKCOUNT = 8;		// 最大スタック数
	private ItemStack stack = ItemStack.EMPTY;
	private Direction face = Direction.NORTH;
//    private final static Direction[] ALLFACE = new Direction[] {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

	protected final StackHandler inputInv = new StackHandler(1);

	public TileAquariumPot(BlockPos pos, BlockState state) {
		super(TileInit.aquariumpot, pos, state);
	}

	public TileAquariumPot(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.resolver = new SingleHandlerProvider(this.inputInv, IN);
	}

	// サーバー側処理
	@Override
	public void serverTick(Level world, BlockPos pos, BlockState state) {
		super.serverTick(world, pos, state);
		this.onUpdate(world, pos, false);

		if (this.tickTime % 20 == 0 && !this.isMaxStackCount()) {
			ItemStack stack = this.getInputItem();
			if (!stack.isEmpty() && stack.is(this.getStack().getItem())) {
				this.addStackCount(stack);
			}
		}

		if (this.tickTime % 600 == 0) {
			this.tickTime = 0;
		}
	}

	// クライアント側処理
	public void clientTick(Level world, BlockPos pos, BlockState state) {
		super.clientTick(world, pos, state);
		this.onUpdate(world, pos, true);

		if (this.tickTime % 200 == 0) {
			this.tickTime = 0;
		}
	}

	public void onUpdate (Level world, BlockPos pos, boolean isClient) {
		if (this.isMaxMF() || isClient) { return; }

		switch (this.getData(pos)) {
		case 0:
			// ドリズリィ
			this.dmPot(world, pos, isClient);
			break;
		case 2:
			// スノードロップ
			this.snodDropPot(world, pos, isClient);
			break;
		case 3:
			// トルコキキョウ
			this.turkeyBalloonPot(world, pos, isClient);
			break;
		case 4:
			// 群青の薔薇
			this.ultramarineRosePot(world, pos, isClient);
			break;
		case 7:
			// ハイドラ
			this.hydoraPot(world, pos, isClient);
			break;
		case 8:
			// カーネーション
			this.carnationPot(world, pos, isClient);
			break;
		case 9:
			// エリックスミシィ
			this.ericsPot(world, pos, isClient);
			break;
		}
	}

	public void addStackCount (ItemStack stack) {

		this.setStackCount(this.getStackCount() + 1);
		CompoundTag tags = stack.getTag();

		if (tags != null) {
			this.setMF(this.getMF() + tags.getInt("mf"));
		}

		this.sentClient();
		stack.shrink(1);
	}

	// ドリズリィ
	public void dmPot (Level world, BlockPos pos, boolean isClient) {

		// 雨が降っていてMFが溜めれる状態なら
		if (world.isRaining()) {

			if (this.tickTime % 20 == 0 && !isClient) {
				this.setMF(this.getMF() + 25 * this.getStackCount());
				this.sentClient();
			}

			else if (this.tickTime % 100 == 0 && isClient) {
				this.spawnParticles(world, pos);
			}
		}

		// 雷発生時に雷を止めてMF生産
		if (this.tickTime % 100 == 0 && world.isThundering()) {

			ResourceKey<Level> overWorld = Level.OVERWORLD;
			MinecraftServer mcServer = world.getServer();
			if (overWorld == null || mcServer == null) { return; }

			ServerLevel server = mcServer.getLevel(overWorld);

			if (server != null && server.getLevelData() instanceof ServerLevelData worldInfo) {
				worldInfo.setThunderTime(0);
				worldInfo.setThundering(false);
				this.setMF(this.getMF() + 6250 * this.getStackCount());
				this.sentClient();
			}
		}
	}

	// スノードロップ
	public void snodDropPot (Level world, BlockPos pos, boolean isClient) {
		if(world.getBiome(pos).get().getBaseTemperature() > 0 && pos.getY() < 120) { return; }

		if (this.tickTime % 20 == 0 && !isClient) {
			this.setMF(this.getMF() + 15 * this.getStackCount());
			this.sentClient();
		}

		if (this.tickTime % 100 == 0 && isClient) {
			this.spawnParticles(world, pos);
		}
	}

	// トルコキキョウ
	public void turkeyBalloonPot (Level world, BlockPos pos, boolean isClient) {
		if (this.tickTime % 10 != 0) { return; }

		// 死んでいるえんちちーが居なければ終了
		List<ExperienceOrb> expList = this.getEntityList(ExperienceOrb.class, 16F).stream().filter(e -> e.getValue() > 0).toList();
		int sumXP = 0;

		for (ExperienceOrb entity : expList) {
			sumXP += entity.getValue();
			entity.discard();
		}

		this.setMF(this.getMF() + sumXP * (8 + this.getStackCount()));
		this.sendInfo();
	}

	// 群青の薔薇
	public void ultramarineRosePot (Level world, BlockPos pos, boolean isClient) {
		if (this.tickTime % 600 != 0) { return; }

		BlockEntity tile = this.getTile(pos.below());
		if (tile == null || tile instanceof HopperBlockEntity || tile instanceof DropperBlockEntity) { return; }

		IItemHandler handler = this.getItemHandler(tile, Direction.UP);
		if (handler == null) { return; }

		int sumMF = 0;

		for (int i = 0; i < handler.getSlots(); i++) {

			ItemStack inStack = handler.extractItem(i, Integer.MAX_VALUE, false);
			if (inStack.isEmpty()) { continue; }

			if (SweetMagicAPI.hasMF(inStack.getItem())) {
				sumMF += SweetMagicAPI.getMF(inStack) * inStack.getCount();
			}

			else {
				sumMF += (2 + this.getStackCount()) * inStack.getCount();
			}

			inStack.shrink(inStack.getCount());
		}

		if (sumMF > 0) {
			this.setMF(this.getMF() + sumMF);
			this.sendPKT();
			this.playSound(pos, SoundEvents.SAND_BREAK, 0.35F, 1F);
		}
	}

	// ハイドラ
	public void hydoraPot (Level world, BlockPos pos, boolean isClient) {
		if (this.tickTime % 10 != 0) { return; }

		// 死んでいるえんちちーが居なければ終了
		List<Monster> entityList = this.getEntityList(Monster.class, 16F).stream().filter(t -> !t.isAlive()).toList();
		if (entityList.isEmpty()) { return; }

		int sumMF = 0;

		for (Monster entity : entityList) {

			CompoundTag tags = entity.getPersistentData();
			if (tags == null || tags.getBoolean("isSMDeadAquarium")) { continue; }

			tags.putBoolean("isSMDeadAquarium", true);
			entity.addAdditionalSaveData(tags);
			sumMF += entity.getMaxHealth() * 20 * this.getStackCount();
		}

		if (sumMF > 0) {

			if (!isClient) {
				this.setMF(this.getMF() + sumMF);
				this.sentClient();
			}

			else {
				this.spawnParticles(world, pos);
			}
		}
	}

	// カーネーション
	public void carnationPot (Level world, BlockPos pos, boolean isClient) {
		if (this.tickTime % 20 != 0) { return; }

		List<ItemStack> stackList = new ArrayList<>();

		// 範囲の座標取得
		Iterable<BlockPos> posList = BlockPos.betweenClosed(pos.offset(-1, 0, -1), pos.offset(1, 0, 1));

		for (BlockPos p : posList) {

			if (p.getX() == 0 && p.getZ() == 0) { continue; }

			BlockState state = this.getState(p);
			Block block = state.getBlock();
			if ( !( block instanceof ISMCrop crop ) || !crop.isMaxAge(state) || !SweetMagicAPI.hasMF(crop.getCrop().asItem())) { continue; }

			stackList.addAll(crop.rightClickStack(world, state, p));
		}

		if (!stackList.isEmpty()) {

			if (!isClient) {

				int sumMF = 0;
				int stackCount = this.getStackCount();

				for (ItemStack stack : stackList) {
					sumMF += (SweetMagicAPI.getMF(stack) * 2F * stackCount);
				}

				this.setMF(this.getMF() + sumMF);
				this.sendPKT();
				this.playSound(pos, SoundEvents.GRASS_PLACE, 0.25F, 0.8F + this.rand.nextFloat() * 0.4F);
			}

			else {
				this.spawnParticles(world, pos);
			}
		}
	}

	// エリックスミシィ
	public void ericsPot (Level world, BlockPos pos, boolean isClient) {
		if (this.getTime() % 7 != 0) { return; }

		BlockPos p = pos.relative(this.face);
		Block block = this.getBlock(p);

		if (block instanceof SnowLayerBlock) {

			if (!isClient) {
				this.level.destroyBlock(p, false);
				this.level.removeBlock(p, false);
				this.setMF((this.getMF() + 25 * this.getStackCount()));
				this.sentClient();
			}

			else {
				this.spawnParticles(world, pos);
			}
		}

		this.face = this.face.getClockWise();
	}

	public int getData (BlockPos pos) {
		if (this.getBlock(pos) instanceof AquariumPot pot) {
			return pot.getData();
		}

		return 0;
	}

	public ItemStack getStack () {
		return this.stack.isEmpty() ? ((AquariumPot) this.getBlock(this.getBlockPos())).getStack() : this.stack;
	}

	// 最大MFの取得
	@Override
	public int getMaxMF () {
		return this.maxMagiaFlux;
	}

	// 受信側かどうかの取得
	public boolean getReceive () {
		return this.isReceive;
	}

	// パーティクルスポーン
	public void spawnParticles(Level world, BlockPos pos) {
		Random rand = this.rand;
		ParticleOptions par = ParticleTypes.HAPPY_VILLAGER;
		for (int i = 0; i < 4; ++i) {
			double d0 = rand.nextGaussian() * 0.02D;
			double d1 = rand.nextGaussian() * 0.02D;
			double d2 = rand.nextGaussian() * 0.02D;
			world.addParticle(par, pos.getX() + 0.5F + this.getRandFloat(0.5F), pos.getY() + 0.5F + this.getRandFloat(0.5F), pos.getZ() + 0.5F + this.getRandFloat(0.5F), d0, d1, d2);
		}
	}

	// NBTの書き込み
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put("inputInv", this.inputInv.serializeNBT());
		tag.putInt("stackCount", this.getStackCount());
	}

	// NBTの読み込み
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.inputInv.deserializeNBT(tag.getCompound("inputInv"));
		this.setStackCount(tag.getInt("stackCount"));
	}

	public int getStackCount () {
		return this.stackCount;
	}

	public void setStackCount (int stackCount) {
		this.stackCount = stackCount;
	}

	public boolean isMaxStackCount () {
		return this.getStackCount() >= MAX_STACKCOUNT;
	}

	// 素材スロットの取得
	public IItemHandler getInput() {
		return this.inputInv;
	}

	// スロットのアイテムを取得
	public  ItemStack getInputItem() {
		return this.getInput().getStackInSlot(0);
	}

	// ゲージの描画量を計算するためのメソッド
	public int getProgressScaled(int value) {
		return Math.min(value, (int) (value * (float) this.getStackCount() / (float) MAX_STACKCOUNT));
    }

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new AquariumPotMenu(windowId, inv, this);
	}

	// ブロック内の情報が空かどうか
	public boolean isInfoEmpty() {
		return this.isMFEmpty() && this.getInputItem().isEmpty() && this.getStackCount() <= 1;
	}
}