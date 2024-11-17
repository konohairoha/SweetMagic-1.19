package sweetmagic.init.block.sm;

import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.block.base.BaseModelBlock;

public class BedSideLamp extends BaseModelBlock {

	private static final VoxelShape AABB = Block.box(5D, 0D, 5D, 11D, 16D, 11D);
	private final int data;

	public BedSideLamp (String name, int data) {
		super(name, setState(Material.WOOD, SoundType.METAL, 0.25F, 8192F, data == 0 ? 0 : 15).noCollission());
		this.data = data;
		BlockInfo.create(this, data == 1 ? null : SweetMagicCore.smTab, name);
	}

	@Nullable
	public BlockState getStateForPlacement(BlockPlaceContext con) {
		boolean isShift = con.getPlayer().isShiftKeyDown();
		return isShift ? BlockInit.bedside_lamp_on.defaultBlockState() : super.getStateForPlacement(con);
	}

	// 右クリック出来るか
	public boolean canRightClick (Player player, ItemStack stack) {
		return player != null;
	}

	// ブロックでのアクション
	public void actionBlock (Level world, BlockPos pos, Player player, ItemStack stack) {
		Block block = null;
		switch (this.data) {
		case 0:
			block = BlockInit.bedside_lamp_on;
			break;
		case 1:
			block = BlockInit.bedside_lamp;
			break;
		}

		world.setBlock(pos, block.defaultBlockState(), 3);
		this.playerSound(world, pos, SoundEvents.UI_BUTTON_CLICK, 0.25F, world.random.nextFloat() * 0.1F + 1.2F);
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext col) {
		return AABB;
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder build) {
		return Arrays.<ItemStack> asList(new ItemStack(BlockInit.bedside_lamp));
	}

	@Override
	public void addBlockTip (List<Component> toolTip) {
		toolTip.add(this.getText("right_change").withStyle(GREEN));
	}

	public ItemStack getCloneItemStack(BlockGetter get, BlockPos pos, BlockState state) {
		return new ItemStack(BlockInit.bedside_lamp);
	}
}
