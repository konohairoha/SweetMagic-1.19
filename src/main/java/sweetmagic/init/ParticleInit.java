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
import net.minecraftforge.registries.RegistryObject;
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

	public static final RegistryObject<SimpleParticleType> MAGICLIGHT = register("magiclight");
	public static final RegistryObject<SimpleParticleType> NORMAL = register("normal");
	public static final RegistryObject<SimpleParticleType> DIG = register("dig");
	public static final RegistryObject<SimpleParticleType> ORB = register("orb");
	public static final RegistryObject<SimpleParticleType> ADDATTACK = register("addattack");
	public static final RegistryObject<SimpleParticleType> MF = register("orb_mf");
	public static final RegistryObject<SimpleParticleType> TWILIGHTLIGHT = register("twilightlight");
	public static final RegistryObject<SimpleParticleType> LAY = register("lay");
	public static final RegistryObject<SimpleParticleType> FROST = register("frost");
	public static final RegistryObject<SimpleParticleType> CYCLONE = register("cyclone");
	public static final RegistryObject<SimpleParticleType> SMOKY = register("smoky");
	public static final RegistryObject<SimpleParticleType> BUBBLE = register("bubble");
	public static final RegistryObject<SimpleParticleType> BLOOD = register("orb_gray");
	public static final RegistryObject<SimpleParticleType> POISON = register("orb_poison");
	public static final RegistryObject<SimpleParticleType> GRAVITY = register("orb_gravity");
	public static final RegistryObject<SimpleParticleType> AETHER = register("orb_aether");
	public static final RegistryObject<SimpleParticleType> DIVINE = register("orb_divine");
	public static final RegistryObject<SimpleParticleType> REFLASH = register("reflash");
	public static final RegistryObject<SimpleParticleType> CYCLE_ORB = register("cycle_orb");
	public static final RegistryObject<SimpleParticleType> CYCLE_ORB_Y = register("cycle_orb_y");
	public static final RegistryObject<SimpleParticleType> CYCLE_ORB_BIG = register("cycle_orb_big");
	public static final RegistryObject<SimpleParticleType> CYCLE_RERITE = register("cycle_rerite");
	public static final RegistryObject<SimpleParticleType> CYCLE_GRAY_ORB = register("cycle_gray_orb");
	public static final RegistryObject<SimpleParticleType> CYCLE_FROST = register("cycle_frost");
	public static final RegistryObject<SimpleParticleType> CYCLE_FIRE = register("cycle_fire");
	public static final RegistryObject<SimpleParticleType> CYCLE_ELECTRIC = register("cycle_electric");
	public static final RegistryObject<SimpleParticleType> CYCLE_BLOOD = register("cycle_blood");
	public static final RegistryObject<SimpleParticleType> CYCLE_HEAL = register("cycle_heal");
	public static final RegistryObject<SimpleParticleType> CYCLE_REFLASH = register("cycle_reflash");
	public static final RegistryObject<SimpleParticleType> CYCLE_TORNADO = register("cycle_tornado");
	public static final RegistryObject<SimpleParticleType> CYCLE_TOXIC = register("cycle_toxic");
	public static final RegistryObject<SimpleParticleType> DARK = register("dark");
	public static final RegistryObject<SimpleParticleType> CRYSTAL = register("crystal");
	public static final RegistryObject<SimpleParticleType> FROST_LASER = register("frost_laser");
	public static final RegistryObject<SimpleParticleType> CHERRY_BLOSSOMS = register("cherry_blossoms");
	public static final RegistryObject<SimpleParticleType> CHERRY_BLOSSOMS_LARGE = register("cherry_blossoms_large");
	public static final RegistryObject<SimpleParticleType> GRAVITY_FIELD = register("gravity_filed");
	public static final RegistryObject<SimpleParticleType> WIND_FIELD = register("wind_filed");
	public static final RegistryObject<SimpleParticleType> RAIN_FIELD = register("rain_filed");

	public static RegistryObject<SimpleParticleType> register (String name) {
		return PARTICLE_TYPES.register(name, () -> new SimpleParticleType(false));
	}

	@OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerParticle(RegisterParticleProvidersEvent event) {
    	ParticleEngine engine = Minecraft.getInstance().particleEngine;
    	engine.register(MAGICLIGHT.get(), ParticleMagicLight.Factory::new);
    	engine.register(NORMAL.get(), ParticleNomal.Factory::new);
    	engine.register(DIG.get(), ParticleNomal.Factory::new);
    	engine.register(ORB.get(), ParticleNomal.Orb::new);
    	engine.register(ADDATTACK.get(), ParticleNomal.AddAttack::new);
    	engine.register(MF.get(), ParticleMF.Factory::new);
    	engine.register(TWILIGHTLIGHT.get(), ParticleMagicLight.Factory::new);
    	engine.register(LAY.get(), ParticleLay.Factory::new);
    	engine.register(FROST.get(), ParticleNomal.Maigc::new);
    	engine.register(CYCLONE.get(), ParticleCyclone.Factory::new);
    	engine.register(SMOKY.get(), ParticleSmoky.Factory::new);
    	engine.register(BUBBLE.get(), ParticleNomal.Maigc::new);
    	engine.register(BLOOD.get(), ParticleNomal.Blood::new);
    	engine.register(POISON.get(), ParticleNomal.Poison::new);
    	engine.register(GRAVITY.get(), ParticleNomal.Gravity::new);
    	engine.register(AETHER.get(), ParticleNomal.Aether::new);
    	engine.register(DIVINE.get(), ParticleNomal.Divine::new);
    	engine.register(DARK.get(), ParticleNomal.Dark::new);
    	engine.register(REFLASH.get(), ParticleNomal.Reflash::new);
    	engine.register(CYCLE_ORB.get(), RotationParticle.Factory::new);
    	engine.register(CYCLE_ORB_Y.get(), RotationParticle.Yellow::new);
    	engine.register(CYCLE_ORB_BIG.get(), RotationParticle.Factory::new);
    	engine.register(CYCLE_RERITE.get(), RotationParticle.Rewrite::new);
    	engine.register(CYCLE_GRAY_ORB.get(), RotationParticle.Factory::new);
    	engine.register(CYCLE_FROST.get(), RotationParticle.Frost::new);
    	engine.register(CYCLE_FIRE.get(), RotationParticle.Frost::new);
    	engine.register(CYCLE_FIRE.get(), RotationParticle.Frost::new);
    	engine.register(CYCLE_ELECTRIC.get(), RotationParticle.Electoric::new);
    	engine.register(CYCLE_BLOOD.get(), RotationParticle.Blood::new);
    	engine.register(CYCLE_HEAL.get(), RotationParticle.Heal::new);
    	engine.register(CYCLE_REFLASH.get(), RotationParticle.Reflash::new);
    	engine.register(CYCLE_TORNADO.get(), RotationParticle.Cyclone::new);
    	engine.register(CYCLE_TOXIC.get(), RotationParticle.Toxic::new);
    	engine.register(CRYSTAL.get(), ParticleNomal.Crystal::new);
    	engine.register(FROST_LASER.get(), ParticleNomal.Frostlaser::new);
    	engine.register(CHERRY_BLOSSOMS.get(), ParticleCherryBlossoms.Factory::new);
    	engine.register(CHERRY_BLOSSOMS_LARGE.get(), ParticleCherryBlossoms.Large::new);
    	engine.register(GRAVITY_FIELD.get(), ParticleNomal.GravityField::new);
    	engine.register(WIND_FIELD.get(), ParticleNomal.WindField::new);
    	engine.register(RAIN_FIELD.get(), ParticleNomal.RainField::new);
    }
}
