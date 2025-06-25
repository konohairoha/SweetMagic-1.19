package sweetmagic.init.block.magic;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
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
import sweetmagic.api.iblock.ISMCraftBlock;
import sweetmagic.api.iitem.IPorch;
import sweetmagic.init.ItemInit;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.TileInit;
import sweetmagic.init.block.base.BaseMFBlock;
import sweetmagic.init.tile.sm.TileAbstractSM;
import sweetmagic.init.tile.sm.TileAltarCreat;
import sweetmagic.init.tile.sm.TileAltarCreatStar;
import sweetmagic.init.tile.sm.TilePedalCreate;
import sweetmagic.recipe.RecipeHelper;
import sweetmagic.recipe.base.AbstractRecipe;
import sweetmagic.recipe.pedal.PedalRecipe;

public class PedalCreate extends BaseMFBlock implements ISMCraftBlock {

	public int data;
	private static final VoxelShape AABB = Block.box(1.2D, 0D, 1.2D, 14.8D, 11.2D, 14.8D);
	private static final VoxelShape ALTAR = Block.box(0D, 0D, 0D, 16D, 14.8D, 16D);
	private static final VoxelShape STAR = Block.box(0D, 0D, 0D, 16D, 20D, 16D);

	public PedalCreate(String name, int data) {
		super(name);
		this.data = data;
	}

	// 当たり判定
	public VoxelShape getShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext col) {
		switch (this.data) {
		case 1:  return ALTAR;
		case 2:  return STAR;
		default: return AABB;
		}
	}

	// ブロックでのアクション
	public boolean actionBlock(Level world, BlockPos pos, Player player, ItemStack stack) {
		if (world.isClientSide()) { return true; }
		this.pedalCraft(world, pos, player, stack, false);
		return true;
	}

	public void pedalCraft(Level world, BlockPos pos, Player player, ItemStack stack, boolean isSneak) {
		TilePedalCreate tile = (TilePedalCreate) this.getTile(world, pos);
		if (tile.isCraft) { return; }

		if (stack.isEmpty()) {
			player.sendSystemMessage(this.getLabel(this.format(tile.getMF()) + "MF", GREEN));
		}

		// クラフト後アイテムが空なら未作成なのでレシピチェック
		else if (tile.resultList.isEmpty()) {

			List<ItemStack> stackList = RecipeHelper.getPlayerInv(player, stack);
			MutableComponent tip = isSneak ? tile.checkCanAllCraft(stackList) : tile.checkCanCraft(stackList);

			// レシピの条件を満たせばクラフトスタート
			if (tip == null) {

				ItemStack leg = player.getItemBySlot(EquipmentSlot.LEGS);

				if (leg.getItem() instanceof IPorch porch) {
					tile.quickCraft = porch.hasAcce(leg, ItemInit.witch_scroll);
				}

				tile.craftStart();
			}

			else {
				player.sendSystemMessage(tip);
			}
		}
	}

	// ブロック設置時
	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {

		TilePedalCreate tile = this.getTile(TilePedalCreate::new, world, pos);
		tile.isHaveBlock = tile.checkBlock();
		tile.sendPKT();

		if (tile.isHaveBlock) {
			this.haveBlock(world, pos);
		}
	}

	public void haveBlock(Level world, BlockPos pos) {
		if (!(world instanceof ServerLevel sever)) { return; }

		float posX = pos.getX() + 0.5F;
		float posY = pos.getY() - 0.5F;
		float posZ = pos.getZ() + 0.5F;
		RandomSource rand = world.getRandom();

		for (int k = 0; k < 12; k++) {
			float f1 = (float) posX - 0.75F + rand.nextFloat() * 1.5F;
			float f2 = (float) posY + 0.5F + rand.nextFloat() * 0.5F;
			float f3 = (float) posZ - 0.75F + rand.nextFloat() * 1.5F;
			sever.sendParticles(ParticleInit.TWILIGHTLIGHT, f1, f2, f3, 2, 0F, 0F, 0F, 0.01F);
		}
	}

	// 最大MFの取得
	public int getMaxMF() {
		switch (this.data) {
		case 1:  return 200000;
		case 2:  return 2000000;
		default: return 20000;
		}
	}

	@Override
	public int getTier() {
		return this.data + 1;
	}

	public void onRemove(Level world, BlockPos pos, BlockState state, TileAbstractSM base) {

		// ドロップアイテムにNBTを登録してアイテムドロップ
		if (base instanceof TilePedalCreate tile && !tile.craftList.isEmpty()) {
			this.spawnItemList(world, pos, tile.getDropList());
			tile.clearInfo();
		}

		super.onRemove(world, pos, state, base);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		switch (this.data) {
		case 1: return new TileAltarCreat(pos, state);
		case 2: return new TileAltarCreatStar(pos, state);
		default: return new TilePedalCreate(pos, state);
		}
	}

	public BlockEntityType<? extends TileAbstractSM> getTileType() {
		switch(this.data) {
		case 1: return TileInit.altarCreat;
		case 2: return TileInit.altarCreatStar;
		default: return TileInit.pedal;
		}
	}

	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return this.createMailBoxTicker(world, type, this.getTileType());
	}

	public void addTip(List<Component> toolTip, ItemStack stack, CompoundTag tags) {
		toolTip.add(this.getTipArray(this.getText("pedestal_creat"), GREEN));
		toolTip.add(this.getTipArray(this.getText("pedestal_creat_allcraft"), GREEN));
	}

	public boolean notNullRecipe(Level world, List<ItemStack> stackList) {
		return !PedalRecipe.getRecipe(world, stackList).isEmpty();
	}

	public AbstractRecipe getRecipe(Level world, List<ItemStack> stackList) {
		return PedalRecipe.getRecipe(world, stackList).get();
	}

	public boolean canShiftCraft() {
		return true;
	}
}
