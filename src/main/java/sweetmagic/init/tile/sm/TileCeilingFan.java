package sweetmagic.init.tile.sm;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import sweetmagic.init.TileInit;

public class TileCeilingFan extends TileAbstractSM {

	public boolean isAction = true;
	public float rote = 0F;
	private float now_rote = 0F;

	public TileCeilingFan(BlockPos pos, BlockState state) {
		this(TileInit.ceilingFan, pos, state);
	}

	public TileCeilingFan(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	// サーバー側処理
	@Override
	public void serverTick(Level world, BlockPos pos, BlockState state) {
		super.serverTick(world, pos, state);

		if (this.isAction || this.rote > 0F) {
			this.sendInfo();
		}

		if (!this.isAction) {

			if (this.rote > 0F) {
				this.now_rote = Math.max(0F, this.now_rote - 0.075F);
				this.rote += this.now_rote;
			}
		}

		else {

			this.now_rote = Math.min(7.5F, this.now_rote + 0.075F);
			this.rote += this.now_rote;

			if (this.rote >= 360F) {
				this.rote -= 360F;
			}
		}
	}

	// NBTの書き込み
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putFloat("now_rote", this.now_rote);
		tag.putFloat("rote", this.rote);
		tag.putBoolean("isAction", this.isAction);
	}

	// NBTの読み込み
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.now_rote = tag.getFloat("now_rote");
		this.rote = tag.getFloat("rote");
		this.isAction = tag.getBoolean("isAction");
	}
}
