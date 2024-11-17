package sweetmagic.init.block.magic;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.iblock.ISMNeedItem;
import sweetmagic.api.iitem.IWand;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.ItemInit;
import sweetmagic.init.TileInit;
import sweetmagic.init.block.base.BaseFaceBlock;
import sweetmagic.init.tile.sm.TileAbstractMagicianLectern;
import sweetmagic.init.tile.sm.TileAbstractMagicianLectern.SummonType;
import sweetmagic.init.tile.sm.TileAbstractSM;
import sweetmagic.init.tile.sm.TileMagicianLecternFire;
import sweetmagic.init.tile.sm.TileMagicianLecternFrost;
import sweetmagic.init.tile.sm.TileMagicianLecternLight;
import sweetmagic.init.tile.sm.TileMagicianLecternWind;
import sweetmagic.recipe.RecipeHelper;

public class MagicianLectern extends BaseFaceBlock implements EntityBlock, ISMNeedItem {

	private final int data;
	private static final VoxelShape AABB = Block.box(3D, 0D, 3D, 13D, 12.5D, 13D);

	public MagicianLectern(String name, int data) {
		super(name, setState(Material.WOOD, SoundType.WOOD, 2F, 8192.0F));
		this.registerDefaultState(this.setState());
		BlockInfo.create(this, SweetMagicCore.smMagicTab, name);
		this.data = data;
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext col) {
		return AABB;
	}

	// 右クリック処理
	@Override
	public boolean canRightClick (Player player, ItemStack stack) {
		return true;
	}

	// ブロックでのアクション
	public void actionBlock (Level world, BlockPos pos, Player player, ItemStack stack) {
		if (world.isClientSide || !this.hasNeedItem(player)) { return; }

		TileAbstractMagicianLectern tile = (TileAbstractMagicianLectern) this.getTile(world, pos);
		if (tile.summonType.is(SummonType.CHARGE) || tile.summonType.is(SummonType.SUMMON)) { return; }

		if (world.getDifficulty() == Difficulty.PEACEFUL) {
			if (!world.isClientSide) {
				player.sendSystemMessage(this.getText("magician_lectern_peaceful").withStyle(RED));
			}

			return;
		}

		// プレイヤーのインベントリを取得
		List<ItemStack> stackList = RecipeHelper.getPlayerInv(player, ItemStack.EMPTY);
		boolean hasHardItem = this.isHard(player);

		List<ItemStack> wandList = stackList.stream().filter(s -> s.getItem() instanceof IWand wand && this.checkWand(hasHardItem, s, wand)).toList();
		if (wandList.isEmpty()) {

			if (!world.isClientSide) {
				player.sendSystemMessage(this.getText(hasHardItem ? "magician_lectern_wand_hard" : "magician_lectern_wand").withStyle(RED));
			}

			return;
		}

		tile.summonType = SummonType.CHARGE;
		tile.stack = stack.copy();
		tile.wave = 1;
		tile.tileTime = 0;
		tile.chargeSummonSize = 0F;
		tile.dethMobCount = 0;
		tile.summonMaxCount = 24;
		tile.summonCount = 0;
		tile.waitTime = 0;
		tile.maxWaitTime = 0;
		tile.chageTime = 0;
		tile.isHard = hasHardItem;

		if (this.data >= 2) {
			tile.wave = 4;
		}

		tile.setMobSize(world, pos);
		tile.sendPKT();

		List<ItemStack> needList = new ArrayList<ItemStack>(this.getNeedItemList());

		for (ItemStack needStack : needList) {

			for (ItemStack pStack : stackList) {

				if (needStack.is(pStack.getItem()) && pStack.getCount() >= needStack.getCount()) {
					pStack.shrink(needStack.getCount());
					break;
				}
			}
		}
	}

	public boolean checkWand (boolean hasHardItem, ItemStack stack, IWand wand) {

		if (hasHardItem) {
			return wand.getWandTier() >= 3 && (wand.getLevel(stack) >= 12 || wand.isCreativeWand());
		}

		return wand.getWandTier() >= 2 && (wand.getLevel(stack) >= 8 || wand.isCreativeWand());
	}

	@Override
	@Deprecated
	public void onRemove(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
		TileAbstractMagicianLectern tile = (TileAbstractMagicianLectern) this.getTile(world, pos);
		tile.removeAllPlayerBossBar();
		super.onRemove(state, world, pos, newState, isMoving);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		switch(this.data) {
		case 1: return new TileMagicianLecternLight(pos, state);
		case 2: return new TileMagicianLecternFire(pos, state);
		case 3: return new TileMagicianLecternWind(pos, state);
		default: return new TileMagicianLecternFrost(pos, state);
		}
	}

	public BlockEntityType<? extends TileAbstractSM> getTileType () {
		switch (this.data) {
		case 1: return TileInit.magicianLecternLight;
		case 2: return TileInit.magicianLecternFire;
		case 3: return TileInit.magicianLecternWind;
		default: return TileInit.magicianLecternFrost;
		}
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		BlockEntityType<? extends TileAbstractSM> tileType = this.getTileType();
		return tileType != null ? this.createMailBoxTicker(world, type, tileType) : null;
	}

	@Override
	public void addBlockTip (List<Component> toolTip) {
		ItemStack magic = new ItemStack(this.getNeedHardItem());
		toolTip.add(this.getText("magician_lectern").withStyle(GREEN));
		toolTip.add(this.getText("magician_lectern_hard").withStyle(GREEN));
		toolTip.add(this.getTipArray(this.getText("magician_lectern_item"), magic.getHoverName()).withStyle(GREEN));
	}

	public List<ItemStack> getNeedItemList () {
		List<ItemStack> stackList = new ArrayList<>();
		stackList.add(new ItemStack(ItemInit.mf_bottle, 3));
		stackList.add(new ItemStack(ItemInit.divine_crystal, 1));
		stackList.add(new ItemStack(ItemInit.acce_bag));
		return stackList;
	}

	public List<ItemStack> getNeedHardItemList () {
		List<ItemStack> stackList = new ArrayList<>();
		stackList.add(new ItemStack(this.getNeedHardItem()));
		stackList.add(new ItemStack(ItemInit.mf_bottle, 5));
		stackList.add(new ItemStack(ItemInit.divine_crystal, 2));
		stackList.add(new ItemStack(ItemInit.acce_bag, 2));
		return stackList;
	}

	public Item getNeedHardItem () {
		switch (this.data) {
		case 1: return ItemInit.magic_holybuster;
		case 2: return ItemInit.magic_ignisblast;
		case 3: return ItemInit.magic_windstorm;
		default: return ItemInit.magic_frostlaser;
		}
	}
}
