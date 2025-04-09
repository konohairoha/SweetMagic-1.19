package sweetmagic.init.block.sm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.util.ISMTip;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.block.base.BaseSMBlock;

public class WoodBed extends BedBlock implements ISMTip {

	public WoodBed(String name) {
		super(DyeColor.WHITE, BaseSMBlock.setState(Material.WOOD, SoundType.WOOD, 0.5F, 8192F));
		BlockInfo.create(this, SweetMagicCore.smTab, name);
	}

	// 右クリック処理
	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {

		if (state.getValue(PART).equals(BedPart.FOOT)) {
			pos = pos.relative(state.getValue(FACING));
		}

		player.setPos(pos.getX(), pos.getY() + 1, pos.getZ());
		player.startSleepInBed(pos).ifLeft((s) -> {
			if (s.getMessage() != null) {
				player.displayClientMessage(s.getMessage(), true);
			}
		});

		return InteractionResult.sidedSuccess(world.isClientSide);
	}

	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder build) {
		return state.getValue(PART).equals(BedPart.HEAD) ? new ArrayList<>() : Arrays.<ItemStack> asList(new ItemStack(this));
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter get, List<Component> toolTip, TooltipFlag flag) {
		toolTip.add(this.getText("woodbed").withStyle(GOLD));
	}
}
