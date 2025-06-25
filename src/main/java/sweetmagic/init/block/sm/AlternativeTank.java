package sweetmagic.init.block.sm;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.TileInit;
import sweetmagic.init.block.base.BaseFaceBlock;
import sweetmagic.init.tile.sm.TileAlternativeTank;
import sweetmagic.init.tile.sm.TileCosmosLightTank;

public class AlternativeTank extends BaseFaceBlock implements EntityBlock {

	private final int data;

	public AlternativeTank(String name, int data) {
		super(name, setState(Material.WOOD, SoundType.METAL, 0.5F, 8192F));
		this.data = data;
		BlockInfo.create(this, SweetMagicCore.smTab, name);
	}

	// ドロップするかどうか
	protected boolean isDrop() {
		return false;
	}

	// tileの中身を保持するか
	public boolean isKeepTile() {
		return true;
	}

	public int getMaxFluidValue() {
		switch(this.data) {
		case 1:  return 25600_000;
		default: return 256_000;
		}
	}

	// 右クリック出来るか
	public boolean canRightClick(Player player, ItemStack stack) {
		return true;
	}

	// ブロックでのアクション
	public boolean actionBlock(Level world, BlockPos pos, Player player, ItemStack stack) {
		if (world.isClientSide) { return true; }

		TileAlternativeTank tile = (TileAlternativeTank) this.getTile(world, pos);
		if(FluidUtil.getFluidHandler(player.getItemInHand(InteractionHand.MAIN_HAND)).isPresent()){
			FluidUtil.interactWithFluidHandler(player, player.getUsedItemHand(), world, pos, null);
			tile.sendPKT();
		}

		else {
			this.openGUI(world, pos, player, tile);
		}
		return true;
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return this.createMailBoxTicker(world, type, this.data == 1 ? TileInit.cosmosLightTank : TileInit.alternativeTank);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		switch(this.data) {
		case 1:  return new TileCosmosLightTank(pos, state);
		default: return new TileAlternativeTank(pos, state);
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter get, List<Component> toolTip, TooltipFlag flag) {
		toolTip.add(this.tierTip(this.data + 1));
		toolTip.add(this.getText("keep_tank").withStyle(GREEN));
		toolTip.add(this.getTipArray(this.getText("max_value"), ": ", this.getLabel(String.format("%,.1f", this.getMaxFluidValue() * 0.001F) + "B", GREEN)));
		if (!stack.getOrCreateTag().contains("BlockEntityTag")) { return; }

		CompoundTag tags = stack.getTagElement("BlockEntityTag");
		if (tags == null) { return; }

		FluidStack fluid = FluidStack.loadFluidStackFromNBT(tags.getCompound("fluid"));
		toolTip.add(this.getTipArray(fluid.getDisplayName().getString(), ": ", this.getLabel(String.format("%,.1f", fluid.getAmount() * 0.001F) + "B", GREEN)));
	}
}
