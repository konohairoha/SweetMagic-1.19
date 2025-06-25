package sweetmagic.init.block.magic;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fluids.FluidUtil;
import sweetmagic.init.TileInit;
import sweetmagic.init.block.base.BaseMFBlock;
import sweetmagic.init.tile.sm.TileAbstractSM;
import sweetmagic.init.tile.sm.TileAquariumPot;

public class AquariumPot extends BaseMFBlock {

	public final int data;
	public final int tier;
	private final Block block;
	private static final VoxelShape AABB = Block.box(2D, 0D, 2D, 14D, 16D, 14D);

	public AquariumPot(String name, Block block, int data, int tier) {
		super(name);
		this.data = data;
		this.tier = tier;
		this.block = block;
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext con) {
		return AABB;
	}

	/**
	 * 0 = ドリィズリミオソチスのアクアリウム（雨MF生産）
	 * 1 = アルストロメリアの瓶（夕方MF生産）
	 * 2 = スノードロップの瓶（雪地MF生産）
	 * 3 = トルコキキョウの瓶（経験値MF生産）
	 * 4 = 群青の花瓶(ゴミ箱)
	 * 5 = ソリッド・スターの花瓶（周囲のMFtierパワー回収）
	 * 6 = ジニアの花瓶（光源でMF変換）
	 * 7 = ハイドランジアの花瓶（敵モブ倒してMF生産）
	 * 8 = カーネーションクレオラの花瓶（MF作物からMF生産）
	 * 9 = クリスマスローズエリックスミシィの花瓶（雪レイヤーをMF変換）
	 * 10 = コスモスの花瓶（マグマMF生産）
	 */

	// ブロックでのアクション
	public boolean actionBlock(Level world, BlockPos pos, Player player, ItemStack stack) {
		if (world.isClientSide()) { return true; }

		TileAbstractSM tile = this.getTile(world, pos);
		if(this.data == 10 && FluidUtil.getFluidHandler(player.getItemInHand(InteractionHand.MAIN_HAND)).isPresent()){
			FluidUtil.interactWithFluidHandler(player, player.getUsedItemHand(), world, pos, null);
			tile.sendPKT();
		}

		else {
			this.openGUI(world, pos, player, tile);
		}

		return true;
	}

	public int getData() {
		return this.data;
	}

	// 最大MFの取得
	public int getMaxMF() {
		return 500000;
	}

	@Override
	public int getTier() {
		return this.tier;
	}

	public ItemStack getStack() {
		return new ItemStack(this.block);
	}

	public void addTip(List<Component> toolTip, ItemStack stack, CompoundTag tags) {

		switch (this.data) {
		case 0:
			toolTip.add(this.getTipArray(this.getText("drizzly_mysotis_pot"), GREEN));
			toolTip.add(this.getTipArray(this.getText(this.name + "_thunder"), GREEN));
			break;
		case 3:
			toolTip.add(this.getTipArray(this.getText(this.name), GREEN));
			toolTip.add(this.getTipArray(this.getText(this.name + "_level"), GREEN));
			break;
		case 4:
			toolTip.add(this.getTipArray(this.getText(this.name), GREEN));
			toolTip.add(this.getTipArray(this.getText(this.name + "_mf"), GREEN));
			break;
		case 5:
			toolTip.add(this.getTipArray(this.getText("solid_star_pot"), GREEN));
			break;
		case 6:
			toolTip.add(this.getTipArray(this.getText("zinnia_pot"), GREEN));
			break;
		case 10:
			toolTip.add(this.getTipArray(this.getText(this.name), GREEN));
			toolTip.add(this.getTipArray(this.getText(this.name + "_lava"), GREEN));
			break;
		default:
			toolTip.add(this.getTipArray(this.getText(this.name), GREEN));
			break;
		}

		toolTip.add(this.getTipArray(this.getText("aquarium_pot", this.block.getName().getString()), GOLD));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileAquariumPot(pos, state);
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return this.createMailBoxTicker(world, type, TileInit.aquariumpot);
	}
}
