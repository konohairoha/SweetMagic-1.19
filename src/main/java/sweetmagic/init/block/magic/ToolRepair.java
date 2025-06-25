package sweetmagic.init.block.magic;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MushroomBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;
import sweetmagic.init.TileInit;
import sweetmagic.init.block.base.BaseMFBlock;
import sweetmagic.init.tile.sm.TileAbstractSM;
import sweetmagic.init.tile.sm.TileAccessoryTable;
import sweetmagic.init.tile.sm.TileAetherPlanter;
import sweetmagic.init.tile.sm.TileAetherRepair;
import sweetmagic.init.tile.sm.TileAetherReverse;
import sweetmagic.init.tile.sm.TileAlstroemeriaAquarium;
import sweetmagic.init.tile.sm.TileEnchantEduce;
import sweetmagic.init.tile.sm.TileMFBottler;
import sweetmagic.init.tile.sm.TileMFBottlerAdcanced;
import sweetmagic.init.tile.sm.TileMFGenerater;
import sweetmagic.init.tile.sm.TileMFMinerAdvanced;
import sweetmagic.init.tile.sm.TileMFWoodCutter;
import sweetmagic.init.tile.sm.TileMagiaAccelerator;
import sweetmagic.init.tile.sm.TileMagiaDrawer;
import sweetmagic.init.tile.sm.TileMagiaRewrite;
import sweetmagic.init.tile.sm.TileMagiaTable;

public class ToolRepair extends BaseMFBlock {

	private final int data;
	private static final VoxelShape TOOL = Block.box(3.2D, 0D, 3.2D, 12.8D, 10.4D, 12.8D);
	private static final VoxelShape AABB = Block.box(1D, 0D, 1D, 15D, 8D, 15D);
	private static final VoxelShape ACCEL = Block.box(0D, 0D, 0D, 16D, 8D, 16D);
	private static final VoxelShape ACCESSORY = Block.box(3D, 0D, 3D, 13D, 8.7D, 13D);
	private static final VoxelShape AQUARIUM = Block.box(2D, 0D, 2D, 14D, 16D, 14D);

	public ToolRepair(String name, int data) {
		super(name);
		this.data = data;
	}

	/**
	 * 0 = エーテルリペアラー
	 * 1 = エンチャント・エデュース
	 * 2 = マギア・リライト
	 * 3 = エーテル・リバース
	 * 4 = マギアドロアー
	 * 5 = マギアアクセラレータ
	 * 6 = アクセサリー加工台
	 * 7 = アルストロメリア・アクアリウム
	 * 8 = エーテルプランナー
	 * 9 = 改良型マイナー
	 * 10 = ボトラー
	 * 11 = 改良型ボトラー
	 * 12 = ジェネレーター
	 */

	// 最大MFの取得
	public int getMaxMF() {
		switch(this.data) {
		case 2: return 1000000;
		case 5: return 200000;
		case 7: return 50000;
		case 8: return 20000;
		case 10: return 1000000;
		case 11: return 7000000;
		case 14: return 10000;
		default: return 100000;
		}
	}

	@Override
	public int getTier() {
		switch(this.data) {
		case 0: return 2;
		case 2: return 2;
		case 5: return 2;
		case 6: return 2;
		case 7: return 2;
		case 8: return 2;
		case 9: return 2;
		case 10: return 2;
		case 11: return 3;
		case 12: return 2;
		case 13: return 2;
		default: return 1;
		}
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return this.data == 2 ? RenderShape.ENTITYBLOCK_ANIMATED : super.getRenderShape(state);
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext col) {
		switch (this.data) {
		case 0: return TOOL;
		case 2: return AABB;
		case 5: return ACCEL;
		case 6: return ACCESSORY;
		case 7: return AQUARIUM;
		default: return Shapes.block();
		}
	}

	// 右クリックしない
	public boolean canRightClick(Level world, BlockPos pos, Player player, ItemStack stack) {
		return !(this.data == 8 && (stack.getItem() instanceof ItemNameBlockItem || stack.is(Items.BAMBOO) ||
				(stack.getItem() instanceof BlockItem bItem && bItem.getBlock() instanceof MushroomBlock) && world.isEmptyBlock(pos.above())));
	}

	// ブロックでのアクション
	public boolean actionBlock(Level world, BlockPos pos, Player player, ItemStack stack) {
		if (world.isClientSide()) { return true; }
		this.openGUI(world, pos, player, this.getTile(world, pos));
		return true;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		switch (this.data) {
		case 1: return new TileEnchantEduce(pos, state);
		case 2: return new TileMagiaRewrite(pos, state);
		case 3: return new TileAetherReverse(pos, state);
		case 4: return new TileMagiaDrawer(pos, state);
		case 5: return new TileMagiaAccelerator(pos, state);
		case 6: return new TileAccessoryTable(pos, state);
		case 7: return new TileAlstroemeriaAquarium(pos, state);
		case 8: return new TileAetherPlanter(pos, state);
		case 9: return new TileMFMinerAdvanced(pos, state);
		case 10: return new TileMFBottler(pos, state);
		case 11: return new TileMFBottlerAdcanced(pos, state);
		case 12: return new TileMFGenerater(pos, state);
		case 13: return new TileMagiaTable(pos, state);
		case 14: return new TileMFWoodCutter(pos, state);
		default: return new TileAetherRepair(pos, state);
		}
	}

	@Override
	public BlockEntityType<? extends TileAbstractSM> getTileType() {
		switch(this.data) {
		case 1: return TileInit.enchantEduce;
		case 2: return TileInit.magiaWrite;
		case 3: return TileInit.aetherReverse;
		case 4: return TileInit.magiarDrawer;
		case 5: return TileInit.magiaAccelerator;
		case 6: return TileInit.accessoryProcessing;
		case 7: return TileInit.alstroemeriaAquarium;
		case 8: return TileInit.aetherPlanter;
		case 9: return TileInit.mfMinerAdvanced;
		case 10: return TileInit.mfBottler;
		case 11: return TileInit.mfBottlerAdvance;
		case 12: return TileInit.mfGenerater;
		case 13: return TileInit.magiaTable;
		case 14: return TileInit.woodCutter;
		default: return TileInit.aetherRepair;
		}
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return this.createMailBoxTicker(world, type, this.getTileType());
	}

	@Override
	public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction face, IPlantable plant) {

		if (this.data != 8) {
			return super.canSustainPlant(state, world, pos, face, plant);
		}

		PlantType plantType = plant.getPlantType(world, pos.relative(face));
		return plantType != PlantType.NETHER && plantType != PlantType.WATER;
	}

	// RS信号で停止するかどうか
	public boolean isRSStop() {
		return this.data == 4 || this.data == 7;
	}

	public void addTip(List<Component> toolTip, ItemStack stack, CompoundTag tags) {
		super.addTip(toolTip, stack, tags);

		if (this.data == 7) {
			toolTip.add(this.getText("hopper_send").withStyle(GOLD));
		}

		else if (this.data == 12) {
			toolTip.add(this.getText(this.name + "_tank").withStyle(GOLD));
			toolTip.add(this.getText(this.name + "_magma", Blocks.MAGMA_BLOCK.getName().getString()).withStyle(GOLD));
		}
	}
}
