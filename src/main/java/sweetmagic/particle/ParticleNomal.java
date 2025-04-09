package sweetmagic.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParticleNomal extends ParticleSMBase {

	public boolean isDown = true;

	public ParticleNomal(ClientLevel level, double x, double y, double z, double vx, double vy, double vz, float scale, SpriteSet spriteSet) {
		super(level, x, y, z, vx, vy, vz, scale, spriteSet);
	}

	public void move(double x, double y, double z) {
		this.setBoundingBox(this.getBoundingBox().move(x, y, z));
		this.setLocationFromBoundingbox();
	}

	@Override
	public void tick() {
		super.tick();
		if (this.isDown) {
			this.xd *= 0.9D;
			this.yd *= 0.9D;
			this.zd *= 0.9D;
		}
		this.setSpriteFromAge(this.sprite);
	}

	@OnlyIn(Dist.CLIENT)
	public record Factory(SpriteSet sprite) implements BaseCreateParticle {

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			return new ParticleNomal(level, x, y, z, xSpeed, ySpeed, zSpeed, 0.2F, sprite);
		}
	}

	@OnlyIn(Dist.CLIENT)
	public record Orb(SpriteSet sprite) implements BaseCreateParticle {

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double red, double blue, double green) {
			ParticleNomal par = new ParticleNomal(level, x, y, z, 0, 0, 0, 0.2F, this.sprite);
			par.setColor((float) red, (float) blue, (float) green);
			return par;
		}
	}

	@OnlyIn(Dist.CLIENT)
	public record Blood(SpriteSet sprite) implements BaseCreateParticle {

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			ParticleNomal par = new ParticleNomal(level, x, y, z, xSpeed, ySpeed, zSpeed, 0.2F, this.sprite);
			par.addColor(195F + this.getRand(32F), 17F + this.getRand(32F), 71F + this.getRand(32F));
			par.scale(this.getRand(0.8F) + 0.3F);
			par.lifetime = 30 + rand.nextInt(25);
			return par;
		}
	}

	@OnlyIn(Dist.CLIENT)
	public record Poison(SpriteSet sprite) implements BaseCreateParticle {

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			ParticleNomal par = new ParticleNomal(level, x, y, z, xSpeed, ySpeed, zSpeed, 0.2F, this.sprite);
			par.addColor(30F + this.getRand(32F), 216F + this.getRand(32F), 86F + this.getRand(32F));
			par.scale(this.getRand(0.8F) + 0.3F);
			return par;
		}
	}

	@OnlyIn(Dist.CLIENT)
	public record Gravity(SpriteSet sprite) implements BaseCreateParticle {

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			ParticleNomal par = new ParticleNomal(level, x, y, z, xSpeed, ySpeed, zSpeed, 0.2F, this.sprite);
			par.addColor(174F + this.getRand(6F), 48F + this.getRand(6F), 242F + this.getRand(6F));
			par.scale(this.getRand(0.8F) + 0.3F);
			par.lifetime = rand.nextInt(16) + 24;
			return par;
		}
	}

	@OnlyIn(Dist.CLIENT)
	public record Aether(SpriteSet sprite) implements BaseCreateParticle {

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			ParticleNomal par = new ParticleNomal(level, x, y, z, xSpeed, ySpeed, zSpeed, 0.2F, this.sprite);
			par.addColor(89F, this.getRand(255F), 255F);
			par.scale(this.getRand(0.3F) + 0.15F);
			return par;
		}
	}

	@OnlyIn(Dist.CLIENT)
	public record Divine(SpriteSet sprite) implements BaseCreateParticle {

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			ParticleNomal par = new ParticleNomal(level, x, y, z, xSpeed, ySpeed, zSpeed, 0.2F, this.sprite);
			par.addColor(this.getRand(255F), 163F, 255F);
			par.scale(this.getRand(0.3F) + 0.15F);
			return par;
		}
	}

	@OnlyIn(Dist.CLIENT)
	public record Dark(SpriteSet sprite) implements BaseCreateParticle {

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			ParticleNomal par = new ParticleNomal(level, x, y, z, xSpeed, ySpeed, zSpeed, 0.2F, this.sprite);
			par.addColor(64F, 8F, 107F);
			par.scale(0.75F);
			return par;
		}
	}

	@OnlyIn(Dist.CLIENT)
	public record Frostlaser(SpriteSet sprite) implements BaseCreateParticle {

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			ParticleNomal par = new ParticleNomal(level, x, y, z, xSpeed, ySpeed, zSpeed, 0.3F, this.sprite);
			par.addColor(203F, 223F, 249F);
			par.scale(2F);
			par.lifetime = rand.nextInt(16) + 24;
			return par;
		}
	}

	@OnlyIn(Dist.CLIENT)
	public record Crystal(SpriteSet sprite) implements BaseCreateParticle {

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			ParticleNomal par = new ParticleNomal(level, x, y, z, xSpeed, ySpeed, zSpeed, 0.4F, this.sprite);
			par.isDown = false;
			return par;
		}
	}

	@OnlyIn(Dist.CLIENT)
	public record GravityField(SpriteSet sprite) implements BaseCreateParticle {

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			ParticleNomal par = new ParticleNomal(level, x, y, z, xSpeed, ySpeed, zSpeed, 0.2F, this.sprite);
			par.addColor(255F, 182F, 100F);
			par.scale(this.getRand(0.8F) + 0.3F);
			return par;
		}
	}

	@OnlyIn(Dist.CLIENT)
	public record WindField(SpriteSet sprite) implements BaseCreateParticle {

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			ParticleNomal par = new ParticleNomal(level, x, y, z, xSpeed, ySpeed, zSpeed, 0.2F, this.sprite);
			par.addColor(120F, 225F, 99F);
			par.scale(this.getRand(0.8F) + 0.3F);
			return par;
		}
	}

	@OnlyIn(Dist.CLIENT)
	public record RainField(SpriteSet sprite) implements BaseCreateParticle {

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			ParticleNomal par = new ParticleNomal(level, x, y, z, xSpeed, ySpeed, zSpeed, 0.2F, this.sprite);
			par.addColor(181F, 255F, 255F);
			par.scale(rand.nextFloat() * 0.8F + 0.3F);
			return par;
		}
	}

	@OnlyIn(Dist.CLIENT)
	public record Maigc(SpriteSet sprite) implements BaseCreateParticle {

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			ParticleNomal par = new ParticleNomal(level, x, y, z, xSpeed, ySpeed, zSpeed, 0.2F, this.sprite);
			par.scale(0.5F);
			return par;
		}
	}

	@OnlyIn(Dist.CLIENT)
	public record Reflash(SpriteSet sprite) implements BaseCreateParticle {

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			ParticleNomal par = new ParticleNomal(world, x, y, z, xSpeed, ySpeed, zSpeed, 0.2F, this.sprite);
			par.addColor(20F + rand.nextInt(100), 103F + rand.nextInt(100), 180F + rand.nextInt(75));
			par.scale(0.5F + rand.nextFloat() * 0.25F);
			return par;
		}
	}

	@OnlyIn(Dist.CLIENT)
	public record AddAttack(SpriteSet sprite) implements BaseCreateParticle {

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double red, double blue, double green) {
			ParticleNomal par = new ParticleNomal(level, x, y, z, 0, 0, 0, 0.1F, this.sprite);
			par.setColor((float) red, (float) blue, (float) green);
			return par;
		}
	}

	@OnlyIn(Dist.CLIENT)
	public record Storage(SpriteSet sprite) implements BaseCreateParticle {

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel world, double x, double y, double z, double r, double g, double b) {

			float xSpeed = (rand.nextFloat() - rand.nextFloat()) * 0.1F;
			float ySpeed = (rand.nextFloat() - rand.nextFloat()) * 0.1F;
			float zSpeed = (rand.nextFloat() - rand.nextFloat()) * 0.1F;

			ParticleNomal par = new ParticleNomal(world, x, y, z, xSpeed, ySpeed, zSpeed, 0.2F, this.sprite);
			par.addColor((float) r, (float) g, (float) b);
			par.scale(0.5F + world.random.nextFloat() * 0.25F);
			return par;
		}
	}
}
