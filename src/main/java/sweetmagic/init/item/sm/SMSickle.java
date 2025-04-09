package sweetmagic.init.item.sm;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.IPlantable;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.iblock.ISMCrop;
import sweetmagic.init.ItemInit;
import sweetmagic.util.WorldHelper;

public class SMSickle extends SMItem {

	private final int data;

	public SMSickle(String name, int data, int value) {
		super(name, SweetMagicCore.smMagicTab);
		this.data = data;
		ItemInit.itemMap.put(this, name);
	}

	public int getRange() {
		switch (this.data) {
		case 2: return 5;
		default: return 2;
		}
	}

	// 右クリック
	@Override
	public InteractionResult useOn(UseOnContext con) {
		Level world = con.getLevel();
		BlockPos pos = con.getClickedPos();
		Block block = world.getBlockState(pos).getBlock();
		if (!(block instanceof IPlantable) || !(world instanceof ServerLevel server)) { return InteractionResult.PASS; }

		if (this.data == 0) { return InteractionResult.SUCCESS; }

		RandomSource rand = world.random;
		Player player = con.getPlayer();
		ItemStack stack = player.getMainHandItem();

		// 範囲とstackListの初期化
		int area = this.getRange();
		Iterable<BlockPos> posList = WorldHelper.getRangePos(pos, area);
		List<ItemStack> stackList = new ArrayList<>();

		// 範囲分回す
		for (BlockPos p : posList) {

			BlockState state = world.getBlockState(p);
			Block b = state.getBlock();

			// まずはスイマジ作物なら右クリック処理を呼び出し
			if (b instanceof ISMCrop smCrop) {

				if (smCrop.isMaxAge(state)) {
					stackList.addAll(smCrop.rightClickStack(world, state, p));
					smCrop.playCropSound(world, rand, pos);
				}
			}

			else if (b instanceof IPlantable && b instanceof BonemealableBlock crop && !crop.isValidBonemealTarget(world, p, state, false)) {

				if (b instanceof StemBlock) { continue; }

				stackList.addAll(Block.getDrops(state, server, p, world.getBlockEntity(p), player, stack));
				BlockState newState = b.defaultBlockState();

				if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
					newState = newState.setValue(BlockStateProperties.HORIZONTAL_FACING, state.getValue(BlockStateProperties.HORIZONTAL_FACING));
				}

				world.setBlock(p, newState, 3);
			}
		}

		if (!stackList.isEmpty()) {

			world.playSound(null, pos, SoundEvents.GRASS_PLACE, SoundSource.BLOCKS, 0.5F, 0.8F + rand.nextFloat() * 0.4F);

			// リスト分スポーン
			for (ItemStack crop : stackList) {
				world.addFreshEntity(new ItemEntity(world, player.xo, player.yo, player.zo, crop));
			}
		}

		return InteractionResult.SUCCESS;
	}

	public void getPickPlant(Level world, Player player, BlockPos pos, ItemStack stack) {
		if (!(world.getBlockState(pos).getBlock() instanceof IPlantable) || !(world instanceof ServerLevel server)) { return; }

		// 範囲とstackListの初期化
		int area = this.getRange();
		RandomSource rand = world.random;
		List<ItemStack> stackList = new ArrayList<>();
		Iterable<BlockPos> posList = WorldHelper.getRangePos(pos, area);

		// 範囲分回す
		for (BlockPos p : posList) {

			BlockState state = world.getBlockState(p);
			Block b = state.getBlock();

			// まずはスイマジ作物なら右クリック処理を呼び出し
			if (b instanceof ISMCrop smCrop) {

				if (smCrop.isMaxAge(state)) {
					stackList.addAll(smCrop.rightClickStack(world, state, p));
					smCrop.playCropSound(world, rand, pos);
				}
			}

			else if (this.data >= 1 && b instanceof IPlantable && b instanceof BonemealableBlock crop && !crop.isValidBonemealTarget(world, p, state, false)) {

				if (b instanceof StemBlock) { continue; }

				stackList.addAll(Block.getDrops(state, server, p, world.getBlockEntity(p), player, stack));
				BlockState newState = b.defaultBlockState();

				if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
					newState = newState.setValue(BlockStateProperties.HORIZONTAL_FACING, state.getValue(BlockStateProperties.HORIZONTAL_FACING));
				}

				world.setBlock(p, newState, 3);
			}
		}

		// Listが空なら終了
		if (stackList.isEmpty()) { return; }

		stackList.forEach(s -> world.addFreshEntity(new ItemEntity(world, player.xo, player.yo, player.zo, s)));
	}

	// ツールチップの表示
	@Override
	public void addTip(ItemStack stack, List<Component> toolTip) {

		if (this.data == 0) {
			toolTip.add(this.getText(this.name).withStyle(GREEN));
		}

		else {
			toolTip.add(this.getText("fluorite_sickle", String.valueOf(this.getRange())).withStyle(GREEN));
		}

	}

	// アイテム修理
	public boolean isValidRepairItem(ItemStack stack, ItemStack ingot) {
		return ingot.is(ItemInit.alt_ingot);
	}
}
