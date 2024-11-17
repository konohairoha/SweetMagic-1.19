package sweetmagic.worldgen.tree.featuer;

import java.util.List;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.BiConsumer;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;
import sweetmagic.init.BlockInit;

public class CherryFeatuer extends Feature<TreeConfiguration> {

	public CherryFeatuer() {
		super(TreeConfiguration.CODEC);
	}

	private static boolean isVine(LevelSimulatedReader world, BlockPos pos) {
		return world.isStateAtPosition(pos, (state) -> { return state.is(Blocks.VINE); });
	}

	public static boolean isBlockWater(LevelSimulatedReader world, BlockPos pos) {
		return world.isStateAtPosition(pos, (state) -> { return state.is(Blocks.WATER); });
	}

	public static boolean isAirOrLeaves(LevelSimulatedReader world, BlockPos pos) {
		return world.isStateAtPosition(pos, (state) -> { return state.is(BlockTags.LEAVES); });
	}

	private static boolean isReplaceablePlant(LevelSimulatedReader world, BlockPos pos) {
		return world.isStateAtPosition(pos, (state) -> {
			Material mate = state.getMaterial();
			return mate == Material.REPLACEABLE_PLANT || mate == Material.REPLACEABLE_WATER_PLANT || mate == Material.REPLACEABLE_FIREPROOF_PLANT;
		});
	}

	private static void setBlockKnownShape(LevelWriter world, BlockPos pos, BlockState state) {
		world.setBlock(pos, state, 19);
	}

	public static boolean validTreePos(LevelSimulatedReader world, BlockPos pos) {
		return isAirOrLeaves(world, pos) || isReplaceablePlant(world, pos) || isBlockWater(world, pos);
	}

	private boolean doPlace(WorldGenLevel world, RandomSource rand, BlockPos pos, BiConsumer<BlockPos, BlockState> bi1, BiConsumer<BlockPos, BlockState> bi2, BiConsumer<BlockPos, BlockState> bi3, TreeConfiguration con) {

		int i = con.trunkPlacer.getTreeHeight(rand);
		int j = con.foliagePlacer.foliageHeight(rand, i, con);
		int k = i - j;
		int l = con.foliagePlacer.foliageRadius(rand, k);
		BlockPos bpos = con.rootPlacer.map((state) -> { return state.getTrunkOrigin(pos, rand); }).orElse(pos);
		int i1 = Math.min(pos.getY(), bpos.getY());
		int j1 = Math.max(pos.getY(), bpos.getY()) + i + 1;

		if (i1 >= world.getMinBuildHeight() + 1 && j1 <= world.getMaxBuildHeight()) {
			OptionalInt opti = con.minimumSize.minClippedHeight();
			int k1 = this.getMaxFreeTreeHeight(world, i, bpos, con);
			if (k1 >= i || !opti.isEmpty() && k1 >= opti.getAsInt()) {
				if (con.rootPlacer.isPresent() && !con.rootPlacer.get().placeRoots(world, bi1, rand, pos, bpos, con)) { return false; }
				List<FoliagePlacer.FoliageAttachment> list = con.trunkPlacer.placeTrunk(world, bi2, rand, k1, bpos, con);
				list.forEach(s -> con.foliagePlacer.createFoliage(world, bi3, rand, con, k1, s, j, l));
				return true;

			}

			return false;
		}

		return false;
	}

	private int getMaxFreeTreeHeight(LevelSimulatedReader world, int height, BlockPos pos, TreeConfiguration con) {

		BlockPos.MutableBlockPos bpos = new BlockPos.MutableBlockPos();

		for (int i = 0; i <= height + 1; ++i) {
			int j = con.minimumSize.getSizeAtHeight(height, i);

			for (int k = -j; k <= j; ++k) {
				for (int l = -j; l <= j; ++l) {
					bpos.setWithOffset(pos, k, i, l);
					if (!con.trunkPlacer.isFree(world, bpos) || !con.ignoreVines && isVine(world, bpos)) { return i - 2; }
				}
			}
		}

		return height;
	}

	protected void setBlock(LevelWriter world, BlockPos pos, BlockState state) {
		setBlockKnownShape(world, pos, state);
	}

