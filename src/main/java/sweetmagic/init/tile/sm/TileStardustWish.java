package sweetmagic.init.tile.sm;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import sweetmagic.init.TileInit;
import sweetmagic.init.tile.menu.ParallelInterfereMenu;

public class TileStardustWish extends TileParallelInterfere {

	public TileStardustWish(BlockPos pos, BlockState state) {
		this(TileInit.stardustWish, pos, state);
	}

	public TileStardustWish(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.resolver = new SingleHandlerProvider(this.inputInv, IN_OUT);
	}

	public final StackHandler inputInv = new StackHandler(this.getInvSize());

	public StackHandler getInputInv () {
		return this.inputInv;
	}

	// NBTの書き込み
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.merge(this.inputInv.serializeNBT());
	}

	// NBTの読み込み
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.inputInv.deserializeNBT(tag);
	}

	// インベントリサイズの取得
	@Override
	public int getInvSize () {
		return 1080;
	}

	// スロットの取得
	public IItemHandler getInput() {
		return this.inputInv;
	}

	@Override
	public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
		return new ParallelInterfereMenu(windowId, inv, this);
	}
}
