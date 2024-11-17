package sweetmagic.worldgen.tree.gen;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

public class CoconutTreeGen extends AbstractTreeGen {

	private final BlockState coconut;

	public CoconutTreeGen(BlockState log, BlockState leave, BlockState coconut, int growValue) {
		super(log, leave, growValue);
		this.coconut = coconut;
	}

    public void generate(Level world, RandomSource rand, BlockPos pos) {

		if (!this.checkBlock(world.getBlockState(pos.below()))) { return; }

		for (int y = 1; y <= 8; y++) {
			Material mate = world.getBlockState(pos.above(y)).getMaterial();
			if (mate != Material.AIR && mate == Material.PLANT){ return; }
		}

		world.setBlock(pos, AIR, 3);

		// 葉っぱ一段目
		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				for (int y = 7; y <= 9; y++) {
					this.setBlock(world, pos.offset(x, y, z), this.leave);
				}
			}
		}

		for (int x = -4; x <= 4; x++) {
			this.setBlock(world, pos.offset(x, 9, 0), this.leave);
			this.setBlock(world, pos.offset(0, 9, x), this.leave);
		}

		world.setBlock(pos.offset(-1, 7, -1), AIR, 3);
		world.setBlock(pos.offset(-1, 7, 1), AIR, 3);
		world.setBlock(pos.offset(1, 7, -1), AIR, 3);
		world.setBlock(pos.offset(1, 7, 1), AIR, 3);

		for (int y = 7; y <= 8; y++) {
			this.setBlock(world, pos.offset(5, y, 0), this.leave);
			this.setBlock(world, pos.offset(0, y, 5), this.leave);
			this.setBlock(world, pos.offset(-5, y, 0), this.leave);
			this.setBlock(world, pos.offset(0, y, -5), this.leave);
			this.setBlock(world, pos.offset(-4, y, -4), this.leave);
			this.setBlock(world, pos.offset(-4, y, 4), this.leave);
			this.setBlock(world, pos.offset(4, y, -4), this.leave);
			this.setBlock(world, pos.offset(4, y, 4), this.leave);
		}

		for (int x = -1; x <= 1; x++) {
			this.setBlock(world, pos.offset(x, 6, 0), this.coconut);
			this.setBlock(world, pos.offset(x, 7, 0), this.leave);
			this.setBlock(world, pos.offset(x, 10, 0), this.leave);
			this.setBlock(world, pos.offset(0, 6, x), this.coconut);
			this.setBlock(world, pos.offset(0, 7, x), this.leave);
			this.setBlock(world, pos.offset(0, 10, x), this.leave);
		}

		this.setBlock(world, pos.offset(-2, 9, -2), this.leave);
		this.setBlock(world, pos.offset(-2, 9, 2), this.leave);
		this.setBlock(world, pos.offset(2, 9, -2), this.leave);
		this.setBlock(world, pos.offset(2, 9, 2), this.leave);

		this.setBlock(world, pos.offset(-3, 9, -3), this.leave);
		this.setBlock(world, pos.offset(-3, 9, 3), this.leave);
		this.setBlock(world, pos.offset(3, 9, -3), this.leave);
		this.setBlock(world, pos.offset(3, 9, 3), this.leave);

		//原木
		for (int y = 0; y <= 8; y++) {
			world.setBlock(pos.offset(0, y, 0), this.log, 3);
		}
    }
}
