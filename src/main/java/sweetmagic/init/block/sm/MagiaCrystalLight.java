package sweetmagic.init.block.sm;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.TileInit;
import sweetmagic.init.block.base.BaseModelBlock;
import sweetmagic.init.tile.sm.TileMagiaCrystalLight;

public class MagiaCrystalLight extends BaseModelBlock implements EntityBlock {

	private static final VoxelShape AABB = Block.box(4D, 0D, 4D, 12D, 16D, 12D);

	public MagiaCrystalLight(String name) {
		super(name, setState(Material.METAL,SoundType.METAL, 1F, 8192F, 15).noCollission());
		BlockInfo.create(this, SweetMagicCore.smTab, name);
	}

	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext cont) {
		return AABB;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileMagiaCrystalLight(pos, state);
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return this.createMailBoxTicker(world, type, TileInit.magiaCrystalLight);
	}
}
