package sweetmagic.init.entity.projectile;

import java.util.List;
import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import sweetmagic.api.emagic.SMElement;
import sweetmagic.api.iitem.info.WandInfo;
import sweetmagic.init.BlockInit;
import sweetmagic.init.EntityInit;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.PotionInit;
import sweetmagic.util.PlayerHelper;

public class LightMagicShot extends AbstractMagicShot {

	public LightMagicShot(EntityType<? extends AbstractMagicShot> entityType, Level world) {
		super(entityType, world);
	}

	public LightMagicShot(double x, double y, double z, Level world) {
		this(EntityInit.lightMagic, world);
		this.setPos(x, y, z);
	}

	public LightMagicShot(Level world, LivingEntity entity, WandInfo wandInfo) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.stack = wandInfo.getStack();
	}

	public LightMagicShot(Level world, LivingEntity entity) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.stack = ItemStack.EMPTY;
	}

	// えんちちーに当たった時の処理
	protected void entityHit(LivingEntity living) {

		int time = 200 * this.getWandLevel();

		if (living instanceof Player && this.getOwner() instanceof Player) {
			this.addPotion(living, MobEffects.NIGHT_VISION, time, 1);
		}

		else {
			this.addPotion(living, MobEffects.GLOWING, time, 1);
		}

		if (this.getData() <= 0) { return; }

		double range = this.getRange() / 0.67D;
		float dame = 0.5F + 0.67F * this.getWandLevel() * this.getDamageRate();

		this.rangeAttack(living.blockPosition(), dame, range);
	}

	// ブロック着弾
	protected void onHitBlock(BlockHitResult result) {
		if (!(this.getOwner() instanceof Player)) { return; }

		if (this.getData() >= 1) {
			double range = this.getRange();
			float dame = 0.5F + 0.5F * this.getWandLevel() * this.getDamageRate();

			this.rangeAttack(result.getBlockPos().above(), dame, range);
			this.discard();
			return;
		}

		BlockPos pos = result.getBlockPos().relative(result.getDirection());
		BlockState state = this.getLevel().getBlockState(pos);
		Block block = state.getBlock();

		// 空気ブロックなら設置
		if (block == Blocks.AIR || state.getMaterial().isReplaceable()) {
			this.getLevel().setBlock(pos, BlockInit.magiclight.defaultBlockState(), 2);
		}

		this.discard();
	}

	public void rangeAttack(BlockPos bPos, float dame, double range) {

		List<LivingEntity> entityList = this.getEntityList(LivingEntity.class, this.isTarget(), this.getRange());
		int time = 200 * this.getWandLevel();
		int data = this.getData();
		boolean isTier4 = data >= 3;

		if (isTier4 && this.getLevel() instanceof ServerLevel sever) {
			float x = (float) (bPos.getX() + this.getRandFloat(0.5F));
			float y = (float) (bPos.getY() + this.getRandFloat(0.5F));
			float z = (float) (bPos.getZ() + this.getRandFloat(0.5F));

			int count = 16;
			float rate = 0.15F;

			for (int i = 0; i < count; i++) {
				sever.sendParticles(ParticleTypes.FLAME, x, y, z, 4, 0F, 0F, 0F, rate);
			}
		}

		int addAttack = 1;

		switch (data) {
		case 2:
			addAttack = 3;
			break;
		case 3:
			addAttack = 5;
			break;
		}

		for (LivingEntity entity : entityList) {

			if (isTier4) {
				List<MobEffectInstance> effecList = PlayerHelper.getEffectList(entity, PotionInit.BUFF);
				effecList.forEach(p -> entity.removeEffect(p.getEffect()));
				this.addPotion(entity, PotionInit.flame, time, 0);
			}

			boolean hasGlow = entity.hasEffect(MobEffects.GLOWING);
			this.addPotion(entity, MobEffects.GLOWING, time, 1);
			this.attackDamage(entity, dame, false);

			if (hasGlow) {
				this.addAttack(entity, dame, addAttack);
			}
		}
	}

	protected void spawnParticleShort(ServerLevel sever, BlockPos pos) {
		float x = (float) pos.getX() + this.getRandFloat(0.25F);
		float y = (float) pos.getY() + this.getRandFloat(0.25F);
		float z = (float) pos.getZ() + this.getRandFloat(0.25F);

		for (int i = 0; i < 3; i++) {
			sever.sendParticles(ParticleInit.MAGICLIGHT, x, y, z, 4, 0F, 0F, 0F, 0.15F);
		}
	}

	public int getMinParticleTick() {
		return 3;
	}

	// パーティクルスポーン
	protected void spawnParticle() {

		Random rand = this.rand;
		Vec3 vec = this.getDeltaMovement();
		float addX = (float) (-vec.x / 20F);
		float addY = (float) (-vec.y / 20F);
		float addZ = (float) (-vec.z / 20F);

		for (int i = 0; i < 4; i++) {

			float x = addX + this.getRandFloat(0.075F);
			float y = addY + this.getRandFloat(0.075F);
			float z = addZ + this.getRandFloat(0.075F);
			float f1 = (float) (this.getX() - 0.5F + rand.nextFloat() + vec.x * i / 4F);
			float f2 = (float) (this.getY() - 0.5F + rand.nextFloat() + vec.y * i / 4F);
			float f3 = (float) (this.getZ() - 0.5F + rand.nextFloat() + vec.z * i / 4F);

			this.addParticle(ParticleInit.MAGICLIGHT, f1, f2, f3, x, y, z);
		}
	}

	// ダメージレートの取得
	public float getDamageRate() {
		switch (this.getData()) {
		case 0: return 1F;
		case 1: return 1.5F;
		case 3: return 3.5F;
		default: return 2F;
		}
	}

	// 属性の取得
	public SMElement getElement() {
		return SMElement.SHINE;
	}
}
