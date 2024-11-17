package sweetmagic.init.block.magic;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
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
import sweetmagic.init.tile.sm.TileAbstractSM;
import sweetmagic.init.tile.sm.TileSMSpawner;
import sweetmagic.init.tile.sm.TileSMSpawnerBoss;

public class SMSpawner extends BaseFaceBlock implements EntityBlock {

	private final int data;
	private final IntProvider xpRange = UniformInt.of(10, 24);

	public SMSpawner(String name, int data) {
		super(name, setState(Material.METAL, SoundType.METAL, 2F, 8192F));
		this.data = data;
		BlockInfo.create(this, SweetMagicCore.smTab, name);
	}

	// 右クリックしない
	public boolean canRightClick (Player player, ItemStack stack) {
		return player.isCreative() && stack.is(ItemInit.creative_wand);
	}

	// ブロックでのアクション
	public void actionBlock (Level world, BlockPos pos, Player player, ItemStack stack) {
		if (world.isClientSide) { return; }
		this.openGUI(world, pos, player, (TileSMSpawner) this.getTile(world, pos));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		switch (this.data) {
		case 1:  return new TileSMSpawnerBoss(pos, state);
		default: return new TileSMSpawner(pos, state);
		}
	}

	public BlockEntityType<? extends TileAbstractSM> getTileType () {
		switch (this.data) {
		case 1: return TileInit.smSpawnerBoss;
		default: return TileInit.smSpawner;
		}
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		BlockEntityType<? extends TileAbstractSM> tileType = this.getTileType();
		return tileType != null ? this.createMailBoxTicker(world, type, tileType) : null;
	}

	@Override
	public void addBlockTip (List<Component> toolTip) {
		toolTip.add(this.getText("dungen_only").withStyle(GREEN));
	}

	@Override
	public int getExpDrop(BlockState state, LevelReader world, RandomSource rand, BlockPos pos, int fortuneLevel, int silkTouchLevel) {
		return silkTouchLevel == 0 ? this.xpRange.sample(rand) : 0;
	}

	// ドロップするかどうか
	protected boolean isDrop () {
		return false;
	}
}
