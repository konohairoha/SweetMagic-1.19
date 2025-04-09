package sweetmagic.init.tile.sm;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import sweetmagic.init.BlockInit;
import sweetmagic.init.TileInit;
import sweetmagic.init.block.sm.MirageGlass;

public class TileMirageGlass extends TileAbstractSM {

	private int recaslTime = 0;
	private static final int MAX_RECAST_TIME = 30;
	public BlockState state = BlockInit.mirage_glass.defaultBlockState();
	public static final DirectionProperty FACING = MirageGlass.FACING;
	public static final BooleanProperty ISVIEW = MirageGlass.ISVIEW;

	public TileMirageGlass(BlockPos pos, BlockState state) {
		super(TileInit.mirageGlass, pos, state);
	}

	public TileMirageGlass(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	// サーバー側処理
	@Override
	public void serverTick(Level world, BlockPos pos, BlockState state) {
		super.serverTick(world, pos, state);
		if (this.tickTime % 20 != 0 || !state.getValue(ISVIEW) || this.recaslTime++ < MAX_RECAST_TIME) { return; }

		world.setBlock(pos, state.setValue(ISVIEW, false), 3);
		this.recaslTime = 0;
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

	public BlockState getState() {
		BlockState state = this.getState(this.getBlockPos());
		if (state.hasProperty(ISVIEW) && state.getValue(ISVIEW)) {
			return state.is(BlockInit.mirage_wall_glass) ? Blocks.AIR.defaultBlockState() : state;
		}

		return this.state.hasProperty(FACING) ? this.state.setValue(FACING, state.getValue(FACING)) : this.state;
	}
}
