package sweetmagic.api.ientity;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import sweetmagic.config.SMConfig;
import sweetmagic.init.DimentionInit;
import sweetmagic.init.PotionInit;
import sweetmagic.init.TagInit;
import sweetmagic.init.entity.animal.AbstractSummonMob;
import sweetmagic.init.entity.projectile.AbstractMagicShot;
import sweetmagic.init.entity.projectile.EvilArrow;
import sweetmagic.util.PlayerHelper;
import sweetmagic.util.SMDamage;
import sweetmagic.util.WorldHelper;

public interface ISMMob {

	public static final EntityDataSerializer<Boolean> BOOLEAN = EntityDataSerializers.BOOLEAN;
	public static final EntityDataSerializer<Integer> INT = EntityDataSerializers.INT;
	public static final EntityDataSerializer<Float> FLOAT = EntityDataSerializers.FLOAT;
	public static final BossEvent.BossBarColor BC_BLUE = BossEvent.BossBarColor.BLUE;
	public static final BossEvent.BossBarColor BC_RED = BossEvent.BossBarColor.RED;
	public static final BossEvent.BossBarColor BC_GREEN = BossEvent.BossBarColor.GREEN;
	public static final BossEvent.BossBarOverlay NOTCHED_6 = BossEvent.BossBarOverlay.NOTCHED_6;

	// 魔法攻撃なら2倍
	default float getDamageAmount(Level world, DamageSource src, float dame, float rate) {

		if (src.getDirectEntity() instanceof Warden entity) {
			entity.hurt(SMDamage.MAGIC, dame);
			entity.invulnerableTime = 0;
			dame *= 0.1F * rate;
		}

		// 低ランク以外ならダメージ倍化なし
		if (!this.isLowRank()) {
			return dame;
		}

		// スイートマジックディメンションなら
		if (this.isSMDimension(world)) {
			return this.isSMDamage(src) ? dame * 0.875F * rate : dame * 0.25F * rate;
		}

		return this.isSMDamage(src) ? dame * 2F : dame;
	}

	// スイートマジックディメンションかどうか
	default boolean isSMDimension(Level world) {
		return world.dimension() == DimentionInit.SweetMagicWorld;
	}

	// ボスダメージ計算
	default float getBossDamageAmount(Level world, int defTime, DamageSource src, float amount, float cap) {

		Entity attacker = src.getDirectEntity();

		// デバフダメージなら
		if (src instanceof SMDamage smDame && smDame.isDebuffFlag()) {
			amount *= 0.25F;
		}

		else if (attacker instanceof Warden entity) {
			entity.hurt(SMDamage.MAGIC, amount);
			entity.invulnerableTime = 0;
			amount = 0F;
		}

		if (attacker instanceof AbstractSummonMob || src.getEntity() instanceof AbstractSummonMob) {
			amount = Math.min(cap * 0.5F, amount) * 0.5F;
		}

		else if(attacker instanceof EvilArrow) {
			return amount;
		}

		else if(attacker instanceof AbstractMagicShot magic && magic.getCritical()) {
			cap *= magic.getCriticalDamage();
		}

		// 火力キャップ
		float defTimeDamage = src == SMDamage.addDamage ? cap / 5F : 0F;
		return Math.min(defTime <= 0 ? cap : defTimeDamage, amount);
	}

	// 魔法によるダメージか？
	default boolean isSMDamage(DamageSource src) {
		Entity entity = src.getDirectEntity();
		return src instanceof SMDamage || entity instanceof AbstractMagicShot;
	}

	// 魔法ダメージ以外か
	default boolean notMagicDamage(Entity attacker, Entity attackEntity) {
		return attacker != null && attackEntity != null && !(attackEntity instanceof AbstractMagicShot || attackEntity instanceof AbstractSummonMob);
	}

	// ボスのダメージチェック
	default boolean checkBossDamage(DamageSource src) {
		return !this.isSMDamage(src);
	}

	default <T extends Entity> List<T> getEntityList(Class<T> enClass, Entity entity, double range) {
		return WorldHelper.getEntityList(entity, enClass, this.getAABB(entity, range));
	}

	default <T extends Entity> List<T> getEntityList(Class<T> enClass, Entity entity, Predicate<T> filter, double range) {
		return WorldHelper.getEntityList(entity, enClass, filter, this.getAABB(entity, range));
	}

