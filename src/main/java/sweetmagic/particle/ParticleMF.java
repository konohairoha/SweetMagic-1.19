package sweetmagic.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParticleMF extends ParticleSMBase {

	public ParticleMF(ClientLevel level, double x, double y, double z, double vx, double vy, double vz, float scale, SpriteSet spriteSet) {
		super(level, x, y, z, vx, vy, vz, scale, spriteSet);
		this.lifetime = 70;
		this.hasPhysics = true;
	}

	public void move(double x, double y, double z) {
		this.setBoundingBox(this.getBoundingBox().move(x, y, z));
		this.setLocationFromBoundingbox();
	}

	@Override
	public void tick() {
		super.tick();
		this.setSpriteFromAge(this.sprite);
		float age = (float) this.age / (float) this.lifetime;
		this.setColor((11F + 231F * age) / 255F, (238F - 182F * age) / 255F, (244F - 244F * age) / 255F);
	}

	@OnlyIn(Dist.CLIENT)
	public record Factory(SpriteSet sprite) implements BaseCreateParticle {

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			ParticleMF par = new ParticleMF(level, x, y, z, xSpeed, ySpeed, zSpeed, 0.2F, this.sprite);
			par.scale(this.getRand(0.8F) + 0.3F);
			return par;
		}
	}
}
