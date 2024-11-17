package sweetmagic.init.tile.sm;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.DropperBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import sweetmagic.api.SweetMagicAPI;
import sweetmagic.api.iblock.ISMCrop;
import sweetmagic.init.TileInit;
import sweetmagic.init.block.magic.MFPot;

public class TileMFPot extends TileSMMagic {

	public int maxMagiaFlux = 200000;				// 最大MF量を設定
	public boolean isReceive = false;				// 受け取る側かどうか

	private Direction face = Direction.NORTH;
    private final static Direction[] ALLFACE = new Direction[] {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

	public TileMFPot(BlockPos pos, BlockState state) {
		super(TileInit.mfpot, pos, state);
	}

	public TileMFPot(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	// サーバー側処理
	@Override
	public void serverTick(Level world, BlockPos pos, BlockState state) {
		super.serverTick(world, pos, state);
		this.onUpdate(world, pos, false);

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
		if (this.isMaxMF()) { return; }

		switch (this.getData(pos)) {
		case 0:
			// ドリズリィ
			this.dmPot(world, pos, isClient);
			break;
		case 1:
			// アルストロメリア
			this.alstPot(world, pos, isClient);
			break;
		case 2:
			// スノードロップ
			this.snodDropPot(world, pos, isClient);
			break;
		case 4:
			// 群青の薔薇
			this.rosePot(world, pos, isClient);
			break;
		case 5:
			// ソリッドスター
			this.solidStarPot(world, pos, isClient);
			break;
		case 6:
			// ジニア
			this.zinniaPot(world, pos, isClient);
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
		case 10:
			// コスモス
			this.cosmosPot(world, pos, isClient);
			break;
		}
	}

	// ドリズリィ
	public void dmPot (Level world, BlockPos pos, boolean isClient) {
		if (!world.isRaining()) { return; }

		if (this.tickTime % 20 == 0 && !isClient) {
			this.setMF(this.getMF() + 20);
			this.sentClient();
		}

		else if (this.tickTime % 100 == 0 && isClient) {
			this.spawnParticles(world, pos);
		}
	}

	// アルストロメリア
	public void alstPot (Level world, BlockPos pos, boolean isClient) {

		long worldTime = world.dayTime() % 24000;

		if (worldTime >= 10400 && worldTime <= 14000) {

			if (this.tickTime % 20 == 0 && !isClient) {
				this.setMF(this.getMF() + 100);
				this.sendPKT();
			}

			if (this.tickTime % 100 == 0 && isClient) {
				this.spawnParticles(world, pos);
			}
		}
	}

	// スノードロップ
	public void snodDropPot (Level world, BlockPos pos, boolean isClient) {
		if(world.getBiome(pos).get().getBaseTemperature() > 0 && pos.getY() < 120) { return; }

		if (this.tickTime % 20 == 0 && !isClient) {
			this.setMF(this.getMF() + 10);
			this.sentClient();
		}

		if (this.tickTime % 100 == 0 && isClient) {
			this.spawnParticles(world, pos);
		}
	}

	// 群青の薔薇
	public void rosePot (Level world, BlockPos pos, boolean isClient) {
		if (isClient || this.tickTime % 600 != 0) { return; }

		BlockEntity tile = this.getTile(pos.below());
		if (tile == null || tile instanceof HopperBlockEntity || tile instanceof DropperBlockEntity) { return; }

		IItemHandler handler = this.getItemHandler(tile, Direction.UP);
		if (handler == null) { return; }

		int sumMF = 0;

		for (int i = 0; i < handler.getSlots(); i++) {

			ItemStack inStack = handler.extractItem(i, Integer.MAX_VALUE, false);
			if (inStack.isEmpty()) { continue; }

			sumMF += inStack.getCount();
			inStack.shrink(inStack.getCount());
		}

		if (sumMF > 0) {
			this.setMF(this.getMF() + sumMF);
			this.sendPKT();
			this.playSound(pos, SoundEvents.SAND_BREAK, 0.35F, 1F);
		}
	}

	// ジニア
	public void zinniaPot (Level world, BlockPos pos, boolean isClient) {
		if (this.tickTime % 40 != 0) { return; }

		boolean isCharge = false;
		float sumLightValue = 0F;

		for (int x = -2; x <= 2; ++x) {
			for (int z = -2; z <= 2; ++z) {

				if (x > -2 && x < 2 && z == -1) { z = 2; }

				for (int y = 0; y <= 1; ++y) {

					BlockState state = this.getState(pos.offset(x, y, z));
					Block block = state.getBlock();
					if (block == Blocks.AIR) { continue; }

					float power = block.getLightEmission(state, world, pos) * 0.25F;
					if (power <= 0F) { continue; }

					if (!this.getState(pos.offset(x / 2, 0, z / 2)).isAir()) { return; }

					isCharge = true;
					sumLightValue += power;
				}
			}
		}

		if (isCharge) {
			if (!isClient) {
				this.setMF((int) (this.getMF() + sumLightValue));
				this.sentClient();
			}

			else {
				this.spawnParticles(world, pos);
			}
		}
	}

	// ジニア
	public void solidStarPot (Level world, BlockPos pos, boolean isClient) {
		if (this.tickTime % 40 != 0) { return; }

		float sumLightValue = 0F;

		for (int x = -2; x <= 2; ++x) {
			for (int z = -2; z <= 2; ++z) {

				if (x == 0 && z == 0) { continue; }

				if (x > -2 && x < 2 && z == -1) { z = 2; }

				for (int y = 0; y <= 1; ++y) {

					BlockPos p = pos.offset(x, y, z);
					BlockState state = this.getState(p);
					sumLightValue += state.getBlock().getEnchantPowerBonus(state, world, p);
				}
			}
		}

		if (sumLightValue > 0) {
			if (!isClient) {
				this.setMF((int) (this.getMF() + sumLightValue));
				this.sentClient();
			}

			else {
				this.spawnParticles(world, pos);
			}
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
			if (tags == null || tags.getBoolean("isSMDead")) { continue; }

			tags.putBoolean("isSMDead", true);
			entity.addAdditionalSaveData(tags);

			sumMF += entity.getMaxHealth() * 10;
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

			if ( !( block instanceof ISMCrop crop ) ) { continue; }

			// 作物が最大成長していないまたは、MFを持っていなければ次へ
			if (!crop.isMaxAge(state) || !SweetMagicAPI.hasMF(crop.getCrop().asItem())) { continue; }

			stackList.addAll(crop.rightClickStack(world, state, p));
		}

		if (!stackList.isEmpty()) {

			if (!isClient) {
				int sumMF = 0;

				for (ItemStack stack : stackList) {
					sumMF += (SweetMagicAPI.getMF(stack) * 1.5F);
				}

				this.setMF(this.getMF() + sumMF);
				this.sendPKT();
				this.playSound(pos, SoundEvents.GRASS_PLACE, 0.25F, 0.8F + world.random.nextFloat() * 0.4F);
			}

			else {
				this.spawnParticles(world, pos);
			}
		}
	}

	// エリックスミシィ
	public void ericsPot (Level world, BlockPos pos, boolean isClient) {
		long time = this.getTime();
		if (time % 7 != 0) { return; }

		BlockPos p = pos.relative(this.face);
		Block block = this.getBlock(p);

		if (block instanceof SnowLayerBlock) {

			if (!isClient) {
				this.level.destroyBlock(p, false);
				this.level.removeBlock(p, false);
				this.setMF((this.getMF() + 12));
				this.sentClient();
			}

			else {
				this.spawnParticles(world, pos);
			}
		}

		this.face = this.face.getClockWise();
	}

	// コスモス
	public void cosmosPot (Level world, BlockPos pos, boolean isClient) {
		if (this.tickTime % 60 != 0) { return; }

		int sumMF = 0;

		for (Direction face : ALLFACE) {

			BlockPos p = pos.relative(face);
			BlockState state = this.getState(p);
			Block block = state.getBlock();

			if ( !state.is(Blocks.LAVA) && !(block instanceof BaseFireBlock)) { continue; }

			if (block instanceof LiquidBlock liq && state.getValue(LiquidBlock.LEVEL) != 0) { continue; }

			sumMF += state.is(Blocks.LAVA) ? 1000 : 10;
			world.setBlock(p, Blocks.AIR.defaultBlockState(), 3);
			this.playSound(p, SoundEvents.FIRE_EXTINGUISH, 1F, 1F);
		}

		if (sumMF > 0) {

			if (!isClient) {
				this.setMF(this.getMF() + sumMF);
				this.sendPKT();
			}

			else {
				this.spawnParticles(world, pos);
			}
		}
	}

	public int getData (BlockPos pos) {
		return this.getBlock(pos) instanceof MFPot pot ? pot.getData() : 0;
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
		ParticleOptions par = ParticleTypes.HAPPY_VILLAGER;
		for (int i = 0; i < 4; ++i) {
			double d0 = this.rand.nextGaussian() * 0.02D;
			double d1 = this.rand.nextGaussian() * 0.02D;
			double d2 = this.rand.nextGaussian() * 0.02D;
			world.addParticle(par, this.getRandomX(pos, this.rand, 1D), this.getRandomY(pos, this.rand), this.getRandomZ(pos, this.rand, 1D), d0, d1, d2);
		}
	}

	@Override
	public IItemHandler getInput() {
		return null;
	}
}
