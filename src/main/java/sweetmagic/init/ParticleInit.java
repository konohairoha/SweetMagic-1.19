package sweetmagic.init;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import sweetmagic.SweetMagicCore;
import sweetmagic.particle.ParticleCherryBlossoms;
import sweetmagic.particle.ParticleCyclone;
import sweetmagic.particle.ParticleLay;
import sweetmagic.particle.ParticleMF;
import sweetmagic.particle.ParticleMagicLight;
import sweetmagic.particle.ParticleNomal;
import sweetmagic.particle.ParticleSmoky;
import sweetmagic.particle.RotationParticle;
public class ParticleInit {

	public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = SweetMagicCore.getDef(ForgeRegistries.PARTICLE_TYPES);

	public static final SimpleParticleType MAGICLIGHT = register("magiclight");
	public static final SimpleParticleType NORMAL = register("normal");
	public static final SimpleParticleType DIG = register("dig");
	public static final SimpleParticleType ORB = register("orb");
	public static final SimpleParticleType ADDATTACK = register("addattack");
	public static final SimpleParticleType MF = register("orb_mf");
	public static final SimpleParticleType TWILIGHTLIGHT = register("twilightlight");
	public static final SimpleParticleType LAY = register("lay");
	public static final SimpleParticleType FROST = register("frost");
	public static final SimpleParticleType CYCLONE = register("cyclone");
	public static final SimpleParticleType SMOKY = register("smoky");
	public static final SimpleParticleType BUBBLE = register("bubble");
	public static final SimpleParticleType BLOOD = register("orb_gray");
	public static final SimpleParticleType POISON = register("orb_poison");
	public static final SimpleParticleType GRAVITY = register("orb_gravity");
	public static final SimpleParticleType AETHER = register("orb_aether");
	public static final SimpleParticleType DIVINE = register("orb_divine");
	public static final SimpleParticleType REFLASH = register("reflash");
	public static final SimpleParticleType STORAGE = register("storage");
	public static final SimpleParticleType CYCLE_ORB = register("cycle_orb");
	public static final SimpleParticleType CYCLE_ORB_Y = register("cycle_orb_y");
	public static final SimpleParticleType CYCLE_ORB_BIG = register("cycle_orb_big");
	public static final SimpleParticleType CYCLE_RERITE = register("cycle_rerite");
	public static final SimpleParticleType CYCLE_GRAY_ORB = register("cycle_gray_orb");
	public static final SimpleParticleType CYCLE_FROST = register("cycle_frost");
	public static final SimpleParticleType CYCLE_FROST_TORNADO = register("cycle_frost_tornado");
	public static final SimpleParticleType CYCLE_FIRE = register("cycle_fire");
	public static final SimpleParticleType CYCLE_FIRE_TORNADO = register("cycle_fire_tornado");
	public static final SimpleParticleType CYCLE_ELECTRIC = register("cycle_electric");
	public static final SimpleParticleType CYCLE_BLOOD = register("cycle_blood");
	public static final SimpleParticleType CYCLE_HEAL = register("cycle_heal");
	public static final SimpleParticleType CYCLE_REFLASH = register("cycle_reflash");
	public static final SimpleParticleType CYCLE_TORNADO = register("cycle_tornado");
	public static final SimpleParticleType CYCLE_TOXIC = register("cycle_toxic");
	public static final SimpleParticleType CYCLE_LIGHT = register("cycle_light");
	public static final SimpleParticleType DARK = register("dark");
	public static final SimpleParticleType CRYSTAL = register("crystal");
	public static final SimpleParticleType FROST_LASER = register("frost_laser");
	public static final SimpleParticleType CHERRY_BLOSSOMS = register("cherry_blossoms");
	public static final SimpleParticleType CHERRY_BLOSSOMS_LARGE = register("cherry_blossoms_large");
	public static final SimpleParticleType GRAVITY_FIELD = register("gravity_filed");
	public static final SimpleParticleType WIND_FIELD = register("wind_filed");
	public static final SimpleParticleType RAIN_FIELD = register("rain_filed");

