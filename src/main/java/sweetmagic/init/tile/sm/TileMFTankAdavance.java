package sweetmagic.init.tile.sm;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import sweetmagic.init.TileInit;

public class TileMFTankAdavance extends TileMFTank {

	public int maxMagiaFlux = 1000000;				// 最大MF量を設定

	public TileMFTankAdavance(BlockPos pos, BlockState state) {
		this(TileInit.tankAdavance, pos, state);
	}

	public TileMFTankAdavance(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.resolver = new InOutHandlerProvider(this.inputInv, this.outputInv);
	}

	protected final StackHandler inputInv = new StackHandler(this.getInvSize());
	protected final StackHandler outputInv = new StackHandler(this.getSubInvSize());

	public void smeltAction () {

		for (int i = 0; i < 3; i++) {
			if (this.canSmelt(i)) {
				this.smeltItem(i);
			}
		}
	}

	// NBTの読み込み
	@Override
	public void loadNBT(CompoundTag tags) {
		super.loadNBT(tags);
		this.inputInv.deserializeNBT(tags.getCompound("inputInv"));
		this.outputInv.deserializeNBT(tags.getCompound("outputInv"));
	}

	// NBTの書き込み
	@Override
	public void saveNBT(CompoundTag tags) {
		super.saveNBT(tags);
		tags.put("inputInv", this.inputInv.serializeNBT());
		tags.put("outputInv", this.outputInv.serializeNBT());
	}

	// 受信するMF量の取得
	@Override
	public int getReceiveMF () {
		return 50000;
	}

	// インベントリサイズの取得
	@Override
	public int getInvSize () {
		return 3;
	}

	public int getSubInvSize () {
		return 5;
	}

	// 最大MFの取得
	@Override
	public int getMaxMF () {
		return this.maxMagiaFlux;
	}

	// 入力スロットの取得
	public IItemHandler getInput() {
		return this.inputInv;
	}

	// 出力スロットの取得
	public IItemHandler getOut() {
		return this.outputInv;
	}
}
