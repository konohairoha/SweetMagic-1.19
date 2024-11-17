package sweetmagic.init.entity.projectile;

import java.util.Random;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import sweetmagic.api.emagic.SMElement;
import sweetmagic.api.iitem.info.WandInfo;
import sweetmagic.init.EntityInit;
import sweetmagic.init.ParticleInit;
import sweetmagic.init.PotionInit;
import sweetmagic.init.SoundInit;

public class BubbleMagicShot extends AbstractMagicShot {

	public BubbleMagicShot(EntityType<? extends BubbleMagicShot> entityType, Level world) {
		super(entityType, world);
	}

	public BubbleMagicShot(double x, double y, double z, Level world) {
		this(EntityInit.bubbleMagic, world);
		this.setPos(x, y, z);
	}

	public BubbleMagicShot(Level world, LivingEntity entity, WandInfo wandInfo) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.setWandInfo(wandInfo);
	}

	public BubbleMagicShot(Level world, LivingEntity entity, ItemStack stack) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.stack = stack;
	}

	// えんちちーに当たった時の処理
	protected void entityHit(LivingEntity living) {

		int time = 10 * this.getWandLevel();

		if ( this.canTargetEffect(living, this.getOwner()) ) {
			this.addPotion(living, PotionInit.bubble, time, this.getData());
		}

		else {
			living.removeEffect(PotionInit.bubble);
		}

		this.playSound(SoundInit.BUBBLE, 0.1F, 1F);
	}

	protected void spawnParticleShort(ServerLevel sever, BlockPos pos) {
		float x = (float) (pos.getX() + this.getRandFloat(0.5F));
		float y = (float) (pos.getY() + this.getRandFloat(0.5F));
		float z = (float) (pos.getZ() + this.getRandFloat(0.5F));

		for (int i = 0; i < 4; i++) {
			sever.sendParticles(ParticleInit.BUBBLE.get(), x, y, z, 4, 0F, 0F, 0F, 0.15F);
		}
	}

	public int getMinParticleTick () {
		return 3;
	}

	// パーティクルスポーン
	protected void spawnParticle() {

		Random rand = new Random();
		Vec3 vec = this.getDeltaMovement();

		for (int i = 0; i < 5; i++) {
			float x = (float) (-vec.x / 20F) + this.getRandFloat(0.085F);
			float y = (float) (-vec.y / 20F) + this.getRandFloat(0.085F);
			float z = (float) (-vec.z / 20F) + this.getRandFloat(0.085F);

			float f1 = (float) (this.getX() - 0.5F + rand.nextFloat() + vec.x * i * 0.25F);
			float f2 = (float) (this.getY() - 0.25F + rand.nextFloat() * 0.5 + vec.y * i * 0.25F);
			float f3 = (float) (this.getZ() - 0.5F + rand.nextFloat() + vec.z * i * 0.25F);
			this.level.addParticle(ParticleInit.BUBBLE.get(), f1, f2, f3, x, y, z);
		}
	}

	// 属性の取得
	public SMElement getElement() {
		return SMElement.WATER;
	}
}
