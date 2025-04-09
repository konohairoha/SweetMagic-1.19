package sweetmagic.init.tile.sm;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import sweetmagic.init.SoundInit;
import sweetmagic.init.TileInit;
import sweetmagic.recipe.oven.OvenRecipe;

public class TilePot extends TileAbstractSMCook {

	private static final int MAX_CRAFT_TIME = 8;
	public int craftTime = 0;
	public int amount = 0;
	public boolean isCraft = false;
	public boolean isFinish = false;
	public List<ItemStack> craftList = new ArrayList<>();	// クラフト素材
	public List<ItemStack> resultList = new ArrayList<>();	// クラフト後素材

	public TilePot(BlockPos pos, BlockState state) {
		super(TileInit.pot, pos, state);
	}

	public TilePot(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	// サーバー側処理
	public void serverTick(Level world, BlockPos pos, BlockState state) {
		super.serverTick(world, pos, state);
		if (this.tickTime % 10 != 0) { return; }

		// クラフト中以外なら終了
		this.tickTime = 0;
		if (!this.isCraft) { return; }

		// クラフト時間の経過
		this.addCookTime();

		// クラフト時間が一定時間を超えたらクラフト終了
		if (this.isFinishCook()) {
			this.craftFinish();
		}

		else {
			this.playSound(this.getBlockPos(), SoundInit.POT, 0.1F, 1F);
		}
	}

	// クライアント側処理
	public void clientTick(Level world, BlockPos pos, BlockState state) {
		super.clientTick(world, pos, state);
		if (!this.isFinish) { return; }

		for (int i = 0; i < 8; i++) {
			double x = pos.getX() + 0.5D + (this.rand.nextDouble() * 0.5D - 0.25D);
			double y = pos.getY() + 0.75D;
			double z = pos.getZ() + 0.5D + (this.rand.nextDouble() * 0.5D - 0.25D);
			double speedX = 0.0075D - this.rand.nextDouble() * 0.015D;
			double speedY = this.rand.nextDouble() * 0.02375D;
			double speedZ = 0.0075D - this.rand.nextDouble() * 0.015D;

			world.addParticle(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, true, x, y, z, speedX, 0.035D + speedY, speedZ);
		}

		this.isFinish = false;
		this.sendPKT();
	}

	// レシピチェック
	public boolean checkRecipe() {
		return !OvenRecipe.getRecipe(this.level, this.craftList).isEmpty();
	}

	// 作成開始
	public void craftStart() {
		this.isCraft = true;
		this.setState(1);
		this.sendPKT();
		this.playSound(this.getBlockPos(), SoundInit.POT, 0.1F, 1F);

		if (this.player != null) {
			this.resultList = this.setCookQuality(this.player, this.resultList, this.amount);
		}
	}

	// クラフトの完成
	public void craftFinish() {
		this.isCraft = false;
		this.isFinish = true;
		this.setState(2);
		this.sendPKT();
		this.playSound(this.getBlockPos(), SoundInit.STOVE_OFF, 0.1F, 1F);
	}

	// 初期化
	public void clearInfo() {
		this.amount = 0;
		this.craftTime = 0;
		this.isCraft = false;
		this.isFinish = false;
		this.craftList.clear();
		this.resultList.clear();
		this.sendPKT();
	}

	// ドロップリストを取得
	@Override
	public List<ItemStack> getDropList() {
		return this.craftList;
	}

	// 最大料理時間の取得
	public int getMaxCookTime() {
		return MAX_CRAFT_TIME;
	}

	// 料理時間の取得
	public int getCookTime() {
		return this.craftTime;
	}

	// 料理時間の設定
	public void setCookTime(int cookTime) {
		this.craftTime = cookTime;
	}

	// 料理中か
	public boolean isCook() {
		return this.isCraft;
	}

	public List<ItemStack> getCraftList() {
		return this.craftList;
	}

	// NBTの書き込み
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putBoolean("isCraft", this.isCraft);
		tag.putBoolean("isFinish", this.isFinish);
		tag.putInt("craftTime", this.craftTime);
		tag.putInt("amount", this.amount);
		this.saveStackList(tag, this.craftList, "craftList");
		this.saveStackList(tag, this.resultList, "resultList");
	}

	// NBTの読み込み
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.isCraft = tag.getBoolean("isCraft");
		this.isFinish = tag.getBoolean("isFinish");
		this.craftTime = tag.getInt("craftTime");
		this.amount = tag.getInt("amount");
		this.craftList = this.loadAllStack(tag, "craftList");
		this.resultList = this.loadAllStack(tag, "resultList");
	}
}