	public boolean place(FeaturePlaceContext<TreeConfiguration> fpc) {

		WorldGenLevel world = fpc.level();
		RandomSource rand = fpc.random();
		BlockPos pos = fpc.origin();
		if (!this.checkBlock(world.getBlockState(pos.below()).getBlock())) { return false; }

		TreeConfiguration con = fpc.config();
		Set<BlockPos> set = Sets.newHashSet();
		Set<BlockPos> set1 = Sets.newHashSet();
		Set<BlockPos> set2 = Sets.newHashSet();
		Set<BlockPos> set3 = Sets.newHashSet();
		BiConsumer<BlockPos, BlockState> bi1 = (s1, s2) -> { set.add(s1.immutable()); world.setBlock(s1, s2, 19); };
		BiConsumer<BlockPos, BlockState> bi2 = (s1, s2) -> { set1.add(s1.immutable()); world.setBlock(s1, s2, 19); };
		BiConsumer<BlockPos, BlockState> bi3 = (s1, s2) -> { set2.add(s1.immutable()); world.setBlock(s1, s2, 19); };
		BiConsumer<BlockPos, BlockState> bi4 = (s1, s2) -> { set3.add(s1.immutable()); world.setBlock(s1, s2, 19); };
		boolean flag = this.doPlace(world, rand, pos, bi1, bi2, bi3, con);

		if (flag && (!set1.isEmpty() || !set2.isEmpty())) {

			if (!con.decorators.isEmpty()) {
				TreeDecorator.Context deco = new TreeDecorator.Context(world, bi4, rand, set1, set2, set);
				con.decorators.forEach((s) -> { s.place(deco); });
			}

			BlockState cherry_blossoms = BlockInit.cherry_blossoms_leaves_carpet.defaultBlockState();

			for (int x = -4; x <= 4; x++) {
				for (int z = -4; z <= 4; z++) {

					float chance = Math.abs(x) <= 2 && Math.abs(z) <= 2 ? 0.725F : 0.275F;
					if ( (x == 0 && z == 0) || rand.nextFloat() >= chance) { continue; }

					this.setSMBlock(world, pos.offset(x, 0, z), cherry_blossoms);
				}
			}

			return BoundingBox.encapsulatingPositions(Iterables.concat(set, set1, set2, set3)).map((s) -> {
				DiscreteVoxelShape face = updateLeaves(world, s, set1, set3, set);
				StructureTemplate.updateShapeAtEdge(world, 3, face, s.minX(), s.minY(), s.minZ());
				return true;
			}).orElse(false);
		}

		return false;
	}

	private static DiscreteVoxelShape updateLeaves(LevelAccessor world, BoundingBox aabb, Set<BlockPos> posSet1, Set<BlockPos> posSet2, Set<BlockPos> posSet3) {

		List<Set<BlockPos>> list = Lists.newArrayList();
		DiscreteVoxelShape face = new BitSetDiscreteVoxelShape(aabb.getXSpan(), aabb.getYSpan(), aabb.getZSpan());

		for (int j = 0; j < 6; ++j) {
			list.add(Sets.newHashSet());
		}

		BlockPos.MutableBlockPos mutPos = new BlockPos.MutableBlockPos();

		for (BlockPos pos : Lists.newArrayList(Sets.union(posSet2, posSet3))) {
			if (aabb.isInside(pos)) {
				face.fill(pos.getX() - aabb.minX(), pos.getY() - aabb.minY(), pos.getZ() - aabb.minZ());
			}
		}

		for (BlockPos pos1 : Lists.newArrayList(posSet1)) {

			if (aabb.isInside(pos1)) {
				face.fill(pos1.getX() - aabb.minX(), pos1.getY() - aabb.minY(), pos1.getZ() - aabb.minZ());
			}

			for (Direction fa : Direction.values()) {

				mutPos.setWithOffset(pos1, fa);
				if (posSet1.contains(mutPos)) { continue; }

				BlockState state = world.getBlockState(mutPos);
				if (!state.hasProperty(BlockStateProperties.DISTANCE)) { continue; }

				list.get(0).add(mutPos.immutable());
				setBlockKnownShape(world, mutPos, state.setValue(BlockStateProperties.DISTANCE, Integer.valueOf(1)));

				if (aabb.isInside(mutPos)) {
					face.fill(mutPos.getX() - aabb.minX(), mutPos.getY() - aabb.minY(), mutPos.getZ() - aabb.minZ());
				}
			}
		}

		for (int l = 1; l < 6; ++l) {

			Set<BlockPos> set = list.get(l - 1);
			Set<BlockPos> set1 = list.get(l);

			for (BlockPos pos2 : set) {
				if (aabb.isInside(pos2)) {
					face.fill(pos2.getX() - aabb.minX(), pos2.getY() - aabb.minY(), pos2.getZ() - aabb.minZ());
				}

				for (Direction face1 : Direction.values()) {

					mutPos.setWithOffset(pos2, face1);
					if (set.contains(mutPos) || set1.contains(mutPos)) { continue; }

					BlockState state1 = world.getBlockState(mutPos);
					if (!state1.hasProperty(BlockStateProperties.DISTANCE)) { continue; }

					int k = state1.getValue(BlockStateProperties.DISTANCE);
					if (k <= l + 1) {  continue; }

					BlockState state2 = state1.setValue(BlockStateProperties.DISTANCE, Integer.valueOf(l + 1));
					setBlockKnownShape(world, mutPos, state2);
					if (aabb.isInside(mutPos)) {
						face.fill(mutPos.getX() - aabb.minX(), mutPos.getY() - aabb.minY(), mutPos.getZ() - aabb.minZ());
					}

					set1.add(mutPos.immutable());
				}
			}
		}

		return face;
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

			for (int y = 1; y < 3; y++) {
				BlockPos targetPos = pos.above(y);
				if (world.getBlockState(targetPos).isAir() && this.checkBlock(world.getBlockState(targetPos.below()).getBlock())) {
					this.setBlock(world, targetPos, state);
					return;
				}
			}
		}
	}

	// 草か土かチェック
	public boolean checkBlock (Block block) {
		return block == Blocks.DIRT || block == Blocks.GRASS_BLOCK;
	}
}
