package sweetmagic.init.tile.sm;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import sweetmagic.init.TileInit;

public class TileHightAetherLamplight extends TileAetherLanp {

	private static final int MIN_RANGE = 1;			// 最小範囲
	private static final int MAX_RANGE = 64;		// 最大範囲
	public int maxMagiaFlux = 200000;				// 最大MF量を設定
	public int range = 12;

	public TileHightAetherLamplight(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public TileHightAetherLamplight(BlockPos pos, BlockState state) {
		super(TileInit.hightAetheLamplight, pos, state);
	}

	public void addRange(int id) {

		int addValue = 0;

		switch (id) {
		case 0:
			addValue = 1;
			break;
		case 1:
			addValue = 10;
			break;
		case 2:
			addValue = -1;
			break;
		case 3:
			addValue = -10;
			break;
		}

		this.range = Math.min(MAX_RANGE, Math.max(MIN_RANGE, this.range + addValue));
		this.clickButton();
		this.sendPKT();
	}

	// NBTの書き込み
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putInt("range", this.range);
	}

	// NBTの読み込み
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.range = tag.getInt("range");
	}

	public int getRange() {
		return this.range;
	}

	// 受信するMF量の取得
	public int getReceiveMF() {
		return 100000;
	}

	// 最大MFの取得
	@Override
	public int getMaxMF() {
		return this.maxMagiaFlux;
	}

	// 受信するMF量の取得
	public int getUserMF() {
		return 10000;
	}
}
