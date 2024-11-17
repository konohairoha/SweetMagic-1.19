package sweetmagic.init.entity.monster.boss;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import sweetmagic.api.ientity.ISMMob;
import sweetmagic.init.EntityInit;
import sweetmagic.init.SoundInit;
import sweetmagic.init.entity.animal.AbstractSummonMob;
import sweetmagic.init.entity.projectile.EvilArrow;
import sweetmagic.util.SMDamage;

public class HolyAngel extends AbstractSMBoss {

	private int holyLightTime = 0;						// ホーリーライト攻撃時間
	private int oruChargeTime = 0;						// オーバーレイユニット

	private static final int HOLYLIGHT_MAXTIME = 500;	// ホーリーライトの最大攻撃時間
	private static final int ORUCHARGE_MAXTIME = 300;	// オーバーレイユニット最大チャージ時間

	private Map<Integer, BlockPos> posMap = new LinkedHashMap<>();	// セイクリッドレイン
	private List<Player> playerList = new ArrayList<>();

	private static final EntityDataAccessor<Integer> ORU = ISMMob.setData(HolyAngel.class, INT);

	public HolyAngel(Level world) {
		super(EntityInit.holyAngel, world);
	}

	public HolyAngel(EntityType<HolyAngel> enType, Level world) {
		super(enType, world);
		this.xpReward = 300;
		this.maxUpStep = 1.25F;
	}

	public static AttributeSupplier.Builder registerAttributes() {
		return Monster.createMonsterAttributes()
				.add(Attributes.MAX_HEALTH, 300D)
				.add(Attributes.MOVEMENT_SPEED, 0.3D)
				.add(Attributes.ATTACK_DAMAGE, 4D)
				.add(Attributes.ARMOR, 6D)
				.add(Attributes.FOLLOW_RANGE, 64D)
				.add(Attributes.KNOCKBACK_RESISTANCE, 64D);
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(ORU, 0);
	}

	protected SoundEvent getAmbientSound() {
		return SoundInit.PAGE;
	}

	protected SoundEvent getHurtSound(DamageSource src) {
		return SoundInit.PAGE;
	}

	protected SoundEvent getDeathSound() {
		return SoundInit.PAGE;
	}

	public float getSoundVolume() {
		return 0.25F;
	}

	// ダメージ処理
	public boolean hurt(DamageSource src, float amount) {

		Entity attacker = src.getEntity();
		Entity attackEntity = src.getDirectEntity();
		if ( attacker != null && attacker instanceof ISMMob) { return false; }

		// ボスダメージ計算
		amount = this.getBossDamageAmount(this.level, this.defTime , src, amount, 10F);
		this.defTime = amount > 0 ? 2 : this.defTime;

		// 魔法攻撃以外なら反撃&ダメージ無効
		if (this.notMagicDamage(attacker, attackEntity)) {
			this.attackDamage(attacker, SMDamage.magicDamage, amount);
			return false;
		}

		// 魔導士の召喚台座による召喚の場合
		if (this.isLectern()) {
			amount = this.getLecternAction(src, amount, this.getORU());
		}

		return super.hurt(src, amount);
	}

	// 召喚台座時のダメージ処理
	public float getLecternAction (DamageSource src, float amount, int armorSize) {

		if (armorSize > 0) {

			// 危険な果実の場合
			if (src.getDirectEntity() instanceof EvilArrow) {
				amount = this.getEvilDamage(amount, armorSize);
			}

			// 危険な果実以外のダメージなら
			else {
				amount *= 0.1F;
				this.specialDamageCut(src, this.playerList, "queen_damecut");
			}
		}

		// バリアが張られていない場合、体力半分時確率テレポート
		else {
			amount = Math.min(5F, amount);
			this.halfHealthTeleport();
		}

		return amount;
	}

	// 危険な果実でのダメージ計算
	public float getEvilDamage (float amount, int armorSize) {

		// ORUを1つ減らす
		this.setORU(armorSize - 1);

		// プレイヤーの数分ダメージ軽減
		amount = 10F / this.getEntityList(Player.class, 80F).size();
		this.playSound(SoundEvents.GLASS_BREAK, 3F, 1.1F);

		// ORUが0になったら
		if (this.getORU() <= 0) {

			// 最大体力の20%をダメージに
			amount = this.getMaxHealth() * 0.2F;
			this.playSound(SoundEvents.LIGHTNING_BOLT_IMPACT, 1F, 1F);
			BlockPos pos = this.blockPosition();

			if (this.level instanceof ServerLevel sever) {
				for (int i = 0; i < 16; i++) {
					float x = (float) (pos.getX() + this.rand.nextFloat());
					float y = (float) (pos.getY() + this.rand.nextFloat() + 0.5F);
					float z = (float) (pos.getZ() + this.rand.nextFloat());
					sever.sendParticles(ParticleTypes.LAVA, x, y, z, 4, 0F, 0F, 0F, 0.15F);
				}
			}
		}

		return amount;
	}

