package sweetmagic.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParticleMagicLight extends ParticleSMBase {

	public ParticleMagicLight(ClientLevel level, double x, double y, double z, double vx, double vy, double vz, float scale, SpriteSet spriteSet) {
		super(level, x, y, z, vx, vy, vz, scale, spriteSet);
		this.lifetime = (int) (5D / (Math.random() * 5D + 0.1D)) + 28;
		this.hasPhysics = true;
	}

	public void move(double x, double y, double z) {
		this.setBoundingBox(this.getBoundingBox().move(x, y, z));
		this.setLocationFromBoundingbox();
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public void tick() {
		super.tick();
		this.xd *= 0.95D;
		this.yd *= 0.95D;
		this.zd *= 0.95D;
		this.setSpriteFromAge(sprite);
	}

	@OnlyIn(Dist.CLIENT)
	public record Factory(SpriteSet sprite) implements BaseCreateParticle {

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			return new ParticleMagicLight(level, x, y, z, xSpeed, ySpeed, zSpeed, 0.2F, this.sprite);
		}
	}
}
