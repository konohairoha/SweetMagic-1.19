package sweetmagic.worldgen.tree.featuer;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.material.Material;
import sweetmagic.api.iblock.ISMCrop;
import sweetmagic.init.BlockInit;

public class EstorFeatuer extends AbstractTreeFeatuer {

	public EstorFeatuer() {
		super();
	}

	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> con) {

		BlockPos pos = con.origin();
		WorldGenLevel world = con.level();
		if (!this.checkBlock(world.getBlockState(pos.below()).getBlock())) { return false; }

		for (int y = 1; y <= 8; y++) {
			Material mate = world.getBlockState(pos.above(y)).getMaterial();
			if (mate != Material.AIR && mate == Material.PLANT){ return false; }
		}

		BlockState log = this.getLog();
		BlockState leave = this.getLeave();

		this.setBlock(world, pos, AIR);

		for (int x = -1; x <= 1; x++) {
			this.setBlock(world, pos.offset(x, 2, 0), leave);
			this.setBlock(world, pos.offset(x, 6, 0), leave);
			this.setBlock(world, pos.offset(x, 7, 0), leave);
			this.setBlock(world, pos.offset(0, 2, x), leave);
			this.setBlock(world, pos.offset(0, 6, x), leave);
			this.setBlock(world, pos.offset(0, 7, x), leave);
		}

		for (int x = -2; x <= 2; x++) {
			for (int z = -1; z <= 1; z++) {
				this.setBlock(world, pos.offset(x, 3, z), leave);
				this.setBlock(world, pos.offset(x, 4, z), leave);
				this.setBlock(world, pos.offset(x, 5, z), leave);
				this.setBlock(world, pos.offset(z, 3, x), leave);
				this.setBlock(world, pos.offset(z, 4, x), leave);
				this.setBlock(world, pos.offset(z, 5, x), leave);
			}
		}

		for (int y = 0; y <= 5; y++) {
			this.setBlock(world, pos.offset(0, y, 0), log);
		}

		return true;
	}

	public BlockState getLog() {
		return BlockInit.estor_log.defaultBlockState();
	}

	public BlockState getLeave() {
		return BlockInit.estor_leaves.defaultBlockState().setValue(ISMCrop.AGE2, 2);
	}
}
