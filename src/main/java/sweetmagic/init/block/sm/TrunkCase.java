package sweetmagic.init.block.sm;

import java.util.List;

import org.antlr.v4.runtime.misc.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
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
import sweetmagic.init.tile.menu.TrunkCaseMenu;
import sweetmagic.init.tile.sm.TileWoodChest;
import sweetmagic.util.FaceAABB;

public class TrunkCase extends WoodChest {

	public static final BooleanProperty ISUNDER = BooleanProperty.create("isunder");
	private final static VoxelShape[] AABB = FaceAABB.create(1D, 0D, 12D, 15D, 10D, 16D);
	private final static VoxelShape[] UNDER = FaceAABB.create(1D, 0D, 6D, 15D, 4D, 16D);

	public TrunkCase(String name) {
		super(name, BlockBehaviour.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(0.5F, 8192F).noOcclusion(), 6);
		this.registerDefaultState(this.setState().setValue(WATERLOGGED, false).setValue(ISUNDER, false));
		BlockInfo.create(this, SweetMagicCore.smTab, name);
	}

	// ブロックでのアクション
	public boolean actionBlock(Level world, BlockPos pos, Player player, ItemStack stack) {

		if (player.isShiftKeyDown()) {

			TileWoodChest tile = (TileWoodChest) this.getTile(world, pos);
			ItemStack drop = this.getDropStack(tile);

			if (stack.isEmpty()) {
				player.setItemInHand(InteractionHand.MAIN_HAND, drop);
			}

			else {
				// えんちちースポーン
				if (world.isClientSide) {
					this.spawnItem(world, pos, drop);
				}
			}

			world.removeBlockEntity(pos);
			world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
			this.playerSound(world, pos, SoundEvents.WOOD_BREAK, 1F, 0.85F);
		}

		else {
			super.actionBlock(world, pos, player, stack);
		}
		return true;
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext con) {
		int meta = state.getValue(FACING).get3DDataValue() - 2;
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
		if (!world.isClientSide) {
			NetworkHooks.openScreen((ServerPlayer) player, new ContainerTrunkCase(stack));
			this.playerSound(world, player.blockPosition(), SoundEvents.WOODEN_TRAPDOOR_OPEN, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
		}
	}

	public record ContainerTrunkCase(ItemStack stack) implements MenuProvider {

		public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory pInv, @NotNull Player player) {
			return new TrunkCaseMenu(windowId, pInv, pInv.player.getMainHandItem());
		}

		public Component getDisplayName() {
			return this.stack.getHoverName();
		}
	}
}
