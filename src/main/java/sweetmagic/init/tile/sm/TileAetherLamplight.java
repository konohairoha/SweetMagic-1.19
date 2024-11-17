package sweetmagic.init.tile.sm;

import java.util.ArrayList;
import java.util.List;

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
import sweetmagic.init.tile.menu.AetherLamplightMenu;

public class TileAetherLamplight extends TileSMMagic {

	private static final int MIN_RANGE = 1;			// 最小範囲
	private static final int MAX_RANGE = 16;		// 最大範囲
	public int maxMagiaFlux = 50000;				// 最大MF量を設定
	public int range = 12;
	public boolean isRangeView = false;

	public TileAetherLamplight(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public TileAetherLamplight(BlockPos pos, BlockState state) {
		super(TileInit.aetheLamplight, pos, state);
	}

	// サーバー側処理
	@Override
	public void serverTick(Level world, BlockPos pos, BlockState state) {
		super.serverTick(world, pos, state);

		if (this.tickTime % 20 == 0 && !this.isRSPower()) {
			if (this.tickTime % 40 == 0) {
				this.tickTime = 0;
			}
			this.roundMFTransmit();
		}
	}

	// 周囲にMF送信
	public void roundMFTransmit () {

		// 範囲の座標取得
		Iterable<BlockPos> posList = this.getRangePosUnder(this.range);
		List<TileSMMagic> tileList = new ArrayList<>();

		// リスト分まわす
		for (BlockPos pos : posList) {

			// MFブロック以外または送信側なら終了
			if ( !(this.getTile(pos) instanceof TileSMMagic tile) || !tile.getReceive()) { continue; }

			if (tile instanceof TileAetherLamplight || tile instanceof TileAetherLanp) { continue; }

			tileList.add(tile);
		}

		tileList = tileList.stream().sorted( (t1, t2) -> sortTile(t1, t2) ).toList();

		for (TileSMMagic tile : tileList) {

			// MFブロックからMFを入れるときの処理、MFを貯めれなくなったら終了
			this.insertMF(tile, this, this.getTickTime());
			if (this.isMFEmpty()) { break; }
		}

		this.sendPKT();
	}

	// アイテムソート
	public static int sortTile (TileSMMagic tile1, TileSMMagic tile2) {
		boolean isTank1 = tile1 instanceof TileMFTank;
		boolean isTank2 = tile2 instanceof TileMFTank;
		if (isTank1 && isTank2) { return 0; }

		return !isTank1 ? -1 : 0;
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

	// 受信するMF量の取得
	public int getReceiveMF () {
		return 50000;
	}

	// 最大MFの取得
	@Override
	public int getMaxMF () {
		return this.maxMagiaFlux;
	}

	// 受信するMF量の取得
	public int getUserMF () {
		return 5000;
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
		return new AetherLamplightMenu(windowId, inv, this);
	}
}
