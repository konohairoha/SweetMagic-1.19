package sweetmagic.init.block.sm;

import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.util.EnumVertical;
import sweetmagic.init.BlockInit;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.block.base.BaseModelBlock;

public class ModenLamp extends BaseModelBlock {

	private final int data;
	private static final VoxelShape AABB = Block.box(5D, 0D, 5D, 11D, 16D, 11D);
	public static final EnumProperty<EnumVertical> VERTICAL = EnumProperty.create("vertical", EnumVertical.class);

	public ModenLamp(String name, int data) {
		super(name, setState(Material.WOOD, SoundType.METAL, 0.5F, 8192F, 15));
		this.registerDefaultState(this.defaultBlockState().setValue(VERTICAL, EnumVertical.NOR));
		this.data = data;
		BlockInfo.create(this, data == 2 ? null : SweetMagicCore.smTab, name);
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext con) {
		return AABB;
	}

	// 右クリック出来るか
	public boolean canRightClick(Player player, ItemStack stack) {
		return true;
	}

	// ブロックでのアクション
	public InteractionResult onUse(Level world, BlockPos pos, Player player, InteractionHand hand) {

		ItemStack stack = player.getItemInHand(hand);

		if (!stack.isEmpty() && stack.getItem() instanceof BlockItem item && item.getBlock() instanceof ModenLamp) {
			this.actionBlock(world, pos, player, stack);
			return InteractionResult.sidedSuccess(world.isClientSide());
		}

		if (player.isShiftKeyDown() && (this.data == 1 || this.data == 2)) {
			Block block = this.data == 1 ? BlockInit.wall_lamp_long : BlockInit.wall_lamp;
			world.setBlock(pos, block.defaultBlockState().setValue(VERTICAL, world.getBlockState(pos).getValue(VERTICAL)), 3);
			this.playerSound(world, pos, SoundEvents.UI_BUTTON_CLICK, 0.25F, world.random.nextFloat() * 0.1F + 1.2F);
			return InteractionResult.sidedSuccess(world.isClientSide());
		}

		return InteractionResult.PASS;
	}

	// ブロックでのアクション
	public boolean actionBlock(Level world, BlockPos pos, Player player, ItemStack stack) {
		if (world.isClientSide()) { return true; }

		Block block = this.getBlock(stack);
		if (block != this) { return false; }

		for (int i = 1; i < 11; i++) {

			BlockPos targetPos = pos.below(i);
			BlockState state = world.getBlockState(targetPos);
			Block targetBlock = state.getBlock();
			if (!state.isAir() && !(targetBlock instanceof ModenLamp)) { return false; }
			if (!state.isAir()) { continue; }

			world.setBlock(targetPos, block.defaultBlockState(), 3);
			this.blockSound(world, block, targetPos, player);
			if (!player.isCreative()) { stack.shrink(1); }
			break;
		}
		return true;
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> build) {
		build.add(VERTICAL);
	}

	@Nullable
	public BlockState getStateForPlacement(BlockPlaceContext con) {
		return this.setVertical(con.getLevel(), con.getClickedPos());
	}

	public BlockState setVertical(LevelAccessor world, BlockPos pos) {
		boolean bot = this.getBlock(world, pos.below()) instanceof ModenLamp;
		boolean top = this.getBlock(world, pos.above()) instanceof ModenLamp;
		return this.defaultBlockState().setValue(VERTICAL, EnumVertical.getVertical(bot, top));
	}

	public BlockState updateShape(BlockState state, Direction face, BlockState state2, LevelAccessor world, BlockPos pos1, BlockPos pos2) {
		return this.setVertical(world, pos1);
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder build) {
		if (this.data == 0) { return super.getDrops(state, build); }
		return Arrays.<ItemStack> asList(new ItemStack(BlockInit.wall_lamp));
	}

	public ItemStack getCloneItemStack(BlockGetter get, BlockPos pos, BlockState state) {
		return this.data == 0 ? new ItemStack(this) : new ItemStack(BlockInit.wall_lamp);
	}

	@Override
	public void addBlockTip(List<Component> toolTip) {
		toolTip.add(this.getText("is_vertical").withStyle(GOLD));

		if (this.data == 2 || this.data == 3) {
			toolTip.add(this.getText("shift_change").withStyle(GREEN));
		}
	}
}
