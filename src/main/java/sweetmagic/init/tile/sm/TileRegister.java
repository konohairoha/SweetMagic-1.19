package sweetmagic.init.tile.sm;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import sweetmagic.init.TileInit;
import sweetmagic.init.tile.menu.RegisterMenu;
import sweetmagic.util.PlayerHelper;

public class TileRegister extends TileAbstractSM {

	public int exp;

	public TileRegister(BlockPos pos, BlockState state) {
		super(TileInit.register, pos, state);
	}

	// NBTの書き込み
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putInt("exp", this.exp);
	}

	// NBTの読み込み
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.exp = tag.getInt("exp");
	}

	public int getExpForLevel(int level) {
		return PlayerHelper.getExpForLevel(Math.max(0, level));
	}

	public int getExpLevel() {
		return this.getExpLevel(this.exp);
	}

	public int getExpLevel(int exp) {
		return PlayerHelper.getLevelForExperience(exp);
	}

	public void addButton(Player player, int id) {
		if(!this.isSever()) { return; }

		this.clickButton();
		int playerExp = player.totalExperience;
		if(playerExp <= 0 && id >= 3) { return; }

		int addValue = 0;
		int playerLevel = player.experienceLevel;

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

		if (addValue > 0) {
			int nowLevel = this.getExpForLevel(playerLevel);
			int shrinkExp = (int) (nowLevel - this.getExpForLevel(playerLevel - addValue) + player.experienceProgress * (this.getExpForLevel(playerLevel + 1) - nowLevel));
			PlayerHelper.addExp(player, -shrinkExp);
			this.exp += shrinkExp;
		}

		else {
			int nowLevel = this.getExpForLevel(playerLevel);
			int addExp = (int) Math.min(this.exp, this.getExpForLevel(playerLevel - addValue) - nowLevel - player.experienceProgress * (this.getExpForLevel(playerLevel + 1) - nowLevel));
			PlayerHelper.addExp(player, addExp);
			this.exp -= addExp;
		}

		this.sendPKT();
	}

	// ブロック内の情報が空かどうか
	public boolean isInfoEmpty() {
		return this.exp <= 0;
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new RegisterMenu(windowId, inv, this);
	}
}