	default <T extends Entity> List<T> getEntityList(Class<T> enClass, Entity entity, Predicate<T> filter, BlockPos pos, double range) {
		return WorldHelper.getEntityList(entity, enClass, filter, this.getAABB(pos, range));
	}

	// 範囲の取得
	default AABB getAABB(Entity entity, double range) {
		return entity.getBoundingBox().inflate(range, range / 2, range);
	}

	// 範囲の取得
	default AABB getAABB(Entity entity, double x, double y, double z) {
		return entity.getBoundingBox().inflate(x, y, z);
	}

	// 範囲の取得
	default AABB getAABB(BlockPos pos, double range) {
		return new AABB(pos.getX() - range, pos.getY() - range, pos.getZ() - range, pos.getX() + range, pos.getY() + range, pos.getZ() + range);
	}

	SynchedEntityData getData();

	default <T> T get(EntityDataAccessor<T> value) {
		return this.getData().get(value);
	}

	default <T> void set(EntityDataAccessor<T> value, T par) {
		this.getData().set(value, par);
	}

	default <T> void define(EntityDataAccessor<T> value, T par) {
		this.getData().define(value, par);
	}

	public static <T> EntityDataAccessor<T> setData(Class<? extends Mob> entity, EntityDataSerializer<T> seria) {
		return SynchedEntityData.defineId(entity, seria);
	}

	// えんちちーソート
	default int sortEntity(Entity mob, Entity entity1, Entity entity2) {
		if (entity1 == null || entity2 == null) { return 0; }

		double distance1 = mob.distanceToSqr(entity1);
		double distance2 = mob.distanceToSqr(entity2);

		if (distance1 > distance2) { return 1; }

		if (distance1 < distance2) { return -1; }

		return 0;
	}

	default void playSound(Level world, LivingEntity entity, SoundEvent sound, float vol, float pitch) {
		world.playSound(null, entity.blockPosition(), sound, SoundSource.HOSTILE, vol, pitch);
	}

	default boolean isTarget() {
		return false;
	}

	default boolean isHard(Level world) {
		return world.getDifficulty().equals(Difficulty.HARD);
	}

	// 体力が半分以下かどうか
	default boolean isHalfHealth(LivingEntity entity) {
		return entity.getHealth() <= (entity.getMaxHealth() * 0.5D);
	}

	// リーダーフラグを所持しているか
	default boolean isLeader(LivingEntity entity) {
		return entity.hasEffect(PotionInit.leader_flag);
	}

	default int getPotionLevel(LivingEntity entity, MobEffect potion) {
		return entity.hasEffect(potion) ? 1 : 0;
	}

	public static boolean isOverworld(ServerLevelAccessor world, BlockPos pos) {
		return world.getBiome(pos).is(BiomeTags.IS_OVERWORLD);
	}

	public static boolean isDarkEnoughToSpawn(ServerLevelAccessor world, BlockPos pos, RandomSource rand) {

		if (!SMConfig.spawnSMMob.get()) {
			if (world.getBrightness(LightLayer.SKY, pos) > rand.nextInt(32) || (!ISMMob.isOverworld(world, pos) || !ISMMob.isSkyView(world, pos) ) && !world.getBiome(pos).is(TagInit.IS_SWEETMAGIC)) { return false; }
		}

		DimensionType dim = world.dimensionType();
		int i = dim.monsterSpawnBlockLightLimit();
		if (i < 15 && world.getBrightness(LightLayer.BLOCK, pos) > i) { return false; }

		int j = world.getLevel().isThundering() ? world.getMaxLocalRawBrightness(pos, 10) : world.getMaxLocalRawBrightness(pos);
		return j <= dim.monsterSpawnLightTest().sample(rand);
	}

	public static boolean isDarkEnoughToSpawnSM(ServerLevelAccessor world, BlockPos pos, RandomSource rand) {
		if (world.getBrightness(LightLayer.SKY, pos) > rand.nextInt(32) || !world.getBiome(pos).is(TagInit.IS_SWEETMAGIC)) { return false; }

		DimensionType dim = world.dimensionType();
		int i = dim.monsterSpawnBlockLightLimit();
		if (i < 15 && world.getBrightness(LightLayer.BLOCK, pos) > i) { return false; }

		int j = world.getLevel().isThundering() ? world.getMaxLocalRawBrightness(pos, 10) : world.getMaxLocalRawBrightness(pos);
		return j <= dim.monsterSpawnLightTest().sample(rand);
	}

