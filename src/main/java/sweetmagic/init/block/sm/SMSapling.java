package sweetmagic.init.block.sm;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraftforge.common.ForgeHooks;
import sweetmagic.SweetMagicCore;
import sweetmagic.api.iblock.ISMCrop;
import sweetmagic.init.BlockInit;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.block.base.BaseSMBlock;
import sweetmagic.worldgen.tree.gen.AbstractTreeGen;
import sweetmagic.worldgen.tree.gen.CoconutTreeGen;
import sweetmagic.worldgen.tree.gen.EstorTreeGen;
import sweetmagic.worldgen.tree.gen.MagiaTreeGen;
import sweetmagic.worldgen.tree.gen.PrismTreeGen;
import sweetmagic.worldgen.tree.gen.SMTreeGen;

public class SMSapling extends SaplingBlock {

	private final int data;

	public SMSapling(String name, int data) {
		super(null, BaseSMBlock.setState(Material.PLANT, SoundType.GRASS, 0F, 8192F).noCollission().randomTicks());
		BlockInfo.create(this, SweetMagicCore.smTab, name);
		this.data = data;
		BlockInit.saplingList.add(this);
	}

	public Block getLog() {
		switch(this.data) {
		case 1: return BlockInit.lemon_log;
		case 2: return BlockInit.orange_log;
		case 3: return BlockInit.coconut_log;
		case 4: return BlockInit.prism_log;
		case 5: return Blocks.JUNGLE_LOG;
		case 6: return BlockInit.estor_log;
		case 7: return BlockInit.peach_log;
		case 8: return BlockInit.magiawood_log;
		case 9: return BlockInit.cherry_blossoms_log;
		case 10: return BlockInit.peach_log;
		default: return BlockInit.chestnut_log;
		}
	}

	public BlockState getLeaves() {
		switch(this.data) {
		case 1: return BlockInit.lemon_leaves.defaultBlockState();
		case 2: return BlockInit.orange_leaves.defaultBlockState();
		case 3: return BlockInit.coconut_leaves.defaultBlockState();
		case 4: return BlockInit.prism_leaves.defaultBlockState();
		case 5: return BlockInit.banana_leaves.defaultBlockState();
		case 6: return BlockInit.estor_leaves.defaultBlockState();
		case 7: return BlockInit.peach_leaves.defaultBlockState();
		case 8: return BlockInit.magiawood_leaves.defaultBlockState();
		case 9: return BlockInit.cherry_blossoms_leaves.defaultBlockState();
		case 10: return BlockInit.maple_leaves.defaultBlockState().setValue(ISMCrop.AGE5, new Random().nextInt(6));
		default: return BlockInit.chestnut_leaves.defaultBlockState();
		}
	}

	public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource rand) {
		if (ForgeHooks.onCropsGrowPre(world, pos, state, this.isGlowChange(rand))) {
			this.advanceTree(world, pos, state, rand);
		}
	}

	// 成長できるかどうか
	public boolean isGlowChange(RandomSource rand) {
		return rand.nextInt(2) == 0;
	}

	public AbstractTreeGen getTreeGen() {
		switch(this.data) {
		case 3: return new CoconutTreeGen(this.getLog().defaultBlockState(), this.getLeaves(), BlockInit.coconut_plant.defaultBlockState(), 0);
		case 4: return new PrismTreeGen(this.getLog().defaultBlockState(), this.getLeaves(), 0);
		case 5: return new CoconutTreeGen(this.getLog().defaultBlockState(), this.getLeaves(), BlockInit.banana_plant.defaultBlockState(), 0);
		case 6: return new EstorTreeGen(this.getLog().defaultBlockState(), this.getLeaves(), 0);
		case 8: return new MagiaTreeGen(this.getLog().defaultBlockState(), this.getLeaves(), 0);
		default: return new SMTreeGen(this.getLog().defaultBlockState(), this.getLeaves(), 0);
		}
	}

	public void advanceTree(ServerLevel world, BlockPos pos, BlockState state, RandomSource rand) {

		if (state.getValue(STAGE) == 0) {
			world.setBlock(pos, state.cycle(STAGE), 4);
		}

		else {
			this.getTreeGen().generate(world, rand, pos);
		}
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder build) {
		return Arrays.<ItemStack> asList(new ItemStack(this));
	}
}
