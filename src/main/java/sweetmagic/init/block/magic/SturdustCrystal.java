package sweetmagic.init.block.magic;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
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
import net.minecraft.world.level.material.Material;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.TileInit;
import sweetmagic.init.block.base.BaseFaceBlock;
import sweetmagic.init.block.sm.MagiaPortal;
import sweetmagic.init.tile.sm.TileStove;
import sweetmagic.init.tile.sm.TileSturdustCrystal;

public class SturdustCrystal extends BaseFaceBlock implements EntityBlock {

	public static final BooleanProperty ISTOP = BooleanProperty.create("is_top");

	public SturdustCrystal(String name) {
		super(name, setState(Material.GLASS, SoundType.GLASS, 0.5F, 8192F).noCollission());
		this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH).setValue(ISTOP, false));
		BlockInfo.create(this, SweetMagicCore.smMagicTab, name);
	}

	@Override
	public RenderShape getRenderShape(BlockState pState) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	// 右クリック出来るか
	public boolean canRightClick(Player player, ItemStack stack) {
		return true;
	}

	// ブロックでのアクション
	public boolean actionBlock(Level world, BlockPos pos, Player player, ItemStack stack) {
		if (world.isClientSide()) { return true; }
		if (pos.getY() <= world.getMinBuildHeight() + 1) { return false; }

		if (world.getBlockState(pos).getValue(ISTOP)) {
			pos = pos.below();
		}

		TileSturdustCrystal tile = (TileSturdustCrystal) this.getTile(world, pos);

		if (!tile.isRender) {
			tile.isRender = true;
			tile.sendPKT();
			player.sendSystemMessage(this.getText("place_gate").withStyle(GREEN));
			player.sendSystemMessage(this.getText("break_gate").withStyle(RED));
			return false;
		}

		Direction face = world.getBlockState(pos).getValue(FACING);
		boolean isZ = face == Direction.NORTH || face == Direction.SOUTH;

		//リストの作成（めっちゃ大事）
		List<ItemStack> dropList = new ArrayList<>();

		for (int addX = -3; addX < 3; addX++) {
			for (int addY = -1; addY < 5; addY++) {
				for (int addZ = -2; addZ < 3; addZ++) {
					BlockPos targetPos = isZ ? pos.offset(addX, addY, addZ) : pos.offset(addZ, addY, addX);
					BlockState state = world.getBlockState(targetPos);
					Block block = state.getBlock();
					if (block == BlockInit.sturdust_crystal) { continue; }

					if (block.defaultDestroyTime() >= 0 && world instanceof ServerLevel server) {
						dropList.addAll(Block.getDrops(state, server, targetPos, world.getBlockEntity(targetPos), player, new ItemStack(Items.DIAMOND_PICKAXE)));
					}

					this.breakBlock(world, targetPos);
				}
			}
		}

		if (!dropList.isEmpty()) {
			this.spawnItemList(world, player.blockPosition(), dropList);
		}

		Map<BlockPos, BlockState> posMap = isZ ? SturdustCrystal.getZPosMap(pos) : SturdustCrystal.getXPosMap(pos);
		posMap.forEach((p, s) -> world.setBlock(p, s, 3));
		return true;
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
		return world.isEmptyBlock(pos.above());
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		world.setBlock(pos.above(), this.defaultBlockState().setValue(ISTOP, true), 3);
	}

	@Override
	@Deprecated
	public void onRemove(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {

		boolean isTop = state.getValue(ISTOP);

		// ドロップするブロックが破壊されたらアイテムドロップ
		if (!isTop && newState.isAir()) {
			this.spawnItem(world, pos, new ItemStack(this));
		}

		// ブロックの状態が変わった場合
		if (state.getBlock() != newState.getBlock() && newState.getBlock() != BlockInit.magia_portal && !world.isClientSide) {
			BlockPos targetPos = isTop ? pos.below() : pos.above();
			this.breakBlock(world, targetPos);
		}

		super.onRemove(state, world, pos, newState, isMoving);
	}

	// ドロップするかどうか
	protected boolean isDrop() {
		return false;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return !state.getValue(ISTOP) ? new TileSturdustCrystal(pos, state) : new TileStove(pos, state);
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return !state.getValue(ISTOP) ? this.createMailBoxTicker(world, type, TileInit.sturdust_crystal) : null;
	}

	public ItemStack getCloneItemStack(BlockGetter get, BlockPos pos, BlockState state) {
		return new ItemStack(BlockInit.sturdust_crystal);
	}

	public float getEnchantPower() {
		return 2.5F;
	}

	@Override
	public void addBlockTip(List<Component> toolTip) {
		toolTip.add(this.getText(this.name).withStyle(GREEN));
		toolTip.add(this.getText(this.name + "_boss").withStyle(GOLD));
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> build) {
		build.add(FACING, ISTOP);
	}

	public static Map<BlockPos, BlockState> getXPosMap(BlockPos pos) {
		Map<BlockPos, BlockState> map = new LinkedHashMap<>();

		pos = pos.below();
		BlockState portal = BlockInit.magia_portal.defaultBlockState().setValue(MagiaPortal.AXIS, Direction.Axis.X);
		BlockState flame = BlockInit.design_brick_w.defaultBlockState();
		BlockState flame_b = BlockInit.whiteline_brick_b.defaultBlockState();
		BlockState flame_l = BlockInit.antique_brick_0l.defaultBlockState();
		BlockState light = BlockInit.glow_light.defaultBlockState();
		BlockState railings = BlockInit.iron_railings_b.defaultBlockState();

		for (int addX = -2; addX < 3; addX++) {
			map.put(pos.offset(addX, 1, -3), railings);
			map.put(pos.offset(addX, 1, 3), railings);
		}

		map.put(pos.offset(-2, 1, -2), railings);
		map.put(pos.offset(2, 1, -2), railings);
		map.put(pos.offset(-2, 1, 2), railings);
		map.put(pos.offset(2, 1, 2), railings);

		for (int addY = 1; addY < 7; addY++)
			for (int addZ = -3; addZ < 4; addZ++)
				map.put(pos.offset(0, addY, addZ), flame_l);

		for (int addY = 0; addY < 6; addY++)
			for (int addZ = -2; addZ < 3; addZ++)
				map.put(pos.offset(0, addY, addZ), flame);

		for (int addX = -2; addX < 3; addX++)
			for (int addZ = -3; addZ < 4; addZ++)
				map.put(pos.offset(addX, 0, addZ), flame_b);

		for (int addX = -1; addX < 2; addX++)
			for (int addZ = -2; addZ < 3; addZ++)
				map.put(pos.offset(addX, 0, addZ), flame);

		for (int addY = 1; addY < 5; addY++)
			for (int addZ = -1; addZ < 2; addZ++)
				map.put(pos.offset(0, addY, addZ), portal);

		map.put(pos.offset(-2, 0, -3), light);
		map.put(pos.offset(2, 0, -3), light);
		map.put(pos.offset(-2, 0, 3), light);
		map.put(pos.offset(2, 0, 3), light);
		return map;
	}

	public static Map<BlockPos, BlockState> getZPosMap(BlockPos pos) {
		Map<BlockPos, BlockState> map = new LinkedHashMap<>();

		pos = pos.below();
		BlockState portal = BlockInit.magia_portal.defaultBlockState().setValue(MagiaPortal.AXIS, Direction.Axis.Z);
		BlockState flame = BlockInit.design_brick_w.defaultBlockState();
		BlockState flame_b = BlockInit.whiteline_brick_b.defaultBlockState();
		BlockState flame_l = BlockInit.antique_brick_0l.defaultBlockState();
		BlockState light = BlockInit.glow_light.defaultBlockState();
		BlockState railings = BlockInit.iron_railings_b.defaultBlockState();

		for (int addX = -2; addX < 3; addX++) {
			map.put(pos.offset(-3, 1, addX), railings);
			map.put(pos.offset(3, 1, addX), railings);
		}

		map.put(pos.offset(-2, 1, -2), railings);
		map.put(pos.offset(-2, 1, 2), railings);
		map.put(pos.offset(2, 1, -2), railings);
		map.put(pos.offset(2, 1, 2), railings);

		for (int addY = 1; addY < 7; addY++)
			for (int addZ = -3; addZ < 4; addZ++)
				map.put(pos.offset(addZ, addY, 0), flame_l);

		for (int addY = 0; addY < 6; addY++)
			for (int addZ = -2; addZ < 3; addZ++)
				map.put(pos.offset(addZ, addY, 0), flame);

		for (int addX = -2; addX < 3; addX++)
			for (int addZ = -3; addZ < 4; addZ++)
				map.put(pos.offset(addZ, 0, addX), flame_b);

		for (int addX = -1; addX < 2; addX++)
			for (int addZ = -2; addZ < 3; addZ++)
				map.put(pos.offset(addZ, 0, addX), flame);

		for (int addY = 1; addY < 5; addY++)
			for (int addZ = -1; addZ < 2; addZ++)
				map.put(pos.offset(addZ, addY, 0), portal);

		map.put(pos.offset(-3, 0, -2), light);
		map.put(pos.offset(-3, 0, 2), light);
		map.put(pos.offset(3, 0, -2), light);
		map.put(pos.offset(3, 0, 2), light);
		return map;
	}
}
