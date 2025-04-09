package sweetmagic.init.tile.sm;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import sweetmagic.init.TileInit;

public class TileMFTankCreative extends TileMFTank {

	public int maxMagiaFlux = Integer.MAX_VALUE;

	public TileMFTankCreative(BlockPos pos, BlockState state) {
		this(TileInit.tankCreative, pos, state);
	}

	public TileMFTankCreative(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		this.resolver = new InOutHandlerProvider(this.inputInv, this.outputInv);
	}

	protected final StackHandler inputInv = new StackHandler(this.getInvSize());
	protected final StackHandler outputInv = new StackHandler(this.getSubInvSize());

	// サーバー側処理
	@Override
	public void serverTick(Level level, BlockPos pos, BlockState state) {
		super.serverTick(level, pos, state);
		if (this.getTickTime() % 10 != 0) { return; }

		if (!this.isMaxMF()) {
			this.setMF(this.getMaxMF());
			this.sendPKT();
		}
	}

	public void smeltAction() {
		if (!this.hasNeedMF()) { return; }

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
	public int getReceiveMF() {
		return 0;
	}

	// インベントリサイズの取得
	@Override
	public int getInvSize() {
		return 9;
	}

	public int getSubInvSize() {
		return 9;
	}

	// 最大MFの取得
	@Override
	public int getMaxMF() {
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
