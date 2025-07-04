package sweetmagic.init.block.sm;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.util.ISMTip;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.block.base.BaseSMBlock;

public class SMTrapDoor extends TrapDoorBlock implements ISMTip {

	private final Block block;

	public SMTrapDoor(String name, int data) {
		super(BaseSMBlock.setState(data == 1 ? Material.STONE : Material.WOOD, data == 1 ? SoundType.STONE: SoundType.WOOD, 0.35F, 8192F));
		BlockInfo.create(this, SweetMagicCore.smTab, name);
		this.block = null;
	}

	public SMTrapDoor(String name, int data, Block block) {
		super(BaseSMBlock.setState(data == 1 ? Material.STONE : Material.WOOD, data == 1 ? SoundType.STONE: SoundType.WOOD, 0.35F, 8192F));
		BlockInfo.create(this, SweetMagicCore.smTab, name);
		this.block = block;
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder build) {
		return Arrays.<ItemStack> asList(new ItemStack(this));
	}

	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	public VoxelShape getVisualShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext con) {
		return Shapes.empty();
	}

	@Override
	public float getShadeBrightness(BlockState state, BlockGetter get, BlockPos pos) {
		return 1F;
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter get, BlockPos pos) {
		return true;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter get, List<Component> toolTip, TooltipFlag flag) {
		if (this.block == null) { return; }
		toolTip.add(this.getText("originatorblock", this.block.getName().getString()).withStyle(GOLD));
	}
}
