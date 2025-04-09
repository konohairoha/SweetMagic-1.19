package sweetmagic.init.block.sm;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.block.base.BaseFaceBlock;
import sweetmagic.init.tile.sm.TileAbstractSM;
import sweetmagic.init.tile.sm.TileCardboardStorage;

public class CardboardStorage extends BaseFaceBlock implements EntityBlock {

	public CardboardStorage(String name) {
		super(name, setState(Material.WOOD, SoundType.WOOL, 0.5F, 8192F));
		BlockInfo.create(this, SweetMagicCore.smTab, name);
	}

	// 右クリック出来るか
	public boolean canRightClick(Player player, ItemStack stack) {
		return true;
	}

	// ブロックでのアクション
	public boolean actionBlock(Level world, BlockPos pos, Player player, ItemStack stack) {
		if (world.isClientSide) { return true; }
		TileCardboardStorage tile = (TileCardboardStorage) this.getTile(world, pos);
		tile.sendPKT();
		this.openGUI(world, pos, player, tile);
		return true;
	}

	// ドロップするかどうか
	protected boolean isDrop() {
		return false;
	}

	// tileの中身を保持するか
	public boolean isKeepTile() {
		return true;
	}

	public ItemStack getDropStack(TileAbstractSM tile) {
		return tile.getDropStack(new ItemStack(this));
	}

	@Override
	public void addBlockTip(List<Component> toolTip) {
		toolTip.add(this.getText("sm_chest").withStyle(GREEN));
		toolTip.add(this.getText(this.name).withStyle(GREEN));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileCardboardStorage(pos, state);
	}
}
