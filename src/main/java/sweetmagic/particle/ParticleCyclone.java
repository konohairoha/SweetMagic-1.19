package sweetmagic.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParticleCyclone extends ParticleSMBase {

	private boolean isSmall = false;

	public ParticleCyclone(ClientLevel level, double x, double y, double z, double vx, double vy, double vz, float scale, SpriteSet spriteSet) {
		super(level, x, y, z, vx, vy, vz, scale, spriteSet);
		this.lifetime = 10;
		this.hasPhysics = true;
	}

	public void move(double x, double y, double z) {
		this.setBoundingBox(this.getBoundingBox().move(x, y, z));
		this.setLocationFromBoundingbox();
	}

	@Override
	public void tick() {
		super.tick();
		this.xd *= 0.9D;
		this.yd *= 0.9D;
		this.zd *= 0.9D;

		if(this.isSmall) {
			this.quadSize *= 0.95D;
		}

		this.setSpriteFromAge(this.sprite);
	}

	@OnlyIn(Dist.CLIENT)
	public record Factory(SpriteSet sprite) implements BaseCreateParticle {

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			return new ParticleCyclone(level, x, y, z, xSpeed, ySpeed, zSpeed, 1F, this.sprite);
		}
	}

	@OnlyIn(Dist.CLIENT)
	public record Belial(SpriteSet sprite) implements BaseCreateParticle {

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			ParticleCyclone par = new ParticleCyclone(level, x, y, z, xSpeed, ySpeed, zSpeed, 0.1F + rand.nextFloat() * 0.1F, this.sprite);
			par.lifetime = 20 + rand.nextInt(10);
			par.isSmall = true;
			return par;
		}
	}
}
