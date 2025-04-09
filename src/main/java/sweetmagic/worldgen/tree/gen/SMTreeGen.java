package sweetmagic.worldgen.tree.gen;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import sweetmagic.api.iblock.ISMCrop;
import sweetmagic.init.BlockInit;

public class SMTreeGen extends AbstractTreeGen {

	public SMTreeGen(BlockState log, BlockState leave, int growValue) {
		super(log, leave, growValue);
	}

	public void generate(Level world, RandomSource rand, BlockPos pos) {
		if (!this.checkBlock(world.getBlockState(pos.below()))) { return; }

		for (int y = 1; y <= 5; y++) {
			Material mate = world.getBlockState(pos.above(y)).getMaterial();
			if (mate != Material.AIR && mate == Material.PLANT){ return; }
		}

		world.setBlock(pos, AIR, 3);
		this.setBlock(world, pos, AIR);

		//葉っぱ一段目
		for (int x = -2; x <= 2; x++) {
			for (int z = -2; z <= 2; z++) {
				this.setBlock(world, pos.offset(x, 3, z), this.leave);
			}
		}

		//葉っぱ２段目
		for (int x = -1; x <= 1; x++) {
			for (int z = -2; z <= 2; z++) {
				this.setBlock(world, pos.offset(x, 4, z), this.leave);
				this.setBlock(world, pos.offset(z, 4, x), this.leave);
			}
		}

		//葉っぱ3段目
		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				this.setBlock(world, pos.offset(x, 5, z), this.leave);
			}
		}

		//葉っぱ最上段
		for (int z = -1; z <= 1; z++) {
			this.setBlock(world, pos.offset(0, 6, z), this.leave);
			this.setBlock(world, pos.offset(z, 6, 0), this.leave);
		}

		//原木
		for (int y = 0; y <= 5; y++) {
			world.setBlock(pos.offset(0, y, 0), this.log, 3);
		}

		if (this.leave.getBlock() != BlockInit.chestnut_leaves) { return; }

		BlockState chestnut = BlockInit.chestnut_plant.defaultBlockState().setValue(ISMCrop.AGE2, this.growValue);
		pos = pos.above(2);

		world.setBlock(pos.north(2), chestnut, 3);
		world.setBlock(pos.west(2), chestnut, 3);
		world.setBlock(pos.south(2), chestnut, 3);
		world.setBlock(pos.east(2), chestnut, 3);
	}
}
