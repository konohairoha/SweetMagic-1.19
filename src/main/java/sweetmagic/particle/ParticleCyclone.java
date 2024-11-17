package sweetmagic.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParticleCyclone extends ParticleSMBase {

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
		this.setSpriteFromAge(this.sprite);
	}

	@OnlyIn(Dist.CLIENT)
	public record Factory(SpriteSet sprite) implements ParticleProvider<SimpleParticleType> {

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			return new ParticleCyclone(level, x, y, z, xSpeed, ySpeed, zSpeed, 1F, this.sprite);
		}
	}
}
