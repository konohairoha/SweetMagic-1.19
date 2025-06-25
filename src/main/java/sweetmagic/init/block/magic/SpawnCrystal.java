package sweetmagic.init.block.magic;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.ItemInit;
import sweetmagic.init.TileInit;
import sweetmagic.init.block.base.BaseFaceBlock;
import sweetmagic.init.tile.sm.TileSpawnCrystal;

public class SpawnCrystal extends BaseFaceBlock implements EntityBlock {

	private final int data;

	public SpawnCrystal(String name, int data) {
		super(name, setState(Material.GLASS, SoundType.GLASS, -1F, 8192F, 15));
		BlockInfo.create(this, SweetMagicCore.smTab, name);
		this.data = data;
	}

	// 右クリックしない
	public boolean canRightClick(Player player, ItemStack stack) {
		return player.isCreative() && stack.is(ItemInit.creative_wand);
	}

	// ブロックでのアクション
	public boolean actionBlock(Level world, BlockPos pos, Player player, ItemStack stack) {
		if (world.isClientSide) { return true; }
		this.openGUI(world, pos, player, this.getTile(world, pos));
		return true;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileSpawnCrystal(pos, state);
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return this.createMailBoxTicker(world, type, TileInit.spawn_crystal);
	}

	@Override
	public void addBlockTip(List<Component> toolTip) {
		toolTip.add(this.getText("dungen_only").withStyle(GREEN));
	}

	public int getData() {
		return this.data;
	}
}
