package sweetmagic.worldgen.tree.featuer;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.material.Material;
import sweetmagic.api.iblock.ISMCrop;
import sweetmagic.init.BlockInit;

public class SMTreeFeatuer extends AbstractTreeFeatuer {

	private final int data;

	public SMTreeFeatuer(int data) {
		super();
		this.data = data;
	}

	public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> con) {
		BlockPos pos = con.origin();
		WorldGenLevel world = con.level();
		if (!this.checkBlock(world.getBlockState(pos.below()).getBlock())) { return false; }

		for (int y = 1; y <= 8; y++) {
			Material mate = world.getBlockState(pos.above(y)).getMaterial();
			if (mate != Material.AIR && mate == Material.PLANT){ return false; }
		}

		Random rand = new Random();
		BlockState log = this.getLog();
		BlockState leave = this.getLeave();

		this.setBlock(world, pos, AIR);

		//葉っぱ一段目
		for (int x = -2; x <= 2; x++) {
			for (int z = -2; z <= 2; z++) {
				this.setBlock(world, pos.offset(x, 3, z), leave);
			}
		}

		//葉っぱ２段目
		for (int x = -1; x <= 1; x++) {
			for (int z = -2; z <= 2; z++) {
				this.setBlock(world, pos.offset(x, 4, z), leave);
				this.setBlock(world, pos.offset(z, 4, x), leave);
			}
		}

		//葉っぱ3段目
		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				this.setBlock(world, pos.offset(x, 5, z), leave);
			}
		}

		//葉っぱ最上段
		for (int z = -1; z <= 1; z++) {
			this.setBlock(world, pos.offset(0, 6, z), leave);
			this.setBlock(world, pos.offset(z, 6, 0), leave);
		}

		//原木
		for (int y = 0; y <= 5; y++) {
			this.setBlock(world, pos.offset(0, y, 0), log);
		}

		if (leave.getBlock() == BlockInit.chestnut_leaves) {
			BlockState chestnut = BlockInit.chestnut_plant.defaultBlockState().setValue(ISMCrop.AGE2, 2);
			pos = pos.above(2);

			this.setBlock(world, pos.north(2), chestnut);
			this.setBlock(world, pos.west(2), chestnut);
			this.setBlock(world, pos.south(2), chestnut);
			this.setBlock(world, pos.east(2), chestnut);
		}

		else if (leave.getBlock() == BlockInit.cherry_blossoms_leaves) {
			BlockState cherry_blossoms = BlockInit.cherry_blossoms_leaves_carpet.defaultBlockState();

			for (int x = -4; x <= 4; x++) {
				for (int z = -4; z <= 4; z++) {

					float chance = Math.abs(x) <= 2 && Math.abs(z) <= 2 ? 0.725F : 0.275F;
					if ( (x == 0 && z == 0) || rand.nextFloat() >= chance) { continue; }

					this.setSMBlock(world, pos.offset(x, 0, z), cherry_blossoms);
				}
			}
		}

		return true;
	}

	public BlockState getLog() {
		switch (this.data) {
		case 1: return BlockInit.lemon_log.defaultBlockState();
		case 2: return BlockInit.orange_log.defaultBlockState();
		case 3: return BlockInit.estor_log.defaultBlockState();
		case 4: return BlockInit.peach_log.defaultBlockState();
		case 5: return BlockInit.cherry_blossoms_log.defaultBlockState();
		case 6: return BlockInit.peach_log.defaultBlockState();
		default: return BlockInit.chestnut_log.defaultBlockState();
		}
	}

	public BlockState getLeave() {
		switch (this.data) {
		case 1: return BlockInit.lemon_leaves.defaultBlockState().setValue(ISMCrop.AGE2, 2);
		case 2: return BlockInit.orange_leaves.defaultBlockState().setValue(ISMCrop.AGE2, 2);
		case 3: return BlockInit.estor_leaves.defaultBlockState().setValue(ISMCrop.AGE2, 2);
		case 4: return BlockInit.peach_leaves.defaultBlockState().setValue(ISMCrop.AGE2, 2);
		case 5: return BlockInit.cherry_blossoms_leaves.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true);
		case 6: return BlockInit.maple_leaves.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true).setValue(ISMCrop.AGE5, new Random().nextInt(6));
		default: return BlockInit.chestnut_leaves.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true);
		}
	}

	public void setSMBlock(WorldGenLevel world, BlockPos pos, BlockState state) {
		if (world.getBlockState(pos).isAir() && this.checkBlock(world.getBlockState(pos.below()).getBlock())) {
			this.setBlock(world, pos, state);
		}

		else {
			for (int y = 1; y < 4; y++) {
				BlockPos targetPos = pos.below(y);
				if (world.getBlockState(targetPos).isAir() && this.checkBlock(world.getBlockState(targetPos.below()).getBlock())) {
					this.setBlock(world, targetPos, state);
					return;
				}
			}

			for (int y = 1; y < 2; y++) {
				BlockPos targetPos = pos.above(y);
				if (world.getBlockState(targetPos).isAir() && this.checkBlock(world.getBlockState(targetPos.below()).getBlock())) {
					this.setBlock(world, targetPos, state);
					return;
				}
			}
		}
	}
}
