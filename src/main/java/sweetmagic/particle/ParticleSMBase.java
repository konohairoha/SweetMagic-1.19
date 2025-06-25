package sweetmagic.particle;

import java.util.Random;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class ParticleSMBase extends TextureSheetParticle {

	protected final SpriteSet sprite;
	protected RandomSource rand = RandomSource.create();

	public ParticleSMBase(ClientLevel level, double x, double y, double z, double vx, double vy, double vz, float scale, SpriteSet sprite) {
		super(level, x, y, z, vx, vy, vz);
		this.pickSprite(sprite);
		this.sprite = sprite;
		this.xd = this.yd = this.zd= 0;
		this.rCol = this.gCol = this.bCol = 1F;
		this.quadSize = scale;
		this.lifetime = (int) (3D / (Math.random() * 4D + 0.2D)) + 32;
		this.hasPhysics = true;
		this.xd = vx;
		this.yd = vy;
		this.zd = vz;
	}

	public void addColor(float r, float g, float b) {
		this.setColor(r / 255F, g / 255F, b / 255F);
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	public int getLightColor(float parTick) {
		return 240 | 240 << 16;
	}

	public interface BaseCreateParticle extends ParticleProvider<SimpleParticleType> {
		public static Random rand = new Random();

		default float getRand(float rate) {
			return rand.nextFloat() * rate;
		}
	}
}
