package sweetmagic.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ParticleLay extends ParticleSMBase {

	public ParticleLay(ClientLevel level, double x, double y, double z, double vx, double vy, double vz, float scale, SpriteSet spriteSet) {
		super(level, x, y, z, vx, vy, vz, scale, spriteSet);
		this.lifetime = 24;
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
		this.setSpriteFromAge(sprite);
	}

	@OnlyIn(Dist.CLIENT)
	public record Factory(SpriteSet sprite) implements BaseCreateParticle {

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double red, double blue, double green) {
			ParticleLay par = new ParticleLay(level, x, y, z, 0, 0.05F, 0, 0.2F, this.sprite);
			par.setColor((float) red, (float) blue, (float) green);
			return par;
		}
	}
}
