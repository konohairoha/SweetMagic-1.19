package sweetmagic.plugin.jade;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.iblock.ISMCrop;
import sweetmagic.api.util.ISMTip;
import sweetmagic.init.ItemInit;
import sweetmagic.init.block.sm.FruitLeaves;
import sweetmagic.init.tile.sm.TileSMMagic;

public class JadeDataProvider implements IBlockComponentProvider, ISMTip {

	public static final ResourceLocation PROVIDER = SweetMagicCore.getSRC("mf_provider");
	static final JadeDataProvider INSTANCE = new JadeDataProvider();

	@Override
	public void appendTooltip(ITooltip tooltip, BlockAccessor acce, IPluginConfig config) {

		BlockState state = acce.getBlockState();
		Block block = acce.getBlock();
		BlockEntity bEntity = acce.getBlockEntity();

		if ( bEntity != null && bEntity instanceof TileSMMagic tile ) {
		    tooltip.add(tooltip.getElementHelper().item(new ItemStack(ItemInit.aether_crystal), 0.67F));
			tooltip.append(this.getTip("MF: " + String.format("%,d", tile.getMF())).withStyle(GREEN));
		}

		else if (block instanceof ISMCrop crop && !(block instanceof FruitLeaves)) {
			this.addMaturityTooltip(tooltip, state.getValue(crop.getSMMaxAge()) / (float) crop.getMaxBlockState());
		}
	}

	private void addMaturityTooltip(ITooltip tooltip, float growthValue) {

		growthValue *= 100F;
		if (growthValue < 100F) {
			tooltip.add(Component.translatable("tooltip.jade.crop_growth", String.format("%.0f%%", growthValue)));
		}

		else {
			tooltip.add(Component.translatable("tooltip.jade.crop_growth", Component.translatable("tooltip.jade.crop_mature").withStyle(ChatFormatting.GREEN)));
		}
	}

	@Override
	public ResourceLocation getUid() {
		return PROVIDER;
	}
}
