package sweetmagic.init.tile.sm;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import sweetmagic.init.BlockInit;
import sweetmagic.init.TileInit;
import sweetmagic.init.block.base.BaseFaceBlock;

public class TileMirageGlass extends TileAbstractSM {

	public BlockState state = BlockInit.mirage_glass.defaultBlockState();
	public static final DirectionProperty FACING = BaseFaceBlock.FACING;

	public TileMirageGlass(BlockPos pos, BlockState state) {
		super(TileInit.mirageGlass, pos, state);
	}

	public TileMirageGlass(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	// NBTの書き込み
	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
        tag.put("block", NbtUtils.writeBlockState(this.state));
	}

	// NBTの読み込み
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.state = tag.contains("block") ? NbtUtils.readBlockState(tag.getCompound("block")) : this.getBlockState();
	}

	public BlockState getState () {
		return this.state.hasProperty(FACING) ? this.state.setValue(FACING, this.getState(this.getBlockPos()).getValue(FACING)) : this.state;
	}
}