	public void addAdditionalSaveData(CompoundTag tags) {
		super.addAdditionalSaveData(tags);
		tags.putInt("oru", this.getORU());
		tags.putInt("oruChargeTime", this.oruChargeTime);
		tags.putInt("holyLightTime", this.holyLightTime);
	}

	public void readAdditionalSaveData(CompoundTag tags) {
		super.readAdditionalSaveData(tags);
		this.setORU(tags.getInt("oru"));
		this.oruChargeTime = tags.getInt("oruChargeTime");
		this.holyLightTime = tags.getInt("holyLightTime");
	}

	public int getORU () {
		return this.entityData.get(ORU);
	}

	public void setORU (int size) {
		this.entityData.set(ORU, size);
	}

	// アーマーがないなら
	public boolean isArmorEmpty () {
		return this.getORU() <= 0;
	}

	public void tick() {
		super.tick();

		if (this.level.isClientSide) {

			Vec3 vec = this.getDeltaMovement();
			float x = (float) (this.getX() - 0.5F + this.rand.nextFloat());
			float y = (float) (this.getY() + 1F);
			float z = (float) (this.getZ() - 0.5F + this.rand.nextFloat());

			float f1 = (float) ( (vec.x + 0.5F - this.rand.nextFloat() ) * 0.2F);
			float f2 = 0F;
			float f3 = (float) ( (vec.z + 0.5F - this.rand.nextFloat() ) * 0.2F);
			this.level.addParticle(ParticleTypes.END_ROD, x, y, z, f1, f2, f3);
		}

		BlockPos spawnPos = this.getSpawnPos();
		if (this.tickCount % 20 == 0 && spawnPos != null) {

			double distance = this.distanceToSqr(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());

			if (distance >= 2000) {
				this.teleport();
			}
		}
	}

	protected boolean teleport() {
		BlockPos spawnPos = this.getSpawnPos();
		if (!this.level.isClientSide() && this.isAlive() && spawnPos != null) {
			double d0 = spawnPos.getX() + (this.rand.nextDouble() - 0.5D) * 12D;
			double d1 = spawnPos.getY() + 0.5D;
			double d2 = spawnPos.getZ() + (this.rand.nextDouble() - 0.5D) * 12D;
			return this.teleport(d0, d1, d2);
		}
		return false;
	}

	protected void customServerAiStep() {

		super.customServerAiStep();
		LivingEntity target = this.getTarget();
		if (target == null) { return; }

		// オーバーレイユニットが0の場合
		if (this.getORU() <= 0) {

			this.oruChargeTime++;
			this.posMap.clear();

			// チャージが溜まったらオーバーレイユニットを追加
			if (this.oruChargeTime >= ORUCHARGE_MAXTIME) {
				this.clearInfo();
			}

			return;
		}

		this.getLookControl().setLookAt(target, 10F, 10F);

		// 1stフェーズの攻撃
		if (!this.isHalfHealth(this)) {
			this.firstPhaseSttack(target);
		}

		// 2ndフェーズの攻撃
		else {
			this.secondPhaseSttack(target);
		}
	}

	// 1stフェーズの攻撃
	public void firstPhaseSttack (LivingEntity target) {

		this.holyLightTime++;
		double range = 16D;

		if (this.holyLightTime >= HOLYLIGHT_MAXTIME - 120) {

			if (this.holyLightTime % 30 == 0) {
				BlockPos pos = this.blockPosition();
				this.spawnParticleCycle(pos, range + 1.15D);
				this.spawnParticleCycle(pos, range - 3.85D);
				this.spawnParticleCycle(pos, range - 7.85D);
			}
		}

		// ホーリーライトの攻撃チャージが終わったら
		if (this.holyLightTime >= HOLYLIGHT_MAXTIME) {

			// 範囲にいるえんちちーを取得
			boolean isPlayer = this.isPlayer(target);
			BlockPos pos = this.blockPosition();
			List<LivingEntity> entityList = this.getEntityList(LivingEntity.class, this.getFilter(isPlayer, pos, range * range), range);
			float damage = (this.isHard() ? 10F : 6F) + this.getORU() * 2F + this.getBuffPower();
			this.attackDamage(entityList, SMDamage.magicDamage, damage);

			this.holyLightTime = 0;
			this.playSound(SoundEvents.DRAGON_FIREBALL_EXPLODE, 2F, 1F);

			if ( !(this.level instanceof ServerLevel sever) ) { return; }

			// 範囲の座標取得
			Iterable<BlockPos> posList = this.getPosList(pos, range);

			for (BlockPos p : posList) {

				if (this.rand.nextFloat() >= 0.2F || !this.checkDistances(pos, p, range * range)) { continue; }

				float x = (float) (p.getX() + this.rand.nextFloat() - 0.5F);
				float y = (float) (p.getY() + this.rand.nextFloat() - 0.5F);
				float z = (float) (p.getZ() + this.rand.nextFloat() - 0.5F);
				sever.sendParticles(ParticleTypes.END_ROD, x, y, z, 0, 0F, this.rand.nextFloat() * 0.5F, 0F, 1F);
			}
		}
	}

