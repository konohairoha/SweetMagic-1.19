package sweetmagic.init.entity.projectile;

import java.util.Random;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import sweetmagic.api.emagic.SMElement;
import sweetmagic.api.iitem.info.WandInfo;
import sweetmagic.init.EntityInit;
import sweetmagic.init.ParticleInit;

public class RockBlastMagicShot extends AbstractMagicShot {

	private static final ItemStack ROCK = new ItemStack(Blocks.STONE);
	private static final ItemStack IRON = new ItemStack(Blocks.IRON_BLOCK);
	private static final ItemStack DIAMON = new ItemStack(Blocks.DIAMOND_BLOCK);

	public RockBlastMagicShot(EntityType<? extends RockBlastMagicShot> entityType, Level world) {
		super(entityType, world);
	}

	public RockBlastMagicShot(double x, double y, double z, Level world) {
		this(EntityInit.rockBlastMagic, world);
		this.setPos(x, y, z);
	}

	public RockBlastMagicShot(Level world, LivingEntity entity, WandInfo wandInfo) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.setWandInfo(wandInfo);
	}

	public RockBlastMagicShot(Level world, LivingEntity entity, ItemStack stack) {
		this(entity.getX(), entity.getEyeY() - (double) 0.1F, entity.getZ(), world);
		this.setOwner(entity);
		this.stack = stack;
	}

	// パーティクルスポーン
	protected void spawnParticle() {

		if (this.tickCount < 3) { return; }
		Vec3 vec = this.getDeltaMovement();
		float x = (float) (-vec.x / 80F);
		float y = (float) (-vec.y / 80F);
		float z = (float) (-vec.z / 80F);
		Random rand = this.rand;

		for (int i = 0; i < 6; i++) {

			float f1 = (float) (this.getX() - 0.5F + rand.nextFloat() + vec.x * i / 4.0F);
			float f2 = (float) (this.getY() + 0.25F + rand.nextFloat() * 0.5 + vec.y * i / 4.0D);
			float f3 = (float) (this.getZ() - 0.5F + rand.nextFloat() + vec.z * i / 4.0D);

			this.level.addParticle(ParticleInit.DIG.get(), f1, f2, f3, x, y, z);
		}
	}

	public BlockState getRockState () {
		return ((BlockItem) this.getRockStack().getItem()).getBlock().defaultBlockState();
	}

	public ItemStack getRockStack () {
		switch (this.getData()) {
		case 1: return IRON;
		case 2: return DIAMON;
		default: return ROCK;
		}
	}

	// 属性の取得
	public SMElement getElement() {
		return SMElement.EARTH;
	}
}
