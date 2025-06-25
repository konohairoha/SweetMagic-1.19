package sweetmagic.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParticleSmoky extends ParticleSMBase {

	public ParticleSmoky(ClientLevel level, double x, double y, double z, double vx, double vy, double vz, float scale, SpriteSet sprite) {
		super(level, x, y, z, vx, vy, vz, scale, sprite);
		this.xd = this.zd = 0.1D - this.rand.nextDouble() * 0.1D;
		this.yd = 0.05D + this.rand.nextDouble() * 0.025D;
		this.lifetime = (int) (Math.random() * 8D) + 20;
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
	public record Factory(SpriteSet sprite) implements BaseCreateParticle {

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double red, double blue, double green) {
			ParticleSmoky par = new ParticleSmoky(level, x, y, z, 0, 0, 0, 1F, this.sprite);
			par.setColor((float) red, (float) blue, (float) green);
			return par;
		}
	}
}
