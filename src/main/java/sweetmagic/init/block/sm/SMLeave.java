package sweetmagic.init.block.sm;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import sweetmagic.SweetMagicCore;
import sweetmagic.init.BlockInit;
import sweetmagic.init.BlockInit.BlockInfo;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.block.base.BaseSMBlock;

public class SMLeave extends LeavesBlock {

	public int data;
	private final static BlockBehaviour.StatePredicate never = (a, b, c) -> false;
	private final static BlockBehaviour.StateArgumentPredicate<EntityType<?>> checkMob = (s, g, p, e) -> e == EntityType.OCELOT || e == EntityType.PARROT;

	public SMLeave(String name, int data) {
		super(BaseSMBlock.setState(Material.LEAVES, SoundType.GRASS, 0.2F, 8192F, (data == 2 || data == 5) ? 7 : 0).randomTicks().isValidSpawn(checkMob).isSuffocating(never).isViewBlocking(never));
		this.data = data;
		BlockInfo.create(this, SweetMagicCore.smTab, name);
	}

	/**
	 * 0 = 栗
	 * 1 = ヤシ
	 * 2 = プリズミウム
	 * 3 = バナナ
	 * 4 = マギアウッド
	 */

	// ドロップアイテムの取得
	public ItemLike getDrop() {
		switch (this.data) {
		case 0: return BlockInit.chestnut_sapling;
		case 1: return BlockInit.coconut_sapling;
		case 2: return BlockInit.prism_sapling;
		case 3: return BlockInit.banana_sapling;
		case 4: return BlockInit.magiawood_sapling;
		case 5: return BlockInit.cherry_blossoms_sapling;
		case 6: return BlockInit.maple_sapling;
		default: return BlockInit.chestnut_sapling;
		}
	}

	// シルクタッチでのドロップ
	public ItemStack getSilkDrop() {
		return new ItemStack(this);
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder build) {

		ServerLevel world = build.getLevel();
		RandomSource rand = world.getRandom();
		List<ItemStack> stackList = new ArrayList<ItemStack>();
		ItemStack stack = build.getOptionalParameter(LootContextParams.TOOL);

		// シルクタッチの場合はそのままドロップ
		if (!stack.isEmpty() && ( stack.getEnchantmentLevel(Enchantments.SILK_TOUCH) > 0 || stack.getItem() instanceof ShearsItem)) {
			stackList.add(this.getSilkDrop());
			return stackList;
		}

		int level = !stack.isEmpty() ? stack.getEnchantmentLevel(Enchantments.BLOCK_FORTUNE) : 0;
		float chance = 0.1F + (0.05F * (float) level);

		if (chance >= rand.nextFloat()) {
			stackList.add(new ItemStack(this.getDrop()));
		}

		return stackList;
	}

	public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource rand) {
		if (this.data != 5 || rand.nextFloat() <= 0.85F || !world.isEmptyBlock(pos.below())) { return; }

		double d0 = rand.nextDouble() * 0.05D;
		double d1 = rand.nextDouble() * 0.02D;
		double d2 = rand.nextDouble() * 0.05D;
		world.addParticle(ParticleInit.CHERRY_BLOSSOMS, this.getRand(pos.getX(), rand, 1.25D), this.getRandY(pos, rand), this.getRand(pos.getZ(), rand, 1.25D), d0, d1, d2);
	}

	public double getRand(int pos, RandomSource rand, double scale) {
		return pos + ((rand.nextDouble() - 0.5D) * scale) + 0.5D;
	}

	public double getRandY(BlockPos pos, RandomSource rand) {
		return pos.getY() - 0.5D + rand.nextDouble() * 0.15D;
	}

	@Override
	public VoxelShape getVisualShape(BlockState state, BlockGetter get, BlockPos pos, CollisionContext con) {
		return Shapes.empty();
	}

	@Override
	public float getShadeBrightness(BlockState state, BlockGetter get, BlockPos pos) {
		return 1F;
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter get, BlockPos pos) {
		return true;
	}
}
