package sweetmagic.init.block.sm;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.block.base.BaseFaceBlock;
import sweetmagic.util.FaceAABB;

public class ChoppingBoard extends BaseFaceBlock {

	private static final VoxelShape[] AABB = FaceAABB.create(1D, 0D, 4D, 12D, 1D, 12D);

	public ChoppingBoard (String name) {
		super(name, setState(Material.WOOD, SoundType.METAL, 0.5F, 8192F));
		BlockInfo.create(this, SweetMagicCore.smTab, name);
	}

	// 右クリック出来るか
	public boolean canRightClick (Player player, ItemStack stack) {
		return true;
	}

	// ブロックでのアクション
	public void actionBlock (Level world, BlockPos pos, Player player, ItemStack stack) {
		if (world.isClientSide) { return; }
		this.openGUI(world, pos, player, this.getMenuProvider(world.getBlockState(pos), world, pos));
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext col) {
		return FaceAABB.getAABB(AABB, state);
	}

	@Override
	public void addBlockTip (List<Component> toolTip) {
		toolTip.add(this.getText("chopping_board").withStyle(GREEN));
	}

	public MenuProvider getMenuProvider(BlockState state, Level world, BlockPos pos) {
		return new SimpleMenuProvider((windowId, pInv, data) -> {
			return new CraftingMenu(windowId, pInv, ContainerLevelAccess.create(world, pos)) {
				public boolean stillValid(Player player) { return true; }
			};
		}, this.getName());
	}
}

