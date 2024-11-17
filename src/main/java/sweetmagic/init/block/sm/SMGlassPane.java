package sweetmagic.init.block.sm;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.util.ISMTip;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.block.base.BaseSMBlock;

public class SMGlassPane extends IronBarsBlock implements ISMTip {

	private final boolean shading;
	private final boolean isPass;

	public SMGlassPane(String name, boolean shading, boolean isPass) {
		super(BaseSMBlock.setState(Material.GLASS, SoundType.GLASS, 0.5F, 8192F).isSuffocating((a, b, c) -> false).isViewBlocking((a, b, c) -> false));
		this.registerDefaultState(this.stateDefinition.any().setValue(NORTH, Boolean.valueOf(false)).setValue(EAST, Boolean.valueOf(false)).setValue(SOUTH, Boolean.valueOf(false)).setValue(WEST, Boolean.valueOf(false)).setValue(WATERLOGGED, Boolean.valueOf(false)));
		this.shading = shading;
		this.isPass = isPass;
		BlockInfo.create(this, SweetMagicCore.smTab, name);
	}

	public SMGlassPane(BlockBehaviour.Properties pro) {
		super(pro);
		this.shading = false;
		this.isPass = false;
	}

	public boolean propagatesSkylightDown(BlockState state, BlockGetter get, BlockPos pos) {
		return this.shading;
	}

	public int getLightBlock(BlockState state, BlockGetter get, BlockPos pos) {
		return this.shading ? get.getMaxLightLevel() : super.getLightBlock(state, get, pos);
	}

	@Nonnull
	@Override
	public VoxelShape getCollisionShape(BlockState state, @Nonnull BlockGetter world, @Nonnull BlockPos pos, CollisionContext col) {

		if (this.isPass && col instanceof EntityCollisionContext ctxt) {

			Entity entity = ctxt.getEntity();
			if (entity instanceof Player) { return Shapes.empty(); }

			else if (entity instanceof Mob) { return super.getCollisionShape(state, world, pos, col); }
		}
		return super.getCollisionShape(state, world, pos, col);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean skipRendering(@Nonnull BlockState state, BlockState adjacentBlockState, @Nonnull Direction side) {
		return adjacentBlockState.getBlock() == this || super.skipRendering(state, adjacentBlockState, side);
	}

	@Override
	public boolean shouldDisplayFluidOverlay(BlockState state, BlockAndTintGetter world, BlockPos pos, FluidState fluidState) {
		return true;
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder build) {
		return Arrays.<ItemStack> asList(new ItemStack(this));
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter get, List<Component> toolTip, TooltipFlag flag) {

		if (this.shading) {
			toolTip.add(this.getText("glass_shading").withStyle(GOLD));
		}

		if (this.isPass) {
			toolTip.add(this.getText("glass_isPass").withStyle(GOLD));
		}
	}
}
