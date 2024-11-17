package sweetmagic.init.block.sm;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.block.base.BaseFaceBlock;
import sweetmagic.init.tile.sm.TileBossFigurine;

public class BossFigurine extends BaseFaceBlock implements EntityBlock {

	private final int data;
	private static final VoxelShape AABB = Block.box(3D, 0D, 3D, 13D, 16D, 13D);

	public BossFigurine(String name, int data) {
		super(name, setState(Material.METAL, SoundType.METAL, 2F, 8192F));
		this.data = data;
		BlockInfo.create(this, SweetMagicCore.smMagicTab, name);
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext col) {
		return AABB;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileBossFigurine(pos, state);
	}

	public int getData () {
		return this.data;
	}

	@Override
	public void addBlockTip (List<Component> toolTip) {
		toolTip.add(this.getText("boss_figurine").withStyle(GREEN));
	}
}