	// 2ndフェーズの攻撃
	public void secondPhaseSttack (LivingEntity target) {

		this.holyLightTime++;
		double range = 12D;
		boolean isPlayer = this.isPlayer(target);

		// ホーリーライトの座標設定
		if (this.posMap.isEmpty()) {
			this.setPosMap(isPlayer, range);
		}

		if (this.holyLightTime >= HOLYLIGHT_MAXTIME - 120 && this.holyLightTime % 30 == 0) {
			for (BlockPos pos : this.posMap.values()) {
				this.spawnParticleCycle(pos, range);
			}
		}

		// ホーリーライトの攻撃チャージが終わったら攻撃
		if (this.holyLightTime >= HOLYLIGHT_MAXTIME && this.tickCount % 6 == 0) {
			this.holyLightAttack(isPlayer, range);
		}
	}

	// ホーリーライトの座標設定
	public void setPosMap (boolean isPlayer, double range) {

		// 範囲にいるえんちちーを取得
		List<LivingEntity> entityList = this.getEntityList(LivingEntity.class, this.getFilter(isPlayer), range);

		// ターゲット分範囲を拡張
		int mapRange = 20 + (entityList.size() - 1) * 4;
		int size = this.getORU() * 3;
		BlockPos basePos = this.blockPosition();

		for (int i = 0; i < size; i++) {

			// 被っていない座標をposMapに登録
			while (true) {
				BlockPos pos = new BlockPos(basePos.getX() + this.randRange(mapRange), basePos.getY(), basePos.getZ() + this.randRange(mapRange));

				if (!this.posMap.containsValue(pos)) {
					this.posMap.put(i, pos);
					break;
				}
			}
		}
	}

	// ホーリーライトの攻撃
	public void holyLightAttack (boolean isPlayer, double range) {

		float damage = (this.isHard() ? 20F : 12F) + this.getBuffPower();

		for (Entry<Integer, BlockPos> map : this.posMap.entrySet()) {

			// 範囲にいるえんちちーを取得
			BlockPos pos = map.getValue();
			AABB aabb = new AABB(pos.offset(-range, -range, -range), pos.offset(range, range, range));
			List<LivingEntity> entityList = this.getEntityList(LivingEntity.class, this.getFilter(isPlayer, pos, range * range), aabb);
			this.attackDamage(entityList, SMDamage.magicDamage, damage);

			// 攻撃した座標を除去
			this.posMap.remove(map.getKey());

			// 座標マップが空になったら攻撃時間を0に
			if (this.posMap.isEmpty()) {
				this.holyLightTime = 0;
			}

			if (this.level instanceof ServerLevel sever) {

				// 範囲の座標取得
				Iterable<BlockPos> posList = this.getPosList(pos, range);

				for (BlockPos p : posList) {

					if (this.rand.nextFloat() >= 0.15F || !this.checkDistances(pos, p, range * range)) { continue; }

					float x = (float) (p.getX() + this.rand.nextFloat() - 0.5F);
					float y = (float) (p.getY() + this.rand.nextFloat() - 0.5F);
					float z = (float) (p.getZ() + this.rand.nextFloat() - 0.5F);
					sever.sendParticles(ParticleTypes.END_ROD, x, y, z, 0, 0F, this.rand.nextFloat() * 0.75F, 0F, 1F);
				}
			}

			this.level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.GENERIC_EXPLODE, this.getSoundSource(), 2F, 1F);
			return;
		}
	}

	public void setORU () {
		int rate = this.isHard() ? 8 : 4;
		List<Player> playerList = this.getEntityList(Player.class, 80D);
		this.setORU(rate * playerList.size());
	}

	public void clearInfo() {
		this.setORU();
		this.oruChargeTime = 0;
		this.holyLightTime = 0;
	}

	public Predicate<LivingEntity> getFilter (boolean isPlayer, BlockPos pos, double range) {
		return e -> !e.isSpectator() && e.isAlive() && (isPlayer ? (e instanceof Player || e instanceof AbstractSummonMob) : !(e instanceof Player) ) && !(e instanceof ISMMob) && this.checkDistances(pos, e.blockPosition(), range * range);
	}
}
