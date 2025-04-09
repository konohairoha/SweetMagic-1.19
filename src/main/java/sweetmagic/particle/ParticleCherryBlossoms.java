package sweetmagic.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ParticleCherryBlossoms extends ParticleSMBase {

	private int tickTime = 0;
	private int maxTick = 70;

	public ParticleCherryBlossoms(ClientLevel level, double x, double y, double z, double vx, double vy, double vz, float scale, SpriteSet spriteSet) {
		super(level, x, y, z, vx, vy, vz, scale, spriteSet);
		this.maxTick += this.random.nextInt(20);
		this.gravity = 0.0475F + this.random.nextFloat() * 0.005F;
		this.quadSize = scale + this.random.nextFloat() * 0.02F;
		this.lifetime = 70 + this.random.nextInt(30);
	}

	@Override
	public void tick() {

		int age = this.age;
		super.tick();

		if (this.onGround) {
			this.age = age;

			if (this.tickTime++ >= this.maxTick) {
				this.remove();
			}
		}

		else if (this.quadSize >= 0.25F) {
			this.xd *= (1D + (this.random.nextDouble() - this.random.nextDouble()) * 0.04D);
			this.yd *= (1D + (this.random.nextDouble() - this.random.nextDouble()) * 0.01D);
			this.zd *= (1D + (this.random.nextDouble() - this.random.nextDouble()) * 0.04D);
		}

		this.setSpriteFromAge(sprite);
	}

	@OnlyIn(Dist.CLIENT)
	public record Factory(SpriteSet sprite) implements ParticleProvider<SimpleParticleType> {

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			ParticleCherryBlossoms par = new ParticleCherryBlossoms(level, x, y, z, xSpeed, ySpeed, zSpeed, 0.08F, this.sprite);
			par.pickSprite(this.sprite);
			return par;
		}
	}

	@OnlyIn(Dist.CLIENT)
	public record Large(SpriteSet sprite) implements BaseCreateParticle {

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			return new ParticleCherryBlossoms(level, x, y, z, xSpeed, ySpeed, zSpeed, 0.25F, this.sprite);
		}
	}
}
