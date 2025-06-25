package sweetmagic.init.entity.projectile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import sweetmagic.api.emagic.SMElement;
import sweetmagic.api.iitem.IMagicItem;
import sweetmagic.api.iitem.IWand;
import sweetmagic.api.iitem.info.MagicInfo;
import sweetmagic.api.iitem.info.WandInfo;
import sweetmagic.init.EntityInit;
import sweetmagic.init.ItemInit;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.PotionInit;
import sweetmagic.util.RenderUtil.RenderColor;
import sweetmagic.util.SMUtil;

public class MagicSquareMagic extends AbstractMagicShot {

	private RenderColor color = null;
	private List<LivingEntity> mobList = new ArrayList<>();
	private List<LivingEntity> enemyList = new ArrayList<>();
	private List<Item> rainMagicList = Arrays.<Item> asList(
		ItemInit.magic_rainfield, ItemInit.magic_rainfield2, ItemInit.magic_rainfield3, ItemInit.magic_rainfield4
	);

	public MagicSquareMagic(EntityType<? extends MagicSquareMagic> entityType, Level world) {
		super(entityType, world);
	}

	public MagicSquareMagic(double x, double y, double z, Level world) {
		this(EntityInit.magicSquare, world);
		this.setPos(x, y, z);
	}

	public MagicSquareMagic(Level world, LivingEntity entity, WandInfo wandInfo) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.setWandInfo(wandInfo);
	}

	public void tick() {
		super.tick();
		this.setDeltaMovement(new Vec3(0, 0, 0));	// 移動の無効化
		this.tickSpawnParticle();					// 常時スポーンパーティクル
		this.tickEffect();							// 常時発動効果
	}

	// 時間経過
	protected void tickDespawn() {

		// 最大生存時間を超えたら効果終了時エフェクトを発生
		if (this.getLifeTime() >= this.getMaxLifeTime()) {
			this.actionEffect(true);
		}

		super.tickDespawn();
	}

	// 常時スポーンパーティクル
	public void tickSpawnParticle() {
		if (!(this.getLevel() instanceof ServerLevel server)) { return; }

		ParticleOptions par = null;
		int data = this.getData();

		switch (data) {
		case 0:
			par = ParticleInit.GRAVITY_FIELD;
			break;
		case 1:
			par = ParticleInit.WIND_FIELD;
			break;
		case 2:
			par = ParticleInit.RAIN_FIELD;
			break;
		case 3:
			par = ParticleInit.DIVINE;
			break;
		case 4:
			par = ParticleInit.REFLASH;
			break;
		}

		this.spawnParticle(server, par, data, (int) this.getRange());
	}

	// パーティクルスポーン
	public void spawnParticle(ServerLevel server, ParticleOptions par, int data, int scale) {

		double range = scale * scale / 3D;
		double posY = data == 2 ? this.getY() + 4D : this.getY();
		float addY = 0F;

		for (int addX = -scale; addX <= scale; addX++) {
			for (int addZ = -scale; addZ <= scale; addZ++) {

				// 乱数による出現調整
				if (this.rand.nextFloat() >= 0.0125F) { continue; }

				// 魔法陣外はパーティクルを出さない
				int posX = (int) (this.getX() + addX);
				int posZ = (int) (this.getZ() + addZ);
				if (this.distanceToSqr(posX, posY, posZ) > range) { continue; }

				if (data == 2) {
					addY = -0.45F - this.rand.nextFloat() * 0.3F;
				}

				float x = posX - 0.5F + this.rand.nextFloat() * 0.5F;
				float y = (float) (posY + 0.25F);
				float z = posZ - 0.5F + this.rand.nextFloat() * 0.5F;
				float ax = this.getRandFloat(this.rand) * 0.075F;
				float ay = this.rand.nextFloat() * 0.1F + 0.05F + addY;
				float az = this.getRandFloat(this.rand) * 0.075F;

				server.sendParticles(par, x, y, z, 0, ax, ay, az, 1F);
			}
		}
	}

	// 常時発動効果
	public void tickEffect() {
		if (this.tickCount % 10 != 0) { return; }
		this.actionEffect(false);
	}

	public void actionEffect(boolean isFinish) {

		// えんちちーリストの取得
		double range = (this.getRange() / 2D) * 1.1D;
		List<LivingEntity> entityList = this.getEntityList(LivingEntity.class, e -> e.isAlive() && this.distanceTo(e) <= range, range);
		if (entityList.isEmpty()) { return; }

		int data = this.getData();
		int tier = this.getTier();

		for (LivingEntity entity : entityList) {

			boolean isEnmy = entity instanceof Enemy;

			switch(data) {
			case 0:
				// グラヴィティフィールド
				this.gravityField(entity, isEnmy, isFinish, data, tier);
				break;
			case 1:
				// ウィンドフィールド
				this.windField(entity, isEnmy, isFinish, data, tier);
				break;
			case 2:
				// レインフィールド
				this.rainField(entity, isEnmy, isFinish, data, tier);
				break;
			case 3:
				// フューチャーヴィジョンフィールド
				this.futureVisionField(entity, isEnmy, isFinish, data, tier);
				break;
			case 4:
				// リフレッシフィールド
				this.reflashField(entity, isEnmy, isFinish, data, tier);
				break;
			}
		}
	}

	// グラヴィティフィールド
	public void gravityField(LivingEntity entity, boolean isEnemy, boolean isFinish, int data, int tier) {

		if (isFinish && isEnemy) {
			int addLevel = this.getHourGlass() ? 1 : 0;
			this.addPotion(entity, PotionInit.gravity, 1200 + tier * 600, addLevel);
			float dame = 3F + this.getWandLevel();
			this.attackDamage(entity, dame, false);
			return;
		}

		if (this.tickCount % 20 != 0) { return; }
		int addLevel = this.getHourGlass() ? 1 : 0;

		// 敵モブなら
		if (isEnemy && !this.enemyList.contains(entity)) {

			this.enemyList.add(entity);
			this.addPotion(entity, PotionInit.gravity, 1200 + tier * 600, tier - 1 + addLevel);

			if (tier >= 2) {
				this.addPotion(entity, MobEffects.MOVEMENT_SLOWDOWN, 1200 + tier * 600, tier - 1 + addLevel);
			}

			if (tier >= 3) {
				this.addPotion(entity, PotionInit.debuff_extension, 1200 + tier * 600, tier - 1 + addLevel);
			}
		}

		// 味方モブなら
		if (!isEnemy && !this.mobList.contains(entity)) {
			this.addPotion(entity, PotionInit.damage_cut, 1200 + tier * 600, tier - 1 + addLevel);
			this.mobList.add(entity);
		}
	}

	// ウィンドフィールド
	public void windField(LivingEntity entity, boolean isEnemy, boolean isFinish, int data, int tier) {

		if (isFinish && isEnemy) {
			int addLevel = this.getHourGlass() ? 1 : 0;
			this.addPotion(entity, PotionInit.bleeding, 1200 + tier * 600, addLevel);
			float dame = 3F + this.getWandLevel();
			this.attackDamage(entity, dame, false);
			return;
		}

		if (this.tickCount % 20 != 0) { return; }

		if (!isEnemy && !this.mobList.contains(entity)) {
			int addLevel = this.getHourGlass() ? 1 : 0;
			this.addPotion(entity, PotionInit.attack_disable, 221, tier - 1 + addLevel);
			this.mobList.add(entity);
		}

		if (!isEnemy) {
			entity.heal(1F);
		}
	}

	// レインフィールド
	public void rainField(LivingEntity entity, boolean isEnemy, boolean isFinish, int data, int tier) {

		if (isFinish && entity instanceof Player player) {
			Predicate<ItemStack> flag = s -> !this.rainMagicList.contains(s.getItem());
			List<ItemStack> magicList = IWand.getMagicList(IWand.getWandList(player), flag);
			float value = 12.5F * tier * 0.01F;
			float bossValue = 6.25F * tier * 0.01F;

			for (ItemStack stack : magicList) {

				IMagicItem magic = new MagicInfo(stack).getMagicItem();
				int recast = magic.getRecastTime(stack);
				if (recast <= 0) { continue; }

				float healValue = magic.isUniqueMagic() ? bossValue : value;
				magic.setRecastTime(stack, Math.max(0, (int) (recast - magic.getMaxRecastTime() * healValue)));
			}

			return;
		}

		if (this.tickCount % 20 != 0) { return; }
		int addLevel = this.getHourGlass() ? 1 : 0;

		// 敵モブなら
		if (isEnemy && !this.enemyList.contains(entity)) {
			this.addPotion(entity, PotionInit.magic_damage_receive, 1200 + tier * 600, tier - 1 + addLevel);
			this.enemyList.add(entity);
		}

		// 味方モブなら
		else if (!isEnemy && !this.mobList.contains(entity)) {
			this.addPotion(entity, PotionInit.magic_damage_cause, 1200 + tier * 600, tier - 1 + addLevel);
			this.mobList.add(entity);
		}
	}

	// フューチャーヴィジョンフィールド
	public void futureVisionField(LivingEntity entity, boolean isEnemy, boolean isFinish, int data, int tier) {

		if (isFinish && isEnemy && entity instanceof Mob mob) {
			SMUtil.tameAIDonmov(mob, 20 * tier);
			return;
		}

		if (this.tickCount % 20 != 0) { return; }

		// 敵モブなら
		if (isEnemy) {

			// 効果適用済みなら終了
			if (this.enemyList.contains(entity) || !(entity instanceof Mob mob)) { return; }

			int rate = entity instanceof Warden ? 3 : 1;
			SMUtil.tameAIDonmov(mob, (40 + (tier - 1) * 30) * rate);
			this.enemyList.add(entity);
		}

		// 味方モブなら
		else {

			// 効果適用済みなら終了
			if (this.mobList.contains(entity)) { return; }

			int addLevel = this.getHourGlass() ? 1 : 0;
			this.addPotion(entity, PotionInit.future_vision, 1200 * (1 + addLevel), tier - 1 + addLevel);
			this.mobList.add(entity);
		}
	}

	// リフレッシュフィールド
	public void reflashField(LivingEntity entity, boolean isEnemy, boolean isFinish, int data, int tier) {

		if (isFinish && !isEnemy) {
			entity.heal(entity.getMaxHealth());
			int addLevel = this.getHourGlass() ? 1 : 0;
			this.addPotion(entity, PotionInit.reflash_effect, 2400, tier - 1 + addLevel);
			return;
		}

		if (this.tickCount % 20 != 0 || isEnemy) { return; }

		if (!this.mobList.contains(entity)) {
			this.addPotion(entity, PotionInit.resurrection, 1200 + (data * 600), 0);
			this.mobList.add(entity);
		}

		int addLevel = this.getHourGlass() ? 1 : 0;
		this.addPotion(entity, PotionInit.reflash_effect, 600, tier - 1 + addLevel);
		entity.heal(0.5F);
	}

	// 乱数取得
	public float getRandFloat(Random rand) {
		return rand.nextFloat() - rand.nextFloat();
	}

	// 魔法陣の色の取得
	public RenderColor getColor(int light) {

		if (this.color == null) {
			switch (this.getData()) {
			case 0:
				this.color = new RenderColor(1F, 132F / 255F, 50F / 255F, light, OverlayTexture.NO_OVERLAY);
				break;
			case 1:
				this.color = new RenderColor(80F / 255F, 185F / 255F, 59F / 255F, light, OverlayTexture.NO_OVERLAY);
				break;
			case 2:
				this.color = new RenderColor(101F / 255F, 233F / 255F, 229F / 255F, light, OverlayTexture.NO_OVERLAY);
				break;
			case 3:
				this.color = new RenderColor(1F, 1F, 120F / 255F, light, OverlayTexture.NO_OVERLAY);
				break;
			case 4:
				this.color = new RenderColor(80F / 255F, 1F, 239F / 255F, light, OverlayTexture.NO_OVERLAY);
				break;
			}
		}

		return this.color;
	}

	// 属性の取得
	public SMElement getElement() {
		return SMElement.NON;
	}
}
