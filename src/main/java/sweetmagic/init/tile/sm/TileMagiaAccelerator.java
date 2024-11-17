package sweetmagic.init.tile.sm;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import sweetmagic.init.TileInit;
import sweetmagic.init.tile.menu.MagiaAcceleratorMenu;

public class TileMagiaAccelerator extends TileSMMagic {

	private static final int MIN_RANGE = 1;			// 最小範囲
	private static final int MAX_RANGE = 16;		// 最大範囲
	public int maxMagiaFlux = 200000;				// 最大MF量を設定
	public int range = 12;
	public boolean isRangeView = false;

	public TileMagiaAccelerator(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public TileMagiaAccelerator(BlockPos pos, BlockState state) {
		super(TileInit.magiaAccelerator, pos, state);
	}

	// サーバー側処理
	@Override
	public void serverTick(Level world, BlockPos pos, BlockState state) {
		super.serverTick(world, pos, state);

		if (this.tickTime % 4 == 0 && !this.isRSPower() && this.getMF() >= 4) {
			this.roundMFAccelerator(world);
		}
	}

	// 周囲のMF回収
	public void roundMFAccelerator (Level world) {

		int sumMF = 0;
		int mf = this.getMF();
		Iterable<BlockPos> posList = this.getRangePosUnder(this.range);

		// リスト分まわす
		for (BlockPos pos : posList) {

			// MFブロック以外、送信側なら終了
			if ( !(this.getTile(pos) instanceof TileSMMagic tile) || !tile.getReceive() || tile instanceof TileMagiaAccelerator) { continue; }

			sumMF += 4;
			tile.serverTick(level, pos, this.getState(pos));
			if (sumMF >= mf) { break; }
		}

		this.setMF(mf - sumMF);
		this.sendPKT();
	}

	public void addRange (int id) {

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
		case 4:
			this.isRangeView = !this.isRangeView;
			break;
		}

		this.range = Math.min(MAX_RANGE, Math.max(MIN_RANGE, this.range + addValue));
		this.clickButton();
		this.sendPKT();
	}

	// 最大MFの取得
	@Override
	public int getMaxMF () {
		return this.maxMagiaFlux;
	}

	// 受信するMF量の取得
	public int getReceiveMF () {
		return 10000;
	}

	@Override
	public IItemHandler getInput() {
		return null;
	}

	// NBTの書き込み
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putInt("range", this.range);
		tag.putBoolean("isRangeView", this.isRangeView);
	}

	// NBTの読み込み
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.range = tag.getInt("range");
		this.isRangeView = tag.getBoolean("isRangeView");
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new MagiaAcceleratorMenu(windowId, inv, this);
	}

	// ブロック内の情報が空かどうか
	public boolean isInfoEmpty() {
		return this.isMFEmpty();
	}
}
