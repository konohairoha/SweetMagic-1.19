package sweetmagic.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;
import sweetmagic.init.item.sm.SMItem;
import sweetmagic.particle.ParticleSMBase.BaseCreateParticle;
import sweetmagic.util.MathHelper;

public class RotationParticle extends SimpleAnimatedParticle {

    public boolean isUp = false;
    private final Vec3 axis;
    private final Vec3 origin;
    private final double radius;
    private float angularVelocity;
    private float currentAngle;
    public ParticleRenderType renderType = ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;

    private RotationParticle(ClientLevel world, double x, double y, double z, Vec3 center, Vec3 rot, int ccw, double radius, double angle, SpriteSet sprite) {
        super(world, x, y, z, sprite, -5.0E-4F);
        this.origin = center;
        this.axis = rot;
        this.angularVelocity = (float) (ccw * SMItem.SPEED * Math.PI / 180F);
        this.radius = radius;
        this.currentAngle = (float) angle;
		this.xd = this.yd = this.zd= 0;
		this.rCol = this.gCol = this.bCol = 1F;
        this.lifetime = 30;
		this.quadSize = 0.075F;
        this.setAlpha(0F);
        this.setSpriteFromAge(sprite);
        this.hasPhysics = false;
    }

    public ParticleRenderType getRenderType() {
       return this.renderType;
    }

    public void setQuadSize(float scale) {
    	this.quadSize = scale;
    }

    @Override
    public void tick() {
        super.tick();
        this.setAlpha(this.age <= 0 ? 0F : 1F);
    }

    @Override
    public void move(double x, double y, double z) {
        this.currentAngle += this.angularVelocity;
        Vec3 rot = new Vec3(this.radius, 0, 0).yRot(currentAngle);
        Vec3 newPos = MathHelper.changeBasisN(this.axis, rot).add(this.origin);
        double addY = this.isUp ? this.age * 0.375D : 0D;
        super.move(newPos.x - this.x, newPos.y - this.y + addY, newPos.z - this.z);
    }

	public void addColor (float r, float g, float b) {
		this.setColor( r / 255F, g / 255F, b / 255F);
	}

