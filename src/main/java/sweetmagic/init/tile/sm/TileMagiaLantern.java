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
import sweetmagic.init.tile.menu.MagiaLanternMenu;
import sweetmagic.util.WorldHelper;

public class TileMagiaLantern extends TileSMMagic {

	private int range = 128;
	public int maxMagiaFlux = 100000;
	private static final int MIN_RANGE = 1;			// 最小範囲
	private static final int MAX_RANGE = 1024;		// 最大範囲

	public TileMagiaLantern(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public TileMagiaLantern(BlockPos pos, BlockState state) {
		super(TileInit.magiaLantern, pos, state);
	}

	// サーバー側処理
	public void serverTick(Level world, BlockPos pos, BlockState state) {
		super.serverTick(world, pos, state);
		if (this.tickTime % 20 != 0 || this.isRSPower() || this.getMF() < this.getShrinkMF() || WorldHelper.isPeace(world)) { return; }

		this.setMF(this.getMF() - this.getShrinkMF());
		this.sendInfo();
	}

	public int getShrinkMF() {
		return this.getRange() / 4;
	}

	public int getRange() {
		return this.range;
	}

	// 最大MFの取得
	@Override
	public int getMaxMF() {
		return this.maxMagiaFlux;
	}

	// 受信するMF量の取得
	public int getReceiveMF() {
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
	}

	// NBTの読み込み
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.range = tag.getInt("range");
	}

	public void addRange(int id) {

		int addValue = 0;

		switch (id) {
		case 0:
			addValue = -1;
			break;
		case 1:
			addValue = -10;
			break;
		case 2:
			addValue = -64;
			break;
		case 3:
			addValue = 1;
			break;
		case 4:
			addValue = 10;
			break;
		case 5:
			addValue = 64;
			break;
		}

		this.range = Math.min(MAX_RANGE, Math.max(MIN_RANGE, this.range + addValue));
		this.clickButton();
		this.sendPKT();
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new MagiaLanternMenu(windowId, inv, this);
	}
}
