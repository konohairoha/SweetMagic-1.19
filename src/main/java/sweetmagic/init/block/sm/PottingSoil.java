package sweetmagic.init.block.sm;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.block.base.BaseModelBlock;
import sweetmagic.init.block.crop.MagiaFlower;

public class PottingSoil extends BaseModelBlock {

	private int chance;

	public PottingSoil(String name, int chance) {
		super(name, BlockBehaviour.Properties.copy(Blocks.DIRT).randomTicks(), SweetMagicCore.smTab);
		this.chance = chance;
	}

	public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource rand) {
		if (!this.isGlowChange(rand)) { return; }

		BlockPos upPos = pos.above();
		BlockState upState = world.getBlockState(upPos);
		Block upBlock = upState.getBlock();

		if (upBlock instanceof BonemealableBlock crop) {
			crop.performBonemeal(world, rand, upPos, upState);
		}

		else if (upBlock instanceof MagiaFlower flower) {
			if (flower.isMaxAge(upState) || !flower.canGlow(world, world.getDayTime() % 24000 < 12000)) { return; }
			flower.glowUp(world, upState, upPos);
		}
	}

	// 成長できるかどうか
	public boolean isGlowChange (RandomSource rand) {
		return rand.nextInt(this.chance) == 0;
	}

	@Override
	public boolean canSustainPlant(BlockState state, BlockGetter world, BlockPos pos, Direction face, IPlantable plant) {
		PlantType plantType = plant.getPlantType(world, pos.relative(face));
		return plantType != PlantType.NETHER && plantType != PlantType.WATER;
	}

	@Override
	public void addBlockTip (List<Component> toolTip) {
		toolTip.add(this.getText("potting_soil").withStyle(GOLD));
	}
}