	public static SimpleParticleType register(String name) {
		SimpleParticleType par = new SimpleParticleType(false);
		PARTICLE_TYPES.register(name, () -> par);
		return par;
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public static void registerParticle(RegisterParticleProvidersEvent event) {
		ParticleEngine engine = Minecraft.getInstance().particleEngine;
		engine.register(MAGICLIGHT, ParticleMagicLight.Factory::new);
		engine.register(NORMAL, ParticleNomal.Factory::new);
		engine.register(DIG, ParticleNomal.Factory::new);
		engine.register(ORB, ParticleNomal.Orb::new);
		engine.register(ADDATTACK, ParticleNomal.AddAttack::new);
		engine.register(MF, ParticleMF.Factory::new);
		engine.register(TWILIGHTLIGHT, ParticleMagicLight.Factory::new);
		engine.register(LAY, ParticleLay.Factory::new);
		engine.register(FROST, ParticleNomal.Maigc::new);
		engine.register(CYCLONE, ParticleCyclone.Factory::new);
		engine.register(SMOKY, ParticleSmoky.Factory::new);
		engine.register(BUBBLE, ParticleNomal.Maigc::new);
		engine.register(BLOOD, ParticleNomal.Blood::new);
		engine.register(POISON, ParticleNomal.Poison::new);
		engine.register(GRAVITY, ParticleNomal.Gravity::new);
		engine.register(AETHER, ParticleNomal.Aether::new);
		engine.register(DIVINE, ParticleNomal.Divine::new);
		engine.register(DARK, ParticleNomal.Dark::new);
		engine.register(REFLASH, ParticleNomal.Reflash::new);
		engine.register(STORAGE, ParticleNomal.Storage::new);
		engine.register(CYCLE_ORB, RotationParticle.Factory::new);
		engine.register(CYCLE_ORB_Y, RotationParticle.Yellow::new);
		engine.register(CYCLE_ORB_BIG, RotationParticle.Factory::new);
		engine.register(CYCLE_RERITE, RotationParticle.Rewrite::new);
		engine.register(CYCLE_GRAY_ORB, RotationParticle.Factory::new);
		engine.register(CYCLE_FROST, RotationParticle.Frost::new);
		engine.register(CYCLE_FROST_TORNADO, RotationParticle.Cyclon2::new);
		engine.register(CYCLE_FIRE, RotationParticle.Frost::new);
		engine.register(CYCLE_FIRE_TORNADO, RotationParticle.Cyclon2::new);
		engine.register(CYCLE_ELECTRIC, RotationParticle.Electoric::new);
		engine.register(CYCLE_BLOOD, RotationParticle.Blood::new);
		engine.register(CYCLE_HEAL, RotationParticle.Heal::new);
		engine.register(CYCLE_REFLASH, RotationParticle.Reflash::new);
		engine.register(CYCLE_TORNADO, RotationParticle.Cyclone::new);
		engine.register(CYCLE_TOXIC, RotationParticle.Toxic::new);
		engine.register(CYCLE_LIGHT, RotationParticle.Cyclon3::new);
		engine.register(CRYSTAL, ParticleNomal.Crystal::new);
		engine.register(FROST_LASER, ParticleNomal.Frostlaser::new);
		engine.register(CHERRY_BLOSSOMS, ParticleCherryBlossoms.Factory::new);
		engine.register(CHERRY_BLOSSOMS_LARGE, ParticleCherryBlossoms.Large::new);
		engine.register(GRAVITY_FIELD, ParticleNomal.GravityField::new);
		engine.register(WIND_FIELD, ParticleNomal.WindField::new);
		engine.register(RAIN_FIELD, ParticleNomal.RainField::new);
	}
}
