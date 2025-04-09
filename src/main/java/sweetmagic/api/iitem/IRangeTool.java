package sweetmagic.api.iitem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import sweetmagic.init.ItemInit;
import sweetmagic.init.PotionInit;
import sweetmagic.util.SMDebug;
import sweetmagic.util.WorldHelper;

public interface IRangeTool {

	public static final List<BlockEntityType<?>> tileList = Arrays.<BlockEntityType<?>> asList(
		BlockEntityType.ENDER_CHEST, BlockEntityType.MOB_SPAWNER
	);

	int getRange();

	default boolean isDepth() {
		return false;
	}

	default void rangeBreake(ItemStack stack, Level world, BlockPos pos, LivingEntity living, RayTracePointer ray) {
		if (world.isClientSide || !(living instanceof Player player) || living.hasEffect(PotionInit.non_destructive)) { return; }

		HitResult mop = ray.rayTrace(world, player, ClipContext.Fluid.NONE);
		if (!(mop instanceof BlockHitResult result) || result.getType() == HitResult.Type.MISS || !pos.equals(result.getBlockPos())) { return; }

		Direction face = result.getDirection();

		int area = this.getRange();
		int xa = 0, ya = 0, za = 0, xb = 0, yb = 0, zb = 0;
		int rangeX, rangeY, rangeZ; //向きに合わせて座標を変えるための変数
		rangeX = rangeY = rangeZ = area;
		area += 1;

		switch (face) {
		case UP:
			ya = this.isDepth() ? -area : 0;
			rangeY = 0;
			break;
		case DOWN:
			yb = this.isDepth() ? area : 0;
			rangeY = 0;
			break;
		case NORTH:
			zb = this.isDepth() ? area : 0;
			rangeZ = 0;
			break;
		case SOUTH:
			za = this.isDepth() ? -area : 0;
			rangeZ = 0;
			break;
		case EAST:
			xb = this.isDepth() ? -area : 0;
			rangeX = 0;
			break;
		case WEST:
			xa = this.isDepth() ? area : 0;
			rangeX = 0;
			break;
		}

		// 範囲の座標取得
		Iterable<BlockPos> pList = WorldHelper.getRangePos(pos, -rangeX + xa, -rangeY + ya, -rangeZ + za, rangeX + xb, rangeY + yb, rangeZ + zb);

		//リストの作成（めっちゃ大事）
		List<ItemStack> dropList = new ArrayList<>();

		int sumXP = 0;
		int fortune = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, stack);
		int silkTouch = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, stack);

		ItemStack leg = player.getItemBySlot(EquipmentSlot.LEGS);

		if (!leg.isEmpty() && leg.getItem() instanceof IPorch porch) {
			silkTouch = porch.hasAcceIsActive(leg, ItemInit.earth_ruby_ring) ? 1 : 0;
		}

		for (BlockPos p : pList) {

			SMDebug.info(p);
			BlockState state = world.getBlockState(p);
			if (!this.isAllBlock() && !stack.isCorrectToolForDrops(state)) { continue; }

			BlockEntity tile = world.getBlockEntity(p);
			if ((tile != null && !tileList.contains(tile.getType())) && tile != null) { continue; }

			if (silkTouch <= 0) {

				if (world instanceof ServerLevel server) {
					dropList.addAll(Block.getDrops(state, server, p, world.getBlockEntity(p), living, stack));
				}

				if (state.getBlock() instanceof DropExperienceBlock block) {
					sumXP += block.getExpDrop(state, world, world.random, pos, fortune, silkTouch);
				}
			}

			else {
				if (world instanceof ServerLevel server) {
					dropList.addAll(state.getBlock().getDrops(state, this.getLoot(server, pos)));
				}
			}

//			world.destroyBlock(p, false);
			world.removeBlock(p, false);
			living.gameEvent(GameEvent.BLOCK_DESTROY);
		}

		if (!dropList.isEmpty()) {
			BlockPos pPos = living.blockPosition();
			dropList.forEach(s -> world.addFreshEntity(new ItemEntity(world, pPos.getX(), pPos.getY(), pPos.getZ(), s)));

			if (sumXP > 0) {
				world.addFreshEntity(new ExperienceOrb(world, pPos.getX(), pPos.getY(), pPos.getZ(), sumXP));
			}
		}
	}

	default LootContext.Builder getLoot (ServerLevel world, BlockPos pos) {
		ItemStack tool = new ItemStack(ItemInit.alt_pick);
		tool.enchant(Enchantments.SILK_TOUCH, 1);
		return new LootContext.Builder(world).withRandom(world.random).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
				.withParameter(LootContextParams.TOOL, tool).withOptionalParameter(LootContextParams.BLOCK_ENTITY, world.getBlockEntity(pos));
	}

	default boolean isAllBlock () {
		return false;
	}

	@FunctionalInterface
	public interface RayTracePointer {
		HitResult rayTrace(Level level, Player player, Fluid fluidMode);
	}
}