	default int getDate(Level world) {
		return (int) (world.dayTime() / 24000);
	}

	// 日付経過のレート
	default float getDateRate(Level world, float minRate) {

		// 難易度の取得
		float rate = 1F;
		Difficulty dif = world.getDifficulty();

		// ハードｍたはスイマジディメンションなら減衰なし
		if (dif.equals(Difficulty.HARD) || this.isSMDimension(world)) {
			return 1F;
		}

		// イージーならスポーン可能日数の4倍まで減衰あり
		if (dif.equals(Difficulty.EASY)) {
			rate = 4F;
		}

		// ノーマルならスポーン可能日数の2倍まで減衰あり
		else if (dif.equals(Difficulty.NORMAL)) {
			rate = 2F;
		}

		// スポーン可能な日数から何日立っているか
		int date = Math.max(0, ( this.getDate(world) - this.getMaxSpawnDate() ));
		return minRate + Math.min(1F, date / ((float) this.getMaxSpawnDate() * rate)) * (1 - minRate);
	}

	// 最大体力の設定
	default void initMobData(LivingEntity entity, DifficultyInstance dif) {

		if (entity.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
			LocalDate date = LocalDate.now();
			int d = date.get(ChronoField.DAY_OF_MONTH);
			int m = date.get(ChronoField.MONTH_OF_YEAR);
			RandomSource rand = entity.getRandom();

			if (m == 10 && d == 31 && rand.nextFloat() < 0.25F) {
				entity.setItemSlot(EquipmentSlot.HEAD, new ItemStack(rand.nextFloat() < 0.1F ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN));
			}
		}

		// 低ランク以外なら終了
		if (!this.isLowRank()) { return; }

		switch (dif.getDifficulty()) {
		case EASY:
			entity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(entity.getMaxHealth() * 0.75F);
			break;
		case NORMAL:
			entity.getAttribute(Attributes.MAX_HEALTH).setBaseValue(entity.getMaxHealth() * 0.9F);
			break;
		}
		entity.setHealth(entity.getMaxHealth());
	}

	// 低ランクかどうか
	default boolean isLowRank() {
		return true;
	}

	// スポーンまでの日数
	default int getMaxSpawnDate() {
		return SMConfig.spawnDate.get();
	}

	default Vector3f getShotVector(Mob entity, Vec3 vec, float shake) {

		Vec3 vec1 = vec.normalize();
		Vec3 vec2 = vec1.cross(new Vec3(0D, 1D, 0D));
		if (vec2.lengthSqr() <= 1.0E-7D) {
			vec2 = vec1.cross(entity.getUpVector(1F));
		}

		Quaternion qua1 = new Quaternion(new Vector3f(vec2), 90F, true);
		Vector3f vecf1 = new Vector3f(vec1);
		vecf1.transform(qua1);
		Quaternion qua2 = new Quaternion(vecf1, shake, true);
		Vector3f vecf2 = new Vector3f(vec1);
		vecf2.transform(qua2);
		return vecf2;
	}

	// 経過日数を満たしていないなら
	public static boolean isDayElapse(ServerLevelAccessor world, int day) {
		if (SMConfig.spawnSMMob.get()) { return true; }
		switch (world.getDifficulty()) {
		case EASY:		return world.dayTime() > (day * 36000);
		case NORMAL:	return world.dayTime() > (day * 24000);
		default:		return true;
		}
	}

	// 経過日数を満たしていないなら
	public static boolean isSkyView(ServerLevelAccessor world, BlockPos pos) {
		return world.canSeeSky(pos) || ( SMConfig.spawnCave.get() && pos.getY() <= 30 && Math.min(1F, ( (float) ( 30F - pos.getY() ) * 0.0333F)) > world.getRandom().nextFloat() );
	}

	// スポーン条件のチェック
	public static boolean checkMonsterSpawnRules(EntityType<? extends Mob> enType, ServerLevelAccessor world, MobSpawnType spType, BlockPos pos, RandomSource rand) {
		return !WorldHelper.isPeace(world) && ISMMob.isDarkEnoughToSpawn(world, pos, rand) &&
				Mob.checkMobSpawnRules(enType, world, spType, pos, rand) && ISMMob.isDayElapse(world, SMConfig.spawnDate.get());
	}

