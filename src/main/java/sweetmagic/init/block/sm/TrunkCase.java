package sweetmagic.init.block.sm;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.tile.menu.container.BaseContainer.ContainerTrunkCase;
import sweetmagic.util.FaceAABB;

public class TrunkCase extends WoodChest {

	public final int data;
	public static final BooleanProperty ISUNDER = BooleanProperty.create("isunder");
	private final static VoxelShape[] AABB = FaceAABB.create(1D, 0D, 12D, 15D, 10D, 16D);
	private final static VoxelShape[] SWITCH = FaceAABB.create(0D, 0D, 9.5D, 16D, 8D, 13.75D);
	private final static VoxelShape[] UNDER = FaceAABB.create(1D, 0D, 6D, 15D, 4D, 16D);

	public TrunkCase(String name, int data) {
		super(name, BlockBehaviour.Properties.of(Material.WOOD).sound(data == 0 ? SoundType.WOOD : SoundType.METAL).strength(0.5F, 8192F).noOcclusion(), 6 + data * 2);
		this.registerDefaultState(this.setState().setValue(WATERLOGGED, false).setValue(ISUNDER, false));
		BlockInfo.create(this, SweetMagicCore.smTab, name);
		this.data = data;
	}

	// ブロックでのアクション
	public boolean actionBlock(Level world, BlockPos pos, Player player, ItemStack stack) {

		if (player.isShiftKeyDown()) {

			ItemStack drop = this.getDropStack(this.getTile(world, pos));

			if (stack.isEmpty()) {
				player.setItemInHand(InteractionHand.MAIN_HAND, drop);
			}

			else {
				// えんちちースポーン
				if (world.isClientSide()) {
					this.spawnItem(world, pos, drop);
				}
			}

			world.removeBlockEntity(pos);
			world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
			this.playerSound(world, pos, this.data == 0 ? SoundEvents.WOOD_BREAK : SoundEvents.METAL_BREAK, 1F, 0.85F);
		}

		else {
			super.actionBlock(world, pos, player, stack);
		}
		return true;
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext con) {
		int meta = state.getValue(FACING).get3DDataValue() - 2;
		if(this.data == 1) { return SWITCH[meta]; }
		return state.getValue(ISUNDER) ? UNDER[meta] : AABB[meta];
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> build) {
		build.add(FACING, WATERLOGGED, ISUNDER);
	}

	@Nullable
	public BlockState getStateForPlacement(BlockPlaceContext con) {
		Direction face = con.getClickedFace().getOpposite();
		return super.getStateForPlacement(con).setValue(ISUNDER, face == Direction.DOWN);
	}

	@Override
	public void addBlockTip(List<Component> toolTip) {
		super.addBlockTip(toolTip);
		toolTip.add(this.getText("trunkcase").withStyle(GREEN));
	}

	public void openGui(Level world, Player player, ItemStack stack) {
		if (!world.isClientSide()) {
			NetworkHooks.openScreen((ServerPlayer) player, new ContainerTrunkCase(stack, this.data));
			this.playerSound(world, player.blockPosition(), this.data == 0 ? SoundEvents.WOODEN_TRAPDOOR_OPEN : SoundEvents.LEVER_CLICK, 0.5F, world.getRandom().nextFloat() * 0.1F + 0.9F);
		}
	}
}
