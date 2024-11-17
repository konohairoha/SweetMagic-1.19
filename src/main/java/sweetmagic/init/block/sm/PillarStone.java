package sweetmagic.init.block.sm;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.util.EnumVertical;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.block.base.BaseModelBlock;

public class PillarStone extends BaseModelBlock {

	public static final EnumProperty<EnumVertical> VERTICAL = EnumProperty.create("vertical", EnumVertical.class);

	public PillarStone (String name) {
		super(name, setState(Material.STONE, SoundType.STONE, 1F, 8192F));
		this.registerDefaultState(this.defaultBlockState().setValue(VERTICAL, EnumVertical.NOR));
		BlockInfo.create(this, SweetMagicCore.smTab, name);
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> build) {
		build.add(VERTICAL);
	}

	@Nullable
	public BlockState getStateForPlacement(BlockPlaceContext con) {
		return this.setVertical(con.getLevel(), con.getClickedPos());
	}

	public BlockState setVertical (LevelAccessor world, BlockPos pos) {
		boolean bot = this.getBlock(world, pos.below()) == this;
		boolean top = this.getBlock(world, pos.above()) == this;
		return this.defaultBlockState().setValue(VERTICAL, EnumVertical.getVertical(bot, top));
	}

	public BlockState updateShape(BlockState state, Direction face, BlockState state2, LevelAccessor world, BlockPos pos1, BlockPos pos2) {
		return this.setVertical(world, pos1);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean skipRendering(@Nonnull BlockState state, BlockState state2, @Nonnull Direction side) {
		return state2.is(this) || super.skipRendering(state, state2, side);
	}
}