	// スポーン条件のチェック
	public static boolean checkMonsterSpawnRulesSM(EntityType<? extends Mob> enType, ServerLevelAccessor world, MobSpawnType spType, BlockPos pos, RandomSource rand) {
		return !WorldHelper.isPeace(world) && ISMMob.isDarkEnoughToSpawnSM(world, pos, rand) && Mob.checkMobSpawnRules(enType, world, spType, pos, rand);
	}

	default void addPotion(LivingEntity entity, MobEffect potion, int time, int level) {
		PlayerHelper.setPotion(entity, potion, level, time);
	}

	default ItemStack getStack() {
		return ItemStack.EMPTY;
	}

	void refreshInfo();

	public class SMMoveControl extends MoveControl {

		private final Mob mob;

		public SMMoveControl(Mob mob) {
			super(mob);
			this.mob = mob;
		}

		public void tick() {
			if (this.operation != MoveControl.Operation.MOVE_TO) { return; }

			Vec3 vec = new Vec3(this.wantedX - this.mob.getX(), this.wantedY - this.mob.getY(), this.wantedZ - this.mob.getZ());
			double d0 = vec.length();

			if (d0 < this.mob.getBoundingBox().getSize() || ( this.mob instanceof AbstractSummonMob su && su.getShit() ) ) {
				this.operation = MoveControl.Operation.WAIT;
				this.mob.setDeltaMovement(this.mob.getDeltaMovement().scale(0.5D));
			}

			else {

				this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(vec.scale(this.speedModifier * 0.05D / d0)));

				if (this.mob.getTarget() == null) {
					Vec3 vec1 = this.mob.getDeltaMovement();
					this.mob.setYRot(-((float) Mth.atan2(vec1.x, vec1.z)) * (180F / (float) Math.PI));
					this.mob.yBodyRot = this.mob.getYRot();
				}

				else {
					double d2 = this.mob.getTarget().getX() - this.mob.getX();
					double d1 = this.mob.getTarget().getZ() - this.mob.getZ();
					this.mob.setYRot(-((float) Mth.atan2(d2, d1)) * (180F / (float) Math.PI));
					this.mob.yBodyRot = this.mob.getYRot();
				}
			}
		}
	}

	public class RandomMoveGoal extends Goal {

		private final Mob mob;

		public RandomMoveGoal(Mob mob) {
			this.setFlags(EnumSet.of(Goal.Flag.MOVE));
			this.mob = mob;
		}

		public boolean canUse() {
			return !this.mob.getMoveControl().hasWanted() && this.mob.getLevel().getRandom().nextInt(reducedTickDelay(1)) == 0;
		}

		public boolean canContinueToUse() {
			return false;
		}

		public void tick() {

			BlockPos pos = this.mob.blockPosition();
			RandomSource rand = this.mob.getLevel().getRandom();

			for (int i = 0; i < 3; ++i) {

				BlockPos pos1 = pos.offset(rand.nextInt(15) - 7, rand.nextInt(11) - 5, rand.nextInt(15) - 7);
				if (!this.mob.getLevel().isEmptyBlock(pos1)) { continue; }

				this.mob.getMoveControl().setWantedPosition((double) pos1.getX() + 0.5D, (double) pos1.getY() + 0.5D, (double) pos1.getZ() + 0.5D, 0.25D);

				if (this.mob.getTarget() == null) {
					this.mob.getLookControl().setLookAt((double) pos1.getX() + 0.5D, (double) pos1.getY() + 0.5D, (double) pos1.getZ() + 0.5D, 180F, 20F);
				}
				break;
			}
		}
	}

	default Iterable<BlockPos> getPosList(BlockPos pos, double range) {
		return WorldHelper.getRangePos(pos, range);
	}

	default Iterable<BlockPos> getPosRangeList(BlockPos pos, double range) {
		return WorldHelper.getRangePos(pos, -range, 0, -range, range, 0, range);
	}

	default boolean isPlayer(Entity entity) {
		return entity instanceof Player || entity instanceof AbstractSummonMob;
	}

	default Predicate<LivingEntity> getTargetEntity() {
		return e -> e instanceof Player || e instanceof AbstractSummonMob;
	}
}
