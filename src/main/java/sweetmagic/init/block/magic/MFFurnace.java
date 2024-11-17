package sweetmagic.init.block.magic;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import sweetmagic.init.TileInit;
import sweetmagic.init.block.base.BaseMFBlock;
import sweetmagic.init.tile.sm.TileAbstractSM;
import sweetmagic.init.tile.sm.TileMFFurnace;
import sweetmagic.init.tile.sm.TileMFFurnaceAdvanced;

public class MFFurnace extends BaseMFBlock {

	private final int data;
	public static final BooleanProperty ISCRAFT = BooleanProperty.create("iscraft");

	public MFFurnace(String name, int data) {
		super(name, setState(Material.PISTON, SoundType.METAL, 1F, 8192F, 5));
		this.data = data;
		this.registerDefaultState(this.setState().setValue(ISCRAFT, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> build) {
		build.add(FACING, ISCRAFT);
	}

	// 最大MFの取得
	public int getMaxMF () {
		switch(this.data) {
		case 1: return 200000;
		default : return 20000;
		}
	}

	@Override
	public int getTier() {
		return this.data + 1;
	}

	// ブロックでのアクション
	public void actionBlock(Level world, BlockPos pos, Player player, ItemStack stack) {
		if (world.isClientSide) { return; }

		MenuProvider tile = null;

		switch (this.data) {
		case 1:
			tile = (TileMFFurnaceAdvanced) world.getBlockEntity(pos);
			break;
		default:
			tile = (TileMFFurnace) world.getBlockEntity(pos);
			break;
		}

		this.openGUI(world, pos, player, tile);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		switch(this.data) {
		case 1: return new TileMFFurnaceAdvanced(pos, state);
		default: return new TileMFFurnace(pos, state);
		}
	}

	public BlockEntityType<? extends TileAbstractSM> getTileType () {
		switch (this.data) {
		case 1: return TileInit.mffurnaceAdavance;
		default: return TileInit.mffurnace;
		}
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		BlockEntityType<? extends TileAbstractSM> tileType = this.getTileType();
		return tileType != null ? this.createMailBoxTicker(world, type, tileType) : null;
	}

	// RS信号で停止するかどうか
	public boolean isRSStop () {
		return true;
	}

	public void addTip (List<Component> toolTip, ItemStack stack, CompoundTag tags) {
		toolTip.add(this.getText(this.name).withStyle(GREEN));
		toolTip.add(this.getText("mffurnace_top").withStyle(GOLD));
		toolTip.add(this.getText("mffurnace_bot").withStyle(GOLD));
	}
}
