package sweetmagic.init.block.sm;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.block.base.BaseFaceBlock;
import sweetmagic.init.entity.block.ChairEntity;

public class SMChair extends BaseFaceBlock {

	private static final VoxelShape AABB = Block.box(2D, 0D, 2D, 14D, 11D, 14D);
	private static final VoxelShape DINING = Block.box(2D, 0D, 2D, 14D, 10D, 14D);
	protected final int data;

	public SMChair(String name, int data) {
		super(name, setState(Material.WOOD, SoundType.WOOD, 0.5F, 8192F));
		this.registerDefaultState(this.setState());
		this.data = data;
		BlockInfo.create(this, SweetMagicCore.smTab, name);
	}

	public SMChair(String name, int data, boolean flag) {
		super(name, setState(Material.WOOD, SoundType.WOOD, 0.5F, 8192F));
		this.data = data;
	}

	// 右クリックしない
	public boolean canRightClick (Player player, ItemStack stack) {
		return true;
	}

	// ブロックでのアクション
	public void actionBlock(Level world, BlockPos pos, Player player, ItemStack stack) {

		double y = 0.5D;
		switch (this.data) {
		case 1:
			y = 0.4D;
			break;
		case 3:
			y = 0.35D;
			break;
		}

		ChairEntity.create(world, pos, y, player, world.getBlockState(pos).getValue(FACING));
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext col) {
		switch (this.data) {
		case 1: return DINING;
		default: return AABB;
		}
	}

	@Override
	public void addBlockTip (List<Component> toolTip) {
		toolTip.add(this.getText("smchair").withStyle(GOLD));
	}
}
