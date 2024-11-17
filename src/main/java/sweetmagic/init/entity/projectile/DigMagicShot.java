package sweetmagic.init.entity.projectile;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import sweetmagic.api.emagic.SMElement;
import sweetmagic.api.iitem.info.WandInfo;
import sweetmagic.init.EntityInit;
import sweetmagic.init.ItemInit;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.PotionInit;

public class DigMagicShot extends AbstractMagicShot {

	private int breakCount = 0;

	public DigMagicShot(EntityType<? extends DigMagicShot> entityType, Level world) {
		super(entityType, world);
	}

	public DigMagicShot(double x, double y, double z, Level world) {
		this(EntityInit.digMagic, world);
		this.setPos(x, y, z);
	}

	public DigMagicShot(Level world, LivingEntity entity, WandInfo wandInfo) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.stack = wandInfo.getStack();
	}

	public DigMagicShot(Level world, LivingEntity entity, ItemStack stack) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.stack = stack;
	}

	// えんちちーに当たった時の処理
	protected void entityHit(LivingEntity living) {

		int time = 200 * this.getWandLevel();

		if (living instanceof Player && this.getOwner() instanceof Player) {
			this.addPotion(living, MobEffects.DIG_SPEED, time, 2);
		}

		else {
			this.addPotion(living, MobEffects.DIG_SLOWDOWN, time, 2);
		}
	}

	// ブロック着弾
	protected void onHitBlock(BlockHitResult result) {

		if (this.level.isClientSide) { return; }

		if (this.getOwner() == null || !(this.getOwner() instanceof Player player) || player.hasEffect(PotionInit.non_destructive)) {
			this.discard();
			return;
		}

		if (this.getData() <= 1) {
			this.normalBreak(result.getBlockPos());
		}

		else {

			this.rangeBreak(result.getBlockPos(), result.getDirection());

			if (this.breakCount++ >= this.getMaxBreak()) {
				this.discard();
			}
		}
	}

	public void normalBreak (BlockPos pos) {

		BlockState state = this.level.getBlockState(pos);
		Block block = state.getBlock();

		if (this.level.getBlockEntity(pos) != null || block.defaultDestroyTime() < 0) {
			this.discard();
			return;
		}

		//リストの作成（めっちゃ大事）
		List<ItemStack> dropList = new ArrayList<>();
		int sumXP = 0;

		if (this.canBreakBlock(block, state)) {
			this.discard();
			return;
		}

		int data = this.getData();

		if (data == 0 && !this.getSilk()) {
			if (this.level instanceof ServerLevel server) {
				dropList.addAll(Block.getDrops(state, server, pos, this.level.getBlockEntity(pos), (Player) this.getOwner(), new ItemStack(Items.DIAMOND_PICKAXE)));

				if (state.getBlock() instanceof DropExperienceBlock exp) {
					sumXP += exp.getExpDrop(state, this.level, this.random, pos, 0, 0);
				}
			}
		}

		else {
			dropList.addAll(block.getDrops(state, this.getLoot(pos)));
		}

		// ブロック破壊
		this.breakBlock(block, state, pos);

		if (!dropList.isEmpty()) {
			BlockPos pPos = this.getOwner().blockPosition();
			for (ItemStack stack : dropList) {
				level.addFreshEntity( new ItemEntity(this.level, pPos.getX(), pPos.getY(), pPos.getZ(), stack));
			}

			if (sumXP > 0) {
				this.level.addFreshEntity( new ExperienceOrb(this.level, pPos.getX(), pPos.getY(), pPos.getZ(), sumXP));
			}
		}

		if (!this.getBlockPenetration()) {
			this.discard();
		}
	}

	public void rangeBreak (BlockPos pos, Direction face) {
		if (this.breakCount == 0) {

			Vec3 vec3 = this.getDeltaMovement();

			double x = vec3.x;
			double y = vec3.y;
			double z = vec3.z;

			switch (face) {
			case NORTH:
				x = 0D;
				y = 0D;
				break;
			case SOUTH:
				x = 0D;
				y = 0D;
				break;
			case EAST:
				y = 0D;
				z = 0D;
				break;
			case WEST:
				y = 0D;
				z = 0D;
				break;
			case UP:
				x = 0D;
				z = 0D;
				break;
			case DOWN:
				x = 0D;
				z = 0D;
				break;
			}

			this.setDeltaMovement(new Vec3(x, y, z));
		}

		int sumXP = 0;
		Player player = (Player) this.getOwner();
		ItemStack PICK = new ItemStack(Items.DIAMOND_PICKAXE);

		//リストの作成（めっちゃ大事）
		List<ItemStack> dropList = new ArrayList<>();

		// 範囲の座標取得
		Iterable<BlockPos> posList = BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1));

		for (BlockPos p : posList)  {

			BlockState state = this.level.getBlockState(p);
			Block block = state.getBlock();
			if (this.canBreakBlock(block, state) || this.level.getBlockEntity(p) != null  || block.defaultDestroyTime() < 0F) { continue; }

			if (!this.getSilk()) {

				if (this.level instanceof ServerLevel server) {
					dropList.addAll(Block.getDrops(state, server, p, null, player, PICK));

					if (state.getBlock() instanceof DropExperienceBlock exp) {
						sumXP += exp.getExpDrop(state, this.level, this.random, pos, 0, 0);
					}
				}
			}

			else {
				dropList.addAll(block.getDrops(state, this.getLoot(pos)));
			}

			// ブロック破壊
			this.breakBlock(block, state, p);
		}

		if (!dropList.isEmpty()) {

			BlockPos pPos = this.getOwner().blockPosition();

			for (ItemStack stack : dropList) {
				this.level.addFreshEntity( new ItemEntity(this.level, pPos.getX(), pPos.getY(), pPos.getZ(), stack));
			}

			if (sumXP > 0) {
				this.level.addFreshEntity( new ExperienceOrb(this.level, pPos.getX(), pPos.getY(), pPos.getZ(), sumXP));
			}
		}
	}

	// 破壊可能なブロックかどうか
	public boolean canBreakBlock (Block block, BlockState state) {
		Material material = state.getMaterial();
		return state.isAir() || material == Material.WATER || material == Material.LAVA || block == Blocks.BEDROCK || block == Blocks.END_PORTAL_FRAME;
	}

	// ブロック破壊
	public void breakBlock (Block block, BlockState state, BlockPos pos) {
		this.level.destroyBlock(pos, false);
		this.level.removeBlock(pos, false);
		this.gameEvent(GameEvent.BLOCK_DESTROY);
	}

	public LootContext.Builder getLoot (BlockPos pos) {
		ItemStack tool = new ItemStack(ItemInit.alt_pick);
		tool.enchant(Enchantments.SILK_TOUCH, 1);
		return (new LootContext.Builder((ServerLevel) this.level)).withRandom(this.level.random)
				.withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos)).withParameter(LootContextParams.TOOL, tool).withOptionalParameter(LootContextParams.BLOCK_ENTITY, this.level.getBlockEntity(pos));
	}

	// パーティクルスポーン
	protected void spawnParticle() {
		Vec3 vec = this.getDeltaMovement();
		float addX = (float) (-vec.x / 20F);
		float addY = (float) (-vec.y / 20F);
		float addZ = (float) (-vec.z / 20F);
		this.level.addParticle(ParticleInit.DIG.get(), this.getX(), this.getY(), this.getZ(), addX, addY, addZ);
	}

	// 属性の取得
	public SMElement getElement() {
		return SMElement.EARTH;
	}
}