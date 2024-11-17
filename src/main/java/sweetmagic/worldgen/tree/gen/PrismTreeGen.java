package sweetmagic.worldgen.tree.gen;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import sweetmagic.init.block.sm.FruitLeaves;

public class PrismTreeGen extends AbstractTreeGen {

    public int height = 19;
    public int randHeight = 24;

	public PrismTreeGen(BlockState log, BlockState leave, int growValue) {
		super(log, leave, growValue);
	}

    public void generate(Level world, RandomSource rand, BlockPos pos) {

		if (!this.checkBlock(world.getBlockState(pos.below()))) { return; }

		for (int y = 1; y <= 8; y++) {
			Material mate = world.getBlockState(pos.above(y)).getMaterial();
			if (mate != Material.AIR && mate == Material.PLANT){ return; }
		}

		world.setBlock(pos, AIR, 3);

		int maxHeight = rand.nextInt(this.randHeight) + this.randHeight;
		BlockState log = this.log;
		BlockState leave = this.leave;

		if (leave.hasProperty(FruitLeaves.NOGLOW)) {
			leave = leave.setValue(FruitLeaves.NOGLOW, true);
		}

		this.subTrunk(world, pos.north(), log);
		this.subTrunk(world, pos.south(), log);
		this.subTrunk(world, pos.west(), log);
		this.subTrunk(world, pos.east(), log);

		for (int y = 0; y < maxHeight; y++) {
			this.setBlock(world, pos.above(y), log);
		}

		for (int x = -2; x <= 2; x++) {
			for (int z = -2; z <= 2; z++) {
				for (int y = this.height; y <= maxHeight - 4; y++) {

					if (rand.nextInt(20) != 0) { continue; }
					this.setLeave(world, pos.offset(x, y, z), leave);

					boolean isOverX = x == 2 || x == -2;
					boolean isOverZ = z == 2 || z == -2;

					if ( isOverX && isOverZ ) {
						this.setBlock(world, pos.offset(x / 2, y - 1, z / 2), log);
					}

					else if (isOverX) {
						this.setBlock(world, pos.offset(x / 2, y - 1, z), log);
					}

					else if (isOverZ) {
						this.setBlock(world, pos.offset(x, y - 1, z / 2), log);
					}
				}
			}
		}

		BlockPos top = pos.offset(0, maxHeight - 1, 0);

		for (int x = -2; x <= 2; x++) {
			for (int z = -2; z <= 2; z++) {
				this.setBlock(world, top.offset(x, 0, z), leave);
			}
		}

		this.setAir(world, top.offset(-2, 0, -2), leave);
		this.setAir(world, top.offset(2, 0, -2), leave);
		this.setAir(world, top.offset(-2, 0, 2), leave);
		this.setAir(world, top.offset(2, 0, 2), leave);

		this.setBlock(world, top.offset(0, 1, -1), leave);
		this.setBlock(world, top.offset(-1, 1, 0), leave);
		this.setBlock(world, top.offset(0, 1, 1), leave);
		this.setBlock(world, top.offset(1, 1, 0), leave);
		this.setBlock(world, top.above(), leave);
		this.setBlock(world, top.above(2), leave);
    }

    // 主軸の横
	public void subTrunk (Level world, BlockPos pos, BlockState state) {

		RandomSource rand = world.random;
		int height = rand.nextInt(6) + 8;

		for (int y = 0; y <= height; y++) {
			this.setBlock(world, pos.above(y), state);
		}
	}

	// 葉っぱ
	public void setLeave (Level world, BlockPos pos, BlockState leave) {

		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				this.setBlock(world, pos.offset(x, 0, z), leave);
				this.setBlock(world, pos.offset(x, 1, z), leave);
			}
		}

		this.setAir(world, pos.offset(-1, 1, -1), leave);
		this.setAir(world, pos.offset(1, 1, -1), leave);
		this.setAir(world, pos.offset(-1, 1, 1), leave);
		this.setAir(world, pos.offset(1, 1, 1), leave);
		world.setBlock(pos, this.log, 3);
	}

	// 空気
	public void setAir (Level world, BlockPos pos, BlockState state) {
		if (world.getBlockState(pos).getBlock() == state.getBlock()) {
			world.setBlock(pos, AIR, 3);
		}
	}
}
