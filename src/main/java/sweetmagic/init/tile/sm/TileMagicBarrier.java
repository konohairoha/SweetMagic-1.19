package sweetmagic.init.tile.sm;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import sweetmagic.init.PotionInit;
import sweetmagic.init.TileInit;
import sweetmagic.init.tile.menu.MagicBarrierMenu;

public class TileMagicBarrier extends TileAbstractSM {

	public int range = 32;

	public TileMagicBarrier(BlockPos pos, BlockState state) {
		super(TileInit.barrierGlass, pos, state);
	}

	public TileMagicBarrier(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	// サーバー側処理
	@Override
	public void serverTick(Level world, BlockPos pos, BlockState state) {
		super.serverTick(world, pos, state);
		if (this.tickTime % 100 != 0) { return; }

		List<Player> playerList = this.getEntityList(Player.class, e -> e.isAlive() && !e.isCreative() && !e.isSpectator(), this.range);
		playerList.forEach(p -> this.addPotion(p, PotionInit.non_destructive, 130, 0));
	}

	public void removeDestructive (Level world) {
		List<Player> playerList = this.getEntityList(Player.class, e -> e.isAlive() && !e.isCreative() && !e.isSpectator(), this.range * 2);
		playerList.forEach(p -> p.removeEffect(PotionInit.non_destructive));
	}

	// ボタンクリック
	public void clickButton (int id) {
		switch (id) {
		case 0:
			this.setRange(Math.min(128, this.getRange() + 1));
			break;
		case 1:
			this.setRange(Math.max(1, this.getRange() - 1));
			break;
		}

		this.sendPKT();
		this.clickButton();
	}

	// スポーン範囲の取得
	public int getRange () {
		return this.range;
	}

	// スポーン範囲の設定
	public void setRange (int range) {
		this.range = range;
	}

	// NBTの書き込み
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putInt("range", this.getRange());
	}

	// NBTの読み込み
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.setRange(tag.getInt("range"));
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new MagicBarrierMenu(windowId, inv, this);
	}
}
