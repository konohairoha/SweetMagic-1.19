package sweetmagic.init.block.sm;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.block.base.BaseModelBlock;
import sweetmagic.init.block.base.BaseSMBlock;

public class SMGlass extends BaseModelBlock {

	private final boolean shading;
	private final boolean isPass;

	public SMGlass(String name, boolean shading, boolean isPass) {
		super(name, BaseSMBlock.setState(Material.GLASS, SoundType.GLASS, 0.5F, 8192F).isSuffocating((a, b, c) -> false).isViewBlocking((a, b, c) -> false));
		this.shading = shading;
		this.isPass = isPass;
		BlockInfo.create(this, SweetMagicCore.smTab, name);
	}

	public SMGlass(String name, boolean shading, boolean isPass, CreativeModeTab tab) {
		super(name, BaseSMBlock.setState(Material.GLASS, SoundType.GLASS, 0.5F, 8192F).isSuffocating((a, b, c) -> false).isViewBlocking((a, b, c) -> false));
		this.shading = shading;
		this.isPass = isPass;
		BlockInfo.create(this, tab, name);
	}

	public SMGlass(String name, boolean shading, boolean isPass, CreativeModeTab tab, BlockBehaviour.Properties pro) {
		super(name, pro);
		this.shading = shading;
		this.isPass = isPass;
		BlockInfo.create(this, tab, name);
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

		if (this.isPass && col instanceof EntityCollisionContext con && con.getEntity() instanceof Player) {
			return Shapes.empty();
		}

		return super.getCollisionShape(state, world, pos, col);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean skipRendering(@Nonnull BlockState state, BlockState adjacentBlockState, @Nonnull Direction side) {
		return adjacentBlockState.is(this) || super.skipRendering(state, adjacentBlockState, side);
	}

	@Override
	public boolean shouldDisplayFluidOverlay(BlockState state, BlockAndTintGetter world, BlockPos pos, FluidState fluidState) {
		return true;
	}

	@Override
	public void addBlockTip (List<Component> toolTip) {

		if (this.shading) {
			toolTip.add(this.getText("glass_shading").withStyle(GOLD));
		}

		if (this.isPass) {
			toolTip.add(this.getText("glass_isPass").withStyle(GOLD));
		}
	}
}
