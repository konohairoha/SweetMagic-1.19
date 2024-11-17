package sweetmagic.init.tile.sm;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import sweetmagic.init.block.base.BaseCookBlock;

public abstract class TileAbstractSMCook extends TileAbstractSM {

	public boolean hasFork = false;

	public TileAbstractSMCook(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	// 最大料理時間の取得
	public abstract int getMaxCookTime();

	// 料理時間の取得
	public abstract int getCookTime();

	// 料理時間の設定
	public abstract void setCookTime(int ｃookTime);

	// 料理中か
	public abstract boolean isCook();

	// レシピチェック
	public abstract boolean checkRecipe ();

	// 作成開始
	public abstract void craftStart ();

	// クラフトの完成
	public abstract void craftFinish ();

	// 初期化
	public abstract void clearInfo ();

	public abstract List<ItemStack> getDropList ();

	// 料理時間の経過
	public void addCookTime () {
		this.setCookTime(this.getCookTime() + 1);
	}

	// 料理時間を超えているか
	public boolean isFinishCook () {
		return this.getCookTime() >= this.getMaxCookTime();
	}

	public void setState (int data) {
		if (this.getBlock(this.getBlockPos()) instanceof BaseCookBlock block) {
			block.setState(this.level, this.getBlockPos(), data);
		}
	}

	// 料理の状態を取得
	public int getCookData () {
		return this.getState(this.getBlockPos()).getValue(BaseCookBlock.COOK);
	}

	// NBTの書き込み
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putBoolean("hasFork", this.hasFork);
	}

	// NBTの読み込み
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.hasFork = tag.getBoolean("hasFork");
	}
}
