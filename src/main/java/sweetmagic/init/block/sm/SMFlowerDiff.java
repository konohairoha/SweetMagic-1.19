package sweetmagic.init.block.sm;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import sweetmagic.api.util.ISMTip;
import sweetmagic.init.ItemInit;

public class SMFlowerDiff extends SMFlower implements ISMTip {

	public static final IntegerProperty SIZE = IntegerProperty.create("size", 0, 2);

	public SMFlowerDiff(String name) {
		super(name);
		this.registerDefaultState(this.defaultBlockState().setValue(SIZE, 0));
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> build) {
		build.add(SIZE);
	}

	// 右クリック処理
	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
		return this.onUse(world, pos, player, hand);
	}

	// ブロックでのアクション
	public InteractionResult onUse(Level world, BlockPos pos, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (player == null || stack.is(ItemInit.magic_meal)) { return InteractionResult.PASS; }

		this.actionBlock(world, pos, player, stack);
		return InteractionResult.sidedSuccess(world.isClientSide());
	}

	// ブロックでのアクション
	public void actionBlock(Level world, BlockPos pos, Player player, ItemStack stack) {
		if (!stack.isEmpty()) { return; }

		BlockState state = world.getBlockState(pos);
		int data = state.getValue(SIZE);

		switch (data) {
		case 0:
			data++;
			break;
		case 1:
			data++;
			break;
		case 2:
			data = 0;
			break;
		}

		world.setBlock(pos, state.setValue(SIZE, data), 3);
		SoundType sound = this.getSoundType(state, world, pos, player);
		this.playerSound(world, pos, sound.getPlaceSound(), (sound.getVolume() + 1F) / 2F, sound.getPitch() * 0.8F);
	}

	public void playerSound(Level world, BlockPos pos, SoundEvent sound, float vol, float pitch) {
		world.playSound(null, pos, sound, SoundSource.BLOCKS, vol, pitch);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter get, List<Component> toolTip, TooltipFlag flag) {
		toolTip.add(this.getText("chaged_flower").withStyle(GOLD));
	}
}
