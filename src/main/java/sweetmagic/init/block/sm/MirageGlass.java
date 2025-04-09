package sweetmagic.init.block.sm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
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
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.TileInit;
import sweetmagic.init.block.base.BaseFaceBlock;
import sweetmagic.init.entity.animal.AbstractSummonMob;
import sweetmagic.init.entity.projectile.AbstractMagicShot;
import sweetmagic.init.tile.sm.TileMirageGlass;

public class MirageGlass extends BaseFaceBlock implements EntityBlock {

	private final int data;
	public static final Property<Integer> LIGHT_LEVEL = IntegerProperty.create("level", 0, 15);
	public static final BooleanProperty ISVIEW = BooleanProperty.create("view");

	public MirageGlass(String name, int data) {
		super(name, setState(Material.GLASS, SoundType.GLASS, 0.5F, 8192F).lightLevel((b) -> b.getValue(LIGHT_LEVEL)).isSuffocating((a, b, c) -> false).isViewBlocking((a, b, c) -> false));
		this.registerDefaultState(this.defaultBlockState().setValue(ISVIEW, false));
		BlockInfo.create(this, SweetMagicCore.smTab, name);
		this.data = data;
	}

	public void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos block2, boolean par1) {
		if (world.isClientSide || this.data != 1 || !world.hasNeighborSignal(pos) || state.getValue(ISVIEW)) { return; }
		this.setValue(world, pos);
	}

	public void setValue(Level world, BlockPos pos) {
		BlockState state1 = world.getBlockState(pos);
		if (state1.getValue(ISVIEW)) { return; }

		if (this.data == 2) {

			TileMirageGlass tile = (TileMirageGlass) this.getTile(world, pos);
			BlockState state = tile.getState();
			SoundType sound = state.getBlock().getSoundType(state, world, pos, null);
			Random rand = new Random();
			this.playerSound(world, pos, sound.getBreakSound(), 2.5F, 0.9F + rand.nextFloat() * 0.2F);

			if (world instanceof ServerLevel sever) {

				int count = 16;
				float rate = 0.15F;
				float x = pos.getX() + 0.5F;
				float y = pos.getY() + 0.5F;
				float z = pos.getZ() + 0.5F;
				ParticleOptions par = new BlockParticleOption(ParticleTypes.BLOCK, state);

				for (int i = 0; i < count; i++) {
					float addX = rand.nextFloat() - rand.nextFloat();
					float addY = rand.nextFloat() - rand.nextFloat();
					float addZ = rand.nextFloat() - rand.nextFloat();
					sever.sendParticles(par, x + addX, y + addY, z + addZ, 4, 0F, 0F, 0F, rate);
				}
			}
		}

		world.setBlock(pos, state1.setValue(ISVIEW, true), 2);

		for (Direction face : Direction.values()) {
			BlockState state = world.getBlockState(pos.relative(face));

			if (state.hasProperty(ISVIEW) && !state.getValue(ISVIEW) && state.getBlock() == this) {
				((MirageGlass) state.getBlock()).setValue(world, pos.relative(face));
			}
		}
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext con) {

		if(world.getBlockEntity(pos) instanceof TileMirageGlass tile && !tile.state.is(this) && !state.getValue(ISVIEW) && !(tile.state.getBlock() instanceof MirageGlass)) {
			BlockState state2 = tile.getState();
			return state2 != null ? state2.getShape(world, pos) : Shapes.block();
		}
		return this.data == 2 && state.getValue(ISVIEW) ? Shapes.empty() : Shapes.block();
	}

	// 右クリック出来るか
	public boolean canRightClick(Player player, ItemStack stack) {
		return player != null;
	}

	// ブロックでのアクション
	public boolean actionBlock(Level world, BlockPos pos, Player player, ItemStack stack) {
		TileMirageGlass tile = (TileMirageGlass) this.getTile(world, pos);
		if (this.data == 1 && !player.isCreative() && !tile.state.is(this)) { return false; }

		if (stack.isEmpty() && !tile.state.is(this)) {
			tile.state = this.defaultBlockState();
			world.setBlockAndUpdate(pos, tile.getState(pos).setValue(LIGHT_LEVEL, tile.state.getLightEmission(world, pos)));
			tile.sendPKT();
			this.playerSound(world, pos, SoundEvents.ITEM_PICKUP, 1F, 1F);
			return true;
		}

		if (!(stack.getItem() instanceof BlockItem item) || item.getBlock() == this) { return false; }

		List<BlockPos> posList = new ArrayList<>();
		posList.add(pos);
		this.rangeSetBlock(world, pos, tile, item.getBlock(), posList);
		this.playerSound(world, pos, SoundEvents.ITEM_PICKUP, 1F, 1F);
		return true;
	}

	public void rangeSetBlock(Level world, BlockPos pos, TileMirageGlass tile, Block block, List<BlockPos> posList) {
		if(world.isClientSide) { return; }

		tile.state = block.defaultBlockState();
		world.setBlockAndUpdate(pos, tile.getState(pos).setValue(LIGHT_LEVEL, tile.state.getLightEmission(world, pos)));
		tile.sendPKT();

		for (Direction face : Direction.values()) {
			BlockPos p = pos.relative(face);
			if (posList.contains(p)) { continue; }

			BlockState state = world.getBlockState(p);
			if (!state.is(this)) { continue; }

			TileMirageGlass tile2 = (TileMirageGlass) this.getTile(world, p);
			if (tile2.state.is(block)) { continue; }

			posList.add(p);
			((MirageGlass) state.getBlock()).rangeSetBlock(world, p, tile2, block, posList);
		}
	}

	@Nonnull
	@Override
	public VoxelShape getCollisionShape(BlockState state, @Nonnull BlockGetter world, @Nonnull BlockPos pos, CollisionContext col) {
		return (this.data == 0 || state.getValue(ISVIEW) ) && col instanceof EntityCollisionContext con && this.checkEntity(con.getEntity()) ? Shapes.empty() : super.getCollisionShape(state, world, pos, col);
	}

	public boolean checkEntity(Entity entity) {
		return entity instanceof Player || entity instanceof AbstractSummonMob || entity instanceof AbstractMagicShot;
	}

	@Override
	public void addBlockTip(List<Component> toolTip) {
		toolTip.add(this.getText("dungen_only").withStyle(GREEN));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileMirageGlass(pos, state);
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		if (this.data != 2) { return null; }
		return this.createMailBoxTicker(level, type, TileInit.mirageGlass);
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
		build.add(FACING, LIGHT_LEVEL, ISVIEW);
	}
}
