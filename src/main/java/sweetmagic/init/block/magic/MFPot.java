package sweetmagic.init.block.magic;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
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
import sweetmagic.init.TileInit;
import sweetmagic.init.block.base.BaseMFBlock;
import sweetmagic.init.tile.sm.TileMFPot;

public class MFPot extends BaseMFBlock {

	public final int data;
	public final int tier;
	private static final VoxelShape AABB = Block.box(6D, 0D, 6D, 10D, 11D, 10D);

	public MFPot(String name, int data, int tier) {
		super(name);
		this.data = data;
		this.tier = tier;
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext con) {
		return AABB;
	}

	/**
	 * 0 = ドリィズリミオソチスの瓶（雨MF生産）
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
		if (world.isClientSide()) { return false; }

		TileMFPot tile = (TileMFPot) this.getTile(world, pos);

		if (this.data == 3 && player.totalExperience >= 500 && !tile.isMaxMF()) {

			int oldEXP = player.totalExperience;
			player.giveExperiencePoints(-500);
			int newEXP = player.totalExperience;

			int difEXP = oldEXP - newEXP;
			tile.setMF(tile.getMF() + difEXP * 8);
			tile.sendPKT();
			this.playerSound(world, pos, SoundEvents.ITEM_PICKUP, 0.5F, 1F);
		}

		else {
			player.sendSystemMessage(this.getLabel(this.format(tile.getMF()) + "MF", GREEN));
		}
		return true;
	}

	public int getData() {
		return this.data;
	}

	// 最大MFの取得
	public int getMaxMF() {
		return 200000;
	}

	@Override
	public int getTier() {
		return this.tier;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TileMFPot(pos, state);
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return this.createMailBoxTicker(world, type, TileInit.mfpot);
	}
}