	public record Factory(SpriteSet sprite) implements BaseCreateParticle {

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel world, double centerX, double centerY, double centerZ, double face, double radius, double angle) {

			Vec3 center = new Vec3(centerX, centerY, centerZ);
			int ccw = 1;
			if (face < 0) {
				ccw = -1;
				face = -face;
			}

			Direction dir = Direction.from3DDataValue((int) face);
			float radAngle = (float) (angle * Math.PI / 180);
			Vec3 axis = MathHelper.V3itoV3(dir.getNormal());
			Vec3 rot = new Vec3(radius, 0, 0).yRot(radAngle);
			Vec3 newPos = MathHelper.changeBasisN(axis, rot).add(center);
			RotationParticle par = new RotationParticle(world, newPos.x, newPos.y, newPos.z, center, axis, ccw, radius, radAngle, this.sprite);
			par.addColor(140F, 230F, 255F);
			return par;
		}
	}

	public record Rewrite(SpriteSet sprite) implements BaseCreateParticle {

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel world, double centerX, double centerY, double centerZ, double face, double radius, double angle) {

			Vec3 center = new Vec3(centerX, centerY, centerZ);
			int ccw = 1;
			if (face < 0) {
				ccw = -1;
				face = -face;
			}

			Direction dir = Direction.from3DDataValue((int) face);
			float radAngle = (float) (angle * Math.PI / 180);
			Vec3 axis = MathHelper.V3itoV3(dir.getNormal());
			Vec3 rot = new Vec3(radius, 0, 0).yRot(radAngle);
			Vec3 newPos = MathHelper.changeBasisN(axis, rot).add(center);
			RotationParticle par = new RotationParticle(world, newPos.x, newPos.y, newPos.z, center, axis, ccw, radius, radAngle, this.sprite);
			par.setColor( 114F / 255F, 1F, 138F / 255F);
			return par;
		}
	}

	public record Frost(SpriteSet sprite) implements BaseCreateParticle {

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel world, double centerX, double centerY, double centerZ, double face, double radius, double angle) {

			Vec3 center = new Vec3(centerX, centerY, centerZ);
			int ccw = 1;
			if (face < 0) {
				ccw = -1;
				face = -face;
			}

			Direction dir = Direction.from3DDataValue((int) face);
			float radAngle = (float) (angle * Math.PI / 180);
			Vec3 axis = MathHelper.V3itoV3(dir.getNormal());
			Vec3 rot = new Vec3(radius, 0, 0).yRot(radAngle);
			Vec3 newPos = MathHelper.changeBasisN(axis, rot).add(center);
			RotationParticle par = new RotationParticle(world, newPos.x, newPos.y, newPos.z, center, axis, ccw, radius, radAngle, this.sprite);
			par.setAlpha(1F);
			par.setQuadSize(0.3F);
			par.renderType = ParticleRenderType.PARTICLE_SHEET_OPAQUE;
			return par;
		}
	}

	public record Electoric(SpriteSet sprite) implements BaseCreateParticle {

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel world, double centerX, double centerY, double centerZ, double face, double radius, double angle) {

			Vec3 center = new Vec3(centerX, centerY, centerZ);
			int ccw = 1;
			if (face < 0) {
				ccw = -1;
				face = -face;
			}

			Direction dir = Direction.from3DDataValue((int) face);
			float radAngle = (float) (angle * Math.PI / 180);
			Vec3 axis = MathHelper.V3itoV3(dir.getNormal());
			Vec3 rot = new Vec3(radius, 0, 0).yRot(radAngle);
			Vec3 newPos = MathHelper.changeBasisN(axis, rot).add(center);
			RotationParticle par = new RotationParticle(world, newPos.x, newPos.y, newPos.z, center, axis, ccw, radius, radAngle, this.sprite);
			par.addColor(239F, 227F, 55F);
			par.setQuadSize(0.5F);
			return par;
		}
	}

	public record Blood(SpriteSet sprite) implements BaseCreateParticle {

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel world, double centerX, double centerY, double centerZ, double face, double radius, double angle) {

			Vec3 center = new Vec3(centerX, centerY, centerZ);
			int ccw = 1;
			if (face < 0) {
				ccw = -1;
				face = -face;
			}

			Direction dir = Direction.from3DDataValue((int) face);
			float radAngle = (float) (angle * Math.PI / 180);
			Vec3 axis = MathHelper.V3itoV3(dir.getNormal());
			Vec3 rot = new Vec3(radius, 0, 0).yRot(radAngle);
			Vec3 newPos = MathHelper.changeBasisN(axis, rot).add(center);
			RotationParticle par = new RotationParticle(world, newPos.x, newPos.y, newPos.z, center, axis, ccw, radius, radAngle, this.sprite);
			par.addColor(195F + this.getRand(32F), 17F + this.getRand(32F), 71F + this.getRand(32F));
			par.setQuadSize(0.5F);
			return par;
		}
	}

	public record Heal(SpriteSet sprite) implements BaseCreateParticle {

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel world, double centerX, double centerY, double centerZ, double face, double radius, double angle) {

			Vec3 center = new Vec3(centerX, centerY, centerZ);
			int ccw = 1;
			if (face < 0) {
				ccw = -1;
				face = -face;
			}

			Direction dir = Direction.from3DDataValue((int) face);
			float radAngle = (float) (angle * Math.PI / 180);
			Vec3 axis = MathHelper.V3itoV3(dir.getNormal());
			Vec3 rot = new Vec3(radius, 0, 0).yRot(radAngle);
			Vec3 newPos = MathHelper.changeBasisN(axis, rot).add(center);
			RotationParticle par = new RotationParticle(world, newPos.x, newPos.y, newPos.z, center, axis, ccw, radius, radAngle, this.sprite);
			par.setAlpha(1F);
			par.setQuadSize(0.15F + this.getRand(0.075F));
			par.renderType = ParticleRenderType.PARTICLE_SHEET_OPAQUE;
			return par;
		}
	}

	public record Reflash(SpriteSet sprite) implements BaseCreateParticle {

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel world, double centerX, double centerY, double centerZ, double face, double radius, double angle) {

			Vec3 center = new Vec3(centerX, centerY, centerZ);
			int ccw = 1;
			if (face < 0) {
				ccw = -1;
				face = -face;
			}

			Direction dir = Direction.from3DDataValue((int) face);
			float radAngle = (float) (angle * Math.PI / 180);
			Vec3 axis = MathHelper.V3itoV3(dir.getNormal());
			Vec3 rot = new Vec3(radius, 0, 0).yRot(radAngle);
			Vec3 newPos = MathHelper.changeBasisN(axis, rot).add(center);
			RotationParticle par = new RotationParticle(world, newPos.x, newPos.y, newPos.z, center, axis, ccw, radius, radAngle, this.sprite);
			par.setAlpha(1F);
			par.setQuadSize(0.25F + this.getRand(0.015F));
			par.addColor(20F + this.getRand(100F), 103F + this.getRand(100F), 180F + this.getRand(75F));
			par.renderType = ParticleRenderType.PARTICLE_SHEET_OPAQUE;
			return par;
		}
	}

	public record Cyclone(SpriteSet sprite) implements BaseCreateParticle {

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel world, double centerX, double centerY, double centerZ, double face, double radius, double angle) {

			Vec3 center = new Vec3(centerX, centerY, centerZ);

			int ccw = 1;
			if (face < 0) {
				ccw = -1;
				face = -face;
			}

			Direction dir = Direction.from3DDataValue((int) face);
			float radAngle = (float) (angle * Math.PI / 180);
			Vec3 axis = MathHelper.V3itoV3(dir.getNormal());
			Vec3 rot = new Vec3(radius, 0, 0).yRot(radAngle);
			Vec3 newPos = MathHelper.changeBasisN(axis, rot).add(center);
			RotationParticle par = new RotationParticle(world, newPos.x, newPos.y, newPos.z, center, axis, ccw, radius, radAngle, this.sprite);
			par.lifetime = 40 + rand.nextInt(31);
			par.setQuadSize(0.5F + this.getRand(0.5F));
			par.yd = 2D;
			par.isUp = true;
			return par;
		}
	}

	public record Yellow(SpriteSet sprite) implements BaseCreateParticle {

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel world, double centerX, double centerY, double centerZ, double face, double radius, double angle) {

			Vec3 center = new Vec3(centerX, centerY, centerZ);
			int ccw = 1;
			if (face < 0) {
				ccw = -1;
				face = -face;
			}

			Direction dir = Direction.from3DDataValue((int) face);
			float radAngle = (float) (angle * Math.PI / 180);
			Vec3 axis = MathHelper.V3itoV3(dir.getNormal());
			Vec3 rot = new Vec3(radius, 0, 0).yRot(radAngle);
			Vec3 newPos = MathHelper.changeBasisN(axis, rot).add(center);
			RotationParticle par = new RotationParticle(world, newPos.x, newPos.y, newPos.z, center, axis, ccw, radius, radAngle, this.sprite);
			par.addColor(255F - this.getRand(75F), 249F - this.getRand(50F), 91F + this.getRand(50F));
			return par;
		}
	}

	public record Toxic(SpriteSet sprite) implements BaseCreateParticle {

		@Override
		public Particle createParticle(SimpleParticleType type, ClientLevel world, double centerX, double centerY, double centerZ, double face, double radius, double angle) {

			Vec3 center = new Vec3(centerX, centerY, centerZ);
			int ccw = 1;
			if (face < 0) {
				ccw = -1;
				face = -face;
			}

			Direction dir = Direction.from3DDataValue((int) face);
			float radAngle = (float) (angle * Math.PI / 180);
			Vec3 axis = MathHelper.V3itoV3(dir.getNormal());
			Vec3 rot = new Vec3(radius, 0, 0).yRot(radAngle);
			Vec3 newPos = MathHelper.changeBasisN(axis, rot).add(center);
			RotationParticle par = new RotationParticle(world, newPos.x, newPos.y, newPos.z, center, axis, ccw, radius, radAngle, this.sprite);
			par.setAlpha(1F);
			par.setQuadSize(0.15F + this.getRand(0.015F));
			par.addColor(30F + this.getRand(32F), 216F + this.getRand(32F), 86F + this.getRand(32F));
//			par.scale(this.getRand(0.8F) + 0.3F);
			par.renderType = ParticleRenderType.PARTICLE_SHEET_OPAQUE;
			return par;
		}
	}
}
