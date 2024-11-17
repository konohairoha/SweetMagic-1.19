package sweetmagic.init.block.sm;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.block.base.BaseFaceBlock;
import sweetmagic.init.tile.sm.TileMirageGlass;

public class MirageGlass extends BaseFaceBlock implements EntityBlock {

    public static final Property<Integer> LIGHT_LEVEL = IntegerProperty.create("level", 0, 15);

	public MirageGlass(String name) {
		super(name, setState(Material.GLASS, SoundType.GLASS, 0.5F, 8192F).lightLevel((b) -> b.getValue(LIGHT_LEVEL)).isSuffocating((a, b, c) -> false).isViewBlocking((a, b, c) -> false));
		BlockInfo.create(this, SweetMagicCore.smTab, name);
	}

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext con) {
        if(world.getBlockEntity(pos) instanceof TileMirageGlass tile && !tile.state.is(this)) {
            return tile.getState().getShape(world, pos);
        }
        return super.getShape(state, world, pos, con);
    }

	// 右クリック出来るか
	public boolean canRightClick (Player player, ItemStack stack) {
		return player != null;
	}

	// ブロックでのアクション
	public void actionBlock (Level world, BlockPos pos, Player player, ItemStack stack) {

		TileMirageGlass tile = (TileMirageGlass) this.getTile(world, pos);

		if (stack.isEmpty() && !tile.state.is(this)) {
			tile.state = this.defaultBlockState();
			world.setBlockAndUpdate(pos, tile.getState(pos).setValue(LIGHT_LEVEL, tile.state.getLightEmission(world, pos)));
			tile.sendPKT();
			this.playerSound(world, pos, SoundEvents.ITEM_PICKUP, 1F, 1F);
			return;
		}

		if (!(stack.getItem() instanceof BlockItem item) || item.getBlock() == BlockInit.mirage_glass) { return; }

		Block block = item.getBlock();
		tile.state = block.defaultBlockState();
		world.setBlockAndUpdate(pos, tile.getState(pos).setValue(LIGHT_LEVEL, tile.state.getLightEmission(world, pos)));
		tile.sendPKT();
		this.playerSound(world, pos, SoundEvents.ITEM_PICKUP, 1F, 1F);
	}

	@Nonnull
	@Override
	public VoxelShape getCollisionShape(BlockState state, @Nonnull BlockGetter world, @Nonnull BlockPos pos, CollisionContext col) {

		if (col instanceof EntityCollisionContext con && con.getEntity() instanceof Player) {
			return Shapes.empty();
		}

		return super.getCollisionShape(state, world, pos, col);
	}

	@Override
	public void addBlockTip (List<Component> toolTip) {
		toolTip.add(this.getText("dungen_only").withStyle(GREEN));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileMirageGlass(pos, state);
	}

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

	public ItemStack getCloneItemStack(BlockGetter get, BlockPos pos, BlockState state) {
		return new ItemStack(((TileMirageGlass) get.getBlockEntity(pos)).state.getBlock());
	}

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> build) {
        build.add(FACING, LIGHT_LEVEL);
    }
}
