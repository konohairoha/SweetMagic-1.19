package sweetmagic.worldgen.tree.gen;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

public class EstorTreeGen extends AbstractTreeGen {

	public EstorTreeGen(BlockState log, BlockState leave, int growValue) {
		super(log, leave, growValue);
	}

    public void generate(Level world, RandomSource rand, BlockPos pos) {

		if (!this.checkBlock(world.getBlockState(pos.below()))) { return; }

		for (int y = 1; y <= 5; y++) {
			Material mate = world.getBlockState(pos.above(y)).getMaterial();
			if (mate != Material.AIR && mate == Material.PLANT){ return; }
		}

		world.setBlock(pos, AIR, 3);

		for (int x = -1; x <= 1; x++) {
			this.setBlock(world, pos.offset(x, 2, 0), this.leave);
			this.setBlock(world, pos.offset(x, 6, 0), this.leave);
			this.setBlock(world, pos.offset(x, 7, 0), this.leave);
			this.setBlock(world, pos.offset(0, 2, x), this.leave);
			this.setBlock(world, pos.offset(0, 6, x), this.leave);
			this.setBlock(world, pos.offset(0, 7, x), this.leave);
		}

		for (int x = -2; x <= 2; x++) {
			for (int z = -1; z <= 1; z++) {
				this.setBlock(world, pos.offset(x, 3, z), this.leave);
				this.setBlock(world, pos.offset(x, 4, z), this.leave);
				this.setBlock(world, pos.offset(x, 5, z), this.leave);
				this.setBlock(world, pos.offset(z, 3, x), this.leave);
				this.setBlock(world, pos.offset(z, 4, x), this.leave);
				this.setBlock(world, pos.offset(z, 5, x), this.leave);
			}
		}

		for (int y = 0; y <= 5; y++) {
			world.setBlock(pos.offset(0, y, 0), this.log, 3);
		}
    }
}
